import os
import pandas as pd
import numpy as np

# Define the datasets and budget constraints
datasets = ["imdb300AuxDS", "imdbAuxDS", "SSTtestAuxDS"]
aux_variables = ["Confidence_Score", "Prediction_Entropy", "Similarity_Score", "DSA", "LSA"]
budgets = [50, 100, 200, 400, 800]
root_dir = "../dataset"
output_dir = "SSRSpy/SSRSpyResults"

# Ensure the output directory exists
os.makedirs(output_dir, exist_ok=True)

def load_data(filename):
    """Load data from CSV files and convert 'Outcome' to binary."""
    df = pd.read_csv(os.path.join(root_dir, filename))
    df['Outcome'] = df['Outcome'].apply(lambda x: 1 if x.lower() == 'pass' else 0)
    return df

def stratified_sampling(df, budget, aux_var, num_partitions=10):
    """Perform stratified sampling with Neyman allocation."""
    try:
        df['partition'] = pd.qcut(df[aux_var], q=num_partitions, labels=False, duplicates='drop')
    except ValueError:
        df['partition'] = pd.cut(df[aux_var], bins=num_partitions, labels=False)

    groupby_partition = df.groupby('partition')[aux_var]
    std_devs = groupby_partition.std().fillna(0)
    sizes = groupby_partition.size()
    prop_allocations = (std_devs / std_devs.sum()) * sizes
    sample_sizes = np.floor(prop_allocations / prop_allocations.sum() * budget).astype(int)

    while sample_sizes.sum() < budget:
        sample_sizes[sample_sizes.idxmax()] += 1

    samples = []
    for partition, size in sample_sizes.items():
        available_size = min(size, sizes[partition])
        if available_size > 0:
            samples.append(df[df['partition'] == partition].sample(n=available_size, replace=False))

    return pd.concat(samples)

def simulate_accuracy_and_failures(sampled_data):
    """Calculate accuracy and number of failures."""
    failures = sampled_data['Outcome'].value_counts().get(0, 0)
    total = len(sampled_data)
    accuracy = (total - failures) / total if total > 0 else 0
    return accuracy, failures

# Run the stratified sampling for each dataset, auxiliary variable, and budget
for dataset in datasets:
    df = load_data(dataset + '.csv')
    for aux_var in aux_variables:
        for budget in budgets:
            output_filename = f"{output_dir}/{dataset}_{aux_var}_{budget}.txt"
            with open(output_filename, 'w') as file:
                file.write("accuracy,failures\n")
                for _ in range(30):  # Perform sampling 30 times for robust statistics
                    sampled_data = stratified_sampling(df, budget, aux_var)
                    accuracy, failures = simulate_accuracy_and_failures(sampled_data)
                    file.write(f"{accuracy},{failures}\n")
            print(f"Sampled data saved to {output_filename}")