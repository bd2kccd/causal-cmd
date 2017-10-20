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

import edu.pitt.dbmi.causal.cmd.util.Application;
import edu.pitt.dbmi.causal.cmd.util.Args;
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

    public static final String FOOTER = "Use --help for guidance list of options.  Use --help-all to show all options.";

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        args = Args.clean(args);
        if (Args.hasLongParam(args, CmdParams.HELP)) {
            try {
                Application.showHelp(args, CmdParser.getHelpOptions(args), null);
            } catch (CmdParserException exception) {
                System.err.println(exception.getCause().getMessage());
                Application.showHelp(args, exception.getParseOptions(), null);
            }
        } else if (Args.hasLongParam(args, CmdParams.HELP_ALL)) {
            Application.showHelp(CmdOptions.getInstance().getOptions(), null);
        } else if (Args.hasLongParam(args, CmdParams.VERSION)) {
            System.out.println(Application.getVersion());
        } else {
            CmdArgs cmdArgs = null;
            try {
                cmdArgs = CmdParser.parse(args);
            } catch (CmdParserException exception) {
                System.err.println(exception.getCause().getMessage());
                Application.showHelp(args, exception.getParseOptions(), FOOTER);
            }
        }
    }

}
