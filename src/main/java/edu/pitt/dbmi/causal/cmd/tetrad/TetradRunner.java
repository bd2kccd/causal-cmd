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
import edu.cmu.tetrad.graph.Graph;
import edu.cmu.tetrad.util.ParamDescriptions;
import edu.pitt.dbmi.causal.cmd.AlgorithmRunException;
import edu.pitt.dbmi.causal.cmd.CmdArgs;
import edu.pitt.dbmi.causal.cmd.util.DateTime;
import edu.pitt.dbmi.causal.cmd.util.GraphIO;
import edu.pitt.dbmi.data.Delimiter;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
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

    public static void runTetrad(CmdArgs cmdArgs) {
        if (cmdArgs == null) {
            throw new IllegalArgumentException("CmdArgs cannot be null.");
        }

        Graph graph = null;
        Path fileOut = Paths.get(cmdArgs.getOutDirectory().toString(), cmdArgs.getFilePrefix() + ".txt");
        try (PrintStream out = new PrintStream(new BufferedOutputStream(Files.newOutputStream(fileOut, StandardOpenOption.CREATE)), true)) {
            writeOutParameters(cmdArgs, out);
            out.println();

            TetradAlgorithmRunner algorithmRunner = new TetradAlgorithmRunner();
            algorithmRunner.setOut(out);
            try {
                algorithmRunner.runAlgorithm(cmdArgs);
            } catch (AlgorithmRunException exception) {
                System.out.println(exception.getMessage());
                LOGGER.error("Algorithm run failed.", exception);
                System.exit(-1);
            }

            graph = algorithmRunner.getGraph();
            if (graph != null) {
                out.println();
                out.println(graph.toString());
            }
        } catch (IOException exception) {
            LOGGER.error("Unable to write to file.", exception);
        }

        if (graph != null && cmdArgs.isJsonGraph()) {
            try {
                GraphIO.writeAsJSON(graph, Paths.get(cmdArgs.getOutDirectory().toString(), cmdArgs.getFilePrefix() + "_graph.json"));
            } catch (IOException exception) {
                LOGGER.error("Unable to write json graph to file.", exception);
            }
        }
    }

    private static void writeOutParameters(CmdArgs cmdArgs, PrintStream out) {
        Class algoClass = cmdArgs.getAlgorithmClass();
        Class indTestClass = cmdArgs.getTestClass();
        Class scoreClass = cmdArgs.getScoreClass();

        String algoName = (algoClass == null) ? null : TetradAlgorithms.getInstance().getName(algoClass);
        String testName = (indTestClass == null) ? null : TetradIndependenceTests.getInstance().getName(indTestClass);
        String scoreName = (scoreClass == null) ? null : TetradScores.getInstance().getName(scoreClass);

        out.println("================================================================================");
        out.printf("%s (%s)%n", algoName, DateTime.printNow());
        out.println("================================================================================");

        out.println();
        out.println("Runtime Parameters:");
        out.printf("number of threads = %s%n", cmdArgs.getNumOfThreads());

        String files = cmdArgs.getDatasetFiles().stream()
                .map(e -> e.getFileName().toString())
                .collect(Collectors.joining(","));
        Delimiter delimiter = cmdArgs.getDelimiter();
        char quoteChar = cmdArgs.getQuoteChar();
        String missing = cmdArgs.getMissingValueMarker();
        String comment = cmdArgs.getCommentMarker();
        boolean hasHeader = cmdArgs.isHasHeader();
        out.println();
        out.println("Dataset:");
        out.printf("file = %s%n", files);
        out.printf("header = %s%n", hasHeader ? "yes" : "no");
        out.printf("delimiter = %s%n", delimiter.name().toLowerCase());
        out.printf("quote char = %s%n", (quoteChar <= 0) ? "none" : String.valueOf(quoteChar));
        out.printf("missing marker = %s%n", (missing == null || missing.isEmpty()) ? "none" : missing);
        out.printf("comment marker = %s%n", (comment == null || comment.isEmpty()) ? "none" : comment);

        out.println();
        out.println("Algorithm Run:");
        if (algoName != null) {
            out.printf("algorithm = %s%n", algoName);
        }
        if (testName != null) {
            out.printf("test of independence = %s%n", testName);
        }
        if (scoreName != null) {
            out.printf("score = %s%n", scoreName);
        }

        out.println();
        out.println("Algorithm Parameters:");
        getParameterValues(cmdArgs).forEach((k, v) -> out.printf("%s = %s%n", k, v));
    }

    private static Map<String, String> getParameterValues(CmdArgs cmdArgs) {
        Map<String, String> params = new TreeMap<>();

        Class algoClass = cmdArgs.getAlgorithmClass();
        Class indTestClass = cmdArgs.getTestClass();
        Class scoreClass = cmdArgs.getScoreClass();

        Algorithm algorithm;
        try {
            algorithm = AlgorithmFactory.create(algoClass, indTestClass, scoreClass);
        } catch (IllegalAccessException | InstantiationException exception) {
            algorithm = null;
            LOGGER.error("Unable to construct algorithm object.", exception);
        }

        if (algorithm != null) {
            ParamDescriptions paramDesc = ParamDescriptions.getInstance();
            algorithm.getParameters().forEach(e -> params.put(e, String.valueOf(paramDesc.get(e).getDefaultValue())));
        }
        cmdArgs.getParameters().forEach((k, v) -> params.put(k, v));

        return params;
    }

}
