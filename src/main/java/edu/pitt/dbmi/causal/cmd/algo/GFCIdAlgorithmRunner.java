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
import edu.cmu.tetrad.algcomparison.independence.ChiSquare;
import edu.cmu.tetrad.algcomparison.score.BdeuScore;
import edu.cmu.tetrad.data.IKnowledge;
import edu.cmu.tetrad.graph.Graph;
import edu.cmu.tetrad.graph.GraphUtils;
import edu.cmu.tetrad.util.Parameters;
import edu.pitt.dbmi.causal.cmd.ParamAttrs;
import edu.pitt.dbmi.causal.cmd.opt.algo.GFCIdCmdOption;
import edu.pitt.dbmi.causal.cmd.opt.algo.TetradCmdAlgoOpt;
import java.util.Formatter;

/**
 *
 * Mar 15, 2017 5:03:37 PM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public class GFCIdAlgorithmRunner extends FGESdAlgorithmRunner {

    public GFCIdAlgorithmRunner() {
    }

    @Override
    protected String graphToString(Graph graph) {
        if (graph == null) {
            return "";
        }

        GraphUtils.addPagColoring(graph);

        return GraphUtils.graphToText(graph).trim();
    }

    @Override
    protected Parameters getParameters(TetradCmdAlgoOpt cmdAlgoOpt) {
        GFCIdCmdOption cmdOption = (GFCIdCmdOption) cmdAlgoOpt;

        double alpha = cmdOption.getAlpha();

        Parameters parameters = super.getParameters(cmdAlgoOpt);
        parameters.set(ParamAttrs.ALPHA, alpha);

        return parameters;
    }

    @Override
    protected Algorithm getAlgorithm(IKnowledge knowledge) {
        Gfci gfci = new Gfci(new ChiSquare(), new BdeuScore());
        if (knowledge != null) {
            gfci.setKnowledge(knowledge);
        }

        return gfci;
    }

    @Override
    protected void printParameterInfos(Formatter fmt, TetradCmdAlgoOpt cmdAlgoOpt) {
        GFCIdCmdOption cmdOption = (GFCIdCmdOption) cmdAlgoOpt;

        double alpha = cmdOption.getAlpha();

        fmt.format("alpha = %f%n", alpha);
        super.printParameterInfos(fmt, cmdAlgoOpt);
    }

    @Override
    protected AlgorithmType getAlgorithmType() {
        return AlgorithmType.GFCID;
    }

    @Override
    protected TetradCmdAlgoOpt getCommandLineOptions() {
        return new GFCIdCmdOption();
    }

}
