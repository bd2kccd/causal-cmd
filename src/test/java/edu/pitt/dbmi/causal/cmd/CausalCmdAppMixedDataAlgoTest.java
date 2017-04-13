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

import edu.cmu.tetrad.data.DataModel;
import edu.cmu.tetrad.data.DataSet;
import edu.cmu.tetrad.graph.Graph;
import edu.cmu.tetrad.search.IndTestConditionalGaussianLRT;
import edu.cmu.tetrad.search.PcMax;
import edu.pitt.dbmi.causal.cmd.util.TetradDataUtils;
import edu.pitt.dbmi.data.Dataset;
import edu.pitt.dbmi.data.Delimiter;
import edu.pitt.dbmi.data.reader.tabular.MixedTabularDataFileReader;
import edu.pitt.dbmi.data.reader.tabular.TabularDataReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import org.junit.Test;

/**
 *
 * Apr 10, 2017 4:42:15 PM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public class CausalCmdAppMixedDataAlgoTest {

    @Test
    public void testFGESm() throws IOException {
        Path dataFile = Paths.get("test", "data", "mixed", "sim_data_20vars_100cases_5categories.txt");
        Delimiter delimiter = Delimiter.TAB;

        int numOfDiscreteCat = 5;

        TabularDataReader reader = new MixedTabularDataFileReader(numOfDiscreteCat, dataFile.toFile(), delimiter);
        Dataset dataset = reader.readInData();

        DataModel dataModel = TetradDataUtils.toDataModel(dataset);
        DataSet dataSet = (DataSet) dataModel;

        double alpha = 0.001;
        IndTestConditionalGaussianLRT indTest = new IndTestConditionalGaussianLRT(dataSet, alpha);
        PcMax pcMax = new PcMax(indTest);

        Graph graph = pcMax.search();
        System.out.println(graph);
    }

}
