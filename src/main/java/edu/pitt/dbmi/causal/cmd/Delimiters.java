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

import edu.pitt.dbmi.data.reader.Delimiter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 * The class {@code Delimiters} contains the data delimiters supported by the
 * data reader.
 *
 * Jan 8, 2019 11:29:32 AM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public final class Delimiters {

    /**
     * An instance of this class.
     */
    private static final Delimiters INSTANCE = new Delimiters();

    /**
     * A data structure that holds different types of delimiters by delimiter
     * name.
     */
    private final Map<String, Delimiter> delimiters = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    private Delimiters() {
        Delimiter[] delims = Delimiter.values();
        for (Delimiter delimiter : delims) {
            delimiters.put(delimiter.getName(), delimiter);
        }
    }

    public static Delimiters getInstance() {
        return INSTANCE;
    }

    public List<String> getNames() {
        List<String> list = delimiters.keySet().stream()
                .collect(Collectors.toList());

        return Collections.unmodifiableList(list);
    }

    public Delimiter get(String delimiterName) {
        return delimiters.get(delimiterName);
    }

    public boolean exists(String delimiterName) {
        return delimiters.containsKey(delimiterName);
    }

}
