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
package edu.pitt.dbmi.causal.cmd.opt.algo;

import edu.pitt.dbmi.causal.cmd.opt.CmdLongOpts;
import edu.pitt.dbmi.causal.cmd.util.Args;
import edu.pitt.dbmi.data.Delimiter;
import java.nio.file.Path;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 *
 * Mar 12, 2017 1:55:15 AM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public abstract class TetradCmdAlgoOpt {

    protected final Options mainOptions = new Options();

    protected Path dataFile;
    protected Path knowledgeFile;
    protected Path excludedVariableFile;
    protected Delimiter delimiter;
    protected boolean verbose;
    protected int numOfThreads;
    protected boolean isSerializeJson;
    protected boolean tetradGraphJson;
    protected Path dirOut;
    protected String outputPrefix;
    protected boolean validationOutput;
    protected boolean skipLatest;

    public TetradCmdAlgoOpt() {
        setOptions();
    }

    public void parseOptions(String[] args) {
        try {
            CommandLineParser cmdParser = new DefaultParser();
            CommandLine cmd = cmdParser.parse(mainOptions, args);
            parseCommonRequiredOptions(cmd);
            parseCommonOptionalOptions(cmd);
            parseRequiredOptions(cmd);
            parseOptionalOptions(cmd);
        } catch (Exception exception) {
            System.err.println(exception.getMessage());
            System.exit(-127);
        }
    }

    protected abstract void parseRequiredOptions(CommandLine cmd) throws Exception;

    protected abstract void parseOptionalOptions(CommandLine cmd) throws Exception;

    protected abstract List<Option> getRequiredOptions();

    protected abstract List<Option> getOptionalOptions();

    public void parseCommonRequiredOptions(CommandLine cmd) throws Exception {
        dataFile = Args.getPathFile(cmd.getOptionValue(CmdLongOpts.DATA), true);
    }

    public void parseCommonOptionalOptions(CommandLine cmd) throws Exception {
        knowledgeFile = Args.getPathFile(cmd.getOptionValue(CmdLongOpts.KNOWLEDGE, null), false);
        excludedVariableFile = Args.getPathFile(cmd.getOptionValue(CmdLongOpts.EXCLUDE_VARS, null), false);
        delimiter = Args.getDelimiterForName(cmd.getOptionValue(CmdLongOpts.DELIMITER, dataFile.getFileName().toString().endsWith(".csv") ? "comma" : "tab"));
        verbose = cmd.hasOption(CmdLongOpts.VERBOSE);
        numOfThreads = Args.getInteger(cmd.getOptionValue(CmdLongOpts.THREAD, Integer.toString(Runtime.getRuntime().availableProcessors())));
        isSerializeJson = cmd.hasOption(CmdLongOpts.JSON);
        tetradGraphJson = cmd.hasOption(CmdLongOpts.TETRAD_GRAPH_JSON);

        dirOut = Args.getPathDir(cmd.getOptionValue(CmdLongOpts.OUT, "."), false);
        outputPrefix = cmd.getOptionValue(CmdLongOpts.OUTPUT_PREFIX, "");
        validationOutput = !cmd.hasOption(CmdLongOpts.NO_VALIDATION_OUTPUT);
        skipLatest = cmd.hasOption(CmdLongOpts.SKIP_LATEST);
    }

    private void setOptions() {
        addOptions(mainOptions, getCommonRequiredOptions());
        addOptions(mainOptions, getCommonOptionalOptions());
        addOptions(mainOptions, getRequiredOptions());
        addOptions(mainOptions, getOptionalOptions());
    }

    protected List<Option> getCommonOptionalOptions() {
        List<Option> options = new LinkedList<>();
        options.add(new Option(null, CmdLongOpts.KNOWLEDGE, true, "A file containing prior knowledge."));
        options.add(new Option(null, CmdLongOpts.EXCLUDE_VARS, true, "A file containing variables to exclude."));
        options.add(new Option("d", CmdLongOpts.DELIMITER, true, "Data delimiter either comma, semicolon, space, colon, or tab. Default: comma for *.csv, else tab."));
        options.add(new Option(null, CmdLongOpts.VERBOSE, false, "Print additional information."));
        options.add(new Option(null, CmdLongOpts.THREAD, true, "Number of threads."));
        options.add(new Option(null, CmdLongOpts.JSON, false, "Create JSON output."));
        options.add(new Option(null, CmdLongOpts.TETRAD_GRAPH_JSON, false, "Create Tetrad Graph JSON output."));
        options.add(new Option("o", CmdLongOpts.OUT, true, "Output directory."));
        options.add(new Option(null, CmdLongOpts.OUTPUT_PREFIX, true, "Prefix name for output files."));
        options.add(new Option(null, CmdLongOpts.NO_VALIDATION_OUTPUT, false, "No validation output files created."));
        options.add(new Option(null, CmdLongOpts.HELP, false, "Show help."));
        options.add(new Option(null, CmdLongOpts.SKIP_LATEST, false, "Skip checking for latest software version"));

        return options;
    }

    protected List<Option> getCommonRequiredOptions() {
        List<Option> options = new LinkedList<>();

        Option requiredOption = new Option("f", CmdLongOpts.DATA, true, "Data file.");
        requiredOption.setRequired(true);
        options.add(requiredOption);

        return options;
    }

    protected void addOptions(Options options, List<Option> listOptions) {
        if (listOptions != null) {
            listOptions.forEach(option -> {
                options.addOption(option);
            });
        }
    }

    public Options getMainOptions() {
        return mainOptions;
    }

    public Path getDataFile() {
        return dataFile;
    }

    public Path getKnowledgeFile() {
        return knowledgeFile;
    }

    public Path getExcludedVariableFile() {
        return excludedVariableFile;
    }

    public Delimiter getDelimiter() {
        return delimiter;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public int getNumOfThreads() {
        return numOfThreads;
    }

    public boolean isIsSerializeJson() {
        return isSerializeJson;
    }

    public boolean isTetradGraphJson() {
        return tetradGraphJson;
    }

    public Path getDirOut() {
        return dirOut;
    }

    public String getOutputPrefix() {
        return outputPrefix;
    }

    public boolean isValidationOutput() {
        return validationOutput;
    }

    public boolean isSkipLatest() {
        return skipLatest;
    }

}
