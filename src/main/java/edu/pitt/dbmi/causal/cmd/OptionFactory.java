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

import edu.cmu.tetrad.data.DataType;
import edu.pitt.dbmi.causal.cmd.tetrad.TetradIndependenceTests;
import edu.pitt.dbmi.causal.cmd.tetrad.TetradScores;
import java.util.List;
import java.util.stream.Collectors;
import org.apache.commons.cli.Option;

/**
 *
 * Sep 15, 2017 1:45:16 PM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public final class OptionFactory {

    private OptionFactory() {
    }

    public static Option createRequiredHelpOpt() {
        Option opt = CmdOptions.getInstance().getLongOption(CmdParams.HELP);

        return Option.builder()
                .argName(opt.getArgName())
                .longOpt(opt.getLongOpt())
                .desc(opt.getDescription())
                .required()
                .type((Class) opt.getType())
                .build();
    }

    public static Option createRequiredTestOpt(DataType dataType) {
        List<String> commands = TetradIndependenceTests.getInstance().getCommands(dataType);
        Option opt = CmdOptions.getInstance().getLongOption(CmdParams.TEST);

        return Option.builder()
                .argName(opt.getArgName())
                .longOpt(opt.getLongOpt())
                .desc("Independence Test: " + commands.stream().collect(Collectors.joining(", ")))
                .hasArg()
                .required()
                .type((Class) opt.getType())
                .build();
    }

    public static Option createRequiredScoreOpt(DataType dataType) {
        List<String> commands = TetradScores.getInstance().getCommands(dataType);
        Option opt = CmdOptions.getInstance().getLongOption(CmdParams.SCORE);

        return Option.builder()
                .argName(opt.getArgName())
                .longOpt(opt.getLongOpt())
                .desc("Score: " + commands.stream().collect(Collectors.joining(", ")))
                .hasArg()
                .required()
                .type((Class) opt.getType())
                .build();
    }

    public static Option createRequiredNumCategoryOpt() {
        Option opt = CmdOptions.getInstance().getLongOption(CmdParams.NUM_CATEGORIES);

        return Option.builder()
                .argName(opt.getArgName())
                .longOpt(opt.getLongOpt())
                .desc(opt.getDescription())
                .hasArg()
                .required()
                .type((Class) opt.getType())
                .build();
    }

}
