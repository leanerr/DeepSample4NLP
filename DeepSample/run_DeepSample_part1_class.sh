# Process each dataset
for dataset in imdb300AuxDS SSTIMDB3000AuxDS SSTtestAuxDS imdbAuxDS
do
    # Get the dataset size using the combined script
    dataset_size=$(python3 threshold_dsa.py $dataset | grep 'Dataset Size' | awk '{print $3}')
    
    for key in 1 2 3 4 5
    do
        java -jar DeepSample/DeepSample_part_1_class.jar $dataset $key $dataset_size >> log.txt
        rm log.txt
    done
done