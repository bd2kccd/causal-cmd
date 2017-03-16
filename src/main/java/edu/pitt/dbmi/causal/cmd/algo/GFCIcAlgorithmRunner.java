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
import edu.cmu.tetrad.data.IKnowledge;
import edu.cmu.tetrad.util.Parameters;
import edu.pitt.dbmi.causal.cmd.ParamAttrs;
import edu.pitt.dbmi.causal.cmd.opt.algo.GFCIcCmdOption;
import edu.pitt.dbmi.causal.cmd.opt.algo.TetradCmdAlgoOpt;
import java.util.Formatter;

/**
 *
 * Mar 14, 2017 11:28:06 PM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public class GFCIcAlgorithmRunner extends FGEScAlgorithmRunner {

    public GFCIcAlgorithmRunner() {
    }

    @Override
    public Parameters getParameters(TetradCmdAlgoOpt cmdAlgoOpt) {
        GFCIcCmdOption cmdOption = (GFCIcCmdOption) cmdAlgoOpt;

        double alpha = cmdOption.getAlpha();

        Parameters parameters = super.getParameters(cmdAlgoOpt);
        parameters.set(ParamAttrs.ALPHA, alpha);

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
    public void printParameterInfos(Formatter fmt, TetradCmdAlgoOpt cmdAlgoOpt) {
        GFCIcCmdOption cmdOption = (GFCIcCmdOption) cmdAlgoOpt;

        double alpha = cmdOption.getAlpha();

        fmt.format("alpha = %f%n", alpha);
        super.printParameterInfos(fmt, cmdAlgoOpt);
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
