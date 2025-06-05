package org.max.jmh.cpu;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/*
Measure branch mis-prediction effect on performance.

Benchmark                              Mode  Cnt        Score       Error  Units
BranchPredictionBenchmark.randomArray  avgt    5  3165683.886 � 58231.982  ns/op
BranchPredictionBenchmark.sortedArray  avgt    5   387693.393 �  7344.369  ns/op

*/
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
public class BranchPredictionBenchmark {

    @State(Scope.Thread)
    public static class ThreadState {
        private final int[] randomArray = generateRandomArray();
        private final int[] sortedArray = copyAndSort(randomArray);

        private int[] generateRandomArray() {
            ThreadLocalRandom rand = ThreadLocalRandom.current();

            int[] arr = new int[1_000_000];

            for (int i = 0; i < arr.length; ++i) {
                arr[i] = rand.nextInt();
            }

            return arr;
        }

        private int[] copyAndSort(int[] arr) {
            int[] res = Arrays.copyOf(arr, arr.length);
            Arrays.sort(res);
            return res;
        }
    }

    @Benchmark
    public void randomArray(ThreadState threadState, Blackhole bh) {
        int totalSum = 0;
        int sumBelow128 = 0;

        for (int i = 0; i < threadState.randomArray.length; ++i) {
            if (threadState.randomArray[i] < 128) {
                sumBelow128 += threadState.randomArray[i];
            }
            totalSum += threadState.randomArray[i];
        }

        bh.consume(totalSum);
        bh.consume(sumBelow128);
    }

    @Benchmark
    public void sortedArray(ThreadState threadState, Blackhole bh) {
        int totalSum = 0;
        int sumBelow128 = 0;

        for (int i = 0; i < threadState.sortedArray.length; ++i) {
            if (threadState.sortedArray[i] < 128) {
                sumBelow128 += threadState.sortedArray[i];
            }
            totalSum += threadState.sortedArray[i];
        }

        bh.consume(totalSum);
        bh.consume(sumBelow128);
    }

    /*
     * ============================== HOW TO RUN THIS BENCHMARK ====================================
     *
     * To run benchmark and see the results do the following:
     *    $ ./mvnw clean package
     *    $ java -jar target/benchmarks.jar BranchPredictionBenchmark
     */

    public static void main(String[] args) throws RunnerException {
        Options opt =
                new OptionsBuilder()
                        .include(BranchPredictionBenchmark.class.getSimpleName())
                        //            .threads(Runtime.getRuntime().availableProcessors())
                        //            .jvmArgs("-ea")
                        .jvmArgs("-ea", "-Xms2G", "-Xmx2G")
                        .build();

        new Runner(opt).run();
    }
}
