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
import edu.cmu.tetrad.algcomparison.algorithm.cluster.ClusterAlgorithm;
import edu.cmu.tetrad.algcomparison.utils.HasKnowledge;
import edu.cmu.tetrad.data.DataModel;
import edu.cmu.tetrad.data.DataSet;
import edu.cmu.tetrad.data.ICovarianceMatrix;
import edu.cmu.tetrad.data.Knowledge;
import edu.cmu.tetrad.graph.Dag;
import edu.cmu.tetrad.graph.Graph;
import edu.cmu.tetrad.graph.GraphUtils;
import edu.cmu.tetrad.graph.Node;
import edu.cmu.tetrad.graph.NodeType;
import edu.cmu.tetrad.search.DagToPag;
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
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class {@code TetradRunner} is a class for handling running Tetrad search
 * algorithms.
 *
 * Oct 23, 2017 11:24:07 AM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public class TetradRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(TetradRunner.class);

    private final CmdArgs cmdArgs;

    private final List<Graph> graphs;

    /**
     * Constructor.
     *
     * @param cmdArgs command-line inputs
     */
    public TetradRunner(CmdArgs cmdArgs) {
        this.cmdArgs = cmdArgs;
        this.graphs = new LinkedList<>();
    }

    /**
     * Run algorithm.
     *
     * @param out output stream to write message to
     * @throws AlgorithmRunException when algorithm run fails
     * @throws IOException when unable to read knowledge file
     */
    public void runAlgorithm(PrintStream out) throws AlgorithmRunException, IOException {
        final List<DataModel> dataModels = DataFiles.readInDatasets(cmdArgs, out);
        final Algorithm algorithm = getAlgorithm(cmdArgs);
        final Knowledge knowledge = DataFiles.readInKnowledge(cmdArgs, out);

        final boolean acceptsKnowledge = TetradAlgorithms.getInstance().acceptKnowledge(cmdArgs.getAlgorithmClass());
        final boolean hasKnowledge = !(knowledge == null || knowledge.getVariables().isEmpty());

        // add knowledge, if any
        if (acceptsKnowledge && hasKnowledge) {
            ((HasKnowledge) algorithm).setKnowledge(knowledge);
        }

        final Parameters parameters = Tetrad.getParameters(cmdArgs);
        parameters.set("printStream", out);

        // warn user about the default use of testwise deletion of data contain missing values
        boolean hasMissingValues = containsMissingValues(dataModels);
        boolean hasScore = cmdArgs.getScoreClass() != null;
        boolean hasTest = cmdArgs.getTestClass() != null;
        if (hasMissingValues && (hasScore || hasTest)) {
            out.println();
            out.println("WARNING: Dataset contains missing values;testwise deletion will be used in test and/or score.");
        }

        boolean verbose = parameters.getBoolean("verbose", false);

        out.printf("%nStart search: %s%n", DateTime.printNow());
        if (verbose) {
            out.println("--------------------------------------------------------------------------------");
        }

        List<Graph> graphList = runSearch(algorithm, parameters, dataModels);

        if (verbose) {
            out.println("--------------------------------------------------------------------------------");
        }
        out.printf("End search: %s%n", DateTime.printNow());

        graphList.forEach(graph -> graphs.add(manipulateGraph(graph)));
    }

    /**
     * Determine if any of the data model contains missing values;
     *
     * @param dataModels dataset
     * @return true if data contains missing values
     */
    private boolean containsMissingValues(final List<DataModel> dataModels) {
        boolean hasMissingValues = false;

        for (DataModel dataModel : dataModels) {
            if (dataModel instanceof DataSet) {
                DataSet dataSet = (DataSet) dataModel;
                if (dataSet.existsMissingValue()) {
                    hasMissingValues = true;
                    break;
                }
            }
        }

        return hasMissingValues;
    }

    /**
     * Run search algorithm.
     *
     * @param algorithm Tetrad algorithm
     * @param parameters algorithm, score, and test parameters
     * @param dataModels list of dataset to run
     * @return list of result search graphs
     */
    private List<Graph> runSearch(final Algorithm algorithm, final Parameters parameters, final List<DataModel> dataModels) {
        List<Graph> graphList = new LinkedList<>();

        if (algorithm instanceof MultiDataSetAlgorithm) {
            int numOfRuns = parameters.getInt("numRuns");
            while (numOfRuns > 0) {
                numOfRuns--;

                List<DataSet> dataSets = dataModels.stream()
                        .map(e -> (DataSet) e)
                        .collect(Collectors.toCollection(ArrayList::new));
                if (dataSets.size() < parameters.getInt("randomSelectionSize")) {
                    throw new IllegalArgumentException("Sorry, the 'random selection size' is greater than "
                            + "the number of data sets.");
                }
                Collections.shuffle(dataSets);

                List<DataModel> sub = new ArrayList<>();
                for (int j = 0; j < parameters.getInt("randomSelectionSize"); j++) {
                    sub.add(dataSets.get(j));
                }

                graphList.add(((MultiDataSetAlgorithm) algorithm).search(sub, parameters));
            }
        } else if (algorithm instanceof ClusterAlgorithm) {
            int numOfRuns = parameters.getInt("numRuns");
            while (numOfRuns > 0) {
                numOfRuns--;

                dataModels.forEach(dataModel -> {
                    if (dataModel instanceof ICovarianceMatrix) {
                        graphList.add(algorithm.search(dataModel, parameters));
                    } else if (dataModel instanceof DataSet) {
                        DataSet dataSet = (DataSet) dataModel;
                        if (dataSet.isContinuous()) {
                            graphList.add(algorithm.search(dataSet, parameters));
                        } else {
                            throw new IllegalArgumentException("Sorry, you need a continuous dataset for a cluster algorithm.");
                        }
                    }
                });
            }
        } else {
            dataModels.forEach(dataModel -> {
                graphList.add(algorithm.search(dataModel, parameters));
            });
        }

        return graphList;
    }

    /**
     * Manipulating graphs.
     *
     * @param graph graph to manipulate
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
                graph = SearchGraphUtils.cpdagFromDag(graph);
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

                DagToPag p = new DagToPag(graph);
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

    /**
     * Get algorithm instance from command-line input.
     *
     * @param cmdArgs command-line arguments
     * @return algorithm from command-line
     * @throws AlgorithmRunException whenever unable to algorithm from
     * command-line
     */
    private Algorithm getAlgorithm(CmdArgs cmdArgs) throws AlgorithmRunException {
        try {
            return AlgorithmFactory.create(cmdArgs.getAlgorithmClass(), cmdArgs.getTestClass(), cmdArgs.getScoreClass());
        } catch (IllegalAccessException | InstantiationException exception) {
            throw new AlgorithmRunException(exception);
        }
    }

    /**
     * Get list of result graphs from search algorithms.
     *
     * @return list of result graphs from search algorithms
     */
    public List<Graph> getGraphs() {
        return graphs;
    }

}
