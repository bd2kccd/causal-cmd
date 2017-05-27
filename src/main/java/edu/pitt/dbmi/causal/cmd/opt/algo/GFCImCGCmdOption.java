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
import edu.pitt.dbmi.causal.cmd.util.Args;
import java.util.Collections;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

/**
 * GFCI conditional Gaussian score for mixed variables.
 *
 * May 26, 2017 11:06:07 PM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public class GFCImCGCmdOption extends AbstractGFCICmdOption {

    protected double alpha;
    protected double penaltyDiscount;
    protected double structurePrior;
    protected int numCategoriesToDiscretize;
    protected int numberOfDiscreteCategories;
    protected boolean discretize;

    public GFCImCGCmdOption() {
        super();
    }

    @Override
    public void parseRequiredOptions(CommandLine cmd) throws Exception {
        // no required options
    }

    @Override
    public void parseOptionalOptions(CommandLine cmd) throws Exception {
        super.parseOptionalOptions(cmd);

        alpha = CmdLongOpts.getDouble(CmdLongOpts.ALPHA, ParamAttrs.ALPHA, cmd);
        penaltyDiscount = CmdLongOpts.getDouble(CmdLongOpts.PENALTY_DISCOUNT, ParamAttrs.PENALTY_DISCOUNT, cmd);
        structurePrior = CmdLongOpts.getDouble(CmdLongOpts.STRUCTURE_PRIOR, ParamAttrs.STRUCTURE_PRIOR, cmd);
        numCategoriesToDiscretize = CmdLongOpts.getInt(CmdLongOpts.NUM_CATEGORIES_TO_DISCRETIZE, ParamAttrs.NUM_CATEGORIES_TO_DISCRETIZE, cmd);
        numberOfDiscreteCategories = Args.getInteger(cmd.getOptionValue(CmdLongOpts.NUM_DISCRETE_CATEGORIES, "3"));
        discretize = cmd.hasOption(CmdLongOpts.DISCRETIZE);

        String prefix = String.format("%s_%s_%d", AlgorithmType.GFCIM_CG.getCmd(), dataFile.getFileName(), System.currentTimeMillis());
        outputPrefix = cmd.getOptionValue("output-prefix", prefix);
    }

    @Override
    public List<Option> getRequiredOptions() {
        return Collections.EMPTY_LIST;
    }

    @Override
    public List<Option> getOptionalOptions() {
        List<Option> options = super.getOptionalOptions();
        options.add(new Option(null, CmdLongOpts.ALPHA, true, CmdLongOpts.getDescription(CmdLongOpts.ALPHA)));
        options.add(new Option(null, CmdLongOpts.PENALTY_DISCOUNT, true, CmdLongOpts.getDescription(CmdLongOpts.PENALTY_DISCOUNT)));
        options.add(new Option(null, CmdLongOpts.STRUCTURE_PRIOR, true, CmdLongOpts.createDescription(ParamAttrs.STRUCTURE_PRIOR)));
        options.add(new Option(null, CmdLongOpts.NUM_CATEGORIES_TO_DISCRETIZE, true, CmdLongOpts.getDescription(CmdLongOpts.NUM_CATEGORIES_TO_DISCRETIZE)));
        options.add(new Option(null, CmdLongOpts.NUM_DISCRETE_CATEGORIES, true, "Number of category considered discrete variable."));
        options.add(new Option(null, CmdLongOpts.DISCRETIZE, false, CmdLongOpts.getDescription(CmdLongOpts.DISCRETIZE)));

        return options;
    }

    public double getAlpha() {
        return alpha;
    }

    public double getPenaltyDiscount() {
        return penaltyDiscount;
    }

    public double getStructurePrior() {
        return structurePrior;
    }

    public int getNumCategoriesToDiscretize() {
        return numCategoriesToDiscretize;
    }

    public int getNumberOfDiscreteCategories() {
        return numberOfDiscreteCategories;
    }

    public boolean isDiscretize() {
        return discretize;
    }

}
