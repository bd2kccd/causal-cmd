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

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 *
 * Mar 9, 2017 3:37:35 PM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public final class Args {

    private Args() {
    }

    public static String[] removeLongOption(String[] args, String option) {
        CmdOptions cmdOptions = CmdOptions.getInstance();
        List<String> argsToKeep = new LinkedList<>();
        boolean skip = false;
        for (String arg : args) {
            if (arg.startsWith("--")) {
                String value = arg.substring(2, arg.length());
                if (value.equals(option)) {
                    if (cmdOptions.hasLongParam(option) && cmdOptions.getLongOption(option).hasArg()) {
                        skip = true;
                    }
                } else {
                    argsToKeep.add(arg);
                }
            } else {
                if (skip) {
                    skip = false;
                } else {
                    argsToKeep.add(arg);
                }
            }
        }

        return argsToKeep.toArray(new String[argsToKeep.size()]);
    }

    public static void parseLongOptions(String[] args, Options options, Map<String, String> argsMap) throws ParseException {
        CommandLine cmd = (new DefaultParser()).parse(options, args);
        options.getOptions().forEach(option -> {
            String opt = option.getLongOpt();
            if (opt != null && cmd.hasOption(opt)) {
                argsMap.put(opt, cmd.getOptionValue(opt));
            }
        });
    }

    public static void parse(String[] args, Options options, Map<String, String> argsMap) throws ParseException {
        CommandLine cmd = (new DefaultParser()).parse(options, args);
        options.getOptions().forEach(option -> {
            String opt = option.getOpt();
            if (opt != null && cmd.hasOption(opt)) {
                argsMap.put(opt, cmd.getOptionValue(opt));
            }

            opt = option.getLongOpt();
            if (opt != null && cmd.hasOption(opt)) {
                argsMap.put(opt, cmd.getOptionValue(opt));
            }
        });
    }

    public static String[] extractLongOptions(String[] args, Options options) {
        List<String> argsList = new LinkedList<>();

        Map<String, String> argsMap = toMapLongOptions(args);
        options.getOptions().forEach(opt -> {
            String param = opt.getLongOpt();
            if (param != null && argsMap.containsKey(param)) {
                argsList.add("--" + param);

                String value = argsMap.get(param);
                if (value != null) {
                    argsList.add(value);
                }
            }
        });

        return argsList.toArray(new String[argsList.size()]);
    }

    public static String[] extractOptions(String[] args, Options options) {
        List<String> argsList = new LinkedList<>();

        Map<String, String> argsMap = toMapOptions(args);
        options.getOptions().forEach(opt -> {
            String param = opt.getOpt();
            if (param != null && argsMap.containsKey(param)) {
                argsList.add("-" + param);

                String value = argsMap.get(param);
                if (value != null) {
                    argsList.add(value);
                }
            }
            param = opt.getLongOpt();
            if (param != null && argsMap.containsKey(param)) {
                argsList.add("--" + param);

                String value = argsMap.get(param);
                if (value != null) {
                    argsList.add(value);
                }
            }
        });

        return argsList.toArray(new String[argsList.size()]);
    }

    /**
     * Parse the long parameters from the command inputs to map where the
     * parameters are map keys and parameter values are map values.
     *
     * @param args
     * @return
     */
    public static Map<String, String> toMapLongOptions(String[] args) {
        Map<String, String> map = new HashMap<>();

        String key = null;
        for (String arg : args) {
            if (key != null) {
                if (arg.startsWith("--")) {
                    map.put(key, null);
                } else {
                    map.put(key, arg);
                }
                key = null;
            }

            if (arg.startsWith("--")) {
                key = arg.substring(2, arg.length());
            }
        }
        if (key != null) {
            map.put(key, null);
        }

        return map;
    }

    /**
     * Parse the command inputs to map where the parameters are map keys and
     * parameter values are map values.
     *
     * @param args
     * @return
     */
    public static Map<String, String> toMapOptions(String[] args) {
        Map<String, String> map = new HashMap<>();

        String key = null;
        for (String arg : args) {
            if (key != null) {
                if (arg.startsWith("--") || arg.startsWith("-")) {
                    map.put(key, null);
                } else {
                    map.put(key, arg);
                }
                key = null;
            }

            if (arg.startsWith("--")) {
                key = arg.substring(2, arg.length());
            } else if (arg.startsWith("-")) {
                key = arg.substring(1, arg.length());
            }
        }
        if (key != null) {
            map.put(key, null);
        }

        return map;
    }

    public static boolean hasLongParam(String[] args, String option) {
        if (isEmpty(args)) {
            return false;
        }

        return Arrays.stream(args)
                .filter(e -> e.startsWith("--"))
                .map(e -> e.substring(2, e.length()))
                .anyMatch(option::equals);
    }

    public static boolean isEmpty(String[] args) {
        return (args == null || args.length == 0);
    }

    public static String[] clean(String[] args) {
        return (args == null)
                ? new String[0]
                : Arrays.stream(args)
                        .filter(Objects::nonNull)
                        .map(String::trim)
                        .filter(e -> !e.isEmpty())
                        .toArray(String[]::new);
    }

}
