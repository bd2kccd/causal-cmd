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
import java.io.FileNotFoundException;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.cli.Option;
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

    public static Map<String, String> parse(String[] args) throws CmdParserException {
        Map<String, String> argsMap = new HashMap<>();

        Options options = getValidOptions(args);

        // get input Tetrad parameters
        try {
            Args.parseLongOptions(args, options, argsMap);
        } catch (ParseException exception) {
            HelpOptions helpOptions = new HelpOptions();
            Options opts = helpOptions.getOptions();
            Options invalidOpts = helpOptions.getInvalidValueOptions();

            options.getOptions().forEach(e -> opts.addOption(e));
            Args.toMapOptions(args).forEach((k, v) -> {
                if (v == null && options.hasLongOption(k)) {
                    invalidOpts.addOption(options.getOption(k));
                }
            });

            throw new CmdParserException(helpOptions, exception);
        }

        return argsMap;
    }

    /**
     * Gather all the required and valid optional options based on user's input.
     *
     * @param args command-line arguments
     * @return valid required and optional options
     * @throws CmdParserException when parameters and value are incorrect or
     * missing
     */
    private static Options getValidOptions(String[] args) throws CmdParserException {
        Options opts = CmdOptions.getInstance().getMainOptions();
        Options invalidOpts = new Options();

        Map<String, String> argsParseMap = new HashMap<>();
        try {
            Args.parseLongOptions(Args.extractLongOptions(args, opts), opts, argsParseMap);
        } catch (ParseException exception) {
            throw new CmdParserException(new HelpOptions(opts, invalidOpts), exception);
        }

        // ensure datasets exist
        String dataset = argsParseMap.get(CmdParams.DATASET);
        String[] dataFiles = dataset.split(",");
        for (String file : dataFiles) {
            try {
                FileUtils.exists(Paths.get(file.trim()));
            } catch (FileNotFoundException exception) {
                invalidOpts.addOption(opts.getOption(CmdParams.DATASET));
                throw new CmdParserException(new HelpOptions(opts, invalidOpts), exception);
            }
        }

        // ensure delimiter is valid
        String delimiterName = argsParseMap.get(CmdParams.DELIMITER);
        if (!Delimiters.getInstance().exists(delimiterName)) {
            invalidOpts.addOption(opts.getOption(CmdParams.DELIMITER));
            String errMsg = String.format("No such delimiter '%s'.", delimiterName);
            throw new CmdParserException(new HelpOptions(opts, invalidOpts), new IllegalArgumentException(errMsg));
        }

        // get data type
        String dataTypeCmd = argsParseMap.get(CmdParams.DATA_TYPE);
        DataType dataType = DataTypes.getInstance().get(dataTypeCmd);
        if (dataType == null) {
            invalidOpts.addOption(opts.getOption(CmdParams.DATA_TYPE));
            String errMsg = String.format("No such data type '%s'.", dataTypeCmd);
            throw new CmdParserException(new HelpOptions(opts, invalidOpts), new IllegalArgumentException(errMsg));
        }

        if (dataType == DataType.Covariance) {
            if (Args.hasLongParam(args, CmdParams.MISSING_MARKER)) {
                String errMsg = createRejectParamMsg(CmdParams.DATA_TYPE, dataType.name().toLowerCase(), CmdParams.MISSING_MARKER);
                throw new CmdParserException(new HelpOptions(opts, invalidOpts), new IllegalArgumentException(errMsg));
            }
            if (Args.hasLongParam(args, CmdParams.EXCLUDE_VARIABLE)) {
                String errMsg = createRejectParamMsg(CmdParams.DATA_TYPE, dataType.name().toLowerCase(), CmdParams.EXCLUDE_VARIABLE);
                throw new CmdParserException(new HelpOptions(opts, invalidOpts), new IllegalArgumentException(errMsg));
            }
            if (Args.hasLongParam(args, CmdParams.NUM_CATEGORIES)) {
                String errMsg = createRejectParamMsg(CmdParams.DATA_TYPE, dataType.name().toLowerCase(), CmdParams.NUM_CATEGORIES);
                throw new CmdParserException(new HelpOptions(opts, invalidOpts), new IllegalArgumentException(errMsg));
            }
        } else {
            // set options for a particular data type
            opts.addOption(CmdOptions.getInstance().getLongOption(CmdParams.EXCLUDE_VARIABLE));
            opts.addOption(CmdOptions.getInstance().getLongOption(CmdParams.MISSING_MARKER));
            if (dataType == DataType.Mixed) {
                opts.addOption(OptionFactory.createRequiredNumCategoryOpt());
                try {
                    Args.parseLongOptions(Args.extractLongOptions(args, opts), opts, argsParseMap);
                } catch (ParseException exception) {
                    throw new CmdParserException(new HelpOptions(opts, invalidOpts), exception);
                }
            } else {
                if (Args.hasLongParam(args, CmdParams.NUM_CATEGORIES)) {
                    String errMsg = createRejectParamMsg(CmdParams.DATA_TYPE, dataType.name().toLowerCase(), CmdParams.NUM_CATEGORIES);
                    throw new CmdParserException(new HelpOptions(opts, invalidOpts), new IllegalArgumentException(errMsg));
                }
            }
        }

        // get algorithm
        String algorithmCmd = argsParseMap.get(CmdParams.ALGORITHM);
        Class algorithmClass = TetradAlgorithms.getInstance().getAlgorithmClass(algorithmCmd);
        if (algorithmClass == null) {
            invalidOpts.addOption(opts.getOption(CmdParams.ALGORITHM));
            String errMsg = String.format("No such algorithm '%s'.", algorithmCmd);
            throw new CmdParserException(new HelpOptions(opts, invalidOpts), new IllegalArgumentException(errMsg));
        }
        if (!TetradAlgorithms.getInstance().acceptMultipleDataset(algorithmClass) && dataFiles.length > 1) {
            String errMsg = String.format("Algorithm '%s' does not accept multiple datasets.", algorithmCmd);
            throw new CmdParserException(new HelpOptions(opts, invalidOpts), new IllegalArgumentException(errMsg));
        }
        if (TetradAlgorithms.getInstance().acceptKnowledge(algorithmClass)) {
            opts.addOption(CmdOptions.getInstance().getLongOption(CmdParams.KNOWLEDGE));
            try {
                Args.parseLongOptions(Args.extractLongOptions(args, opts), opts, argsParseMap);
            } catch (ParseException exception) {
                invalidOpts.addOption(opts.getOption(CmdParams.KNOWLEDGE));
                throw new CmdParserException(new HelpOptions(opts, invalidOpts), exception);
            }

            String knowledge = argsParseMap.get(CmdParams.KNOWLEDGE);
            if (knowledge != null) {
                try {
                    FileUtils.exists(Paths.get(knowledge.trim()));
                } catch (FileNotFoundException exception) {
                    throw new CmdParserException(new HelpOptions(opts, invalidOpts), exception);
                }
            }
        } else {
            if (opts.hasLongOption(CmdParams.KNOWLEDGE)) {
                String errMsg = String.format("Algorithm '%s' does not accept knowledge.", algorithmCmd);
                throw new CmdParserException(new HelpOptions(opts, invalidOpts), new IllegalArgumentException(errMsg));
            }
        }

        Class indTestClass = null;
        if (TetradAlgorithms.getInstance().requireIndependenceTest(algorithmClass)) {
            opts.addOption(OptionFactory.createRequiredTestOpt(dataType));
            try {
                Args.parseLongOptions(Args.extractLongOptions(args, opts), opts, argsParseMap);
            } catch (ParseException exception) {
                invalidOpts.addOption(opts.getOption(CmdParams.TEST));
                throw new CmdParserException(new HelpOptions(opts, invalidOpts), exception);
            }

            String indTestCmd = argsParseMap.get(CmdParams.TEST);
            TetradIndependenceTests indTests = TetradIndependenceTests.getInstance();
            if (!indTests.hasCommand(indTestCmd)) {
                invalidOpts.addOption(opts.getOption(CmdParams.TEST));
                String errMsg = String.format("No such test '%s'.", indTestCmd);
                throw new CmdParserException(new HelpOptions(opts, invalidOpts), new IllegalArgumentException(errMsg));
            }
            if (!indTests.hasCommand(indTestCmd, dataType)) {
                invalidOpts.addOption(opts.getOption(CmdParams.TEST));
                String errMsg = String.format("Independence test '%s' is invalid for data-type '%s'.", indTestCmd, argsParseMap.get(CmdParams.DATA_TYPE));
                throw new CmdParserException(new HelpOptions(opts, invalidOpts), new IllegalArgumentException(errMsg));
            }

            indTestClass = TetradIndependenceTests.getInstance().getTestOfIndependenceClass(indTestCmd);
        }

        Class scoreClass = null;
        if (TetradAlgorithms.getInstance().requireScore(algorithmClass)) {
            opts.addOption(OptionFactory.createRequiredScoreOpt(dataType));
            try {
                Args.parseLongOptions(Args.extractLongOptions(args, opts), opts, argsParseMap);
            } catch (ParseException exception) {
                invalidOpts.addOption(opts.getOption(CmdParams.SCORE));
                throw new CmdParserException(new HelpOptions(opts, invalidOpts), exception);
            }

            String scoreCmd = argsParseMap.get(CmdParams.SCORE);
            TetradScores scores = TetradScores.getInstance();
            if (!scores.hasCommand(scoreCmd)) {
                invalidOpts.addOption(opts.getOption(CmdParams.SCORE));
                String errMsg = String.format("No such score '%s'.", scoreCmd);
                throw new CmdParserException(new HelpOptions(opts, invalidOpts), new IllegalArgumentException(errMsg));
            }
            if (!scores.hasCommand(scoreCmd, dataType)) {
                invalidOpts.addOption(opts.getOption(CmdParams.SCORE));
                String errMsg = String.format("Score '%s' is invalid for data-type '%s'.", scoreCmd, argsParseMap.get(CmdParams.DATA_TYPE));
                throw new CmdParserException(new HelpOptions(opts, invalidOpts), new IllegalArgumentException(errMsg));
            }

            scoreClass = TetradScores.getInstance().getScoreClass(scoreCmd);
        }

        List<String> params = new LinkedList<>();
        try {
            params.addAll(AlgorithmFactory.create(algorithmClass, indTestClass, scoreClass).getParameters());
        } catch (IllegalAccessException | InstantiationException exception) {
            throw new CmdParserException(new HelpOptions(opts, invalidOpts), exception);
        }

        // add Tetrad parameters
        params.forEach(param -> {
            opts.addOption(CmdOptions.getInstance().getLongOption(param));
        });

        // added required parameter for mixed dataset
        if (dataType == DataType.Mixed) {
            params.add(CmdParams.NUM_CATEGORIES);
        }

        try {
            Args.parseLongOptions(Args.extractLongOptions(args, opts), opts, argsParseMap);
        } catch (ParseException exception) {
            throw new CmdParserException(new HelpOptions(opts, invalidOpts), exception);
        }

        ParamDescriptions paramDescs = ParamDescriptions.getInstance();
        for (String param : params) {
            if (argsParseMap.containsKey(param)) {
                ParamDescription paramDesc = paramDescs.get(param);
                String value = argsParseMap.get(param);
                Option opt = opts.getOption(param);
                Object type = opt.getType();
                if (type == Integer.class) {
                    int val = Integer.MIN_VALUE;
                    try {
                        val = Integer.parseInt(value);
                    } catch (NumberFormatException exception) {
                        invalidOpts.addOption(opts.getOption(param));
                        String errMsg = String.format("The value '%s' for parameter %s is not a integer.", value, param);
                        throw new CmdParserException(new HelpOptions(opts, invalidOpts), new NumberFormatException(errMsg));
                    }

                    int min = paramDesc.getLowerBoundInt();
                    if (val < min) {
                        invalidOpts.addOption(opts.getOption(param));
                        String errMsg = String.format("Value for parameter %s is %d but minimum is %d.", param, val, min);
                        throw new CmdParserException(new HelpOptions(opts, invalidOpts), new NumberFormatException(errMsg));
                    }

                    int max = paramDesc.getUpperBoundInt();
                    if (val > max) {
                        invalidOpts.addOption(opts.getOption(param));
                        String errMsg = String.format("Value for parameter %s is %d but maximum is %d.", param, val, max);
                        throw new CmdParserException(new HelpOptions(opts, invalidOpts), new NumberFormatException(errMsg));
                    }
                } else if (type == Double.class) {
                    double val = Double.NaN;
                    try {
                        val = Double.parseDouble(value);
                    } catch (NumberFormatException exception) {
                        invalidOpts.addOption(opts.getOption(param));
                        String errMsg = String.format("The value '%s' for parameter %s is not a double.", value, param);
                        throw new CmdParserException(new HelpOptions(opts, invalidOpts), new NumberFormatException(errMsg));
                    }

                    double min = paramDesc.getLowerBoundDouble();
                    if (val < min) {
                        invalidOpts.addOption(opts.getOption(param));
                        String errMsg = String.format("Value for parameter %s is %f but minimum is %f.", param, val, min);
                        throw new CmdParserException(new HelpOptions(opts, invalidOpts), new NumberFormatException(errMsg));
                    }

                    double max = paramDesc.getUpperBoundDouble();
                    if (val > max) {
                        invalidOpts.addOption(opts.getOption(param));
                        String errMsg = String.format("Value for parameter %s is %f but maximum is %f.", param, val, max);
                        throw new CmdParserException(new HelpOptions(opts, invalidOpts), new NumberFormatException(errMsg));
                    }
                }
            }
        }

        return opts;
    }

    public static HelpOptions getHelpOptions(String[] args) {
        HelpOptions helpOptions = new HelpOptions();
        Options opts = helpOptions.getOptions();
        Options invalidOpts = helpOptions.getInvalidValueOptions();

        CmdOptions.getInstance().getRequiredOptions()
                .forEach(e -> opts.addOption(e));

        Map<String, String> argsMap = Args.toMapOptions(args);
        argsMap.forEach((k, v) -> {
            if (v == null && opts.hasLongOption(k)) {
                invalidOpts.addOption(opts.getOption(k));
            }
        });

        DataType dataType = null;
        String dataTypeCmd = argsMap.get(CmdParams.DATA_TYPE);
        if (dataTypeCmd != null) {
            if (DataTypes.getInstance().exists(dataTypeCmd)) {
                dataType = DataTypes.getInstance().get(dataTypeCmd);
                if (dataType != DataType.Covariance) {
                    opts.addOption(CmdOptions.getInstance().getLongOption(CmdParams.EXCLUDE_VARIABLE));
                    opts.addOption(CmdOptions.getInstance().getLongOption(CmdParams.MISSING_MARKER));
                    if (dataType == DataType.Mixed) {
                        opts.addOption(OptionFactory.createRequiredNumCategoryOpt());
                    }
                }
            } else {
                invalidOpts.addOption(opts.getOption(CmdParams.DATA_TYPE));
                System.err.println(createNoSuchValueMsg(CmdParams.DATA_TYPE, dataTypeCmd));
            }

        }

        if (argsMap.containsKey(CmdParams.ALGORITHM) && argsMap.get(CmdParams.ALGORITHM) != null) {
            String algoCmd = argsMap.get(CmdParams.ALGORITHM);
            TetradAlgorithms algorithms = TetradAlgorithms.getInstance();
            if (algorithms.hasCommand(algoCmd)) {
                Class algoClass = algorithms.getAlgorithmClass(algoCmd);
                if (algorithms.acceptKnowledge(algoClass)) {
                    opts.addOption(CmdOptions.getInstance().getLongOption(CmdParams.KNOWLEDGE));
                }

                if (dataType != null) {
                    if (algorithms.requireIndependenceTest(algoClass)) {
                        opts.addOption(OptionFactory.createRequiredTestOpt(dataType));
                    }
                    if (algorithms.requireScore(algoClass)) {
                        opts.addOption(OptionFactory.createRequiredScoreOpt(dataType));
                    }

                    Class testClass = null;
                    if (argsMap.containsKey(CmdParams.TEST) && argsMap.get(CmdParams.TEST) != null) {
                        String testCmd = argsMap.get(CmdParams.TEST);
                        TetradIndependenceTests tests = TetradIndependenceTests.getInstance();
                        if (tests.hasCommand(testCmd)) {
                            if (tests.hasCommand(testCmd, dataType)) {
                                testClass = tests.getTestOfIndependenceClass(testCmd);
                            } else {
                                invalidOpts.addOption(opts.getOption(CmdParams.TEST));

                                String errMsg = String.format("Invalid test '%s' for data-type '%s'.", testCmd, dataTypeCmd);
                                System.err.println(errMsg);
                            }
                        } else {
                            invalidOpts.addOption(opts.getOption(CmdParams.TEST));
                            System.err.println(createNoSuchValueMsg(CmdParams.TEST, testCmd));
                        }
                    }

                    Class scoreClass = null;
                    if (argsMap.containsKey(CmdParams.SCORE) && argsMap.get(CmdParams.SCORE) != null) {
                        String scoreCmd = argsMap.get(CmdParams.SCORE);
                        TetradScores scores = TetradScores.getInstance();
                        if (scores.hasCommand(scoreCmd)) {
                            if (scores.hasCommand(scoreCmd, dataType)) {
                                scoreClass = scores.getScoreClass(scoreCmd);
                            } else {
                                invalidOpts.addOption(opts.getOption(CmdParams.SCORE));

                                String errMsg = String.format("Invalid score '%s' for data-type '%s'.", scoreCmd, dataTypeCmd);
                                System.err.println(errMsg);
                            }
                        } else {
                            invalidOpts.addOption(opts.getOption(CmdParams.SCORE));
                            System.err.println(createNoSuchValueMsg(CmdParams.SCORE, scoreCmd));
                        }
                    }

                    List<String> params = new LinkedList<>();
                    try {
                        params.addAll(AlgorithmFactory.create(algoClass, testClass, scoreClass).getParameters());
                    } catch (IllegalAccessException | InstantiationException | IllegalArgumentException exception) {
                    }

                    // add Tetrad parameters
                    params.forEach(param -> {
                        opts.addOption(CmdOptions.getInstance().getLongOption(param));
                    });
                }
            } else {
                invalidOpts.addOption(opts.getOption(CmdParams.ALGORITHM));
                System.err.println(createNoSuchValueMsg(CmdParams.ALGORITHM, algoCmd));
            }

            if (argsMap.containsKey(CmdParams.DELIMITER) && argsMap.get(CmdParams.DELIMITER) != null) {
                String delimiterName = argsMap.get(CmdParams.DELIMITER);
                if (!Delimiters.getInstance().exists(delimiterName)) {
                    invalidOpts.addOption(opts.getOption(CmdParams.DELIMITER));

                    String errMsg = String.format("No such delimiter '%s'.", delimiterName);
                    System.err.println(errMsg);
                }
            }
        }

        return helpOptions;
    }

    private static String createNoSuchValueMsg(String param, String value) {
        return String.format("Invalid value for parameter --%s: %s", param, value);
    }

    private static String createRejectParamMsg(String param, String value, String rejectedParam) {
        return String.format("Parameter --%s with value '%s' cannot be used with parameter --%s.", param, value, rejectedParam);
    }

}
