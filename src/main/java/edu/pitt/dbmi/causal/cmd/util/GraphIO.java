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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import edu.cmu.tetrad.graph.Graph;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;

/**
 * The class {@code FileUtils} is a utility class for converting and writing out
 * graph object.
 *
 * Jan 15, 2019 2:58:45 PM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public final class GraphIO {

    private GraphIO() {
    }

    /**
     * Write out graph object to a text file.
     *
     * @param graph
     * @param path
     * @throws IOException
     */
    public static void writeAsTXT(Graph graph, Path path) throws IOException {
        Scanner scanner = new Scanner(graph.toString());
        try ( PrintStream out = new PrintStream(Files.newOutputStream(path), true)) {
            while (scanner.hasNextLine()) {
                out.println(scanner.nextLine().trim());
            }
        }
    }

    /**
     * Write out graph object to a JSON file.
     *
     * @param graph
     * @param path
     * @throws IOException
     */
    public static void writeAsJSON(Graph graph, Path path) throws IOException {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        try ( PrintStream out = new PrintStream(Files.newOutputStream(path), true)) {
            out.println(gson.toJson(graph));
        }
    }

}
