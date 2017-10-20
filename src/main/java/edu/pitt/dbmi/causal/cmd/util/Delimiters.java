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

import edu.pitt.dbmi.data.Delimiter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 *
 * Sep 12, 2017 3:18:14 PM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public class Delimiters {

    private static final Delimiters INSTANCE = new Delimiters();

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
