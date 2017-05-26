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
package edu.pitt.dbmi.causal.cmd.opt;

import edu.cmu.tetrad.util.ParamDescription;
import edu.cmu.tetrad.util.ParamDescriptions;
import edu.pitt.dbmi.causal.cmd.ParamAttrs;
import edu.pitt.dbmi.causal.cmd.util.Args;
import org.apache.commons.cli.CommandLine;

/**
 *
 * Mar 9, 2017 3:36:02 PM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public class CmdLongOpts {

    private static final ParamDescriptions PARAM_DESCRIPTIONS = ParamDescriptions.instance();

    private CmdLongOpts() {
    }

    public static final String DATA = "data";

    public static final String KNOWLEDGE = "knowledge";
    public static final String EXCLUDE_VARS = "exclude-variables";
    public static final String DELIMITER = "delimiter";
    public static final String VERBOSE = "verbose";
    public static final String THREAD = "thread";
    public static final String JSON = "json";
    public static final String TETRAD_GRAPH_JSON = "tetrad-graph-json";
    public static final String OUT = "out";
    public static final String OUTPUT_PREFIX = "output-prefix";
    public static final String NO_VALIDATION_OUTPUT = "no-validation-output";
    public static final String HELP = "help";
    public static final String SKIP_LATEST = "skip-latest";

    public static final String PENALTY_DISCOUNT = "penalty-discount";
    public static final String MAX_DEGREE = "max-degree";
    public static final String MAX_INDEGREE = "max-indegree";
    public static final String MAX_OUTDEGREE = "max-outdegree";
    public static final String MAX_PATH_LENGTH = "max-path-length";
    public static final String FAITHFULNESS_ASSUMED = "faithfulness-assumed";
    public static final String SYMMETRIC_FIRST_STEP = "symmetric-first-step";
    public static final String ALPHA = "alpha";
    public static final String STRUCTURE_PRIOR = "structure-prior";
    public static final String SAMPLE_PRIOR = "sample-prior";
    public static final String COMPLETE_RULE_SET_USED = "use-complete-rule-set";

    public static final String LATENT = "latent";
    public static final String AVG_DEGREE = "avg-degree";
    public static final String CONNECTED = "connected";

    public static final String MIN_CATEGORIES = "min-categories";
    public static final String MAX_CATEGORIES = "max-categories";
    public static final String NUM_CATEGORIES_TO_DISCRETIZE = "num-categories-to-discretize";

    public static final String DISCRETIZE = "discretize";

    public static final String PERCENT_DISCRETE = "percent-discrete";

    public static final String SKIP_UNIQUE_VAR_NAME = "skip-unique-var-name";
    public static final String SKIP_NONZERO_VARIANCE = "skip-nonzero-variance";
    public static final String SKIP_CATEGORY_LIMIT = "skip-category-limit";

    public static int getInt(String cmdOption, String paramAttr, CommandLine cmd) {
        ParamDescription paramDesc = PARAM_DESCRIPTIONS.get(paramAttr);
        String defaultValue = paramDesc.getDefaultValue().toString();
        int minValue = paramDesc.getLowerBoundInt();
        int maxValue = paramDesc.getUpperBoundInt();

        return Args.getIntegerMinMax(cmd.getOptionValue(cmdOption, defaultValue), minValue, maxValue);
    }

    public static double getDouble(String cmdOption, String paramAttr, CommandLine cmd) {
        ParamDescription paramDesc = PARAM_DESCRIPTIONS.get(paramAttr);
        String defaultValue = paramDesc.getDefaultValue().toString();
        double minValue = paramDesc.getLowerBoundDouble();
        double maxValue = paramDesc.getUpperBoundDouble();

        return Args.getDoubleMinMax(cmd.getOptionValue(cmdOption, defaultValue), minValue, maxValue);
    }

    public static String createDescription(String paramAttr) {
        ParamDescription paramDesc = PARAM_DESCRIPTIONS.get(paramAttr);

        return String.format("%s. Default is %s.", paramDesc.getDescription(), paramDesc.getDefaultValue());
    }

    public static String getDescription(String cmdOption) {
        switch (cmdOption) {
            case PENALTY_DISCOUNT:
                return createDescription(ParamAttrs.PENALTY_DISCOUNT);
            case MAX_DEGREE:
                return createDescription(ParamAttrs.MAX_DEGREE);
            case MAX_INDEGREE:
                return createDescription(ParamAttrs.MAX_INDEGREE);
            case MAX_OUTDEGREE:
                return createDescription(ParamAttrs.MAX_OUTDEGREE);
            case MAX_PATH_LENGTH:
                return createDescription(ParamAttrs.MAX_PATH_LENGTH);
            case FAITHFULNESS_ASSUMED:
                return createDescription(ParamAttrs.FAITHFULNESS_ASSUMED);
            case ALPHA:
                return createDescription(ParamAttrs.ALPHA);
            case STRUCTURE_PRIOR:
                return createDescription(ParamAttrs.STRUCTURE_PRIOR);
            case SAMPLE_PRIOR:
                return createDescription(ParamAttrs.SAMPLE_PRIOR);
            case LATENT:
                return createDescription(ParamAttrs.NUM_LATENTS);
            case AVG_DEGREE:
                return createDescription(ParamAttrs.AVG_DEGREE);
            case CONNECTED:
                return createDescription(ParamAttrs.CONNECTED);
            case SYMMETRIC_FIRST_STEP:
                return createDescription(ParamAttrs.SYMMETRIC_FIRST_STEP);
            case DISCRETIZE:
                return createDescription(ParamAttrs.DISCRETIZE);
            case NUM_CATEGORIES_TO_DISCRETIZE:
                return createDescription(ParamAttrs.NUM_CATEGORIES_TO_DISCRETIZE);
            case SKIP_UNIQUE_VAR_NAME:
                return "Skip check for unique variable names.";
            case SKIP_NONZERO_VARIANCE:
                return "Skip check for zero variance variables.";
            case SKIP_CATEGORY_LIMIT:
                return "Skip 'limit number of categories' check.";
            default:
                return "";
        }
    }

}
