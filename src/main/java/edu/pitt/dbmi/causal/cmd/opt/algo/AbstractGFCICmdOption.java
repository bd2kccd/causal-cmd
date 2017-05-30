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

import edu.pitt.dbmi.causal.cmd.ParamAttrs;
import edu.pitt.dbmi.causal.cmd.opt.CmdLongOpts;
import edu.pitt.dbmi.causal.cmd.opt.CmdOption;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

/**
 *
 * May 25, 2017 4:14:49 PM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public abstract class AbstractGFCICmdOption extends TetradCmdAlgoOpt implements CmdOption {

    protected int maxDegree;
    protected int maxPathLength;
    protected boolean faithfulnessAssumed;
    protected boolean completeRuleSetUsed;

    public AbstractGFCICmdOption() {
        super();
    }

    @Override
    protected void parseOptionalOptions(CommandLine cmd) throws Exception {
        maxDegree = CmdLongOpts.getInt(CmdLongOpts.MAX_DEGREE, ParamAttrs.MAX_DEGREE, cmd);
        maxPathLength = CmdLongOpts.getInt(CmdLongOpts.MAX_PATH_LENGTH, ParamAttrs.MAX_PATH_LENGTH, cmd);
        faithfulnessAssumed = cmd.hasOption(CmdLongOpts.FAITHFULNESS_ASSUMED);
        completeRuleSetUsed = cmd.hasOption(CmdLongOpts.COMPLETE_RULE_SET_USED);
    }

    @Override
    protected List<Option> getOptionalOptions() {
        List<Option> options = new LinkedList<>();
        options.add(new Option(null, CmdLongOpts.MAX_DEGREE, true, CmdLongOpts.getDescription(CmdLongOpts.MAX_DEGREE)));
        options.add(new Option(null, CmdLongOpts.MAX_PATH_LENGTH, true, CmdLongOpts.getDescription(CmdLongOpts.MAX_PATH_LENGTH)));
        options.add(new Option(null, CmdLongOpts.FAITHFULNESS_ASSUMED, false, CmdLongOpts.getDescription(CmdLongOpts.FAITHFULNESS_ASSUMED)));
        options.add(new Option(null, CmdLongOpts.COMPLETE_RULE_SET_USED, false, CmdLongOpts.getDescription(CmdLongOpts.COMPLETE_RULE_SET_USED)));

        return options;
    }

    public int getMaxDegree() {
        return maxDegree;
    }

    public int getMaxPathLength() {
        return maxPathLength;
    }

    public boolean isFaithfulnessAssumed() {
        return faithfulnessAssumed;
    }

    public boolean isCompleteRuleSetUsed() {
        return completeRuleSetUsed;
    }

}
