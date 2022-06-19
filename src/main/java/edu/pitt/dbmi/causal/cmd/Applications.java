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

import edu.cmu.tetrad.util.AlgorithmDescriptions;
import edu.cmu.tetrad.util.IndependenceTestDescriptions;
import edu.cmu.tetrad.util.ScoreDescriptions;
import edu.pitt.dbmi.causal.cmd.tetrad.TetradAlgorithms;
import edu.pitt.dbmi.causal.cmd.tetrad.TetradIndependenceTests;
import edu.pitt.dbmi.causal.cmd.tetrad.TetradScores;
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
 * The class {@code Applications} is a utility class for displaying help
 * information and for getting the application jar file information..
 *
 * Jan 8, 2019 12:04:29 PM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public final class Applications {

    private static final DateFormat DF = new SimpleDateFormat("EEE, MMMM dd, yyyy hh:mm:ss a");

    private Applications() {
    }

    /**
     * Print out help messages to terminal for options that are not satisfied..
     *
     * @param args user's input of options and arguments
     * @param parseOptions options that has been parsed
     * @param footer help message footer
     */
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

    /**
     * Print out a list of independence tests with descriptions to terminal..
     */
    public static void showTestsAndDescriptions() {
        System.out.println("================================================================================");
        System.out.println("Independence Tests");
        System.out.println("================================================================================");
        IndependenceTestDescriptions desc = IndependenceTestDescriptions.getInstance();
        TetradIndependenceTests.getInstance().getCommands()
                .forEach(e -> {
                    System.out.println(e);
                    System.out.println("--------------------------------------------------------------------------------");
                    System.out.println(desc.get(e));
                    System.out.println();
                    System.out.println();
                });
    }

    /**
     * Print out a list of scores with descriptions to terminal..
     */
    public static void showScoresAndDescriptions() {
        System.out.println("================================================================================");
        System.out.println("Scores");
        System.out.println("================================================================================");
        ScoreDescriptions desc = ScoreDescriptions.getInstance();
        TetradScores.getInstance().getCommands()
                .forEach(e -> {
                    System.out.println(e);
                    System.out.println("--------------------------------------------------------------------------------");
                    System.out.println(desc.get(e));
                    System.out.println();
                    System.out.println();
                });
    }

    /**
     * Print out a list of algorithms with descriptions to terminal..
     */
    public static void showAlgorithmsAndDescriptions() {
        System.out.println("================================================================================");
        System.out.println("Algorithms");
        System.out.println("================================================================================");
        AlgorithmDescriptions desc = AlgorithmDescriptions.getInstance();
        TetradAlgorithms.getInstance().getCommands()
                .forEach(e -> {
                    System.out.println(e);
                    System.out.println("--------------------------------------------------------------------------------");
                    System.out.println(desc.get(e));
                    System.out.println();
                    System.out.println();
                });
    }

    /**
     * Print out help messages to terminal for the given options.
     *
     * @param options options to print out in help messages.
     */
    public static void showHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setWidth(-1);
        formatter.printHelp(getHelpTitle(), options, true);
    }

    /**
     * Print out help messages to terminal for the given options with message
     * footer.
     *
     * @param options options to print out in help messages.
     * @param footer message footer
     */
    public static void showHelp(Options options, String footer) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setWidth(-1);
        formatter.printHelp(getHelpTitle(), null, options, footer, true);
    }

    /**
     * Get the application version.
     *
     * @return the application version
     */
    public static String getVersion() {
        return String.format("Version %s", jarVersion());
    }

    /**
     * Formats the given {@link Date} into a date-time string.
     *
     * @param date the time value to be formatted into a date-time string.
     * @return the formatted date-time string of the given Date
     */
    public static String fmtDate(Date date) {
        return DF.format(date);
    }

    /**
     * Formats the current {@link Date} into a date-time string.
     *
     * @return the formatted date-time string of the current Date
     */
    public static String fmtDateNow() {
        return fmtDate(new Date(System.currentTimeMillis()));
    }

    /**
     * et the application jar filename.
     *
     * @return the jar filename.
     */
    public static String jarTitle() {
        return Applications.class.getPackage().getImplementationTitle();
    }

    /**
     * Get the application jar file version.
     *
     * @return the jar file version
     */
    public static String jarVersion() {
        String version = Applications.class.getPackage().getImplementationVersion();

        return (version == null) ? "unknown" : version;
    }

    /**
     * Get the title for the help message.
     *
     * @return title of the help message
     */
    private static String getHelpTitle() {
        String title = jarTitle();
        String version = jarVersion();

        return (title == null || version == null)
                ? "java -jar causal-cmd.jar"
                : String.format("java -jar %s-%s.jar", title, version);
    }

}
