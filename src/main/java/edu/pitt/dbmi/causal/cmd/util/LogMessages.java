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
package edu.pitt.dbmi.causal.cmd.util;

import edu.cmu.tetrad.data.BoxDataSet;
import edu.cmu.tetrad.data.CovarianceMatrix;
import edu.cmu.tetrad.data.DataModel;
import edu.pitt.dbmi.data.reader.validation.ValidationCode;
import edu.pitt.dbmi.data.reader.validation.ValidationResult;
import java.io.PrintStream;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;

/**
 * The class {@code LogMessages} is a utility class for writing out log
 * messages.
 *
 * Jan 11, 2019 11:39:19 AM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public final class LogMessages {

    private LogMessages() {
    }

    /**
     * Write out dataset information to log file.
     *
     * @param file dataset file to log information about
     * @param dataModel dataset read in from dataset file
     * @param logger write log message to
     * @param out output stream to write message to
     */
    public static void dataInfo(Path file, DataModel dataModel, Logger logger, PrintStream out) {
        int row = 0;
        int col = 0;
        if (dataModel instanceof BoxDataSet) {
            BoxDataSet boxDataSet = (BoxDataSet) dataModel;
            row = boxDataSet.getNumRows();
            col = boxDataSet.getNumColumns();
        } else if (dataModel instanceof CovarianceMatrix) {
            CovarianceMatrix covMatrix = (CovarianceMatrix) dataModel;
            row = covMatrix.getSampleSize();
            col = covMatrix.getDimension();
        }

        String fileName = file.getFileName().toString();
        String msg = String.format("File %s contains %d cases, %d variables.", fileName, row, col);
        logMessage(msg, logger, out);
    }

    /**
     * Log the end time of reading file.
     *
     * @param file file to log information about
     * @param logger write log message to
     * @param out output stream to write message to
     */
    public static void readingFileEnd(Path file, Logger logger, PrintStream out) {
        String fileName = file.getFileName().toString();
        String msg = String.format("Finished reading in file %s.", fileName);
        logMessage(msg, logger, out);
    }

    /**
     * Log the start time of reading file.
     *
     * @param file file to log information about
     * @param logger write log message to
     * @param out output stream to write message to
     */
    public static void readingFileStart(Path file, Logger logger, PrintStream out) {
        String fileName = file.getFileName().toString();
        String msg = String.format("Start reading in file %s.", fileName);
        logMessage(msg, logger, out);
    }

    /**
     * Log data validation results.
     *
     * @param groupedResults validation results
     * @param logger write log message to
     * @param out output stream to write message to
     */
    public static void dataValidationResults(Map<ValidationCode, List<ValidationResult>> groupedResults, Logger logger, PrintStream out) {
        if (groupedResults.containsKey(ValidationCode.INFO)) {
            groupedResults.get(ValidationCode.INFO).stream()
                    .map(e -> e.getMessage())
                    .forEach(e -> {
                        logger.info(e);
                        out.println(e);
                    });
        }
        if (groupedResults.containsKey(ValidationCode.WARNING)) {
            groupedResults.get(ValidationCode.WARNING).stream()
                    .map(e -> e.getMessage())
                    .forEach(e -> {
                        logger.warn(e);
                        out.println(e);
                    });
        }
        if (groupedResults.containsKey(ValidationCode.ERROR)) {
            groupedResults.get(ValidationCode.ERROR).stream()
                    .map(e -> e.getMessage())
                    .forEach(e -> {
                        logger.error(e);
                        out.println(e);
                    });
        }
    }

    /**
     * Log the end time of the data validation.
     *
     * @param file data file to log information about
     * @param logger write log message to
     * @param out output stream to write message to
     */
    public static void dataValidationEnd(Path file, Logger logger, PrintStream out) {
        String fileName = file.getFileName().toString();
        String msg = String.format("End data validation on file %s.", fileName);
        logMessage(msg, logger, out);
    }

    /**
     * Log the start time of the data validation.
     *
     * @param file data file to log information about
     * @param logger write log message to
     * @param out output stream to write message to
     */
    public static void dataValidationStart(Path file, Logger logger, PrintStream out) {
        String fileName = file.getFileName().toString();
        String msg = String.format("Start data validation on file %s.", fileName);
        logMessage(msg, logger, out);
    }

    /**
     * Log message.
     *
     * @param message message to write to log file
     * @param logger write log message to
     * @param out output stream to write message to
     */
    public static void logMessage(String message, Logger logger, PrintStream out) {
        out.printf("%s: %s%n", DateTime.printNow(), message);
        logger.info(message);
    }

}
