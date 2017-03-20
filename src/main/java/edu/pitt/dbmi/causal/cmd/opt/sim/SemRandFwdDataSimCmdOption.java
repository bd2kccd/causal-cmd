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

import edu.pitt.dbmi.causal.cmd.ParamAttrs;
import edu.pitt.dbmi.causal.cmd.opt.CmdLongOpts;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

/**
 *
 * Mar 15, 2017 12:28:29 PM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public class SemRandFwdDataSimCmdOption extends TetraDataSimCmdOpt {

    protected int numOfLatentConfounders;
    protected double avgDegree;
    protected int maxDegree;
    protected int maxIndegree;
    protected int maxOutdegree;
    protected boolean connected;

    public SemRandFwdDataSimCmdOption() {
        super();
    }

    @Override
    protected void parseRequiredOptions(CommandLine cmd) throws Exception {
        // no required options
    }

    @Override
    protected void parseOptionalOptions(CommandLine cmd) throws Exception {
        numOfLatentConfounders = CmdLongOpts.getInt(CmdLongOpts.LATENT, ParamAttrs.NUM_LATENTS, cmd);
        avgDegree = CmdLongOpts.getDouble(CmdLongOpts.AVG_DEGREE, ParamAttrs.AVG_DEGREE, cmd);
        maxDegree = CmdLongOpts.getInt(CmdLongOpts.MAX_DEGREE, ParamAttrs.MAX_DEGREE, cmd);
        maxIndegree = CmdLongOpts.getInt(CmdLongOpts.MAX_INDEGREE, ParamAttrs.MAX_INDEGREE, cmd);
        maxOutdegree = CmdLongOpts.getInt(CmdLongOpts.MAX_OUTDEGREE, ParamAttrs.MAX_OUTDEGREE, cmd);
        connected = cmd.hasOption(CmdLongOpts.CONNECTED);
    }

    @Override
    protected List<Option> getRequiredOptions() {
        return Collections.EMPTY_LIST;
    }

    @Override
    protected List<Option> getOptionalOptions() {
        List<Option> options = new LinkedList<>();
        options.add(new Option(null, CmdLongOpts.LATENT, true, CmdLongOpts.createDescription(ParamAttrs.NUM_LATENTS)));
        options.add(new Option(null, CmdLongOpts.AVG_DEGREE, true, CmdLongOpts.createDescription(ParamAttrs.AVG_DEGREE)));
        options.add(new Option(null, CmdLongOpts.MAX_DEGREE, true, CmdLongOpts.createDescription(ParamAttrs.MAX_DEGREE)));
        options.add(new Option(null, CmdLongOpts.MAX_INDEGREE, true, CmdLongOpts.createDescription(ParamAttrs.MAX_INDEGREE)));
        options.add(new Option(null, CmdLongOpts.MAX_OUTDEGREE, true, CmdLongOpts.createDescription(ParamAttrs.MAX_OUTDEGREE)));
        options.add(new Option(null, CmdLongOpts.CONNECTED, false, CmdLongOpts.createDescription(ParamAttrs.CONNECTED)));

        return options;
    }

    public int getNumOfLatentConfounders() {
        return numOfLatentConfounders;
    }

    public double getAvgDegree() {
        return avgDegree;
    }

    public int getMaxDegree() {
        return maxDegree;
    }

    public int getMaxIndegree() {
        return maxIndegree;
    }

    public int getMaxOutdegree() {
        return maxOutdegree;
    }

    public boolean isConnected() {
        return connected;
    }

}
