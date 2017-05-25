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
import java.util.Collections;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

/**
 * FGES conditional Gaussian score for mixed variables.
 *
 * May 25, 2017 3:52:34 PM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public class FGESmCGCmdOption extends AbstractFGESCmdOption {

    protected double penaltyDiscount;
    protected double structurePrior;

    protected boolean discretize;
    protected int numCategoriesToDiscretize;

    public FGESmCGCmdOption() {
        super();
    }

    @Override
    public void parseRequiredOptions(CommandLine cmd) throws Exception {
        // no required options
    }

    @Override
    public void parseOptionalOptions(CommandLine cmd) throws Exception {
        super.parseOptionalOptions(cmd);

        penaltyDiscount = CmdLongOpts.getDouble(CmdLongOpts.PENALTY_DISCOUNT, ParamAttrs.PENALTY_DISCOUNT, cmd);
        structurePrior = CmdLongOpts.getDouble(CmdLongOpts.STRUCTURE_PRIOR, ParamAttrs.STRUCTURE_PRIOR, cmd);

        numCategoriesToDiscretize = CmdLongOpts.getInt(CmdLongOpts.NUM_CATEGORIES_TO_DISCRETIZE, ParamAttrs.NUM_CATEGORIES_TO_DISCRETIZE, cmd);
        discretize = cmd.hasOption(CmdLongOpts.DISCRETIZE);

        String prefix = String.format("%s_%s_%d", AlgorithmType.FGESM_CG.getCmd(), dataFile.getFileName(), System.currentTimeMillis());
        outputPrefix = cmd.getOptionValue("output-prefix", prefix);
    }

    @Override
    public List<Option> getRequiredOptions() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<Option> getOptionalOptions() {
        List<Option> options = super.getOptionalOptions();
        options.add(new Option(null, CmdLongOpts.PENALTY_DISCOUNT, true, CmdLongOpts.getDescription(CmdLongOpts.PENALTY_DISCOUNT)));
        options.add(new Option(null, CmdLongOpts.STRUCTURE_PRIOR, true, CmdLongOpts.createDescription(ParamAttrs.STRUCTURE_PRIOR)));
        options.add(new Option(null, CmdLongOpts.NUM_CATEGORIES_TO_DISCRETIZE, true, CmdLongOpts.getDescription(CmdLongOpts.NUM_CATEGORIES_TO_DISCRETIZE)));
        options.add(new Option(null, CmdLongOpts.DISCRETIZE, false, CmdLongOpts.getDescription(CmdLongOpts.DISCRETIZE)));

        return options;
    }

}
