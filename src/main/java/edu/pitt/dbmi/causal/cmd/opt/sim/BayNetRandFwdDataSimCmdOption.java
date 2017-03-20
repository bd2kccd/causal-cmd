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
 * Mar 15, 2017 3:17:30 PM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public class BayNetRandFwdDataSimCmdOption extends SemRandFwdDataSimCmdOption {

    protected int minCategories;
    protected int maxCategories;

    public BayNetRandFwdDataSimCmdOption() {
        super();
    }

    @Override
    protected void parseOptionalOptions(CommandLine cmd) throws Exception {
        super.parseOptionalOptions(cmd);
        minCategories = CmdLongOpts.getInt(CmdLongOpts.MIN_CATEGORIES, ParamAttrs.MIN_CATEGORIES, cmd);
        maxCategories = CmdLongOpts.getInt(CmdLongOpts.MAX_CATEGORIES, ParamAttrs.MAX_CATEGORIES, cmd);
    }

    @Override
    protected List<Option> getOptionalOptions() {
        List<Option> options = super.getOptionalOptions();
        options.add(new Option(null, CmdLongOpts.MIN_CATEGORIES, true, CmdLongOpts.createDescription(ParamAttrs.MIN_CATEGORIES)));
        options.add(new Option(null, CmdLongOpts.MAX_CATEGORIES, true, CmdLongOpts.createDescription(ParamAttrs.MAX_CATEGORIES)));

        return options;
    }

    public int getMinCategories() {
        return minCategories;
    }

    public int getMaxCategories() {
        return maxCategories;
    }

}
