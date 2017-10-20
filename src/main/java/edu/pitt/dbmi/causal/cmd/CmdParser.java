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
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
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

    public static CmdArgs parse(String[] args) throws CmdParserException {
        CmdArgs cmdArgs = new CmdArgs();

        ParseOptions parseOptions = getValidOptions(args, CmdOptions.getInstance().getMainOptions());

        try {
            CommandLine cmd = (new DefaultParser()).parse(parseOptions.getOptions(), args);
            parseRequiredOptions(cmd, parseOptions, cmdArgs);
            parseOptionalOptions(cmd, parseOptions, cmdArgs);
        } catch (ParseException exception) {
            throw new CmdParserException(parseOptions, exception);
        }

        return cmdArgs;
    }

    private static void parseOptionalOptions(CommandLine cmd, ParseOptions parseOptions, CmdArgs cmdArgs) throws CmdParserException {
        cmdArgs.knowledgeFile = cmd.hasOption(CmdParams.KNOWLEDGE)
                ? getValidFile(cmd.getOptionValue(CmdParams.KNOWLEDGE), parseOptions, CmdParams.KNOWLEDGE)
                : null;
        cmdArgs.excludeVariableFile = cmd.hasOption(CmdParams.EXCLUDE_VARIABLE)
                ? getValidFile(cmd.getOptionValue(CmdParams.EXCLUDE_VARIABLE), parseOptions, CmdParams.EXCLUDE_VARIABLE)
                : null;
        cmdArgs.outDirectory = cmd.hasOption(CmdParams.DIR_OUT)
                ? Paths.get(cmd.getOptionValue(CmdParams.DIR_OUT))
                : Paths.get(".");
        cmdArgs.missingValueMarker = cmd.hasOption(CmdParams.MISSING_MARKER)
                ? cmd.getOptionValue(CmdParams.MISSING_MARKER)
                : null;
        cmdArgs.commentMarker = cmd.hasOption(CmdParams.COMMENT_MARKER)
                ? cmd.getOptionValue(CmdParams.COMMENT_MARKER)
                : null;
        cmdArgs.quoteChar = cmd.hasOption(CmdParams.QUOTE_CHAR)
                ? getValidChar(cmd.getOptionValue(CmdParams.QUOTE_CHAR), parseOptions, CmdParams.QUOTE_CHAR)
                : 0;
        cmdArgs.filePrefix = cmd.hasOption(CmdParams.FILE_PREFIX)
                ? cmd.getOptionValue(CmdParams.FILE_PREFIX)
                : null;
        cmdArgs.testClass = cmd.hasOption(CmdParams.TEST)
                ? TetradIndependenceTests.getInstance().getClass(cmd.getOptionValue(CmdParams.TEST))
                : null;
        cmdArgs.scoreClass = cmd.hasOption(CmdParams.SCORE)
                ? TetradScores.getInstance().getClass(cmd.getOptionValue(CmdParams.SCORE))
                : null;
        cmdArgs.time = cmd.hasOption(CmdParams.TIMEOUT)
                ? getValidLong(cmd.getOptionValue(CmdParams.TIMEOUT), parseOptions, CmdParams.TIMEOUT)
                : -1;
        cmdArgs.timeUnit = cmd.hasOption(CmdParams.TIMEOUT)
                ? getValidTimeUnit(cmd.getOptionValue(CmdParams.TIMEOUT), parseOptions, CmdParams.TIMEOUT)
                : null;
        cmdArgs.json = cmd.hasOption(CmdParams.JSON);
        cmdArgs.skipLatest = cmd.hasOption(CmdParams.SKIP_LATEST);
        cmdArgs.skipValidation = cmd.hasOption(CmdParams.SKIP_VALIDATION);

        cmdArgs.parameters = getValidParameters(cmd, cmdArgs, parseOptions);
    }

    private static void parseRequiredOptions(CommandLine cmd, ParseOptions parseOptions, CmdArgs cmdArgs) throws CmdParserException {
        String datasetCmd = cmd.getOptionValue(CmdParams.DATASET);
        String[] datasetFiles = datasetCmd.split(",");
        List<Path> dataset = new LinkedList<>();
        for (String datasetFile : datasetFiles) {
            dataset.add(getValidFile(datasetFile, parseOptions, CmdParams.DATASET));
        }
        cmdArgs.datasetFiles = dataset;

        cmdArgs.dataType = DataTypes.getInstance().get(cmd.getOptionValue(CmdParams.DATA_TYPE));
        cmdArgs.delimiter = Delimiters.getInstance().get(cmd.getOptionValue(CmdParams.DELIMITER));
        cmdArgs.algorithmClass = TetradAlgorithms.getInstance().getAlgorithmClass(cmd.getOptionValue(CmdParams.ALGORITHM));
    }

    public static ParseOptions getHelpOptions(String[] args) throws CmdParserException {
        CmdOptions cmdOptions = CmdOptions.getInstance();

        return getValidOptions(Args.removeLongOption(args, CmdParams.HELP), cmdOptions.toOptions(cmdOptions.getBaseOptions()));
    }

    private static ParseOptions getValidOptions(String[] args, Options options) throws CmdParserException {
        ParseOptions parseOptions = new ParseOptions(options);
        Options opts = parseOptions.getOptions();
        Options invalidOpts = parseOptions.getInvalidValueOptions();

        Map<String, String> argsMap = new HashMap<>();
        try {
            Args.parseLongOptions(Args.extractLongOptions(args, opts), opts, argsMap);
        } catch (ParseException exception) {
            throw new CmdParserException(parseOptions, exception);
        }

        // ensure delimiter is valid
        String delimiterName = argsMap.get(CmdParams.DELIMITER);
        if (!Delimiters.getInstance().exists(delimiterName)) {
            invalidOpts.addOption(opts.getOption(CmdParams.DELIMITER));
            String errMsg = String.format("No such delimiter '%s'.", delimiterName);
            throw new CmdParserException(parseOptions, new IllegalArgumentException(errMsg));
        }

        // get data type
        String dataTypeCmd = argsMap.get(CmdParams.DATA_TYPE);
        DataType dataType = DataTypes.getInstance().get(dataTypeCmd);
        if (dataType == null) {
            invalidOpts.addOption(opts.getOption(CmdParams.DATA_TYPE));
            String errMsg = String.format("No such data type '%s'.", dataTypeCmd);
            throw new CmdParserException(parseOptions, new IllegalArgumentException(errMsg));
        }
        if (dataType != DataType.Covariance) {
            opts.addOption(CmdOptions.getInstance().getLongOption(CmdParams.EXCLUDE_VARIABLE));
            opts.addOption(CmdOptions.getInstance().getLongOption(CmdParams.MISSING_MARKER));
            if (dataType == DataType.Mixed) {
                opts.addOption(OptionFactory.createRequiredNumCategoryOpt());
            }
        }

        // get algorithm
        String algorithmCmd = argsMap.get(CmdParams.ALGORITHM);
        Class algorithmClass = TetradAlgorithms.getInstance().getAlgorithmClass(algorithmCmd);
        if (algorithmClass == null) {
            invalidOpts.addOption(opts.getOption(CmdParams.ALGORITHM));
            String errMsg = String.format("No such algorithm '%s'.", algorithmCmd);
            throw new CmdParserException(parseOptions, new IllegalArgumentException(errMsg));
        }
        if (TetradAlgorithms.getInstance().acceptKnowledge(algorithmClass)) {
            opts.addOption(CmdOptions.getInstance().getLongOption(CmdParams.KNOWLEDGE));
        }

        Class indTestClass = null;
        if (TetradAlgorithms.getInstance().requireIndependenceTest(algorithmClass)) {
            opts.addOption(OptionFactory.createRequiredTestOpt(dataType));
            try {
                Args.parseLongOptions(Args.extractLongOptions(args, opts), opts, argsMap);
            } catch (ParseException exception) {
                invalidOpts.addOption(opts.getOption(CmdParams.TEST));
                throw new CmdParserException(parseOptions, exception);
            }

            String indTestCmd = argsMap.get(CmdParams.TEST);
            TetradIndependenceTests indTests = TetradIndependenceTests.getInstance();
            if (!indTests.hasCommand(indTestCmd)) {
                invalidOpts.addOption(opts.getOption(CmdParams.TEST));
                String errMsg = String.format("No such test '%s'.", indTestCmd);
                throw new CmdParserException(parseOptions, new IllegalArgumentException(errMsg));
            }
            if (!indTests.hasCommand(indTestCmd, dataType)) {
                invalidOpts.addOption(opts.getOption(CmdParams.TEST));
                String errMsg = String.format("Independence test '%s' is invalid for data-type '%s'.", indTestCmd, argsMap.get(CmdParams.DATA_TYPE));
                throw new CmdParserException(parseOptions, new IllegalArgumentException(errMsg));
            }

            indTestClass = TetradIndependenceTests.getInstance().getClass(indTestCmd);
        }

        Class scoreClass = null;
        if (TetradAlgorithms.getInstance().requireScore(algorithmClass)) {
            opts.addOption(OptionFactory.createRequiredScoreOpt(dataType));
            try {
                Args.parseLongOptions(Args.extractLongOptions(args, opts), opts, argsMap);
            } catch (ParseException exception) {
                invalidOpts.addOption(opts.getOption(CmdParams.SCORE));
                throw new CmdParserException(parseOptions, exception);
            }

            String scoreCmd = argsMap.get(CmdParams.SCORE);
            TetradScores scores = TetradScores.getInstance();
            if (!scores.hasCommand(scoreCmd)) {
                invalidOpts.addOption(opts.getOption(CmdParams.SCORE));
                String errMsg = String.format("No such score '%s'.", scoreCmd);
                throw new CmdParserException(parseOptions, new IllegalArgumentException(errMsg));
            }
            if (!scores.hasCommand(scoreCmd, dataType)) {
                invalidOpts.addOption(opts.getOption(CmdParams.SCORE));
                String errMsg = String.format("Score '%s' is invalid for data-type '%s'.", scoreCmd, argsMap.get(CmdParams.DATA_TYPE));
                throw new CmdParserException(parseOptions, new IllegalArgumentException(errMsg));
            }

            scoreClass = TetradScores.getInstance().getClass(scoreCmd);
        }

        List<String> params = new LinkedList<>();
        try {
            params.addAll(AlgorithmFactory.create(algorithmClass, indTestClass, scoreClass).getParameters());
        } catch (IllegalAccessException | InstantiationException exception) {
            throw new CmdParserException(parseOptions, exception);
        }

        // add Tetrad parameters
        params.forEach(param -> {
            opts.addOption(CmdOptions.getInstance().getLongOption(param));
        });

        try {
            (new DefaultParser()).parse(parseOptions.getOptions(), args);
        } catch (ParseException exception) {
            Args.toMapOptions(args).forEach((k, v) -> {
                if (v == null && options.hasLongOption(k) && options.getOption(k).hasArg()) {
                    invalidOpts.addOption(options.getOption(k));
                }
            });
            throw new CmdParserException(parseOptions, exception);
        }
        return parseOptions;
    }

    private static Map<String, String> getValidParameters(CommandLine cmd, CmdArgs cmdArgs, ParseOptions parseOptions) throws CmdParserException {
        Map<String, String> parameters = new HashMap<>();

        List<String> params = new LinkedList<>();
        try {
            params.addAll(AlgorithmFactory.create(cmdArgs.algorithmClass, cmdArgs.testClass, cmdArgs.scoreClass).getParameters());
        } catch (IllegalAccessException | InstantiationException exception) {
            throw new CmdParserException(parseOptions, exception);
        }

        Options opts = parseOptions.getOptions();
        Options invalidOpts = parseOptions.getInvalidValueOptions();
        ParamDescriptions paramDescs = ParamDescriptions.getInstance();
        for (String param : params) {
            if (cmd.hasOption(param)) {
                ParamDescription paramDesc = paramDescs.get(param);
                String value = cmd.getOptionValue(param);
                Option opt = opts.getOption(param);
                Object type = opt.getType();
                if (type == Integer.class) {
                    int val = Integer.MIN_VALUE;
                    try {
                        val = Integer.parseInt(value);
                    } catch (NumberFormatException exception) {
                        invalidOpts.addOption(opts.getOption(param));
                        String errMsg = String.format("The value '%s' for parameter %s is not a integer.", value, param);
                        throw new CmdParserException(parseOptions, new NumberFormatException(errMsg));
                    }

                    int min = paramDesc.getLowerBoundInt();
                    if (val < min) {
                        invalidOpts.addOption(opts.getOption(param));
                        String errMsg = String.format("Value for parameter %s is %d but minimum is %d.", param, val, min);
                        throw new CmdParserException(parseOptions, new NumberFormatException(errMsg));
                    }

                    int max = paramDesc.getUpperBoundInt();
                    if (val > max) {
                        invalidOpts.addOption(opts.getOption(param));
                        String errMsg = String.format("Value for parameter %s is %d but maximum is %d.", param, val, max);
                        throw new CmdParserException(parseOptions, new NumberFormatException(errMsg));
                    }
                } else if (type == Double.class) {
                    double val = Double.NaN;
                    try {
                        val = Double.parseDouble(value);
                    } catch (NumberFormatException exception) {
                        invalidOpts.addOption(opts.getOption(param));
                        String errMsg = String.format("The value '%s' for parameter %s is not a double.", value, param);
                        throw new CmdParserException(parseOptions, new NumberFormatException(errMsg));
                    }

                    double min = paramDesc.getLowerBoundDouble();
                    if (val < min) {
                        invalidOpts.addOption(opts.getOption(param));
                        String errMsg = String.format("Value for parameter %s is %f but minimum is %f.", param, val, min);
                        throw new CmdParserException(parseOptions, new NumberFormatException(errMsg));
                    }

                    double max = paramDesc.getUpperBoundDouble();
                    if (val > max) {
                        invalidOpts.addOption(opts.getOption(param));
                        String errMsg = String.format("Value for parameter %s is %f but maximum is %f.", param, val, max);
                        throw new CmdParserException(parseOptions, new NumberFormatException(errMsg));
                    }
                }
                parameters.put(param, value);
            }
        };

        return parameters;
    }

    private static TimeUnit getValidTimeUnit(String time, ParseOptions parseOptions, String cmdParam) throws CmdParserException {
        Options opts = parseOptions.getOptions();
        Options invalidOpts = parseOptions.getInvalidValueOptions();

        char unit = time.charAt(time.length() - 1);
        if (unit >= 'a' && unit <= 'z') {
            if (unit == 'd' || unit == 'h' || unit == 'm' || unit == 's') {
                switch (unit) {
                    case 'd':
                        return TimeUnit.DAYS;
                    case 'h':
                        return TimeUnit.HOURS;
                    case 'm':
                        return TimeUnit.MINUTES;
                    default:
                        return TimeUnit.SECONDS;
                }
            } else {
                invalidOpts.addOption(opts.getOption(CmdParams.TIMEOUT));
                String errMsg = String.format("Value '%s' does not a valid time unit.", time);
                throw new CmdParserException(parseOptions, new IllegalArgumentException(errMsg));
            }
        } else {
            invalidOpts.addOption(opts.getOption(CmdParams.TIMEOUT));
            String errMsg = String.format("Value '%s' requires time unit.", time);
            throw new CmdParserException(parseOptions, new IllegalArgumentException(errMsg));
        }
    }

    private static long getValidLong(String time, ParseOptions parseOptions, String cmdParam) throws CmdParserException {
        Options opts = parseOptions.getOptions();
        Options invalidOpts = parseOptions.getInvalidValueOptions();

        String digit = time.substring(0, time.length() - 1);
        if (digit.isEmpty()) {
            invalidOpts.addOption(opts.getOption(CmdParams.TIMEOUT));
            String errMsg = String.format("Value '%s' requires time.", time);
            throw new CmdParserException(parseOptions, new IllegalArgumentException(errMsg));
        } else {
            try {
                return Long.parseLong(digit);
            } catch (NumberFormatException exception) {
                invalidOpts.addOption(opts.getOption(CmdParams.TIMEOUT));
                String errMsg = String.format("Value '%s' is either not a number or of a type long.", time);
                throw new CmdParserException(parseOptions, new IllegalArgumentException(errMsg));
            }
        }
    }

    private static char getValidChar(String quoteChar, ParseOptions parseOptions, String cmdParam) throws CmdParserException {
        char c = 0;

        if (quoteChar.length() == 1) {
            c = quoteChar.charAt(0);
        } else {
            Options opts = parseOptions.getOptions();
            Options invalidOpts = parseOptions.getInvalidValueOptions();

            invalidOpts.addOption(opts.getOption(cmdParam));
            String errMsg = String.format("Parameter %s requires a single character.", cmdParam);
            throw new CmdParserException(parseOptions, new IllegalArgumentException(errMsg));
        }

        return c;
    }

    private static Path getValidFile(String filePath, ParseOptions parseOptions, String cmdParam) throws CmdParserException {
        Path file = Paths.get(filePath);

        try {
            FileUtils.exists(file);
        } catch (FileNotFoundException exception) {
            Options opts = parseOptions.getOptions();
            Options invalidOpts = parseOptions.getInvalidValueOptions();

            invalidOpts.addOption(opts.getOption(cmdParam));
            throw new CmdParserException(parseOptions, exception);
        }

        return file;
    }

}
