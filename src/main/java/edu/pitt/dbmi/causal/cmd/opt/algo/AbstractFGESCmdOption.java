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
 * May 25, 2017 2:39:38 PM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public abstract class AbstractFGESCmdOption extends TetradCmdAlgoOpt implements CmdOption {

    protected int maxDegree;

    protected boolean symmetricFirstStep;
    protected boolean faithfulnessAssumed;

    public AbstractFGESCmdOption() {
        super();
    }

    @Override
    protected void parseOptionalOptions(CommandLine cmd) throws Exception {
        maxDegree = CmdLongOpts.getInt(CmdLongOpts.MAX_DEGREE, ParamAttrs.MAX_DEGREE, cmd);
        symmetricFirstStep = cmd.hasOption(CmdLongOpts.SYMMETRIC_FIRST_STEP);
        faithfulnessAssumed = cmd.hasOption(CmdLongOpts.FAITHFULNESS_ASSUMED);
    }

    @Override
    protected List<Option> getOptionalOptions() {
        List<Option> options = new LinkedList<>();
        options.add(new Option(null, CmdLongOpts.MAX_DEGREE, true, CmdLongOpts.getDescription(CmdLongOpts.MAX_DEGREE)));
        options.add(new Option(null, CmdLongOpts.SYMMETRIC_FIRST_STEP, false, CmdLongOpts.getDescription(CmdLongOpts.SYMMETRIC_FIRST_STEP)));
        options.add(new Option(null, CmdLongOpts.FAITHFULNESS_ASSUMED, false, CmdLongOpts.getDescription(CmdLongOpts.FAITHFULNESS_ASSUMED)));

        return options;
    }

    public int getMaxDegree() {
        return maxDegree;
    }

    public boolean isSymmetricFirstStep() {
        return symmetricFirstStep;
    }

    public boolean isFaithfulnessAssumed() {
        return faithfulnessAssumed;
    }

}
