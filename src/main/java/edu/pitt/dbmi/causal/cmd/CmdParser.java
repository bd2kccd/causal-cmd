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
        if (argsMap.containsKey(CmdParams.SKIP_VALIDATION)) {
            cmdArgs.skipValidation = true;
        }

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

    private static Options getValidOptions(String[] args) throws CmdParserException {
        Options options = CmdOptions.getInstance().getMainOptions();

        Map<String, String> argsParseMap = new HashMap<>();
        try {
            Args.parse(Args.extractOptions(args, options), options, argsParseMap);
        } catch (ParseException exception) {
            throw new CmdParserException(options, exception);
        }

        // ensure dataset is valid
        String dataset = argsParseMap.get(CmdParams.DATASET);
        String[] files = dataset.split(",");
        for (String file : files) {
            try {
                FileUtils.exists(Paths.get(file.trim()));
            } catch (FileNotFoundException exception) {
                throw new CmdParserException(options, exception);
            }
        }

        // ensure exclude variable file, if any, is valid
        String excludeVar = argsParseMap.get(CmdParams.EXCLUDE_VARIABLE);
        if (excludeVar != null) {
            try {
                FileUtils.exists(Paths.get(excludeVar));
            } catch (FileNotFoundException exception) {
                throw new CmdParserException(options, exception);
            }
        }

        String delimiterName = argsParseMap.get(CmdParams.DELIMITER);
        if (!Delimiters.getInstance().exists(delimiterName)) {
            String errMsg = String.format("No such delimiter '%s'.", delimiterName);
            throw new CmdParserException(options, new IllegalArgumentException(errMsg));
        }

        String dataTypeName = argsParseMap.get(CmdParams.DATA_TYPE);
        if (!DataTypes.getInstance().exists(dataTypeName)) {
            String errMsg = String.format("No such data type '%s'.", dataTypeName);
            throw new CmdParserException(options, new IllegalArgumentException(errMsg));
        }

        String quoteChar = argsParseMap.get(CmdParams.QUOTE_CHAR);
        System.out.println(quoteChar);
        if (quoteChar != null && quoteChar.length() > 1) {
            String errMsg = "Quote character requires a single character.";
            throw new CmdParserException(options, new IllegalArgumentException(errMsg));
        }

        DataType dataType = DataTypes.getInstance().get(dataTypeName);
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
        }

        TetradAlgorithms algorithms = TetradAlgorithms.getInstance();
        String algoCmd = argsParseMap.get(CmdParams.ALGORITHM);
        if (!algorithms.hasCommand(algoCmd)) {
            String errMsg = String.format("No such algorithm '%s'.", algoCmd);
            throw new CmdParserException(options, new IllegalArgumentException(errMsg));
        }

        Class algoClass = algorithms.getAlgorithmClass(algoCmd);
        Class indTestClass = null;
        Class scoreClass = null;
        if (files.length > 1 && !algorithms.acceptMultipleDataset(algoClass)) {
            String errMsg = String.format("Algorithm '%s' does not take multiple dataset.", algoCmd);
            throw new CmdParserException(options, new IllegalArgumentException(errMsg));
        }
        if (algorithms.acceptKnowledge(algoClass)) {
            options.addOption(CmdOptions.getInstance().getLongOption(CmdParams.KNOWLEDGE));
            try {
                Args.parse(Args.extractOptions(args, options), options, argsParseMap);
            } catch (ParseException exception) {
                throw new CmdParserException(options, exception);
            }

            // ensure knowledge file, if any, is valid
            String knowledge = argsParseMap.get(CmdParams.KNOWLEDGE);
            if (knowledge != null) {
                try {
                    FileUtils.exists(Paths.get(knowledge));
                } catch (FileNotFoundException exception) {
                    throw new CmdParserException(options, exception);
                }
            }
        }
        if (algorithms.requireIndependenceTest(algoClass)) {
            options.addOption(OptionFactory.createRequiredTestOpt(dataType));
            try {
                Args.parse(Args.extractOptions(args, options), options, argsParseMap);
            } catch (ParseException exception) {
                throw new CmdParserException(options, exception);
            }

            TetradIndependenceTests indTests = TetradIndependenceTests.getInstance();
            String testCmd = argsParseMap.get(CmdParams.TEST);
            if (indTests.hasCommand(testCmd)) {
                indTestClass = indTests.getTestOfIndependenceClass(testCmd);
            } else {
                String errMsg = String.format("No such independence test '%s'.", testCmd);
                throw new CmdParserException(options, new IllegalArgumentException(errMsg));
            }
        }
        if (algorithms.requireScore(algoClass)) {
            options.addOption(OptionFactory.createRequiredScoreOpt(dataType));
            try {
                Args.parse(Args.extractOptions(args, options), options, argsParseMap);
            } catch (ParseException exception) {
                throw new CmdParserException(options, exception);
            }

            TetradScores scores = TetradScores.getInstance();
            String scoreCmd = argsParseMap.get(CmdParams.SCORE);
            if (scores.hasCommand(scoreCmd)) {
                scoreClass = scores.getScoreClass(scoreCmd);
            } else {
                String errMsg = String.format("No such score '%s'.", scoreCmd);
                throw new CmdParserException(options, new IllegalArgumentException(errMsg));
            }
        }

        List<String> params = new LinkedList<>();
        try {
            params.addAll(AlgorithmFactory.create(algoClass, indTestClass, scoreClass).getParameters());
        } catch (IllegalAccessException | InstantiationException exception) {
            throw new CmdParserException(options, exception);
        }

        params.forEach(param -> {
            options.addOption(CmdOptions.getInstance().getLongOption(param));
        });
        try {
            Args.parse(Args.extractOptions(args, options), options, argsParseMap);
        } catch (ParseException exception) {
            throw new CmdParserException(options, exception);
        }

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

        return options;
    }

}
