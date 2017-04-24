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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;

/**
 *
 * Mar 10, 2017 1:26:52 PM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public class AppUtils {

    private static final DateFormat DF = new SimpleDateFormat("EEE, MMMM dd, yyyy hh:mm:ss a");

    private static final String usageOf = "java -jar <causal-cmd-jarfile>";

    private AppUtils() {
    }

    public static String fmtDate(Date date) {
        return DF.format(date);
    }

    public static String fmtDateNow() {
        return fmtDate(new Date(System.currentTimeMillis()));
    }

    public static String jarTitle() {
        return AppUtils.class.getPackage().getImplementationTitle();
    }

    public static String jarVersion() {
        return AppUtils.class.getPackage().getImplementationVersion();
    }

    public static void showHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setWidth(-1);
        formatter.printHelp(usageOf, options, true);
    }

    public static void showHelp(Options options, String footer) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setWidth(-1);
        formatter.printHelp(usageOf, null, options, footer, true);
    }

    public static void showHelp(String algorithmName, Options options) {
        String cmdLineSyntax = String.format("%s --algorithm %s", usageOf, algorithmName);

        HelpFormatter formatter = new HelpFormatter();
        formatter.setWidth(-1);
        formatter.printHelp(cmdLineSyntax, options, true);
    }

}
