#!/bin/bash

# Install necessary Python packages
pip install torch numpy pandas

# Navigate to the DeepSamplePy directory
cd DeepSamplePy || echo "Failed to navigate to DeepSamplePy directory"

# Run the Python scripts in each directory

# 2UPSpy
echo "Running 2UPSpy.py..."
cd 2UPSpy || echo "Failed to navigate to 2UPSpy directory"
python3 2UPSpy.py
cd ..

# GBSpy
echo "Running GBS_V2.py..."
cd GBSpy || echo "Failed to navigate to GBSpy directory"
python3 GBS_V2.py
cd ..

# SSRSpy
echo "Running SRSpy.py..."
cd SSRSpy || echo "Failed to navigate to SSRSpy directory"
python3 SRSpy.py
cd ..

# SRSpy
echo "Running SRSpy.py..."
cd SRSpy || echo "Failed to navigate to SRSpy directory"
python3 SRSpy.py
cd ..

echo "All scripts have been executed."