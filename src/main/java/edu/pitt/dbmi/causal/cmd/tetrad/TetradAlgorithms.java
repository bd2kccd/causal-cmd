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

import edu.cmu.tetrad.annotation.Algorithm;
import edu.cmu.tetrad.annotation.AlgorithmAnnotations;
import edu.cmu.tetrad.annotation.AnnotatedClass;
import edu.cmu.tetrad.annotation.Experimental;
import edu.pitt.dbmi.causal.cmd.CausalCmdApplication;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

/**
 *
 * Sep 21, 2017 5:46:44 PM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public final class TetradAlgorithms {

    private static final TetradAlgorithms INSTANCE = new TetradAlgorithms();

    private final Map<String, AnnotatedClass<Algorithm>> algorithms = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private final Map<String, AnnotatedClass<Algorithm>> nonExpAlgorithms = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    private TetradAlgorithms() {
        AlgorithmAnnotations.getInstance().getAnnotatedClasses().stream().forEach(e -> {
            String key = e.getAnnotation().command();
            algorithms.put(key, e);
            if (!e.getClazz().isAnnotationPresent(Experimental.class)) {
                nonExpAlgorithms.put(key, e);
            }
        });
    }

    public static TetradAlgorithms getInstance() {
        return INSTANCE;
    }

    public List<String> getCommands() {
        List<String> list = CausalCmdApplication.showExperimental
                ? algorithms.keySet().stream().collect(Collectors.toList())
                : nonExpAlgorithms.keySet().stream().collect(Collectors.toList());

        return Collections.unmodifiableList(list);
    }

    public boolean hasCommand(String command) {
        if (command == null || command.isEmpty()) {
            return false;
        }

        return CausalCmdApplication.showExperimental
                ? algorithms.containsKey(command)
                : nonExpAlgorithms.containsKey(command);
    }

    public Class getAlgorithmClass(String command) {
        if (command == null || command.isEmpty()) {
            return null;
        }

        AnnotatedClass<Algorithm> annotatedClass = CausalCmdApplication.showExperimental
                ? algorithms.get(command)
                : nonExpAlgorithms.get(command);

        return (annotatedClass == null) ? null : annotatedClass.getClazz();
    }

    public boolean requireIndependenceTest(Class clazz) {
        return AlgorithmAnnotations.getInstance().requireIndependenceTest(clazz);
    }

    public boolean requireScore(Class clazz) {
        return AlgorithmAnnotations.getInstance().requireScore(clazz);
    }

    public boolean acceptMultipleDataset(Class clazz) {
        return AlgorithmAnnotations.getInstance().acceptMultipleDataset(clazz);
    }

    public boolean acceptKnowledge(Class clazz) {
        return AlgorithmAnnotations.getInstance().acceptKnowledge(clazz);
    }

    public String getName(Class clazz) {
        return (clazz != null && clazz.isAnnotationPresent(Algorithm.class))
                ? ((Algorithm) clazz.getAnnotation(Algorithm.class)).name()
                : "";
    }

}
