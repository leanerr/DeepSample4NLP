package selector.classification;

import java.util.ArrayList;
import java.util.Random;
import utility.TestCase;

public class GBSSelector extends TestCaseSelector {

	// Declare variables to track reliability estimation and test outcomes
	public double REstimate;
	public double trueReliability;
	public int numberOfDetectedFailurePoints;
	public int numberOfExecutedTestCases;
	public long executionTime;

	// Constructor initializes the selector with necessary parameters
	public GBSSelector(ArrayList<TestCase> _potentialTestSuite, ArrayList<ArrayList<TestCase>> _partitions, int _nPartitions, int _initialNumberOfTestCases, int _budget) {
		super(_potentialTestSuite, _partitions, _nPartitions, _initialNumberOfTestCases, _budget);
	}

	// Method to select and run test cases
	public void selectAndRunTestCase() {
		int indexPartition;
		TestCase testCaseToExecute;

		// Initialize arrays to track executed test cases, failure rates, etc.
		double[] executedTestCasesPerPartition = new double[this.numberOfPartitions];
		double[] failureRates = new double[this.numberOfPartitions];
		int[] failedTestCases = new int[this.numberOfPartitions];
		double[] gradient = new double[this.numberOfPartitions];
		double[] domainProbSum = new double[this.numberOfPartitions];

		int totalTests = this.budget;
		int indexCurrentTest = 0;
		int totalFailurePoint = 0;

		// Record the start time of the execution
		long initTime = System.currentTimeMillis();

		// Initialize the counts for each partition
		for (indexPartition = 0; indexPartition < this.numberOfPartitions; indexPartition++) {
			executedTestCasesPerPartition[indexPartition] = 1;
			failedTestCases[indexPartition] = 0;
			domainProbSum[indexPartition] = 0;
		}

		// Calculate the domain probability sum for each partition
		for (indexPartition = 0; indexPartition < this.numberOfPartitions; indexPartition++) {
			for (TestCase testCase : this.partitions.get(indexPartition)) {
				domainProbSum[indexPartition] += testCase.getExpectedOccurrenceProbability();
			}
		}

		// Execute one test case from each partition initially
		for (indexPartition = 0; indexPartition < this.numberOfPartitions; indexPartition++) {
			if (this.partitions.get(indexPartition).isEmpty()) {
				System.err.println("Warning: Partition " + indexPartition + " is empty.");
				continue;
			}

			testCaseToExecute = selectFromPartition(indexPartition);
			boolean testOutcome = testCaseToExecute.runTestCase("FITTIZIO");
			if (!testOutcome) {
				failedTestCases[indexPartition]++;
				totalFailurePoint++;
			}
			indexCurrentTest++;
			executedTestCasesPerPartition[indexPartition]++;
		}

		// Loop to execute remaining test cases based on gradients
		double maximumGradient;
		int maximumGradientPartition = -1;

		while (indexCurrentTest < totalTests) {
			// Update failure rate estimates for each partition
			for (indexPartition = 0; indexPartition < this.numberOfPartitions; indexPartition++) {
				// Avoid division by zero
				if (executedTestCasesPerPartition[indexPartition] == 0) {
					continue;
				}

				failureRates[indexPartition] = (failedTestCases[indexPartition] + 1.0) / (executedTestCasesPerPartition[indexPartition] + 1.0);

				// Calculate the gradient for the partition
				gradient[indexPartition] = (Math.pow(domainProbSum[indexPartition], 2) * failureRates[indexPartition] * (1 - failureRates[indexPartition])) / Math.pow(executedTestCasesPerPartition[indexPartition], 2);
			}

			// Find the partition with the maximum gradient
			maximumGradient = Double.MIN_VALUE;
			maximumGradientPartition = -1;

			for (indexPartition = 0; indexPartition < this.numberOfPartitions; indexPartition++) {
				if (gradient[indexPartition] > maximumGradient) {
					maximumGradient = gradient[indexPartition];
					maximumGradientPartition = indexPartition;
				}
			}

			// Check if a valid partition was found
			if (maximumGradientPartition == -1 || this.partitions.get(maximumGradientPartition).isEmpty()) {
				System.err.println("Error: No valid partition found with maximum gradient or partition is empty.");
				break; // Or handle accordingly
			}

			// Select and run a test case from the partition with the maximum gradient
			testCaseToExecute = selectFromPartition(maximumGradientPartition);
			boolean testOutcome = testCaseToExecute.runTestCase("FITTIZIO");

			if (!testOutcome) {
				failedTestCases[maximumGradientPartition]++;
				totalFailurePoint++;
			}

			executedTestCasesPerPartition[maximumGradientPartition]++;
			indexCurrentTest++;
		}

		// Update the final failure rate estimate for the partition
		if (executedTestCasesPerPartition[maximumGradientPartition] > 0) {
			failureRates[maximumGradientPartition] = (double) failedTestCases[maximumGradientPartition] / executedTestCasesPerPartition[maximumGradientPartition];
		}

		// Calculate the overall reliability estimate
		double sumFailureRate = 0.0;
		for (indexPartition = 0; indexPartition < this.numberOfPartitions; indexPartition++) {
			sumFailureRate += domainProbSum[indexPartition] * failureRates[indexPartition];
		}
		this.REstimate = 1 - sumFailureRate;
		this.trueReliability = computeTrueReliability();

		// Record the end time and calculate execution time
		long endTime = System.currentTimeMillis();
		this.executionTime = endTime - initTime;
		this.numberOfExecutedTestCases = indexCurrentTest;
		this.numberOfDetectedFailurePoints = totalFailurePoint;
	}

	// Method to compute true reliability
	private double computeTrueReliability() {
		boolean outcome;
		double unrel = 0.0;

		for (TestCase testCase : this.potentialTestSuite) {
			outcome = testCase.getOutcome();
			if (!outcome) {
				unrel += testCase.getExpectedOccurrenceProbability();
			}
		}

		return (1 - unrel);
	}

	// Method to select a test case from a given partition
	private TestCase selectFromPartition(int partitionIndex) {
		// Check for non-empty partition
		if (this.partitions.get(partitionIndex).isEmpty()) {
			throw new IllegalStateException("Partition " + partitionIndex + " is empty. Cannot select a test case.");
		}

		// Randomly select a test case from the specified partition
		int randIndex = new Random().nextInt(this.partitions.get(partitionIndex).size());
		return this.partitions.get(partitionIndex).get(randIndex);
	}
}