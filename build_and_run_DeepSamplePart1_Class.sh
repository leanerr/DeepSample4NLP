#!/bin/bash

# Define the root directory of the project
PROJECT_ROOT="$(pwd)"

# Define the source and class directories
SRC_DIR="$PROJECT_ROOT/DeepSample/source_code"
BIN_DIR="$SRC_DIR/bin"

# Clean previous build artifacts
echo "Cleaning previous build artifacts..."
rm -rf "$BIN_DIR"
mkdir -p "$BIN_DIR"

# Compile Java source files
echo "Compiling Java source files..."
javac -d "$BIN_DIR" -cp "$PROJECT_ROOT/libs/commons-lang3-3.12.0.jar:$PROJECT_ROOT/libs/commons-math3-3.6.1.jar:$PROJECT_ROOT/libs/weka.jar" $(find "$SRC_DIR" -name "*.java")

# Check if compilation was successful
if [ $? -ne 0 ]; then
    echo "Compilation failed. Please check the errors above."
    exit 1
fi

# Create the JAR file
echo "Creating JAR file..."
cd "$BIN_DIR" || exit
jar cfm "$PROJECT_ROOT/DeepSample/DeepSample_part_1_class.jar" "$PROJECT_ROOT/manifest.txt" *

# Check if the JAR creation was successful
if [ $? -ne 0 ]; then
    echo "JAR creation failed. Please check the errors above."
    exit 1
fi

# Return to the project root
cd "$PROJECT_ROOT" || exit

# Run the DeepSample part 1 class with different datasets
echo "Running DeepSample Part 1 for datasets..."

# Define datasets and sizes
declare -A datasets
datasets=(
    ["imdb300AuxDS"]=2999
    ["SSTIMDB3000AuxDS"]=1160
    ["SSTtestAuxDS"]=1820
    ["imdbAuxDS"]=50000
)

# Iterate over datasets and keys
for dataset in "${!datasets[@]}"
do
    size=${datasets[$dataset]}
    for key in {0..4}
    do
        echo "Running for dataset: $dataset with key: $key and size: $size"
        java -cp "DeepSample/DeepSample_part_1_class.jar:libs/commons-lang3-3.12.0.jar:libs/commons-math3-3.6.1.jar:libs/weka.jar" main.Classification_main_jar $dataset $key $size

        # Check if execution was successful
        if [ $? -ne 0 ]; then
            echo "Execution failed for dataset: $dataset, key: $key"
        else
            echo "Execution successful for dataset: $dataset, key: $key"
        fi
    done
done

echo "All datasets processed."





java -cp "DeepSample/DeepSample_part_1_class.jar:libs/commons-lang3-3.12.0.jar:libs/commons-math3-3.6.1.jar:libs/weka.jar" main.Classification_main_jar imdb300AuxDS 0 2999
java -cp "DeepSample/DeepSample_part_1_class.jar:libs/commons-lang3-3.12.0.jar:libs/commons-math3-3.6.1.jar:libs/weka.jar" main.Classification_main_jar imdb300AuxDS 1 2999
java -cp "DeepSample/DeepSample_part_1_class.jar:libs/commons-lang3-3.12.0.jar:libs/commons-math3-3.6.1.jar:libs/weka.jar" main.Classification_main_jar imdb300AuxDS 2 2999
java -cp "DeepSample/DeepSample_part_1_class.jar:libs/commons-lang3-3.12.0.jar:libs/commons-math3-3.6.1.jar:libs/weka.jar" main.Classification_main_jar imdb300AuxDS 3 2999
java -cp "DeepSample/DeepSample_part_1_class.jar:libs/commons-lang3-3.12.0.jar:libs/commons-math3-3.6.1.jar:libs/weka.jar" main.Classification_main_jar imdb300AuxDS 4 2999

java -cp "DeepSample/DeepSample_part_1_class.jar:libs/commons-lang3-3.12.0.jar:libs/commons-math3-3.6.1.jar:libs/weka.jar" main.Classification_main_jar SSTIMDB3000AuxDS 0 1160
java -cp "DeepSample/DeepSample_part_1_class.jar:libs/commons-lang3-3.12.0.jar:libs/commons-math3-3.6.1.jar:libs/weka.jar" main.Classification_main_jar SSTIMDB3000AuxDS 1 1160
java -cp "DeepSample/DeepSample_part_1_class.jar:libs/commons-lang3-3.12.0.jar:libs/commons-math3-3.6.1.jar:libs/weka.jar" main.Classification_main_jar SSTIMDB3000AuxDS 2 1160
java -cp "DeepSample/DeepSample_part_1_class.jar:libs/commons-lang3-3.12.0.jar:libs/commons-math3-3.6.1.jar:libs/weka.jar" main.Classification_main_jar SSTIMDB3000AuxDS 3 1160
java -cp "DeepSample/DeepSample_part_1_class.jar:libs/commons-lang3-3.12.0.jar:libs/commons-math3-3.6.1.jar:libs/weka.jar" main.Classification_main_jar SSTIMDB3000AuxDS 4 1160

java -cp "DeepSample/DeepSample_part_1_class.jar:libs/commons-lang3-3.12.0.jar:libs/commons-math3-3.6.1.jar:libs/weka.jar" main.Classification_main_jar SSTtestAuxDS 0 1820
java -cp "DeepSample/DeepSample_part_1_class.jar:libs/commons-lang3-3.12.0.jar:libs/commons-math3-3.6.1.jar:libs/weka.jar" main.Classification_main_jar SSTtestAuxDS 1 1820
java -cp "DeepSample/DeepSample_part_1_class.jar:libs/commons-lang3-3.12.0.jar:libs/commons-math3-3.6.1.jar:libs/weka.jar" main.Classification_main_jar SSTtestAuxDS 2 1820
java -cp "DeepSample/DeepSample_part_1_class.jar:libs/commons-lang3-3.12.0.jar:libs/commons-math3-3.6.1.jar:libs/weka.jar" main.Classification_main_jar SSTtestAuxDS 3 1820
java -cp "DeepSample/DeepSample_part_1_class.jar:libs/commons-lang3-3.12.0.jar:libs/commons-math3-3.6.1.jar:libs/weka.jar" main.Classification_main_jar SSTtestAuxDS 4 1820

java -cp "DeepSample/DeepSample_part_1_class.jar:libs/commons-lang3-3.12.0.jar:libs/commons-math3-3.6.1.jar:libs/weka.jar" main.Classification_main_jar imdbAuxDS 0 50000
java -cp "DeepSample/DeepSample_part_1_class.jar:libs/commons-lang3-3.12.0.jar:libs/commons-math3-3.6.1.jar:libs/weka.jar" main.Classification_main_jar imdbAuxDS 1 50000
java -cp "DeepSample/DeepSample_part_1_class.jar:libs/commons-lang3-3.12.0.jar:libs/commons-math3-3.6.1.jar:libs/weka.jar" main.Classification_main_jar imdbAuxDS 2 50000
java -cp "DeepSample/DeepSample_part_1_class.jar:libs/commons-lang3-3.12.0.jar:libs/commons-math3-3.6.1.jar:libs/weka.jar" main.Classification_main_jar imdbAuxDS 3 50000
java -cp "DeepSample/DeepSample_part_1_class.jar:libs/commons-lang3-3.12.0.jar:libs/commons-math3-3.6.1.jar:libs/weka.jar" main.Classification_main_jar imdbAuxDS 4 50000