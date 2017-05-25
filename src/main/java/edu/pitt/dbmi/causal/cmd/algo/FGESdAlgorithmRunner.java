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
import edu.cmu.tetrad.algcomparison.score.BdeuScore;
import edu.cmu.tetrad.data.DataSet;
import edu.cmu.tetrad.data.IKnowledge;
import edu.cmu.tetrad.util.Parameters;
import edu.pitt.dbmi.causal.cmd.ParamAttrs;
import edu.pitt.dbmi.causal.cmd.opt.algo.FGESdCmdOption;
import edu.pitt.dbmi.causal.cmd.opt.algo.TetradCmdAlgoOpt;
import edu.pitt.dbmi.causal.cmd.validation.DiscreteCategoryLimitValidation;
import edu.pitt.dbmi.causal.cmd.validation.TetradDataValidation;
import edu.pitt.dbmi.causal.cmd.validation.UniqueVariableValidation;
import edu.pitt.dbmi.data.Delimiter;
import edu.pitt.dbmi.data.reader.DataReader;
import edu.pitt.dbmi.data.reader.tabular.VerticalDiscreteTabularDataReader;
import edu.pitt.dbmi.data.validation.tabular.TabularDataValidation;
import edu.pitt.dbmi.data.validation.tabular.VerticalDiscreteTabularDataFileValidation;
import java.io.File;
import java.nio.file.Paths;
import java.util.Formatter;
import java.util.LinkedList;
import java.util.List;

/**
 *
 * Mar 14, 2017 4:48:08 PM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public class FGESdAlgorithmRunner extends AbstractAlgorithmRunner {

    public FGESdAlgorithmRunner() {
    }

    @Override
    protected Parameters getParameters(TetradCmdAlgoOpt cmdAlgoOpt) {
        FGESdCmdOption cmdOption = (FGESdCmdOption) cmdAlgoOpt;

        double structurePrior = cmdOption.getStructurePrior();
        double samplePrior = cmdOption.getSamplePrior();
        int maxDegree = cmdOption.getMaxDegree();
        boolean symmetricFirstStep = cmdOption.isSymmetricFirstStep();
        boolean faithfulnessAssumed = cmdOption.isFaithfulnessAssumed();
        boolean verbose = cmdOption.isVerbose();

        Parameters parameters = new Parameters();
        parameters.set(ParamAttrs.SAMPLE_PRIOR, samplePrior);
        parameters.set(ParamAttrs.STRUCTURE_PRIOR, structurePrior);
        parameters.set(ParamAttrs.MAX_DEGREE, maxDegree);
        parameters.set(ParamAttrs.SYMMETRIC_FIRST_STEP, symmetricFirstStep);
        parameters.set(ParamAttrs.FAITHFULNESS_ASSUMED, faithfulnessAssumed);
        parameters.set(ParamAttrs.VERBOSE, verbose);

        return parameters;
    }

    @Override
    protected Algorithm getAlgorithm(IKnowledge knowledge) {
        Fges fges = new Fges(new BdeuScore());
        if (knowledge != null) {
            fges.setKnowledge(knowledge);
        }

        return fges;
    }

    @Override
    protected List<TetradDataValidation> getDataValidations(DataSet dataSet, TetradCmdAlgoOpt cmdAlgoOpt) {
        FGESdCmdOption cmdOption = (FGESdCmdOption) cmdAlgoOpt;

        String outputDir = cmdAlgoOpt.getDirOut().toString();
        String filePrefix = cmdAlgoOpt.getOutputPrefix();
        int categoryLimit = FGESdCmdOption.CATEGORY_LIMIT;
        boolean validationOutput = cmdAlgoOpt.isValidationOutput();
        boolean skipUniqueVarName = cmdOption.isSkipUniqueVarName();
        boolean skipCategoryLimit = cmdOption.isSkipCategoryLimit();

        List<TetradDataValidation> validations = new LinkedList<>();
        if (!skipUniqueVarName) {
            if (validationOutput) {
                validations.add(new UniqueVariableValidation(dataSet, Paths.get(outputDir, filePrefix + "_duplicate_var_name.txt")));
            } else {
                validations.add(new UniqueVariableValidation(dataSet));
            }
        }
        if (!skipCategoryLimit) {
            validations.add(new DiscreteCategoryLimitValidation(dataSet, categoryLimit));
        }

        return validations;
    }

    @Override
    protected DataReader getDataReader(TetradCmdAlgoOpt cmdAlgoOpt) {
        File dataFile = cmdAlgoOpt.getDataFile().toFile();
        Delimiter delimiter = cmdAlgoOpt.getDelimiter();

        return new VerticalDiscreteTabularDataReader(dataFile, delimiter);
    }

    @Override
    protected TabularDataValidation getTabularDataValidation(TetradCmdAlgoOpt cmdAlgoOpt) {
        File dataFile = cmdAlgoOpt.getDataFile().toFile();
        Delimiter delimiter = cmdAlgoOpt.getDelimiter();

        return new VerticalDiscreteTabularDataFileValidation(dataFile, delimiter);
    }

    @Override
    protected void printParameterInfos(Formatter fmt, TetradCmdAlgoOpt cmdAlgoOpt) {
        FGESdCmdOption cmdOption = (FGESdCmdOption) cmdAlgoOpt;

        double structurePrior = cmdOption.getStructurePrior();
        double samplePrior = cmdOption.getSamplePrior();
        int maxDegree = cmdOption.getMaxDegree();
        boolean symmetricFirstStep = cmdOption.isSymmetricFirstStep();
        boolean faithfulnessAssumed = cmdOption.isFaithfulnessAssumed();

        fmt.format("sample prior = %f%n", samplePrior);
        fmt.format("structure prior = %f%n", structurePrior);
        fmt.format("max degree = %d%n", maxDegree);
        fmt.format("symmetric first step = %s%n", symmetricFirstStep);
        fmt.format("faithfulness assumed = %s%n", faithfulnessAssumed);
    }

    @Override
    protected void printValidationInfos(Formatter fmt, TetradCmdAlgoOpt cmdAlgoOpt) {
        FGESdCmdOption cmdOption = (FGESdCmdOption) cmdAlgoOpt;

        boolean skipUniqueVarName = cmdOption.isSkipUniqueVarName();
        boolean skipCategoryLimit = cmdOption.isSkipCategoryLimit();
        int categoryLimit = FGESdCmdOption.CATEGORY_LIMIT;

        fmt.format("ensure variable names are unique = %s%n", !skipUniqueVarName);
        fmt.format("limit number of categories (%d) = %s%n", categoryLimit, !skipCategoryLimit);
    }

    @Override
    protected AlgorithmType getAlgorithmType() {
        return AlgorithmType.FGESD;
    }

    @Override
    protected TetradCmdAlgoOpt getCommandLineOptions() {
        return new FGESdCmdOption();
    }

}
