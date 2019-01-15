/*
 * Copyright (C) 2019 University of Pittsburgh.
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
package edu.pitt.dbmi.causal.cmd.tetrad;

import edu.cmu.tetrad.algcomparison.algorithm.Algorithm;
import edu.cmu.tetrad.algcomparison.algorithm.AlgorithmFactory;
import edu.cmu.tetrad.algcomparison.algorithm.MultiDataSetAlgorithm;
import edu.cmu.tetrad.algcomparison.utils.HasKnowledge;
import edu.cmu.tetrad.data.DataModel;
import edu.cmu.tetrad.data.IKnowledge;
import edu.cmu.tetrad.graph.Graph;
import edu.cmu.tetrad.util.Parameters;
import edu.pitt.dbmi.causal.cmd.AlgorithmRunException;
import edu.pitt.dbmi.causal.cmd.CmdArgs;
import edu.pitt.dbmi.causal.cmd.data.DataFiles;
import edu.pitt.dbmi.causal.cmd.util.DateTime;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * Oct 23, 2017 11:24:07 AM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public class TetradRunner {

    private final CmdArgs cmdArgs;

    private final List<Graph> graphs;

    public TetradRunner(CmdArgs cmdArgs) {
        this.cmdArgs = cmdArgs;
        this.graphs = new LinkedList<>();
    }

    public void runAlgorithm(PrintStream out) throws AlgorithmRunException, IOException {
        final List<DataModel> dataModels = DataFiles.readInDatasets(cmdArgs, out);

        final Algorithm algorithm = getAlgorithm(cmdArgs);

        // add knowledge, if any
        if (TetradAlgorithms.getInstance().acceptKnowledge(cmdArgs.getAlgorithmClass())) {
            IKnowledge knowledge = DataFiles.readInKnowledge(cmdArgs, out);
            if (knowledge != null) {
                ((HasKnowledge) algorithm).setKnowledge(knowledge);
            }
        }

        final Parameters parameters = Tetrad.getParameters(cmdArgs);
        parameters.set("printStream", out);

        boolean verbose = parameters.getBoolean("verbose", false);

        out.printf("%nStart search: %s%n", DateTime.printNow());
        if (verbose) {
            out.println("--------------------------------------------------------------------------------");
        }

        if (TetradAlgorithms.getInstance().acceptMultipleDataset(cmdArgs.getAlgorithmClass())) {
            graphs.add(((MultiDataSetAlgorithm) algorithm).search(dataModels, parameters));
        } else {
            graphs.add(algorithm.search(dataModels.get(0), parameters));
        }

        if (verbose) {
            out.println("--------------------------------------------------------------------------------");
        }
        out.printf("End search: %s%n", DateTime.printNow());
    }

    private Algorithm getAlgorithm(CmdArgs cmdArgs) throws AlgorithmRunException {
        try {
            return AlgorithmFactory.create(cmdArgs.getAlgorithmClass(), cmdArgs.getTestClass(), cmdArgs.getScoreClass());
        } catch (IllegalAccessException | InstantiationException exception) {
            throw new AlgorithmRunException(exception);
        }
    }

    public List<Graph> getGraphs() {
        return graphs;
    }

}
