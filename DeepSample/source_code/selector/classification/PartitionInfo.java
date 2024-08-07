package selector.classification;

import java.util.ArrayList;
import utility.TestCase;

public class PartitionInfo {
	private String name;
	private String partitionID;
	private double expectedOccurrenceProbability;
	private double realOccurrenceProbability;
	private double expectedFailureLikelihood;
	private ArrayList<TestCase> listOfTests;
	private int numberOfTestCases;

	public PartitionInfo(String name, String partitionID) {
		this.name = name;
		this.partitionID = partitionID;
		this.listOfTests = new ArrayList<>();
	}

	public void addTestCase(TestCase t) {
		listOfTests.add(t);
		computeStatistics(); // Automatically update statistics when a test case is added
	}

	/**
	 * Compute expected occurrence probability.
	 */
	public void computeExpectedOccurrenceProbability() {
		expectedOccurrenceProbability = listOfTests.stream()
				.mapToDouble(TestCase::getExpectedOccurrenceProbability)
				.sum();
	}

	/**
	 * Compute expected failure likelihood.
	 */
	public void computeExpectedFailureLikelihood() {
		expectedFailureLikelihood = listOfTests.stream()
				.mapToDouble(TestCase::getExpectedFailureLikelihood)
				.average().orElse(0.0); // Use average to reflect likelihood per test case
	}

	/**
	 * Compute both expected occurrence probability and failure likelihood.
	 */
	public void computeStatistics() {
		computeExpectedOccurrenceProbability();
		computeExpectedFailureLikelihood();
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPartitionID() {
		return partitionID;
	}

	public void setPartitionID(String partitionID) {
		this.partitionID = partitionID;
	}

	public double getExpectedOccurrenceProbability() {
		return expectedOccurrenceProbability;
	}

	public void setExpectedOccurrenceProbability(double expectedOccurrenceProbability) {
		this.expectedOccurrenceProbability = expectedOccurrenceProbability;
	}

	public double getRealOccurrenceProbability() {
		return realOccurrenceProbability;
	}

	public void setRealOccurrenceProbability(double realOccurrenceProbability) {
		this.realOccurrenceProbability = realOccurrenceProbability;
	}

	public double getExpectedFailureLikelihood() {
		return expectedFailureLikelihood;
	}

	public void setExpectedFailureLikelihood(double expectedFailureLikelihood) {
		this.expectedFailureLikelihood = expectedFailureLikelihood;
	}

	public ArrayList<TestCase> getListOfTests() {
		return listOfTests;
	}

	public void setListOfTests(ArrayList<TestCase> listOfTests) {
		this.listOfTests = listOfTests;
		computeStatistics(); // Recompute statistics when list is reset
	}

	public int getNumberOfTestCases() {
		return numberOfTestCases;
	}

	public int computeNumberOfTestCases() {
		return listOfTests.size();
	}

	public void setNumberOfTestCases(int numberOfTestCases) {
		this.numberOfTestCases = numberOfTestCases;
	}
}