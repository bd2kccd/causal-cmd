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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 *
 * Jan 8, 2019 12:04:29 PM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public final class Applications {

    private static final DateFormat DF = new SimpleDateFormat("EEE, MMMM dd, yyyy hh:mm:ss a");

    private Applications() {
    }

    public static void showHelp(String[] args, ParseOptions parseOptions, String footer) {
        Options opts = parseOptions.getOptions();
        Options invalidOpts = parseOptions.getInvalidValueOptions();

        Map<String, String> argsMap = new TreeMap<>(Args.toMapLongOptions(args));

        // remove all the options with invalid value
        invalidOpts.getOptions().forEach(e -> argsMap.remove(e.getLongOpt()));

        List<String> optList = new LinkedList<>();
        argsMap.forEach((k, v) -> {
            Option opt = opts.getOption(k);
            if (opt != null) {
                optList.add(String.format("--%s", opt.getLongOpt()));
                if (v != null) {
                    optList.add(v);
                }
            }
        });
        String header = optList.stream().collect(Collectors.joining(" "));
        String cmdLineSyntax = String.format("%s %s", getHelpTitle(), header);

        // create new options
        Options helpOpts = new Options();
        opts.getOptions().stream()
                .filter(e -> !argsMap.containsKey(e.getLongOpt()))
                .forEach(e -> helpOpts.addOption(e));

        invalidOpts.getOptions().forEach(e -> helpOpts.addOption(e));

        HelpFormatter formatter = new HelpFormatter();
        formatter.setWidth(-1);
        if (footer == null) {
            formatter.printHelp(cmdLineSyntax, helpOpts, true);
        } else {
            formatter.printHelp(cmdLineSyntax, null, helpOpts, footer, true);
        }
    }

    public static void showHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setWidth(-1);
        formatter.printHelp(getHelpTitle(), options, true);
    }

    public static void showHelp(Options options, String footer) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setWidth(-1);
        formatter.printHelp(getHelpTitle(), null, options, footer, true);
    }

    public static String getVersion() {
        return String.format("Version %s", jarVersion());
    }

    public static String fmtDate(Date date) {
        return DF.format(date);
    }

    public static String fmtDateNow() {
        return fmtDate(new Date(System.currentTimeMillis()));
    }

    public static String jarTitle() {
        return Applications.class.getPackage().getImplementationTitle();
    }

    public static String jarVersion() {
        String version = Applications.class.getPackage().getImplementationVersion();

        return (version == null) ? "unknown" : version;
    }

    private static String getHelpTitle() {
        String title = jarTitle();
        String version = jarVersion();

        return (title == null || version == null)
                ? "java -jar causal-cmd.jar"
                : String.format("java -jar %s-%s.jar", title, version);
    }

}
