package org.max.jmh.math;

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
 * MMicro-benchmark class for POW function.
 *
 * Benchmark                            Mode  Cnt   Score    Error  Units
 * PowerBenchmark.powerQuickIterative   avgt   20   8.724 ±  0.469  ns/op
 * PowerBenchmark.powerQuickRec         avgt   20   9.220 ±  0.585  ns/op
 * PowerBenchmark.powerSlow             avgt   20  30.280 ± 18.679  ns/op
 * PowerBenchmark.powerStandardLibrary  avgt   20  26.755 ±  2.610  ns/op
 */
@Fork(2)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 10, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 10, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
public class PowerBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        final int value = ThreadLocalRandom.current().nextInt(100);
        final int exp = 20 + ThreadLocalRandom.current().nextInt(100);
    }

    @Benchmark
    public void powerStandardLibrary(BenchmarkState benchmarkState, Blackhole bh) {
        double res = Math.pow(benchmarkState.value, benchmarkState.exp);
        bh.consume(res);
    }

    @Benchmark
    public void powerSlow(BenchmarkState benchmarkState, Blackhole bh) {
        double res = powerSlow(benchmarkState.value, benchmarkState.exp);
        bh.consume(res);
    }

    @Benchmark
    public void powerQuickRec(BenchmarkState benchmarkState, Blackhole bh) {
        double res = powerQuickRec(benchmarkState.value, benchmarkState.exp);
        bh.consume(res);
    }

    @Benchmark
    public void powerQuickIterative(BenchmarkState benchmarkState, Blackhole bh) {
        double res = powerQuickIterative(benchmarkState.value, benchmarkState.exp);
        bh.consume(res);
    }

    private static double powerSlow(int value, int exp) {
        if (exp == 0) {
            return 1.0;
        }
        if (exp == 1) {
            return value;
        }

        double res = value;

        for (int i = 0; i < exp - 1; ++i) {
            res *= value;
        }
        return res;
    }

    private static double powerQuickRec(int value, int exp) {
        if (exp == 0) {
            return 1.0;
        }
        if (exp == 1) {
            return value;
        }

        final double part = powerQuickRec(value, exp >>> 1);

        double res = part * part;

        if ((exp & 1) != 0) {
            res *= value;
        }

        return res;
    }

    private static double powerQuickIterative(int value, int exp) {
        if (exp == 0) {
            return 1.0;
        }
        if (exp == 1) {
            return value;
        }

        double prod = value;
        double res = 1.0;

        while (exp != 0) {
            if ((exp & 1) != 0) {
                // odd case
                res *= prod;
            }
            prod *= prod;
            exp >>>= 1;
        }

        return res;
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
     *    $ java -jar target/benchmarks.jar PowerBenchmark
     */

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
            .include(PowerBenchmark.class.getSimpleName())
//            .threads(Runtime.getRuntime().availableProcessors())
//            .jvmArgs("-ea")
            .build();

        new Runner(opt).run();
    }

}
