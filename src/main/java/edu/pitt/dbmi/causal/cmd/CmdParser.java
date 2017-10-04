/*
 * Copyright (C) 2017 University of Pittsburgh.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301  USA
 */
package edu.pitt.dbmi.causal.cmd;

import edu.cmu.tetrad.algcomparison.algorithm.AlgorithmFactory;
import edu.cmu.tetrad.data.DataType;
import edu.cmu.tetrad.util.ParamDescription;
import edu.cmu.tetrad.util.ParamDescriptions;
import edu.pitt.dbmi.causal.cmd.tetrad.TetradAlgorithms;
import edu.pitt.dbmi.causal.cmd.tetrad.TetradIndependenceTests;
import edu.pitt.dbmi.causal.cmd.tetrad.TetradScores;
import edu.pitt.dbmi.causal.cmd.util.Args;
import edu.pitt.dbmi.causal.cmd.util.DataTypes;
import edu.pitt.dbmi.causal.cmd.util.Delimiters;
import edu.pitt.dbmi.causal.cmd.util.FileUtils;
import edu.pitt.dbmi.causal.cmd.util.OptionFactory;
import edu.pitt.dbmi.causal.cmd.util.Validators;
import java.io.FileNotFoundException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 *
 * Sep 15, 2017 11:34:22 AM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public class CmdParser {

    private CmdParser() {
    }

    public static CmdArgs parse(String[] args) throws CmdParserException {
        CmdArgs cmdArgs = new CmdArgs();

        Map<String, String> argsMap = parseToMap(args);
        parseRequiredOptions(cmdArgs, argsMap);
        parseOptionalOptions(cmdArgs, argsMap);

        return cmdArgs;
    }

    private static String extractName(Class clazz) {
        String name = clazz.getName();
        String[] fields = name.toLowerCase().split("\\.");

        return fields[fields.length - 1];
    }

    private static void parseOptionalOptions(CmdArgs cmdArgs, Map<String, String> argsMap) {
        if (argsMap.containsKey(CmdParams.KNOWLEDGE)) {
            cmdArgs.knowledgeFile = Paths.get(argsMap.get(CmdParams.KNOWLEDGE));
        }
        if (argsMap.containsKey(CmdParams.EXCLUDE_VARIABLE)) {
            cmdArgs.excludeVariableFile = Paths.get(argsMap.get(CmdParams.EXCLUDE_VARIABLE));
        }
        if (argsMap.containsKey(CmdParams.DIR_OUT)) {
            cmdArgs.outDirectory = Paths.get(argsMap.get(CmdParams.DIR_OUT));
        }
        if (argsMap.containsKey(CmdParams.QUOTE_CHAR)) {
            cmdArgs.quoteChar = argsMap.get(CmdParams.QUOTE_CHAR).charAt(0);
        }
        if (argsMap.containsKey(CmdParams.MISSING_MARKER)) {
            cmdArgs.missingValueMarker = argsMap.get(CmdParams.MISSING_MARKER);
        }
        if (argsMap.containsKey(CmdParams.COMMENT_MARKER)) {
            cmdArgs.commentMarker = argsMap.get(CmdParams.COMMENT_MARKER);
        }
        if (argsMap.containsKey(CmdParams.FILE_PREFIX)) {
            cmdArgs.filePrefix = argsMap.get(CmdParams.FILE_PREFIX);
        }

        cmdArgs.skipValidation = argsMap.containsKey(CmdParams.SKIP_VALIDATION);
        cmdArgs.json = argsMap.containsKey(CmdParams.JSON);
        cmdArgs.skipLatest = argsMap.containsKey(CmdParams.SKIP_LATEST);

        if (cmdArgs.outDirectory == null) {
            cmdArgs.outDirectory = Paths.get(".");
        }

        cmdArgs.fileName = (cmdArgs.filePrefix == null)
                ? extractName(cmdArgs.algorithmClass) + "_" + System.currentTimeMillis()
                : cmdArgs.filePrefix;

        List<String> params = new LinkedList<>();
        try {
            params.addAll(AlgorithmFactory.create(cmdArgs.algorithmClass, cmdArgs.testClass, cmdArgs.scoreClass).getParameters());
        } catch (IllegalAccessException | InstantiationException exception) {
            exception.printStackTrace(System.err);
        }
        cmdArgs.parameters = params.stream()
                .filter(e -> argsMap.containsKey(e))
                .collect(HashMap::new, (m, e) -> m.put(e, argsMap.get(e)), HashMap::putAll);
    }

    private static void parseRequiredOptions(CmdArgs cmdArgs, Map<String, String> argsMap) {
        String value = argsMap.get(CmdParams.DATASET);
        String[] values = value.split(",");
        List<Path> datasetFiles = new LinkedList<>();
        for (String val : values) {
            datasetFiles.add(Paths.get(val));
        }
        cmdArgs.datasetFiles = datasetFiles;

        cmdArgs.dataType = DataTypes.getInstance().get(argsMap.get(CmdParams.DATA_TYPE));
        cmdArgs.delimiter = Delimiters.getInstance().getDelimiter(argsMap.get(CmdParams.DELIMITER));
        cmdArgs.algorithmClass = TetradAlgorithms.getInstance().getAlgorithmClass(argsMap.get(CmdParams.ALGORITHM));

        if (argsMap.containsKey(CmdParams.TEST)) {
            cmdArgs.testClass = TetradIndependenceTests.getInstance().getTestOfIndependenceClass(argsMap.get(CmdParams.TEST));
        }
        if (argsMap.containsKey(CmdParams.SCORE)) {
            cmdArgs.scoreClass = TetradScores.getInstance().getScoreClass(argsMap.get(CmdParams.SCORE));
        }
    }

    private static Map<String, String> parseToMap(String[] args) throws CmdParserException {
        Map<String, String> argsMap = new HashMap<>();

        Options options = getValidOptions(args);
        try {
            Args.parse(args, options, argsMap);
        } catch (ParseException exception) {
            throw new CmdParserException(options, exception);
        }

        return argsMap;
    }

    private static DataType getDataType(Map<String, String> argsParseMap, Options options) throws CmdParserException {
        String dataTypeName = argsParseMap.get(CmdParams.DATA_TYPE);
        if (!DataTypes.getInstance().exists(dataTypeName)) {
            String errMsg = String.format("No such data type '%s'.", dataTypeName);
            throw new CmdParserException(options, new IllegalArgumentException(errMsg));
        }

        return DataTypes.getInstance().get(dataTypeName);
    }

    private static Class getAlgorithmClass(Map<String, String> argsParseMap, Options options) throws CmdParserException {
        TetradAlgorithms algorithms = TetradAlgorithms.getInstance();
        String algoCmd = argsParseMap.get(CmdParams.ALGORITHM);
        if (!algorithms.hasCommand(algoCmd)) {
            String errMsg = String.format("No such algorithm '%s'.", algoCmd);
            throw new CmdParserException(options, new IllegalArgumentException(errMsg));
        }

        return algorithms.getAlgorithmClass(algoCmd);
    }

    private static Class getTestClass(Map<String, String> argsParseMap, Options options, DataType dataType) throws CmdParserException {
        if (!options.hasLongOption(CmdParams.TEST)) {
            return null;
        }

        TetradIndependenceTests indTests = TetradIndependenceTests.getInstance();
        String testCmd = argsParseMap.get(CmdParams.TEST);
        if (!indTests.hasCommand(testCmd)) {
            String errMsg = String.format("No such test '%s'.", testCmd);
            throw new CmdParserException(options, new IllegalArgumentException(errMsg));
        }
        if (!indTests.hasCommand(testCmd, dataType)) {
            String errMsg = String.format("Invalid test '%s' for data-type '%s'.", testCmd, argsParseMap.get(CmdParams.DATA_TYPE));
            throw new CmdParserException(options, new IllegalArgumentException(errMsg));
        }

        return indTests.getTestOfIndependenceClass(testCmd);
    }

    private static Class getScoreClass(Map<String, String> argsParseMap, Options options, DataType dataType) throws CmdParserException {
        if (!options.hasLongOption(CmdParams.SCORE)) {
            return null;
        }

        TetradScores scores = TetradScores.getInstance();
        String scoreCmd = argsParseMap.get(CmdParams.SCORE);
        if (!scores.hasCommand(scoreCmd)) {
            String errMsg = String.format("No such score '%s'.", scoreCmd);
            throw new CmdParserException(options, new IllegalArgumentException(errMsg));
        }
        if (!scores.hasCommand(scoreCmd, dataType)) {
            String errMsg = String.format("Invalid score '%s' for data-type '%s'.", scoreCmd, argsParseMap.get(CmdParams.DATA_TYPE));
            throw new CmdParserException(options, new IllegalArgumentException(errMsg));
        }

        return scores.getScoreClass(scoreCmd);
    }

    private static Options getValidOptions(String[] args) throws CmdParserException {
        Options options = CmdOptions.getInstance().getMainOptions();

        Map<String, String> argsParseMap = new HashMap<>();
        try {
            Args.parse(Args.extractOptions(args, options), options, argsParseMap);
        } catch (ParseException exception) {
            throw new CmdParserException(options, exception);
        }

        DataType dataType = getDataType(argsParseMap, options);

        // set options for a particular data type
        if (dataType != DataType.Covariance) {
            options.addOption(CmdOptions.getInstance().getLongOption(CmdParams.EXCLUDE_VARIABLE));
            options.addOption(CmdOptions.getInstance().getLongOption(CmdParams.MISSING_MARKER));
            if (dataType == DataType.Mixed) {
                options.addOption(OptionFactory.createRequiredNumCategoryOpt());
            }
        }

        Class algorithmClass = getAlgorithmClass(argsParseMap, options);

        // set options for a particular algorithm
        TetradAlgorithms algorithms = TetradAlgorithms.getInstance();
        if (algorithms.acceptKnowledge(algorithmClass)) {
            options.addOption(CmdOptions.getInstance().getLongOption(CmdParams.KNOWLEDGE));
        }
        if (algorithms.requireIndependenceTest(algorithmClass)) {
            options.addOption(OptionFactory.createRequiredTestOpt(dataType));
        }
        if (algorithms.requireScore(algorithmClass)) {
            options.addOption(OptionFactory.createRequiredScoreOpt(dataType));
        }

        // ensure we have the required test and score, if any
        try {
            Args.parse(Args.extractOptions(args, options), options, argsParseMap);
        } catch (ParseException exception) {
            throw new CmdParserException(options, exception);
        }

        Class indTestClass = getTestClass(argsParseMap, options, dataType);
        Class scoreClass = getScoreClass(argsParseMap, options, dataType);

        List<String> params = new LinkedList<>();
        try {
            params.addAll(AlgorithmFactory.create(algorithmClass, indTestClass, scoreClass).getParameters());
        } catch (IllegalAccessException | InstantiationException exception) {
            throw new CmdParserException(options, exception);
        }

        // add Tetrad parameters
        params.forEach(param -> {
            options.addOption(CmdOptions.getInstance().getLongOption(param));
        });

        // get input Tetrad parameters
        try {
            Args.parse(Args.extractOptions(args, options), options, argsParseMap);
        } catch (ParseException exception) {
            throw new CmdParserException(options, exception);
        }

        // validate all input Tetrad parameters
        ParamDescriptions paramDescs = ParamDescriptions.getInstance();
        for (String param : params) {
            if (argsParseMap.containsKey(param)) {
                ParamDescription paramDesc = paramDescs.get(param);
                try {
                    Validators.validateNumber(paramDesc.getDefaultValue(), argsParseMap.get(param));
                } catch (NumberFormatException exception) {
                    throw new CmdParserException(options, exception);
                }
            }
        }

        // ensure algorithm can handle multiple dataset
        String dataset = argsParseMap.get(CmdParams.DATASET);
        String[] files = dataset.split(",");
        if (files.length > 1 && !TetradAlgorithms.getInstance().acceptMultipleDataset(algorithmClass)) {
            String errMsg = String.format("Algorithm '%s' does not take multiple dataset.", argsParseMap.get(CmdParams.ALGORITHM));
            throw new CmdParserException(options, new IllegalArgumentException(errMsg));
        }

        // ensure dataset exists
        for (String file : files) {
            try {
                FileUtils.exists(Paths.get(file.trim()));
            } catch (FileNotFoundException exception) {
                throw new CmdParserException(options, exception);
            }
        }

        // ensure algorithm can handle knowledge
        if (options.hasLongOption(CmdParams.KNOWLEDGE)) {
            String knowledge = argsParseMap.get(CmdParams.KNOWLEDGE);
            if (knowledge != null) {
                try {
                    FileUtils.exists(Paths.get(knowledge));
                } catch (FileNotFoundException exception) {
                    throw new CmdParserException(options, exception);
                }
            }
        } else {
            if (Args.hasLongParam(args, CmdParams.KNOWLEDGE)) {
                rejectParamMsg(CmdParams.ALGORITHM, argsParseMap.get(CmdParams.ALGORITHM), CmdParams.KNOWLEDGE, options);
            }
        }

        // ensure delimiter is valid
        String delimiterName = argsParseMap.get(CmdParams.DELIMITER);
        if (!Delimiters.getInstance().exists(delimiterName)) {
            String errMsg = String.format("No such delimiter '%s'.", delimiterName);
            throw new CmdParserException(options, new IllegalArgumentException(errMsg));
        }

        // ensure we have required parameters for particular data type
        if (dataType == DataType.Covariance) {
            if (Args.hasLongParam(args, CmdParams.MISSING_MARKER)) {
                rejectParamMsg(CmdParams.DATA_TYPE, dataType.name().toLowerCase(), CmdParams.MISSING_MARKER, options);
            }
            if (Args.hasLongParam(args, CmdParams.EXCLUDE_VARIABLE)) {
                rejectParamMsg(CmdParams.DATA_TYPE, dataType.name().toLowerCase(), CmdParams.EXCLUDE_VARIABLE, options);
            }
            if (Args.hasLongParam(args, CmdParams.NUM_CATEGORIES)) {
                rejectParamMsg(CmdParams.DATA_TYPE, dataType.name().toLowerCase(), CmdParams.NUM_CATEGORIES, options);
            }
        } else {
            // ensure exclude variable file, if any, is valid
            String excludeVar = argsParseMap.get(CmdParams.EXCLUDE_VARIABLE);
            if (excludeVar != null) {
                try {
                    FileUtils.exists(Paths.get(excludeVar));
                } catch (FileNotFoundException exception) {
                    throw new CmdParserException(options, exception);
                }
            }

            if (dataType == DataType.Mixed) {
                options.addOption(OptionFactory.createRequiredNumCategoryOpt());
                try {
                    Args.parse(Args.extractOptions(args, options), options, argsParseMap);
                } catch (ParseException exception) {
                    throw new CmdParserException(options, exception);
                }

                String numOfCategory = argsParseMap.get(CmdParams.NUM_CATEGORIES);
                try {
                    Integer.parseInt(numOfCategory);
                } catch (NumberFormatException exception) {
                    String errMsg = String.format("'%s' is not an integer.", numOfCategory);
                    throw new CmdParserException(options, new NumberFormatException(errMsg));
                }
            } else {
                if (Args.hasLongParam(args, CmdParams.NUM_CATEGORIES)) {
                    rejectParamMsg(CmdParams.DATA_TYPE, dataType.name().toLowerCase(), CmdParams.NUM_CATEGORIES, options);
                }
            }
        }

        return options;
    }

    private static void rejectParamMsg(String param, String value, String rejectedParam, Options options) throws CmdParserException {
        String errMsg = String.format("Parameter --%s with value '%s' cannot be used with parameter --%s.", param, value, rejectedParam);

        throw new CmdParserException(options, new IllegalArgumentException(errMsg));
    }

}
