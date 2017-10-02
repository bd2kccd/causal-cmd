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

import java.nio.file.Path;
import java.nio.file.Paths;

/**
 *
 * Sep 12, 2017 10:50:55 AM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public class TestFiles {

    private static final TestFiles INSTANCE = new TestFiles();

    private static final String DIR_NAME = "data";
    private static final String CONTINUOUS_DATA = "sim_data_continuous_20var_100case.txt";
    private static final String DISCRETE_DATA = "sim_data_discrete_20var_100case.txt";
    private static final String MIXED_DATA = "sim_data_mixed_20var_100case.txt";
    private static final String COVARIANCE_DATA = "spartina.txt";

    private static final String KNOWLEDGE = "knowledge_sim_data_continuous_20var_100case.txt";

    private static final String EXCLUDE_VAR = "exclude_vars.txt";

    private final String directory;

    private TestFiles() {
        ClassLoader classLoader = getClass().getClassLoader();
        directory = classLoader.getResource(DIR_NAME).getPath();
    }

    public static TestFiles getInstance() {
        return INSTANCE;
    }

    public Path getContinuousData() {
        return Paths.get(directory, CONTINUOUS_DATA);
    }

    public Path getDiscreteData() {
        return Paths.get(directory, DISCRETE_DATA);
    }

    public Path getMixedData() {
        return Paths.get(directory, MIXED_DATA);
    }

    public Path getCovarianceData() {
        return Paths.get(directory, COVARIANCE_DATA);
    }

    public Path getKnowledge() {
        return Paths.get(directory, KNOWLEDGE);
    }

    public Path getExcludeVars() {
        return Paths.get(directory, EXCLUDE_VAR);
    }

}
