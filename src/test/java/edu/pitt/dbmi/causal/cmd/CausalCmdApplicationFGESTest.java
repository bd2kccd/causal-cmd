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

import edu.pitt.dbmi.causal.cmd.algo.AlgorithmType;
import edu.pitt.dbmi.data.Delimiter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

/**
 *
 * Mar 22, 2017 4:14:50 PM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public class CausalCmdApplicationFGESTest {

    @ClassRule
    public static TemporaryFolder tmpDir = new TemporaryFolder();

    public CausalCmdApplicationFGESTest() {
    }

    @AfterClass
    public static void tearDownClass() {
        tmpDir.delete();
    }

    @Test
    public void testFGESd() throws IOException {
        Path dataFile = Paths.get("test", "data", "discrete", "sim_discrete_data_20vars_100cases.txt");
        String delimiter = Delimiter.TAB.getName();
        String algorithm = AlgorithmType.FGESD.getCmd();
        String dirOut = tmpDir.newFolder(algorithm).toString();
        String outputPrefix = algorithm;
        String[] args = {
            "--algorithm", algorithm,
            "--data", dataFile.toString(),
            "--delimiter", delimiter,
            "--out", dirOut,
            "--output-prefix", outputPrefix,
            "--json",
            "--skip-latest"
        };
        CausalCmdApplication.main(args);

        Path outFile = Paths.get(dirOut, outputPrefix + ".txt");
        String errMsg = outFile.getFileName().toString() + " does not exist.";
        Assert.assertTrue(errMsg, Files.exists(outFile, LinkOption.NOFOLLOW_LINKS));

        outFile = Paths.get(dirOut, outputPrefix + "_graph.json");
        errMsg = outFile.getFileName().toString() + " does not exist.";
        Assert.assertTrue(errMsg, Files.exists(outFile, LinkOption.NOFOLLOW_LINKS));
    }

    @Test
    public void testFGESc() throws IOException {
        Path dataFile = Paths.get("test", "data", "cmu", "Retention.txt");
        String delimiter = Delimiter.TAB.getName();
        String maxDegree = "100";
        String penaltyDiscount = "4.0";
        String algorithm = AlgorithmType.FGESC.getCmd();
        String dirOut = tmpDir.newFolder(algorithm).toString();
        String outputPrefix = algorithm;
        String[] args = {
            "--algorithm", algorithm,
            "--data", dataFile.toString(),
            "--delimiter", delimiter,
            "--max-degree", maxDegree,
            "--penalty-discount", penaltyDiscount,
            "--out", dirOut,
            "--output-prefix", outputPrefix,
            "--json",
            "--skip-latest"
        };
        CausalCmdApplication.main(args);

        Path outFile = Paths.get(dirOut, outputPrefix + ".txt");
        String errMsg = outFile.getFileName().toString() + " does not exist.";
        Assert.assertTrue(errMsg, Files.exists(outFile, LinkOption.NOFOLLOW_LINKS));

        outFile = Paths.get(dirOut, outputPrefix + "_graph.json");
        errMsg = outFile.getFileName().toString() + " does not exist.";
        Assert.assertTrue(errMsg, Files.exists(outFile, LinkOption.NOFOLLOW_LINKS));
    }

}
