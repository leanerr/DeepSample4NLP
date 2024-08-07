package utility;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import selector.classification.PartitionInfo;
import weka.clusterers.SimpleKMeans;
import weka.core.Instances;
import weka.core.converters.ConverterUtils.DataSource;

public class TestSuiteFileLoader {

	private ArrayList<TestCase> localTestSuite;
	public int numberOfPartitions;
	public double tolerance;

	public TestSuiteFileLoader() {
		tolerance = 0.0001;
	}

	/****** RANDOM GENERATION OF TEST SUITE USED FOR SIMULATION ******/
	public ArrayList<TestCase> loadTestSuiteSimulation(int tsSize) {
		localTestSuite = new ArrayList<TestCase>();
		String name = "name";
		for (int index = 0; index < tsSize; index++) {
			TestCase tc = new TestCase(name, new Integer(index).toString());
			tc.setExecutionState(tc.getExecutionState().executed);
			localTestSuite.add(tc);
		}
		return localTestSuite;
	}

	public ArrayList<TestCase> loadTestSuiteSimulation_class(String csvFile, int key, int size) {
		localTestSuite = new ArrayList<TestCase>();
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(csvFile));
			String line = br.readLine();
			String[] parser = line.split(",");
			System.out.println("Auxiliary variable: " + parser[key]);
			double val = 0;
			String outcome;
			boolean fail = true;

			double occ = 1 / (double) size;
			int i = 0;

			while ((line = br.readLine()) != null) {
				i++;
				parser = line.split(",");
				outcome = parser[5]; // Corrected index to access 'Outcome' from your header
				if (outcome.equals("Pass")) {
					fail = false;
				} else {
					fail = true;
				}

				val = Double.parseDouble(parser[key]);

				if (key == 3 || key == 4) {  // Adjusted condition based on key relevance
					val = 1 - val;
				}

				localTestSuite.add(new TestCase(parser[0], parser[0], fail, occ, val));
			}

			if (i != size)
				System.out.println("[WARNING] The size is lower/greater! Read size: " + i);

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return localTestSuite;
	}

	public ArrayList<PartitionInfo> getPartitionsSimulation_kNN(int NPartitions, int key, String path) throws Exception {
		if (this.localTestSuite.isEmpty()) {
			throw new Exception("Test cases not loaded. Run the loadTestSuiteSimulation method before invoking getPartitions");
		}

		ArrayList<PartitionInfo> partitionsInfo = new ArrayList<PartitionInfo>();
		ArrayList<Integer> Indexes = new ArrayList<Integer>();
		this.numberOfPartitions = NPartitions;

		// Calculate clusters
		int[] clusters = compute_clusters(NPartitions, key, path);
		for (int i = 0; i < NPartitions; i++) {
			partitionsInfo.add(new PartitionInfo("" + i, "" + i));
			partitionsInfo.get(i).setListOfTests(new ArrayList<TestCase>());
		}

		for (int i = 0; i < this.localTestSuite.size(); i++) {
			partitionsInfo.get(clusters[i]).addTestCase(localTestSuite.get(i));
		}

		// Example correction in TestSuiteFileLoader
		for (int i = 0; i < NPartitions; i++) {
			partitionsInfo.get(i).computeStatistics(); // Ensure this matches the method name in PartitionInfo
		}

		checkWellFormedness(partitionsInfo);

		return partitionsInfo;
	}

	private void checkWellFormedness(ArrayList<PartitionInfo> partitionsInfo) throws Exception {
		System.out.println("...CHECK WELL-FORMEDNESS...");
		double sum2 = 0.0;
		double sumPerPart = 0.0;
		int countPerPart = 0;
		for (int i = 0; i < partitionsInfo.size(); i++) {
			for (int j = 0; j < partitionsInfo.get(i).getListOfTests().size(); j++) {
				sum2 += partitionsInfo.get(i).getListOfTests().get(j).getExpectedOccurrenceProbability();
				sumPerPart += partitionsInfo.get(i).getListOfTests().get(j).getExpectedOccurrenceProbability();
				countPerPart++;
			}
			if ((sumPerPart > partitionsInfo.get(i).getExpectedOccurrenceProbability() + tolerance)
					|| (sumPerPart < partitionsInfo.get(i).getExpectedOccurrenceProbability() - tolerance))
				throw new Exception("Sum of occurrence probabilities of test cases over partitions is not as expected ");
			if (countPerPart != partitionsInfo.get(i).getListOfTests().size())
				throw new Exception("Number of test cases per partition is not as expected ");
			sumPerPart = 0;
			countPerPart = 0;
		}
		if ((sum2 > 1.00 + tolerance) || (sum2 < 1 - tolerance))
			throw new Exception("Sum of occurrence probabilities of test cases is not 1");
		System.out.println(" ... DONE\n");
	}

	public int[] compute_clusters(int num_clusters, int key, String path) throws Exception {
		// Setup data sources, grab instances, and calculate the nearest neighbors
		DataSource source = new DataSource(path);
		Instances instances = source.getDataSet();

		// Debug: print initial attributes
		System.out.println("Initial number of attributes: " + instances.numAttributes());

		// Ensure only the key attribute is left for clustering
		for (int i = 0; i < instances.numAttributes(); i++) {
			if (i != key) {
				instances.deleteAttributeAt(0); // Always delete the first attribute until only the key remains
			}
		}

		// Debug: print remaining attributes
		System.out.println("Remaining number of attributes: " + instances.numAttributes());

		SimpleKMeans kmeans = new SimpleKMeans();
		kmeans.setPreserveInstancesOrder(true);
		kmeans.setNumClusters(num_clusters);
		kmeans.buildClusterer(instances);

		System.out.println(kmeans);

		int[] assignments = kmeans.getAssignments();
		return assignments;
	}

	// Legacy code for older simulations, left as-is for context
	public ArrayList<PartitionInfo> getPartitionsSimulation_old(int NPartitions, double minFL, double max_minFL) throws Exception {
		if (this.localTestSuite.isEmpty()) {
			throw new Exception("Test cases not loaded. Run the loadTestSuiteSimulation method before invoking getPartitions");
		}

		ArrayList<PartitionInfo> partitionsInfo = new ArrayList<PartitionInfo>();
		ArrayList<Integer> Indexes = new ArrayList<Integer>();
		this.numberOfPartitions = NPartitions;

		int size = (int) Math.floor((double) this.localTestSuite.size() / (double) NPartitions);

		int[] randIndex = new int[this.localTestSuite.size()];
		for (int j = 0; j < this.localTestSuite.size(); j++)
			Indexes.add(j);
		for (int j = 0; j < this.localTestSuite.size(); j++)
			randIndex[j] = Indexes.remove(new Random().nextInt(Indexes.size()));

		// FIXED SIZE PARTITIONS
		ArrayList<TestCase> tcList;
		double expectedFailureLikelihood;
		double[] expectedOccurrenceProbability = new double[NPartitions];
		double sum = 0;
		for (int i = 0; i < NPartitions; i++) {
			expectedOccurrenceProbability[i] = Math.random();
			sum = sum + expectedOccurrenceProbability[i];
		}
		for (int i = 0; i < NPartitions; i++)
			expectedOccurrenceProbability[i] = expectedOccurrenceProbability[i] / sum;

		for (int i = 0; i < NPartitions; i++) {
			expectedFailureLikelihood = minFL + Math.random() * (max_minFL); // random tra min e max
			PartitionInfo partition = new PartitionInfo("name", new Integer(i).toString());
			partition.setExpectedFailureLikelihood(expectedFailureLikelihood);
			partition.setExpectedOccurrenceProbability(expectedOccurrenceProbability[i]);
			tcList = new ArrayList<TestCase>();
			for (int k = 0; k < size; k++) {
				this.localTestSuite.get(randIndex[i * size + k]).setExpectedFailureLikelihood(expectedFailureLikelihood);
				this.localTestSuite.get(randIndex[i * size +k]).setExpectedOccurrenceProbability(expectedOccurrenceProbability[i] / (double) size);
				if (Math.random() <= expectedFailureLikelihood)
					this.localTestSuite.get(randIndex[i * size + k]).setOutcome(false);
				else
					this.localTestSuite.get(randIndex[i * size + k]).setOutcome(true);
				tcList.add(this.localTestSuite.get(randIndex[i * size + k]));
			}
			partition.setListOfTests(tcList);
			partitionsInfo.add(partition);
		}    checkWellFormedness(partitionsInfo);

		return partitionsInfo;
	}

	public ArrayList<PartitionInfo> getPartitionsSimulation(int NPartitions) throws Exception {
		if (this.localTestSuite.isEmpty()) {
			throw new Exception("Test cases not loaded. Run the loadTestSuiteSimulation method before invoking getPartitions");
		}

		ArrayList<PartitionInfo> partitionsInfo = new ArrayList<PartitionInfo>();
		ArrayList<Integer> Indexes = new ArrayList<Integer>();
		this.numberOfPartitions = NPartitions;

		int size = (int) Math.floor((double) this.localTestSuite.size() / (double) NPartitions);
		// DA CAMBIRAE - COSI'  PERDO QUALCHE CASO DI TEST SE NON HO INTERI MULTIPLI

		int[] randIndex = new int[this.localTestSuite.size()];
		for (int j = 0; j < this.localTestSuite.size(); j++)
			Indexes.add(j);
		for (int j = 0; j < this.localTestSuite.size(); j++)
			randIndex[j] = Indexes.remove(new Random().nextInt(Indexes.size()));

		// FIXED SIZE PARTITIONS
		ArrayList<TestCase> tcList;
		double expectedFailureLikelihood;
		double[] expectedOccurrenceProbability = new double[NPartitions];
		double sum = 0;
		for (int i = 0; i < NPartitions; i++) {
			expectedOccurrenceProbability[i] = 1;
			sum = sum + expectedOccurrenceProbability[i];
		}
		for (int i = 0; i < NPartitions; i++)
			expectedOccurrenceProbability[i] = expectedOccurrenceProbability[i] / sum;

		for (int i = 0; i < NPartitions; i++) {
			PartitionInfo partition = new PartitionInfo("name", new Integer(i).toString());
			partition.setExpectedOccurrenceProbability(expectedOccurrenceProbability[i]);
			tcList = new ArrayList<TestCase>();
			expectedFailureLikelihood = 0;
			for (int k = 0; k < size; k++) {
				this.localTestSuite.get(randIndex[i * size + k]).setExpectedOccurrenceProbability(expectedOccurrenceProbability[i] / (double) size);
				tcList.add(this.localTestSuite.get(randIndex[i * size + k]));
				expectedFailureLikelihood = expectedFailureLikelihood + this.localTestSuite.get(randIndex[i * size + k]).getExpectedFailureLikelihood();
			}
			partition.setExpectedFailureLikelihood(expectedFailureLikelihood / (double) size);
			partition.setListOfTests(tcList);
			partitionsInfo.add(partition);
		}

		checkWellFormedness(partitionsInfo);

		return partitionsInfo;
	}
}