import os
import pandas as pd
import numpy as np
import torch

# Define the datasets and auxiliary variables
datasets = ["imdbAuxDS"]
aux_variables = ["Confidence_Score", "Prediction_Entropy", "Similarity_Score", "DSA", "LSA"]
budgets = [50, 100, 200, 400, 800]
output_dir = "GBSpy/GBSpyResults"

# Ensure the output directory exists
os.makedirs(output_dir, exist_ok=True)

def convert_outcome(outcome):
    return 1 if outcome.lower() == "pass" else 0

def load_data_to_tensor(filepath):
    # Load data into pandas DataFrame
    df = pd.read_csv(filepath)
    df['Outcome'] = df['Outcome'].apply(convert_outcome)

    # Convert DataFrame to PyTorch tensor
    device = torch.device("cuda" if torch.cuda.is_available() else "cpu")
    outcomes_tensor = torch.tensor(df['Outcome'].values, dtype=torch.float32).to(device)
    return outcomes_tensor

def gradient_based_sampling(dataset_name, aux_var, budget):
    # Path to dataset
    dataset_path = f"../dataset/{dataset_name}.csv"
    outcomes = load_data_to_tensor(dataset_path)

    accuracies = []
    failures = []

    for _ in range(30):  # 30 repetitions for each setting
        total_samples = 0
        total_failures = 0

        # Sampling simulation: just random sampling for this example
        for _ in range(budget):
            indices = torch.randperm(len(outcomes))[:1]  # Simulate random sampling
            sample = outcomes[indices]
            total_samples += 1
            total_failures += 1 - sample.sum().item()  # Count failures

        accuracy = (total_samples - total_failures) / total_samples if total_samples > 0 else 0
        accuracies.append(accuracy)
        failures.append(total_failures)

    return accuracies, failures

# Main loop to generate outputs
for dataset in datasets:
    for aux_var in aux_variables:
        for budget in budgets:
            accuracies, failures = gradient_based_sampling(dataset, aux_var, budget)
            filename = f"{output_dir}/{dataset}_{aux_var}_{budget}.txt"
            with open(filename, 'w') as f:
                f.write("accuracy,failures\n")
                for accuracy, failure in zip(accuracies, failures):
                    f.write(f"{accuracy},{failure}\n")
            print(f"Generated: {filename}")