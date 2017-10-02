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
import edu.pitt.dbmi.causal.cmd.util.FileIO;
import edu.pitt.dbmi.data.Dataset;
import edu.pitt.dbmi.data.reader.covariance.CovarianceDataReader;
import edu.pitt.dbmi.data.reader.covariance.LowerCovarianceDataReader;
import edu.pitt.dbmi.data.reader.tabular.ContinuousTabularDataFileReader;
import edu.pitt.dbmi.data.reader.tabular.MixedTabularDataFileReader;
import edu.pitt.dbmi.data.reader.tabular.TabularDataReader;
import edu.pitt.dbmi.data.reader.tabular.VerticalDiscreteTabularDataReader;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    private static void logStart(Path file, PrintStream out) {
        String fileName = file.getFileName().toString();
        String msg = "Start reading in file: " + fileName;
        out.println(msg);
        LOGGER.info(msg);
    }

    private static void logFinish(Path file, PrintStream out) {
        String fileName = file.getFileName().toString();
        String msg = "Finish reading in file: " + fileName;
        out.println(msg);
        LOGGER.info(msg);
    }

    public static IKnowledge readInKnowledge(CmdArgs cmdArgs, PrintStream out) throws IOException {
        IKnowledge knowledge;

        Path file = cmdArgs.getKnowledgeFile();
        if (file == null) {
            knowledge = null;
        } else {
            logStart(file, out);
            knowledge = new edu.cmu.tetrad.data.DataReader().parseKnowledge(file.toFile());
            logFinish(file, out);
        }

        return knowledge;
    }

    private static Set<String> getExcludeVariables(CmdArgs cmdArgs, PrintStream out) throws IOException {
        Set<String> excludeVars;

        Path file = cmdArgs.getExcludeVariableFile();
        if (file == null) {
            excludeVars = new HashSet<>();
        } else {
            logStart(file, out);
            excludeVars = FileIO.extractUniqueLine(cmdArgs.getExcludeVariableFile());
            logFinish(file, out);
        }

        return excludeVars;
    }

    public static List<DataModel> getDataModel(CmdArgs cmdArgs, PrintStream out) throws IOException {
        List<DataModel> dataModels = new LinkedList<>();

        DataType dataType = cmdArgs.getDataType();
        switch (dataType) {
            case Covariance:
                getCovariance(cmdArgs, dataModels, out);
                break;
            case Continuous:
            case Discrete:
            case Mixed:
                getTabularData(cmdArgs, dataModels, out);
                break;
        }

        return dataModels;
    }

    private static void getCovariance(CmdArgs cmdArgs, List<DataModel> dataModels, PrintStream out) throws IOException {
        List<Path> dataFiles = cmdArgs.getDatasetFiles();
        for (Path dataFile : dataFiles) {
            CovarianceDataReader dataReader = new LowerCovarianceDataReader(dataFile.toFile(), cmdArgs.getDelimiter());

            logStart(dataFile, out);
            Dataset dataset = dataReader.readInData();
            logFinish(dataFile, out);

            dataModels.add(DataConvertUtils.toDataModel(dataset));
        }
    }

    private static void getTabularData(CmdArgs cmdArgs, List<DataModel> dataModels, PrintStream out) throws IOException {
        Set<String> excludeVars = getExcludeVariables(cmdArgs, out);

        List<Path> dataFiles = cmdArgs.getDatasetFiles();
        for (Path dataFile : dataFiles) {
            TabularDataReader dataReader = getDataReader(dataFile.toFile(), cmdArgs);
            if (dataReader != null) {
                dataReader.setCommentMarker(cmdArgs.getCommentMarker());
                dataReader.setMissingValueMarker(cmdArgs.getMissingValueMarker());
                dataReader.setQuoteCharacter(cmdArgs.getQuoteChar());

                logStart(dataFile, out);
                Dataset dataset = dataReader.readInData(excludeVars);
                logFinish(dataFile, out);

                dataModels.add(DataConvertUtils.toDataModel(dataset));
            }
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
                parameters.set(k, Boolean.TRUE);
            } else if (obj instanceof String) {
                parameters.set(k, v);
            }
        });

        return parameters;
    }

}
