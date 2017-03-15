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
import edu.cmu.tetrad.algcomparison.simulation.LinearFisherModel;
import edu.cmu.tetrad.algcomparison.simulation.Simulation;
import edu.cmu.tetrad.util.Parameters;
import edu.pitt.dbmi.causal.cmd.ParamAttrs;
import edu.pitt.dbmi.causal.cmd.opt.sim.SemRandFwdDataSimCmdOption;
import edu.pitt.dbmi.causal.cmd.opt.sim.TetraDataSimCmdOpt;
import java.util.Formatter;

/**
 *
 * Mar 15, 2017 12:57:27 PM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public class SemRandFwdDataSimulationRunner extends AbstractDataSimulationRunner {

    public SemRandFwdDataSimulationRunner() {
    }

    @Override
    protected Parameters getParameters(TetraDataSimCmdOpt simCmdOpt) {
        SemRandFwdDataSimCmdOption cmdOption = (SemRandFwdDataSimCmdOption) simCmdOpt;

        int numOfLatentConfounders = cmdOption.getNumOfLatentConfounders();
        double avgDegree = cmdOption.getAvgDegree();
        int maxDegree = cmdOption.getMaxDegree();
        int maxIndegree = cmdOption.getMaxIndegree();
        int maxOutdegree = cmdOption.getMaxOutdegree();
        boolean connected = cmdOption.isConnected();

        int numOfVariables = cmdOption.getNumOfVariables();
        int numOfCases = cmdOption.getNumOfCases();

        Parameters parameters = new Parameters();

        // RandomForward parameters
        parameters.set(ParamAttrs.NUM_LATENTS, numOfLatentConfounders);
        parameters.set(ParamAttrs.AVG_DEGREE, avgDegree);
        parameters.set(ParamAttrs.MAX_DEGREE, maxDegree);
        parameters.set(ParamAttrs.MAX_INDEGREE, maxIndegree);
        parameters.set(ParamAttrs.MAX_OUTDEGREE, maxOutdegree);
        parameters.set(ParamAttrs.CONNECTED, connected);

        // SemSimulation parameters
        parameters.set(ParamAttrs.NUM_MEASURES, numOfVariables);
        parameters.set(ParamAttrs.NUM_RUNS, 1);
        parameters.set(ParamAttrs.DIFFERENT_GRAPHS, Boolean.FALSE);
        parameters.set(ParamAttrs.SAMPLE_SIZE, numOfCases);

        return parameters;
    }

    @Override
    protected void printSimulationParameters(Formatter fmt, TetraDataSimCmdOpt simCmdOpt) {
        SemRandFwdDataSimCmdOption cmdOption = (SemRandFwdDataSimCmdOption) simCmdOpt;

        int numOfLatentConfounders = cmdOption.getNumOfLatentConfounders();
        double avgDegree = cmdOption.getAvgDegree();
        int maxDegree = cmdOption.getMaxDegree();
        int maxIndegree = cmdOption.getMaxIndegree();
        int maxOutdegree = cmdOption.getMaxOutdegree();
        boolean connected = cmdOption.isConnected();

        fmt.format("Number of Latent Confounders: %d%n", numOfLatentConfounders);
        fmt.format("Average Degree: %f%n", avgDegree);
        fmt.format("Maximum Degree: %d%n", maxDegree);
        fmt.format("Maximum Indegree: %d%n", maxIndegree);
        fmt.format("Maximum Outdegree: %d%n", maxOutdegree);
        fmt.format("Connected: %s%n", connected);
    }

    @Override
    protected Simulation getSimulation() {
        return new LinearFisherModel(new RandomForward());
    }

    @Override
    protected TetraDataSimCmdOpt getCommandLineOptions() {
        return new SemRandFwdDataSimCmdOption();
    }

    @Override
    public DataSimulationType getSimulationType() {
        return DataSimulationType.SEM_RAND_FWD;
    }

}
