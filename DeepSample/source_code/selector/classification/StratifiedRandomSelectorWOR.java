package selector.classification;

import java.util.ArrayList;
import java.util.Random;

import utility.TestCase;

// StratifiedRandomSelectorWOR class implementing stratified sampling without replacement
public class StratifiedRandomSelectorWOR extends TestCaseSelector {

    // Variables to track reliability estimates and outcomes
    public double REstimate;
    public double trueReliability;
    public double numberOfDetectedFailurePoints;
    public double numberOfExecutedTestCases;
    public long executionTime;

    // Constructor to initialize the selector with the given parameters
    public StratifiedRandomSelectorWOR(ArrayList<TestCase> potentialTestSuite, ArrayList<ArrayList<TestCase>> partitions,
                                       int nPartitions, int initialNumberOfTestCases, int budget, int numberOfFaults) {
        super(potentialTestSuite, partitions, nPartitions, initialNumberOfTestCases, budget, numberOfFaults);
    }

    // Method to select and run test cases
    public void selectAndRunTestCase() {
        TestCase testCaseToExecute;
        boolean testOutcome;
        int totalFailurePoint = 0;
        int indexCurrentTest = 0;
        int totalTests = this.budget;

        double[] executedTestCasesPerPartition = new double[this.numberOfPartitions]; // ni
        double[] failureRates = new double[this.numberOfPartitions]; // theta
        int[] failedTestCases = new int[this.numberOfPartitions]; // z
        double[] domainProbSum = new double[this.numberOfPartitions];  // DOMAIN SIZE: p
        int indexPartition;

        // Calculate optimal allocation using auxiliary variables
        double[] meanFailureLikelihood = new double[this.numberOfPartitions];
        double[] varianceFailureLikelihood = new double[this.numberOfPartitions];
        double sumOfWeightedSTD = 0;

        // Step 1: Calculate mean and variance intra-partitions
        for (indexPartition = 0; indexPartition < this.numberOfPartitions; indexPartition++) {
            meanFailureLikelihood[indexPartition] = 0;
            for (int index = 0; index < this.partitions.get(indexPartition).size(); index++) {
                meanFailureLikelihood[indexPartition] += this.partitions.get(indexPartition).get(index).getExpectedFailureLikelihood();
            }
            meanFailureLikelihood[indexPartition] /= this.partitions.get(indexPartition).size();

            varianceFailureLikelihood[indexPartition] = 0;
            for (int index = 0; index < this.partitions.get(indexPartition).size(); index++) {
                varianceFailureLikelihood[indexPartition] += Math.pow((this.partitions.get(indexPartition).get(index).getExpectedFailureLikelihood() - meanFailureLikelihood[indexPartition]), 2);
            }
            varianceFailureLikelihood[indexPartition] /= (this.partitions.get(indexPartition).size() - 1);
            sumOfWeightedSTD += Math.sqrt(varianceFailureLikelihood[indexPartition]) * this.partitions.get(indexPartition).size();
        }

        int[] testCasesPerPartition = new int[this.numberOfPartitions];
        int temp_tot = 0;

        // Allocate test cases proportionally to variance and partition size
        for (indexPartition = 0; indexPartition < this.numberOfPartitions; indexPartition++) {
            testCasesPerPartition[indexPartition] = Math.min(this.partitions.get(indexPartition).size(),
                    (int) Math.round(totalTests * this.partitions.get(indexPartition).size() * Math.sqrt(varianceFailureLikelihood[indexPartition]) / sumOfWeightedSTD));
            temp_tot += testCasesPerPartition[indexPartition];
        }

        int residual_tests = totalTests - temp_tot;
        int add_tests = 0;

        // Distribute remaining tests
        if (residual_tests > 0) {
            for (indexPartition = 0; indexPartition < this.numberOfPartitions; indexPartition++) {
                add_tests = Math.min(this.partitions.get(indexPartition).size() - testCasesPerPartition[indexPartition],
                        (int) Math.round(residual_tests * this.partitions.get(indexPartition).size() * Math.sqrt(varianceFailureLikelihood[indexPartition]) / sumOfWeightedSTD));
                testCasesPerPartition[indexPartition] += add_tests;
                temp_tot += add_tests;
            }

            residual_tests = totalTests - temp_tot;
            indexPartition = 0;

            while (residual_tests > 0) {
                if (this.partitions.get(indexPartition).size() - testCasesPerPartition[indexPartition] > 0) {
                    add_tests = Math.min(residual_tests, this.partitions.get(indexPartition).size() - testCasesPerPartition[indexPartition]);
                    testCasesPerPartition[indexPartition] += add_tests;
                    temp_tot += add_tests;
                    residual_tests = totalTests - temp_tot;
                }
                indexPartition++;
            }
        }

        // Initialize timing
        long initTime = System.currentTimeMillis();

        // Execute test cases and collect failure data
        double[] failureRatePerPartition = new double[this.numberOfPartitions];
        double sumFailureRate = 0.0;

        for (indexPartition = 0; indexPartition < this.numberOfPartitions; indexPartition++) {
            failureRatePerPartition[indexPartition] = 0.0;

            ArrayList<TestCase> sampleTestCase = new ArrayList<>(this.partitions.get(indexPartition));

            for (int numberOfTests = 0; numberOfTests < testCasesPerPartition[indexPartition]; numberOfTests++) {
                int randIndex = new Random().nextInt(sampleTestCase.size());
                testCaseToExecute = sampleTestCase.get(randIndex);

                testOutcome = testCaseToExecute.runTestCase("FITTIZIO");
                if (!testOutcome) {
                    failedTestCases[indexPartition]++;
                    totalFailurePoint++;
                }
                executedTestCasesPerPartition[indexPartition]++;
                indexCurrentTest++;
                sampleTestCase.remove(randIndex);
            }

            if (executedTestCasesPerPartition[indexPartition] > 0) {
                failureRatePerPartition[indexPartition] = failedTestCases[indexPartition] / executedTestCasesPerPartition[indexPartition];
                sumFailureRate += failureRatePerPartition[indexPartition] * this.partitions.get(indexPartition).size();
            }
        }

        // Calculate reliability estimate
        sumFailureRate /= potentialTestSuite.size();
        this.REstimate = 1 - sumFailureRate;
        this.trueReliability = computeTrueReliability();

        // Calculate execution time
        long endTime = System.currentTimeMillis();
        this.executionTime = endTime - initTime;
        this.numberOfExecutedTestCases = indexCurrentTest;
        this.numberOfDetectedFailurePoints = totalFailurePoint;
    }

    // Compute the true reliability by evaluating all test cases
    private double computeTrueReliability() {
        boolean outcome;
        double unrel = 0.0;
        int totalFailPoints = 0;
        for (int i = 0; i < this.potentialTestSuite.size(); i++) {
            outcome = this.potentialTestSuite.get(i).getOutcome();
            if (!outcome) {
                unrel += this.potentialTestSuite.get(i).getExpectedOccurrenceProbability();
                totalFailPoints++;
            }
        }
        return (1 - unrel);
    }
}