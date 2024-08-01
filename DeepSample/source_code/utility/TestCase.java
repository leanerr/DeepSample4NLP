package utility;
import java.io.PrintWriter;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class TestCase {
	private String name;
	private String tcID;
	private int numberOfCommands;
	private ArrayList<String> listOfCommands;
	private ArrayList<?> inputs;
	private int maxNumberOfInputs;
	private Path pathRootDirectory;
	private boolean outcome;
	private ExecutionState executionState;
	private double expectedOccurrenceProbability;
	private double realOccurrenceProbability;
	private double expectedFailureLikelihood;
	private boolean outcomeClass;

	// New auxiliary variables
	private double confidenceScore;
	private double predictionEntropy;
	private double similarityScore;
	private double dsa;
	private double lsa;

	public enum ExecutionState { executed, notExecuted }

	// Constructor: TestCase(String, String)
	public TestCase(String _name, String _tcID) {
		name = _name;
		tcID = _tcID;
		outcome = false;
		executionState = ExecutionState.notExecuted;
		pathRootDirectory = Paths.get(System.getProperty("user.dir"));
	}

	// Constructor: TestCase(String, String, boolean, double, double)
	public TestCase(String _name, String _tcID, boolean _outcomeClass, double _occ, double _failProb) {
		name = _name;
		tcID = _tcID;
		outcomeClass = _outcomeClass;
		outcome = !(_outcomeClass);
		expectedFailureLikelihood = _failProb;
		expectedOccurrenceProbability = _occ;
		realOccurrenceProbability = _occ;
		executionState = ExecutionState.notExecuted;
		pathRootDirectory = Paths.get(System.getProperty("user.dir"));
	}

	public ExecutionState getExecutionState() {
		return executionState;
	}

	public void setExecutionState(ExecutionState executionState) {
		this.executionState = executionState;
	}

	public boolean runTestCase(String string) {
		outcome = this.getOutcome();
		executionState = ExecutionState.executed;
		return outcome;
	}

	private File createTempScript(Path directoryPath) throws IOException {
		File tempScript = File.createTempFile("script", null, new File(directoryPath.toString()));
		String cmdPerm = "chmod +x " + tempScript;
		String[] cmds = {"bash", "-c", cmdPerm};
		try {
			Process process2 = Runtime.getRuntime().exec(cmds);
			process2.waitFor();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		try (FileWriter fileout = new FileWriter(tempScript);
			 BufferedWriter filebuf = new BufferedWriter(fileout);
			 PrintWriter printWriter = new PrintWriter(filebuf)) {

			printWriter.println("#!/bin/bash");
			for (String command : this.listOfCommands) {
				printWriter.println(command);
			}
		}
		return tempScript;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getNumber() {
		return tcID;
	}

	public void setNumber(String number) {
		this.tcID = number;
	}

	public int getNumberOfCommands() {
		return numberOfCommands;
	}

	public void setNumberOfCommands(int numberOfCommands) {
		this.numberOfCommands = numberOfCommands;
	}

	public ArrayList<String> getListOfCommands() {
		return listOfCommands;
	}

	public void setListOfCommands(ArrayList<String> listOfCommands) {
		this.listOfCommands = listOfCommands;
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

	public void setOutcome(boolean outcome) {
		this.outcome = outcome;
	}

	public boolean getOutcome() {
		return this.outcome;
	}

	public ArrayList<?> getInputs() {
		return inputs;
	}

	public void setInputs(ArrayList<?> inputs) {
		this.inputs = inputs;
	}

	public int getMaxNumberOfInputs() {
		return maxNumberOfInputs;
	}

	public void setMaxNumberOfInputs(int maxNumberOfInputs) {
		this.maxNumberOfInputs = maxNumberOfInputs;
	}

	public String getTcID() {
		return tcID;
	}

	public void setTcID(String tcID) {
		this.tcID = tcID;
	}

	public double getConfidenceScore() {
		return confidenceScore;
	}

	public void setConfidenceScore(double confidenceScore) {
		this.confidenceScore = confidenceScore;
	}

	public double getPredictionEntropy() {
		return predictionEntropy;
	}

	public void setPredictionEntropy(double predictionEntropy) {
		this.predictionEntropy = predictionEntropy;
	}

	public double getSimilarityScore() {
		return similarityScore;
	}

	public void setSimilarityScore(double similarityScore) {
		this.similarityScore = similarityScore;
	}

	public double getDsa() {
		return dsa;
	}

	public void setDsa(double dsa) {
		this.dsa = dsa;
	}

	public double getLsa() {
		return lsa;
	}

	public void setLsa(double lsa) {
		this.lsa = lsa;
	}

	public double getExpectedFailureLikelihood() {
		return expectedFailureLikelihood;
	}

	public void setExpectedFailureLikelihood(double expectedFailureLikelihood) {
		this.expectedFailureLikelihood = expectedFailureLikelihood;
	}
}