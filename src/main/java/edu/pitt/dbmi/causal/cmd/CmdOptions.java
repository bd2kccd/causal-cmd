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

import edu.cmu.tetrad.util.ParamDescription;
import edu.cmu.tetrad.util.ParamDescriptions;
import edu.pitt.dbmi.causal.cmd.tetrad.TetradAlgorithms;
import edu.pitt.dbmi.causal.cmd.tetrad.TetradIndependenceTests;
import edu.pitt.dbmi.causal.cmd.tetrad.TetradScores;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 * The class {@code CmdOptions} is a class for storing command-line options.
 *
 * Aug 27, 2017 10:42:03 PM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public final class CmdOptions {

    private static CmdOptions instance;

    private final Map<String, Option> options = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    private CmdOptions() {
        addRequiredOptions();
        addOptionalOptions();
    }

    /**
     * Get an instance of the command-line options.
     *
     * @return CmdOptions instance
     */
    public static CmdOptions getInstance() {
        if (instance == null) {
            instance = new CmdOptions();
        }

        return instance;
    }

    /**
     * Destroy the instance of the command-line options.
     */
    public static void clear() {
        instance = null;
    }

    /**
     * Get multi-character name options for a given name..
     *
     * @param param multi-character name
     * @return command-line option
     */
    public Option getLongOption(String param) {
        return options.get(param);
    }

    /**
     * Test if the command-line options has an option with the given
     * multi-character name.
     *
     * @param param multi-character name
     * @return true if the command-line options contains the option with the
     * given multi-character name.
     */
    public boolean hasLongParam(String param) {
        return options.containsKey(param);
    }

    /**
     * Get all the stored command-line options.
     *
     * @return command-line options
     */
    public Options getOptions() {
        Options opts = new Options();
        options.entrySet().forEach(e -> {
            opts.addOption(e.getValue());
        });

        return opts;
    }

    /**
     * Get all the application main command-line options.
     *
     * @return command-line options
     */
    public Options getMainOptions() {
        List<Option> optList = getBaseOptions();
        optList.add(options.get(CmdParams.HELP));
        optList.add(options.get(CmdParams.HELP_ALL));
        optList.add(options.get(CmdParams.HELP_ALGO_DESC));
        optList.add(options.get(CmdParams.HELP_SCORE_DESC));
        optList.add(options.get(CmdParams.HELP_TEST_DESC));
        optList.add(options.get(CmdParams.VERSION));

        return toOptions(optList);
    }

    /**
     * Add a list of options to the {@code Options} object.
     *
     * @param optionList list of options
     * @return the Options object containing the given list of options.
     */
    public Options toOptions(List<Option> optionList) {
        Options opts = new Options();

        if (optionList != null) {
            optionList.forEach(e -> opts.addOption(e));
        }

        return opts;
    }

    /**
     * Get all the application base command-line options.
     *
     * @return list of command-line options.
     */
    public List<Option> getBaseOptions() {
        List<Option> opts = new LinkedList<>();

        getRequiredOptions().forEach(e -> opts.add(e));

        // optional files
        opts.add(options.get(CmdParams.METADATA));

        opts.add(options.get(CmdParams.NO_HEADER));

        // dataset options
        opts.add(options.get(CmdParams.QUOTE_CHAR));
        opts.add(options.get(CmdParams.COMMENT_MARKER));

        // output options
        opts.add(options.get(CmdParams.FILE_PREFIX));
        opts.add(options.get(CmdParams.JSON_GRAPH));
        opts.add(options.get(CmdParams.DIR_OUT));

//        opts.add(options.get(CmdParams.THREAD));
        // data validation options
        opts.add(options.get(CmdParams.SKIP_VALIDATION));

        opts.add(options.get(CmdParams.EXPERIMENTAL));

        opts.add(options.get(CmdParams.DEFAULT));

        return opts;
    }

    /**
     * Add application optional options to the stored options.
     */
    private void addOptionalOptions() {
        options.put(CmdParams.QUOTE_CHAR, Option.builder().longOpt(CmdParams.QUOTE_CHAR).desc("Single character denotes quote.").hasArg().argName("character").build());
        options.put(CmdParams.MISSING_MARKER, Option.builder().longOpt(CmdParams.MISSING_MARKER).desc("Denotes missing value.").hasArg().argName("string").build());
        options.put(CmdParams.COMMENT_MARKER, Option.builder().longOpt(CmdParams.COMMENT_MARKER).desc("Comment marker.").hasArg().argName("string").build());
        options.put(CmdParams.NO_HEADER, Option.builder().longOpt(CmdParams.NO_HEADER).desc("Indicates tabular dataset has no header.").build());

        options.put(CmdParams.HELP, new Option(null, CmdParams.HELP, false, "Show help."));
        options.put(CmdParams.HELP_ALL, new Option(null, CmdParams.HELP_ALL, false, "Show all options and descriptions."));
        options.put(CmdParams.HELP_ALGO_DESC, new Option(null, CmdParams.HELP_ALGO_DESC, false, "Show all the algorithms along with their descriptions."));
        options.put(CmdParams.HELP_SCORE_DESC, new Option(null, CmdParams.HELP_SCORE_DESC, false, "Show all the scores along with their descriptions."));
        options.put(CmdParams.HELP_TEST_DESC, new Option(null, CmdParams.HELP_TEST_DESC, false, "Show all the independence tests along with their descriptions."));
        options.put(CmdParams.VERSION, new Option(null, CmdParams.VERSION, false, "Show version."));
        options.put(CmdParams.FILE_PREFIX, Option.builder().longOpt(CmdParams.FILE_PREFIX).desc("Output file name prefix.").hasArg().argName("string").build());
        options.put(CmdParams.JSON_GRAPH, new Option(null, CmdParams.JSON_GRAPH, false, "Write out graph as json."));
        options.put(CmdParams.DIR_OUT, Option.builder().longOpt(CmdParams.DIR_OUT).desc("Output directory").hasArg().argName("directory").build());

        options.put(CmdParams.KNOWLEDGE, Option.builder().longOpt(CmdParams.KNOWLEDGE).desc("Prior knowledge file.").hasArg().argName("file").build());
        options.put(CmdParams.EXCLUDE_VARIABLE, Option.builder().longOpt(CmdParams.EXCLUDE_VARIABLE).desc("Variables to be excluded from run.").hasArg().argName("file").build());
        options.put(CmdParams.METADATA, Option.builder().longOpt(CmdParams.METADATA).desc("Metadata file.  Cannot apply to dataset without header.").hasArg().argName("file").build());

        options.put(CmdParams.SKIP_VALIDATION, new Option(null, CmdParams.SKIP_VALIDATION, false, "Skip validation."));

        options.put(CmdParams.THREAD, Option.builder().longOpt(CmdParams.THREAD).desc("Number threads.").hasArg().argName("string").build());

        options.put(CmdParams.TEST, Option.builder().longOpt(CmdParams.TEST).desc(getIndependenceTestDesc()).hasArg().argName("string").build());
        options.put(CmdParams.SCORE, Option.builder().longOpt(CmdParams.SCORE).desc(getScoreDesc()).hasArg().argName("string").build());

        // graph manipulations
        options.put(CmdParams.CHOOSE_DAG_IN_PATTERN, new Option(null, CmdParams.CHOOSE_DAG_IN_PATTERN, false, "Choose DAG in Pattern graph."));
        options.put(CmdParams.CHOOSE_MAG_IN_PAG, new Option(null, CmdParams.CHOOSE_MAG_IN_PAG, false, "Choose MAG in PAG."));
        options.put(CmdParams.GENERATE_PATTERN_FROM_DAG, new Option(null, CmdParams.GENERATE_PATTERN_FROM_DAG, false, "Generate pattern graph from PAG."));
        options.put(CmdParams.GENERATE_PAG_FROM_DAG, new Option(null, CmdParams.GENERATE_PAG_FROM_DAG, false, "Generate PAG from DAG."));
        options.put(CmdParams.GENERATE_PAG_FROM_TSDAG, new Option(null, CmdParams.GENERATE_PAG_FROM_TSDAG, false, "Generate PAG from TsDAG."));
        options.put(CmdParams.MAKE_BIDIRECTED_UNDIRECTED, new Option(null, CmdParams.MAKE_BIDIRECTED_UNDIRECTED, false, "Make bidirected edges undirected."));
        options.put(CmdParams.MAKE_UNDIRECTED_BIDIRECTED, new Option(null, CmdParams.MAKE_UNDIRECTED_BIDIRECTED, false, "Make undirected edges bidirected."));
        options.put(CmdParams.MAKE_ALL_EDGES_UNDIRECTED, new Option(null, CmdParams.MAKE_ALL_EDGES_UNDIRECTED, false, "Make all edges undirected."));
        options.put(CmdParams.GENEREATE_COMPLETE_GRAPH, new Option(null, CmdParams.GENEREATE_COMPLETE_GRAPH, false, "Generate complete graph."));
        options.put(CmdParams.EXTRACT_STRUCT_MODEL, new Option(null, CmdParams.EXTRACT_STRUCT_MODEL, false, "Extract sturct model."));

        options.put(CmdParams.EXPERIMENTAL, new Option(null, CmdParams.EXPERIMENTAL, false, "Show experimental algorithms, tests, and scores."));

        options.put(CmdParams.DEFAULT, new Option(null, CmdParams.DEFAULT, false, "Use Tetrad default parameter values."));

        // tetrad parameters
        ParamDescriptions paramDescs = ParamDescriptions.getInstance();
        Set<String> params = paramDescs.getNames();
        params.forEach(param -> {
            ParamDescription paramDesc = paramDescs.get(param);
            String longOpt = param;
            String desc = paramDesc.getShortDescription();
            Serializable defaultVal = paramDesc.getDefaultValue();
            String argName = defaultVal.getClass().getSimpleName().toLowerCase();
            boolean hasArg = !(paramDesc.getDefaultValue() instanceof Boolean);
            Class type = paramDesc.getDefaultValue().getClass();
            options.put(param, Option.builder().longOpt(longOpt).desc(desc).hasArg(hasArg).type(type).argName(argName).build());
        });
    }

    /**
     * Add application required options to the stored options.
     */
    private void addRequiredOptions() {
        options.put(CmdParams.ALGORITHM, Option.builder().longOpt(CmdParams.ALGORITHM).desc(getAlgorithmDesc()).hasArg().argName("string").required().build());
        options.put(CmdParams.DATASET, Option.builder().longOpt(CmdParams.DATASET).desc("Dataset. Multiple files are seperated by commas.").hasArg().argName("files").required().build());
        options.put(CmdParams.DELIMITER, Option.builder().longOpt(CmdParams.DELIMITER).desc(getDelimiterDesc()).hasArg().argName("string").required().build());
        options.put(CmdParams.DATA_TYPE, Option.builder().longOpt(CmdParams.DATA_TYPE).desc(getDataTypeDesc()).hasArg().argName("string").required().build());
    }

    /**
     * Get the application required options from the stored options.
     *
     * @return required command-line options
     */
    public List<Option> getRequiredOptions() {
        return options.entrySet().stream()
                .filter(e -> e.getValue().isRequired())
                .map(e -> e.getValue())
                .collect(Collectors.toList());
    }

    /**
     * Get the names of all the datatypes.
     *
     * @return datatype description
     */
    private String getDataTypeDesc() {
        return "Data type: " + DataTypes.getInstance().getNames().stream()
                .collect(Collectors.joining(", "));
    }

    /**
     * Get the names of all the delimiters.
     *
     * @return names of delimiters
     */
    private String getDelimiterDesc() {
        return "Delimiter: " + Delimiters.getInstance().getNames().stream()
                .collect(Collectors.joining(", "));
    }

    /**
     * Get the names of all the scores.
     *
     * @return description for score
     */
    private String getScoreDesc() {
        return "Score: " + TetradScores.getInstance().getCommands().stream()
                .collect(Collectors.joining(", "));
    }

    /**
     * Get the names of all the independence test.
     *
     * @return description for test of independence
     */
    private String getIndependenceTestDesc() {
        return "Independence Test: " + TetradIndependenceTests.getInstance().getCommands().stream()
                .collect(Collectors.joining(", "));
    }

    /**
     * Get the names of all the algorithms.
     *
     * @return description for algorithm
     */
    private String getAlgorithmDesc() {
        return "Algorithm: " + TetradAlgorithms.getInstance().getCommands().stream()
                .collect(Collectors.joining(", "));
    }

}
