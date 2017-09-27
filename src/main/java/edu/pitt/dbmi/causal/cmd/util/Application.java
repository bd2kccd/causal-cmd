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
public class Application {

    private static final DateFormat DF = new SimpleDateFormat("EEE, MMMM dd, yyyy hh:mm:ss a");

    private Application() {
    }

    public static void showHelp(Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setWidth(-1);
        formatter.printHelp(getHelpTitle(), options, true);
    }

    public static void showHelp(String algorithmName, Options options) {
        HelpFormatter formatter = new HelpFormatter();
        formatter.setWidth(-1);
        formatter.printHelp(String.format("%s --algorithm %s", getHelpTitle(), algorithmName), options, true);
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
        return Application.class.getPackage().getImplementationTitle();
    }

    public static String jarVersion() {
        String version = Application.class.getPackage().getImplementationVersion();

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
