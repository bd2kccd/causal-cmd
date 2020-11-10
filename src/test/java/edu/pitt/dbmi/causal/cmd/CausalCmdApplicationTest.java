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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 *
 * Sep 26, 2017 3:43:19 PM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public class CausalCmdApplicationTest {

    @Rule
    public final TemporaryFolder tmpFolder = new TemporaryFolder();

    public CausalCmdApplicationTest() {
    }

    @Test
    public void testExperimentalAlgorithm() throws IOException {
        String dataset = TestFiles.DISCRETE_DATA;
        String dirOut = tmpFolder.newFolder("rfci-bsc_discrete").toString();
        String[] args = {
            "--dataset", dataset,
            "--delimiter", "tab",
            "--data-type", "discrete",
            "--algorithm", "rfci-bsc",
            "--verbose",
            "--skip-latest",
            "--experimental",
            "--out", dirOut
        };
        CausalCmdApplication.main(args);
    }

    @Test
    public void testGFCIWithContinuousDataAndChooseMagInPag() throws IOException {
        String dataset = TestFiles.CONTINUOUS_DATA;
        String dirOut = tmpFolder.newFolder("gfci_choose_dag_in_pattern").toString();
        String[] args = {
            "--dataset", dataset,
            "--delimiter", "tab",
            "--data-type", "continuous",
            "--algorithm", "gfci",
            "--test", "fisher-z-test",
            "--score", "sem-bic-score",
            "--verbose",
            "--skip-latest",
            "--choose-mag-in-pag",
            "--out", dirOut
        };
        CausalCmdApplication.main(args);
    }

    @Test
    public void testMultiFaskWithMultipleContinuousData() throws IOException {
        String dataset1 = TestFiles.CONTINUOUS_DATA_20K_PART1;
        String dataset2 = TestFiles.CONTINUOUS_DATA_20K_PART2;
        String dirOut = tmpFolder.newFolder("multi-fask_cont").toString();
        String[] args = {
            "--dataset", dataset1 + "," + dataset2,
            "--delimiter", "tab",
            "--data-type", "continuous",
            "--algorithm", "multi-fask",
                "--test", "fisher-z-test",
            "--verbose",
            "--skip-latest",
            "--out", dirOut
        };
        CausalCmdApplication.main(args);
    }

    @Test
    public void testGFCIWithContinuousDataAndVariablesToExclude() throws IOException {
        String dataset = TestFiles.CONTINUOUS_DATA;
        String excludeVar = TestFiles.EXCLUDE_VARIABLES;
        String dirOut = tmpFolder.newFolder("gfci_exclude_vars").toString();
        String[] args = {
            "--dataset", dataset,
            "--exclude-var", excludeVar,
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

    @Test
    public void testFGESCovariance() throws IOException {
        String dataset = TestFiles.COVARIANCE_CONTINUOUS_DATA;
        String dirOut = tmpFolder.newFolder("gfci_covar").toString();
        String[] args = {
            "--dataset", dataset,
            "--delimiter", "tab",
            "--data-type", "covariance",
            "--algorithm", "gfci",
            "--test", "fisher-z-test",
            "--score", "sem-bic-score",
            "--verbose",
            "--skip-latest",
            "--out", dirOut
        };
        CausalCmdApplication.main(args);
    }

    @Test
    public void testGFCIWithContinuousDataAndKnowledge() throws IOException {
        String dataset = TestFiles.CONTINUOUS_DATA;
        String knowledge = TestFiles.KNOWLEDGE_CONTINUOUS_DATA;
        String dirOut = tmpFolder.newFolder("gfci_knowledge").toString();
        String[] args = {
            "--dataset", dataset,
            "--knowledge", knowledge,
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

    @Test
    public void testGFCIWithNoHeaderContinuousData() throws IOException {
        String dataset = TestFiles.CONTINUOUS_DATA_NO_HEADER;
        String dirOut = tmpFolder.newFolder("gfci_cont_no_header").toString();
        String[] args = {
            "--dataset", dataset,
            "--no-header",
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

    @Test
    public void testGFCIWithMixedData() throws IOException {
        String dataset = TestFiles.MIXED_DATA;
        String dirOut = tmpFolder.newFolder("gfci_mixed").toString();
        String[] args = {
            "--dataset", dataset,
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
        String dataset = TestFiles.DISCRETE_DATA;
        String dirOut = tmpFolder.newFolder("gfci_discrete").toString();
        String[] args = {
            "--dataset", dataset,
            "--delimiter", "tab",
            "--data-type", "discrete",
            "--algorithm", "gfci",
            "--test", "g-square-test",
            "--score", "bdeu-score",
            "--faithfulnessAssumed",
            "--verbose",
            "--skip-latest",
            "--out", dirOut
        };
        CausalCmdApplication.main(args);
    }

    @Test
    public void testGFCIWithContinuousData() throws IOException {
        String dataset = TestFiles.CONTINUOUS_DATA;
        String dirOut = tmpFolder.newFolder("gfci_cont").toString();
        String[] args = {
            "--dataset", dataset,
            "--delimiter", "tab",
            "--data-type", "continuous",
            "--algorithm", "gfci",
            "--test", "fisher-z-test",
            "--score", "sem-bic-score",
            "--penaltyDiscount", "1.0",
            "--structurePrior", "0.01",
            "--verbose",
            "--skip-latest",
            "--out", dirOut
        };
        CausalCmdApplication.main(args);
    }

}
