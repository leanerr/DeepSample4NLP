#!/bin/bash

# Set the classpath for external libraries
LIB_PATH="/Users/aliasgari/Downloads/DeepSample-master/libs"
CLASSPATH="$LIB_PATH/commons-lang3-3.12.0.jar:$LIB_PATH/commons-math3-3.6.1.jar:$LIB_PATH/weka.jar:."

# Directory paths
SRC_DIR="DeepSample/source_code"
BIN_DIR="$SRC_DIR/bin"
RESULTS_DIR="DeepSample/Results/Classification/SUPS"
MODEL="imdb300AuxDS"
AUX_VAR="confidence"
BUDGET=100
KEY=0
SIZE=2999

# Create necessary directories if they don't exist
mkdir -p $BIN_DIR
mkdir -p $RESULTS_DIR

# Compile Java source files
echo "Compiling Java files..."
javac -d $BIN_DIR -cp "$CLASSPATH" $(find $SRC_DIR/main $SRC_DIR/utility $SRC_DIR/selector/classification $SRC_DIR/selector/regression -name "*.java")

# Check if compilation was successful
if [ $? -ne 0 ]; then
    echo "Compilation failed. Exiting."
    exit 1
fi

# Create JAR file
echo "Creating JAR file..."
echo "Main-Class: main.SUPS_classification" > $SRC_DIR/manifest.txt
jar cfm SUPS_class.jar $SRC_DIR/manifest.txt -C $BIN_DIR .

# Check if JAR creation was successful
if [ $? -ne 0 ]; then
    echo "JAR creation failed. Exiting."
    exit 1
fi

# Move the JAR file to the DeepSample directory
mv SUPS_class.jar DeepSample/

# Navigate to the DeepSample directory
cd DeepSample || exit

# Run the existing run_SUPS.sh script
echo "Running the SUPS script..."
chmod +x run_SUPS.sh
./run_SUPS.sh

# Check if execution was successful
if [ $? -ne 0 ]; then
    echo "Execution failed. Check error_log.txt for details."
    exit 1
fi

echo "Execution completed successfully."