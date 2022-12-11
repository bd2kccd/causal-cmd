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
package edu.pitt.dbmi.causal.cmd.util;

/**
 * The class {@code WordUtil} is a utility class for converting string values.
 *
 * Jan 9, 2019 11:30:13 AM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public final class WordUtil {

    private WordUtil() {
    }

    /**
     * Convert true/false value to yes/no.
     *
     * @param value true or false literal value
     * @return yes literal value if the value literal is true, no otherwise.
     */
    public static final String toYesOrNo(String value) {
        switch (value) {
            case "true":
                return "yes";
            case "false":
                return "no";
            default:
                return value;
        }
    }

}
