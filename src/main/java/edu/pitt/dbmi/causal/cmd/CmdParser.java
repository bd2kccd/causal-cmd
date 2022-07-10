/*
 * Copyright (C) 2019 University of Pittsburgh.
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

import edu.cmu.tetrad.algcomparison.algorithm.Algorithm;
import edu.cmu.tetrad.algcomparison.algorithm.AlgorithmFactory;
import edu.cmu.tetrad.algcomparison.utils.TakesIndependenceWrapper;
import edu.cmu.tetrad.algcomparison.utils.UsesScoreWrapper;
import edu.cmu.tetrad.data.DataType;
import edu.cmu.tetrad.util.ParamDescription;
import edu.cmu.tetrad.util.ParamDescriptions;
import edu.cmu.tetrad.util.Params;
import edu.pitt.dbmi.causal.cmd.tetrad.TetradAlgorithms;
import edu.pitt.dbmi.causal.cmd.tetrad.TetradIndependenceTests;
import edu.pitt.dbmi.causal.cmd.tetrad.TetradScores;
import edu.pitt.dbmi.causal.cmd.util.FileUtils;
import java.io.FileNotFoundException;
import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 * The class {@code CmdParser} is a utility class for parsing and extractiong
 * command-line options.
 *
 * Sep 15, 2017 11:34:22 AM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public final class CmdParser {

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

    /**
     * Parse command-line options.
     *
     * @param cmd
     * @param parseOptions
     * @param cmdArgs
     * @throws CmdParserException
     */
    private static void parseOptionalOptions(CommandLine cmd, ParseOptions parseOptions, CmdArgs cmdArgs) throws CmdParserException {
        cmdArgs.knowledgeFile = cmd.hasOption(CmdParams.KNOWLEDGE)
                ? getValidFile(cmd.getOptionValue(CmdParams.KNOWLEDGE), parseOptions, CmdParams.KNOWLEDGE)
                : null;
        cmdArgs.excludeVariableFile = cmd.hasOption(CmdParams.EXCLUDE_VARIABLE)
                ? getValidFile(cmd.getOptionValue(CmdParams.EXCLUDE_VARIABLE), parseOptions, CmdParams.EXCLUDE_VARIABLE)
                : null;
        cmdArgs.metadataFile = cmd.hasOption(CmdParams.METADATA)
                ? getValidFile(cmd.getOptionValue(CmdParams.METADATA), parseOptions, CmdParams.METADATA)
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
        cmdArgs.testClass = cmd.hasOption(CmdParams.TEST)
                ? TetradIndependenceTests.getInstance().getClass(cmd.getOptionValue(CmdParams.TEST))
                : null;
        cmdArgs.scoreClass = cmd.hasOption(CmdParams.SCORE)
                ? TetradScores.getInstance().getClass(cmd.getOptionValue(CmdParams.SCORE))
                : null;
        cmdArgs.filePrefix = getValidPrefix(cmd, cmdArgs, parseOptions);
        cmdArgs.jsonGraph = cmd.hasOption(CmdParams.JSON_GRAPH);
        cmdArgs.skipValidation = cmd.hasOption(CmdParams.SKIP_VALIDATION);
        cmdArgs.hasHeader = !cmd.hasOption(CmdParams.NO_HEADER);
        cmdArgs.numOfThreads = cmd.hasOption(CmdParams.THREAD)
                ? getValidThreadNumber(cmd.getOptionValue(CmdParams.THREAD), parseOptions, CmdParams.THREAD)
                : Runtime.getRuntime().availableProcessors() - 1;

        // graph manipulations
        cmdArgs.chooseDagInPattern = cmd.hasOption(CmdParams.CHOOSE_DAG_IN_PATTERN);
        cmdArgs.chooseMagInPag = cmd.hasOption(CmdParams.CHOOSE_MAG_IN_PAG);
        cmdArgs.generatePatternFromDag = cmd.hasOption(CmdParams.GENERATE_PATTERN_FROM_DAG);
        cmdArgs.generatePagFromDag = cmd.hasOption(CmdParams.GENERATE_PAG_FROM_DAG);
        cmdArgs.generatePagFromTsDag = cmd.hasOption(CmdParams.GENERATE_PAG_FROM_TSDAG);
        cmdArgs.makeBidirectedUndirected = cmd.hasOption(CmdParams.MAKE_BIDIRECTED_UNDIRECTED);
        cmdArgs.makeUndirectedBidirected = cmd.hasOption(CmdParams.MAKE_UNDIRECTED_BIDIRECTED);
        cmdArgs.makeAllEdgesUndirected = cmd.hasOption(CmdParams.MAKE_ALL_EDGES_UNDIRECTED);
        cmdArgs.generateCompleteGraph = cmd.hasOption(CmdParams.GENEREATE_COMPLETE_GRAPH);
        cmdArgs.extractStructModel = cmd.hasOption(CmdParams.EXTRACT_STRUCT_MODEL);

        cmdArgs.experimental = cmd.hasOption(CmdParams.EXPERIMENTAL);

        cmdArgs.defaultParamValues = cmd.hasOption(CmdParams.DEFAULT);

        cmdArgs.parameters = getValidParameters(cmd, cmdArgs, parseOptions);
    }

    /**
     * Parse the required command-line options.
     *
     * @param cmd
     * @param parseOptions
     * @param cmdArgs
     * @throws CmdParserException
     */
    private static void parseRequiredOptions(CommandLine cmd, ParseOptions parseOptions, CmdArgs cmdArgs) throws CmdParserException {
        cmdArgs.dataType = DataTypes.getInstance().get(cmd.getOptionValue(CmdParams.DATA_TYPE));
        cmdArgs.delimiter = Delimiters.getInstance().get(cmd.getOptionValue(CmdParams.DELIMITER));
        cmdArgs.algorithmClass = TetradAlgorithms.getInstance().getAlgorithmClass(cmd.getOptionValue(CmdParams.ALGORITHM));

        String datasetCmd = cmd.getOptionValue(CmdParams.DATASET);
        String[] datasetFiles = datasetCmd.split(",");
        List<Path> dataset = new LinkedList<>();
        for (String datasetFile : datasetFiles) {
            dataset.add(getValidFile(datasetFile, parseOptions, CmdParams.DATASET));
        }
        cmdArgs.datasetFiles = dataset;

        // make sure algorithm can handle multiple dataset
        if (dataset.size() > 1 && !TetradAlgorithms.getInstance().acceptMultipleDataset(cmdArgs.algorithmClass)) {
            Options opts = parseOptions.getOptions();
            Options invalidOpts = parseOptions.getInvalidValueOptions();

            invalidOpts.addOption(opts.getOption(CmdParams.DATASET));
            String algoName = TetradAlgorithms.getInstance().getName(cmdArgs.getAlgorithmClass());
            String errMsg = String.format("Algorithm '%s' cannot handle multiple dataset files.", algoName);
            throw new CmdParserException(parseOptions, new IllegalArgumentException(errMsg));
        }
    }

    /**
     * Get the options for the help message.
     *
     * @param args
     * @return
     * @throws CmdParserException
     */
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

        // ensure metadata is not given with dataset without header
        boolean hasNoHeader = argsMap.containsKey(CmdParams.NO_HEADER);
        boolean hasMetadata = argsMap.containsKey(CmdParams.METADATA);
        if (hasNoHeader && hasMetadata) {
            invalidOpts.addOption(opts.getOption(CmdParams.NO_HEADER));
            invalidOpts.addOption(opts.getOption(CmdParams.METADATA));
            String errMsg = "Metadata cannot apply to dataset without header.";
            throw new CmdParserException(parseOptions, new IllegalArgumentException(errMsg));
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
            opts.addOption(CmdOptions.getInstance().getLongOption(CmdParams.NO_HEADER));
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

        addGraphManipulationOptions(opts);

        boolean testParamReq = TetradAlgorithms.getInstance().requireIndependenceTest(algorithmClass);
        boolean scoreParamReq = TetradAlgorithms.getInstance().requireScore(algorithmClass);
        if (testParamReq) {
            opts.addOption(OptionFactory.createRequiredTestOpt(dataType));
        }
        if (scoreParamReq) {
            opts.addOption(OptionFactory.createRequiredScoreOpt(dataType));
        }

        try {
            Args.parseLongOptions(Args.extractLongOptions(args, opts), opts, argsMap);
        } catch (ParseException exception) {
            if (testParamReq) {
                invalidOpts.addOption(opts.getOption(CmdParams.TEST));
            }
            if (scoreParamReq) {
                invalidOpts.addOption(opts.getOption(CmdParams.SCORE));
            }
            throw new CmdParserException(parseOptions, exception);
        }

        Class indTestClass = null;
        if (testParamReq) {
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
        if (scoreParamReq) {
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

        Set<String> params = new HashSet<>();
        try {
            params.addAll(getAlgorithmRelatedParameters(AlgorithmFactory.create(algorithmClass, indTestClass, scoreClass)));
        } catch (IllegalAccessException | InstantiationException exception) {
            throw new CmdParserException(parseOptions, exception);
        }

        // add Tetrad parameters
        params.forEach(param -> {
            if (param.equals(CmdParams.TARGET_NAME)) {
                // add required Tetrad parameters
                opts.addOption(Option.builder().longOpt(CmdParams.TARGET_NAME).desc("Target variable.").hasArg().argName("string").required().build());
            } else {
                opts.addOption(CmdOptions.getInstance().getLongOption(param));
            }
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

    /**
     * Get the parameters for the algorithm, the algorithm's test, algorithm's
     * score and bootstrap.
     *
     * @param algorithm
     * @return
     */
    private static Set<String> getAlgorithmRelatedParameters(Algorithm algorithm) {
        if (algorithm == null) {
            return Collections.EMPTY_SET;
        }

        Set<String> params = new HashSet<>();

        // add algorithm parameters
        params.addAll(algorithm.getParameters());

        // add the algorithm test parameters, if any
        if (algorithm instanceof TakesIndependenceWrapper) {
            params.addAll(((TakesIndependenceWrapper) algorithm).getIndependenceWrapper().getParameters());
        }

        // add the algorithm's score parameters, if any
        if (algorithm instanceof UsesScoreWrapper) {
            params.addAll(((UsesScoreWrapper) algorithm).getScoreWrapper().getParameters());
        }

        // add the bootstrap parameters, if any
        params.addAll(Params.getBootstrappingParameters(algorithm));

        return params;
    }

    /**
     * Add options for manipulating Tetrad output graph.
     *
     * @param opts
     */
    private static void addGraphManipulationOptions(Options opts) {
        opts.addOption(CmdOptions.getInstance().getLongOption(CmdParams.CHOOSE_DAG_IN_PATTERN));
        opts.addOption(CmdOptions.getInstance().getLongOption(CmdParams.CHOOSE_MAG_IN_PAG));
        opts.addOption(CmdOptions.getInstance().getLongOption(CmdParams.GENERATE_PATTERN_FROM_DAG));
        opts.addOption(CmdOptions.getInstance().getLongOption(CmdParams.GENERATE_PAG_FROM_DAG));
        opts.addOption(CmdOptions.getInstance().getLongOption(CmdParams.GENERATE_PAG_FROM_TSDAG));
        opts.addOption(CmdOptions.getInstance().getLongOption(CmdParams.MAKE_BIDIRECTED_UNDIRECTED));
        opts.addOption(CmdOptions.getInstance().getLongOption(CmdParams.MAKE_UNDIRECTED_BIDIRECTED));
        opts.addOption(CmdOptions.getInstance().getLongOption(CmdParams.MAKE_ALL_EDGES_UNDIRECTED));
        opts.addOption(CmdOptions.getInstance().getLongOption(CmdParams.GENEREATE_COMPLETE_GRAPH));
        opts.addOption(CmdOptions.getInstance().getLongOption(CmdParams.EXTRACT_STRUCT_MODEL));
    }

    private static String getValidPrefix(CommandLine cmd, CmdArgs cmdArgs, ParseOptions parseOptions) {
        if (cmd.hasOption(CmdParams.FILE_PREFIX)) {
            return cmd.getOptionValue(CmdParams.FILE_PREFIX);
        } else {
            return extractName(cmdArgs.getAlgorithmClass()) + "_" + System.currentTimeMillis();
        }
    }

    private static String extractName(Class clazz) {
        String name = clazz.getName();
        String[] fields = name.toLowerCase().split("\\.");

        return fields[fields.length - 1];
    }

    /**
     * Get all the parameters related to the selected algorithm, test, and
     * score.
     *
     * @param cmdArgs
     * @param parseOptions
     * @return
     * @throws CmdParserException
     */
    private static Set<String> getAllRelatedParameters(CmdArgs cmdArgs, ParseOptions parseOptions) throws CmdParserException {
        try {
            return getAlgorithmRelatedParameters(AlgorithmFactory.create(cmdArgs.getAlgorithmClass(), cmdArgs.getTestClass(), cmdArgs.getScoreClass()));
        } catch (IllegalAccessException | InstantiationException exception) {
            throw new CmdParserException(parseOptions, exception);
        }
    }

    /**
     * Get all the parameters for algorithm, test, and score. Use default values
     * for the parameters if user use the "--default" flag. Replace any value if
     * user explicitly specified with parameter-arg input or flag.
     *
     * @param cmd
     * @param cmdArgs
     * @param parseOptions
     * @return
     * @throws CmdParserException
     */
    private static Map<String, String> getValidParameters(CommandLine cmd, CmdArgs cmdArgs, ParseOptions parseOptions) throws CmdParserException {
        Map<String, String> parametersWithValues = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

        Set<String> parameters = getAllRelatedParameters(cmdArgs, parseOptions);

        setParametersAndValues(parametersWithValues, parameters, cmdArgs.defaultParamValues);
        setUserParameterValues(parametersWithValues, parameters, cmd, parseOptions);

        return parametersWithValues;
    }

    private static void setUserParameterValues(Map<String, String> parametersWithValues, Set<String> parameters, CommandLine cmd, ParseOptions parseOptions) throws CmdParserException {
        ParamDescriptions paramDescriptions = ParamDescriptions.getInstance();
        Options opts = parseOptions.getOptions();
        Options invalidOpts = parseOptions.getInvalidValueOptions();
        for (String param : parameters) {
            if (cmd.hasOption(param)) {
                ParamDescription paramDesc = paramDescriptions.get(param);
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
                } else if (type == Boolean.class) {
                    if (value == null) {
                        value = "true";
                    }
                }
                parametersWithValues.put(param, value);
            }
        }
    }

    /**
     * Add all the parameters with values.
     *
     * @param parametersWithValues holds parameters along with their values
     * @param parameters parameters to add
     * @param useDefaultValues true if default values should be used for boolean
     * parameters. Else, the values for the boolean parameters are false.
     */
    private static void setParametersAndValues(Map<String, String> parametersWithValues, Set<String> parameters, boolean useDefaultValues) {
        ParamDescriptions paramDescriptions = ParamDescriptions.getInstance();
        for (String param : parameters) {
            ParamDescription paramDesc = paramDescriptions.get(param);
            Serializable defaultValue = paramDesc.getDefaultValue();
            String value = useDefaultValues
                    ? String.valueOf(defaultValue)
                    : (defaultValue instanceof Boolean)
                            ? Boolean.FALSE.toString()
                            : String.valueOf(defaultValue);
            parametersWithValues.put(param, value);
        }
    }

    /**
     * Extract the thread number from the command-line option and check to make
     * sure the number is valid.
     *
     * @param value
     * @param parseOptions
     * @param cmdParam
     * @return
     * @throws CmdParserException
     */
    private static int getValidThreadNumber(String value, ParseOptions parseOptions, String cmdParam) throws CmdParserException {
        int numOfThreads = 0;

        Options opts = parseOptions.getOptions();
        Options invalidOpts = parseOptions.getInvalidValueOptions();

        try {
            numOfThreads = Integer.parseInt(value);
        } catch (NumberFormatException exception) {
            invalidOpts.addOption(opts.getOption(cmdParam));
            String errMsg = String.format("The value '%s' for parameter %s is not a integer.", value, cmdParam);
            throw new CmdParserException(parseOptions, new NumberFormatException(errMsg));
        }

        if (numOfThreads < 1) {
            invalidOpts.addOption(opts.getOption(cmdParam));
            String errMsg = String.format("Parameter %s requires value greater than or equal to 1.", cmdParam);
            throw new CmdParserException(parseOptions, new IllegalArgumentException(errMsg));
        }

        return numOfThreads;
    }

    /**
     * Extract the delimiter charactor from the command-line option and make
     * sure the character is valid.
     *
     * @param quoteChar
     * @param parseOptions
     * @param cmdParam
     * @return
     * @throws CmdParserException
     */
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

    /**
     * Extract the file location from the command-line option and make sure the
     * file is valid.
     *
     * @param filePath
     * @param parseOptions
     * @param cmdParam
     * @return
     * @throws CmdParserException
     */
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
