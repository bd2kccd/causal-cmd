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
package edu.pitt.dbmi.causal.cmd.tetrad;

import edu.cmu.tetrad.algcomparison.algorithm.Algorithm;
import edu.cmu.tetrad.algcomparison.algorithm.AlgorithmFactory;
import edu.cmu.tetrad.algcomparison.algorithm.MultiDataSetAlgorithm;
import edu.cmu.tetrad.algcomparison.utils.HasKnowledge;
import edu.cmu.tetrad.data.DataModel;
import edu.cmu.tetrad.data.DataType;
import edu.cmu.tetrad.data.IKnowledge;
import edu.cmu.tetrad.graph.Graph;
import edu.cmu.tetrad.util.Parameters;
import edu.pitt.dbmi.causal.cmd.CmdArgs;
import edu.pitt.dbmi.causal.cmd.ValidationException;
import edu.pitt.dbmi.causal.cmd.util.DateTime;
import edu.pitt.dbmi.causal.cmd.util.TetradUtils;
import java.io.IOException;
import java.io.PrintStream;
import java.util.List;

/**
 *
 * Sep 27, 2017 10:12:49 AM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public class TetradAlgorithmRunner {

    private Graph graph;

    public TetradAlgorithmRunner() {
    }

    public void runAlgorithm(CmdArgs cmdArgs, PrintStream out) throws IOException, ValidationException, IllegalAccessException, InstantiationException {
        DataType dataType = cmdArgs.getDataType();
        switch (dataType) {
            case Covariance:
                runOnCovariance(cmdArgs, out);
                break;
            case Continuous:
            case Discrete:
            case Mixed:
                runOnTabularData(cmdArgs, out);
                break;
        }
    }

    private void runOnCovariance(CmdArgs cmdArgs, PrintStream out) {
    }

    private void runOnTabularData(CmdArgs cmdArgs, PrintStream out) throws IOException, ValidationException, IllegalAccessException, InstantiationException {
        List<DataModel> dataModels = TetradUtils.getDataModels(cmdArgs);
        IKnowledge knowledge = TetradUtils.readInKnowledge(cmdArgs);

        Algorithm algorithm = AlgorithmFactory.create(cmdArgs.getAlgorithmClass(), cmdArgs.getTestClass(), cmdArgs.getScoreClass());
        if (knowledge != null && TetradAlgorithms.getInstance().acceptKnowledge(cmdArgs.getAlgorithmClass())) {
            ((HasKnowledge) algorithm).setKnowledge(knowledge);
        }

        Parameters parameters = TetradUtils.getParameters(cmdArgs);
        parameters.set("printStream", out);

        out.printf("%nStart search: %s%n", DateTime.printNow());
        out.println("--------------------------------------------------------------------------------");
        if (TetradAlgorithms.getInstance().acceptMultipleDataset(cmdArgs.getAlgorithmClass())) {
            graph = ((MultiDataSetAlgorithm) algorithm).search(dataModels, parameters);
        } else {
            graph = algorithm.search(dataModels.get(0), parameters);
        }
        out.println("--------------------------------------------------------------------------------");
        out.printf("End search: %s%n", DateTime.printNow());
    }

    public Graph getGraph() {
        return graph;
    }

}
