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
import edu.cmu.tetrad.data.IKnowledge;
import edu.cmu.tetrad.graph.Graph;
import edu.cmu.tetrad.util.Parameters;
import edu.pitt.dbmi.causal.cmd.AlgorithmRunException;
import edu.pitt.dbmi.causal.cmd.CmdArgs;
import edu.pitt.dbmi.causal.cmd.ValidationException;
import edu.pitt.dbmi.causal.cmd.util.DateTime;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Sep 27, 2017 10:12:49 AM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public class TetradAlgorithmRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(TetradAlgorithmRunner.class);

    private PrintStream out;
    private Graph graph;

    public TetradAlgorithmRunner() {
    }

    public void runAlgorithm(CmdArgs cmdArgs) throws AlgorithmRunException {
        if (out == null) {
            out = System.out;
        }

        if (!cmdArgs.isSkipValidation()) {
            try {
                TetradUtils.validateDataModels(cmdArgs, out);
            } catch (ValidationException exception) {
                throw new AlgorithmRunException(exception);
            }
        }

        final List<DataModel> dataModels = new LinkedList<>();
        try {
            dataModels.addAll(TetradUtils.getDataModels(cmdArgs, out));
        } catch (IOException exception) {
            throw new AlgorithmRunException(exception);
        }

        final Parameters parameters = TetradUtils.getParameters(cmdArgs);
        parameters.set("printStream", out);

        boolean verbose = parameters.getBoolean("verbose", false);

        final Algorithm algorithm = getAlgorithm(cmdArgs);
        if (TetradAlgorithms.getInstance().acceptKnowledge(cmdArgs.getAlgorithmClass())) {
            IKnowledge knowledge = null;
            try {
                knowledge = TetradUtils.readInKnowledge(cmdArgs, out);
            } catch (IOException exception) {
                throw new AlgorithmRunException(exception);
            }
            if (knowledge != null) {
                ((HasKnowledge) algorithm).setKnowledge(knowledge);
            }
        }

        out.printf("%nStart search: %s%n", DateTime.printNow());
        if (verbose) {
            out.println("--------------------------------------------------------------------------------");
        }

        if (TetradAlgorithms.getInstance().acceptMultipleDataset(cmdArgs.getAlgorithmClass())) {
            graph = ((MultiDataSetAlgorithm) algorithm).search(dataModels, parameters);
        } else {
            graph = algorithm.search(dataModels.get(0), parameters);
        }

        if (verbose) {
            out.println("--------------------------------------------------------------------------------");
        }
        out.printf("End search: %s%n", DateTime.printNow());
    }

    private Algorithm getAlgorithm(CmdArgs cmdArgs) throws AlgorithmRunException {
        Algorithm algorithm = null;
        try {
            algorithm = AlgorithmFactory.create(cmdArgs.getAlgorithmClass(), cmdArgs.getTestClass(), cmdArgs.getScoreClass());
        } catch (IllegalAccessException | InstantiationException exception) {
            throw new AlgorithmRunException(exception);
        }

        return algorithm;
    }

    public Graph getGraph() {
        return graph;
    }

    public void setOut(PrintStream out) {
        this.out = out;
    }

}
