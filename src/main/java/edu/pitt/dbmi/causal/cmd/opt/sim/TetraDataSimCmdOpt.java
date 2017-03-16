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
package edu.pitt.dbmi.causal.cmd.opt.sim;

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
 * Mar 15, 2017 11:14:42 AM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public abstract class TetraDataSimCmdOpt {

    protected final Options mainOptions = new Options();

    protected int numOfVariables;
    protected int numOfCases;
    protected Delimiter delimiter;
    protected Path dirOut;
    protected String outputPrefix;

    public TetraDataSimCmdOpt() {
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
        numOfVariables = Args.getIntegerMin(cmd.getOptionValue("var"), 1);
        numOfCases = Args.getIntegerMin(cmd.getOptionValue("case"), 1);
    }

    public void parseCommonOptionalOptions(CommandLine cmd) throws Exception {
        delimiter = Args.getDelimiterForName(cmd.getOptionValue("delimiter", "tab"));
        dirOut = Args.getPathDir(cmd.getOptionValue("out", "."), false);
        outputPrefix = cmd.getOptionValue("output-prefix", String.format("sim_data_%dvars_%dcases_%d", numOfVariables, numOfCases, System.currentTimeMillis()));
    }

    private void setOptions() {
        addOptions(mainOptions, getCommonRequiredOptions());
        addOptions(mainOptions, getCommonOptionalOptions());
        addOptions(mainOptions, getRequiredOptions());
        addOptions(mainOptions, getOptionalOptions());
    }

    protected List<Option> getCommonOptionalOptions() {
        List<Option> options = new LinkedList<>();
        options.add(new Option("d", CmdLongOpts.DELIMITER, true, "Data delimiter either comma, semicolon, space, colon, or tab. Default: comma for *.csv, else tab."));
        options.add(new Option("o", CmdLongOpts.OUT, true, "Output directory."));
        options.add(new Option(null, CmdLongOpts.OUTPUT_PREFIX, true, "Prefix name for output files."));
        options.add(new Option(null, CmdLongOpts.HELP, false, "Show help."));

        return options;
    }

    protected List<Option> getCommonRequiredOptions() {
        List<Option> options = new LinkedList<>();

        Option requiredOption = new Option(null, "var", true, "Number of variables.");
        requiredOption.setRequired(true);
        options.add(requiredOption);

        requiredOption = new Option(null, "case", true, "Number of cases.");
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

    public int getNumOfVariables() {
        return numOfVariables;
    }

    public int getNumOfCases() {
        return numOfCases;
    }

    public Delimiter getDelimiter() {
        return delimiter;
    }

    public Path getDirOut() {
        return dirOut;
    }

    public String getOutputPrefix() {
        return outputPrefix;
    }

}
