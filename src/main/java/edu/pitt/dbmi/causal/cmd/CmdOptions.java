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

import edu.cmu.tetrad.util.ParamDescription;
import edu.cmu.tetrad.util.ParamDescriptions;
import edu.pitt.dbmi.causal.cmd.tetrad.TetradAlgorithms;
import edu.pitt.dbmi.causal.cmd.tetrad.TetradIndependenceTests;
import edu.pitt.dbmi.causal.cmd.tetrad.TetradScores;
import edu.pitt.dbmi.causal.cmd.util.DataTypes;
import edu.pitt.dbmi.causal.cmd.util.Delimiters;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 *
 * Aug 27, 2017 10:42:03 PM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public class CmdOptions {

    private static final CmdOptions INSTANCE = new CmdOptions();

    private final Map<String, Option> options = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    private CmdOptions() {
        addRequiredOptions();
        addOptionalOptions();
    }

    public static CmdOptions getInstance() {
        return INSTANCE;
    }

    public Option getLongOption(String param) {
        return options.get(param);
    }

    public boolean hasLongParam(String param) {
        return options.containsKey(param);
    }

    public Options getOptions() {
        Options opts = new Options();
        options.entrySet().forEach(e -> {
            opts.addOption(e.getValue());
        });

        return opts;
    }

    public Options getMainOptions() {
        Options opts = new Options();

        // required options
        getRequiredOptions().forEach(opt -> opts.addOption(opt));

        // dataset options
        opts.addOption(options.get(CmdParams.QUOTE_CHAR));
        opts.addOption(options.get(CmdParams.MISSING_MARKER));
        opts.addOption(options.get(CmdParams.COMMENT_MARKER));

        // additional data file options
        opts.addOption(options.get(CmdParams.EXCLUDE_VARIABLE));

        // output options
        opts.addOption(options.get(CmdParams.FILE_PREFIX));
        opts.addOption(options.get(CmdParams.DIR_OUT));

        // data validation options
        opts.addOption(options.get(CmdParams.SKIP_VALIDATION));

        // info options
        opts.addOption(options.get(CmdParams.VERSION));
        opts.addOption(options.get(CmdParams.HELP));

        return opts;
    }

    private void addOptionalOptions() {
        options.put(CmdParams.QUOTE_CHAR, new Option(null, CmdParams.QUOTE_CHAR, true, "Single character denotes quote."));
        options.put(CmdParams.MISSING_MARKER, new Option(null, CmdParams.MISSING_MARKER, true, "Denote missing value."));
        options.put(CmdParams.COMMENT_MARKER, new Option(null, CmdParams.COMMENT_MARKER, true, "Comment character."));

        options.put(CmdParams.HELP, new Option(null, CmdParams.HELP, false, "Show help."));
        options.put(CmdParams.VERSION, new Option(null, CmdParams.VERSION, false, "Show version."));
        options.put(CmdParams.FILE_PREFIX, new Option(null, CmdParams.FILE_PREFIX, true, "Output filename prefix."));
        options.put(CmdParams.DIR_OUT, new Option(null, CmdParams.DIR_OUT, true, "Output directory."));

        options.put(CmdParams.KNOWLEDGE, new Option(null, CmdParams.KNOWLEDGE, true, "Prior Knowledge."));
        options.put(CmdParams.EXCLUDE_VARIABLE, new Option(null, CmdParams.EXCLUDE_VARIABLE, true, "Exclude variables."));

        options.put(CmdParams.SKIP_VALIDATION, new Option(null, CmdParams.SKIP_VALIDATION, false, "Skip validation."));

        options.put(CmdParams.TEST, new Option(null, CmdParams.TEST, true, getIndependenceTestDesc()));
        options.put(CmdParams.SCORE, new Option(null, CmdParams.SCORE, true, getScoreDesc()));

        // tetrad parameters
        ParamDescriptions paramDescs = ParamDescriptions.getInstance();
        Set<String> params = paramDescs.getNames();
        params.forEach(param -> {
            ParamDescription paramDesc = paramDescs.get(param);
            options.put(param, new Option(null, param, !(paramDesc.getDefaultValue() instanceof Boolean), paramDesc.getDescription()));
        });
    }

    private void addRequiredOptions() {
        options.put(CmdParams.ALGORITHM, Option.builder().longOpt(CmdParams.ALGORITHM).desc(getAlgorithmDesc()).hasArg().required().build());
        options.put(CmdParams.DATASET, Option.builder().longOpt(CmdParams.DATASET).desc("Dataset. Multiple files are seperated by commas.").hasArg().required().build());
        options.put(CmdParams.DELIMITER, Option.builder().longOpt(CmdParams.DELIMITER).desc(getDelimiterDesc()).hasArg().required().build());
        options.put(CmdParams.DATA_TYPE, Option.builder().longOpt(CmdParams.DATA_TYPE).desc(getDataTypeDesc()).hasArg().required().build());
    }

    public List<Option> getRequiredOptions() {
        return options.entrySet().stream()
                .filter(e -> e.getValue().isRequired())
                .map(e -> e.getValue())
                .collect(Collectors.toList());
    }

    private String getDataTypeDesc() {
        return "Data type: " + DataTypes.getInstance().getNames().stream()
                .collect(Collectors.joining(", "));
    }

    private String getDelimiterDesc() {
        return "Delimiter: " + Delimiters.getInstance().getNames().stream()
                .collect(Collectors.joining(", "));
    }

    private String getScoreDesc() {
        return "Score: " + TetradScores.getInstance().getCommands().stream()
                .collect(Collectors.joining(", "));
    }

    private String getIndependenceTestDesc() {
        return "Independence Test: " + TetradIndependenceTests.getInstance().getCommands().stream()
                .collect(Collectors.joining(", "));
    }

    private String getAlgorithmDesc() {
        return "Algorithm: " + TetradAlgorithms.getInstance().getCommands().stream()
                .collect(Collectors.joining(", "));
    }

}
