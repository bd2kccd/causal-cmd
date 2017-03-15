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
package edu.pitt.dbmi.causal.cmd.sim;

import edu.cmu.tetrad.algcomparison.graph.RandomForward;
import edu.cmu.tetrad.algcomparison.simulation.BayesNetSimulation;
import edu.cmu.tetrad.algcomparison.simulation.Simulation;
import edu.cmu.tetrad.util.Parameters;
import edu.pitt.dbmi.causal.cmd.ParamAttrs;
import edu.pitt.dbmi.causal.cmd.opt.sim.BayNetRandFwdDataSimCmdOption;
import edu.pitt.dbmi.causal.cmd.opt.sim.TetraDataSimCmdOpt;
import java.util.Formatter;

/**
 *
 * Mar 15, 2017 3:35:10 PM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public class BayNetRandFwdDataSimulationRunner extends SemRandFwdDataSimulationRunner {

    public BayNetRandFwdDataSimulationRunner() {
    }

    @Override
    protected Parameters getParameters(TetraDataSimCmdOpt simCmdOpt) {
        Parameters parameters = super.getParameters(simCmdOpt);

        BayNetRandFwdDataSimCmdOption cmdOption = (BayNetRandFwdDataSimCmdOption) simCmdOpt;
        int minCategories = cmdOption.getMinCategories();
        int maxCategories = cmdOption.getMaxCategories();

        // BayesPm
        parameters.set(ParamAttrs.MIN_CATEGORIES, minCategories);
        parameters.set(ParamAttrs.MAX_CATEGORIES, maxCategories);

        return parameters;
    }

    @Override
    protected void printSimulationParameters(Formatter fmt, TetraDataSimCmdOpt simCmdOpt) {
        super.printSimulationParameters(fmt, simCmdOpt);

        BayNetRandFwdDataSimCmdOption cmdOption = (BayNetRandFwdDataSimCmdOption) simCmdOpt;
        int minCategories = cmdOption.getMinCategories();
        int maxCategories = cmdOption.getMaxCategories();

        fmt.format("Minimum Categories: %d%n", minCategories);
        fmt.format("Maximum Categories: %d%n", maxCategories);
    }

    @Override
    protected Simulation getSimulation() {
        return new BayesNetSimulation(new RandomForward());
    }

    @Override
    protected TetraDataSimCmdOpt getCommandLineOptions() {
        return new BayNetRandFwdDataSimCmdOption();
    }

    @Override
    public DataSimulationType getSimulationType() {
        return DataSimulationType.BAYES_NET_RAND_FWD;
    }

}
