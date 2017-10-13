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

import org.apache.commons.cli.Options;

/**
 *
 * Sep 15, 2017 12:26:52 PM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public class CmdParserException extends Exception {

    private static final long serialVersionUID = 4277546481941173659L;

    private final HelpOptions helpOptions;

    public CmdParserException(HelpOptions helpOptions) {
        this.helpOptions = helpOptions;
    }

    public CmdParserException(HelpOptions helpOptions, String message) {
        super(message);
        this.helpOptions = helpOptions;
    }

    public CmdParserException(HelpOptions helpOptions, String message, Throwable cause) {
        super(message, cause);
        this.helpOptions = helpOptions;
    }

    public CmdParserException(HelpOptions helpOptions, Throwable cause) {
        super(cause);
        this.helpOptions = helpOptions;
    }

    public CmdParserException(HelpOptions helpOptions, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.helpOptions = helpOptions;
    }

    public HelpOptions getHelpOptions() {
        return helpOptions;
    }

}
