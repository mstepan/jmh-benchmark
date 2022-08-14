package org.max.jmh;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
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

/**
 * More detailed JMH examples can be found here:
 * https://github.com/openjdk/jmh/tree/master/jmh-samples/src/main/java/org/openjdk/jmh/samples
 */
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
public class ExampleBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        final double PI = Math.PI;
    }

    @State(Scope.Thread)
    public static class ThreadState {
        final ThreadLocalRandom rand = ThreadLocalRandom.current();
        private int[] arr1 = generateRandomArray();
        private int[] arr2 = Arrays.copyOf(arr1, arr1.length);

        private int[] generateRandomArray() {

            int[] arr = new int[10_000];

            for (int i = 0; i < arr.length; ++i) {
                arr[i] = rand.nextInt();
            }

            return arr;
        }

    }


    @Benchmark
    public void classicForLoop(BenchmarkState benchmarkState, ThreadState threadState, Blackhole bh) {
        int sum = 0;
        for (int i = 0; i < threadState.arr1.length; ++i) {
            sum += (threadState.arr1[i] * benchmarkState.PI);
        }

        bh.consume(sum);
    }

    @Benchmark
    public void foreach(BenchmarkState benchmarkState, ThreadState threadState, Blackhole bh) {
        int sum = 0;
        for (int val : threadState.arr2) {
            sum += (val * benchmarkState.PI);
        }

        bh.consume(sum);
    }

    /*
     * ============================== HOW TO RUN THIS TEST: ====================================
     *
     * You can see the benchmark runs as usual.
     *
     * You can run this test:
     *
     * a) Via the command line:
     *    $ ./mvnw clean package
     *    $ java -jar target/benchmarks.jar ExampleBenchmark
     */

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
            .include(ExampleBenchmark.class.getSimpleName())
//            .threads(Runtime.getRuntime().availableProcessors())
//            .jvmArgs("-ea")
            .build();

        new Runner(opt).run();
    }

}
