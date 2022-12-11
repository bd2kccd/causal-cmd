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

import edu.cmu.tetrad.data.DataType;
import edu.pitt.dbmi.causal.cmd.CmdArgs;
import edu.pitt.dbmi.causal.cmd.ValidationException;
import edu.pitt.dbmi.causal.cmd.util.LogMessages;
import edu.pitt.dbmi.data.reader.DataColumn;
import edu.pitt.dbmi.data.reader.Delimiter;
import edu.pitt.dbmi.data.reader.tabular.TabularColumnFileReader;
import edu.pitt.dbmi.data.reader.tabular.TabularColumnReader;
import edu.pitt.dbmi.data.reader.tabular.TabularDataFileReader;
import edu.pitt.dbmi.data.reader.tabular.TabularDataReader;
import edu.pitt.dbmi.data.reader.validation.ValidationCode;
import edu.pitt.dbmi.data.reader.validation.ValidationResult;
import edu.pitt.dbmi.data.reader.validation.covariance.CovarianceValidation;
import edu.pitt.dbmi.data.reader.validation.covariance.LowerCovarianceDataFileValidation;
import edu.pitt.dbmi.data.reader.validation.tabular.TabularDataFileValidation;
import edu.pitt.dbmi.data.reader.validation.tabular.TabularDataValidation;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class {@code DataValidations} is a utility class for validating data.
 *
 * Jan 9, 2019 2:17:36 PM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public final class DataValidations {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataValidations.class);

    private DataValidations() {
    }

    /**
     * Validate tabular dataset and covariance data.
     *
     * @param cmdArgs command-line arguments
     * @param out output stream to write message to
     * @throws ValidationException when dataset validation fails
     */
    public static void validate(CmdArgs cmdArgs, PrintStream out) throws ValidationException {
        DataType dataType = cmdArgs.getDataType();
        switch (dataType) {
            case Covariance:
                validateCovariance(cmdArgs, out);
                break;
            case Continuous:
            case Discrete:
            case Mixed:
                validateTabularData(cmdArgs, out);
                break;
            default:
                String errMsg = String.format("Data type %s not supported.", dataType.name());
                throw new ValidationException(errMsg);
        }
    }

    /**
     * Validate tabular dataset.
     *
     * @param cmdArgs command-line arguments
     * @param out output stream to write message to
     * @throws ValidationException
     */
    private static void validateTabularData(CmdArgs cmdArgs, PrintStream out) throws ValidationException {
        try {
            Delimiter delimiter = cmdArgs.getDelimiter();
            char quoteCharacter = cmdArgs.getQuoteChar();
            String commentMarker = cmdArgs.getCommentMarker();
            String missingValueMarker = cmdArgs.getMissingValueMarker();
            boolean hasHeader = cmdArgs.isHasHeader();

            Set<String> varsToExclude = DataFiles.readInVariablesToExclude(cmdArgs, out);
            for (Path dataFile : cmdArgs.getDatasetFiles()) {
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

                TabularDataValidation dataValidation = new TabularDataFileValidation(dataFile, delimiter);
                dataValidation.setCommentMarker(commentMarker);
                dataValidation.setQuoteCharacter(quoteCharacter);
                dataValidation.setMissingDataMarker(missingValueMarker);

                // run data validationn
                LogMessages.dataValidationStart(dataFile, LOGGER, out);
                List<ValidationResult> validationResults = dataValidation.validate(dataColumns, hasHeader);
                LogMessages.dataValidationEnd(dataFile, LOGGER, out);

                // group validation results by validation code
                Map<ValidationCode, List<ValidationResult>> groupedResults = validationResults.stream()
                        .collect(Collectors.groupingBy(ValidationResult::getCode));
                LogMessages.dataValidationResults(groupedResults, LOGGER, out);

                if (groupedResults.containsKey(ValidationCode.ERROR)) {
                    throw new ValidationException();
                }
            }
        } catch (IOException exception) {
            throw new ValidationException(exception);
        }
    }

    /**
     * Validate covariance data.
     *
     * @param cmdArgs command-line arguments
     * @param out output stream to write message to
     * @throws ValidationException
     */
    private static void validateCovariance(CmdArgs cmdArgs, PrintStream out) throws ValidationException {
        for (Path dataFile : cmdArgs.getDatasetFiles()) {
            Delimiter delimiter = cmdArgs.getDelimiter();
            char quoteCharacter = cmdArgs.getQuoteChar();
            String commentMarker = cmdArgs.getCommentMarker();

            CovarianceValidation validation = new LowerCovarianceDataFileValidation(dataFile, delimiter);
            validation.setCommentMarker(commentMarker);
            validation.setQuoteCharacter(quoteCharacter);

            // run data validationn
            LogMessages.dataValidationStart(dataFile, LOGGER, out);
            List<ValidationResult> validationResults = validation.validate();
            LogMessages.dataValidationEnd(dataFile, LOGGER, out);

            // group validation results by validation code
            Map<ValidationCode, List<ValidationResult>> groupedResults = validationResults.stream()
                    .collect(Collectors.groupingBy(ValidationResult::getCode));
            LogMessages.dataValidationResults(groupedResults, LOGGER, out);

            if (groupedResults.containsKey(ValidationCode.ERROR)) {
                throw new ValidationException();
            }
        }
    }

}
