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

import edu.cmu.tetrad.algcomparison.simulation.Simulation;
import edu.cmu.tetrad.data.DataModel;
import edu.cmu.tetrad.graph.Graph;
import edu.cmu.tetrad.util.Parameters;
import edu.pitt.dbmi.causal.cmd.opt.sim.TetraDataSimCmdOpt;
import edu.pitt.dbmi.causal.cmd.util.AppUtils;
import edu.pitt.dbmi.causal.cmd.util.Args;
import edu.pitt.dbmi.causal.cmd.util.DataSetIO;
import edu.pitt.dbmi.causal.cmd.util.GraphIO;
import edu.pitt.dbmi.data.Delimiter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Formatter;
import org.apache.commons.cli.Options;

/**
 *
 * Mar 15, 2017 12:48:52 PM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public abstract class AbstractDataSimulationRunner implements DataSimulationRunner {

    public AbstractDataSimulationRunner() {
    }

    protected abstract Parameters getParameters(TetraDataSimCmdOpt simCmdOpt);

    protected abstract void printSimulationParameters(Formatter fmt, TetraDataSimCmdOpt simCmdOpt);

    protected abstract Simulation getSimulation();

    protected abstract TetraDataSimCmdOpt getCommandLineOptions();

    public abstract DataSimulationType getSimulationType();

    @Override
    public void runDataSimulation(String[] args) {
        DataSimulationType simulationType = getSimulationType();
        TetraDataSimCmdOpt simCmdOpt = getCommandLineOptions();
        if (needsToShowHelp(args)) {
            showHelp(simulationType.getCmd(), simCmdOpt.getMainOptions());
            return;
        }

        simCmdOpt.parseOptions(args);

        Simulation simulation = getSimulation();
        System.out.println(printInformation(simulation, simCmdOpt));
        simulation.createData(getParameters(simCmdOpt));

        writeGraphToFile(simulation.getTrueGraph(0), simCmdOpt);
        writeDatasetToFile(simulation.getDataModel(0), simCmdOpt);
    }

    protected void writeDatasetToFile(DataModel dataModel, TetraDataSimCmdOpt simCmdOpt) {
        Delimiter delimiter = simCmdOpt.getDelimiter();
        Path dirOut = simCmdOpt.getDirOut();
        String outputPrefix = simCmdOpt.getOutputPrefix();

        Path outputFile = Paths.get(dirOut.toString(), outputPrefix + ".txt");
        try {
            DataSetIO.write(dataModel, (char) delimiter.getDelimiterChar(), outputFile);
        } catch (IOException exception) {
            exception.printStackTrace(System.err);
        }
    }

    protected void writeGraphToFile(Graph graph, TetraDataSimCmdOpt simCmdOpt) {
        Path dirOut = simCmdOpt.getDirOut();
        String outputPrefix = simCmdOpt.getOutputPrefix();

        Path outputFile = Paths.get(dirOut.toString(), outputPrefix + ".graph");
        try {
            GraphIO.write(graph, outputFile);
        } catch (IOException exception) {
            exception.printStackTrace(System.err);
        }
    }

    protected String printInformation(Simulation simulation, TetraDataSimCmdOpt simCmdOpt) {
        int numOfVariables = simCmdOpt.getNumOfVariables();
        int numOfCases = simCmdOpt.getNumOfCases();
        Delimiter delimiter = simCmdOpt.getDelimiter();
        Path dirOut = simCmdOpt.getDirOut();
        String outputPrefix = simCmdOpt.getOutputPrefix();

        Formatter fmt = new Formatter();
        fmt.format("================================================================================%n");
        fmt.format("%s (%s)%n", getSimulationType().getTitle(), AppUtils.fmtDateNow());
        fmt.format("================================================================================%n");
        fmt.format("Description: %s%n", simulation.getDescription());
        fmt.format("Delimiter: %s%n", delimiter.getName());
        fmt.format("Number of Variables: %d%n", numOfVariables);
        fmt.format("Number of Cases: %d%n", numOfCases);
        fmt.format("Directory Out: %s%n", dirOut.getFileName().toString());
        fmt.format("Output File Prefix: %s%n", outputPrefix);
        printSimulationParameters(fmt, simCmdOpt);

        return fmt.toString().trim();
    }

    protected void showHelp(String cmd, Options mainOptions) {
        AppUtils.showHelp(cmd, mainOptions);
    }

    protected boolean needsToShowHelp(String[] args) {
        return args == null || args.length == 0 || Args.hasLongOption(args, "help");
    }

}
