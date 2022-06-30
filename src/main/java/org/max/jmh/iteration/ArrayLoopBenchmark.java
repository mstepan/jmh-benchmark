package org.max.jmh.iteration;

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
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.TearDown;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * Benchmark                            Mode  Cnt     Score     Error  Units
 * ArrayLoopBenchmark.classicForLoop    avgt   10  2671.164 ± 172.315  ns/op
 * ArrayLoopBenchmark.foreach           avgt   10  2540.569 ± 177.170  ns/op
 * ArrayLoopBenchmark.optimizedForLoop  avgt   10  2461.539 ±  26.587  ns/op
 */
@State(Scope.Thread)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 10, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 10, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
public class ArrayLoopBenchmark {

    private final ThreadLocalRandom RAND = ThreadLocalRandom.current();

    private int[] arr1;

    private int[] arr2;

    private int[] arr3;

    @Setup
    public void setUp() {
        arr1 = generateRandomArray();
        arr2 = Arrays.copyOf(arr1, arr1.length);
        arr3 = Arrays.copyOf(arr1, arr1.length);
    }

    @TearDown
    public void tearDown() {
        arr1 = null;
        arr2 = null;
        arr3 = null;
    }

    private int[] generateRandomArray() {

        int[] arr = new int[10_000];

        for (int i = 0; i < arr.length; ++i) {
            arr[i] = RAND.nextInt();
        }

        return arr;
    }


    @Benchmark
    public void classicForLoop(Blackhole bh) {
        int sum = 0;
        for (int i = 0; i < arr1.length; ++i) {
            sum += arr1[i];
        }

        bh.consume(sum);
    }

    @Benchmark
    public void optimizedForLoop(Blackhole bh) {
        int sum = 0;
        for (int i = 0, length = arr2.length; i < length; ++i) {
            sum += arr2[i];
        }

        bh.consume(sum);
    }

    @Benchmark
    public void foreach(Blackhole bh) {
        int sum = 0;
        for (int val : arr3) {
            sum += val;
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
     *    $ mvn clean package
     *    $ java -jar target/benchmarks.jar ArrayLoopBenchmark
     */

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
            .include(ArrayLoopBenchmark.class.getSimpleName())
//            .threads(Runtime.getRuntime().availableProcessors())
//            .jvmArgs("-ea")
            .build();

        new Runner(opt).run();
    }

}
