package utility;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class InitializerTF {

	private String csvFile;

	public InitializerTF(String path) {
		csvFile = path;
	}

	public ArrayList<TestFrame> readTestFrames(int key, int size) {
		BufferedReader br = null;
		String line = "";
		ArrayList<TestFrame> aux = new ArrayList<TestFrame>();

		try {
			br = new BufferedReader(new FileReader(csvFile));
			line = br.readLine(); // Read header
			String[] parser = line.split(",");

			// Debug: Print headers and check if key is within bounds
			System.out.println("Headers: " + Arrays.toString(parser));
			System.out.println("Key index: " + key);

			if (key >= parser.length - 1) {
				throw new IllegalArgumentException("Invalid key index. Must be within the range of numeric columns.");
			}

			double val = 0;
			String outcome;
			boolean fail = true;
			double occ = 1.0 / (double) size;
			int i = 0;

			while ((line = br.readLine()) != null) {
				i++;
				parser = line.split(",");

				// Ensure there's no out of bounds error
				if (parser.length < key + 1) {
					System.err.println("[ERROR] Line does not have enough columns: " + line);
					continue;
				}

				outcome = parser[parser.length - 1];
				fail = outcome.equalsIgnoreCase("Fail");

				try {
					val = Double.parseDouble(parser[key]);
					System.out.println("Parsed value: " + val + " from line: " + line);
				} catch (NumberFormatException e) {
					System.err.println("[ERROR] Number format exception: " + e.getMessage() + " in line: " + line);
					continue;
				}

				if (key == 0 || key == 5) { // Updated indexes for confidence and combo
					val = 1 - val;
				}

				// Convert the outcome to binary
				int binaryOutcome = fail ? 1 : 0;

				aux.add(new TestFrame("" + aux.size(), "" + aux.size(), val, occ, parser[0], binaryOutcome));
			}

			if (i != size) {
				System.out.println("[WARNING] The size is lower/greater!!! " + i);
			}

		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			System.err.println("[ERROR] " + e.getMessage());
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		return aux;
	}

	public double[][] weightedMatrixComputation(ArrayList<TestFrame> tf, int key) {
		double[][] wm = new double[tf.size()][tf.size()];
		double conf_i, conf_j;
		double threshold;

		// Set the threshold based on the dataset and key
		if (csvFile.contains("imdb300AuxDS")) {
			threshold = (key == 0) ? 0.7 : (key == 3) ? 1.1002582828849559 : 0.20060669187064412;
		} else if (csvFile.contains("SSTIMDB3000AuxDS")) {
			threshold = (key == 0) ? 0.7 : (key == 3) ? 1.0999657275806323 : 0.7396367533581553;
		} else if (csvFile.contains("SSTtestAuxDS")) {
			threshold = (key == 0) ? 0.7 : (key == 3) ? 1.185390441280056 : 0.15676195310276675;
		} else if (csvFile.contains("imdbAuxDS")) {
			threshold = (key == 0) ? 0.7 : (key == 3) ? 1.088059648139901 : 0.217697339165843;
		} else {
			threshold = 0.3; // Default value for any other dataset
		}

		for (int i = 0; i < tf.size(); i++) {
			conf_i = tf.get(i).getFailureProb();
			for (int j = 0; j < tf.size(); j++) {
				conf_j = tf.get(j).getFailureProb();
				if (conf_j > threshold) {
					wm[i][j] = conf_j;
				} else {
					wm[i][j] = 0;
				}
			}
		}

		return wm;
	}

	public double[][] weightedMatrixComputation_threshold(ArrayList<TestFrame> tf, int key, double threshold) {
		double[][] wm = new double[tf.size()][tf.size()];
		double conf_j;

		for (int i = 0; i < tf.size(); i++) {
			for (int j = 0; j < tf.size(); j++) {
				conf_j = tf.get(j).getFailureProb();
				if (conf_j > threshold) {
					wm[i][j] = conf_j;
				} else {
					wm[i][j] = 0;
				}
			}
		}

		return wm;
	}
}
/*

	•	imdb300AuxDS
	•	Threshold LSA: 0.20060669187064412
	•	Threshold DSA: 1.1002582828849559
	•	Dataset Size: 2999
	•	SSTIMDB3000AuxDS
	•	Threshold LSA: 0.7396367533581553
	•	Threshold DSA: 1.0999657275806323
	•	Dataset Size: 1160
	•	SSTtestAuxDS
	•	Threshold LSA: 0.15676195310276675
	•	Threshold DSA: 1.185390441280056
	•	Dataset Size: 1820
	•	imdbAuxDS
	•	Threshold LSA: 0.217697339165843
	•	Threshold DSA: 1.088059648139901
	•	Dataset Size: 50000




	java -cp "DeepSample/DeepSample_part_1_class.jar:libs/commons-lang3-3.12.0.jar:libs/commons-math3-3.6.1.jar:libs/weka.jar" main.Classification_main_jar imdb300AuxDS 0 2999


java -cp "DeepSample/DeepSample_part_1_class.jar:libs/commons-lang3-3.12.0.jar:libs/commons-math3-3.6.1.jar:libs/weka.jar" main.Classification_main_jar SSTIMDB3000AuxDS 0 1160


java -cp "DeepSample/DeepSample_part_1_class.jar:libs/commons-lang3-3.12.0.jar:libs/commons-math3-3.6.1.jar:libs/weka.jar" main.Classification_main_jar SSTtestAuxDS 0 1820


java -cp "DeepSample/DeepSample_part_1_class.jar:libs/commons-lang3-3.12.0.jar:libs/commons-math3-3.6.1.jar:libs/weka.jar" main.Classification_main_jar imdbAuxDS 0 50000
 */



/*
(env) aliasgari@Rebellean DeepSample-master % javac -d bin -cp "/Users/aliasgari/Downloads/DeepSample-master/libs/commons-lang3-3.12.0.jar:/Users/aliasgari/Downloads/DeepSample-master/libs/commons-math3-3.6.1.jar:/Users/aliasgari/Downloads/DeepSample-master/libs/weka.jar:." $(find main utility selector/classification -name "*.java")

 */


/*
Problems for part1:

java -cp "DeepSample/DeepSample_part_1_class.jar:libs/commons-lang3-3.12.0.jar:libs/commons-math3-3.6.1.jar:libs/weka.jar" main.Classification_main_jar imdb300AuxDS 2 2999
java -cp "DeepSample/DeepSample_part_1_class.jar:libs/commons-lang3-3.12.0.jar:libs/commons-math3-3.6.1.jar:libs/weka.jar" main.Classification_main_jar imdb300AuxDS 4 2999

java -cp "DeepSample/DeepSample_part_1_class.jar:libs/commons-lang3-3.12.0.jar:libs/commons-math3-3.6.1.jar:libs/weka.jar" main.Classification_main_jar SSTIMDB3000AuxDS 0 1160
java -cp "DeepSample/DeepSample_part_1_class.jar:libs/commons-lang3-3.12.0.jar:libs/commons-math3-3.6.1.jar:libs/weka.jar" main.Classification_main_jar SSTIMDB3000AuxDS 2 1160
java -cp "DeepSample/DeepSample_part_1_class.jar:libs/commons-lang3-3.12.0.jar:libs/commons-math3-3.6.1.jar:libs/weka.jar" main.Classification_main_jar SSTIMDB3000AuxDS 4 1160

java -cp "DeepSample/DeepSample_part_1_class.jar:libs/commons-lang3-3.12.0.jar:libs/commons-math3-3.6.1.jar:libs/weka.jar" main.Classification_main_jar SSTtestAuxDS 2 1820


java -cp "DeepSample/DeepSample_part_1_class.jar:libs/commons-lang3-3.12.0.jar:libs/commons-math3-3.6.1.jar:libs/weka.jar" main.Classification_main_jar imdbAuxDS 0 50000
java -cp "DeepSample/DeepSample_part_1_class.jar:libs/commons-lang3-3.12.0.jar:libs/commons-math3-3.6.1.jar:libs/weka.jar" main.Classification_main_jar imdbAuxDS 1 50000
java -cp "DeepSample/DeepSample_part_1_class.jar:libs/commons-lang3-3.12.0.jar:libs/commons-math3-3.6.1.jar:libs/weka.jar" main.Classification_main_jar imdbAuxDS 2 50000
java -cp "DeepSample/DeepSample_part_1_class.jar:libs/commons-lang3-3.12.0.jar:libs/commons-math3-3.6.1.jar:libs/weka.jar" main.Classification_main_jar imdbAuxDS 3 50000
java -cp "DeepSample/DeepSample_part_1_class.jar:libs/commons-lang3-3.12.0.jar:libs/commons-math3-3.6.1.jar:libs/weka.jar" main.Classification_main_jar imdbAuxDS 4 50000



 */