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
package edu.pitt.dbmi.causal.cmd;

import edu.cmu.tetrad.data.DataType;
import edu.cmu.tetrad.graph.Graph;
import edu.pitt.dbmi.causal.cmd.tetrad.TetradAlgorithmRunner;
import edu.pitt.dbmi.causal.cmd.tetrad.TetradAlgorithms;
import edu.pitt.dbmi.causal.cmd.tetrad.TetradIndependenceTests;
import edu.pitt.dbmi.causal.cmd.tetrad.TetradScores;
import edu.pitt.dbmi.causal.cmd.util.Application;
import edu.pitt.dbmi.causal.cmd.util.Args;
import edu.pitt.dbmi.causal.cmd.util.DateTime;
import edu.pitt.dbmi.causal.cmd.util.GraphIO;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Mar 8, 2017 6:11:17 PM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public class CausalCmdApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(CausalCmdApplication.class);

    public static final String FOOTER = "Additional parameters are available when using --algorithm <arg>.";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        args = Args.clean(args);
        if (Args.isEmpty(args)) {
            Application.showHelp(CmdOptions.getInstance().getMainOptions(), FOOTER);
            System.exit(-1);
        } else if (Args.hasLongParam(args, CmdParams.HELP)) {
            if (args.length == 1) {
                Application.showHelp(CmdOptions.getInstance().getOptions(), FOOTER);
            } else {
                Options options = null;
                try {
                    options = CmdParser.getAlgorithmHelpOptions(args);
                } catch (CmdParserException exception) {
                    System.err.println(exception.getCause().getMessage());
                    Application.showHelp(exception.getOptions(), FOOTER);
                }
                if (options != null) {
                    Application.showHelp(options, FOOTER);
                }
            }
        } else if (Args.hasLongParam(args, CmdParams.VERSION)) {
            System.out.println(Application.getVersion());
        } else {
            CmdArgs cmdArgs = null;
            try {
                cmdArgs = CmdParser.parse(args);
            } catch (CmdParserException exception) {
                System.err.println(exception.getCause().getMessage());
                Application.showHelp(exception.getOptions(), FOOTER);
            }
            if (cmdArgs == null) {
                System.exit(-1);
            }

            TetradAlgorithmRunner algorithmRunner = new TetradAlgorithmRunner();
            try (PrintStream out = new PrintStream(Files.newOutputStream(getOutputFile(cmdArgs)), true)) {
                algorithmRunner.setOut(out);
                writeInputInfo(cmdArgs, out);
                try {
                    algorithmRunner.runAlgorithm(cmdArgs);
                } catch (AlgorithmRunException exception) {
                    out.println(exception.getMessage());
                    LOGGER.error("Algorithm run failed.", exception);
                    System.exit(-1);
                }
            } catch (IOException exception) {
                LOGGER.error("Algorithm run failed.", exception);
            }

            Graph graph = algorithmRunner.getGraph();
            try {
                if (cmdArgs.json) {
                    GraphIO.writeAsJSON(graph, Paths.get(cmdArgs.getOutDirectory().toString(), cmdArgs.fileName + "_graph.json"));
                } else {
                    GraphIO.writeAsTXT(graph, Paths.get(cmdArgs.getOutDirectory().toString(), cmdArgs.fileName + "_graph.txt"));
                }
            } catch (IOException exception) {
                LOGGER.error("Unable to write out graph.", exception);
                System.exit(-1);
            }
        }
    }

    private static Path getOutputFile(CmdArgs cmdArgs) {
        return Paths.get(cmdArgs.getOutDirectory().toString(), cmdArgs.fileName + ".txt");
    }

    private static void writeInputInfo(CmdArgs cmdArgs, PrintStream out) throws IOException {
        out.printf("%s (%s)%n", TetradAlgorithms.getInstance().getName(cmdArgs.algorithmClass), DateTime.printNow());
        out.println("================================================================================");
        out.println(TetradAlgorithms.getInstance().getDescription(cmdArgs.algorithmClass));

        out.println();
        out.println("Options:");
        out.println("--------------------------------------------------------------------------------");
        out.printf("Algorithm: %s%n", TetradAlgorithms.getInstance().getName(cmdArgs.algorithmClass));
        out.printf("Test: %s%n", TetradIndependenceTests.getInstance().getName(cmdArgs.testClass));
        out.printf("Score: %s%n", TetradScores.getInstance().getName(cmdArgs.scoreClass));
        cmdArgs.datasetFiles.forEach(s -> out.printf("Dataset: %s%n", s.toAbsolutePath().toAbsolutePath()));
        if (cmdArgs.knowledgeFile != null) {
            out.printf("Knowledge: %s%n", cmdArgs.knowledgeFile.toAbsolutePath().toString());
        }
        if (cmdArgs.excludeVariableFile != null) {
            out.printf("Knowledge: %s%n", cmdArgs.excludeVariableFile.toAbsolutePath().toString());
        }
        out.printf("Data Type: %s%n", cmdArgs.dataType.name().toLowerCase());
        out.printf("Delimiter: %s%n", cmdArgs.delimiter.name().toLowerCase());
        if (cmdArgs.dataType == DataType.Mixed) {
            out.printf("Number of Categories: %s%n", cmdArgs.numCategories);
        }
        if (cmdArgs.quoteChar > 0) {
            out.printf("Quote Character: %s%n", cmdArgs.quoteChar);
        }
        if (cmdArgs.missingValueMarker != null) {
            out.printf("Missing Value Marker: %s%n", cmdArgs.missingValueMarker);
        }
        if (cmdArgs.commentMarker != null) {
            out.printf("Comment Marker: %s%n", cmdArgs.commentMarker);
        }
        out.printf("Skip Validation: %s%n", cmdArgs.skipValidation);

        if (!cmdArgs.parameters.isEmpty()) {
            out.println();
            out.println("Algorithm Parameters:");
            out.println("--------------------------------------------------------------------------------");
            cmdArgs.parameters.forEach((k, v) -> out.printf("%s: %s%n", k, (v == null) ? "true" : v));
        }
        out.println();
    }

}
