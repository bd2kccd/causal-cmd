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
import edu.pitt.dbmi.data.Delimiter;
import java.nio.file.Path;
import java.util.List;
import java.util.Map;

/**
 *
 * Sep 24, 2017 10:10:21 PM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public class CmdArgs {

    protected List<Path> datasetFiles;
    protected Path knowledgeFile;
    protected Path excludeVariableFile;
    protected Path outDirectory;

    protected String filePrefix;
    protected String fileName;

    protected char quoteChar;
    protected String missingValueMarker;
    protected String commentMarker;

    protected DataType dataType;
    protected Delimiter delimiter;

    protected Class algorithmClass;
    protected Class scoreClass;
    protected Class testClass;

    protected int numCategories;

    protected boolean skipValidation;
    protected boolean json;

    protected Map<String, String> parameters;

    public CmdArgs() {
    }

    public List<Path> getDatasetFiles() {
        return datasetFiles;
    }

    public Path getKnowledgeFile() {
        return knowledgeFile;
    }

    public Path getExcludeVariableFile() {
        return excludeVariableFile;
    }

    public Path getOutDirectory() {
        return outDirectory;
    }

    public String getFilePrefix() {
        return filePrefix;
    }

    public String getFileName() {
        return fileName;
    }

    public char getQuoteChar() {
        return quoteChar;
    }

    public String getMissingValueMarker() {
        return missingValueMarker;
    }

    public String getCommentMarker() {
        return commentMarker;
    }

    public DataType getDataType() {
        return dataType;
    }

    public Delimiter getDelimiter() {
        return delimiter;
    }

    public Class getAlgorithmClass() {
        return algorithmClass;
    }

    public Class getScoreClass() {
        return scoreClass;
    }

    public Class getTestClass() {
        return testClass;
    }

    public int getNumCategories() {
        return numCategories;
    }

    public boolean isSkipValidation() {
        return skipValidation;
    }

    public boolean isJson() {
        return json;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

}
