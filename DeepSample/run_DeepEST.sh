#!/bin/bash

# Navigate to the dataset directory
cd /Users/aliasgari/Downloads/DeepSample-master/dataset

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

    java -Xmx51200m -cp "/Users/aliasgari/Downloads/DeepSample-master/DeepSample/source_code/bin:/Users/aliasgari/Downloads/DeepSample-master/libs/commons-lang3-3.12.0.jar:/Users/aliasgari/Downloads/DeepSample-master/libs/commons-math3-3.6.1.jar:/Users/aliasgari/Downloads/DeepSample-master/libs/weka.jar" main.DeepEST_classification /Users/aliasgari/Downloads/DeepSample-master/dataset/$dataset.csv confidence 0.7 $budget $dataset_size /Users/aliasgari/Downloads/DeepSample-master/Results/Classification/DeepEST/${dataset}.confidence >> log.txt
    rm log.txt
    java -Xmx51200m -cp "/Users/aliasgari/Downloads/DeepSample-master/DeepSample/source_code/bin:/Users/aliasgari/Downloads/DeepSample-master/libs/commons-lang3-3.12.0.jar:/Users/aliasgari/Downloads/DeepSample-master/libs/commons-math3-3.6.1.jar:/Users/aliasgari/Downloads/DeepSample-master/libs/weka.jar" main.DeepEST_classification /Users/aliasgari/Downloads/DeepSample-master/dataset/$dataset.csv lsa $threshold_lsa $budget $dataset_size /Users/aliasgari/Downloads/DeepSample-master/Results/Classification/DeepEST/${dataset}.lsa >> log.txt
    rm log.txt
    java -Xmx51200m -cp "/Users/aliasgari/Downloads/DeepSample-master/DeepSample/source_code/bin:/Users/aliasgari/Downloads/DeepSample-master/libs/commons-lang3-3.12.0.jar:/Users/aliasgari/Downloads/DeepSample-master/libs/commons-math3-3.6.1.jar:/Users/aliasgari/Downloads/DeepSample-master/libs/weka.jar" main.DeepEST_classification /Users/aliasgari/Downloads/DeepSample-master/dataset/$dataset.csv dsa $threshold_dsa $budget $dataset_size /Users/aliasgari/Downloads/DeepSample-master/Results/Classification/DeepEST/${dataset}.dsa >> log.txt
    rm log.txt
done