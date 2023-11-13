# Project: Shard Assigner

This is a script to do shard allocation. Shard allocation can be seen as a modified bin-packing problem. 
We want to distribute m items (shards) across n bins (nodes) so as to minimize load on the most loaded bin.

## Table of Contents

- [Installation](#installation)
- [Usage](#usage)
- [Questions](#questions)


## Installation

The source code is written in Java. You will need Java8 and mvn to run the script.

## Usage

Build
```
make

```
Run
```
make run

```
Clean
```
make run

```
You can pass args in Makefile.

The output will be written in `output`.

`output/assignments.json`: the results of all assignments

`output/primary_assignments.json`: the results of primary shard assignments

`output/replica_assignments.json`: the results of replica shard assignments

`output/unassigned_shards.json`: the results of unassigned shards

**Args**

`-s`: the file path to store  info of unassigned shards 

`-n`: the file path to store info of nodes

`-r`: the number of replica of the primary shard

**Data**

`/src/main/resources`: the input data

`/output`: the output results

**Source Code**

`/src/main/java/assigner`: the folder where source code are stored

**Tests**

`/src/test`: the folder where the tests are stored

 