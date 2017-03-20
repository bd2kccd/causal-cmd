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
package edu.pitt.dbmi.causal.cmd.algo;

import edu.cmu.tetrad.algcomparison.algorithm.Algorithm;
import edu.cmu.tetrad.algcomparison.algorithm.oracle.pattern.Fges;
import edu.cmu.tetrad.algcomparison.score.SemBicScore;
import edu.cmu.tetrad.data.DataSet;
import edu.cmu.tetrad.data.IKnowledge;
import edu.cmu.tetrad.graph.Graph;
import edu.cmu.tetrad.util.Parameters;
import edu.pitt.dbmi.causal.cmd.ParamAttrs;
import edu.pitt.dbmi.causal.cmd.opt.algo.FGEScCmdOption;
import edu.pitt.dbmi.causal.cmd.opt.algo.TetradCmdAlgoOpt;
import edu.pitt.dbmi.causal.cmd.validation.NonZeroVarianceValidation;
import edu.pitt.dbmi.causal.cmd.validation.TetradDataValidation;
import edu.pitt.dbmi.causal.cmd.validation.UniqueVariableValidation;
import edu.pitt.dbmi.data.Delimiter;
import edu.pitt.dbmi.data.reader.DataReader;
import edu.pitt.dbmi.data.reader.tabular.ContinuousTabularDataFileReader;
import edu.pitt.dbmi.data.validation.tabular.ContinuousTabularDataFileValidation;
import edu.pitt.dbmi.data.validation.tabular.TabularDataValidation;
import java.io.File;
import java.nio.file.Paths;
import java.util.Formatter;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * Mar 13, 2017 11:23:56 AM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public class FGEScAlgorithmRunner extends AbstractAlgorithmRunner {

    public FGEScAlgorithmRunner() {
    }

    @Override
    protected String graphToString(Graph graph) {
        return (graph == null) ? "" : graph.toString().trim();
    }

    @Override
    protected Parameters getParameters(TetradCmdAlgoOpt cmdAlgoOpt) {
        FGEScCmdOption cmdOption = (FGEScCmdOption) cmdAlgoOpt;

        double penaltyDiscount = cmdOption.getPenaltyDiscount();
        int maxDegree = cmdOption.getMaxDegree();
        boolean faithfulnessAssumed = cmdOption.isFaithfulnessAssumed();
        boolean verbose = cmdOption.isVerbose();

        Parameters parameters = new Parameters();
        parameters.set(ParamAttrs.PENALTY_DISCOUNT, penaltyDiscount);
        parameters.set(ParamAttrs.MAX_DEGREE, maxDegree);
        parameters.set(ParamAttrs.FAITHFULNESS_ASSUMED, faithfulnessAssumed);
        parameters.set(ParamAttrs.VERBOSE, verbose);

        return parameters;
    }

    @Override
    protected Algorithm getAlgorithm(IKnowledge knowledge) {
        Fges fges = new Fges(new SemBicScore());
        if (knowledge != null) {
            fges.setKnowledge(knowledge);
        }

        return fges;
    }

    @Override
    protected List<TetradDataValidation> getDataValidations(DataSet dataSet, TetradCmdAlgoOpt cmdAlgoOpt) {
        FGEScCmdOption cmdOption = (FGEScCmdOption) cmdAlgoOpt;

        String outputDir = cmdAlgoOpt.getDirOut().toString();
        String filePrefix = cmdAlgoOpt.getOutputPrefix();
        boolean validationOutput = cmdAlgoOpt.isValidationOutput();
        int numOfThreads = cmdAlgoOpt.getNumOfThreads();

        List<TetradDataValidation> validations = new LinkedList<>();
        if (!cmdOption.isSkipUniqueVarName()) {
            if (validationOutput) {
                validations.add(new UniqueVariableValidation(dataSet, Paths.get(outputDir, filePrefix + "_duplicate_var_name.txt")));
            } else {
                validations.add(new UniqueVariableValidation(dataSet));
            }
        }
        if (!cmdOption.isSkipZeroVariance()) {
            if (validationOutput) {
                validations.add(new NonZeroVarianceValidation(dataSet, numOfThreads, Paths.get(outputDir, filePrefix + "_zero_variance.txt")));
            } else {
                validations.add(new NonZeroVarianceValidation(dataSet, numOfThreads));
            }
        }

        return validations;
    }

    @Override
    protected DataReader getDataReader(TetradCmdAlgoOpt cmdAlgoOpt) {
        File dataFile = cmdAlgoOpt.getDataFile().toFile();
        Delimiter delimiter = cmdAlgoOpt.getDelimiter();

        return new ContinuousTabularDataFileReader(dataFile, delimiter);
    }

    @Override
    protected TabularDataValidation getTabularDataValidation(TetradCmdAlgoOpt cmdAlgoOpt) {
        File dataFile = cmdAlgoOpt.getDataFile().toFile();
        Delimiter delimiter = cmdAlgoOpt.getDelimiter();

        return new ContinuousTabularDataFileValidation(dataFile, delimiter);
    }

    @Override
    public void printParameterInfos(Formatter fmt, TetradCmdAlgoOpt cmdAlgoOpt) {
        FGEScCmdOption cmdOption = (FGEScCmdOption) cmdAlgoOpt;

        double penaltyDiscount = cmdOption.getPenaltyDiscount();
        int maxDegree = cmdOption.getMaxDegree();
        boolean faithfulnessAssumed = cmdOption.isFaithfulnessAssumed();

        fmt.format("penalty discount = %f%n", penaltyDiscount);
        fmt.format("max degree = %d%n", maxDegree);
        fmt.format("faithfulness assumed = %s%n", faithfulnessAssumed);
    }

    @Override
    public void printValidationInfos(Formatter fmt, TetradCmdAlgoOpt cmdAlgoOpt) {
        FGEScCmdOption cmdOption = (FGEScCmdOption) cmdAlgoOpt;

        boolean skipUniqueVarName = cmdOption.isSkipUniqueVarName();
        boolean skipZeroVariance = cmdOption.isSkipZeroVariance();

        fmt.format("ensure variable names are unique = %s%n", !skipUniqueVarName);
        fmt.format("ensure variables have non-zero variance = %s%n", !skipZeroVariance);
    }

    @Override
    protected AlgorithmType getAlgorithmType() {
        return AlgorithmType.FGESC;
    }

    @Override
    protected TetradCmdAlgoOpt getCommandLineOptions() {
        return new FGEScCmdOption();
    }

}
