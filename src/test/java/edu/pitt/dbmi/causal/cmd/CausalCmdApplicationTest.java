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
 * Sep 26, 2017 3:43:19 PM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public class CausalCmdApplicationTest {

    @Rule
    public final TemporaryFolder tmpFolder = new TemporaryFolder();

    private final Path continuousDataFile = Paths.get(getClass().getResource("/data/sim_data_continuous_20var_100case.txt").getFile());
    private final Path discreteDataFile = Paths.get(getClass().getResource("/data/sim_data_discrete_20var_100case.txt").getFile());
    private final Path mixedDataFile = Paths.get(getClass().getResource("/data/sim_data_mixed_20var_100case.txt").getFile());

    private final Path noHeaderContinuousDataFile = Paths.get(getClass().getResource("/data/sim_data_continuous_20var_100case_no_header.txt").getFile());

    private final Path covarianceFile = Paths.get(getClass().getResource("/data/spartina.txt").getFile());

    private final Path knowledgeFile = Paths.get(getClass().getResource("/data/knowledge_sim_data_continuous_20var_100case.txt").getFile());

    public CausalCmdApplicationTest() {
    }

    @Test
    public void testMainFgesMb() throws IOException {
        String contData = continuousDataFile.toString();
        String dirOut = tmpFolder.newFolder("fges_mb").toString();
        String[] args = {
            "--dataset", contData,
            "--delimiter", "tab",
            "--data-type", "continuous",
            "--algorithm", "fges-mb",
            "--score", "sem-bic",
            "--targetName", "X1",
            "--verbose",
            "--skip-latest",
            "--out", dirOut
        };
        CausalCmdApplication.main(args);
    }

    @Test
    public void testMainCovariance() throws IOException {
        String covarData = covarianceFile.toString();
        String dirOut = tmpFolder.newFolder("gfci_covar").toString();
        String[] args = {
            "--dataset", covarData,
            "--delimiter", "space",
            "--data-type", "covariance",
            "--algorithm", "gfci",
            "--test", "sem-bic",
            "--score", "sem-bic",
            "--verbose",
            "--skip-latest",
            "--out", dirOut
        };
        CausalCmdApplication.main(args);
    }

    @Test
    public void testMainWithBootstrap() throws IOException {
        String contData = continuousDataFile.toString();
        String dirOut = tmpFolder.newFolder("gfci_bootstrap").toString();
        String[] args = {
            "--resamplingEnsemble", "1",
            "--numberResampling", "5",
            "--percentResampleSize", "100",
            "--dataset", contData,
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

    /**
     * Test of main method, of class CausalCmdApplication.
     *
     * @throws IOException
     */
    @Test
    public void testMain() throws IOException {
        String contData = continuousDataFile.toString();
        String knowledge = knowledgeFile.toString();
        String dirOut = tmpFolder.newFolder("gfci").toString();
        String[] args = {
            "--dataset", contData,
            "--delimiter", "tab",
            "--data-type", "continuous",
            "--algorithm", "gfci",
            "--test", "sem-bic",
            "--score", "fisher-z",
            "--verbose",
            "--maxDegree", "3",
            "--skip-latest",
            "--knowledge", knowledge,
            "--out", dirOut
        };
        CausalCmdApplication.main(args);
    }

}
