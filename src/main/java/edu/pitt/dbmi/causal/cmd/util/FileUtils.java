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

import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * The class {@code FileUtils} is a utility class for file management.
 *
 * Sep 15, 2017 4:03:38 PM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public class FileUtils {

    private FileUtils() {
    }

    public static void exists(Path file) throws FileNotFoundException {
        if (Files.notExists(file)) {
            throw new FileNotFoundException(String.format("File '%s' does not exist.", file.toString()));
        }
        if (!Files.isRegularFile(file)) {
            throw new FileNotFoundException(String.format("'%s' is not a file.", file.toString()));
        }
    }

}
