//
//
///*
// * Copyright (C) 2023 University of Pittsburgh.
// *
// * This library is free software; you can redistribute it and/or
// * modify it under the terms of the GNU Lesser General Public
// * License as published by the Free Software Foundation; either
// * version 2.1 of the License, or (at your option) any later version.
// *
// * This library is distributed in the hope that it will be useful,
// * but WITHOUT ANY WARRANTY; without even the implied warranty of
// * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// * Lesser General Public License for more details.
// *
// * You should have received a copy of the GNU Lesser General Public
// * License along with this library; if not, write to the Free Software
// * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
// * MA 02110-1301  USA
// */
//package edu.pitt.dbmi.causal.experiment;
//
//        import edu.cmu.tetrad.algcomparison.independence.ProbabilisticTest;
//        import edu.cmu.tetrad.data.DataSet;
//        import edu.cmu.tetrad.graph.Graph;
//        import edu.cmu.tetrad.search.Rfci;
//        import edu.cmu.tetrad.search.utils.GraphSearchUtils;
//        import edu.cmu.tetrad.util.GraphSampling;
//        import edu.cmu.tetrad.util.ParamDescriptions;
//        import edu.cmu.tetrad.util.Parameters;
//        import edu.cmu.tetrad.util.Params;
//        import edu.pitt.dbmi.causal.experiment.calibration.GraphStatistics;
//        import edu.pitt.dbmi.causal.experiment.tetrad.Graphs;
//        import edu.pitt.dbmi.causal.experiment.util.DataSampling;
//        import edu.pitt.dbmi.causal.experiment.util.GraphDetails;
//        import edu.pitt.dbmi.causal.experiment.util.ResourceLoader;
//        import edu.pitt.dbmi.data.reader.Delimiter;
//        import java.io.PrintStream;
//        import java.nio.file.Path;
//        import java.nio.file.Paths;
//        import java.time.LocalDateTime;
//        import java.time.format.DateTimeFormatter;
//        import java.util.LinkedList;
//        import java.util.List;
//        import java.util.concurrent.TimeUnit;
//        import org.apache.commons.math3.random.RandomGenerator;
//
///**
// *
// * Feb 27, 2023 3:14:43 PM
// *
// * @author Kevin V. Bui (kvb2univpitt@gmail.com)
// */
//public class BRFCI {
//
//    private static final DateTimeFormatter DATETIME_FORMATTER = DateTimeFormatter.ofPattern("MMM dd, yyyy hh:mm:ss");
//
//    private static final String TITLE = "Rfci with Bootstrapping Using Probabilistic Test";
//
//    private static void run(Path dataFile, Path trueGraphFile, Path dirOut) throws Exception {
//        Graph trueGraph = ResourceLoader.loadGraph(trueGraphFile);
//        Graph pagFromDagGraph = GraphSearchUtils.dagToPag(trueGraph);
//        DataSet dataSet = (DataSet) ResourceLoader.loadDataModel(dataFile, Delimiter.TAB);
//
//        // get algorithm parameters
//        Parameters parameters = getParameters();
//
//        RandomGenerator randGen = DataSampling.createRandomGenerator(parameters);
//        List<DataSet> dataSets = DataSampling.sample(dataSet, parameters, randGen);
//
//        // start the timer
//        final LocalDateTime startDateTime = LocalDateTime.now();
//        final long startTime = System.nanoTime();
//
//        // run searches on sample data
//        int numOfSearchRuns = 0;
//        List<Graph> graphs = new LinkedList<>();
//        for (DataSet data : dataSets) {
//            Graph graph = runSearch(data, parameters);
//            if (GraphSearchUtils.isLegalPag(graph).isLegalPag()) {
//                graphs.add(graph);
//            }
//            numOfSearchRuns++;
//        }
//
//        // continue to run searches until the number of desire graphs has reached
//        int numOfAdditionalDataSampling = 0;
//        while (graphs.size() < dataSets.size()) {
//            numOfAdditionalDataSampling++;
//            numOfSearchRuns++;
//
//            DataSet sampleData = DataSampling.sampleWithReplacement(dataSet, randGen);
//            Graph graph = runSearch(sampleData, parameters);
//            if (GraphSearchUtils.isLegalPag(graph).isLegalPag()) {
//                graphs.add(graph);
//            }
//        }
//
//        // stop the timer
//        final long endTime = System.nanoTime();
//        final LocalDateTime endDateTime = LocalDateTime.now();
//        final long duration = endTime - startTime;
//
//        Graph searchGraph = GraphSampling.createGraphWithHighProbabilityEdges(graphs);
//
//        String outputDir = dirOut.toString();
//        GraphStatistics graphCalibration = new GraphStatistics(searchGraph, pagFromDagGraph);
//        graphCalibration.saveGraphData(Paths.get(outputDir, "edge_data.csv"));
//        graphCalibration.saveStatistics(Paths.get(outputDir, "statistics.txt"));
//        graphCalibration.saveCalibrationPlot(
//                "RFCI-Probabilistic Bootstrapping", "rfci-bootstrapping",
//                1000, 1000,
//                Paths.get(outputDir, "calibration.png"));
//
//        GraphDetails.saveDetails(pagFromDagGraph, searchGraph, Paths.get(outputDir, "graph_details.txt"));
//        Graphs.saveGraph(searchGraph, Paths.get(outputDir, "graph.txt"));
//        Graphs.exportAsPngImage(searchGraph, 1000, 1000, Paths.get(outputDir, "graph.png"));
//
//        // write out details
//        try (PrintStream writer = new PrintStream(Paths.get(outputDir, "run_details.txt").toFile())) {
//            writer.println(TITLE);
//            writer.println("================================================================================");
//            writer.println("Algorithm: RFCI");
//            writer.println("Test of Independence: Probabilistic Test");
//            writer.println();
//
//            writer.println("Parameters");
//            writer.println("========================================");
//            printParameters(parameters, writer);
//            writer.println();
//
//            writer.println("Dataset");
//            writer.println("========================================");
//            writer.printf("Variables: %d%n", dataSet.getNumColumns());
//            writer.printf("Cases: %d%n", dataSet.getNumRows());
//            writer.printf("Data Samples: %d%n", dataSets.size());
//            writer.println();
//
//            writer.println("Search Run Details");
//            writer.println("========================================");
//            writer.println("Run Time");
//            writer.println("--------------------");
//            writer.printf("Search start: %s%n", startDateTime.format(DATETIME_FORMATTER));
//            writer.printf("Search end: %s%n", endDateTime.format(DATETIME_FORMATTER));
//            writer.printf("Duration: %,d seconds%n", TimeUnit.NANOSECONDS.toSeconds(duration));
//            writer.println();
//            writer.println("Search Counts");
//            writer.println("--------------------");
//            writer.printf("Number of searches: %d%n", numOfSearchRuns);
//            writer.println();
//            writer.println("Data Sampling");
//            writer.println("--------------------");
//            writer.printf("Number of initial data sampling: %d%n", dataSets.size());
//            writer.printf("Number of additional data sampling: %d%n", numOfAdditionalDataSampling);
//            writer.println();
//            writer.println("PAG Counts");
//            writer.println("--------------------");
//            writer.printf("Number of valid PAGs: %d%n", graphs.size());
//            writer.printf("Number of invalid PAGs: %d%n", numOfSearchRuns - graphs.size());
//            writer.println();
//
//            writer.println("High-Edge-Probability Graph");
//            writer.println("========================================");
//            writer.println(searchGraph.toString().replaceAll(" - ", " ... ").trim());
//        }
//    }
//
//    private static Graph runSearch(DataSet dataSet, Parameters parameters) {
//        Rfci rfci = new Rfci((new ProbabilisticTest()).getTest(dataSet, parameters));
//        rfci.setDepth(parameters.getInt(Params.DEPTH));
//        rfci.setMaxPathLength(parameters.getInt(Params.MAX_PATH_LENGTH));
//        rfci.setVerbose(parameters.getBoolean(Params.VERBOSE));
//
//        return rfci.search();
//    }
//
//    private static void printParameters(Parameters parameters, PrintStream writer) {
//        ParamDescriptions paramDescs = ParamDescriptions.getInstance();
//
//        writer.println("RFCI");
//        writer.println("--------------------");
//        writer.printf("%s: %s%n",
//                paramDescs.get(Params.MAX_PATH_LENGTH).getShortDescription(),
//                getParameterValue(parameters, Params.MAX_PATH_LENGTH));
//        writer.printf("%s: %s%n",
//                paramDescs.get(Params.DEPTH).getShortDescription(),
//                getParameterValue(parameters, Params.DEPTH));
//        writer.printf("%s: %s%n",
//                paramDescs.get(Params.VERBOSE).getShortDescription(),
//                getParameterValue(parameters, Params.VERBOSE));
//        writer.println();
//
//        writer.println("Probabilistic Test");
//        writer.println("--------------------");
//        writer.printf("%s: %s%n",
//                paramDescs.get(Params.CUTOFF_IND_TEST).getShortDescription(),
//                getParameterValue(parameters, Params.CUTOFF_IND_TEST));
//        writer.printf("%s: %s%n",
//                paramDescs.get(Params.PRIOR_EQUIVALENT_SAMPLE_SIZE).getShortDescription(),
//                getParameterValue(parameters, Params.PRIOR_EQUIVALENT_SAMPLE_SIZE));
//        writer.printf("%s: %s%n",
//                paramDescs.get(Params.NO_RANDOMLY_DETERMINED_INDEPENDENCE).getShortDescription(),
//                getParameterValue(parameters, Params.NO_RANDOMLY_DETERMINED_INDEPENDENCE));
//        writer.println();
//
//        writer.println("Bootstrapping");
//        writer.println("--------------------");
//        writer.printf("%s: %s%n",
//                paramDescs.get(Params.SEED).getShortDescription(),
//                getParameterValue(parameters, Params.SEED));
//        writer.printf("%s: %s%n",
//                paramDescs.get(Params.NUMBER_RESAMPLING).getShortDescription(),
//                getParameterValue(parameters, Params.NUMBER_RESAMPLING));
//        writer.printf("%s: %s%n",
//                paramDescs.get(Params.ADD_ORIGINAL_DATASET).getShortDescription(),
//                getParameterValue(parameters, Params.ADD_ORIGINAL_DATASET));
//        writer.printf("%s: %s%n",
//                paramDescs.get(Params.RESAMPLING_WITH_REPLACEMENT).getShortDescription(),
//                getParameterValue(parameters, Params.RESAMPLING_WITH_REPLACEMENT));
//    }
//
//    private static String getParameterValue(Parameters parameters, String name) {
//        String paramValue = String.valueOf(parameters.get(name));
//        if (paramValue.equals("true")) {
//            paramValue = "Yes";
//        } else if (paramValue.equals("false")) {
//            paramValue = "No";
//        }
//
//        return paramValue;
//    }
//
//    private static Parameters getParameters() {
//        Parameters parameters = new Parameters();
//
//        // rfci
//        int maxPathLength = -1;
//        int depth = -1;
//        boolean verbose = false;
//        parameters.set(Params.MAX_PATH_LENGTH, maxPathLength);
//        parameters.set(Params.DEPTH, depth);
//        parameters.set(Params.VERBOSE, verbose);
//
//        // probabilistic test of independence
//        double cutoffIndTest = 0.5;
//        double priorEquivalentSampleSize = 10;
//        boolean noRandomlyDeterminedIndependence = true;
//        parameters.set(Params.CUTOFF_IND_TEST, cutoffIndTest);
//        parameters.set(Params.PRIOR_EQUIVALENT_SAMPLE_SIZE, priorEquivalentSampleSize);
//        parameters.set(Params.NO_RANDOMLY_DETERMINED_INDEPENDENCE, noRandomlyDeterminedIndependence);
//
//        // bootstrapping
//        long seed = 1673588774198L;
//        int numberOfResampling = 99;
//        boolean addOriginalDataset = true;
//        boolean resamplingWithReplacement = true;
//        parameters.set(Params.SEED, seed);
//        parameters.set(Params.NUMBER_RESAMPLING, numberOfResampling);
//        parameters.set(Params.ADD_ORIGINAL_DATASET, addOriginalDataset);
//        parameters.set(Params.RESAMPLING_WITH_REPLACEMENT, resamplingWithReplacement);
//
//        return parameters;
//    }
//
//    /**
//     * @param args the command line arguments
//     */
//    public static void main(String[] args) {
//        System.out.println("================================================================================");
//        System.out.println(TITLE);
//        System.out.println("================================================================================");
//        Path datasetFile = Paths.get(args[0]);
//        Path trueGraph = Paths.get(args[1]);
//        Path dirOut = Paths.get(args[2]);
//        try {
//            run(datasetFile, trueGraph, dirOut);
//        } catch (Exception exception) {
//            exception.printStackTrace(System.err);
//        }
//        System.out.println("================================================================================");
//    }
//
//}
