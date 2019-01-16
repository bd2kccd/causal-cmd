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

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 *
 * Jan 16, 2019 3:42:59 PM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public class CausalCmdApplicationMetadataTest {

    @Rule
    public final TemporaryFolder tmpFolder = new TemporaryFolder();

    private final Path continuousDataFile = Paths.get(getClass().getResource("/data/metadata/sim_continuous_intervention.txt").getFile());
    private final Path discreteDataFile = Paths.get(getClass().getResource("/data/metadata/sim_discrete_intervention.txt").getFile());
    private final Path mixedDataFile = Paths.get(getClass().getResource("/data/metadata/sim_mixed_intervention.txt").getFile());

    private final Path continuousMetadataFile = Paths.get(getClass().getResource("/data/metadata/sim_continuous_intervention_metadata.json").getFile());
    private final Path discreteMetadataFile = Paths.get(getClass().getResource("/data/metadata/sim_discrete_intervention_metadata.json").getFile());
    private final Path mixedMetadataFile = Paths.get(getClass().getResource("/data/metadata/sim_mixed_intervention_metadata.json").getFile());

    public CausalCmdApplicationMetadataTest() {
    }

    @Test
    public void testGFCIWithMixedData() throws IOException {
        String dataset = mixedDataFile.toString();
        String metadata = mixedMetadataFile.toString();
        String dirOut = tmpFolder.newFolder("gfci").toString();
        String[] args = {
            "--dataset", dataset,
            "--metadata", metadata,
            "--missing-marker", "*",
            "--delimiter", "tab",
            "--data-type", "mixed",
            "--numCategories", "4",
            "--algorithm", "gfci",
            "--test", "cond-gauss-lrt",
            "--score", "cond-gauss-bic",
            "--verbose",
            "--skip-latest",
            "--out", dirOut
        };
        CausalCmdApplication.main(args);
    }

    @Test
    public void testGFCIWithDiscreteData() throws IOException {
        String dataset = discreteDataFile.toString();
        String metadata = discreteMetadataFile.toString();
        String dirOut = tmpFolder.newFolder("gfci").toString();
        String[] args = {
            "--dataset", dataset,
            "--metadata", metadata,
            "--delimiter", "tab",
            "--data-type", "discrete",
            "--algorithm", "gfci",
            "--test", "bdeu",
            "--score", "bdeu",
            "--verbose",
            "--skip-latest",
            "--out", dirOut
        };
        CausalCmdApplication.main(args);
    }

    @Test
    public void testGFCIWithContinuousData() throws IOException {
        String dataset = continuousDataFile.toString();
        String metadata = continuousMetadataFile.toString();
        String dirOut = tmpFolder.newFolder("gfci").toString();
        String[] args = {
            "--dataset", dataset,
            "--metadata", metadata,
            "--delimiter", "tab",
            "--data-type", "continuous",
            "--algorithm", "gfci",
            "--test", "sem-bic",
            "--score", "fisher-z",
            "--verbose",
            "--skip-latest",
            "--out", dirOut
        };
        CausalCmdApplication.main(args);
    }

}
