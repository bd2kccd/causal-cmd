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
    public TemporaryFolder tmpFolder = new TemporaryFolder();

    public CausalCmdApplicationTest() {
    }

    @Test
    public void testMainFgesMb() throws IOException {
        String contData = TestFiles.getInstance().getContinuousData().toString();
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
        String covarData = TestFiles.getInstance().getCovarianceData().toString();
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
        String contData = TestFiles.getInstance().getContinuousData().toString();
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
        String contData = TestFiles.getInstance().getContinuousData().toString();
        String knowledge = TestFiles.getInstance().getKnowledge().toString();
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
