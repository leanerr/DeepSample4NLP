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
Here's a concise `README.md` for your project, including instructions for cloning, running the project, and an overview of the project architecture:

---


# DeepSample Project

DeepSample is a comprehensive sampling tool designed for performing advanced sampling techniques on datasets to facilitate enhanced analysis and processing. The project leverages Java for computational efficiency and Python for preprocessing and threshold calculations. It includes two main components: **DeepEST** and **SUPS**.

## Table of Contents

- [Project Architecture](#project-architecture)
- [Cloning the Repository](#cloning-the-repository)
- [Setting Up and Running the Project](#setting-up-and-running-the-project)
- [Directory Structure](#directory-structure)
- [Results](#results)

## Project Architecture

The DeepSample project consists of two main components, each handling a specific aspect of the workflow:

1. **DeepEST Component**:
   - **Python Scripts**: Calculate thresholds for various auxiliary variables such as LSA and DSA.
   - **Java Processing**: Implements the DeepEST sampling algorithm using efficient Java classes.

2. **SUPS Component**:
   - **Java Logic**: Implements the SUPS sampling strategy, which applies sophisticated sampling techniques to datasets using auxiliary variables like confidence and entropy.
   - **Shell Scripts**: Automate the setup, build, and execution processes for SUPS sampling.

3. **Integration**:
   - **Shell Scripts**: Facilitate the seamless integration of Python preprocessing and Java execution, automating the workflow from start to finish.

## Cloning the Repository

To get started with the DeepSample project, clone the repository from GitHub:

```bash
git clone https://github.com/leanerr/DeepSample.git
cd DeepSample-master
```

This command will download the project files to your local machine and navigate you into the project directory.

## Setting Up and Running the Project

Follow these steps to set up and execute the project:

### 1. Create and Activate a Python Virtual Environment

Navigate to the `dataset` directory and set up the Python environment:

```bash
cd dataset
python3 -m venv env
source env/bin/activate  # On Windows, use `env\Scripts\activate`
pip install pandas
```

> This sets up a virtual environment and installs the necessary Python packages.

### 2. Build and Run the DeepEST Component

Navigate back to the project root and execute the build and run script for DeepEST:

```bash
cd ..
./build_and_run.sh
```

This script will compile the Java source code and run the DeepEST sampling algorithm on the datasets.

### 3. Build and Run the SUPS Component

Ensure you are in the project root and run the build and run script for SUPS:

```bash
./build_and_run_SUPS.sh
```

This script compiles the Java source code and executes the SUPS sampling process, storing results and logs appropriately.

## Directory Structure

The project is organized as follows:

```plaintext
DeepSample-master/
│
├── dataset/
│   ├── env/                       # Python virtual environment
│   ├── threshold_lsa.py           # Python script to calculate LSA threshold
│   ├── threshold_dsa.py           # Python script to calculate DSA threshold
│   ├── *.csv                      # Dataset files
│
├── DeepSample/
│   ├── source_code/
│   │   ├── bin/                   # Compiled Java class files
│   │   ├── main/                  # Main Java source files
│   │   ├── selector/              # Selector algorithm implementations
│   │   └── utility/               # Utility classes and methods
│   ├── SUPS_class.jar             # Compiled JAR for SUPS execution
│   ├── run_DeepEST.sh             # Script to run the DeepEST process
│   └── run_SUPS.sh                # Script to run the SUPS process
├── libs/                          # Java libraries
│   ├── commons-lang3-3.12.0.jar
│   ├── commons-math3-3.6.1.jar
│   └── weka.jar
│
├── Results/
│   └── Classification/
│       ├── DeepEST/               # Output results of the DeepEST sampling process
│       └── SUPS/                  # Output results of the SUPS sampling process
│
├── logs/                          # Directory for log files
│   └── sups_log_*.txt             # Logs for SUPS execution
│
├── build_and_run_DeepEST.sh       # Script to build and run the DeepEST project
└── build_and_run_SUPS.sh          # Script to build and run the SUPS project
```

## Results

After running the project, the results are stored in the `Results/Classification/` directory. Each dataset is processed with different auxiliary variables, and the results are saved in separate CSV files named according to the dataset and variable, such as:

- **DeepEST Results**: Stored in `Results/Classification/DeepEST/`.
- **SUPS Results**: Stored in `Results/Classification/SUPS/`.

These results can be used for further analysis and validation of the sampling strategies applied.


