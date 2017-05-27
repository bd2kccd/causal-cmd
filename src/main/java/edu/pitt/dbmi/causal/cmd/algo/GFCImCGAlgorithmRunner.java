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
import edu.cmu.tetrad.algcomparison.independence.ConditionalGaussianLRT;
import edu.cmu.tetrad.algcomparison.score.ConditionalGaussianBicScore;
import edu.cmu.tetrad.data.DataSet;
import edu.cmu.tetrad.data.IKnowledge;
import edu.cmu.tetrad.util.Parameters;
import edu.pitt.dbmi.causal.cmd.ParamAttrs;
import edu.pitt.dbmi.causal.cmd.opt.algo.GFCImCGCmdOption;
import edu.pitt.dbmi.causal.cmd.opt.algo.TetradCmdAlgoOpt;
import edu.pitt.dbmi.causal.cmd.validation.TetradDataValidation;
import edu.pitt.dbmi.data.Delimiter;
import edu.pitt.dbmi.data.reader.DataReader;
import edu.pitt.dbmi.data.reader.tabular.MixedTabularDataFileReader;
import edu.pitt.dbmi.data.validation.tabular.MixedTabularDataFileValidation;
import edu.pitt.dbmi.data.validation.tabular.TabularDataValidation;
import java.io.File;
import java.util.Formatter;
import java.util.LinkedList;
import java.util.List;

/**
 * GFCI conditional Gaussian score for mixed variables.
 *
 * May 26, 2017 11:29:20 PM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public class GFCImCGAlgorithmRunner extends AbstractAlgorithmRunner {

    public GFCImCGAlgorithmRunner() {
    }

    @Override
    protected Parameters getParameters(TetradCmdAlgoOpt cmdAlgoOpt) {
        GFCImCGCmdOption cmdOption = (GFCImCGCmdOption) cmdAlgoOpt;

        double alpha = cmdOption.getAlpha();
        double penaltyDiscount = cmdOption.getPenaltyDiscount();
        double structurePrior = cmdOption.getStructurePrior();
        int numCategoriesToDiscretize = cmdOption.getNumCategoriesToDiscretize();
        boolean discretize = cmdOption.isDiscretize();
        int maxDegree = cmdOption.getMaxDegree();
        int maxPathLength = cmdOption.getMaxPathLength();
        boolean faithfulnessAssumed = cmdOption.isFaithfulnessAssumed();
        boolean completeRuleSetUsed = cmdOption.isCompleteRuleSetUsed();
        boolean verbose = cmdOption.isVerbose();

        Parameters parameters = new Parameters();
        parameters.set(ParamAttrs.ALPHA, alpha);
        parameters.set(ParamAttrs.PENALTY_DISCOUNT, penaltyDiscount);
        parameters.set(ParamAttrs.STRUCTURE_PRIOR, structurePrior);
        parameters.set(ParamAttrs.DISCRETIZE, discretize);
        parameters.set(ParamAttrs.NUM_CATEGORIES_TO_DISCRETIZE, numCategoriesToDiscretize);
        parameters.set(ParamAttrs.MAX_DEGREE, maxDegree);
        parameters.set(ParamAttrs.MAX_PATH_LENGTH, maxPathLength);
        parameters.set(ParamAttrs.FAITHFULNESS_ASSUMED, faithfulnessAssumed);
        parameters.set(ParamAttrs.COMPLETE_RULE_SET_USED, completeRuleSetUsed);
        parameters.set(ParamAttrs.VERBOSE, verbose);

        return parameters;
    }

    @Override
    public Algorithm getAlgorithm(IKnowledge knowledge) {
        Gfci gfci = new Gfci(new ConditionalGaussianLRT(), new ConditionalGaussianBicScore());
        if (knowledge != null) {
            gfci.setKnowledge(knowledge);
        }

        return gfci;
    }

    @Override
    protected List<TetradDataValidation> getDataValidations(DataSet dataSet, TetradCmdAlgoOpt cmdAlgoOpt) {
        List<TetradDataValidation> validations = new LinkedList<>();

        return validations;
    }

    @Override
    protected DataReader getDataReader(TetradCmdAlgoOpt cmdAlgoOpt) {
        GFCImCGCmdOption cmdOption = (GFCImCGCmdOption) cmdAlgoOpt;
        File dataFile = cmdOption.getDataFile().toFile();
        Delimiter delimiter = cmdOption.getDelimiter();
        int numberOfDiscreteCategories = cmdOption.getNumberOfDiscreteCategories();

        return new MixedTabularDataFileReader(numberOfDiscreteCategories, dataFile, delimiter);
    }

    @Override
    protected TabularDataValidation getTabularDataValidation(TetradCmdAlgoOpt cmdAlgoOpt) {
        GFCImCGCmdOption cmdOption = (GFCImCGCmdOption) cmdAlgoOpt;
        File dataFile = cmdOption.getDataFile().toFile();
        Delimiter delimiter = cmdOption.getDelimiter();
        int numberOfDiscreteCategories = cmdOption.getNumberOfDiscreteCategories();

        return new MixedTabularDataFileValidation(numberOfDiscreteCategories, dataFile, delimiter);
    }

    @Override
    protected void printParameterInfos(Formatter fmt, TetradCmdAlgoOpt cmdAlgoOpt) {
        GFCImCGCmdOption cmdOption = (GFCImCGCmdOption) cmdAlgoOpt;

        double alpha = cmdOption.getAlpha();
        double penaltyDiscount = cmdOption.getPenaltyDiscount();
        double structurePrior = cmdOption.getStructurePrior();
        int numCategoriesToDiscretize = cmdOption.getNumCategoriesToDiscretize();
        boolean discretize = cmdOption.isDiscretize();
        int maxDegree = cmdOption.getMaxDegree();
        int maxPathLength = cmdOption.getMaxPathLength();
        boolean faithfulnessAssumed = cmdOption.isFaithfulnessAssumed();
        boolean completeRuleSetUsed = cmdOption.isCompleteRuleSetUsed();

        fmt.format("alpha = %f%n", alpha);
        fmt.format("penalty discount = %f%n", penaltyDiscount);
        fmt.format("structure prior = %f%n", structurePrior);
        fmt.format("number of categories to discretize = %d%n", numCategoriesToDiscretize);
        fmt.format("discretize = %s%n", discretize);
        fmt.format("max degree = %d%n", maxDegree);
        fmt.format("max path length = %d%n", maxPathLength);
        fmt.format("faithfulness assumed = %s%n", faithfulnessAssumed);
        fmt.format("complete rule set used = %s%n", completeRuleSetUsed);
    }

    @Override
    protected void printValidationInfos(Formatter fmt, TetradCmdAlgoOpt cmdAlgoOpt) {
    }

    @Override
    protected AlgorithmType getAlgorithmType() {
        return AlgorithmType.GFCIM_CG;
    }

    @Override
    protected TetradCmdAlgoOpt getCommandLineOptions() {
        return new GFCImCGCmdOption();
    }

}
