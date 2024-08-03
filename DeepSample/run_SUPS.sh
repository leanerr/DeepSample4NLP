#!/bin/bash

# Set the project root to the directory where this script is located
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

# Navigate to the project root directory to ensure relative paths work correctly
cd "$PROJECT_ROOT" || exit

# Define the datasets and their sizes using parallel arrays
datasets=("imdb300AuxDS" "SSTIMDB3000AuxDS" "SSTtestAuxDS" "imdbAuxDS")
dataset_sizes=(2999 1160 1820 50000)

# Define the list of auxiliary variables and their corresponding zero-based indices
auxiliary_variables=("confidence" "entropy" "similarity" "dsa" "lsa")
auxiliary_indices=(0 1 2 3 4)  # Correct zero-based indices

# Define a list of budgets to iterate over
budgets=(50 100 200 400 800 1600)

# Check if the results directory exists; if not, create it
results_dir="$PROJECT_ROOT/Results/Classification/SUPS"
mkdir -p "$results_dir"

# Check if the logs directory exists; if not, create it
log_dir="$PROJECT_ROOT/logs"
mkdir -p "$log_dir"

# Define the path to the dataset directory
DATASET_ROOT="$PROJECT_ROOT/dataset"

# Path to the SUPS_class.jar file
JAR_PATH="$PROJECT_ROOT/DeepSample/SUPS_class.jar"

# Process each dataset
for index in "${!datasets[@]}"
do
    dataset="${datasets[$index]}"
    dataset_size="${dataset_sizes[$index]}"

    # Ensure the dataset file path is correctly specified
    DATASET_PATH="$DATASET_ROOT/$dataset.csv"

    # Check if the dataset file exists
    if [ ! -f "$DATASET_PATH" ]; then
        echo "Dataset file not found: $DATASET_PATH"
        continue  # Skip to the next dataset if the file is not found
    fi

    # Iterate over each auxiliary variable
    for aux_index in "${!auxiliary_variables[@]}"
    do
        aux="${auxiliary_variables[$aux_index]}"
        aux_column_index="${auxiliary_indices[$aux_index]}"  # Get the correct index for the auxiliary variable

        # Iterate over each budget
        for budget in "${budgets[@]}"
        do
            # Construct the result path dynamically based on the auxiliary variable and budget
            result_path="$results_dir/${dataset}_${aux}_${budget}.txt"
            log_file="$log_dir/sups_log_${dataset}_${aux}_${budget}.txt"

            # Execute the Java program with the appropriate parameters
            echo "Running SUPS_classification for $dataset with auxiliary variable $aux and budget $budget..."
            java -Xmx51200m -jar "$JAR_PATH" "$dataset" "$aux" "$budget" "$aux_column_index" "$dataset_size" > "$result_path" 2> "$log_file"

            # Check if the command was successful
            if [ $? -eq 0 ]; then
                echo "Successfully executed SUPS_classification for $dataset with auxiliary variable $aux and budget $budget"
                echo "Results are stored in: $result_path"
            else
                echo "Error executing SUPS_classification for $dataset with auxiliary variable $aux and budget $budget"
                echo "Check log file: $log_file for more details."
            fi
        done
    done
done

echo "All datasets processed successfully."