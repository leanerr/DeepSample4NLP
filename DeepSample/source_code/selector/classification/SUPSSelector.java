package selector.classification;

import java.util.ArrayList;
import utility.TestFrame;

public class SUPSSelector {

	public double[] weigthedOperationalTesting(ArrayList<TestFrame> tfa_in, int num) {
		// Create a local copy of the test frames to work with
		ArrayList<TestFrame> tfa = new ArrayList<>(tfa_in);

		if (tfa.isEmpty()) {
			System.out.println("Warning: No test frames provided.");
			return new double[] {0.0, 0.0};  // Return default values if input is empty
		}

		// Arrays to store cumulative and normalized probabilities
		double[] takenProb = new double[tfa.size()];
		double[] newtakenProb = new double[tfa.size()];

		int failedTC = 0; // Counter for failed test cases

		// Initialize the cumulative probability with the first failure probability
		takenProb[0] = tfa.get(0).getFailureProb();

		// Compute cumulative probabilities
		for (int i = 1; i < tfa.size(); i++) {
			takenProb[i] = tfa.get(i).getFailureProb() + takenProb[i - 1];
		}

		if (takenProb[takenProb.length - 1] == 0) {
			System.out.println("Warning: Total cumulative probability is zero. Check failure probabilities.");
			return new double[] {0.0, 0.0};  // Return default values if cumulative probability is zero
		}

		// Normalize the failure probabilities
		double norm = 0;
		for (int i = 0; i < tfa.size(); i++) {
			newtakenProb[i] = tfa.get(i).getFailureProb() / takenProb[takenProb.length - 1];
			norm += newtakenProb[i];
		}

		// Variables for random sampling and failure probability calculation
		double rand;
		double failProb = 0.0;
		int k;
		int outcome;

		// Perform random sampling to simulate the testing process
		for (int i = 0; i < num; i++) {
			rand = Math.random() * takenProb[takenProb.length - 1];
			k = 0;

			// Find the index corresponding to the random value
			while (k < takenProb.length - 1 && (rand >= takenProb[k])) {
				k++;
			}

			// Execute the test case and check if it fails
			outcome = tfa.get(k).extractAndExecuteTestCase();
			if (outcome == 1) { // If the test case fails
				failedTC++;
				failProb += 1 / ((double) num * newtakenProb[k]);
			}
		}

		// Calculate the reliability based on failure probability
		failProb = failProb / (double) tfa_in.size();
		double rel = 1 - failProb;

		// Return an array with reliability and the number of failed test cases
		double[] ret = new double[2];
		ret[0] = rel;
		ret[1] = failedTC;

		System.out.println("Reliability: " + rel + ", Failed Test Cases: " + failedTC);

		return ret;
	}
}