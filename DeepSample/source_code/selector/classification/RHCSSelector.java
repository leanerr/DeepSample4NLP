package selector.classification;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

import org.apache.commons.math3.distribution.TDistribution;
import org.apache.commons.lang3.mutable.MutableDouble;

import utility.TestCase;

public class RHCSSelector extends TestCaseSelector {

	// Variables for tracking reliability estimates and outcomes
	public double estimate;
	public double varianceEstimate;
	public long executionTime;
	public int failurePoints;
	public int numberOfFailedTestCases;
	public ArrayList<TestCase> failedTestCasesList;
	public ArrayList<TestCase> copy;

	// Constructor to initialize RHCSSelector
	public RHCSSelector(ArrayList<TestCase> potentialTestSuite, int initialNumberOfTestCases, int budget) {
		super(potentialTestSuite, initialNumberOfTestCases, budget);
		this.failurePoints = 0;
		this.numberOfFailedTestCases = 0;
		this.copy = new ArrayList<>(potentialTestSuite);
		this.failedTestCasesList = new ArrayList<>();
	}

	// Overloaded method to execute the test case selection and evaluation
	public void selectAndRunTestCase() {
		selectAndRunTestCase(95.0); // Default desired percentage accuracy
	}

	// Main selection and execution logic
	public void selectAndRunTestCase(double desiredPercentageAccuracy) {
		System.out.println("\n\nStarting the RHC-S Algorithm ... \n");

		int totalTests = this.budget;
		ArrayList<TestCase> FailedTestCases = new ArrayList<>();

		int indexCurrentTest = 0;
		this.failurePoints = 0;

		MutableDouble sampleOutput = new MutableDouble(0);
		MutableDouble estimatedOutput = new MutableDouble(0);
		MutableDouble estimatedVariance = new MutableDouble(0);
		MutableDouble estimatedCI_HalfWidth = new MutableDouble(0);

		long initTime = System.currentTimeMillis();

		while (indexCurrentTest < totalTests) {
			// Reset values for each iteration
			sampleOutput.setValue(0);
			estimatedOutput.setValue(0);
			estimatedVariance.setValue(0);
			estimatedCI_HalfWidth.setValue(0);

			try {
				// Select and execute RHC
				selectAndExecute_RHC(this.potentialTestSuite, this.initialNumberOfTestCases, sampleOutput, estimatedOutput, estimatedVariance, estimatedCI_HalfWidth, FailedTestCases);
			} catch (Exception e) {
				System.out.println("Exception occurred during RHC execution: " + e.getMessage());
				e.printStackTrace();
				break;
			}

			indexCurrentTest += this.initialNumberOfTestCases;
		}

		long endTime = System.currentTimeMillis();

		// Constrain the estimate within valid bounds
		if (estimatedOutput.doubleValue() > 1) {
			System.out.println("The failure probability estimate provided a value greater than 1. It is forced to 1 for consistency.");
			estimatedOutput.setValue(1);
		}

		// Output for a single iteration
		this.estimate = (1 - estimatedOutput.doubleValue());
		this.varianceEstimate = estimatedVariance.doubleValue();
		this.executionTime = endTime - initTime;
		this.failedTestCasesList = FailedTestCases;
		this.numberOfFailedTestCases = FailedTestCases.size();
	}

	// Compute variance estimator
	private double computeVarianceEstimator(MutableDouble estimatedOutput, int sampleSize, int populationSize, double[] pValue, double[] q, double[] unreliability, double sumSquaredGroupsSize) {
		double firstTerm = (sumSquaredGroupsSize - populationSize) / (Math.pow(populationSize, 2) - sumSquaredGroupsSize);
		double sumSecondTerm = 0.0;
		for (int r = 0; r < sampleSize; r++) {
			if (pValue[r] != 0) { // Avoid division by zero
				sumSecondTerm += q[r] * Math.pow((unreliability[r] / pValue[r] - estimatedOutput.doubleValue()), 2);
			}
		}
		return (firstTerm * sumSecondTerm);
	}

	// Select and execute test cases using RHC-S
	private void selectAndExecute_RHC(ArrayList<TestCase> population, int sampleSize, MutableDouble sampleOutput, MutableDouble estimatedOutput, MutableDouble estimatedVariance, MutableDouble estimatedCI_HalfWidth, ArrayList<TestCase> FailedTestCases) throws Exception {

		System.out.println("Size of the population: " + population.size());
		System.out.println("Size of the sample: " + sampleSize);
		int originalSize = population.size();

		// Probability values for each test case
		double[] pValue = new double[sampleSize];
		double[] q = new double[sampleSize];
		double[] unreliability = new double[sampleSize];
		double[] p = new double[population.size()];
		double[] size = new double[population.size()];

		double sumOverSize = 0.0;

		// Calculate probabilities and sizes
		for (int t = 0; t < population.size(); t++) {
			size[t] = population.get(t).getExpectedOccurrenceProbability() * population.get(t).getExpectedFailureLikelihood();
			sumOverSize += size[t];
		}

		// Check for zero sumOverSize and handle appropriately
		if (sumOverSize == 0) {
			throw new Exception("Sum of expected probabilities is zero, cannot proceed with test case selection.");
		}

		for (int t = 0; t < population.size(); t++) {
			p[t] = size[t] / sumOverSize;
		}

		// Step 2: Create homogeneous groups
		int numberOfGroups = sampleSize;
		int groupSize = (int) Math.floor((double) p.length / sampleSize);

		double[][] GMatrix;
		int[][] GMatrixIndex;
		if (groupSize * numberOfGroups == p.length) {
			GMatrix = new double[numberOfGroups][groupSize];
			GMatrixIndex = new int[numberOfGroups][groupSize];
		} else {
			GMatrix = new double[numberOfGroups][groupSize + 1];
			GMatrixIndex = new int[numberOfGroups][groupSize + 1];
		}

		ArrayList<Integer> listOfIndexes = new ArrayList<>();
		for (int i = 0; i < p.length; i++) {
			listOfIndexes.add(i);
		}

		// Assign test cases to groups
		int indexForGroup;
		Random random = new Random(); // Create one Random instance
		for (int indexGroup1 = 0; indexGroup1 < GMatrix.length; indexGroup1++) {
			for (int indexGroup2 = 0; indexGroup2 < GMatrix[0].length - 1; indexGroup2++) {
				int randomInt = random.nextInt(listOfIndexes.size());
				indexForGroup = listOfIndexes.remove(randomInt);
				GMatrix[indexGroup1][indexGroup2] = p[indexForGroup];
				GMatrixIndex[indexGroup1][indexGroup2] = indexForGroup;
			}
		}

		for (int i = 0; i < GMatrix.length; i++) {
			if (!listOfIndexes.isEmpty()) {
				indexForGroup = listOfIndexes.remove(0);
				GMatrix[i][GMatrix[0].length - 1] = p[indexForGroup];
				GMatrixIndex[i][GMatrixIndex[0].length - 1] = indexForGroup;
			} else {
				GMatrix[i][GMatrix[0].length - 1] = 0;
				GMatrixIndex[i][GMatrixIndex[0].length - 1] = -1;
			}
		}

		// Step 3: Select a unit from the group with a PPS method
		boolean[] outputValues = new boolean[numberOfGroups];
		double[] probSumOfGroup = new double[numberOfGroups];
		double[][] cumProbOfGroup = new double[GMatrix.length][GMatrix[0].length];

		for (int i = 0; i < GMatrix.length; i++) {
			for (int j = 0; j < GMatrix[0].length; j++) {
				probSumOfGroup[i] += GMatrix[i][j];
				if (j == 0) {
					cumProbOfGroup[i][j] = GMatrix[i][j];
				} else {
					cumProbOfGroup[i][j] = cumProbOfGroup[i][j - 1] + GMatrix[i][j];
				}
			}
		}

		double randomDouble;
		int indexOfUnitToRead = -1;
		ArrayList<Integer> indexesToRemove = new ArrayList<>();

		for (int i = 0; i < numberOfGroups; i++) {
			if (probSumOfGroup[i] == 0) {
				System.out.println("Warning: Probability sum of group " + i + " is zero, skipping this group.");
				continue;  // Skip groups with zero probability sum
			}
			randomDouble = random.nextDouble() * probSumOfGroup[i];
			for (int j = 0; j < GMatrix[0].length; j++) {
				if (randomDouble <= cumProbOfGroup[i][j]) {
					indexOfUnitToRead = j;
					break;
				}
			}
			if (indexOfUnitToRead == -1) {
				throw new Exception("No unit selected from group " + i);
			}

			int indexInP = GMatrixIndex[i][indexOfUnitToRead];
			if (indexInP == -1) {
				throw new Exception("Invalid index in group " + i + " at position " + indexOfUnitToRead);
			}

			TestCase testCaseToExecute = population.get(indexInP);

			// Execute test case and collect output
			outputValues[i] = testCaseToExecute.runTestCase("running...");
			pValue[i] = p[indexInP];

			if (!outputValues[i]) {
				FailedTestCases.add(testCaseToExecute);
				unreliability[i] = testCaseToExecute.getExpectedOccurrenceProbability();
				this.failurePoints++;
			} else {
				unreliability[i] = 0;
			}

			// Remove executed test from the population (without replacement)
			indexesToRemove.add(indexInP);
		}

		// Remove executed test cases from the population
		Collections.sort(indexesToRemove, Collections.reverseOrder());
		for (int index : indexesToRemove) {
			population.remove(index);
		}

		// Estimates calculation
		double sumQ = 0;
		for (int r = 0; r < numberOfGroups; r++) {
			q[r] = probSumOfGroup[r];
			sumQ += q[r];
		}

		double Y = 0;
		for (int r = 0; r < pValue.length; r++) {
			if (!outputValues[r]) {
				Y += unreliability[r] / (pValue[r] / q[r]);
				if (Double.isNaN(Y)) {
					throw new Exception("Fatal Error in the computation of reliability estimator");
				}
				sampleOutput.setValue(sampleOutput.doubleValue() + unreliability[r]);
			}
		}

		// Variance estimator
		double sumSquaredGroupsSize = 0.0;
		for (int r = 0; r < sampleSize; r++) {
			int lastIndex = GMatrixIndex[r][GMatrix[0].length - 1];
			int groupSizeActual = (lastIndex == -1) ? GMatrix[0].length - 1 : GMatrix[0].length;
			sumSquaredGroupsSize += Math.pow(groupSizeActual, 2);
		}
		estimatedOutput.setValue(Y);

		estimatedVariance.setValue(computeVarianceEstimator(estimatedOutput, numberOfGroups, originalSize, pValue, q, unreliability, sumSquaredGroupsSize));
		TDistribution t = new TDistribution(numberOfGroups - 1);
		estimatedCI_HalfWidth.setValue(t.inverseCumulativeProbability(0.975) * Math.sqrt(estimatedVariance.doubleValue()));
	}
}