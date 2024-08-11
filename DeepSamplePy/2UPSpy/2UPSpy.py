import pandas as pd
import numpy as np
import os

# Setup environment
datasets = ["imdb300AuxDS.csv", "imdbAuxDS.csv", "SSTtestAuxDS.csv"]
aux_variables = ["Confidence_Score", "Prediction_Entropy", "Similarity_Score", "DSA", "LSA"]
budgets = [50, 100, 200, 400, 800]
root_dir = "../dataset"
output_dir = "TwoUPSSamplingResults"
os.makedirs(output_dir, exist_ok=True)

def load_data(filename):
    df = pd.read_csv(os.path.join(root_dir, filename))
    df['Outcome'] = df['Outcome'].apply(lambda x: 1 if x.lower() == 'pass' else 0)
    return df

def calculate_partition_probabilities(df, aux_variable, num_partitions):
    df['partition'] = pd.qcut(df[aux_variable], q=num_partitions, labels=False, duplicates='drop')
    partition_sums = df.groupby('partition')[aux_variable].sum()
    total_sum = partition_sums.sum()
    partition_probabilities = partition_sums / total_sum
    return partition_probabilities, df['partition'].unique()

def two_stage_sampling(df, aux_variable, num_partitions=10, budget=100):
    partition_probabilities, partitions = calculate_partition_probabilities(df, aux_variable, num_partitions)
    selected_partitions = np.random.choice(partitions, size=budget, p=partition_probabilities.values, replace=True)

    samples = []
    for partition in selected_partitions:
        partition_data = df[df['partition'] == partition]
        if not partition_data.empty:
            # Increase likelihood of sampling failures if they are underrepresented
            if partition_data['Outcome'].mean() < 0.5:  # More failures than successes
                sample = partition_data.sample(n=1, replace=False, weights='Outcome' if partition_data['Outcome'].sum() > 0 else None)
            else:
                sample = partition_data.sample(n=1, replace=False)
            samples.append(sample)

    sampled_data = pd.concat(samples) if samples else pd.DataFrame()
    if not sampled_data.empty:
        sampled_data['weight'] = 1 / partition_probabilities[sampled_data['partition']].values
        sampled_data['weight'] *= (budget / sampled_data['weight'].sum())
    return sampled_data

def calculate_hansen_hurwitz_estimators(samples):
    if not samples.empty:
        weighted_failures = np.sum((1 - samples['Outcome']) * samples['weight'])
        total_weight = np.sum(samples['weight'])
        accuracy = np.sum(samples['Outcome'] * samples['weight']) / total_weight if total_weight > 0 else 0
        return accuracy, int(weighted_failures)
    else:
        return 0, 0

for dataset in datasets:
    df = load_data(dataset)
    for aux_var in aux_variables:
        for budget in budgets:
            output_filename = f"{output_dir}/{dataset[:-4]}_{aux_var}_{budget}.txt"
            with open(output_filename, 'w') as file:
                file.write("accuracy,failures\n")
                for _ in range(30):
                    sampled_data = two_stage_sampling(df, aux_var, num_partitions=10, budget=budget)
                    accuracy, failures = calculate_hansen_hurwitz_estimators(sampled_data)
                    file.write(f"{accuracy},{failures}\n")
            print(f"Sampled data saved to {output_filename}")