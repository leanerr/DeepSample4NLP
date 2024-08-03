package main;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import utility.TestFrame;
import utility.InitializerTF;
import selector.classification.SUPSSelector;

public class SUPS_classification {

	public static void main(String[] args) {

		// Check if the correct number of arguments is provided
		if (args.length < 5) {
			System.out.println("Usage: SUPS_classification <model> <auxiliary_variable> <budget> <key> <size>");
			return;
		}

		String model = args[0];
		String auxiliaryVariable = args[1];
		int budget = Integer.parseInt(args[2]);
		int key = Integer.parseInt(args[3]); // Using provided key directly
		int size = Integer.parseInt(args[4]);

		String path = "dataset/" + model + ".csv";

		// Verify correct key setting based on auxiliary variable
		switch (auxiliaryVariable.toLowerCase()) {
			case "confidence":
				key = 0; // Confidence_Score
				break;
			case "entropy":
				key = 1; // Prediction_Entropy
				break;
			case "similarity":
				key = 2; // Similarity_Score
				break;
			case "dsa":
				key = 3; // DSA
				break;
			case "lsa":
				key = 4; // LSA
				break;
			default:
				throw new IllegalArgumentException("Invalid auxiliary variable. Must be confidence, entropy, similarity, dsa, or lsa.");
		}

		InitializerTF csvR = new InitializerTF(path);

		int rep = 30;

		ArrayList<TestFrame> tf = csvR.readTestFrames(key, size);

		/******** SUPS *********/
		SUPSSelector wo = new SUPSSelector();
		double[] ret;
		double[] rel = new double[rep];
		double[] fp = new double[rep];

		for (int i = 0; i < rep; i++) {
			ret = wo.weigthedOperationalTesting(tf, budget);
			rel[i] = ret[0];
			fp[i] = ret[1];
		}

		// Output directory
		String outputDirPath = "Results/Classification/SUPS/";
		String outputFilePath = outputDirPath + model + "_" + auxiliaryVariable + "_" + budget + ".txt";

		// Ensure the output directory exists
		File outputDir = new File(outputDirPath);
		if (!outputDir.exists()) {
			boolean created = outputDir.mkdirs();
			if (!created) {
				System.err.println("Failed to create directory: " + outputDir.getAbsolutePath());
				return;
			}
		}

		// Write results to file
		try (FileWriter writer = new FileWriter(outputFilePath)) {
			writer.write("accuracy,failures\n");

			for (int i = 0; i < rep; i++) {
				writer.write(rel[i] + "," + fp[i] + "\n");
				System.out.println("Written: " + rel[i] + "," + fp[i]); // Debugging line
			}

			System.out.println("Results successfully written to: " + outputFilePath);
		} catch (IOException e) {
			System.err.println("An error occurred while writing to the file: " + outputFilePath);
			e.printStackTrace();
		}

	}
}