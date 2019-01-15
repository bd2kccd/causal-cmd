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

/**
 *
 * Sep 15, 2017 12:26:52 PM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public class CmdParserException extends Exception {

    private static final long serialVersionUID = 4277546481941173659L;

    private final ParseOptions parseOptions;

    public CmdParserException(ParseOptions parseOptions) {
        this.parseOptions = parseOptions;
    }

    public CmdParserException(ParseOptions parseOptions, String message) {
        super(message);
        this.parseOptions = parseOptions;
    }

    public CmdParserException(ParseOptions parseOptions, String message, Throwable cause) {
        super(message, cause);
        this.parseOptions = parseOptions;
    }

    public CmdParserException(ParseOptions parseOptions, Throwable cause) {
        super(cause);
        this.parseOptions = parseOptions;
    }

    public CmdParserException(ParseOptions parseOptions, String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
        this.parseOptions = parseOptions;
    }

    public ParseOptions getParseOptions() {
        return parseOptions;
    }

}
