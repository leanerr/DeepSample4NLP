package selector.classification;

import java.util.ArrayList;
import utility.TestCase;

public abstract class TestCaseSelector {

	public ArrayList<TestCase> potentialTestSuite;
	public ArrayList<ArrayList<TestCase>> partitions;
	public int numberOfPartitions;
	public int initialNumberOfTestCases;
	public int budget;
	public int numberOfFaults;

	public TestCaseSelector(ArrayList<TestCase> _potentialTestSuite, ArrayList<ArrayList<TestCase>> _partitions, int _nPartitions, int _initialNumberOfTestCases, int _budget, int _numberOfFaults) {
		potentialTestSuite = _potentialTestSuite;
		partitions = _partitions;
		numberOfPartitions = _nPartitions;
		initialNumberOfTestCases = _initialNumberOfTestCases;
		budget = _budget;
		numberOfFaults = _numberOfFaults;
		validateInputs();
	}

	public TestCaseSelector(ArrayList<TestCase> _potentialTestSuite, ArrayList<ArrayList<TestCase>> _partitions, int _nPartitions, int _initialNumberOfTestCases, int _budget) {
		potentialTestSuite = _potentialTestSuite;
		partitions = _partitions;
		numberOfPartitions = _nPartitions;
		initialNumberOfTestCases = _initialNumberOfTestCases;
		budget = _budget;
		validateInputs();
	}

	public TestCaseSelector(ArrayList<TestCase> _potentialTestSuite, int _initialNumberOfTestCases, int _budget) {
		potentialTestSuite = _potentialTestSuite;
		initialNumberOfTestCases = _initialNumberOfTestCases;
		budget = _budget;
		validateInputs();
	}

	private void validateInputs() {
		if (budget > potentialTestSuite.size()) {
			System.out.println("ERROR: BUDGET CANNOT BE GREATER THAN THE NUMBER OF INPUTS");
			System.exit(0); // Replace with exceptions if preferred
		}
		if (initialNumberOfTestCases > budget) {
			System.out.println("ERROR: INITIAL NUMBER OF TEST CASES CANNOT BE GREATER THAN THE BUDGET");
			System.exit(0); // Replace with exceptions if preferred
		}
	}

	abstract void selectAndRunTestCase();
}