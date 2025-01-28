# Java Microbenchmark Harness playground

## Pre-requisites
* Install `Java 23`
* Use maven wrapper (was installed with `mvn wrapper:wrapper -Dmaven=3.8.6`)
* Install python 3.13.* (check `.python-version` file)
* Create python virtual environment `python -m venv .venv`

## Execute 2 benchmarks and compare results
We will use `org.max.jmh.ExampleBenchmark` as our main benchmark class:

1. Build maven project using `./mvnw clean package`
2. Run benchmark and save result in JSON format `java -jar target/benchmarks.jar ExampleBenchmark -rf json -rff jmh-result-1.json`
3. Modify benchmarked code and run 2nd experiment `java -jar target/benchmarks.jar ExampleBenchmark -rf json -rff jmh-result-2.json`
