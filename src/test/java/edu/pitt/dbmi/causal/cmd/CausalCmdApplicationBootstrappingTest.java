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

import java.nio.file.Path;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

/**
 *
 * Jan 13, 2023 12:16:29 AM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class CausalCmdApplicationBootstrappingTest {

    @TempDir
    public static Path tempDir;

    @Test
    public void testFgesBootstrappingWithContinuousData() throws Exception {
        String dataset = TestFiles.CONTINUOUS_DATA_JESSI;
        String dirOut = TestFiles.createSubDir(tempDir, "fges_bootstrapping_jessi").toString();
        String[] args = {
            "--dataset", dataset,
            "--delimiter", "tab",
            "--data-type", "continuous",
            "--algorithm", "fges",
            "--score", "ebic-score",
            "--default",
            "--prefix", "fges-bootstrapping",
            "--numberResampling", "1",
            "--percentResampleSize", "100",
            "--seed", "1673588774198",
            "--resamplingEnsemble", "2",
            "--out", dirOut
        };
        long startTime = System.nanoTime();

        CausalCmdApplication.main(args);

        long endTime = System.nanoTime();

        long duration = (endTime - startTime) / 1000000000;  //divide by 1000000 to get milliseconds.

        System.out.println(
                "Total time: " + duration + " seconds"
        );

    }

}
