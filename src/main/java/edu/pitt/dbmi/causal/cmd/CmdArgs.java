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

    protected Map<String, String> parameters;

    public CmdArgs() {
    }

    public List<Path> getDatasetFiles() {
        return datasetFiles;
    }

    public void setDatasetFiles(List<Path> datasetFiles) {
        this.datasetFiles = datasetFiles;
    }

    public Path getKnowledgeFile() {
        return knowledgeFile;
    }

    public void setKnowledgeFile(Path knowledgeFile) {
        this.knowledgeFile = knowledgeFile;
    }

    public Path getExcludeVariableFile() {
        return excludeVariableFile;
    }

    public void setExcludeVariableFile(Path excludeVariableFile) {
        this.excludeVariableFile = excludeVariableFile;
    }

    public Path getOutDirectory() {
        return outDirectory;
    }

    public void setOutDirectory(Path outDirectory) {
        this.outDirectory = outDirectory;
    }

    public String getFilePrefix() {
        return filePrefix;
    }

    public void setFilePrefix(String filePrefix) {
        this.filePrefix = filePrefix;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public char getQuoteChar() {
        return quoteChar;
    }

    public void setQuoteChar(char quoteChar) {
        this.quoteChar = quoteChar;
    }

    public String getMissingValueMarker() {
        return missingValueMarker;
    }

    public void setMissingValueMarker(String missingValueMarker) {
        this.missingValueMarker = missingValueMarker;
    }

    public String getCommentMarker() {
        return commentMarker;
    }

    public void setCommentMarker(String commentMarker) {
        this.commentMarker = commentMarker;
    }

    public DataType getDataType() {
        return dataType;
    }

    public void setDataType(DataType dataType) {
        this.dataType = dataType;
    }

    public Delimiter getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(Delimiter delimiter) {
        this.delimiter = delimiter;
    }

    public Class getAlgorithmClass() {
        return algorithmClass;
    }

    public void setAlgorithmClass(Class algorithmClass) {
        this.algorithmClass = algorithmClass;
    }

    public Class getScoreClass() {
        return scoreClass;
    }

    public void setScoreClass(Class scoreClass) {
        this.scoreClass = scoreClass;
    }

    public Class getTestClass() {
        return testClass;
    }

    public void setTestClass(Class testClass) {
        this.testClass = testClass;
    }

    public int getNumCategories() {
        return numCategories;
    }

    public void setNumCategories(int numCategories) {
        this.numCategories = numCategories;
    }

    public boolean isSkipValidation() {
        return skipValidation;
    }

    public void setSkipValidation(boolean skipValidation) {
        this.skipValidation = skipValidation;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, String> parameters) {
        this.parameters = parameters;
    }

}
