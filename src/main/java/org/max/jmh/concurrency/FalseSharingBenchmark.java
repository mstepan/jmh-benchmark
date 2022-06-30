package org.max.jmh.concurrency;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Group;
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
 * False sharing benchmark.
 *
 * sysctl -a | grep hw.cachelinesize => hw.cachelinesize: 64
 *
 * Benchmark                                 Mode  Cnt   Score   Error  Units
 * FalseSharingBenchmark.falseSharing        avgt    5  13.422 ± 2.372  ns/op
 * FalseSharingBenchmark.falseSharing:read   avgt    5   3.092 ± 0.849  ns/op
 * FalseSharingBenchmark.falseSharing:write  avgt    5  23.751 ± 3.958  ns/op
 *
 * FalseSharingBenchmark.withPadding         avgt    5   9.121 ± 1.560  ns/op
 * FalseSharingBenchmark.withPadding:read    avgt    5   2.231 ± 0.414  ns/op
 * FalseSharingBenchmark.withPadding:write   avgt    5  16.011 ± 2.718  ns/op
 *
 */
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
public class FalseSharingBenchmark {

    private static final ThreadLocalRandom RAND = ThreadLocalRandom.current();

    @State(Scope.Thread)
    public static class ThreadState {
        int randValue = RAND.nextInt();
    }

    @State(Scope.Group)
    public static class Register {
        volatile int readCount;
        volatile int writeCount;
    }

    @State(Scope.Group)
    public static class RegisterWithPadding {
        volatile int readCount;
        // 64 bytes cache line
        int x1, x2, x3, x4, x5, x6, x7, x8, x9, x10, x11, x12, x13, x14, x15, x16;

        volatile int writeCount;
        // 64 bytes cache line
        int y1, y2, y3, y4, y5, y6, y7, y8, y9, y10, y11, y12, y13, y14, y15, y16;
    }

    @Benchmark
    @Group("falseSharing")
    public void read(Register state, Blackhole bh) {
        bh.consume(state.readCount);
    }

    @Benchmark
    @Group("falseSharing")
    public void write(Register state, ThreadState threadState) {
        state.writeCount = threadState.randValue;
    }

    @Benchmark
    @Group("withPadding")
    public void read(RegisterWithPadding state, Blackhole bh) {
        bh.consume(state.readCount);
    }

    @Benchmark
    @Group("withPadding")
    public void write(RegisterWithPadding state, ThreadState threadState) {
        state.writeCount = threadState.randValue;
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
     *    $ java -jar target/benchmarks.jar FalseSharingBenchmark -t 32
     */

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
            .include(FalseSharingBenchmark.class.getSimpleName())
            .build();

        new Runner(opt).run();
    }

}
