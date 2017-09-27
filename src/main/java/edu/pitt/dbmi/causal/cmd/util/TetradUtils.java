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
package edu.pitt.dbmi.causal.cmd.util;

import edu.cmu.tetrad.data.DataModel;
import edu.cmu.tetrad.data.DataType;
import edu.cmu.tetrad.data.IKnowledge;
import edu.cmu.tetrad.util.DataConvertUtils;
import edu.cmu.tetrad.util.ParamDescription;
import edu.cmu.tetrad.util.ParamDescriptions;
import edu.cmu.tetrad.util.Parameters;
import edu.pitt.dbmi.causal.cmd.CmdArgs;
import edu.pitt.dbmi.causal.cmd.ValidationException;
import edu.pitt.dbmi.data.Dataset;
import edu.pitt.dbmi.data.Delimiter;
import edu.pitt.dbmi.data.reader.tabular.ContinuousTabularDataFileReader;
import edu.pitt.dbmi.data.reader.tabular.MixedTabularDataFileReader;
import edu.pitt.dbmi.data.reader.tabular.TabularDataReader;
import edu.pitt.dbmi.data.reader.tabular.VerticalDiscreteTabularDataReader;
import edu.pitt.dbmi.data.validation.ValidationCode;
import edu.pitt.dbmi.data.validation.ValidationResult;
import edu.pitt.dbmi.data.validation.tabular.ContinuousTabularDataFileValidation;
import edu.pitt.dbmi.data.validation.tabular.DataFileValidation;
import edu.pitt.dbmi.data.validation.tabular.MixedTabularDataFileValidation;
import edu.pitt.dbmi.data.validation.tabular.VerticalDiscreteTabularDataFileValidation;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Sep 26, 2017 5:16:40 PM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public class TetradUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(TetradUtils.class);

    private TetradUtils() {
    }

    public static IKnowledge readInKnowledge(CmdArgs cmdArgs) throws IOException {
        Path knowledge = cmdArgs.getKnowledgeFile();
        if (knowledge == null) {
            return null;
        }

        return new edu.cmu.tetrad.data.DataReader().parseKnowledge(knowledge.toFile());
    }

    public static Parameters getParameters(CmdArgs cmdArgs) {
        Parameters parameters = new Parameters();

        ParamDescriptions paramDescs = ParamDescriptions.getInstance();
        Map<String, String> params = cmdArgs.getParameters();
        params.forEach((k, v) -> {
            ParamDescription paramDesc = paramDescs.get(k);
            Object obj = paramDesc.getDefaultValue();
            if (obj instanceof Byte) {
                parameters.set(k, Byte.valueOf(v));
            } else if (obj instanceof Integer) {
                parameters.set(k, Integer.valueOf(v));
            } else if (obj instanceof Long) {
                parameters.set(k, Long.valueOf(v));
            } else if (obj instanceof Float) {
                parameters.set(k, Float.valueOf(v));
            } else if (obj instanceof Double) {
                parameters.set(k, Double.valueOf(v));
            } else if (obj instanceof Boolean) {
                parameters.set(k, Boolean.TRUE);
            } else if (obj instanceof String) {
                parameters.set(k, v);
            }
        });

        return parameters;
    }

    public static List<DataModel> getDataModels(CmdArgs cmdArgs) throws IOException, ValidationException {
        List<DataModel> dataModels = new LinkedList<>();

        Path excludeVariableFile = cmdArgs.getExcludeVariableFile();
        char quoteChar = cmdArgs.getQuoteChar();
        String missingValueMarker = cmdArgs.getMissingValueMarker();
        String commentMarker = cmdArgs.getCommentMarker();
        Delimiter delimiter = cmdArgs.getDelimiter();
        DataType dataType = cmdArgs.getDataType();
        int numCategories = cmdArgs.getNumCategories();
        boolean validate = !cmdArgs.isSkipValidation();

        Set<String> excludeVars;
        if (excludeVariableFile == null) {
            excludeVars = new HashSet<>();
        } else {
            LOGGER.info("Start reading file: " + excludeVariableFile.toString());
            excludeVars = FileIO.extractUniqueLine(excludeVariableFile);
            LOGGER.info("Finish reading file: " + excludeVariableFile.toString());
        }

        List<Path> dataFiles = cmdArgs.getDatasetFiles();
        if (validate) {
            validate(dataFiles, cmdArgs);
        }

        for (Path dataFile : dataFiles) {
            String fileName = dataFile.toString();

            TabularDataReader dataReader = getDataReader(dataFile.toFile(), delimiter, dataType, numCategories);
            if (dataReader != null) {
                dataReader.setCommentMarker(commentMarker);
                dataReader.setMissingValueMarker(missingValueMarker);
                dataReader.setQuoteCharacter(quoteChar);

                LOGGER.info("Start reading file: " + fileName);
                Dataset dataset = dataReader.readInData(excludeVars);
                LOGGER.info("Finish reading file: " + fileName);

                dataModels.add(DataConvertUtils.toDataModel(dataset));
            }
        }

        return dataModels;
    }

    private static void validate(List<Path> dataFiles, CmdArgs cmdArgs) throws ValidationException {
        char quoteChar = cmdArgs.getQuoteChar();
        String missingValueMarker = cmdArgs.getMissingValueMarker();
        String commentMarker = cmdArgs.getCommentMarker();
        Delimiter delimiter = cmdArgs.getDelimiter();
        DataType dataType = cmdArgs.getDataType();
        int numCategories = cmdArgs.getNumCategories();

        for (Path dataFile : dataFiles) {
            String fileName = dataFile.toString();

            DataFileValidation validation = getDataFileValidation(dataFile.toFile(), delimiter, dataType, numCategories);
            if (validation != null) {
                validation.setCommentMarker(commentMarker);
                validation.setMissingValueMarker(missingValueMarker);
                validation.setQuoteCharacter(quoteChar);

                LOGGER.info("Start validating file: " + fileName);
                validation.validate();
                LOGGER.info("Start validating file: " + fileName);

                Map<ValidationCode, List<ValidationResult>> results = validation.getValidationResults().stream()
                        .collect(Collectors.groupingBy(ValidationResult::getCode));
                results.forEach((k, v) -> {
                    switch (k) {
                        case INFO:
                            v.forEach(s -> LOGGER.info(s.getMessage()));
                            break;
                        case WARNING:
                            v.forEach(s -> LOGGER.warn(s.getMessage()));
                            break;
                        case ERROR:
                            v.forEach(s -> LOGGER.error(s.getMessage()));
                            break;
                    }
                });
                if (results.containsKey(ValidationCode.ERROR) && !results.get(ValidationCode.ERROR).isEmpty()) {
                    throw new ValidationException("Validation failed: " + fileName);
                }
            }
        }
    }

    private static DataFileValidation getDataFileValidation(File dataFile, Delimiter delimiter, DataType dataType, int numCategories) {
        switch (dataType) {
            case Continuous:
                return new ContinuousTabularDataFileValidation(dataFile, delimiter);
            case Discrete:
                return new VerticalDiscreteTabularDataFileValidation(dataFile, delimiter);
            case Mixed:
                return new MixedTabularDataFileValidation(numCategories, dataFile, delimiter);
            default:
                return null;
        }
    }

    private static TabularDataReader getDataReader(File dataFile, Delimiter delimiter, DataType dataType, int numCategories) {
        switch (dataType) {
            case Continuous:
                return new ContinuousTabularDataFileReader(dataFile, delimiter);
            case Discrete:
                return new VerticalDiscreteTabularDataReader(dataFile, delimiter);
            case Mixed:
                return new MixedTabularDataFileReader(numCategories, dataFile, delimiter);
            default:
                return null;
        }
    }

}
