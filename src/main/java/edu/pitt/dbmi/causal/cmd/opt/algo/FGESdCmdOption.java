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
 * Mar 14, 2017 4:38:01 PM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public class FGESdCmdOption extends TetradCmdAlgoOpt implements CmdOption {

    public static final int CATEGORY_LIMIT = 10;

    protected double structurePrior;
    protected double samplePrior;
    protected int maxDegree;
    protected boolean faithfulnessAssumed;

    protected boolean skipUniqueVarName;
    protected boolean skipCategoryLimit;

    public FGESdCmdOption() {
        super();
    }

    @Override
    protected void parseRequiredOptions(CommandLine cmd) throws Exception {
    }

    @Override
    protected void parseOptionalOptions(CommandLine cmd) throws Exception {
        structurePrior = CmdLongOpts.getDouble(CmdLongOpts.STRUCTURE_PRIOR, ParamAttrs.STRUCTURE_PRIOR, cmd);
        samplePrior = CmdLongOpts.getDouble(CmdLongOpts.SAMPLE_PRIOR, ParamAttrs.SAMPLE_PRIOR, cmd);
        maxDegree = CmdLongOpts.getInt(CmdLongOpts.MAX_DEGREE, ParamAttrs.MAX_DEGREE, cmd);
        faithfulnessAssumed = cmd.hasOption(CmdLongOpts.FAITHFULNESS_ASSUMED);
        skipUniqueVarName = cmd.hasOption(CmdLongOpts.SKIP_UNIQUE_VAR_NAME);
        skipCategoryLimit = cmd.hasOption(CmdLongOpts.SKIP_CATEGORY_LIMIT);

        String prefix = String.format("%s_%s_%d", AlgorithmType.FGESD.getCmd(), dataFile.getFileName(), System.currentTimeMillis());
        outputPrefix = cmd.getOptionValue("output-prefix", prefix);
    }

    @Override
    protected List<Option> getRequiredOptions() {
        return Collections.EMPTY_LIST;
    }

    @Override
    protected List<Option> getOptionalOptions() {
        List<Option> options = new LinkedList<>();
        options.add(new Option(null, CmdLongOpts.STRUCTURE_PRIOR, true, CmdLongOpts.createDescription(ParamAttrs.STRUCTURE_PRIOR)));
        options.add(new Option(null, CmdLongOpts.SAMPLE_PRIOR, true, CmdLongOpts.createDescription(ParamAttrs.SAMPLE_PRIOR)));
        options.add(new Option(null, CmdLongOpts.MAX_DEGREE, true, CmdLongOpts.createDescription(ParamAttrs.MAX_DEGREE)));
        options.add(new Option(null, CmdLongOpts.FAITHFULNESS_ASSUMED, false, CmdLongOpts.getDescription(CmdLongOpts.FAITHFULNESS_ASSUMED)));
        options.add(new Option(null, CmdLongOpts.SKIP_UNIQUE_VAR_NAME, false, CmdLongOpts.getDescription(CmdLongOpts.SKIP_UNIQUE_VAR_NAME)));
        options.add(new Option(null, CmdLongOpts.SKIP_CATEGORY_LIMIT, false, CmdLongOpts.getDescription(CmdLongOpts.SKIP_CATEGORY_LIMIT)));

        return options;
    }

    public double getStructurePrior() {
        return structurePrior;
    }

    public double getSamplePrior() {
        return samplePrior;
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

    public boolean isSkipCategoryLimit() {
        return skipCategoryLimit;
    }

}
