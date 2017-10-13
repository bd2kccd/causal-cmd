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

import edu.pitt.dbmi.causal.cmd.CmdOptions;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

/**
 *
 * Mar 9, 2017 3:37:35 PM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public class Args {

    private Args() {
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
        if (isEmpty(args) || !CmdOptions.getInstance().hasLongParam(option)) {
            return false;
        }

        CmdOptions cmdOptions = CmdOptions.getInstance();
        Option longOpt = cmdOptions.getLongOption(option);
        for (String arg : args) {
            if (arg.startsWith("--")) {
                String opt = arg.substring(2, arg.length());
                if (longOpt == cmdOptions.getLongOption(opt)) {
                    return true;
                }
            }
        }

        return false;
    }

    public static boolean isEmpty(String[] args) {
        return (args == null || args.length == 0);
    }

    public static String[] clean(String[] args) {
        if (args == null) {
            return null;
        }

        List<String> argList = new LinkedList<>();
        for (String arg : args) {
            if (arg != null) {
                arg = arg.trim();
                if (!arg.isEmpty()) {
                    argList.add(arg);
                }
            }
        }

        return argList.toArray(new String[argList.size()]);
    }

}
