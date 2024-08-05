#!/bin/bash

# Ensure we're using Bash
if [ -z "$BASH_VERSION" ]; then
    exec bash "$0" "$@"
fi

# Define an associative array to hold dataset names and sizes
declare -A datasets
datasets=(
    ["imdb300AuxDS"]=2999
    ["SSTIMDB3000AuxDS"]=1160
    ["SSTtestAuxDS"]=1820
    ["imdbAuxDS"]=50000
)

# Loop through each dataset and key
for dataset in "${!datasets[@]}"; do
    # Get the size from the associative array
    size=${datasets[$dataset]}

    # Loop through all auxiliary variable keys
    for key in {0..4}; do
        echo "Running for dataset: $dataset, key: $key, size: $size"

        # Execute the Java command
        java -cp "DeepSample/DeepSample_part_1_class.jar:libs/commons-lang3-3.12.0.jar:libs/commons-math3-3.6.1.jar:libs/weka.jar" main.Classification_main_jar "$dataset" "$key" "$size" >> log.txt

        # Check if the previous command was successful
        if [ $? -ne 0 ]; then
            echo "Execution failed for dataset: $dataset, key: $key" >> log.txt
        fi

        # Output the results of this execution
        echo "Finished execution for dataset: $dataset, key: $key"
    done
done

echo "All executions completed."