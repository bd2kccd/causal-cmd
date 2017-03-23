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
 * Mar 13, 2017 2:02:17 PM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public class CausalCmdApplicationGFCITest {

    @ClassRule
    public static TemporaryFolder tmpDir = new TemporaryFolder();

    public CausalCmdApplicationGFCITest() {
    }

    @AfterClass
    public static void tearDownClass() {
        tmpDir.delete();
    }

    @Test
    public void testGFCId() throws IOException {
        Path dataFile = Paths.get("test", "data", "cmu", "avatarwithdependencies.esv");
        Path knowledgeFile = Paths.get("test", "data", "cmu", "avatarknowledge.txt");
        String delimiter = Delimiter.TAB.getName();
        String algorithm = AlgorithmType.GFCID.getCmd();
        String dirOut = tmpDir.newFolder(algorithm).toString();
        String outputPrefix = algorithm;
        String[] args = {
            "--algorithm", algorithm,
            "--data", dataFile.toString(),
            "--knowledge", knowledgeFile.toString(),
            "--delimiter", delimiter,
            "--skip-category-limit",
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
    public void testGFCIc() throws IOException {
        Path dataFile = Paths.get("test", "data", "cmu", "rawst.tetrad.txt");
        String delimiter = Delimiter.TAB.getName();
        String algorithm = AlgorithmType.GFCIC.getCmd();
        String dirOut = tmpDir.newFolder(algorithm).toString();
        String outputPrefix = algorithm;
        String[] args = {
            "--algorithm", algorithm,
            "--data", dataFile.toString(),
            "--delimiter", delimiter,
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

}
