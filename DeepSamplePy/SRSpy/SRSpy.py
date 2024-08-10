import pandas as pd
import numpy as np
import os

# Define the environment setup
datasets = ["imdb300AuxDS.csv", "imdbAuxDS.csv", "SSTtestAuxDS.csv"]
aux_variables = ["Confidence_Score", "Prediction_Entropy", "Similarity_Score", "DSA", "LSA"]
budgets = [50, 100, 200, 400, 800]
root_dir = "../dataset"
output_dir = "SRS_results"
os.makedirs(output_dir, exist_ok=True)

def load_data(filename):
    """Load data and convert 'Outcome' to binary."""
    df = pd.read_csv(os.path.join(root_dir, filename))
    df['Outcome'] = df['Outcome'].apply(lambda x: 1 if x.lower() == 'pass' else 0)
    return df

def simple_random_sampling(df, budget):
    """Perform simple random sampling with replacement."""
    return df.sample(n=budget, replace=True)

def calculate_metrics(sampled_data):
    """Calculate accuracy and count of failures."""
    if not sampled_data.empty:
        accuracy = sampled_data['Outcome'].mean()
        failures = len(sampled_data) - sampled_data['Outcome'].sum()
        return accuracy, failures
    return 0, 0

# Processing all datasets, auxiliary variables, and budgets
for dataset in datasets:
    df = load_data(dataset)
    for aux_var in aux_variables:
        for budget in budgets:
            output_filename = f"{output_dir}/{dataset[:-4]}_{aux_var}_{budget}.txt"
            with open(output_filename, 'w') as file:
                file.write("accuracy,failures\n")
                for _ in range(30):
                    sampled_data = simple_random_sampling(df, budget)
                    accuracy, failures = calculate_metrics(sampled_data)
                    file.write(f"{accuracy},{failures}\n")
            print(f"Results saved to {output_filename}")