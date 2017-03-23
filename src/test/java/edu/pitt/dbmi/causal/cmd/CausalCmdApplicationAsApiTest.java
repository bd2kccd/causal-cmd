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

import edu.cmu.tetrad.algcomparison.algorithm.oracle.pattern.Fges;
import edu.cmu.tetrad.algcomparison.score.SemBicScore;
import edu.cmu.tetrad.data.DataModel;
import edu.cmu.tetrad.data.DataSet;
import edu.cmu.tetrad.graph.Graph;
import edu.cmu.tetrad.util.Parameters;
import edu.pitt.dbmi.causal.cmd.util.TetradDataUtils;
import edu.pitt.dbmi.causal.cmd.validation.TetradDataValidation;
import edu.pitt.dbmi.causal.cmd.validation.UniqueVariableValidation;
import edu.pitt.dbmi.data.Dataset;
import edu.pitt.dbmi.data.Delimiter;
import edu.pitt.dbmi.data.reader.tabular.ContinuousTabularDataFileReader;
import edu.pitt.dbmi.data.reader.tabular.TabularDataReader;
import edu.pitt.dbmi.data.validation.ValidationCode;
import edu.pitt.dbmi.data.validation.ValidationResult;
import edu.pitt.dbmi.data.validation.tabular.ContinuousTabularDataFileValidation;
import edu.pitt.dbmi.data.validation.tabular.TabularDataValidation;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * Mar 23, 2017 4:54:29 PM
 *
 * @author Kevin V. Bui (kvb2@pitt.edu)
 */
public class CausalCmdApplicationAsApiTest {

    @Test
    public void test() throws IOException {
        // set the data file and its properites
        Path dataFile = Paths.get("test", "data", "cmu", "Retention.txt");
        Delimiter delimiter = Delimiter.TAB;
        char quoteCharacter = '"';
        String missingValueMarker = "*";
        String commentMarker = "//";

        // ensure the data file is valid format
        TabularDataValidation dataFileValidation = new ContinuousTabularDataFileValidation(dataFile.toFile(), delimiter);
        dataFileValidation.setQuoteCharacter(quoteCharacter);
        dataFileValidation.setMissingValueMarker(missingValueMarker);
        dataFileValidation.setCommentMarker(commentMarker);
        dataFileValidation.validate();

        // ensure there is no error
        int errorCount = 0;
        List<ValidationResult> fileValidResults = dataFileValidation.getValidationResults();
        for (ValidationResult validation : fileValidResults) {
            if (validation.getCode() == ValidationCode.ERROR) {
                errorCount++;
            }
        }
        Assert.assertTrue(errorCount == 0);

        // read in data
        TabularDataReader reader = new ContinuousTabularDataFileReader(dataFile.toFile(), delimiter);
        reader.setQuoteCharacter(quoteCharacter);
        reader.setMissingValueMarker(missingValueMarker);
        reader.setCommentMarker(commentMarker);
        Dataset dataset = reader.readInData();

        // convert to Tetrad data
        DataModel dataModel = TetradDataUtils.toDataModel(dataset);

        // ensure the data read in is valid
        TetradDataValidation dataValidation = new UniqueVariableValidation((DataSet) dataModel);
        boolean isValidData = dataValidation.validate(System.err, true);
        Assert.assertTrue(isValidData);

        //set algorithm parameters
        Parameters parameters = new Parameters();
        parameters.set(ParamAttrs.PENALTY_DISCOUNT, 2.0);
        parameters.set(ParamAttrs.MAX_DEGREE, -1);
        parameters.set(ParamAttrs.FAITHFULNESS_ASSUMED, false);
        parameters.set(ParamAttrs.VERBOSE, false);

        Fges fges = new Fges(new SemBicScore());

        Graph graph = fges.search(dataModel, parameters);
    }

}
