package org.max.jmh.ffm;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.lang.foreign.Arena;
import java.lang.foreign.MemorySegment;
import java.lang.foreign.ValueLayout;
import java.util.concurrent.TimeUnit;

/*

Benchmark off heap allocation inside Confined area.

https://inside.java/2026/10/23/confined-pools/

==============================================================================================
java 25
==============================================================================================
Benchmark                                                 Mode  Cnt   Score   Error  Units
OffHeapAllocationInConfidentAreaBenchmark.allocateValues  avgt    5  30.568 ± 0.957  ns/op

==============================================================================================
java 28
==============================================================================================
Benchmark                                                 Mode  Cnt  Score   Error  Units
OffHeapAllocationInConfidentAreaBenchmark.allocateValues  avgt    5  1.520 ± 0.050  ns/op
 */
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
public class OffHeapAllocationInConfidentAreaBenchmark {

    @Benchmark
    public void allocateValues(Blackhole bh) {

        try (Arena arena = Arena.ofConfined()) {
            MemorySegment x = arena.allocate(ValueLayout.JAVA_INT);
            MemorySegment y = arena.allocate(ValueLayout.JAVA_INT);

            x.set(ValueLayout.JAVA_INT, 0L, 111);
            y.set(ValueLayout.JAVA_INT, 0L, 222);

            int result = x.get(ValueLayout.JAVA_INT, 0L) + y.get(ValueLayout.JAVA_INT, 0L);

            bh.consume(result);
        }
    }

    /*
     * ============================== HOW TO RUN THIS BENCHMARK ====================================
     *
     * To run benchmark and see the results do the following:
     *    $ ./mvnw clean package
     *    $ java -jar target/benchmarks.jar OffHeapAllocationInConfidentAreaBenchmark
     * ==============================================================================================
     */

    public static void main(String[] args) throws RunnerException {
        Options opt =
                new OptionsBuilder()
                        .include(OffHeapAllocationInConfidentAreaBenchmark.class.getSimpleName())
                        .build();

        new Runner(opt).run();
    }
}
