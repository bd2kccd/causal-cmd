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

    private final Options options;

    public CmdParserException(Options options) {
        this.options = options;
    }

    public CmdParserException(Options options, String message) {
        super(message);
        this.options = options;
    }

    public CmdParserException(Options options, String message, Throwable cause) {
        super(message, cause);
        this.options = options;
    }

    public CmdParserException(Options options, Throwable cause) {
        super(cause);
        this.options = options;
    }

    public CmdParserException(Options options, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.options = options;
    }

    public Options getOptions() {
        return options;
    }

}
