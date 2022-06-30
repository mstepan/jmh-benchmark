package org.max.jmh.numbers;

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
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * Benchmark ints vs longs multiplication.
 */
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
public class LongVsIntArithmeticBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        final ThreadLocalRandom rand = ThreadLocalRandom.current();
        final int int1 = rand.nextInt(1_000_000);
        final int int2 = rand.nextInt(1_000_000);

        final long long1 = rand.nextLong(1_000_000);
        final long long2 = rand.nextLong(1_000_000);
    }

    @Benchmark
    public int mulInts(BenchmarkState state) {
        return state.int1 * state.int2;
    }

    @Benchmark
    public long mulLongs(BenchmarkState state) {
        return state.long1 * state.long2;
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
     *    $ java -jar target/benchmarks.jar LongVsIntArithmeticBenchmark
     */

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
            .include(LongVsIntArithmeticBenchmark.class.getSimpleName())
//            .threads(Runtime.getRuntime().availableProcessors())
//            .jvmArgs("-ea")
            .build();

        new Runner(opt).run();
    }

}
