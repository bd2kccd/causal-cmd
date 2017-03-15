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
 * Mar 13, 2017 2:02:17 PM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public class CausalCmdApplicationTest {

    @ClassRule
    public static TemporaryFolder tmpDir = new TemporaryFolder();

    public CausalCmdApplicationTest() {
    }

    @AfterClass
    public static void tearDownClass() {
        tmpDir.delete();
    }

    @Test
    public void testGFCIc() throws IOException {
        Path dataFile = Paths.get("test", "data", "continuous", "sim_data_20vars_100cases.txt");
        String delimiter = "tab";
        String algorithm = "gfcic";
        String dirOut = tmpDir.newFolder(algorithm).toString();
        String outputPrefix = algorithm;
        String[] args = {
            "--algorithm", algorithm,
            "--data", dataFile.toString(),
            "--delimiter", delimiter,
            "--alpha", "0.05",
            "--out", dirOut,
            "--output-prefix", outputPrefix,
            "--json",
            "--verbose",
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
    public void testFGESd() throws IOException {
        Path dataFile = Paths.get("test", "data", "discrete", "sim_discrete_data_20vars_100cases.txt");
        String delimiter = "tab";
        String algorithm = "fgesd";
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
        Path dataFile = Paths.get("test", "data", "continuous", "sim_data_20vars_100cases.txt");
        String delimiter = "tab";
        String algorithm = "fgesc";
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

}
