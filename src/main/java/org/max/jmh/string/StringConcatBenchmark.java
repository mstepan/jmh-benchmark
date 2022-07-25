package org.max.jmh.string;

import java.util.Random;
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
 * Benchmark                                                     Mode  Cnt    Score    Error  Units
 * StringConcatBenchmark.stringBuilderConcat                     avgt    5  107.174 ± 11.181  ns/op
 * StringConcatBenchmark.stringBuilderWithInitialCapacityConcat  avgt    5   76.207 ±  7.390  ns/op
 * StringConcatBenchmark.stringConcat                            avgt    5   49.878 ±  6.855  ns/op
 */
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
public class StringConcatBenchmark {

    private static final Random RAND = new Random();

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        final int first = RAND.nextInt();
        final int second = RAND.nextInt();
        final int third = RAND.nextInt();
    }

    @Benchmark
    public void stringConcat(BenchmarkState state, Blackhole bh) {
        // below code will be compiled into:
        // invokedynamic 'StringConcatFactory.makeConcatWithConstants:(III)Ljava/lang/String;'
        String res = "hello" + state.first + ", " + state.second + ", " + state.third + "!!!";
        bh.consume(res);
    }

    @Benchmark
    public void stringBuilderConcat(BenchmarkState state, Blackhole bh) {
        StringBuilder buf = new StringBuilder();

        buf.append("hello").
            append(state.first).append(", ").
            append(state.second).append(", ").
            append(state.third).append("!!!");

        bh.consume(buf.toString());
    }

    @Benchmark
    public void stringBuilderWithInitialCapacityConcat(BenchmarkState state, Blackhole bh) {
        StringBuilder buf = new StringBuilder(64);

        buf.append("hello").
            append(state.first).append(", ").
            append(state.second).append(", ").
            append(state.third).append("!!!");

        bh.consume(buf.toString());
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
     *    $ java -jar target/benchmarks.jar StringConcatBenchmark
     */

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
            .include(StringConcatBenchmark.class.getSimpleName())
//            .threads(Runtime.getRuntime().availableProcessors())
//            .jvmArgs("-ea")
            .build();

        new Runner(opt).run();
    }

}
