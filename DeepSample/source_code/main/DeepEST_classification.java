package main;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;

import utility.TestFrame;
import utility.InitializerTF;
import selector.classification.DeepESTSelector;

public class DeepEST_classification {

	public static void main(String[] args) {
		String help = "Please specify:\n"
				+ "    - dataset path\n"
				+ "    - auxiliary variable: confidence, entropy, similarity, dsa, lsa\n"
				+ "    - threshold\n"
				+ "    - budget\n"
				+ "    - dataset size\n"
				+ "    - results path";

		if (args.length < 6) {
			System.out.println(help);
			return;
		}

		try {
			String datasetPath = args[0];
			String auxiliaryVariable = args[1];
			double threshold = Double.parseDouble(args[2]);
			int budget = Integer.parseInt(args[3]);
			int size = Integer.parseInt(args[4]);
			String resultsPath = args[5];

			System.out.println("Dataset Path: " + datasetPath);
			System.out.println("Auxiliary Variable: " + auxiliaryVariable);
			System.out.println("Threshold: " + threshold);
			System.out.println("Budget: " + budget);
			System.out.println("Dataset Size: " + size);
			System.out.println("Results Path: " + resultsPath);

			InitializerTF csvR = new InitializerTF(datasetPath);

			int key;
			switch (auxiliaryVariable.toLowerCase()) {
				case "confidence":
					key = 0; // Confidence_Score
					threshold = 1 - threshold;
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

			System.out.println("Approach execution on " + datasetPath + " with auxiliary variable " + auxiliaryVariable + " and budget " + budget);

			int rep = 30;

			ArrayList<TestFrame> tf = csvR.readTestFrames(key, size);
			System.out.println("Loaded " + tf.size() + " test frames.");

			DeepESTSelector aws = new DeepESTSelector();
			double[][] weightsMatrix = csvR.weightedMatrixComputation_threshold(tf, key, threshold);

			for (int j = 0; j < 5; j++) {
				int currentBudget = budget * (int) Math.pow(2, j);

				double[] rel;
				double[] rel_arr = new double[rep];
				int[] num_fp = new int[rep];

				for (int i = 0; i < rep; i++) {
					rel = aws.selectAndRunTestCase(currentBudget, tf, weightsMatrix, 0.8);
					rel_arr[i] = 1 - rel[0];
					num_fp[i] = aws.getnumfp();
				}

				try (FileWriter writer = new FileWriter(resultsPath + "_" + currentBudget + ".csv")) {
					writer.write("accuracy,failures\n");

					for (int i = 0; i < rep; i++) {
						writer.write(rel_arr[i] + "," + num_fp[i] + "\n");
						System.out.println("Budget: " + currentBudget + ", Accuracy: " + rel_arr[i] + ", Failures: " + num_fp[i]);
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println(help);
		}
	}
}