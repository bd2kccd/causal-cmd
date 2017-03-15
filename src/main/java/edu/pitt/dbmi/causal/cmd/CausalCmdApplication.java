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
package edu.pitt.dbmi.causal.cmd;

import edu.pitt.dbmi.causal.cmd.algo.AlgorithmType;
import edu.pitt.dbmi.causal.cmd.algo.FGEScAlgorithmRunner;
import edu.pitt.dbmi.causal.cmd.algo.FGESdAlgorithmRunner;
import edu.pitt.dbmi.causal.cmd.algo.GFCIcAlgorithmRunner;
import edu.pitt.dbmi.causal.cmd.algo.GFCIdAlgorithmRunner;
import edu.pitt.dbmi.causal.cmd.sim.BayNetRandFwdDataSimulationRunner;
import edu.pitt.dbmi.causal.cmd.sim.DataSimulationType;
import edu.pitt.dbmi.causal.cmd.sim.SemRandFwdDataSimulationRunner;
import edu.pitt.dbmi.causal.cmd.util.AppUtils;
import edu.pitt.dbmi.causal.cmd.util.Args;
import java.util.Map;
import java.util.TreeMap;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;

/**
 *
 * Mar 8, 2017 6:11:17 PM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public class CausalCmdApplication {

    private static final Options MAIN_OPTIONS = new Options();

    private static final String ALGO_OPT = "algorithm";
    private static final String SIM_DATA_OPT = "simulate-data";
    private static final String VERSION_OPT = "version";

    private static final Map<String, AlgorithmType> ALGO_TYPES = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);
    private static final Map<String, DataSimulationType> DATA_SIM_TYPES = new TreeMap<>(String.CASE_INSENSITIVE_ORDER);

    static {
        populateMainOptions();
        populateCmdTypes();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        if (Args.hasLongOption(args, VERSION_OPT)) {
            System.out.println(AppUtils.jarTitle() + " version " + AppUtils.jarVersion());
        } else {
            boolean algoOpt = Args.hasLongOption(args, ALGO_OPT);
            boolean simDataOpt = Args.hasLongOption(args, SIM_DATA_OPT);
            if (algoOpt ^ simDataOpt) {
                if (algoOpt) {
                    String algorithm = Args.getOptionValue(args, ALGO_OPT);
                    AlgorithmType algorithmType = ALGO_TYPES.get(algorithm);
                    if (algorithmType == null) {
                        showHelp();
                    } else {
                        args = Args.removeOption(args, ALGO_OPT);
                        switch (algorithmType) {
                            case FGESC:
                                new FGEScAlgorithmRunner().runAlgorithm(args);
                                break;
                            case FGESD:
                                new FGESdAlgorithmRunner().runAlgorithm(args);
                                break;
                            case GFCIC:
                                new GFCIcAlgorithmRunner().runAlgorithm(args);
                                break;
                            case GFCID:
                                new GFCIdAlgorithmRunner().runAlgorithm(args);
                                break;
                        }
                    }
                } else {
                    String simulation = Args.getOptionValue(args, SIM_DATA_OPT);
                    DataSimulationType dataSimulationType = DATA_SIM_TYPES.get(simulation);
                    if (dataSimulationType == null) {
                        showHelp();
                    } else {
                        args = Args.removeOption(args, SIM_DATA_OPT);
                        switch (dataSimulationType) {
                            case BAYES_NET_RAND_FWD:
                                new BayNetRandFwdDataSimulationRunner().runDataSimulation(args);
                                break;
                            case SEM_RAND_FWD:
                                new SemRandFwdDataSimulationRunner().runDataSimulation(args);
                                break;
                        }
                    }
                }
            } else {
                showHelp();
            }
        }
    }

    private static void showHelp() {
        AppUtils.showHelp(MAIN_OPTIONS, "Additional parameters are available when using --algorithm <arg> or --simulate-data <arg>.");
    }

    private static void populateCmdTypes() {
        for (AlgorithmType type : AlgorithmType.values()) {
            ALGO_TYPES.put(type.getCmd(), type);
        }
        for (DataSimulationType type : DataSimulationType.values()) {
            DATA_SIM_TYPES.put(type.getCmd(), type);
        }
    }

    private static void populateMainOptions() {
        OptionGroup optGrp = new OptionGroup();
        optGrp.addOption(new Option(null, ALGO_OPT, true, algorithmCmd()));
        optGrp.addOption(new Option(null, SIM_DATA_OPT, true, simulationCmd()));
        optGrp.setRequired(true);
        MAIN_OPTIONS.addOptionGroup(optGrp);

        MAIN_OPTIONS.addOption(null, VERSION_OPT, false, "Show software version.");
    }

    private static String algorithmCmd() {
        StringBuilder algoOpt = new StringBuilder();
        AlgorithmType[] types = AlgorithmType.values();
        int lastIndex = types.length - 1;
        for (int i = 0; i < lastIndex; i++) {
            algoOpt.append(types[i].getCmd());
            algoOpt.append(", ");
        }
        algoOpt.append(types[lastIndex].getCmd());

        return algoOpt.toString();
    }

    private static String simulationCmd() {
        StringBuilder algoOpt = new StringBuilder();
        DataSimulationType[] types = DataSimulationType.values();
        int lastIndex = types.length - 1;
        for (int i = 0; i < lastIndex; i++) {
            algoOpt.append(types[i].getCmd());
            algoOpt.append(", ");
        }
        algoOpt.append(types[lastIndex].getCmd());

        return algoOpt.toString();
    }

}
