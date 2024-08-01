import pandas as pd
import sys

# Ensure the correct number of arguments
if len(sys.argv) != 2:
    print("Usage: python3 threshold_dsa.py <dataset_name>")
    sys.exit(1)

# Get the dataset name from the command-line arguments
dataset_name = sys.argv[1]

# Load the dataset
try:
    df = pd.read_csv(f"{dataset_name}.csv")
except FileNotFoundError:
    print(f"Error: File {dataset_name}.csv not found")
    sys.exit(1)

# Calculate the threshold for DSA
threshold_dsa = df["DSA"].mean() + 2 * df["DSA"].std()

# Get the size of the dataset
dataset_size = len(df)

print(f"Threshold DSA: {threshold_dsa}")
print(f"Dataset Size: {dataset_size}")