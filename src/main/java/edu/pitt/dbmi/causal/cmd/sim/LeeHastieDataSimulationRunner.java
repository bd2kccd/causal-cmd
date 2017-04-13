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
import edu.cmu.tetrad.algcomparison.simulation.LeeHastieSimulation;
import edu.cmu.tetrad.algcomparison.simulation.Simulation;
import edu.cmu.tetrad.util.Parameters;
import edu.pitt.dbmi.causal.cmd.ParamAttrs;
import edu.pitt.dbmi.causal.cmd.opt.sim.LeeHastieDataSimCmdOption;
import edu.pitt.dbmi.causal.cmd.opt.sim.TetraDataSimCmdOpt;
import java.util.Formatter;

/**
 *
 * Apr 10, 2017 2:58:10 PM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public class LeeHastieDataSimulationRunner extends BayNetRandFwdDataSimulationRunner {

    public LeeHastieDataSimulationRunner() {
    }

    @Override
    protected Parameters getParameters(TetraDataSimCmdOpt simCmdOpt) {
        Parameters parameters = super.getParameters(simCmdOpt);

        LeeHastieDataSimCmdOption cmdOption = (LeeHastieDataSimCmdOption) simCmdOpt;
        int percentDiscrete = cmdOption.getPercentDiscrete();

        parameters.set(ParamAttrs.PERCENT_DISCRETE, percentDiscrete);

        return parameters;
    }

    @Override
    protected void printSimulationParameters(Formatter fmt, TetraDataSimCmdOpt simCmdOpt) {
        super.printSimulationParameters(fmt, simCmdOpt);

        LeeHastieDataSimCmdOption cmdOption = (LeeHastieDataSimCmdOption) simCmdOpt;
        int percentDiscrete = cmdOption.getPercentDiscrete();

        fmt.format("Percent Discrete: %d%n", percentDiscrete);
    }

    @Override
    protected Simulation getSimulation() {
        return new LeeHastieSimulation(new RandomForward());
    }

    @Override
    protected TetraDataSimCmdOpt getCommandLineOptions() {
        return new LeeHastieDataSimCmdOption();
    }

    @Override
    public DataSimulationType getSimulationType() {
        return DataSimulationType.LEE_HASTIE;
    }

}
