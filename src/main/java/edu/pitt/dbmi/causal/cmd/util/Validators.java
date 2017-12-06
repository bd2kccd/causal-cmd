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
package edu.pitt.dbmi.causal.cmd.util;

/**
 *
 * Oct 1, 2017 10:09:10 AM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public class Validators {

    private Validators() {
    }

    public static void validateNumber(Object type, String value) {
        if (type instanceof Byte) {
            try {
                Byte.valueOf(value);
            } catch (NumberFormatException exception) {
                String errMsg = String.format("Invalid byte number '%s'.", value);
                throw new NumberFormatException(errMsg);
            }
        } else if (type instanceof Integer) {
            try {
                Integer.valueOf(value);
            } catch (NumberFormatException exception) {
                String errMsg = String.format("Invalid integer number '%s'.", value);
                throw new NumberFormatException(errMsg);
            }
        } else if (type instanceof Long) {
            try {
                Long.valueOf(value);
            } catch (NumberFormatException exception) {
                String errMsg = String.format("Invalid long number '%s'.", value);
                throw new NumberFormatException(errMsg);
            }
        } else if (type instanceof Float) {
            try {
                Float.valueOf(value);
            } catch (NumberFormatException exception) {
                String errMsg = String.format("Invalid float number '%s'.", value);
                throw new NumberFormatException(errMsg);
            }
        } else if (type instanceof Double) {
            try {
                Double.valueOf(value);
            } catch (NumberFormatException exception) {
                String errMsg = String.format("Invalid double number '%s'.", value);
                throw new NumberFormatException(errMsg);
            }
        }
    }

}
