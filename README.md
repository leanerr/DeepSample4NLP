

# DeepSample4NLP Projects

DeepSample is a comprehensive sampling tool designed for performing advanced sampling techniques on datasets to facilitate enhanced analysis and processing. The project leverages Java for computational efficiency and Python for preprocessing and threshold calculations. It includes two main components: **DeepEST** and **SUPS**.

## Table of Contents

- [Project Architecture](#project-architecture)
- [Cloning the Repository](#cloning-the-repository)
- [Setting Up and Running the Project](#setting-up-and-running-the-project)
- [Classification Methods](#classification-methods)
- [Directory Structure](#directory-structure)
- [Results](#results)

## Project Architecture

The DeepSample project is structured into distinct components that manage different aspects of the workflow:

1. **DeepEST Component**:
   - **Python Scripts**: Calculate thresholds for various auxiliary variables such as Local Sensitivity Analysis (LSA) and Deep Sensitivity Analysis (DSA).
   - **Java Processing**: Implements the DeepEST sampling algorithm using efficient Java classes.

2. **SUPS Component**:
   - **Java Logic**: Implements the SUPS sampling strategy, applying sophisticated sampling techniques to datasets using auxiliary variables like confidence and entropy.
   - **Shell Scripts**: Automate the setup, build, and execution processes for SUPS sampling.

3. **Integration**:
   - **Shell Scripts**: Facilitate the seamless integration of Python preprocessing and Java execution, automating the workflow from start to finish.

## Cloning the Repository

To get started with the DeepSample project, clone the repository from GitHub:

```bash
git clone https://github.com/leanerr/DeepSample4NLP.git
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

### 2. Build and Run the DeepSample Part 1 for Classification Methods

Navigate back to the project root and execute the build and run script for DeepSample Part 1:

```bash
cd ..
./build_and_run_DeepSamplePart1_Class.sh
```

This script will compile the Java source code and run the classification sampling algorithms on the datasets, specifically targeting Part 1 classification methods.

### 3. Build and Run the DeepEST Component

Navigate back to the project root and execute the build and run script for DeepEST:

```bash
cd ..
./build_and_run.sh
```

This script will compile the Java source code and run the DeepEST sampling algorithm on the datasets.

### 4. Build and Run the SUPS Component

Ensure you are in the project root and run the build and run script for SUPS:

```bash
./build_and_run_SUPS.sh
```

This script compiles the Java source code and executes the SUPS sampling process, storing results and logs appropriately.

## Classification Methods

DeepSample Part 1 supports the following classification methods for testing and analysis:

- **G2-UPS**: A two-stage sampling method that enhances accuracy by combining various sampling techniques.
- **DeepEST**: Implements advanced sampling using auxiliary variables to achieve precise sampling outcomes.
- **GBS (Greedy Bayesian Sampling)**: Utilizes Bayesian methods for efficient sampling, optimizing based on model uncertainty.
- **RHC-S (Randomized Hill Climbing with Sampling)**: Uses randomized hill climbing strategies to explore the solution space efficiently.
- **SSRS (Stratified Sampling with Random Sampling)**: Combines stratified sampling techniques with random sampling for better representation.
- **SUPS (Sampling Using Prediction Sensitivity)**: Applies sampling strategies based on prediction sensitivity to improve failure detection.

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
│   
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
├── build_and_run.sh               # Script to build and run the DeepEST project
└── build_and_run_SUPS.sh          # Script to build and run the SUPS project
└── build_and_run_DeepSamplePart1_Class.sh # Script for classification methods
```

## Results

After running the project, the results are stored in the `Results/Classification/` directory. Each dataset is processed with different auxiliary variables, and the results are saved in separate CSV files named according to the dataset and variable, such as:

- **DeepEST Results**: Stored in `Results/Classification/DeepEST/`.
- **SUPS Results**: Stored in `Results/Classification/SUPS/`.

These results can be used for further analysis and validation of the sampling strategies applied.


Certainly! Here's the content to include a section about the model and datasets used in the DeepSample project:

---

## Model

### DistilBERT for Sentiment Analysis

The DeepSample project utilizes the DistilBERT model for performing sentiment analysis, specifically focusing on binary classification tasks. DistilBERT is a distilled version of BERT (Bidirectional Encoder Representations from Transformers), designed to be lighter and faster while maintaining a high level of accuracy.

- **Model Name**: [distilbert/distilbert-base-uncased-finetuned-sst-2-english](https://huggingface.co/distilbert/distilbert-base-uncased-finetuned-sst-2-english)
- **Task**: Sentiment Analysis (Binary Classification)
- **Description**: This model is fine-tuned on the SST-2 dataset, making it well-suited for sentiment classification tasks where the goal is to determine the sentiment (positive or negative) of a given text input.

## Datasets

The DeepSample project employs various datasets for training and evaluating the performance of the sentiment analysis model. These datasets encompass a variety of text samples, ranging from movie reviews to balanced datasets, combining different sources for comprehensive analysis.

1. **SST-2 Dataset**:
   - **Source**: [SST-2 Sentiment Analysis Dataset](https://github.com/YJiangcm/SST-2-sentiment-analysis/tree/master/data)
   - **Description**: The Stanford Sentiment Treebank (SST-2) is a well-known benchmark dataset for sentiment analysis. It includes a collection of movie reviews with binary sentiment annotations (positive or negative).
   - **Splits**: Train, Dev, Test

2. **IMDB Dataset**:
   - **Source**: [IMDB Movie Reviews Dataset](https://www.kaggle.com/datasets/lakshmi25npathi/imdb-dataset-of-50k-movie-reviews)
   - **Description**: This dataset consists of 50,000 movie reviews from IMDB, labeled as positive or negative. It is widely used for sentiment analysis tasks due to its large size and rich content.

3. **IMDB3000 Dataset**:
   - **Source**: [IMDB3000 Dataset](https://huggingface.co/datasets/enoreyes/imdb_3000_sphere)
   - **Description**: A smaller, curated version of the IMDB dataset, the IMDB3000 dataset provides a balanced subset of 3,000 reviews, designed to test model performance on a more concise dataset.

4. **Balanced Dataset (SST + IMDB3000)**:
   - **Description**: This dataset combines the SST-2 dataset and the IMDB3000 dataset to create a balanced collection of sentiment annotations. By merging these datasets, the project ensures a diverse range of sentiment expressions, allowing for robust testing and validation of the model.

---



