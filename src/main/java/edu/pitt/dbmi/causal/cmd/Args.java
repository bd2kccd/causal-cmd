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
 * The class {@code Args} is a utility class for parsing, extracting, and
 * manipulate command-line arguments .
 *
 * Mar 9, 2017 3:37:35 PM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public final class Args {

    private Args() {
    }

    /**
     * Remove the given option from the command-line arguments.
     *
     * @param args command-line arguments
     * @param option option to remove
     * @return command-line arguments
     */
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

        return argsToKeep.toArray(new String[0]);
    }

    /**
     * Parse options from command-line arguments by its multi-character name
     * into argsMap.
     *
     * @param args command-line arguments
     * @param options multi-character name options.
     * @param argsMap holds options by name
     * @throws ParseException whenever error occurs during parsing of a
     * command-line.
     */
    public static void parseLongOptions(String[] args, Options options, Map<String, String> argsMap) throws ParseException {
        CommandLine cmd = (new DefaultParser()).parse(options, args);
        options.getOptions().forEach(option -> {
            String opt = option.getLongOpt();
            if (opt != null && cmd.hasOption(opt)) {
                argsMap.put(opt, cmd.getOptionValue(opt));
            }
        });
    }

    /**
     * Parse options from command-line arguments by its single-character name
     * into argsMap.
     *
     * @param args command-line arguments
     * @param options single-character name options.
     * @param argsMap holds options by name
     * @throws ParseException whenever error occurs during parsing of a
     * command-line.
     */
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

    /**
     * Extract multi-character name options from command-line arguments.
     *
     * @param args command-line arguments
     * @param options multi-character name options.
     * @return extracted command-line arguments
     */
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

        return argsList.toArray(new String[0]);
    }

    /**
     * Extract both multi-character name and single-character name options from
     * command-line arguments.
     *
     * @param args command-line arguments
     * @param options multi-character name and single-character name options.
     * @return command-line arguments
     */
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

        return argsList.toArray(new String[0]);
    }

    /**
     * Extract multi-character name options into a parameter-argument
     * collection.
     *
     * @param args command-line arguments
     * @return parameter-argument collection
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
     * Extract multi-character name and and single-character name options into a
     * parameter-argument collection.
     *
     * @param args command-line arguments
     * @return parameter-argument collection
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

    /**
     * Check if the given option is in the command-line arguments.
     *
     * @param args command-line arguments
     * @param option option to check for
     * @return true if the given option is found in the command-line arguments
     */
    public static boolean hasLongParam(String[] args, String option) {
        if (isEmpty(args)) {
            return false;
        }

        return Arrays.stream(args)
                .filter(e -> e.startsWith("--"))
                .map(e -> e.substring(2, e.length()))
                .anyMatch(option::equals);
    }

    /**
     * Test if the command-line arguments has any parameters or arguments.
     *
     * @param args command-line arguments
     * @return true if the command-line arguments has not parameters or
     * arguments
     */
    public static boolean isEmpty(String[] args) {
        return (args == null || args.length == 0);
    }

    /**
     * Remove extract spaces in the parameter names and arguments.
     *
     * @param args command-line arguments
     * @return command-line arguments
     */
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
