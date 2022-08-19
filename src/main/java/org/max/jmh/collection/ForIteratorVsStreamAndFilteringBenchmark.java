package org.max.jmh.collection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
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
 *
 * Benchmark for loop iteration with if-else as filtering VS real stream with filter predicate operations.
 *
 * Benchmark                                                     Mode  Cnt    Score    Error  Units
 * ForIteratorVsStreamAndFilteringBenchmark.classicForLoop       avgt    5  103.620 ± 11.563  ns/op
 * ForIteratorVsStreamAndFilteringBenchmark.streamWithFiltering  avgt    5  129.280 ± 11.511  ns/op
 *
 */
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
public class ForIteratorVsStreamAndFilteringBenchmark {


    @State(Scope.Benchmark)
    public static class BenchmarkState {
        final double PI = Math.PI;
    }

    @State(Scope.Thread)
    public static class ThreadState {

        private Map<String, String> map1 = createRandomMap(10);
        private Map<String, String> map2 = new HashMap<>(map1);

        private final Set<String> whitelist = createSubset(map1, 5);

        private Set<String> createSubset(Map<String, String> baseMap, int size) {
            Set<String> res = new HashSet<>();

            int index = 0;
            for (String key : baseMap.keySet()) {
                if (index >= size) {
                    break;
                }
                res.add(key);
            }

            return res;
        }

        private Map<String, String> createRandomMap(int size) {

            final ThreadLocalRandom rand = ThreadLocalRandom.current();

            Map<String, String> res = new HashMap<>();
            for (int i = 0; i < size; ++i) {
                int randVal = rand.nextInt();
                res.put("key-" + randVal, "val-" + randVal);
            }
            return res;
        }
    }

    @Benchmark
    public void classicForLoop(ThreadState threadState, Blackhole bh) {

        final Map<String, String> curMap = threadState.map1;
        final Set<String> whitelist = threadState.whitelist;

        for (Map.Entry<String, String> entry : curMap.entrySet()) {
            if (whitelist.contains(entry.getKey())) {
                bh.consume(entry.getKey());
            }
        }
    }

    @Benchmark
    public void streamWithFiltering(ThreadState threadState, Blackhole bh) {

        final Map<String, String> curMap = threadState.map2;
        final Set<String> whitelist = threadState.whitelist;

        curMap.entrySet().stream().
            filter(entry -> whitelist.contains(entry.getKey())).
            forEach(bh::consume);
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
     *    $ java -jar target/benchmarks.jar ForIteratorVsStreamAndFilteringBenchmark
     */

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
            .include(ForIteratorVsStreamAndFilteringBenchmark.class.getSimpleName())
//            .threads(Runtime.getRuntime().availableProcessors())
//            .jvmArgs("-ea")
            .build();

        new Runner(opt).run();
    }

}
