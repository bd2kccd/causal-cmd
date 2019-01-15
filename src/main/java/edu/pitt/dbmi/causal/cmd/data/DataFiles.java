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
package edu.pitt.dbmi.causal.cmd.data;

import edu.cmu.tetrad.data.DataModel;
import edu.cmu.tetrad.data.DataType;
import edu.cmu.tetrad.data.IKnowledge;
import edu.cmu.tetrad.util.DataConvertUtils;
import edu.pitt.dbmi.causal.cmd.AlgorithmRunException;
import edu.pitt.dbmi.causal.cmd.CmdArgs;
import edu.pitt.dbmi.causal.cmd.util.LogMessages;
import edu.pitt.dbmi.data.reader.DataColumn;
import edu.pitt.dbmi.data.reader.Delimiter;
import edu.pitt.dbmi.data.reader.covariance.CovarianceData;
import edu.pitt.dbmi.data.reader.covariance.CovarianceDataReader;
import edu.pitt.dbmi.data.reader.covariance.LowerCovarianceDataFileReader;
import edu.pitt.dbmi.data.reader.tabular.TabularColumnFileReader;
import edu.pitt.dbmi.data.reader.tabular.TabularColumnReader;
import edu.pitt.dbmi.data.reader.tabular.TabularDataFileReader;
import edu.pitt.dbmi.data.reader.tabular.TabularDataReader;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Jan 14, 2019 11:25:47 AM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public final class DataFiles {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataFiles.class);

    private DataFiles() {
    }

    public static IKnowledge readInKnowledge(CmdArgs cmdArgs, PrintStream out) throws IOException {
        IKnowledge knowledge;

        Path file = cmdArgs.getKnowledgeFile();
        if (file == null) {
            knowledge = null;
        } else {
            LogMessages.readingFileStart(file, LOGGER, out);
            knowledge = (new edu.cmu.tetrad.data.DataReader()).parseKnowledge(file.toFile());
            LogMessages.readingFileEnd(file, LOGGER, out);
        }

        return knowledge;
    }

    public static List<DataModel> readInDatasets(CmdArgs cmdArgs, PrintStream out) throws IOException, AlgorithmRunException {
        DataType dataType = cmdArgs.getDataType();
        switch (dataType) {
            case Covariance:
                return readInCovarianceFile(cmdArgs, out);
            case Continuous:
            case Discrete:
            case Mixed:
                return readInTabularData(cmdArgs, out);
            default:
                String errMsg = String.format("Data type %s not supported.", dataType.name());
                throw new AlgorithmRunException(errMsg);
        }
    }

    private static List<DataModel> readInTabularData(CmdArgs cmdArgs, PrintStream out) throws IOException {
        List<DataModel> dataModels = new LinkedList<>();

        Set<String> varsToExclude = DataFiles.readInVariablesToExclude(cmdArgs, out);
        for (Path dataFile : cmdArgs.getDatasetFiles()) {
            Delimiter delimiter = cmdArgs.getDelimiter();
            char quoteCharacter = cmdArgs.getQuoteChar();
            String commentMarker = cmdArgs.getCommentMarker();
            String missingValueMarker = cmdArgs.getMissingValueMarker();
            boolean hasHeader = cmdArgs.isHasHeader();

            TabularColumnReader columnReader = new TabularColumnFileReader(dataFile, delimiter);
            columnReader.setCommentMarker(commentMarker);
            columnReader.setQuoteCharacter(quoteCharacter);

            boolean isDiscrete = (cmdArgs.getDataType() == DataType.Discrete);
            DataColumn[] dataColumns = columnReader.readInDataColumns(varsToExclude, isDiscrete);

            // handle mixed data
            if (cmdArgs.getDataType() == DataType.Mixed) {
                TabularDataReader dataReader = new TabularDataFileReader(dataFile, delimiter);
                dataReader.setCommentMarker(commentMarker);
                dataReader.setQuoteCharacter(quoteCharacter);
                dataReader.setMissingDataMarker(missingValueMarker);

                int numberOfCategories = cmdArgs.getNumCategories();
                dataReader.determineDiscreteDataColumns(dataColumns, numberOfCategories, hasHeader);
            }

            TabularDataReader dataReader = new TabularDataFileReader(dataFile, delimiter);
            dataReader.setCommentMarker(commentMarker);
            dataReader.setQuoteCharacter(quoteCharacter);
            dataReader.setMissingDataMarker(missingValueMarker);

            LogMessages.readingFileStart(dataFile, LOGGER, out);
            DataModel dataModel = DataConvertUtils.toDataModel(dataReader.read(dataColumns, hasHeader));
            LogMessages.readingFileEnd(dataFile, LOGGER, out);

            LogMessages.dataInfo(dataFile, dataModel, LOGGER, out);

            dataModels.add(dataModel);
        }

        return dataModels;
    }

    private static List<DataModel> readInCovarianceFile(CmdArgs cmdArgs, PrintStream out) throws IOException {
        List<DataModel> dataModels = new LinkedList<>();

        for (Path dataFile : cmdArgs.getDatasetFiles()) {
            Delimiter delimiter = cmdArgs.getDelimiter();
            char quoteCharacter = cmdArgs.getQuoteChar();
            String commentMarker = cmdArgs.getCommentMarker();

            CovarianceDataReader dataFileReader = new LowerCovarianceDataFileReader(dataFile, delimiter);
            dataFileReader.setCommentMarker(commentMarker);
            dataFileReader.setQuoteCharacter(quoteCharacter);

            LogMessages.readingFileStart(dataFile, LOGGER, out);
            CovarianceData covarianceData = dataFileReader.readInData();
            LogMessages.readingFileEnd(dataFile, LOGGER, out);

            dataModels.add(DataConvertUtils.toCovarianceMatrix(covarianceData));
        }

        return dataModels;
    }

    public static Set<String> readInVariablesToExclude(CmdArgs cmdArgs, PrintStream out) throws IOException {
        Set<String> variablesToExclude = new HashSet<>();

        Path file = cmdArgs.getExcludeVariableFile();
        if (file != null) {
            LogMessages.readingFileStart(file, LOGGER, out);
            try (Stream<String> stream = Files.lines(file)) {
                stream
                        .map(e -> e.trim())
                        .filter(e -> !e.isEmpty())
                        .distinct()
                        .collect(Collectors.toCollection(() -> variablesToExclude));
            }
            LogMessages.readingFileEnd(file, LOGGER, out);
        }

        return variablesToExclude;
    }

}
