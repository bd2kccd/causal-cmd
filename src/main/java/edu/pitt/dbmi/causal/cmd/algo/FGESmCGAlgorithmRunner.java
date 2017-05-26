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
import edu.cmu.tetrad.algcomparison.score.ConditionalGaussianBicScore;
import edu.cmu.tetrad.data.DataSet;
import edu.cmu.tetrad.data.IKnowledge;
import edu.cmu.tetrad.util.Parameters;
import edu.pitt.dbmi.causal.cmd.ParamAttrs;
import edu.pitt.dbmi.causal.cmd.opt.algo.FGESmCGCmdOption;
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
 *
 * May 26, 2017 12:14:35 AM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public class FGESmCGAlgorithmRunner extends AbstractAlgorithmRunner {

    public FGESmCGAlgorithmRunner() {
        super();
    }

    @Override
    protected Parameters getParameters(TetradCmdAlgoOpt cmdAlgoOpt) {
        FGESmCGCmdOption cmdOption = (FGESmCGCmdOption) cmdAlgoOpt;

        double penaltyDiscount = cmdOption.getPenaltyDiscount();
        double structurePrior = cmdOption.getStructurePrior();
        boolean discretize = cmdOption.isDiscretize();
        int numCategoriesToDiscretize = cmdOption.getNumCategoriesToDiscretize();
        int maxDegree = cmdOption.getMaxDegree();
        boolean symmetricFirstStep = cmdOption.isSymmetricFirstStep();
        boolean faithfulnessAssumed = cmdOption.isFaithfulnessAssumed();

        boolean verbose = cmdOption.isVerbose();

        Parameters parameters = new Parameters();
        parameters.set(ParamAttrs.PENALTY_DISCOUNT, penaltyDiscount);
        parameters.set(ParamAttrs.STRUCTURE_PRIOR, structurePrior);
        parameters.set(ParamAttrs.DISCRETIZE, discretize);
        parameters.set(ParamAttrs.NUM_CATEGORIES_TO_DISCRETIZE, numCategoriesToDiscretize);
        parameters.set(ParamAttrs.MAX_DEGREE, maxDegree);
        parameters.set(ParamAttrs.SYMMETRIC_FIRST_STEP, symmetricFirstStep);
        parameters.set(ParamAttrs.FAITHFULNESS_ASSUMED, faithfulnessAssumed);
        parameters.set(ParamAttrs.VERBOSE, verbose);

        return parameters;
    }

    @Override
    protected Algorithm getAlgorithm(IKnowledge knowledge) {
        Fges fges = new Fges(new ConditionalGaussianBicScore());
        if (knowledge != null) {
            fges.setKnowledge(knowledge);
        }

        return fges;
    }

    @Override
    protected List<TetradDataValidation> getDataValidations(DataSet dataSet, TetradCmdAlgoOpt cmdAlgoOpt) {
        List<TetradDataValidation> validations = new LinkedList<>();

        return validations;
    }

    @Override
    protected DataReader getDataReader(TetradCmdAlgoOpt cmdAlgoOpt) {
        FGESmCGCmdOption cmdOption = (FGESmCGCmdOption) cmdAlgoOpt;
        File dataFile = cmdOption.getDataFile().toFile();
        Delimiter delimiter = cmdOption.getDelimiter();
        int numberOfDiscreteCategories = cmdOption.getNumberOfDiscreteCategories();

        return new MixedTabularDataFileReader(numberOfDiscreteCategories, dataFile, delimiter);
    }

    @Override
    protected TabularDataValidation getTabularDataValidation(TetradCmdAlgoOpt cmdAlgoOpt) {
        FGESmCGCmdOption cmdOption = (FGESmCGCmdOption) cmdAlgoOpt;
        File dataFile = cmdOption.getDataFile().toFile();
        Delimiter delimiter = cmdOption.getDelimiter();
        int numberOfDiscreteCategories = cmdOption.getNumberOfDiscreteCategories();

        return new MixedTabularDataFileValidation(numberOfDiscreteCategories, dataFile, delimiter);
    }

    @Override
    protected void printParameterInfos(Formatter fmt, TetradCmdAlgoOpt cmdAlgoOpt) {
        FGESmCGCmdOption cmdOption = (FGESmCGCmdOption) cmdAlgoOpt;

        double penaltyDiscount = cmdOption.getPenaltyDiscount();
        double structurePrior = cmdOption.getStructurePrior();
        boolean discretize = cmdOption.isDiscretize();
        int numCategoriesToDiscretize = cmdOption.getNumCategoriesToDiscretize();
        int maxDegree = cmdOption.getMaxDegree();
        boolean symmetricFirstStep = cmdOption.isSymmetricFirstStep();
        boolean faithfulnessAssumed = cmdOption.isFaithfulnessAssumed();

        fmt.format("penalty discount = %f%n", penaltyDiscount);
        fmt.format("structure prior = %f%n", structurePrior);
        fmt.format("discretize = %s%n", discretize);
        fmt.format("number of categories to discretize = %d%n", numCategoriesToDiscretize);
        fmt.format("max degree = %d%n", maxDegree);
        fmt.format("symmetric first step = %s%n", symmetricFirstStep);
        fmt.format("faithfulness assumed = %s%n", faithfulnessAssumed);
    }

    @Override
    protected void printValidationInfos(Formatter fmt, TetradCmdAlgoOpt cmdAlgoOpt) {
    }

    @Override
    protected AlgorithmType getAlgorithmType() {
        return AlgorithmType.FGESM_CG;
    }

    @Override
    protected TetradCmdAlgoOpt getCommandLineOptions() {
        return new FGESmCGCmdOption();
    }

}
