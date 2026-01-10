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
import static edu.pitt.dbmi.causal.cmd.CmdDataType.Continuous;
import static edu.pitt.dbmi.causal.cmd.CmdDataType.Covariance;
import static edu.pitt.dbmi.causal.cmd.CmdDataType.Discrete;
import static edu.pitt.dbmi.causal.cmd.CmdDataType.LCovariance;
import static edu.pitt.dbmi.causal.cmd.CmdDataType.Mixed;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 *
 * Jan 10, 2026 2:03:44 PM
 *
 * @author Kevin V. Bui (kvb2univpitt@gmail.com)
 */
public class CmdDataTypes {

    private static final CmdDataTypes INSTANCE = new CmdDataTypes();

    private final Map<String, CmdDataType> dataTypes = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    private CmdDataTypes() {
        CmdDataType[] types = CmdDataType.values();
        for (CmdDataType type : types) {
            dataTypes.put(type.toString().toLowerCase(), type);
        }
    }

    public static CmdDataTypes getInstance() {
        return INSTANCE;
    }

    public List<String> getNames() {
        List<String> list = dataTypes.keySet().stream()
                .collect(Collectors.toList());

        return Collections.unmodifiableList(list);
    }

    public CmdDataType get(String dataTypeName) {
        return (dataTypeName == null) ? null : dataTypes.get(dataTypeName);
    }

    public boolean exists(String dataTypeName) {
        return (dataTypeName == null) ? false : dataTypes.containsKey(dataTypeName);
    }

    public static DataType toTetradDataType(CmdDataType dataType) {
        return switch (dataType) {
            case Continuous ->
                DataType.Continuous;
            case Discrete ->
                DataType.Discrete;
            case Mixed ->
                DataType.Mixed;
            case Covariance ->
                DataType.Covariance;
            case LCovariance ->
                DataType.Covariance;
            default -> {
                String errMsg = String.format("Data type %s not supported.", dataType.name());
                throw new IllegalArgumentException(errMsg);
            }
        };
    }

}
