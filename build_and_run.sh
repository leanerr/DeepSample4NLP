#!/bin/bash

# Navigate to the source_code directory
cd DeepSample/source_code

# Activate the Python virtual environment
echo "Activating Python virtual environment..."
source ../../dataset/env/bin/activate

# Compile Java source files
echo "Compiling Java source files..."
javac -d bin -cp "../../libs/commons-lang3-3.12.0.jar:../../libs/commons-math3-3.6.1.jar:../../libs/weka.jar:." $(find main utility selector/classification -name "*.java")

# Check for successful compilation
if [ $? -ne 0 ]; then
    echo "Compilation failed. Please check the errors above."
    exit 1
fi

# Navigate back to the project root
echo "Returning to project root..."
cd ../..

# Run the DeepEST script
echo "Running the DeepEST script..."
./DeepSample/run_DeepEST.sh

# Check for successful execution
if [ $? -ne 0 ]; then
    echo "Execution of run_DeepEST.sh failed. Please check the errors above."
    exit 1
fi

echo "Build and execution completed successfully."