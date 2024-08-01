#!/bin/bash

# Determine the project root directory dynamically based on the script's location
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"

# Navigate to the source_code directory
cd "$PROJECT_ROOT/DeepSample/source_code" || exit

# Activate the Python virtual environment
echo "Activating Python virtual environment..."
source "$PROJECT_ROOT/dataset/env/bin/activate"

# Compile Java source files
echo "Compiling Java source files..."
javac -d bin -cp "$PROJECT_ROOT/libs/commons-lang3-3.12.0.jar:$PROJECT_ROOT/libs/commons-math3-3.6.1.jar:$PROJECT_ROOT/libs/weka.jar:." $(find main utility selector/classification -name "*.java")

# Check for successful compilation
if [ $? -ne 0 ]; then
    echo "Compilation failed. Please check the errors above."
    exit 1
fi

# Navigate back to the project root
echo "Returning to project root..."
cd "$PROJECT_ROOT" || exit

# Run the DeepEST script
echo "Running the DeepEST script..."
./DeepSample/run_DeepEST.sh

# Check for successful execution
if [ $? -ne 0 ]; then
    echo "Execution of run_DeepEST.sh failed. Please check the errors above."
    exit 1
fi

echo "Build and execution completed successfully."