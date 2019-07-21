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
import edu.cmu.tetrad.annotation.TestOfIndependence;
import edu.cmu.tetrad.annotation.TestOfIndependenceAnnotations;
import edu.cmu.tetrad.data.DataType;
import edu.pitt.dbmi.causal.cmd.CausalCmdApplication;
import java.util.Arrays;
import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * Sep 26, 2017 2:48:25 PM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public final class TetradIndependenceTests {

    private static TetradIndependenceTests instance;

    private final Map<String, AnnotatedClass<TestOfIndependence>> annotatedClasses;

    private final Map<DataType, List<String>> groupByDataType = new EnumMap<>(DataType.class);

    private TetradIndependenceTests() {
        TestOfIndependenceAnnotations testAnno = TestOfIndependenceAnnotations.getInstance();
        List<AnnotatedClass<TestOfIndependence>> testList = CausalCmdApplication.showExperimental
                ? testAnno.getAnnotatedClasses()
                : testAnno.filterOutExperimental(testAnno.getAnnotatedClasses());

        this.annotatedClasses = testList.stream()
                .filter(e -> !Arrays.asList(e.getAnnotation().dataType()).contains(DataType.Graph))
                .collect(() -> new TreeMap<>(String.CASE_INSENSITIVE_ORDER),
                        (m, e) -> m.put(e.getAnnotation().command(), e),
                        (m, u) -> m.putAll(u));

        this.annotatedClasses.forEach((k, v) -> {
            DataType[] dataTypes = v.getAnnotation().dataType();
            for (DataType dataType : dataTypes) {
                List<String> list = groupByDataType.get(dataType);
                if (list == null) {
                    list = new LinkedList<>();
                    groupByDataType.put(dataType, list);
                }
                list.add(k);
            }
        });

        // merge continuous datatype with mixed datatype
        List<String> mergeList = Stream.concat(groupByDataType.get(DataType.Continuous).stream(), groupByDataType.get(DataType.Mixed).stream())
                .sorted()
                .collect(Collectors.toList());
        groupByDataType.put(DataType.Continuous, mergeList);

        // merge discrete datatype with mixed datatype
        mergeList = Stream.concat(groupByDataType.get(DataType.Discrete).stream(), groupByDataType.get(DataType.Mixed).stream())
                .sorted()
                .collect(Collectors.toList());
        groupByDataType.put(DataType.Discrete, mergeList);
    }

    public static TetradIndependenceTests getInstance() {
        if (instance == null) {
            instance = new TetradIndependenceTests();
        }

        return instance;
    }

    public static void clear() {
        instance = null;
    }

    public boolean hasCommand(String command) {
        return (command == null || command.isEmpty())
                ? false
                : annotatedClasses.containsKey(command);
    }

    public boolean hasCommand(String command, DataType dataType) {
        if (command == null || command.isEmpty() || dataType == null) {
            return false;
        }

        if (!groupByDataType.containsKey(dataType)) {
            return false;
        }

        return groupByDataType.get(dataType).stream()
                .anyMatch(e -> e.equalsIgnoreCase(command));
    }

    public List<String> getCommands() {
        List<String> list = annotatedClasses.keySet().stream()
                .collect(Collectors.toList());

        return Collections.unmodifiableList(list);
    }

    public List<String> getCommands(DataType dataType) {
        List<String> list = new LinkedList<>();

        if (dataType != null && groupByDataType.containsKey(dataType)) {
            list.addAll(groupByDataType.get(dataType));
        }

        return Collections.unmodifiableList(list);
    }

    public Class getClass(String command) {
        if (command == null || command.isEmpty()) {
            return null;
        }

        AnnotatedClass<TestOfIndependence> annotatedClass = annotatedClasses.get(command);

        return (annotatedClass == null) ? null : annotatedClass.getClazz();
    }

    public String getName(Class clazz) {
        return (clazz != null && clazz.isAnnotationPresent(TestOfIndependence.class))
                ? ((TestOfIndependence) clazz.getAnnotation(TestOfIndependence.class)).name()
                : "";
    }

    public String getDescription(Class clazz) {
        return (clazz != null && clazz.isAnnotationPresent(TestOfIndependence.class))
                ? ((TestOfIndependence) clazz.getAnnotation(TestOfIndependence.class)).description()
                : "";
    }

}
