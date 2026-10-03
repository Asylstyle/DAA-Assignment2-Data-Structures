# DAA Assignment 2: Data Structures

Assylzhan Amangeldi  
SE-2526

Repository:
https://github.com/Asylstyle/DAA-Assignment2-Data-Structures

## Requirements

- JDK 17 or newer
- Maven
- Python 3 with matplotlib for generating plots

## Build and run tests

From the project root:

```shell
mvn clean verify
java -cp target/classes Benchmark
$ErrorActionPreference = "Stop"

git switch feature/metrics
if ($LASTEXITCODE -ne 0) { throw "Cannot switch branch." }

@'
# DAA Assignment 2: Data Structures

Author: Assylzhan Amangeldi
Group: SE-2526

Repository:
https://github.com/Asylstyle/DAA-Assignment2-Data-Structures

## Requirements

- JDK 17 or newer
- Maven
- Python 3 and matplotlib for plots

## Build and test

Run from the project root:

    mvn clean verify

Tests cover random operations, empty structures, duplicates, invalid
indices, resizing, tail updates, heap property and operation counters.
Java reference collections are used only in tests.

## Benchmark

After building, run:

    java -cp target/classes Benchmark

Output: results/results.csv, containing 36 rows.
Sizes: 100, 1000, 10000 and 100000.
Each case uses two discarded warm-ups and five measured runs.
Reported time is the median. Data uses Random(42).
Running the benchmark replaces the CSV.

W1: 10000 random get calls.
W2: 1000 contains queries, half present and half absent.
W3: 1000 insertions and removals, at head or original n/2.
W4: n heap insertions and n extractions.

## Plots

Install the plotting dependency and generate figures:

    python -m pip install matplotlib
    python plot_results.py

Four PNG charts are saved in results/plots.
Each chart shows time, steps, moves and comparisons.
W3 includes both head and middle variants.

## Layout

- src/main/java: structures, interface, metrics and benchmark
- src/test/java: JUnit 5 tests
- results/results.csv: measurements
- results/plots: workload charts
- plot_results.py: plot generator
- REPORT.md: analysis, proofs and discussion

## Structures and counters

DynamicArray doubles its int array when full.
MyLinkedList is singly linked with head and tail.
MinHeap uses an int array with bubble-up and bubble-down.

Steps count array-cell reads or next-node traversals.
Moves count element relocations or structural reference updates.
Comparisons count element-value comparisons.
Head and tail updates count as structural reference updates.
Fresh array insertion is not a relocation.
Loop conditions and index checks are not element comparisons.
Diagnostic heap checks do not modify counters.

## Timing boundaries

W1-W3 exclude initial filling and query generation.
W4 includes insertion, extraction and writing output values.
Validation occurs after timing and saving counters.
Instrumentation adds overhead to measured operations.

## Optional functionality

Floyd buildHeap is implemented and tested.
A separate buildHeap comparison and JOL measurements are not included.
Complete bonus experiments are not claimed.

## Git workflow

Branches: main, feature/array, feature/list, feature/heap,
feature/metrics.

Changes are integrated using fast-forward merges.
Release tag: v1.0.
