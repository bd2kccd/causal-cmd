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
package edu.pitt.dbmi.causal.cmd.sim;

/**
 *
 * Mar 10, 2017 12:55:49 PM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public enum DataSimulationType {

    SEM_RAND_FWD("Sem-Random Foward Continuous Data Simulation", "sem-rand-fwd"),
    BAYES_NET_RAND_FWD("Bayes Net-Random Foward Discrete Data Simulation", "bayes-net-rand-fwd"),
    LEE_HASTIE("Lee-Hastie Mixed Data Simulation", "lee-hastie");

    private final String title;

    private final String cmd;

    private DataSimulationType(String title, String cmd) {
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
