package utility;

public class TestFrame {

	private String name; // Name of the test frame
	private String tfID; // Test frame ID
	private double failureProb; // Failure probability
	private double occurrenceProb; // Probability of occurrence
	private String output; // Output or result of the test frame
	private int fail; // Binary indicating if the test frame failed

	public TestFrame(String _name, String _tfID, double _failureProb, double _occurrenceProb, String _output, int _fail) {
		super();
		this.name = _name;
		this.tfID = _tfID;
		this.failureProb = _failureProb;
		this.occurrenceProb = _occurrenceProb;
		this.output = _output;
		this.fail = _fail;
	}

	// Method to extract and execute the test case, returning the fail status as an int
	public int extractAndExecuteTestCase() {
		return this.fail;
	}

	// Getter for output
	public String getOutput() {
		return output;
	}

	// Getter for name
	public String getName() {
		return name;
	}

	// Setter for name
	public void setName(String name) {
		this.name = name;
	}

	// Getter for tfID
	public String getTfID() {
		return tfID;
	}

	// Setter for tfID
	public void setTfID(String tfID) {
		this.tfID = tfID;
	}

	// Getter for failureProb
	public double getFailureProb() {
		return failureProb;
	}

	// Setter for failureProb
	public void setFailureProb(double failureProb) {
		this.failureProb = failureProb;
	}

	// Getter for occurrenceProb
	public double getOccurrenceProb() {
		return occurrenceProb;
	}

	// Setter for occurrenceProb
	public void setOccurrenceProb(double occurrenceProb) {
		this.occurrenceProb = occurrenceProb;
	}

	@Override
	public String toString() {
		return "TestFrame [name=" + name + ", tfID=" + tfID + ", failureProb=" + failureProb + ", occurrenceProb=" + occurrenceProb
				+ ", output=" + output + ", fail=" + fail + "]";
	}
}