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
package edu.pitt.dbmi.causal.cmd.tetrad;

import edu.cmu.tetrad.annotation.AnnotatedClass;
import edu.cmu.tetrad.annotation.Experimental;
import edu.cmu.tetrad.annotation.TestOfIndependence;
import edu.cmu.tetrad.annotation.TestOfIndependenceAnnotations;
import edu.cmu.tetrad.data.DataType;
import edu.pitt.dbmi.causal.cmd.CausalCmdApplication;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * The class {@code TetradIndependenceTests} is a utility class for handling
 * Tetrad independence tests.
 *
 * Sep 26, 2017 2:48:25 PM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public final class TetradIndependenceTests {

    private static final TetradIndependenceTests INSTANCE = new TetradIndependenceTests();

    private final Map<String, AnnotatedClass<TestOfIndependence>> tests = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private final Map<String, AnnotatedClass<TestOfIndependence>> nonExpTests = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    private final Map<DataType, List<String>> groupByDataType = new EnumMap<>(DataType.class);
    private final Map<DataType, List<String>> nonExpGroupByDataType = new EnumMap<>(DataType.class);

    /**
     * Private constructor.
     */
    private TetradIndependenceTests() {
        TestOfIndependenceAnnotations.getInstance().getAnnotatedClasses().stream().forEach(e -> {
            String key = e.annotation().command();
            tests.put(key, e);
            if (!e.clazz().isAnnotationPresent(Experimental.class)) {
                nonExpTests.put(key, e);
            }
        });

        tests.forEach((k, v) -> {
            DataType[] dataTypes = v.annotation().dataType();
            for (DataType dataType : dataTypes) {
                List<String> list = groupByDataType.get(dataType);
                if (list == null) {
                    list = new LinkedList<>();
                    groupByDataType.put(dataType, list);
                }
                list.add(k);
            }
        });

        nonExpTests.forEach((k, v) -> {
            DataType[] dataTypes = v.annotation().dataType();
            for (DataType dataType : dataTypes) {
                List<String> list = nonExpGroupByDataType.get(dataType);
                if (list == null) {
                    list = new LinkedList<>();
                    nonExpGroupByDataType.put(dataType, list);
                }
                list.add(k);
            }
        });

        // merge continuous datatype with mixed datatype and merge discrete datatype with mixed datatype
        groupByDataType.put(DataType.Continuous, mergeList(groupByDataType.get(DataType.Continuous), groupByDataType.get(DataType.Mixed)));
        groupByDataType.put(DataType.Discrete, mergeList(groupByDataType.get(DataType.Discrete), groupByDataType.get(DataType.Mixed)));

        // merge continuous datatype with mixed datatype and merge discrete datatype with mixed datatype
        nonExpGroupByDataType.put(DataType.Continuous, mergeList(nonExpGroupByDataType.get(DataType.Continuous), nonExpGroupByDataType.get(DataType.Mixed)));
        nonExpGroupByDataType.put(DataType.Discrete, mergeList(nonExpGroupByDataType.get(DataType.Discrete), nonExpGroupByDataType.get(DataType.Mixed)));
    }

    /**
     * Combine two lists of command-line options into one.
     *
     * @param listA list to merge
     * @param listB list to merge
     * @return list containing values from listA and listB
     */
    private static List<String> mergeList(List<String> listA, List<String> listB) {
        return Stream.concat(listA.stream(), listB.stream())
                .sorted()
                .collect(Collectors.toList());
    }

    /**
     * Get the instance of this class.
     *
     * @return instance of TetradIndependenceTests
     */
    public static TetradIndependenceTests getInstance() {
        return INSTANCE;
    }

    /**
     * Determine if the giving command is a validate command-line option.
     *
     * @param command command-line argument
     * @return true if the giving command is a validate command-line option,
     * otherwise false
     */
    public boolean hasCommand(String command) {
        if (command == null || command.isEmpty()) {
            return false;
        }

        return CausalCmdApplication.showExperimental
                ? tests.containsKey(command)
                : nonExpTests.containsKey(command);
    }

    /**
     * Determine if the giving command is a validate command-line option for a
     * giving datatype.
     *
     * @param command command-line argument
     * @param dataType data type
     * @return true if the giving command is a validate command-line option for
     * a giving datatype. Otherwise, false.
     */
    public boolean hasCommand(String command, DataType dataType) {
        if (command == null || command.isEmpty() || dataType == null) {
            return false;
        }

        Map<DataType, List<String>> map = CausalCmdApplication.showExperimental
                ? groupByDataType
                : nonExpGroupByDataType;

        if (!map.containsKey(dataType)) {
            return false;
        }

        return map.get(dataType).stream()
                .anyMatch(e -> e.equalsIgnoreCase(command));
    }

    /**
     * Get a list of command-line options for test of independence.
     *
     * @return a list of command-line options for test of independence
     */
    public List<String> getCommands() {
        List<String> list = CausalCmdApplication.showExperimental
                ? tests.keySet().stream().collect(Collectors.toList())
                : nonExpTests.keySet().stream().collect(Collectors.toList());

        return Collections.unmodifiableList(list);
    }

    /**
     * Get a list of command-line options for a giving datatype.
     *
     * @param dataType data type
     * @return a list of command-line options for a giving datatype
     */
    public List<String> getCommands(DataType dataType) {
        Map<DataType, List<String>> map = CausalCmdApplication.showExperimental
                ? groupByDataType
                : nonExpGroupByDataType;

        return map.containsKey(dataType)
                ? Collections.unmodifiableList(map.get(dataType))
                : Collections.EMPTY_LIST;
    }

    /**
     * Get class for a giving command-line option.
     *
     * @param command command-line argument
     * @return class for a giving command-line option
     */
    public Class getClass(String command) {
        if (command == null || command.isEmpty()) {
            return null;
        }

        AnnotatedClass<TestOfIndependence> annotatedClass = CausalCmdApplication.showExperimental
                ? tests.get(command)
                : nonExpTests.get(command);

        return (annotatedClass == null) ? null : annotatedClass.clazz();
    }

    /**
     * Get the class name for test of independence.
     *
     * @param clazz test-of-independence class
     * @return class name for test of independence.
     */
    public String getName(Class clazz) {
        return (clazz != null && clazz.isAnnotationPresent(TestOfIndependence.class))
                ? ((TestOfIndependence) clazz.getAnnotation(TestOfIndependence.class)).name()
                : "";
    }

    /**
     * Get the description for test of independence from class.
     *
     * @param clazz test-of-independence class
     * @return description for test of independence
     */
    public String getDescription(Class clazz) {
        return (clazz != null && clazz.isAnnotationPresent(TestOfIndependence.class))
                ? ((TestOfIndependence) clazz.getAnnotation(TestOfIndependence.class)).description()
                : "";
    }

}
