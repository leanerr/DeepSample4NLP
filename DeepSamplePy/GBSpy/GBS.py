import os
import pandas as pd
import numpy as np

# Define the datasets and auxiliary variables
datasets = ["SSTtestAuxDS"]
aux_variables = ["Confidence_Score", "Prediction_Entropy", "Similarity_Score", "DSA", "LSA"]
budgets = [50, 100, 200, 400, 800]
output_dir = "GBSpy/GBSpyResults"

# Create the output directory if it does not exist
os.makedirs(output_dir, exist_ok=True)

# Function to convert outcome to numerical values
def convert_outcome(outcome):
    return 1 if outcome.lower() == "pass" else 0

# Function to perform GBS
def gradient_based_sampling(dataset_name, aux_var, budget):
    # Load the dataset
    df = pd.read_csv(f"../dataset/{dataset_name}.csv")

    # Convert outcomes to 0/1
    df['Outcome'] = df['Outcome'].apply(convert_outcome)

    # Initialize storage for results
    accuracies = []
    failures = []

    for _ in range(30):  # 30 repetitions for each setting
        total_samples = 0
        total_failures = 0

        # Calculate initial variance for each partition
        partitions = df.groupby(aux_var)
        partition_variances = {name: group['Outcome'].var() if len(group) > 1 else 0 for name, group in partitions}

        sampled_partitions = {name: [] for name in partitions.groups.keys()}

        for _ in range(budget):
            # Select partition with the largest negative gradient
            gradient_partitions = {name: (-partition_variances[name] / (len(sampled_partitions[name]) + 1))
                                   for name in partitions.groups.keys() if len(sampled_partitions[name]) < len(partitions.get_group(name))}

            if not gradient_partitions:
                print(f"No valid gradients calculated. Check partitioning for {dataset_name} on {aux_var}")
                break

            # Handle ties in gradient selection
            max_gradient_value = max(gradient_partitions.values())
            potential_partitions = [name for name, grad in gradient_partitions.items() if grad == max_gradient_value]
            selected_partition = np.random.choice(potential_partitions)

            # Sample with replacement from the selected partition
            sample = partitions.get_group(selected_partition).sample(n=1, replace=True)
            sampled_partitions[selected_partition].append(sample)

            # Update total samples and failures
            total_samples += 1
            if sample['Outcome'].values[0] == 0:  # Fail
                total_failures += 1

            # Recalculate variances for sampled partition
            sampled_data = pd.concat(sampled_partitions[selected_partition])
            partition_variances[selected_partition] = sampled_data['Outcome'].var() if len(sampled_data) > 1 else 0

        if total_samples > 0:
            accuracy = (total_samples - total_failures) / total_samples
            accuracies.append(accuracy)
            failures.append(total_failures)
        else:
            print(f"No samples collected for {dataset_name} with {aux_var} and budget {budget}")
            accuracies.append(0)
            failures.append(budget)  # Assuming max failures if no samples collected

    return accuracies, failures

# Main loop to generate outputs
for dataset in datasets:
    for aux_var in aux_variables:
        for budget in budgets:
            filename = f"{output_dir}/{dataset}_{aux_var}_{budget}.txt"

            # Perform GBS for the current setting
            accuracies, failures = gradient_based_sampling(dataset, aux_var, budget)

            # Write results to file
            with open(filename, 'w') as f:
                f.write("accuracy,failures\n")
                for accuracy, failure in zip(accuracies, failures):
                    f.write(f"{accuracy},{failure}\n")

            print(f"Generated: {filename}")
