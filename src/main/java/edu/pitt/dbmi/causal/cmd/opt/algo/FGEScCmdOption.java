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
import edu.pitt.dbmi.causal.cmd.algo.AlgorithmType;
import edu.pitt.dbmi.causal.cmd.opt.CmdLongOpts;
import edu.pitt.dbmi.causal.cmd.opt.CmdOption;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

/**
 *
 * Mar 12, 2017 1:20:42 AM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public class FGEScCmdOption extends TetradCmdAlgoOpt implements CmdOption {

    protected double penaltyDiscount;
    protected int maxDegree;
    protected boolean faithfulnessAssumed;

    protected boolean skipUniqueVarName;
    protected boolean skipZeroVariance;

    public FGEScCmdOption() {
    }

    @Override
    public void parseRequiredOptions(CommandLine cmd) throws Exception {
    }

    @Override
    public void parseOptionalOptions(CommandLine cmd) throws Exception {
        penaltyDiscount = CmdLongOpts.getDouble(CmdLongOpts.PENALTY_DISCOUNT, ParamAttrs.PENALTY_DISCOUNT, cmd);
        maxDegree = CmdLongOpts.getInt(CmdLongOpts.MAX_DEGREE, ParamAttrs.MAX_DEGREE, cmd);
        faithfulnessAssumed = cmd.hasOption(CmdLongOpts.FAITHFULNESS_ASSUMED);
        skipUniqueVarName = cmd.hasOption(CmdLongOpts.SKIP_UNIQUE_VAR_NAME);
        skipZeroVariance = cmd.hasOption(CmdLongOpts.SKIP_NONZERO_VARIANCE);

        if (outputPrefix.isEmpty()) {
            outputPrefix = String.format("%s_%s_%d", AlgorithmType.FGESC.getCmd(), dataFile.getFileName(), System.currentTimeMillis());
        }
    }

    @Override
    public List<Option> getRequiredOptions() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<Option> getOptionalOptions() {
        List<Option> options = new LinkedList<>();
        options.add(new Option(null, CmdLongOpts.PENALTY_DISCOUNT, true, CmdLongOpts.getDescription(CmdLongOpts.PENALTY_DISCOUNT)));
        options.add(new Option(null, CmdLongOpts.MAX_DEGREE, true, CmdLongOpts.getDescription(CmdLongOpts.MAX_DEGREE)));
        options.add(new Option(null, CmdLongOpts.FAITHFULNESS_ASSUMED, false, CmdLongOpts.getDescription(CmdLongOpts.FAITHFULNESS_ASSUMED)));
        options.add(new Option(null, CmdLongOpts.SKIP_UNIQUE_VAR_NAME, false, CmdLongOpts.getDescription(CmdLongOpts.SKIP_UNIQUE_VAR_NAME)));
        options.add(new Option(null, CmdLongOpts.SKIP_NONZERO_VARIANCE, false, CmdLongOpts.getDescription(CmdLongOpts.SKIP_NONZERO_VARIANCE)));

        return options;
    }

    public double getPenaltyDiscount() {
        return penaltyDiscount;
    }

    public int getMaxDegree() {
        return maxDegree;
    }

    public boolean isFaithfulnessAssumed() {
        return faithfulnessAssumed;
    }

    public boolean isSkipUniqueVarName() {
        return skipUniqueVarName;
    }

    public boolean isSkipZeroVariance() {
        return skipZeroVariance;
    }

}
