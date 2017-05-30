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
import edu.cmu.tetrad.algcomparison.algorithm.oracle.pag.Gfci;
import edu.cmu.tetrad.algcomparison.independence.FisherZ;
import edu.cmu.tetrad.algcomparison.score.SemBicScore;
import edu.cmu.tetrad.data.DataSet;
import edu.cmu.tetrad.data.IKnowledge;
import edu.cmu.tetrad.util.Parameters;
import edu.pitt.dbmi.causal.cmd.ParamAttrs;
import edu.pitt.dbmi.causal.cmd.opt.algo.GFCIcCmdOption;
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
 * Mar 14, 2017 11:28:06 PM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public class GFCIcAlgorithmRunner extends AbstractAlgorithmRunner {

    public GFCIcAlgorithmRunner() {
    }

    @Override
    public Parameters getParameters(TetradCmdAlgoOpt cmdAlgoOpt) {
        GFCIcCmdOption cmdOption = (GFCIcCmdOption) cmdAlgoOpt;

        double alpha = cmdOption.getAlpha();
        double penaltyDiscount = cmdOption.getPenaltyDiscount();
        int maxDegree = cmdOption.getMaxDegree();
        int maxPathLength = cmdOption.getMaxPathLength();
        boolean faithfulnessAssumed = cmdOption.isFaithfulnessAssumed();
        boolean completeRuleSetUsed = cmdOption.isCompleteRuleSetUsed();
        boolean verbose = cmdOption.isVerbose();

        Parameters parameters = new Parameters();
        parameters.set(ParamAttrs.ALPHA, alpha);
        parameters.set(ParamAttrs.PENALTY_DISCOUNT, penaltyDiscount);
        parameters.set(ParamAttrs.MAX_DEGREE, maxDegree);
        parameters.set(ParamAttrs.MAX_PATH_LENGTH, maxPathLength);
        parameters.set(ParamAttrs.FAITHFULNESS_ASSUMED, faithfulnessAssumed);
        parameters.set(ParamAttrs.COMPLETE_RULE_SET_USED, completeRuleSetUsed);
        parameters.set(ParamAttrs.VERBOSE, verbose);

        return parameters;
    }

    @Override
    public Algorithm getAlgorithm(IKnowledge knowledge) {
        Gfci gfci = new Gfci(new FisherZ(), new SemBicScore());
        if (knowledge != null) {
            gfci.setKnowledge(knowledge);
        }

        return gfci;
    }

    @Override
    protected List<TetradDataValidation> getDataValidations(DataSet dataSet, TetradCmdAlgoOpt cmdAlgoOpt) {
        GFCIcCmdOption cmdOption = (GFCIcCmdOption) cmdAlgoOpt;

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
        if (!cmdOption.isSkipNonZeroVariance()) {
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
        GFCIcCmdOption cmdOption = (GFCIcCmdOption) cmdAlgoOpt;

        double alpha = cmdOption.getAlpha();
        double penaltyDiscount = cmdOption.getPenaltyDiscount();
        int maxDegree = cmdOption.getMaxDegree();
        int maxPathLength = cmdOption.getMaxPathLength();
        boolean faithfulnessAssumed = cmdOption.isFaithfulnessAssumed();
        boolean completeRuleSetUsed = cmdOption.isCompleteRuleSetUsed();

        fmt.format("alpha = %f%n", alpha);
        fmt.format("penalty discount = %f%n", penaltyDiscount);
        fmt.format("max degree = %d%n", maxDegree);
        fmt.format("max path length = %d%n", maxPathLength);
        fmt.format("faithfulness assumed = %s%n", faithfulnessAssumed);
        fmt.format("complete rule set used = %s%n", completeRuleSetUsed);
    }

    @Override
    public void printValidationInfos(Formatter fmt, TetradCmdAlgoOpt cmdAlgoOpt) {
        GFCIcCmdOption cmdOption = (GFCIcCmdOption) cmdAlgoOpt;

        boolean skipUniqueVarName = cmdOption.isSkipUniqueVarName();
        boolean skipZeroVariance = cmdOption.isSkipNonZeroVariance();

        fmt.format("ensure variable names are unique = %s%n", !skipUniqueVarName);
        fmt.format("ensure variables have non-zero variance = %s%n", !skipZeroVariance);
    }

    @Override
    public AlgorithmType getAlgorithmType() {
        return AlgorithmType.GFCIC;
    }

    @Override
    public TetradCmdAlgoOpt getCommandLineOptions() {
        return new GFCIcCmdOption();
    }

}
