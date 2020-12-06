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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 *
 * Jan 16, 2019 3:42:59 PM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public class CausalCmdApplicationMetadataTest {

    @TempDir
    public static Path tempDir;

    @Test
    public void testGFCIWithMixedData() throws IOException {
        String dataset = TestFiles.MIXED_INTERVENTIONAL_DATA;
        String metadata = TestFiles.MIXED_INTERVENTIONAL_METADATA;
        String dirOut = TestFiles.createSubDir(tempDir, "gfci_interv_mixed").toString();
        String[] args = {
            "--dataset", dataset,
            "--metadata", metadata,
            "--missing-marker", "*",
            "--delimiter", "tab",
            "--data-type", "mixed",
            "--numCategories", "4",
            "--algorithm", "gfci",
            "--test", "cg-lr-test",
            "--score", "cg-bic-score",
            "--verbose",
            "--skip-latest",
            "--out", dirOut
        };
        CausalCmdApplication.main(args);
    }

    @Test
    public void testGFCIWithDiscreteData() throws IOException {
        String dataset = TestFiles.DISCRETE_INTERVENTIONAL_DATA;
        String metadata = TestFiles.DISCRETE_INTERVENTIONAL_METADATA;
        String dirOut = TestFiles.createSubDir(tempDir, "gfci_interv_discrete").toString();
        String[] args = {
            "--dataset", dataset,
            "--metadata", metadata,
            "--delimiter", "tab",
            "--data-type", "discrete",
            "--algorithm", "gfci",
            "--test", "g-square-test",
            "--score", "bdeu-score",
            "--verbose",
            "--skip-latest",
            "--out", dirOut
        };
        CausalCmdApplication.main(args);
    }

    @Test
    public void testGFCIWithContinuousData() throws IOException {
        String dataset = TestFiles.CONTINUOUS_INTERVENTIONAL_DATA;
        String metadata = TestFiles.CONTINUOUS_INTERVENTIONAL_METADATA;
        String dirOut = TestFiles.createSubDir(tempDir, "gfci_interv_cont").toString();
        String[] args = {
            "--dataset", dataset,
            "--metadata", metadata,
            "--delimiter", "tab",
            "--data-type", "continuous",
            "--algorithm", "gfci",
            "--test", "fisher-z-test",
            "--score", "sem-bic-score",
            "--verbose",
            "--skip-latest",
            "--out", dirOut
        };
        CausalCmdApplication.main(args);
    }

}
