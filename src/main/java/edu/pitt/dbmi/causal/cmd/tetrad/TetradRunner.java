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

import edu.cmu.tetrad.data.DataType;
import edu.cmu.tetrad.graph.Graph;
import edu.pitt.dbmi.causal.cmd.AlgorithmRunException;
import edu.pitt.dbmi.causal.cmd.CmdArgs;
import edu.pitt.dbmi.causal.cmd.util.DateTime;
import edu.pitt.dbmi.causal.cmd.util.GraphIO;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
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

        Path fileOut = Paths.get(cmdArgs.getOutDirectory().toString(), cmdArgs.getFilePrefix() + ".txt");
        TetradAlgorithmRunner algorithmRunner = new TetradAlgorithmRunner();
        try (PrintStream out = new PrintStream(Files.newOutputStream(fileOut), true)) {
            printRunInfo(cmdArgs, out);

            algorithmRunner.setOut(out);
            try {
                algorithmRunner.runAlgorithm(cmdArgs);
            } catch (AlgorithmRunException exception) {
                System.out.println(exception.getMessage());
                LOGGER.error("Algorithm run failed.", exception);
                System.exit(-1);
            }
        } catch (IOException exception) {
            LOGGER.error("Algorithm run failed.", exception);
        }

        Graph graph = algorithmRunner.getGraph();
        if (graph != null) {
            try {
                if (cmdArgs.isJson()) {
                    GraphIO.writeAsJSON(graph, Paths.get(cmdArgs.getOutDirectory().toString(), cmdArgs.getFilePrefix() + "_graph.json"));
                } else {
                    GraphIO.writeAsTXT(graph, Paths.get(cmdArgs.getOutDirectory().toString(), cmdArgs.getFilePrefix() + "_graph.txt"));
                }
            } catch (IOException exception) {
                LOGGER.error("Unable to write out graph.", exception);
                System.exit(-1);
            }
        }
    }

    private static void printRunInfo(CmdArgs cmdArgs, PrintStream out) {
        Class algoClass = cmdArgs.getAlgorithmClass();
        Class testClass = cmdArgs.getTestClass();
        Class scoreClass = cmdArgs.getScoreClass();
        List<Path> dataset = cmdArgs.getDatasetFiles();
        Map<String, String> parameters = cmdArgs.getParameters();

        out.printf("Tetrad: %s (%s)%n", TetradAlgorithms.getInstance().getName(algoClass), DateTime.printNow());
        out.println("================================================================================");
        out.println(TetradAlgorithms.getInstance().getDescription(algoClass));
        out.println("--------------------------------------------------------------------------------");
        out.printf("Algorithm: %s%n", TetradAlgorithms.getInstance().getName(algoClass));
        if (testClass != null) {
            out.printf("Test: %s%n", TetradIndependenceTests.getInstance().getName(testClass));
        }
        if (scoreClass != null) {
            out.printf("Score: %s%n", TetradScores.getInstance().getName(scoreClass));
        }
        out.println();

        out.println("Tetrad Parameters");
        out.println("================================================================================");
        parameters.forEach((k, v) -> out.printf("%s: %s%n", k, (v == null) ? "true" : v));
        out.println();

        out.println("Dataset");
        out.println("================================================================================");
        if (dataset.size() > 1) {
            StringBuilder sb = new StringBuilder();
            dataset.forEach(e -> sb.append(String.format("       %s%n", e.toAbsolutePath())));
            out.printf("Files: %s%n", sb.toString().trim());
        } else {
            out.printf("File: %s%n", dataset.get(0).toAbsolutePath());
        }
        out.printf("Has Header: %s%n", cmdArgs.isHasHeader() ? "yes" : "no");
        if (cmdArgs.getQuoteChar() > 0) {
            out.printf("Quote Character: %s%n", cmdArgs.getQuoteChar());
        }
        if (cmdArgs.getMissingValueMarker() != null) {
            out.printf("Missing Value Marker: %s%n", cmdArgs.getMissingValueMarker());
        }
        if (cmdArgs.getCommentMarker() != null) {
            out.printf("Comment Marker: %s%n", cmdArgs.getCommentMarker());
        }
        out.printf("Delimiter: %s%n", cmdArgs.getDelimiter().name().toLowerCase());
        out.printf("Data Type: %s%n", cmdArgs.getDataType().name().toLowerCase());
        if (cmdArgs.getDataType() == DataType.Mixed) {
            out.printf("Number of Categories: %s%n", cmdArgs.getNumCategories());
        }
        out.println();

        Path knowledgeFile = cmdArgs.getKnowledgeFile();
        Path excludeVariableFile = cmdArgs.getExcludeVariableFile();
        if (!(knowledgeFile == null && excludeVariableFile == null)) {
            out.println("Other Input Files");
            out.println("================================================================================");
            if (knowledgeFile != null) {
                out.printf("Knowledge: %s%n", knowledgeFile.toAbsolutePath());
            }
            if (excludeVariableFile != null) {
                out.printf("Exclude Variables: %s%n", excludeVariableFile.toAbsolutePath());
            }
        }

        out.println();
        out.println("Miscellaneous");
        out.println("================================================================================");
        out.printf("Skip Validation: %s%n", cmdArgs.isSkipValidation() ? "yes" : "no");
        out.printf("JSON Output: %s%n", cmdArgs.isJson() ? "yes" : "no");
        out.printf("Output Directory: %s%n", cmdArgs.getOutDirectory());
        out.println();
    }

}
