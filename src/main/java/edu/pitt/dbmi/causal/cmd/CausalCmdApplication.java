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

import edu.cmu.tetrad.graph.Graph;
import edu.pitt.dbmi.causal.cmd.tetrad.TetradAlgorithmRunner;
import edu.pitt.dbmi.causal.cmd.util.Application;
import edu.pitt.dbmi.causal.cmd.util.Args;
import edu.pitt.dbmi.causal.cmd.util.GraphIO;
import java.io.IOException;
import java.nio.file.Paths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * Mar 8, 2017 6:11:17 PM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public class CausalCmdApplication {

    private static final Logger LOGGER = LoggerFactory.getLogger(CausalCmdApplication.class);

    public static final String FOOTER = "Additional parameters are available when using --algorithm <arg>.";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        args = Args.clean(args);
        if (Args.isEmpty(args)) {
            Application.showHelp(CmdOptions.getInstance().getMainOptions(), FOOTER);
            System.exit(-1);
        } else if (Args.hasLongParam(args, CmdParams.HELP)) {
            Application.showHelp(CmdOptions.getInstance().getOptions(), FOOTER);
        } else if (Args.hasLongParam(args, CmdParams.VERSION)) {
            System.out.println(Application.getVersion());
        } else {
            CmdArgs cmdArgs;
            try {
                cmdArgs = CmdParser.parse(args);
            } catch (CmdParserException exception) {
                cmdArgs = null;
                System.err.println(exception.getCause().getMessage());
                Application.showHelp(exception.getOptions(), FOOTER);
            }
            if (cmdArgs == null) {
                System.exit(-1);
            }

            TetradAlgorithmRunner algorithmRunner = new TetradAlgorithmRunner();
            try {
                algorithmRunner.runAlgorithm(cmdArgs);
            } catch (IOException | IllegalAccessException | InstantiationException | ValidationException exception) {
                LOGGER.error("Algorithm run failed.", exception);
                System.exit(-1);
            }

            Graph graph = algorithmRunner.getGraph();
            try {
                GraphIO.write(graph, Paths.get(cmdArgs.getOutDirectory().toString(), cmdArgs.fileName + ".graph"));
            } catch (IOException exception) {
                LOGGER.error("Unable to write out graph.", exception);
                System.exit(-1);
            }
        }
    }

}
