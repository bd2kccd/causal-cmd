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

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * Mar 20, 2019 3:12:52 PM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
final class TestFiles {

    public static final String CONTINUOUS_DATA_JESSI = new File(TestFiles.class
            .getResource("/data/1000_nodes_400_edges_deg_1.txt").getFile()).getAbsolutePath();


    public static final String CONTINUOUS_DATA = new File(TestFiles.class
            .getResource("/data/sim_data_continuous_20var_100case.txt").getFile()).getAbsolutePath();

    public static final String CONTINUOUS_10VAR_1KCASE_MISSING_DATA = new File(TestFiles.class
            .getResource("/data/sim_data_continuous_10var_1kcase_missing.csv").getFile()).getAbsolutePath();

    public static final String CONTINUOUS_DATA_NO_HEADER = new File(TestFiles.class
            .getResource("/data/sim_data_continuous_20var_100case_no_header.txt").getFile()).getAbsolutePath();

    public static final String CONTINUOUS_DATA_20K_PART1 = new File(TestFiles.class
            .getResource("/data/sim_data_continuous_20var_100case_part_1.txt").getFile()).getAbsolutePath();

    public static final String CONTINUOUS_DATA_20K_PART2 = new File(TestFiles.class
            .getResource("/data/sim_data_continuous_20var_100case_part_2.txt").getFile()).getAbsolutePath();

    public static final String DISCRETE_DATA = new File(TestFiles.class
            .getResource("/data/sim_data_discrete_20var_100case.txt").getFile()).getAbsolutePath();

    public static final String MIXED_DATA = new File(TestFiles.class
            .getResource("/data/sim_data_mixed_20var_100case.txt").getFile()).getAbsolutePath();

    public static final String COVARIANCE_CONTINUOUS_DATA = new File(TestFiles.class
            .getResource("/data/covariance_sim_data_continuous_20var_100case.txt").getFile()).getAbsolutePath();

    public static final String KNOWLEDGE_CONTINUOUS_DATA = new File(TestFiles.class
            .getResource("/data/knowledge_sim_data_continuous_20var_100case.txt").getFile()).getAbsolutePath();

    public static final String EXCLUDE_VARIABLES = new File(TestFiles.class
            .getResource("/data/exclude_vars.txt").getFile()).getAbsolutePath();

    public static final String CONTINUOUS_INTERVENTIONAL_DATA = new File(TestFiles.class
            .getResource("/data/metadata/sim_continuous_intervention.txt").getFile()).getAbsolutePath();

    public static final String DISCRETE_INTERVENTIONAL_DATA = new File(TestFiles.class
            .getResource("/data/metadata/sim_discrete_intervention.txt").getFile()).getAbsolutePath();

    public static final String MIXED_INTERVENTIONAL_DATA = new File(TestFiles.class
            .getResource("/data/metadata/sim_mixed_intervention.txt").getFile()).getAbsolutePath();

    public static final String CONTINUOUS_INTERVENTIONAL_METADATA = new File(TestFiles.class
            .getResource("/data/metadata/sim_continuous_intervention_metadata.json").getFile()).getAbsolutePath();

    public static final String DISCRETE_INTERVENTIONAL_METADATA = new File(TestFiles.class
            .getResource("/data/metadata/sim_discrete_intervention_metadata.json").getFile()).getAbsolutePath();

    public static final String MIXED_INTERVENTIONAL_METADATA = new File(TestFiles.class
            .getResource("/data/metadata/sim_mixed_intervention_metadata.json").getFile()).getAbsolutePath();

    private TestFiles() {
    }

    public static Path createSubDir(Path dir, String name) throws IOException {
        return Files.createDirectory(Paths.get(dir.toString(), name));
    }

    public static List<String> readFileLineByLine(Path file) {
        List<String> allLines = new LinkedList<>();

        try {
            allLines.addAll(Files.readAllLines(file));
        } catch (IOException exception) {
        }

        return allLines;
    }

}
