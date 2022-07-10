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
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 *
 * Sep 26, 2017 3:43:19 PM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public class CausalCmdApplicationTest {

    @TempDir
    public static Path tempDir;

    @Disabled
    @Test
    public void testExperimentalAlgorithm() throws IOException {
        String dataset = TestFiles.DISCRETE_DATA;
        String dirOut = TestFiles.createSubDir(tempDir, "rfci-bsc_discrete").toString();
        String[] args = {
            "--dataset", dataset,
            "--delimiter", "tab",
            "--data-type", "discrete",
            "--algorithm", "rfci-bsc",
            "--default",
            "--experimental",
            "--out", dirOut
        };
        CausalCmdApplication.main(args);
    }

    @Test
    public void testGFCIWithContinuousMissingData() throws IOException {
        String dataset = TestFiles.CONTINUOUS_10VAR_1KCASE_MISSING_DATA;
        String dirOut = TestFiles.createSubDir(tempDir, "gfci_missing_values").toString();
        String[] args = {
            "--dataset", dataset,
            "--delimiter", "comma",
            "--data-type", "continuous",
            "--algorithm", "gfci",
            "--test", "fisher-z-test",
            "--score", "sem-bic-score",
            "--missing", "*",
            "--default",
            "--out", dirOut
        };
        CausalCmdApplication.main(args);
    }

    @Test
    public void testGFCIWithContinuousDataAndChooseMagInPag() throws IOException {
        String dataset = TestFiles.CONTINUOUS_DATA;
        String dirOut = TestFiles.createSubDir(tempDir, "gfci_choose_dag_in_pattern").toString();
        String[] args = {
            "--dataset", dataset,
            "--delimiter", "tab",
            "--data-type", "continuous",
            "--algorithm", "gfci",
            "--test", "fisher-z-test",
            "--score", "sem-bic-score",
            "--default",
            "--choose-mag-in-pag",
            "--out", dirOut
        };
        CausalCmdApplication.main(args);
    }

    @Test
    public void testFaskVoteWithMultipleContinuousData() throws IOException {
        String dataset1 = TestFiles.CONTINUOUS_DATA_20K_PART1;
        String dataset2 = TestFiles.CONTINUOUS_DATA_20K_PART2;
        String dirOut = TestFiles.createSubDir(tempDir, "fask-vote_cont").toString();
        String[] args = {
            "--dataset", dataset1 + "," + dataset2,
            "--delimiter", "tab",
            "--data-type", "continuous",
            "--algorithm", "fask-vote",
            "--test", "fisher-z-test",
            "--score", "sem-bic-score",
            "--default",
            "--out", dirOut
        };
        CausalCmdApplication.main(args);
    }

    @Test
    public void testGFCIWithContinuousDataAndVariablesToExclude() throws IOException {
        String dataset = TestFiles.CONTINUOUS_DATA;
        String excludeVar = TestFiles.EXCLUDE_VARIABLES;
        String dirOut = TestFiles.createSubDir(tempDir, "gfci_exclude_vars").toString();
        String[] args = {
            "--dataset", dataset,
            "--exclude-var", excludeVar,
            "--delimiter", "tab",
            "--data-type", "continuous",
            "--algorithm", "gfci",
            "--test", "fisher-z-test",
            "--score", "sem-bic-score",
            "--default",
            "--out", dirOut
        };
        CausalCmdApplication.main(args);
    }

    @Test
    public void testFGESCovariance() throws IOException {
        String dataset = TestFiles.COVARIANCE_CONTINUOUS_DATA;
        String dirOut = TestFiles.createSubDir(tempDir, "gfci_covar").toString();
        String[] args = {
            "--dataset", dataset,
            "--delimiter", "tab",
            "--data-type", "covariance",
            "--algorithm", "gfci",
            "--test", "fisher-z-test",
            "--score", "sem-bic-score",
            "--default",
            "--out", dirOut
        };
        CausalCmdApplication.main(args);
    }

    @Test
    public void testGFCIWithContinuousDataAndKnowledge() throws IOException {
        String dataset = TestFiles.CONTINUOUS_DATA;
        String knowledge = TestFiles.KNOWLEDGE_CONTINUOUS_DATA;
        String dirOut = TestFiles.createSubDir(tempDir, "gfci_knowledge").toString();
        String[] args = {
            "--dataset", dataset,
            "--knowledge", knowledge,
            "--delimiter", "tab",
            "--data-type", "continuous",
            "--algorithm", "gfci",
            "--test", "fisher-z-test",
            "--score", "sem-bic-score",
            "--default",
            "--out", dirOut
        };
        CausalCmdApplication.main(args);
    }

    @Test
    public void testGFCIWithNoHeaderContinuousData() throws IOException {
        String dataset = TestFiles.CONTINUOUS_DATA_NO_HEADER;
        String dirOut = TestFiles.createSubDir(tempDir, "gfci_cont_no_header").toString();
        String[] args = {
            "--dataset", dataset,
            "--no-header",
            "--delimiter", "tab",
            "--data-type", "continuous",
            "--algorithm", "gfci",
            "--test", "fisher-z-test",
            "--score", "sem-bic-score",
            "--default",
            "--out", dirOut
        };
        CausalCmdApplication.main(args);
    }

    @Test
    public void testGFCIWithMixedData() throws IOException {
        String dataset = TestFiles.MIXED_DATA;
        String dirOut = TestFiles.createSubDir(tempDir, "gfci_mixed").toString();
        String[] args = {
            "--dataset", dataset,
            "--delimiter", "tab",
            "--data-type", "mixed",
            "--numCategories", "4",
            "--algorithm", "gfci",
            "--test", "cg-lr-test",
            "--score", "cg-bic-score",
            "--default",
            "--out", dirOut
        };
        CausalCmdApplication.main(args);
    }

    @Test
    public void testGFCIWithDiscreteData() throws IOException {
        String dataset = TestFiles.DISCRETE_DATA;
        String dirOut = TestFiles.createSubDir(tempDir, "gfci_discrete").toString();
        String[] args = {
            "--dataset", dataset,
            "--delimiter", "tab",
            "--data-type", "discrete",
            "--algorithm", "gfci",
            "--test", "g-square-test",
            "--score", "bdeu-score",
            "--faithfulnessAssumed",
            "--default",
            "--out", dirOut
        };
        CausalCmdApplication.main(args);
    }

    @Test
    public void testGFCIWithContinuousData() throws IOException {
        String dataset = TestFiles.CONTINUOUS_DATA;
        String dirOut = TestFiles.createSubDir(tempDir, "gfci_cont").toString();
        String[] args = {
            "--dataset", dataset,
            "--delimiter", "tab",
            "--data-type", "continuous",
            "--algorithm", "gfci",
            "--test", "fisher-z-test",
            "--score", "sem-bic-score",
            "--penaltyDiscount", "1.0",
            "--semBicStructurePrior", "0.01",
            "--default",
            "--out", dirOut
        };
        CausalCmdApplication.main(args);
    }

    @Test
    public void testGRaSPWithContinuousData() throws IOException {
        String dataset = TestFiles.CONTINUOUS_DATA;
        String dirOut = TestFiles.createSubDir(tempDir, "grasp_cont").toString();
        String[] args = {
            "--dataset", dataset,
            "--delimiter", "tab",
            "--data-type", "continuous",
            "--algorithm", "grasp",
            "--test", "fisher-z-test",
            "--score", "sem-bic-score",
            "--default",
            "--out", dirOut
        };
        CausalCmdApplication.main(args);
    }

}
