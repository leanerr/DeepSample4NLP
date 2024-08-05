package selector.classification;

import java.util.ArrayList;
import utility.TestCase;

/**
 * Abstract class for selecting test cases from a potential suite.
 */
public abstract class TestCaseSelector {

	// Fields for the test case selection process
	public ArrayList<TestCase> potentialTestSuite;
	public ArrayList<ArrayList<TestCase>> partitions;
	public int numberOfPartitions;
	public int initialNumberOfTestCases;
	public int budget;
	public int numberOfFaults;

	/**
	 * Constructor with all parameters.
	 *
	 * @param _potentialTestSuite  The list of potential test cases.
	 * @param _partitions          The partitions of test cases.
	 * @param _nPartitions         The number of partitions.
	 * @param _initialNumberOfTestCases  The initial number of test cases to run.
	 * @param _budget              The total budget for test execution.
	 * @param _numberOfFaults      The number of faults expected.
	 */
	public TestCaseSelector(ArrayList<TestCase> _potentialTestSuite, ArrayList<ArrayList<TestCase>> _partitions,
							int _nPartitions, int _initialNumberOfTestCases, int _budget, int _numberOfFaults) {
		this.potentialTestSuite = _potentialTestSuite;
		this.partitions = _partitions;
		this.numberOfPartitions = _nPartitions;
		this.initialNumberOfTestCases = _initialNumberOfTestCases;
		this.budget = _budget;
		this.numberOfFaults = _numberOfFaults;
		validateInputs();
	}

	/**
	 * Constructor without number of faults.
	 *
	 * @param _potentialTestSuite  The list of potential test cases.
	 * @param _partitions          The partitions of test cases.
	 * @param _nPartitions         The number of partitions.
	 * @param _initialNumberOfTestCases  The initial number of test cases to run.
	 * @param _budget              The total budget for test execution.
	 */
	public TestCaseSelector(ArrayList<TestCase> _potentialTestSuite, ArrayList<ArrayList<TestCase>> _partitions,
							int _nPartitions, int _initialNumberOfTestCases, int _budget) {
		this(_potentialTestSuite, _partitions, _nPartitions, _initialNumberOfTestCases, _budget, 0);
	}

	/**
	 * Constructor for cases without partitions or faults.
	 *
	 * @param _potentialTestSuite  The list of potential test cases.
	 * @param _initialNumberOfTestCases  The initial number of test cases to run.
	 * @param _budget              The total budget for test execution.
	 */
	public TestCaseSelector(ArrayList<TestCase> _potentialTestSuite, int _initialNumberOfTestCases, int _budget) {
		this(_potentialTestSuite, new ArrayList<>(), 0, _initialNumberOfTestCases, _budget, 0);
	}

	/**
	 * Validates the inputs provided to the selector.
	 */
	private void validateInputs() {
		if (budget > potentialTestSuite.size()) {
			throw new IllegalArgumentException("ERROR: BUDGET CANNOT BE GREATER THAN THE NUMBER OF INPUTS");
		}
		if (initialNumberOfTestCases > budget) {
			throw new IllegalArgumentException("ERROR: INITIAL NUMBER OF TEST CASES CANNOT BE GREATER THAN THE BUDGET");
		}
	}

	/**
	 * Abstract method to select and run test cases.
	 * This method must be implemented by subclasses.
	 */
	abstract void selectAndRunTestCase();
}