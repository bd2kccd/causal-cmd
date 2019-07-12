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
package edu.pitt.dbmi.causal.cmd;

import edu.cmu.tetrad.graph.Graph;
import edu.cmu.tetrad.latest.LatestClient;
import edu.pitt.dbmi.causal.cmd.data.DataValidations;
import edu.pitt.dbmi.causal.cmd.tetrad.Tetrad;
import edu.pitt.dbmi.causal.cmd.tetrad.TetradAlgorithms;
import edu.pitt.dbmi.causal.cmd.tetrad.TetradIndependenceTests;
import edu.pitt.dbmi.causal.cmd.tetrad.TetradRunner;
import edu.pitt.dbmi.causal.cmd.tetrad.TetradScores;
import edu.pitt.dbmi.causal.cmd.util.DateTime;
import edu.pitt.dbmi.causal.cmd.util.GraphIO;
import edu.pitt.dbmi.causal.cmd.util.WordUtil;
import edu.pitt.dbmi.data.reader.Delimiter;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Arrays;
import java.util.stream.Collectors;
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

    public static final String FOOTER = "Use --help for guidance list of options.  Use --help-all to show all options.";

    public static boolean showExperimental;

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        args = Args.clean(args);

        // clear
        TetradAlgorithms.clear();
        TetradIndependenceTests.clear();
        TetradScores.clear();
        CmdOptions.clear();

        showExperimental = Arrays.stream(args).anyMatch("--experimental"::equals);

        if (Args.hasLongParam(args, CmdParams.HELP)) {
            try {
                Applications.showHelp(args, CmdParser.getHelpOptions(args), null);
            } catch (CmdParserException exception) {
                System.err.println(exception.getCause().getMessage());
                Applications.showHelp(args, exception.getParseOptions(), null);
            }
        } else if (Args.hasLongParam(args, CmdParams.HELP_ALL)) {
            Applications.showHelp(CmdOptions.getInstance().getOptions(), null);
        } else if (Args.hasLongParam(args, CmdParams.VERSION)) {
            System.out.println(Applications.getVersion());
        } else {
            CmdArgs cmdArgs = null;
            try {
                cmdArgs = CmdParser.parse(args);
            } catch (CmdParserException exception) {
                System.err.println(exception.getCause().getMessage());
                Applications.showHelp(args, exception.getParseOptions(), FOOTER);
            }

            if (cmdArgs == null) {
                System.exit(-1);
            }

            if (!cmdArgs.isSkipLatest()) {
                try {
                    LatestClient latestClient = LatestClient.getInstance();
                    String version = Applications.jarVersion();
                    if (version == null) {
                        version = "DEVELOPMENT";
                    }
                    latestClient.checkLatest("causal-cmd", version);
                    System.out.println(latestClient.getLatestResult());
                } catch (Exception exception) {
                    LOGGER.error("Unable to check for the latest version.", exception);
                }
            }

            try {
                runTetrad(cmdArgs);
            } catch (IOException | ValidationException | AlgorithmRunException exception) {
                LOGGER.error("", exception);
                exception.printStackTrace(System.err);
                System.exit(-1);
            }
        }
    }

    private static void runTetrad(CmdArgs cmdArgs) throws AlgorithmRunException, ValidationException, IOException {
        String outDir = cmdArgs.getOutDirectory().toString();
        String prefix = cmdArgs.getFilePrefix();
        Path outTxtFile = Paths.get(outDir, String.format("%s.txt", prefix));

        // remove previous files
        if (Files.exists(outTxtFile)) {
            Files.deleteIfExists(outTxtFile);
        }

        try (PrintStream out = new PrintStream(new BufferedOutputStream(Files.newOutputStream(outTxtFile, StandardOpenOption.CREATE)), true)) {
            writeOutParameters(cmdArgs, out);

            if (!cmdArgs.isSkipValidation()) {
                DataValidations.validate(cmdArgs, out);
                out.println();
            }

            TetradRunner tetradRunner = new TetradRunner(cmdArgs);
            tetradRunner.runAlgorithm(out);
            out.println();
            out.println("================================================================================");

            Graph[] graphs = tetradRunner.getGraphs().stream()
                    .toArray(Graph[]::new);
            for (int i = 0; i < graphs.length; i++) {
                if (i > 0) {
                    out.println("--------------------------------------------------------------------------------");
                }

                out.println(graphs[i].toString().trim());

                if (cmdArgs.isJsonGraph()) {
                    String fileName = (i > 0)
                            ? String.format("%s_graph_%d.json", prefix, i)
                            : String.format("%s_graph.json", prefix);

                    Path outGraphFile = Paths.get(outDir, fileName);
                    if (Files.exists(outGraphFile)) {
                        Files.deleteIfExists(outTxtFile);
                    }

                    GraphIO.writeAsJSON(graphs[i], outGraphFile);
                }
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
        out.println("Runtime Parameters");
        out.println("--------------------------------------------------------------------------------");
        out.printf("number of threads: %s%n", cmdArgs.getNumOfThreads());

        String files = cmdArgs.getDatasetFiles().stream()
                .map(e -> e.getFileName().toString())
                .collect(Collectors.joining(","));
        Delimiter delimiter = cmdArgs.getDelimiter();
        char quoteChar = cmdArgs.getQuoteChar();
        String missing = cmdArgs.getMissingValueMarker();
        String comment = cmdArgs.getCommentMarker();
        boolean hasHeader = cmdArgs.isHasHeader();
        out.println();
        out.println("Dataset");
        out.println("--------------------------------------------------------------------------------");
        out.printf("file: %s%n", files);
        out.printf("header: %s%n", hasHeader ? "yes" : "no");
        out.printf("delimiter: %s%n", delimiter.name().toLowerCase());
        out.printf("quote char: %s%n", (quoteChar <= 0) ? "none" : String.valueOf(quoteChar));
        out.printf("missing marker: %s%n", (missing == null || missing.isEmpty()) ? "none" : missing);
        out.printf("comment marker: %s%n", (comment == null || comment.isEmpty()) ? "none" : comment);

        out.println();
        out.println("Algorithm Run");
        out.println("--------------------------------------------------------------------------------");
        if (algoName != null) {
            out.printf("algorithm: %s%n", algoName);
        }
        if (testName != null) {
            out.printf("test of independence: %s%n", testName);
        }
        if (scoreName != null) {
            out.printf("score: %s%n", scoreName);
        }

        out.println();
        out.println("Algorithm Parameters");
        out.println("--------------------------------------------------------------------------------");
        Tetrad.getParameterValues(cmdArgs)
                .forEach((k, v) -> out.printf("%s: %s%n", k, WordUtil.toYesOrNo(v)));
        out.println();
        out.println();
    }

}
