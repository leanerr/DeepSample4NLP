package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import selector.classification.GBSSelector;
import selector.classification.PartitionInfo;
import selector.classification.RHCSSelector;
import selector.classification.StratifiedRandomSelectorWOR;
import utility.TestCase;
import utility.TestSuiteFileLoader;

public class Classification_main_jar {

	public static void main(String[] args) {
		String model = args[0];
		String path = "./dataset/" + model + ".csv";
		int key = Integer.parseInt(args[1]);
		int size = Integer.parseInt(args[2]);

		// Determine the index for each auxiliary variable based on the CSV header
		Map<String, Integer> columnIndices = getColumnIndices(path);

		// Mapping key to auxiliary variable name, updated to zero-based index
		String aux = getAuxiliaryVariableName(key);

		// Validate if the column exists in the dataset
		if (!columnIndices.containsKey(aux)) {
			throw new IllegalArgumentException("Auxiliary variable " + aux + " not found in dataset.");
		}

		// Get the correct column index for the selected auxiliary variable
		int auxIndex = columnIndices.get(aux);

		/**** INITIALIZATION ****/

		int NPartitionsSimulation = 10;
		TestSuiteFileLoader tsfl = new TestSuiteFileLoader();
		tsfl.loadTestSuiteSimulation_class(path, auxIndex, size);

		ArrayList<PartitionInfo> partitionsStructure = null;
		ArrayList<TestCase> completeTestSuite = new ArrayList<>();
		ArrayList<TestCase> completeTestSuite_copy;

		try {
			partitionsStructure = tsfl.getPartitionsSimulation_kNN(NPartitionsSimulation, auxIndex, path);
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(1); // Exit on error
		}

		// Check if partitionsStructure is null
		if (partitionsStructure == null) {
			System.err.println("Error: partitionsStructure is null");
			System.exit(1);
		}

		// Initialize partitions and complete test suite
		ArrayList<ArrayList<TestCase>> partitions = new ArrayList<>();
		for (PartitionInfo partition : partitionsStructure) {
			partitions.add(new ArrayList<>(partition.getListOfTests()));
		}

		int budget = 25;
		int rep = 30;

		for (int i = 0; i < 5; i++) {
			budget *= 2;
			System.out.println("SSRS: budget " + budget);

			try (FileWriter writer = new FileWriter("./Results/Classification/SSRS/" + model + "_" + aux + "_" + budget + ".txt")) {
				writer.write("accuracy,failures\n");

				for (int j = 0; j < rep; j++) {
					completeTestSuite.clear();
					for (ArrayList<TestCase> partition : partitions) {
						completeTestSuite.addAll(partition);
					}

					// Shuffle the test suite to ensure different starting conditions
					Collections.shuffle(completeTestSuite);

					StratifiedRandomSelectorWOR srs = new StratifiedRandomSelectorWOR(completeTestSuite, partitions, NPartitionsSimulation, 0, budget, 0);
					srs.selectAndRunTestCase();
					System.out.println(srs.REstimate + "    " + srs.numberOfDetectedFailurePoints);
					writer.write(srs.REstimate + "," + srs.numberOfDetectedFailurePoints + "\n");
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

			System.out.println("GBS: budget " + budget);

			try (FileWriter writer = new FileWriter("./Results/Classification/GBS/" + model + "_" + aux + "_" + budget + ".txt")) {
				writer.write("accuracy,failures\n");

				for (int j = 0; j < rep; j++) {
					completeTestSuite.clear();
					for (ArrayList<TestCase> partition : partitions) {
						completeTestSuite.addAll(partition);
					}

					// Shuffle the test suite to ensure different starting conditions
					Collections.shuffle(completeTestSuite);

					GBSSelector ats = new GBSSelector(completeTestSuite, partitions, NPartitionsSimulation, 0, budget);
					ats.selectAndRunTestCase();
					System.out.println(ats.REstimate + "    " + ats.numberOfDetectedFailurePoints);
					writer.write(ats.REstimate + "," + ats.numberOfDetectedFailurePoints + "\n");
				}

			} catch (IOException e) {
				e.printStackTrace();
			}

			System.out.println("RHC-S: budget " + budget);

			try (FileWriter writer = new FileWriter("./Results/Classification/RHC-S/" + model + "_" + aux + "_" + budget + ".txt")) {
				writer.write("accuracy,failures\n");

				for (int j = 0; j < rep; j++) {
					completeTestSuite_copy = new ArrayList<>(completeTestSuite);  // Create a fresh copy
					RHCSSelector rdts = new RHCSSelector(completeTestSuite_copy, budget, budget);
					rdts.selectAndRunTestCase(95);
					System.out.println(rdts.estimate + "    " + rdts.numberOfFailedTestCases);
					writer.write(rdts.estimate + "," + rdts.numberOfFailedTestCases + "\n");
				}

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * Reads the CSV file header and returns a map of column names to their indices.
	 *
	 * @param csvFilePath The path to the CSV file.
	 * @return A map of column names to indices.
	 */
	private static Map<String, Integer> getColumnIndices(String csvFilePath) {
		Map<String, Integer> columnIndices = new HashMap<>();
		try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {
			String headerLine = br.readLine();
			if (headerLine != null) {
				String[] headers = headerLine.split(",");
				for (int i = 0; i < headers.length; i++) {
					columnIndices.put(headers[i].trim(), i);
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return columnIndices;
	}

	/**
	 * Maps the key to the corresponding auxiliary variable name.
	 *
	 * @param key The key indicating the auxiliary variable.
	 * @return The name of the auxiliary variable.
	 */
	private static String getAuxiliaryVariableName(int key) {
		switch (key) {
			case 0:
				return "Confidence_Score";
			case 1:
				return "Prediction_Entropy";
			case 2:
				return "Similarity_Score";
			case 3:
				return "DSA";
			case 4:
				return "LSA";
			default:
				throw new IllegalArgumentException("Invalid key. Must be 0, 1, 2, 3, or 4.");
		}
	}
}


/*
java -cp "DeepSample/DeepSample_part_1_class.jar:libs/commons-lang3-3.12.0.jar:libs/commons-math3-3.6.1.jar:libs/weka.jar" main.Classification_main_jar imdb300AuxDS 0 2999
*/
/*
java -cp "DeepSample/DeepSample_part_1_class.jar:libs/commons-lang3-3.12.0.jar:libs/commons-math3-3.6.1.jar:libs/weka.jar" main.Classification_main_jar imdb300AuxDS 0 2999
 */


// Commenting out the TwoUPSSelector related code
            /*
            System.out.println("2-UPS: budget " + budget);

            for (int j = 0; j < rep; j++) {
                TwoUPSSelector opt = new TwoUPSSelector(completeTestSuite, partitions, NPartitionsSimulation, 0, budget);
                opt.selectAndRunTestCase();
            }

            try {
                FileWriter writer = new FileWriter("./Results/Classification/2-UPS/" + model + "_" + aux + "_" + budget + ".txt");

                writer.write("accuracy,failures\n");

                for (int j = 0; j < rep; j++) {
                    TwoUPSSelector opt = new TwoUPSSelector(completeTestSuite, partitions, NPartitionsSimulation, 0, budget);
                    opt.selectAndRunTestCase();
                    System.out.println("" + opt.REstimate + "    " + opt.numberOfDetectedFailurePoints);
                    writer.write("" + opt.REstimate + "," + opt.numberOfDetectedFailurePoints + "\n");
                }

                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            */