package org.max.jmh.collection;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
 * Normal collections vs fully synchronized collections benchmark.
 *
 * Benchmark                                            Mode  Cnt   Score   Error  Units
 * SynchronizedVsNormalCollectionsBenchmark.normalList  avgt    5  11.047 ± 0.474  ns/op
 * SynchronizedVsNormalCollectionsBenchmark.synchList   avgt    5  52.717 ± 6.022  ns/op
 */
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
public class SynchronizedVsNormalCollectionsBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        final List<Integer> list = new ArrayList<>();
        final List<Integer> synchList = Collections.synchronizedList(new ArrayList<>());
    }

    @Benchmark
    public void normalList(BenchmarkState state, Blackhole bh) {
        state.list.add(133);
        state.list.remove(0);
        bh.consume(state.list.size());
    }

    @Benchmark
    public void synchList(BenchmarkState state, Blackhole bh) {
        state.synchList.add(133);
        state.synchList.remove(0);
        bh.consume(state.synchList.size());
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
     *    $ java -jar target/benchmarks.jar SynchronizedVsNormalCollectionsBenchmark
     */

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
            .include(SynchronizedVsNormalCollectionsBenchmark.class.getSimpleName())
//            .threads(Runtime.getRuntime().availableProcessors())
//            .jvmArgs("-ea")
            .build();

        new Runner(opt).run();
    }

}
