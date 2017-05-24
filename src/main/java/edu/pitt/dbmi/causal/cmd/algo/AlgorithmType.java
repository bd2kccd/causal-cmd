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
package edu.pitt.dbmi.causal.cmd.algo;

/**
 *
 * Mar 9, 2017 5:43:36 PM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public enum AlgorithmType {

    FGESC("FGES Continuous", "FGESc"),
    FGESD("FGES Discrete", "FGESd"),
    FGESM_CG("FGES Discrete", "FGESm-cg"),
    GFCIC("GFCI Continuous", "GFCIc"),
    GFCID("GFCI Discrete", "GFCId");

    private final String title;

    private final String cmd;

    private AlgorithmType(String title, String cmd) {
        this.title = title;
        this.cmd = cmd;
    }

    public String getTitle() {
        return title;
    }

    public String getCmd() {
        return cmd;
    }

}
