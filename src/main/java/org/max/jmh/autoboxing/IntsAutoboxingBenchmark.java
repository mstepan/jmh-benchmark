package org.max.jmh.autoboxing;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Benchmark Integer autoboxing effect.
 *
 * Benchmark                                 Mode  Cnt      Score      Error  Units
 * IntsAutoboxingBenchmark.sumPrimitiveInts  avgt    5  42163.093 ± 1350.278  ns/op
 * IntsAutoboxingBenchmark.sumWrapperInts    avgt    5  42099.845 ± 1384.201  ns/op
 */
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
public class IntsAutoboxingBenchmark {

    @Benchmark
    public void sumPrimitiveInts(Blackhole bh) {
        int sum = 0;
        for (int i = 0; i < 1000_000; ++i) {
            sum = sum + i;
        }

        bh.consume(sum);
    }

    @Benchmark
    public void sumWrapperInts(Blackhole bh) {
        Integer sum = 0;
        for (Integer i = 0; i < 1000_000; ++i) {
            sum = sum + i;
        }

        bh.consume(sum);
    }

    /*
     * ============================== HOW TO RUN THIS BENCHMARK ====================================
     *
     * To run benchmark and see the results do the following:
     *    $ ./mvnw clean package
     *    $ java -jar target/benchmarks.jar IntsAutoboxingBenchmark
     *
     * ============================== HOW TO RUN THIS BENCHMARK ====================================
     */

    static void main() throws RunnerException {
        Options opt =
                new OptionsBuilder()
                        .include(IntsAutoboxingBenchmark.class.getSimpleName())
                        //            .threads(Runtime.getRuntime().availableProcessors())
                        .jvmArgs("-Xms1G", "-Xmx1G")
                        .build();

        new Runner(opt).run();
    }
}
