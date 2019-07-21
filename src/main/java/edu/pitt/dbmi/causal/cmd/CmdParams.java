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
package edu.pitt.dbmi.causal.cmd;

/**
 *
 * Sep 7, 2017 4:52:54 PM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public final class CmdParams {

    // user guide
    public static final String HELP = "help";
    public static final String HELP_ALL = "help-all";
    public static final String VERSION = "version";

    public static final String SKIP_VALIDATION = "skip-validation";
    public static final String SKIP_LATEST = "skip-latest";

    // output
    public static final String FILE_PREFIX = "prefix";
    public static final String JSON_GRAPH = "json-graph";
    public static final String DIR_OUT = "out";

    // file
    public static final String DATASET = "dataset";
    public static final String KNOWLEDGE = "knowledge";
    public static final String EXCLUDE_VARIABLE = "exclude-var";
    public static final String METADATA = "metadata";

    // options for dataset files
    public static final String DELIMITER = "delimiter";
    public static final String QUOTE_CHAR = "quote-char";
    public static final String MISSING_MARKER = "missing-marker";
    public static final String COMMENT_MARKER = "comment-marker";
    public static final String NO_HEADER = "no-header";

    public static final String ALGORITHM = "algorithm";
    public static final String DATA_TYPE = "data-type";
    public static final String TEST = "test";
    public static final String SCORE = "score";

    public static final String NUM_CATEGORIES = "numCategories";
    public static final String TARGET_NAME = "targetName";

    public static final String THREAD = "thread";

    // graph manipulations
    public static final String CHOOSE_DAG_IN_PATTERN = "choose-dag-in-pattern";
    public static final String CHOOSE_MAG_IN_PAG = "choose-mag-in-pag";
    public static final String GENERATE_PATTERN_FROM_DAG = "genereate-pattern-from-dag";
    public static final String GENERATE_PAG_FROM_DAG = "genereate-pag-from-dag";
    public static final String GENERATE_PAG_FROM_TSDAG = "genereate-pag-from-tsdag";
    public static final String MAKE_BIDIRECTED_UNDIRECTED = "make-bidirected-undirected";
    public static final String MAKE_UNDIRECTED_BIDIRECTED = "make-undirected-bidirected";
    public static final String MAKE_ALL_EDGES_UNDIRECTED = "make-all-edges-undirected";
    public static final String GENEREATE_COMPLETE_GRAPH = "generate-complete-graph";
    public static final String EXTRACT_STRUCT_MODEL = "extract-struct-model";

    public static final String EXPERIMENTAL = "experimental";

    private CmdParams() {
    }

}
