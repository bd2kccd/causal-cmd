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
import edu.cmu.tetrad.graph.Dag;
import edu.cmu.tetrad.graph.Graph;
import edu.cmu.tetrad.graph.GraphUtils;
import edu.cmu.tetrad.graph.Node;
import edu.cmu.tetrad.graph.NodeType;
import edu.cmu.tetrad.search.DagToPag2;
import edu.cmu.tetrad.search.SearchGraphUtils;
import edu.cmu.tetrad.search.TsDagToPag;
import edu.cmu.tetrad.util.Parameters;
import edu.pitt.dbmi.causal.cmd.AlgorithmRunException;
import edu.pitt.dbmi.causal.cmd.CmdArgs;
import edu.pitt.dbmi.causal.cmd.data.DataFiles;
import edu.pitt.dbmi.causal.cmd.util.DateTime;
import java.io.IOException;
import java.io.PrintStream;
import java.rmi.MarshalledObject;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Oct 23, 2017 11:24:07 AM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public class TetradRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(TetradRunner.class);

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

        Graph graph;
        if (TetradAlgorithms.getInstance().acceptMultipleDataset(cmdArgs.getAlgorithmClass())) {
            graph = ((MultiDataSetAlgorithm) algorithm).search(dataModels, parameters);
        } else {
            graph = algorithm.search(dataModels.get(0), parameters);
        }

        if (graph != null) {
            graphs.add(manipulateGraph(graph));
        }

        if (verbose) {
            out.println("--------------------------------------------------------------------------------");
        }
        out.printf("End search: %s%n", DateTime.printNow());
    }

    /**
     * Manipulating graphs.
     *
     * @param graphs
     * @author: Chirayu Wongchokprasitti, PhD
     */
    private Graph manipulateGraph(Graph graph) {
        // graph manipulations
        if (cmdArgs.isChooseDagInPattern()) {
            try {

            } catch (Exception exception) {
                LOGGER.error("Unable to choose DAG in pattern graph.", exception);
            }
        }

        if (cmdArgs.isChooseMagInPag()) {
            try {
                graph = SearchGraphUtils.pagToMag(graph);
            } catch (Exception exception) {
                LOGGER.error("Unable to choose MAG in PAG.", exception);
            }
        }

        if (cmdArgs.isGeneratePatternFromDag()) {
            try {
                graph = SearchGraphUtils.patternFromDag(graph);
            } catch (Exception exception) {
                LOGGER.error("Unable to generate pattern graph from DAG.", exception);
            }
        }

        if (cmdArgs.isGeneratePagFromDag()) {
            try {
                // make sure the given graph is a dag.
                try {
                    new Dag(graph);
                } catch (Exception e) {
                    throw new IllegalArgumentException("The source graph is not a DAG.");
                }

                DagToPag2 p = new DagToPag2(graph);
                graph = p.convert();

            } catch (Exception exception) {
                LOGGER.error("Unable to generate PAG from DAG.", exception);
            }
        }

        if (cmdArgs.isGeneratePagFromTsDag()) {
            try {
                // make sure the given graph is a dag.
                try {
                    new Dag(graph);
                } catch (Exception e) {
                    throw new IllegalArgumentException("The source graph is not a DAG.");
                }

                TsDagToPag p = new TsDagToPag(graph);
                graph = p.convert();

            } catch (Exception exception) {
                LOGGER.error("Unable to generate PAG from DAG.", exception);
            }
        }

        if (cmdArgs.isMakeBidirectedUndirected()) {
            try {
                graph = GraphUtils.bidirectedToUndirected(graph);
            } catch (Exception exception) {
                LOGGER.error("Unable to make bidirected edges undirected.", exception);
            }
        }

        if (cmdArgs.isMakeUndirectedBidirected()) {
            try {
                graph = GraphUtils.undirectedToBidirected(graph);
            } catch (Exception exception) {
                LOGGER.error("Unable to make undirected edges bidirected.", exception);
            }
        }

        if (cmdArgs.isMakeAllEdgesUndirected()) {
            try {
                graph = GraphUtils.undirectedGraph(graph);
            } catch (Exception exception) {
                LOGGER.error("Unable to make all edges undirected.", exception);
            }
        }

        if (cmdArgs.isGenerateCompleteGraph()) {
            try {
                graph = GraphUtils.completeGraph(graph);
            } catch (Exception exception) {
                LOGGER.error("Unable to generate complete graph.", exception);
            }
        }

        if (cmdArgs.isExtractStructModel()) {
            try {
                List<Node> latents = new ArrayList<>();

                for (Node node : graph.getNodes()) {
                    if (node.getNodeType() == NodeType.LATENT) {
                        latents.add(node);
                    }
                }

                Graph graph2 = graph.subgraph(latents);

                graph = (Graph) new MarshalledObject(graph2).get();
            } catch (Exception exception) {
                LOGGER.error("Unable to extract structure model.", exception);
            }
        }

        return graph;
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
