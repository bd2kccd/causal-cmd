# causal-cmd v0.1.0

## Introduction

Causal-cmd is a Java application that provides a Command-Line Interface (CLI) tool for causal discovery algorithms produced by the [Center for Causal Discovery](http://www.ccd.pitt.edu/).  The application currently includes the following algorithms:

- FGESc - a version of FGES (Fast Greedy Search is an optimization and parallelized version of the Greedy Equivalence Search algorithm (GES)) that works with continuous variables
- FGESd - a version of FGES that works with discrete variables
- GFCIc - a version of GFCI (Greedy Fast Causal Inference) that works with continuous variables
- GFCId - a version of FGES that works with discrete variables

Note that in previous versions released by the Center, FGES was called FGS.

Causal discovery algorithms are a class of search algorithms that explore a space of graphical causal models, i.e., graphical models where directed edges imply causation, for a model (or models) that are a good fit for a dataset. We suggest that newcomers to the field review Causation, Prediction and Search by Spirtes, Glymour and Scheines for a primer on the subject.

Causal discovery algorithms allow a user to uncover the causal relationships between variables in a dataset. These discovered causal relationships may be used further--understanding the underlying the processes of a system (e.g., the metabolic pathways of an organism), hypothesis generation (e.g., variables that best explain an outcome), guide experimentation (e.g., what gene knockout experiments should be performed) or prediction (e.g. parameterization of the causal graph using data and then using it as a classifier).

## Command Line Usage

Java 7 or higher is the only prerequisite to run the software. Note that by default Java will allocate the smaller of 1/4 system memory or 1GB to the Java virtual machine (JVM). If you run out of memory (heap memory space) running your analyses you should increase the memory allocated to the JVM with the following switch '-XmxXXG' where XX is the number of gigabytes of ram you allow the JVM to utilize. For example to allocate 8 gigabytes of ram you would add -Xmx8G immediately after the java command.

In this example, we'll use download the [Retention.txt](http://www.ccd.pitt.edu/wp-content/uploads/files/Retention.txt) file, which is a dataset containing information on college graduation and used in the publication of "What Do College Ranking Data Tell Us About Student Retention?" by Drudzel and Glymour, 1994.

Keep in mind that causal-cmd has different switches for different algorithms. To start, type the following command in your terminal:

```
java -jar causal-cmd-0.1.0-jar-with-dependencies.jar
```

And you'll see the following instructions:

```
usage: java -jar causal-cmd-0.1.0.jar --algorithm <arg> | --simulate-data <arg>  [--version]
    --algorithm <arg>       FGESc, FGESd, GFCIc, GFCId
    --simulate-data <arg>   sem-rand-fwd, bayes-net-rand-fwd
    --version               Show software version.

```

In this example, we'll be running FGESc on this `Retention.txt`.

```
java -jar causal-cmd-0.1.0-jar-with-dependencies.jar --algorithm FGESc --data Retention.txt
```

This command will output the following messages in your terminal:

````
================================================================================
FGES Continuous (Wed, March 22, 2017 10:43:43 AM)
================================================================================
data = Retention.txt
delimiter = tab
verbose = false
thread = 2
penalty discount = 4.000000
max degree = 100
faithfulness assumed = false
ensure variable names are unique = true
ensure variables have non-zero variance = true
out = .
output-prefix = FGESc_Retention.txt_1490193823839
no-validation-output = false

Running version 0.1.0 but unable to contact latest version server.  To disable checking use the skip-latest option.
There are 170 cases and 8 variables.
Wed, March 22, 2017 10:43:45 AM: Start reading in data file.
Wed, March 22, 2017 10:43:45 AM: End reading in data file.
Wed, March 22, 2017 10:43:45 AM: Start running algorithm FGES (Fast Greedy Equivalence Search) using Sem BIC Score.
Wed, March 22, 2017 10:43:45 AM: End running algorithm FGES (Fast Greedy Equivalence Search) using Sem BIC Score.
````

Note that the filename `causal-cmd-x.x.x-jar-with-dependencies.jar` should match the version you have downloaded. 


At the same time, this program will also write the results of the FGES search procedure into a text file named like "FGESc_Retention.txt_1490193823839.txt". Below is the content of this result file:

````
================================================================================
FGES Continuous (Wed, March 22, 2017 10:43:43 AM)
================================================================================

Runtime Parameters:
verbose = false
number of threads = 2

Dataset:
file = Retention.txt
delimiter = tab
cases read in = 170
variables read in = 8

Algorithm Parameters:
penalty discount = 4.000000
max degree = 100
faithfulness assumed = false

Data Validations:
ensure variable names are unique = true
ensure variables have non-zero variance = true


Graph Nodes:
spending_per_stdt,grad_rate,stdt_clss_stndng,rjct_rate,tst_scores,stdt_accept_rate,stdt_tchr_ratio,fac_salary

Graph Edges:
1. grad_rate --- tst_scores
2. spending_per_stdt --- stdt_tchr_ratio
3. stdt_clss_stndng --- rjct_rate
4. tst_scores --- fac_salary
5. tst_scores --- spending_per_stdt
6. tst_scores --- stdt_clss_stndng
````

The end of the file contains the causal graph edgesfrom the search procedure. Here is a key to the edge types:

- A --- B - There is causal relationship between variable A and B but we cannot determine the direction of the relationship
- A --> B - There is a causal relationship from variable A to B

The GFCI algorithm has additional edge types:

- A <-> B - There is an unmeasured confounder of A and B
- A o-> B - Either A is a cause of B or there is an unmeasured confounder of A and B or both
- A o-o B - Either (1) A is a cause of B or B is a cause of A, or (2) there is an unmeasured confounder of A and B, or both 1 and 2 hold.


## Complete Usage Guide

```
usage: java -jar causal-cmd-0.1.0.jar --algorithm <arg> | --simulate-data <arg>  [--version]
    --algorithm <arg>       FGESc, FGESd, GFCIc, GFCId
    --simulate-data <arg>   sem-rand-fwd, bayes-net-rand-fwd
    --version               Show software version.
```

You can use the `--algorithm <arg>` parameter to see specific algorithm usage information, which we'll also list below.

### FGESc

```
usage: java -jar causal-cmd-0.1.0.jar --algorithm FGESc [-d <arg>] [--exclude-variables <arg>] -f <arg> [--faithfulness-assumed] [--help] [--json] [--knowledge <arg>] [--max-degree <arg>] [--no-validation-output] [-o <arg>] [--output-prefix <arg>] [--penalty-discount <arg>] [--skip-latest] [--skip-nonzero-variance] [--skip-unique-var-name] [--tetrad-graph-json] [--thread <arg>] [--verbose]
 -d,--delimiter <arg>           Data delimiter either comma, semicolon, space, colon, or tab. Default: comma for *.csv, else tab.
    --exclude-variables <arg>   A file containing variables to exclude.
 -f,--data <arg>                Data file.
    --faithfulness-assumed      Yes if (one edge) faithfulness should be assumed. Default is false.
    --help                      Show help.
    --json                      Create JSON output.
    --knowledge <arg>           A file containing prior knowledge.
    --max-degree <arg>          The maximum degree of the graph.. Default is 100.
    --no-validation-output      No validation output files created.
 -o,--out <arg>                 Output directory.
    --output-prefix <arg>       Prefix name for output files.
    --penalty-discount <arg>    Penalty discount. Default is 4.0.
    --skip-latest               Skip checking for latest software version
    --skip-nonzero-variance     Skip check for zero variance variables.
    --skip-unique-var-name      Skip check for unique variable names.
    --tetrad-graph-json         Create Tetrad Graph JSON output.
    --thread <arg>              Number of threads.
    --verbose                   Print additional information.
```

### FGESd

```
usage: java -jar causal-cmd-0.1.0.jar --algorithm FGESd [-d <arg>] [--exclude-variables <arg>] -f <arg> [--faithfulness-assumed] [--help] [--json] [--knowledge <arg>] [--max-degree <arg>] [--no-validation-output] [-o <arg>] [--output-prefix <arg>] [--sample-prior <arg>] [--skip-category-limit] [--skip-latest] [--skip-unique-var-name] [--structure-prior <arg>] [--tetrad-graph-json] [--thread <arg>] [--verbose]
 -d,--delimiter <arg>           Data delimiter either comma, semicolon, space, colon, or tab. Default: comma for *.csv, else tab.
    --exclude-variables <arg>   A file containing variables to exclude.
 -f,--data <arg>                Data file.
    --faithfulness-assumed      Yes if (one edge) faithfulness should be assumed. Default is false.
    --help                      Show help.
    --json                      Create JSON output.
    --knowledge <arg>           A file containing prior knowledge.
    --max-degree <arg>          The maximum degree of the graph.. Default is 100.
    --no-validation-output      No validation output files created.
 -o,--out <arg>                 Output directory.
    --output-prefix <arg>       Prefix name for output files.
    --sample-prior <arg>        Sample prior. Default is 1.0.
    --skip-category-limit       Skip 'limit number of categories' check.
    --skip-latest               Skip checking for latest software version
    --skip-unique-var-name      Skip check for unique variable names.
    --structure-prior <arg>     Structure prior coefficient. Default is 1.0.
    --tetrad-graph-json         Create Tetrad Graph JSON output.
    --thread <arg>              Number of threads.
    --verbose                   Print additional information.
```

### GFCIc

```
usage: java -jar causal-cmd-0.1.0.jar --algorithm GFCIc [--alpha <arg>] [-d <arg>] [--exclude-variables <arg>] -f <arg> [--faithfulness-assumed] [--help] [--json] [--knowledge <arg>] [--max-degree <arg>] [--no-validation-output] [-o <arg>] [--output-prefix <arg>] [--penalty-discount <arg>] [--skip-latest] [--skip-nonzero-variance] [--skip-unique-var-name] [--tetrad-graph-json] [--thread <arg>] [--verbose]
    --alpha <arg>               Cutoff for p values (alpha). Default is 0.01.
 -d,--delimiter <arg>           Data delimiter either comma, semicolon, space, colon, or tab. Default: comma for *.csv, else tab.
    --exclude-variables <arg>   A file containing variables to exclude.
 -f,--data <arg>                Data file.
    --faithfulness-assumed      Yes if (one edge) faithfulness should be assumed. Default is false.
    --help                      Show help.
    --json                      Create JSON output.
    --knowledge <arg>           A file containing prior knowledge.
    --max-degree <arg>          The maximum degree of the graph.. Default is 100.
    --no-validation-output      No validation output files created.
 -o,--out <arg>                 Output directory.
    --output-prefix <arg>       Prefix name for output files.
    --penalty-discount <arg>    Penalty discount. Default is 4.0.
    --skip-latest               Skip checking for latest software version
    --skip-nonzero-variance     Skip check for zero variance variables.
    --skip-unique-var-name      Skip check for unique variable names.
    --tetrad-graph-json         Create Tetrad Graph JSON output.
    --thread <arg>              Number of threads.
    --verbose                   Print additional information.
```

### GFCId

```
usage: java -jar causal-cmd-0.1.0.jar --algorithm GFCId [--alpha <arg>] [-d <arg>] [--exclude-variables <arg>] -f <arg> [--faithfulness-assumed] [--help] [--json] [--knowledge <arg>] [--max-degree <arg>] [--no-validation-output] [-o <arg>] [--output-prefix <arg>] [--sample-prior <arg>] [--skip-category-limit] [--skip-latest] [--skip-unique-var-name] [--structure-prior <arg>] [--tetrad-graph-json] [--thread <arg>] [--verbose]
    --alpha <arg>               Cutoff for p values (alpha). Default is 0.01.
 -d,--delimiter <arg>           Data delimiter either comma, semicolon, space, colon, or tab. Default: comma for *.csv, else tab.
    --exclude-variables <arg>   A file containing variables to exclude.
 -f,--data <arg>                Data file.
    --faithfulness-assumed      Yes if (one edge) faithfulness should be assumed. Default is false.
    --help                      Show help.
    --json                      Create JSON output.
    --knowledge <arg>           A file containing prior knowledge.
    --max-degree <arg>          The maximum degree of the graph.. Default is 100.
    --no-validation-output      No validation output files created.
 -o,--out <arg>                 Output directory.
    --output-prefix <arg>       Prefix name for output files.
    --sample-prior <arg>        Sample prior. Default is 1.0.
    --skip-category-limit       Skip 'limit number of categories' check.
    --skip-latest               Skip checking for latest software version
    --skip-unique-var-name      Skip check for unique variable names.
    --structure-prior <arg>     Structure prior coefficient. Default is 1.0.
    --tetrad-graph-json         Create Tetrad Graph JSON output.
    --thread <arg>              Number of threads.
    --verbose                   Print additional information.
```


### Sample Prior Knowledge File

From the above useage guide, we see the option of `--knowledge <arg>`, with which we can specify the prior knowledge file. Below is the content of a sample prior knowledge file:

```
/knowledge

addtemporal
1 spending_per_stdt fac_salary stdt_tchr_ratio 
2 rjct_rate stdt_accept_rate 
3 tst_scores stdt_clss_stndng 
4* grad_rate 

forbiddirect
x3 x4

requiredirect
x1 x2
```

The first line of the prior knowledge file must say `/knowledge`. And a prior knowledge file consists of three sections:

- addtemporal - tiers of variables where the first tier preceeds the last. Adding a asterisk next to the tier id prohibits edges between tier variables
- forbiddirect - forbidden edges indicated by a list of pairs of variables
- requireddirect - required edges indicated by a list of pairs of variables

## API Usage

In addition to using causal-cmd directly in the command line interface, you can also use the Tetred library that is includedin in causal-cmd as an [Java API](http://cmu-phil.github.io/tetrad/tetrad-lib-apidocs/). Here we provide an example of how to run `FGESc` algorithm using this API.

````java
public class FGEScApiExample {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
		// Set the data file and its properites
		Path dataFile = Paths.get("test", "data", "cmu", "Retention.txt");
		Delimiter delimiter = Delimiter.TAB;
		char quoteCharacter = '"';
		String missingValueMarker = "*";
		String commentMarker = "//";

		// Ensure the data file is valid format
		TabularDataValidation dataFileValidation = new ContinuousTabularDataFileValidation(dataFile.toFile(), delimiter);
		dataFileValidation.setQuoteCharacter(quoteCharacter);
		dataFileValidation.setMissingValueMarker(missingValueMarker);
		dataFileValidation.setCommentMarker(commentMarker);
		dataFileValidation.validate();

		// Ensure there is no error
		int errorCount = 0;
		List<ValidationResult> fileValidResults = dataFileValidation.getValidationResults();
		for (ValidationResult validation : fileValidResults) {
		    if (validation.getCode() == ValidationCode.ERROR) {
		        errorCount++;
		    }
		}
		Assert.assertTrue(errorCount == 0);

		// Read in data
		TabularDataReader reader = new ContinuousTabularDataFileReader(dataFile.toFile(), delimiter);
		reader.setQuoteCharacter(quoteCharacter);
		reader.setMissingValueMarker(missingValueMarker);
		reader.setCommentMarker(commentMarker);
		Dataset dataset = reader.readInData();

		// Convert to Tetrad data model
		DataModel dataModel = TetradDataUtils.toDataModel(dataset);

		// Ensure the data read in is valid
		TetradDataValidation dataValidation = new UniqueVariableValidation((DataSet) dataModel);
		boolean isValidData = dataValidation.validate(System.err, true);
		Assert.assertTrue(isValidData);

		// Set algorithm parameters
		Parameters parameters = new Parameters();
		parameters.set(ParamAttrs.PENALTY_DISCOUNT, 2.0);
		parameters.set(ParamAttrs.MAX_DEGREE, -1);
		parameters.set(ParamAttrs.FAITHFULNESS_ASSUMED, false);
		parameters.set(ParamAttrs.VERBOSE, false);

		// Specify which algorithm to use
		Fges fges = new Fges(new SemBicScore());

		// Run the algorithm on this data with specified parameters
		// and return the Graph object
		Graph graph = fges.search(dataModel, parameters);

		System.out.println();
		System.out.println(graph.toString().trim());
		System.out.flush();
    }

}
````


