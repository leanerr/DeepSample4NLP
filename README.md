# DeepSample 

This repository provides the artifacts for DeepSample execution. 
The experiments on 9 DNN for classification and 2 DNN for regression can be replicated by running the script 'run_DeepSample.sh'.
Once the experiments are completed, all the results will be generated in the 'Results' folder.

## DeepSample folder
The folder "DeepSample" contains all the ".jar" files and the source code.
The source code is organized as follows:
- The "main" folder contains all the java files for the jar generation.
- "selector.classification" contains the source code of the implemented sampling algorithms for classification, plus DeepEST.
- "selector.regression" contains the source code of the implemented sampling algorithms for regression, plus DeepEST.
- "utility" and "utility.regression" contain utilities and data structures useful for the selectors.

The code has been developed with Eclipse.

## Other samplers
CES and SRS implementations are available at: 'https://github.com/Lizenan1995/DNNOpAcc'.
The results can be replicated by:
1. cloning the repository;
2. importing the models in 'CE method/MNIST/normal';
3. importing and running in 'CE method/MNIST/normal' the files available in the 'CES_SRS' folder of our repository ('crossentropy*.py' files). 

In the following, we reported the chosen layers (LAY parameter in 'crossentropy*.py') required to run CES for each model: 
- A: -2 
- B: -4
- C: -2
- D: -4
- E: -4
- F: -2
- G: -2
- H: -4
- I: -4
- Dave_orig: -3
- Dave_dropout: -3

## Neural Networks availability
The trained models are included in the 'models' folder. Model G, Dave_orig and Dave_dropout models are available at 'https://file.io/0405r74sgwqV'.
The folder 'dataset' contains the dataset with the predictions and the auxiliary variables for all models.
The source code for the classification models is available at: 'https://github.com/ICOS-OAA/ICOS.git'

## Paper results
The folder 'Results_paper' contains the results reported in the paper.

A Python notebook is provided for the practitioners to query the results, for instance to ask for the best performing technique (based on their occurrences in the top-3 rankings) given a main objective (low RMSE, RMedSE or large failure exposure ability) and a configuration as input (e.g., small sample size, MNIST dataset, confidence as auxiliary variable) ([notebook](./Results_paper/_Discussion/interactive_notebook/summary.ipynb))

## Requirements and Dependencies
The provided code requires Java 8.
The "libs" folder contains all the libraries required to run the experiments.







---

# DeepEST Project  

This project is designed for running a deep sampling technique on datasets, using various auxiliary variables. The project is structured with Java for computational tasks and Python for threshold computations.

## Prerequisites

Before you begin, ensure you have the following installed:

- Java Development Kit (JDK) 11 or later
- Python 3.x
- Python virtual environment (`venv`)

## Directory Structure

```plaintext
DeepSample-master/
│
├── dataset/
│   ├── threshold_lsa.py
│   ├── threshold_dsa.py
│   ├── imdb300AuxDS.csv
│   ├── SSTIMDB3000AuxDS.csv
│   ├── SSTtestAuxDS.csv
│   ├── imdbAuxDS.csv
│   └── env/                # Python virtual environment
│
├── DeepSample/
│   ├── source_code/
│   │   ├── bin/
│   │   ├── main/
│   │   ├── selector/
│   │   └── utility/
│   ├── build_and_run.sh
│   └── run_DeepEST.sh
│
├── libs/
│   ├── commons-lang3-3.12.0.jar
│   ├── commons-math3-3.6.1.jar
│   └── weka.jar
│
└── Results/
    └── Classification/
        └── DeepEST/
```

## Setup Instructions

Follow these steps to set up and run the project:

### 1. Clone the Repository

Clone this repository to your local machine:

```bash
git clone https://github.com/leanerr/DeepSample.git
cd DeepSample-master
```

### 2. Set Up the Python Environment

Navigate to the `dataset` directory and set up the Python environment:

```bash
cd dataset
python3 -m venv env
source env/bin/activate  # On Windows, use `env\Scripts\activate`
pip install -r requirements.txt  # Install required Python packages
```

### 3. Build the Java Project

Navigate to the `DeepSample` directory and run the build script:

```bash
cd ../DeepSample
./build_and_run.sh
```

This script will compile the Java source code located in `source_code` and generate class files in the `bin` directory.

### 4. Run the DeepEST Script

Execute the DeepEST script to process datasets using different auxiliary variables:

```bash
./run_DeepEST.sh
```

### 5. Check the Results

After the script execution, results will be available in the `Results/Classification/DeepEST` directory, with separate CSV files for each dataset and auxiliary variable.

## Modifying the Auxiliary Variables

To modify the auxiliary variables or thresholds used in the computations, edit the `run_DeepEST.sh` script accordingly:

```bash
auxiliary_variables=("confidence" "entropy" "similarity" "dsa" "lsa")
```

Adjust the thresholds within the script as needed.

## Troubleshooting

- Ensure all necessary dependencies are installed and that your `JAVA_HOME` is correctly set.
- Check file paths and permissions if you encounter any issues during script execution.



