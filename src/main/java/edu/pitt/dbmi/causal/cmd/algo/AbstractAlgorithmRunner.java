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
package edu.pitt.dbmi.causal.cmd.algo;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.cmu.tetrad.algcomparison.algorithm.Algorithm;
import edu.cmu.tetrad.data.DataModel;
import edu.cmu.tetrad.data.DataSet;
import edu.cmu.tetrad.data.IKnowledge;
import edu.cmu.tetrad.graph.Graph;
import edu.cmu.tetrad.latest.LatestClient;
import edu.cmu.tetrad.util.Parameters;
import edu.pitt.dbmi.causal.cmd.ParamAttrs;
import edu.pitt.dbmi.causal.cmd.opt.algo.TetradCmdAlgoOpt;
import edu.pitt.dbmi.causal.cmd.util.AppUtils;
import edu.pitt.dbmi.causal.cmd.util.Args;
import edu.pitt.dbmi.causal.cmd.util.DateTime;
import edu.pitt.dbmi.causal.cmd.util.FileIO;
import edu.pitt.dbmi.causal.cmd.util.JsonSerializer;
import edu.pitt.dbmi.causal.cmd.util.TetradDataUtils;
import edu.pitt.dbmi.causal.cmd.validation.TetradDataValidation;
import edu.pitt.dbmi.data.Delimiter;
import edu.pitt.dbmi.data.reader.DataReader;
import static edu.pitt.dbmi.data.validation.ValidationCode.INFO;
import static edu.pitt.dbmi.data.validation.ValidationCode.WARNING;
import edu.pitt.dbmi.data.validation.ValidationResult;
import edu.pitt.dbmi.data.validation.tabular.ContinuousTabularDataFileValidation;
import edu.pitt.dbmi.data.validation.tabular.TabularDataValidation;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Formatter;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import org.apache.commons.cli.Options;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Mar 13, 2017 4:29:09 PM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public abstract class AbstractAlgorithmRunner implements AlgorithmRunner {

    private static final Logger LOGGER = LoggerFactory.getLogger(AbstractAlgorithmRunner.class);

    public AbstractAlgorithmRunner() {
    }

    protected abstract Parameters getParameters(TetradCmdAlgoOpt cmdAlgoOpt);

    protected abstract Algorithm getAlgorithm(IKnowledge knowledge);

    protected abstract List<TetradDataValidation> getDataValidations(DataSet dataSet, TetradCmdAlgoOpt cmdAlgoOpt);

    protected abstract DataReader getDataReader(TetradCmdAlgoOpt cmdAlgoOpt);

    protected abstract void printParameterInfos(Formatter fmt, TetradCmdAlgoOpt cmdAlgoOpt);

    protected abstract void printValidationInfos(Formatter fmt, TetradCmdAlgoOpt cmdAlgoOpt);

    protected abstract AlgorithmType getAlgorithmType();

    protected abstract TetradCmdAlgoOpt getCommandLineOptions();

    @Override
    public void runAlgorithm(String[] args) {
        AlgorithmType algorithmType = getAlgorithmType();
        TetradCmdAlgoOpt cmdAlgoOpt = getCommandLineOptions();
        if (needsToShowHelp(args)) {
            showHelp(algorithmType.getCmd(), cmdAlgoOpt.getMainOptions());
            return;
        }

        cmdAlgoOpt.parseOptions(args);

        String heading = creteHeading(algorithmType);
        String argInfo = createArgsInfo(cmdAlgoOpt);
        System.out.printf(heading);
        System.out.println(argInfo);
        LOGGER.info(String.format("=== Starting %s: %s", algorithmType.getTitle(), Args.toString(args, ' ')));
        LOGGER.info(argInfo.trim().replaceAll("\n", ",").replaceAll(" = ", "="));

        if (!cmdAlgoOpt.isSkipLatest()) {
            LatestClient latestClient = LatestClient.getInstance();
            String version = AppUtils.jarVersion();
            if (version == null) {
                version = "DEVELOPMENT";
            }
            latestClient.checkLatest("causal-cmd", version);
            System.out.println(latestClient.getLatestResult());
        }

        Set<String> excludedVariables = readInVariables(cmdAlgoOpt.getExcludedVariableFile());
        doContinuousTabularDataFileValidation(cmdAlgoOpt, excludedVariables);

        DataSet dataSet = readInDataSet(getDataReader(cmdAlgoOpt), excludedVariables);
        doDataValidation(dataSet, cmdAlgoOpt);

        IKnowledge knowledge = readInPriorKnowledge(cmdAlgoOpt);

        Path dirOut = cmdAlgoOpt.getDirOut();
        String outputPrefix = cmdAlgoOpt.getOutputPrefix();
        Path outputFile = Paths.get(dirOut.toString(), outputPrefix + ".txt");
        boolean verbose = cmdAlgoOpt.isVerbose();
        try (PrintStream writer = new PrintStream(new BufferedOutputStream(Files.newOutputStream(outputFile, StandardOpenOption.CREATE)))) {
            writer.println(heading);
            writer.println(createRunInfo(dataSet, excludedVariables, cmdAlgoOpt));

            Algorithm algorithm = getAlgorithm(knowledge);
            Parameters parameters = getParameters(cmdAlgoOpt);
            if (verbose) {
                parameters.set(ParamAttrs.PRINT_STREAM, writer);
            }

            Graph graph = search(dataSet, algorithm, parameters);
            writer.println();
            writer.println(graph.toString());

            if (cmdAlgoOpt.isIsSerializeJson()) {
                writeOutJson(outputPrefix, graph, Paths.get(dirOut.toString(), outputPrefix + "_graph.json"));
            }

            if (cmdAlgoOpt.isTetradGraphJson()) {
                writeOutTetradGraphJson(graph, Paths.get(dirOut.toString(), outputPrefix + ".json"));
            }
        } catch (Exception exception) {
            LOGGER.error("Run algorithm failed.", exception);
            System.exit(-128);
        }
    }

    public static void writeOutTetradGraphJson(Graph graph, Path outputFile) {
        if (graph == null) {
            return;
        }

        try (PrintStream graphWriter = new PrintStream(
                new BufferedOutputStream(Files.newOutputStream(outputFile, StandardOpenOption.CREATE)))) {
            String fileName = outputFile.getFileName().toString();

            String msg = String.format("Writing out Tetrad Graph Json file '%s'.", fileName);
            System.out.printf("%s: %s%n", DateTime.printNow(), msg);
            LOGGER.info(msg);

            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            graphWriter.print(gson.toJson(graph));

            msg = String.format("Finished writing out Tetrad Graph Json file '%s'.", fileName);
            System.out.printf("%s: %s%n", DateTime.printNow(), msg);
            LOGGER.info(msg);

        } catch (Throwable throwable) {
            String errMsg = String.format("Failed when writing out Tetrad Graph Json file '%s'.",
                    outputFile.getFileName().toString());
            System.err.println(errMsg);
            LOGGER.error(errMsg, throwable);
        }
    }

    public static void writeOutJson(String graphId, Graph graph, Path outputFile) {
        String fileName = outputFile.getFileName().toString();
        String task = "writing out Json file " + fileName;
        logStartTask(task);
        try (PrintStream graphWriter = new PrintStream(new BufferedOutputStream(Files.newOutputStream(outputFile, StandardOpenOption.CREATE)))) {
            JsonSerializer.writeToStream(JsonSerializer.serialize(graph, graphId), graphWriter);
        } catch (Exception exception) {
            logFailedTask(task, exception);
        }
        logEndTask(task);
    }

    public static Graph search(DataSet dataSet, Algorithm algorithm, Parameters parameters) {
        String task = "running algorithm " + algorithm.getDescription();
        logStartTask(task);
        Graph graph = algorithm.search(dataSet, parameters);
        logEndTask(task);

        return graph;
    }

    private String createRunInfo(DataSet dataSet, Set<String> excludedVariables, TetradCmdAlgoOpt cmdAlgoOpt) {
        Formatter fmt = new Formatter();
        fmt.format("Runtime Parameters:%n");
        fmt.format("verbose = %s%n", cmdAlgoOpt.isVerbose());
        fmt.format("number of threads = %s%n", cmdAlgoOpt.getNumOfThreads());
        fmt.format("%n");

        fmt.format("Dataset:%n");
        fmt.format("file = %s%n", cmdAlgoOpt.getDataFile().getFileName());
        fmt.format("delimiter = %s%n", cmdAlgoOpt.getDelimiter().getName());
        fmt.format("cases read in = %s%n", dataSet.getNumRows());
        fmt.format("variables read in = %s%n", dataSet.getNumColumns());
        fmt.format("%n");

        Path excludedVariableFile = cmdAlgoOpt.getExcludedVariableFile();
        Path knowledgeFile = cmdAlgoOpt.getKnowledgeFile();
        if (excludedVariableFile != null || knowledgeFile != null) {
            fmt.format("Filters:%n");
            if (excludedVariableFile != null) {
                fmt.format("excluded variables (%d variables) = %s%n", excludedVariables.size(), excludedVariableFile.getFileName());
            }
            if (knowledgeFile != null) {
                fmt.format("knowledge = %s%n", knowledgeFile.getFileName());
            }
            fmt.format("%n");
        }

        fmt.format("Algorithm Parameters:%n");
        printParameterInfos(fmt, cmdAlgoOpt);
        fmt.format("%n");

        fmt.format("Data Validations:%n");
        printValidationInfos(fmt, cmdAlgoOpt);

        return fmt.toString();
    }

    protected void doDataValidation(DataSet dataSet, TetradCmdAlgoOpt cmdAlgoOpt) {
        boolean isValid = true;
        boolean verbose = cmdAlgoOpt.isVerbose();
        List<TetradDataValidation> validations = getDataValidations(dataSet, cmdAlgoOpt);
        for (TetradDataValidation validation : validations) {
            isValid = validation.validate(System.err, verbose) && isValid;
        }
    }

    protected DataSet readInDataSet(DataReader dataReader, Set<String> excludedVariables) {
        DataSet dataSet = null;

        String task = "reading in data file";
        logStartTask(task);
        try {
            DataModel dataModel = TetradDataUtils.toDataModel(dataReader.readInData());
            if (dataModel instanceof DataSet) {
                dataSet = (DataSet) dataModel;
            }
        } catch (IOException exception) {
            logFailedTask(task, exception);
        }
        logEndTask(task);

        return dataSet;
    }

    protected IKnowledge readInPriorKnowledge(TetradCmdAlgoOpt cmdAlgoOpt) {
        IKnowledge knowledge = null;

        Path knowledgeFile = cmdAlgoOpt.getKnowledgeFile();
        if (knowledgeFile != null) {
            String task = "reading in prior knowledge file " + knowledgeFile.getFileName();
            logStartTask(task);
            try {
                edu.cmu.tetrad.data.DataReader reader = new edu.cmu.tetrad.data.DataReader();
                knowledge = reader.parseKnowledge(knowledgeFile.toFile());
            } catch (IOException exception) {
                logFailedTask(task, exception);
                System.exit(-127);
            }
            logEndTask(task);
        }

        return knowledge;
    }

    protected void doContinuousTabularDataFileValidation(TetradCmdAlgoOpt cmdAlgoOpt, Set<String> excludedVariables) {
        File dataFile = cmdAlgoOpt.getDataFile().toFile();
        Delimiter delimiter = cmdAlgoOpt.getDelimiter();

        TabularDataValidation dataValidation = new ContinuousTabularDataFileValidation(dataFile, delimiter);
        dataValidation.validate(excludedVariables);
        List<ValidationResult> results = dataValidation.getValidationResults();
        List<ValidationResult> infos = new LinkedList<>();
        List<ValidationResult> warnings = new LinkedList<>();
        List<ValidationResult> errors = new LinkedList<>();
        for (ValidationResult result : results) {
            switch (result.getCode()) {
                case INFO:
                    infos.add(result);
                    break;
                case WARNING:
                    warnings.add(result);
                    break;
                default:
                    errors.add(result);
            }
        }
        if (!errors.isEmpty()) {
            errors.forEach(result -> {
                String msg = result.getMessage();
                LOGGER.error(msg);
                System.err.println(msg);
            });
            System.exit(-1);
        }
        warnings.forEach(result -> {
            String msg = result.getMessage();
            LOGGER.warn(msg);
            System.out.println(msg);
        });
        infos.forEach(result -> {
            String msg = result.getMessage();
            LOGGER.info(msg);
            System.out.println(msg);
        });
    }

    protected Set<String> readInVariables(Path variableFile) {
        Set<String> variables = new HashSet<>();

        if (variableFile != null) {
            String task = "reading in excluded variable file " + variableFile.getFileName();
            logStartTask(task);
            try {
                variables.addAll(FileIO.extractUniqueLine(variableFile));
            } catch (IOException exception) {
                logFailedTask(task, exception);
                System.exit(-127);
            }
            logEndTask(task);
        }

        return variables;
    }

    protected String createArgsInfo(TetradCmdAlgoOpt cmdAlgoOpt) {
        Path dataFile = cmdAlgoOpt.getDataFile();
        Path excludedVariableFile = cmdAlgoOpt.getExcludedVariableFile();
        Path knowledgeFile = cmdAlgoOpt.getKnowledgeFile();
        Delimiter delimiter = cmdAlgoOpt.getDelimiter();
        int numOfThreads = cmdAlgoOpt.getNumOfThreads();
        Path dirOut = cmdAlgoOpt.getDirOut();
        String outputPrefix = cmdAlgoOpt.getOutputPrefix();
        boolean validationOutput = cmdAlgoOpt.isValidationOutput();
        boolean verbose = cmdAlgoOpt.isVerbose();

        Formatter fmt = new Formatter();
        if (dataFile != null) {
            fmt.format("data = %s%n", dataFile.getFileName());
        }
        if (excludedVariableFile != null) {
            fmt.format("exclude-variables = %s%n", excludedVariableFile.getFileName());
        }
        if (knowledgeFile != null) {
            fmt.format("knowledge = %s%n", knowledgeFile.getFileName());
        }
        fmt.format("delimiter = %s%n", delimiter.getName());
        fmt.format("verbose = %s%n", verbose);
        fmt.format("thread = %s%n", numOfThreads);
        printParameterInfos(fmt, cmdAlgoOpt);

        printValidationInfos(fmt, cmdAlgoOpt);

        fmt.format("out = %s%n", dirOut.getFileName().toString());
        fmt.format("output-prefix = %s%n", outputPrefix);
        fmt.format("no-validation-output = %s%n", !validationOutput);

        return fmt.toString();
    }

    protected String creteHeading(AlgorithmType algorithmType) {
        Formatter fmt = new Formatter();
        fmt.format("================================================================================%n");
        fmt.format("%s (%s)%n", algorithmType.getTitle(), AppUtils.fmtDateNow());
        fmt.format("================================================================================%n");

        return fmt.toString();
    }

    protected void showHelp(String cmd, Options mainOptions) {
        AppUtils.showHelp(cmd, mainOptions);
    }

    protected boolean needsToShowHelp(String[] args) {
        return args == null || args.length == 0 || Args.hasLongOption(args, "help");
    }

    private static void logStartTask(String task) {
        String msg = String.format("%s: Start %s.", AppUtils.fmtDateNow(), task);
        System.out.println(msg);
        LOGGER.info(String.format("Start %s.", task));
    }

    private static void logEndTask(String task) {
        String msg = String.format("%s: End %s.", AppUtils.fmtDateNow(), task);
        System.out.println(msg);
        LOGGER.info(String.format("End %s.", task));
    }

    private static void logFailedTask(String task, Exception exception) {
        String errMsg = String.format("Failed %s.", task);
        System.err.println(errMsg);
        LOGGER.error(errMsg, exception);
    }

}
