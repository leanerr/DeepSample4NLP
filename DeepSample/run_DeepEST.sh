#!/bin/bash

# Determine the project root dynamically based on the script's location
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/.." && pwd)"

# Navigate to the dataset directory
cd "$PROJECT_ROOT/dataset" || exit

# Activate the Python virtual environment
source env/bin/activate

# Define the list of auxiliary variables to process
auxiliary_variables=("confidence" "entropy" "similarity" "dsa" "lsa")

# Process each dataset
for dataset in imdb300AuxDS SSTIMDB3000AuxDS SSTtestAuxDS imdbAuxDS
do
    # Run the threshold_lsa.py script and capture output
    lsa_output=$(python3 threshold_lsa.py $dataset)
    threshold_lsa=$(echo "$lsa_output" | grep 'Threshold LSA' | awk '{print $3}')
    dataset_size=$(echo "$lsa_output" | grep 'Dataset Size' | awk '{print $3}')

    # Run the threshold_dsa.py script and capture output
    dsa_output=$(python3 threshold_dsa.py $dataset)
    threshold_dsa=$(echo "$dsa_output" | grep 'Threshold DSA' | awk '{print $3}')

    budget=50

    # Iterate over each auxiliary variable
    for aux in "${auxiliary_variables[@]}"
    do
        case $aux in
            confidence)
                threshold=0.7
                ;;
            entropy)
                threshold=0.5  # Adjust as needed
                ;;
            similarity)
                threshold=0.5  # Adjust as needed
                ;;
            lsa)
                threshold=$threshold_lsa
                ;;
            dsa)
                threshold=$threshold_dsa
                ;;
            *)
                echo "Invalid auxiliary variable: $aux"
                continue
                ;;
        esac

        # Construct the result path dynamically based on the auxiliary variable
        result_path="$PROJECT_ROOT/Results/Classification/DeepEST/${dataset}.${aux}"

        # Execute the Java program with the appropriate parameters
        java -Xmx51200m -cp "$PROJECT_ROOT/DeepSample/source_code/bin:$PROJECT_ROOT/libs/commons-lang3-3.12.0.jar:$PROJECT_ROOT/libs/commons-math3-3.6.1.jar:$PROJECT_ROOT/libs/weka.jar" main.DeepEST_classification "$PROJECT_ROOT/dataset/$dataset.csv" $aux $threshold $budget $dataset_size $result_path >> log.txt

        # Clear the log file after each execution
        rm log.txt
    done
done

# Deactivate the Python virtual environment
deactivate