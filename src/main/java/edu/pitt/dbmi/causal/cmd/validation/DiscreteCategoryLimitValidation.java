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
package edu.pitt.dbmi.causal.cmd.validation;

import edu.cmu.tetrad.data.DataSet;
import edu.cmu.tetrad.data.DiscreteVariable;
import edu.cmu.tetrad.graph.Node;
import java.io.PrintStream;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Mar 14, 2017 5:16:22 PM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public class DiscreteCategoryLimitValidation implements TetradDataValidation {

    private static final Logger LOGGER = LoggerFactory.getLogger(DiscreteCategoryLimitValidation.class);

    private final DataSet dataSet;
    private final int categoryLimit;

    public DiscreteCategoryLimitValidation(DataSet dataSet, int categoryLimit) {
        this.dataSet = dataSet;
        this.categoryLimit = categoryLimit;
    }

    @Override
    public boolean validate(PrintStream stderr, boolean verbose) {
        boolean valid = true;

        List<Node> nodes = dataSet.getVariables();
        for (Node node : nodes) {
            if (node instanceof DiscreteVariable) {
                DiscreteVariable discreteVar = (DiscreteVariable) node;
                if (discreteVar.getNumCategories() > categoryLimit) {
                    String errMsg = String.format("Number of categories exceeded. Variable '%s' has %d categories but the limit is %d.", discreteVar.getName(), discreteVar.getNumCategories(), categoryLimit);
                    stderr.println(errMsg);
                    LOGGER.error(errMsg);
                    valid = false && valid;
                }
            } else {
                String errMsg = String.format("Variable '%s' is not discrete.", node.getName());
                stderr.println(errMsg);
                LOGGER.error(errMsg);
                valid = false && valid;
            }
        }

        return valid;
    }

}
