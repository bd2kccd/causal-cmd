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
import java.util.List;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

/**
 *
 * Apr 10, 2017 12:17:01 PM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public class LeeHastieDataSimCmdOption extends BayNetRandFwdDataSimCmdOption {

    protected int percentDiscrete;

    public LeeHastieDataSimCmdOption() {
        super();
    }

    @Override
    protected void parseOptionalOptions(CommandLine cmd) throws Exception {
        super.parseOptionalOptions(cmd);
        percentDiscrete = CmdLongOpts.getInt(CmdLongOpts.PERCENT_DISCRETE, ParamAttrs.PERCENT_DISCRETE, cmd);
    }

    @Override
    protected List<Option> getOptionalOptions() {
        List<Option> options = super.getOptionalOptions();
        options.add(new Option(null, CmdLongOpts.PERCENT_DISCRETE, true, CmdLongOpts.createDescription(ParamAttrs.PERCENT_DISCRETE)));

        return options;
    }

    public int getPercentDiscrete() {
        return percentDiscrete;
    }

}
