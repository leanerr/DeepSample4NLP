package selector.classification;

import java.util.ArrayList;
import java.util.Random;
import utility.TestCase;

/**
 * Implements Two-Stage Universal Partitioning Sampling (Two-UPS) Selector.
 * This class is adapted to handle new datasets with auxiliary variables.
 */
public class TwoUPSSelector extends TestCaseSelector {

	public double REstimate;
	public double trueReliability;
	public int numberOfDetectedFailurePoints;
	public int numberOfExecutedTestCases;
	public long executionTime;

	/**
	 * Constructor to initialize the Two-UPS Selector with partitions.
	 */
	public TwoUPSSelector(ArrayList<TestCase> _potentialTestSuite, ArrayList<ArrayList<TestCase>> _partitions,
						  int _nPartitions, int _initialNumberOfTestCases, int _budget) {
		super(_potentialTestSuite, _partitions, _nPartitions, _initialNumberOfTestCases, _budget);
	}

	/**
	 * Selects and executes test cases based on the Two-UPS algorithm.
	 */
	public void selectAndRunTestCase() {

		System.out.println("\n\nStarting the Two-UPS Algorithm ... \n");

		// Create a copy of the partitions for sampling
		ArrayList<ArrayList<TestCase>> samplePartitions = new ArrayList<>();
		for (ArrayList<TestCase> partition : this.partitions) {
			ArrayList<TestCase> tempTC = new ArrayList<>(partition);
			samplePartitions.add(tempTC);
		}

		TestCase testCaseToExecute;
		boolean testOutcome;
		int totalFailurePoint = 0;
		int indexCurrentTest = 0;
		int totalTests = this.budget;
		double[] executedTestCasesPerPartition = new double[this.numberOfPartitions]; // ni
		int[] failedTestCases = new int[this.numberOfPartitions]; // z
		double[] domainProbSum = new double[this.numberOfPartitions]; // DOMAIN SIZE: p

		// Initialize arrays
		for (int indexPartition = 0; indexPartition < this.numberOfPartitions; indexPartition++) {
			domainProbSum[indexPartition] = 0;
			executedTestCasesPerPartition[indexPartition] = 0;
			failedTestCases[indexPartition] = 0;
		}

		// Calculate the probability sum for each partition based on auxiliary variables
		double domainProbSumTotal = 0;
		int auxIndex = 0; // Ensure this index is valid for the dataset

		for (int indexPartition = 0; indexPartition < this.numberOfPartitions; indexPartition++) {
			for (TestCase testCase : samplePartitions.get(indexPartition)) {
				// Use auxiliary variables like confidence or entropy for weight
				domainProbSum[indexPartition] += testCase.getAuxiliaryVariable(auxIndex); // Pass the correct index here
			}
			domainProbSumTotal += domainProbSum[indexPartition];
		}

		// Check for zero total domain probability sum
		if (domainProbSumTotal == 0) {
			throw new IllegalStateException("Total domain probability sum is zero, indicating an issue with auxiliary variable values.");
		}

		// Normalize domain probabilities
		for (int i = 0; i < domainProbSum.length; i++) {
			domainProbSum[i] /= domainProbSumTotal;
		}

		// Initialize cumulative probability vector for partitions
		double[] cumulativePVector = new double[this.numberOfPartitions];
		cumulativePVector[0] = domainProbSum[0];
		for (int indexPartition = 1; indexPartition < this.numberOfPartitions; indexPartition++) {
			cumulativePVector[indexPartition] = cumulativePVector[indexPartition - 1] + domainProbSum[indexPartition];
		}

		long initTime = System.currentTimeMillis();

		// Select and execute test cases
		while (indexCurrentTest < totalTests) {
			double rand = Math.random();
			int selectedPartition = -1;
			for (int index = 0; index < this.numberOfPartitions; index++) {
				if (rand <= cumulativePVector[index]) {
					selectedPartition = index;
					break;
				}
			}

			// Validate selected partition
			if (selectedPartition == -1) {
				System.err.println("Failed to select a valid partition. Check cumulative probability logic.");
				continue; // Continue to the next iteration instead of throwing an exception
			}

			// Check if the selected partition is non-empty
			if (samplePartitions.get(selectedPartition).isEmpty()) {
				// Log a message and continue to the next iteration
				System.out.println("Partition " + selectedPartition + " is empty, selecting another partition.");
				continue;
			}

			// Select a random test case from the selected partition
			int randIndex = new Random().nextInt(samplePartitions.get(selectedPartition).size());
			testCaseToExecute = samplePartitions.get(selectedPartition).get(randIndex);

			// Execute the test case and check the outcome
			testOutcome = testCaseToExecute.runTestCase("TESTING");
			if (!testOutcome) {
				failedTestCases[selectedPartition]++;
				totalFailurePoint++;
			}

			// Remove the executed test case from the sample partition
			samplePartitions.get(selectedPartition).remove(randIndex);
			executedTestCasesPerPartition[selectedPartition]++;
			indexCurrentTest++;
		}

		// Calculate the reliability estimate
		double sumFailureRate = 0.0;
		int numValidPartitions = 0;

		for (int indexPartition = 0; indexPartition < this.numberOfPartitions; indexPartition++) {
			if (executedTestCasesPerPartition[indexPartition] != 0) {
				sumFailureRate += ((double) failedTestCases[indexPartition] / executedTestCasesPerPartition[indexPartition])
						* (this.partitions.get(indexPartition).size()) / domainProbSum[indexPartition];
				numValidPartitions++;
			}
		}

		// Finalize the failure rate and reliability estimates
		if (numValidPartitions > 0) {
			sumFailureRate /= numValidPartitions;
		} else {
			System.err.println("Warning: No valid partitions found for reliability estimate calculation.");
		}

		sumFailureRate /= this.potentialTestSuite.size();

		this.REstimate = 1 - sumFailureRate;
		this.trueReliability = computeTrueReliability();
		long endTime = System.currentTimeMillis();
		this.executionTime = endTime - initTime;
		this.numberOfExecutedTestCases = indexCurrentTest;
		this.numberOfDetectedFailurePoints = totalFailurePoint;
	}

	/**
	 * Computes the true reliability of the test suite based on expected occurrence probabilities.
	 */
	private double computeTrueReliability() {
		double unrel = 0.0;
		for (TestCase testCase : this.potentialTestSuite) {
			if (!testCase.getOutcome()) {
				unrel += testCase.getExpectedOccurrenceProbability();
			}
		}
		return (1 - unrel);
	}
}