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
package edu.pitt.dbmi.causal.cmd.tetrad;

import edu.cmu.tetrad.util.ParamDescription;
import edu.cmu.tetrad.util.ParamDescriptions;
import edu.cmu.tetrad.util.Parameters;
import edu.pitt.dbmi.causal.cmd.CmdArgs;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The class {@code Tetrad} is a utility class for getting Tetrad parameters.
 *
 * Jan 14, 2019 4:27:15 PM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public final class Tetrad {

    private static final Logger LOGGER = LoggerFactory.getLogger(Tetrad.class);

    private Tetrad() {
    }

    /**
     * Get Tetrad parameters from command-line input.
     *
     * @param cmdArgs command-line arguments
     * @return Tetrad parameters
     */
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
