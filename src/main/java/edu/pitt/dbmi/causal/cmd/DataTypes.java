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

import edu.cmu.tetrad.data.DataType;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 *
 * Jan 8, 2019 11:28:17 AM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public final class DataTypes {

    private static final DataTypes INSTANCE = new DataTypes();

    private final Map<String, DataType> dataTypes = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    private DataTypes() {
        DataType[] types = DataType.values();
        for (DataType type : types) {
            dataTypes.put(type.toString().toLowerCase(), type);
        }

        // remove graph type
        dataTypes.remove(DataType.Graph.toString().toLowerCase());
    }

    public static DataTypes getInstance() {
        return INSTANCE;
    }

    public List<String> getNames() {
        List<String> list = dataTypes.keySet().stream()
                .collect(Collectors.toList());

        return Collections.unmodifiableList(list);
    }

    public DataType get(String dataTypeName) {
        return (dataTypeName == null) ? null : dataTypes.get(dataTypeName);
    }

    public boolean exists(String dataTypeName) {
        return (dataTypeName == null) ? false : dataTypes.containsKey(dataTypeName);
    }

}
