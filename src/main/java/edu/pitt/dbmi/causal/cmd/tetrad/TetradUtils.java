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

import edu.cmu.tetrad.data.DataModel;
import edu.cmu.tetrad.data.DataType;
import edu.cmu.tetrad.data.IKnowledge;
import edu.cmu.tetrad.util.DataConvertUtils;
import edu.cmu.tetrad.util.ParamDescription;
import edu.cmu.tetrad.util.ParamDescriptions;
import edu.cmu.tetrad.util.Parameters;
import edu.pitt.dbmi.causal.cmd.CmdArgs;
import edu.pitt.dbmi.causal.cmd.ValidationException;
import edu.pitt.dbmi.causal.cmd.util.FileIO;
import edu.pitt.dbmi.data.Dataset;
import edu.pitt.dbmi.data.reader.covariance.CovarianceDataReader;
import edu.pitt.dbmi.data.reader.covariance.LowerCovarianceDataReader;
import edu.pitt.dbmi.data.reader.tabular.ContinuousTabularDataFileReader;
import edu.pitt.dbmi.data.reader.tabular.MixedTabularDataFileReader;
import edu.pitt.dbmi.data.reader.tabular.TabularDataReader;
import edu.pitt.dbmi.data.reader.tabular.VerticalDiscreteTabularDataReader;
import edu.pitt.dbmi.data.validation.ValidationCode;
import edu.pitt.dbmi.data.validation.ValidationResult;
import edu.pitt.dbmi.data.validation.covariance.CovarianceDataFileValidation;
import edu.pitt.dbmi.data.validation.tabular.ContinuousTabularDataFileValidation;
import edu.pitt.dbmi.data.validation.tabular.DataFileValidation;
import edu.pitt.dbmi.data.validation.tabular.MixedTabularDataFileValidation;
import edu.pitt.dbmi.data.validation.tabular.TabularDataValidation;
import edu.pitt.dbmi.data.validation.tabular.VerticalDiscreteTabularDataFileValidation;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.Collections;
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

    private static void logStartValidation(Path file, PrintStream out) {
        String fileName = file.getFileName().toString();
        String msg = "Start validating file: " + fileName;
        out.println(msg);
        LOGGER.info(msg);
    }

    private static void logFinishValidation(Path file, PrintStream out) {
        String fileName = file.getFileName().toString();
        String msg = "Finish validating file: " + fileName;
        out.println(msg);
        LOGGER.info(msg);
    }

    private static void logStartReading(Path file, PrintStream out) {
        String fileName = file.getFileName().toString();
        String msg = "Start reading in file: " + fileName;
        out.println(msg);
        LOGGER.info(msg);
    }

    private static void logFinishReading(Path file, PrintStream out) {
        String fileName = file.getFileName().toString();
        String msg = "Finish reading in file: " + fileName;
        out.println(msg);
        LOGGER.info(msg);
    }

    private static void logValidationResults(Map<ValidationCode, List<ValidationResult>> map, PrintStream out) {
        if (map.containsKey(ValidationCode.INFO)) {
            map.get(ValidationCode.INFO).stream()
                    .map(e -> e.getMessage())
                    .forEach(e -> {
                        LOGGER.info(e);
                        out.println(e);
                    });
        }
        if (map.containsKey(ValidationCode.WARNING)) {
            map.get(ValidationCode.WARNING).stream()
                    .map(e -> e.getMessage())
                    .forEach(e -> {
                        LOGGER.info(e);
                        out.println(e);
                    });
        }
        if (map.containsKey(ValidationCode.ERROR)) {
            map.get(ValidationCode.ERROR).stream()
                    .map(e -> e.getMessage())
                    .forEach(e -> {
                        LOGGER.info(e);
                        out.println(e);
                    });
        }
    }

    public static void validateDataModels(CmdArgs cmdArgs, PrintStream out) throws ValidationException {
        DataType dataType = cmdArgs.getDataType();
        switch (dataType) {
            case Covariance:
                validateCovariance(cmdArgs, out);
            case Continuous:
            case Discrete:
            case Mixed:
                validateTabularData(cmdArgs, out);
        }
    }

    public static IKnowledge readInKnowledge(CmdArgs cmdArgs, PrintStream out) throws IOException {
        IKnowledge knowledge;

        Path file = cmdArgs.getKnowledgeFile();
        if (file == null) {
            knowledge = null;
        } else {
            logStartReading(file, out);
            knowledge = new edu.cmu.tetrad.data.DataReader().parseKnowledge(file.toFile());
            logFinishReading(file, out);
        }

        return knowledge;
    }

    private static Set<String> getExcludeVariables(CmdArgs cmdArgs, PrintStream out) throws IOException {
        Set<String> excludeVars;

        Path file = cmdArgs.getExcludeVariableFile();
        if (file == null) {
            excludeVars = new HashSet<>();
        } else {
            logStartReading(file, out);
            excludeVars = FileIO.extractUniqueLine(cmdArgs.getExcludeVariableFile());
            logFinishReading(file, out);
        }

        return excludeVars;
    }

    public static List<DataModel> getDataModels(CmdArgs cmdArgs, PrintStream out) throws IOException {
        DataType dataType = cmdArgs.getDataType();
        switch (dataType) {
            case Covariance:
                return getCovariance(cmdArgs, out);
            case Continuous:
            case Discrete:
            case Mixed:
                return getTabularData(cmdArgs, out);
            default:
                return Collections.EMPTY_LIST;
        }
    }

    private static void validateCovariance(CmdArgs cmdArgs, PrintStream out) throws ValidationException {
        List<Path> dataFiles = cmdArgs.getDatasetFiles();
        for (Path dataFile : dataFiles) {
            DataFileValidation validation = new CovarianceDataFileValidation(dataFile.toFile(), cmdArgs.getDelimiter());
            validation.setCommentMarker(cmdArgs.getCommentMarker());

            logStartValidation(dataFile, out);
            validation.validate();
            logFinishValidation(dataFile, out);

            List<ValidationResult> results = validation.getValidationResults();
            Map<ValidationCode, List<ValidationResult>> map = results.stream()
                    .collect(Collectors.groupingBy(ValidationResult::getCode));

            logValidationResults(map, out);

            if (map.containsKey(ValidationCode.ERROR)) {
                throw new ValidationException();
            }
        }
    }

    private static void validateTabularData(CmdArgs cmdArgs, PrintStream out) throws ValidationException {
        Set<String> excludeVars = new HashSet<>();
        try {
            excludeVars.addAll(getExcludeVariables(cmdArgs, out));
        } catch (IOException exception) {
            throw new ValidationException(exception);
        }

        List<Path> dataFiles = cmdArgs.getDatasetFiles();
        for (Path dataFile : dataFiles) {
            TabularDataValidation dataReader = getDataValidationReader(dataFile.toFile(), cmdArgs);
            if (dataReader != null) {
                dataReader.setCommentMarker(cmdArgs.getCommentMarker());
                dataReader.setMissingValueMarker(cmdArgs.getMissingValueMarker());
                dataReader.setQuoteCharacter(cmdArgs.getQuoteChar());

                logStartValidation(dataFile, out);
                dataReader.validate(excludeVars);
                logFinishValidation(dataFile, out);

                List<ValidationResult> results = dataReader.getValidationResults();
                Map<ValidationCode, List<ValidationResult>> map = results.stream()
                        .collect(Collectors.groupingBy(ValidationResult::getCode));

                logValidationResults(map, out);

                if (map.containsKey(ValidationCode.ERROR)) {
                    throw new ValidationException();
                }
            }
        }
    }

    private static List<DataModel> getCovariance(CmdArgs cmdArgs, PrintStream out) throws IOException {
        List<DataModel> dataModels = new LinkedList<>();

        List<Path> dataFiles = cmdArgs.getDatasetFiles();
        for (Path dataFile : dataFiles) {
            CovarianceDataReader dataReader = new LowerCovarianceDataReader(dataFile.toFile(), cmdArgs.getDelimiter());
            dataReader.setCommentMarker(cmdArgs.getCommentMarker());

            logStartReading(dataFile, out);
            Dataset dataset = dataReader.readInData();
            logFinishReading(dataFile, out);

            dataModels.add(DataConvertUtils.toDataModel(dataset));
        }

        return dataModels;
    }

    private static List<DataModel> getTabularData(CmdArgs cmdArgs, PrintStream out) throws IOException {
        List<DataModel> dataModels = new LinkedList<>();

        Set<String> excludeVars = getExcludeVariables(cmdArgs, out);

        List<Path> dataFiles = cmdArgs.getDatasetFiles();
        for (Path dataFile : dataFiles) {
            TabularDataReader dataReader = getDataReader(dataFile.toFile(), cmdArgs);
            if (dataReader != null) {
                dataReader.setCommentMarker(cmdArgs.getCommentMarker());
                dataReader.setMissingValueMarker(cmdArgs.getMissingValueMarker());
                dataReader.setQuoteCharacter(cmdArgs.getQuoteChar());
                dataReader.setHasHeader(cmdArgs.isHasHeader());

                logStartReading(dataFile, out);
                Dataset dataset = dataReader.readInData(excludeVars);
                logFinishReading(dataFile, out);

                dataModels.add(DataConvertUtils.toDataModel(dataset));
            }
        }

        return dataModels;
    }

    private static TabularDataValidation getDataValidationReader(File dataFile, CmdArgs cmdArgs) {
        switch (cmdArgs.getDataType()) {
            case Continuous:
                return new ContinuousTabularDataFileValidation(dataFile, cmdArgs.getDelimiter());
            case Discrete:
                return new VerticalDiscreteTabularDataFileValidation(dataFile, cmdArgs.getDelimiter());
            case Mixed:
                return new MixedTabularDataFileValidation(cmdArgs.getNumCategories(), dataFile, cmdArgs.getDelimiter());
            default:
                return null;
        }
    }

    private static TabularDataReader getDataReader(File dataFile, CmdArgs cmdArgs) {
        switch (cmdArgs.getDataType()) {
            case Continuous:
                return new ContinuousTabularDataFileReader(dataFile, cmdArgs.getDelimiter());
            case Discrete:
                return new VerticalDiscreteTabularDataReader(dataFile, cmdArgs.getDelimiter());
            case Mixed:
                return new MixedTabularDataFileReader(cmdArgs.getNumCategories(), dataFile, cmdArgs.getDelimiter());
            default:
                return null;
        }
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
                parameters.set(k, (v == null) ? Boolean.TRUE : Boolean.valueOf(v));
            } else if (obj instanceof String) {
                parameters.set(k, v);
            }
        });

        return parameters;
    }

}
