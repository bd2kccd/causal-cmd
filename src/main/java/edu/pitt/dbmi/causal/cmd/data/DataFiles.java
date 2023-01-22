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
import edu.cmu.tetrad.data.DataUtils;
import edu.cmu.tetrad.data.DelimiterType;
import edu.cmu.tetrad.data.Knowledge;
import edu.cmu.tetrad.util.DataConvertUtils;
import edu.pitt.dbmi.causal.cmd.AlgorithmRunException;
import edu.pitt.dbmi.causal.cmd.CmdArgs;
import edu.pitt.dbmi.causal.cmd.util.LogMessages;
import edu.pitt.dbmi.data.reader.DataColumn;
import edu.pitt.dbmi.data.reader.DataColumns;
import edu.pitt.dbmi.data.reader.Delimiter;
import edu.pitt.dbmi.data.reader.covariance.CovarianceData;
import edu.pitt.dbmi.data.reader.covariance.CovarianceDataReader;
import edu.pitt.dbmi.data.reader.covariance.LowerCovarianceDataFileReader;
import edu.pitt.dbmi.data.reader.metadata.Metadata;
import edu.pitt.dbmi.data.reader.metadata.MetadataFileReader;
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
 * The class {@code DataFiles} is a utility class for reading various data
 * files.
 *
 * Jan 14, 2019 11:25:47 AM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public final class DataFiles {

    /**
     * The logger for the class {@code DataFiles}.
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(DataFiles.class);

    private DataFiles() {
    }

    /**
     * Read in metadata file.
     *
     * @param cmdArgs command-line arguments
     * @param out output stream to write message to
     * @return metadata
     * @throws IOException when errors occur during reading file
     */
    public static Metadata readInMetadata(CmdArgs cmdArgs, PrintStream out) throws IOException {
        Path file = cmdArgs.getMetadataFile();
        if (file == null) {
            return null;
        } else {
            LogMessages.readingFileStart(file, LOGGER, out);
            Metadata metadata = (new MetadataFileReader(file)).read();
            LogMessages.readingFileEnd(file, LOGGER, out);

            return metadata;
        }
    }

    /**
     * Read in knowledge file.
     *
     * @param cmdArgs command-line arguments
     * @param out output stream to write message to
     * @return knowledge information from file
     * @throws IOException when errors occur during reading file
     */
    public static Knowledge readInKnowledge(CmdArgs cmdArgs, PrintStream out) throws IOException {
        Path file = cmdArgs.getKnowledgeFile();
        if (file == null) {
            return null;
        } else {
            LogMessages.readingFileStart(file, LOGGER, out);
            Knowledge knowledge = DataUtils.loadKnowledge(file.toFile(), DelimiterType.WHITESPACE, "//");
            LogMessages.readingFileEnd(file, LOGGER, out);

            return knowledge;
        }
    }

    /**
     * Read in datasets files.
     *
     * @param cmdArgs command-line arguments
     * @param out output stream to write message to
     * @return list of datasets read in from files
     * @throws IOException when errors occur during reading file
     * @throws AlgorithmRunException when dataset is not supported by the given
     * algorithm from command-line
     */
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

    /**
     * Read in tabular dataset files.
     *
     * @param cmdArgs command-line arguments
     * @param out output stream to write message to
     * @return list of datasets read in from files
     * @throws IOException when errors occur during reading file
     */
    private static List<DataModel> readInTabularData(CmdArgs cmdArgs, PrintStream out) throws IOException {
        List<DataModel> dataModels = new LinkedList<>();

        final Metadata metadata = readInMetadata(cmdArgs, out);

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
            DataColumn[] dataColumns;
            if (hasHeader) {
                dataColumns = columnReader.readInDataColumns(varsToExclude, isDiscrete);

                if (metadata != null) {
                    dataColumns = DataColumns.update(dataColumns, metadata);
                }
            } else {
                dataColumns = columnReader.generateColumns(new int[0], isDiscrete);
            }

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
            DataModel dataModel = DataConvertUtils.toDataModel(dataReader.read(dataColumns, hasHeader, metadata));
            LogMessages.readingFileEnd(dataFile, LOGGER, out);

            LogMessages.dataInfo(dataFile, dataModel, LOGGER, out);

            dataModels.add(dataModel);
        }

        return dataModels;
    }

    /**
     * Read in covariances files.
     *
     * @param cmdArgs command-line arguments
     * @param out output stream to write message to
     * @return list of datasets read in from files
     * @throws IOException when errors occur during reading file
     */
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

    /**
     * Read in exclude-variable file.
     *
     * @param cmdArgs command-line arguments
     * @param out output stream to write message to
     * @return set of variables to exclude reading in
     * @throws IOException when errors occur during reading file
     */
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
