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

import edu.pitt.dbmi.causal.cmd.sim.DataSimulationType;
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
 * Mar 22, 2017 4:13:05 PM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public class CausalCmdApplicationDataSimTest {

    @ClassRule
    public static TemporaryFolder tmpDir = new TemporaryFolder();

    public CausalCmdApplicationDataSimTest() {
    }

    @AfterClass
    public static void tearDownClass() {
        tmpDir.delete();
    }

    @Test
    public void testBayNetRandFwdDataSimulation() throws IOException {
        String simulation = DataSimulationType.BAYES_NET_RAND_FWD.getCmd();
        String delimiter = Delimiter.TAB.getName();
        String dirOut = tmpDir.newFolder(simulation).toString();
        String outputPrefix = simulation;
        String numOfVariables = "8";
        String numOfCases = "10";
        String[] args = {
            "--simulate-data", simulation,
            "--var", numOfVariables,
            "--case", numOfCases,
            "--delimiter", delimiter,
            "--out", dirOut,
            "--output-prefix", outputPrefix
        };
        CausalCmdApplication.main(args);

        Path outFile = Paths.get(dirOut, outputPrefix + ".txt");
        String errMsg = outFile.getFileName().toString() + " does not exist.";
        Assert.assertTrue(errMsg, Files.exists(outFile, LinkOption.NOFOLLOW_LINKS));

        outFile = Paths.get(dirOut, outputPrefix + ".graph");
        errMsg = outFile.getFileName().toString() + " does not exist.";
        Assert.assertTrue(errMsg, Files.exists(outFile, LinkOption.NOFOLLOW_LINKS));
    }

    @Test
    public void testSemRandFwdDataSimulation() throws IOException {
        String simulation = DataSimulationType.SEM_RAND_FWD.getCmd();
        String delimiter = Delimiter.TAB.getName();
        String dirOut = tmpDir.newFolder(simulation).toString();
        String outputPrefix = simulation;
        String numOfVariables = "8";
        String numOfCases = "10";
        String[] args = {
            "--simulate-data", simulation,
            "--var", numOfVariables,
            "--case", numOfCases,
            "--delimiter", delimiter,
            "--out", dirOut,
            "--output-prefix", outputPrefix
        };
        CausalCmdApplication.main(args);

        Path outFile = Paths.get(dirOut, outputPrefix + ".txt");
        String errMsg = outFile.getFileName().toString() + " does not exist.";
        Assert.assertTrue(errMsg, Files.exists(outFile, LinkOption.NOFOLLOW_LINKS));

        outFile = Paths.get(dirOut, outputPrefix + ".graph");
        errMsg = outFile.getFileName().toString() + " does not exist.";
        Assert.assertTrue(errMsg, Files.exists(outFile, LinkOption.NOFOLLOW_LINKS));
    }

}
