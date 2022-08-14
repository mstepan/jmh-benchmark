package org.max.jmh.exceptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
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

/**
 * Measure flow pass using checked exception, runtime exception and normal control flow.
 *
 * Benchmark                                         Mode  Cnt        Score        Error  Units
 * ExceptionVsControlFlowBenchmark.checkedException  avgt    5  3816731.012 ± 152898.567  ns/op
 * ExceptionVsControlFlowBenchmark.controlFlow       avgt    5    21106.774 ±   4106.185  ns/op
 * ExceptionVsControlFlowBenchmark.runtimeException  avgt    5   298844.739 ±   8389.289  ns/op
 */
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
public class ExceptionVsControlFlowBenchmark {

    @Benchmark
    public void checkedException(Blackhole bh) {
        List<Integer> data = new ArrayList<>();
        final int errorIt = 3;

        for (int i = 0; i < 10_000; ++i) {
            try {
                if ((i % errorIt) == 0) {
                    throw new IOException("Some exception");
                }
            }
            catch (IOException ioEx) {
                data.add(i);
            }
        }


        bh.consume(data);
    }

    @Benchmark
    public void runtimeException(Blackhole bh) {
        List<Integer> data = new ArrayList<>();
        final int errorIt = 3;

        Object dump = new Object();
        Object obj;

        for (int i = 0; i < 10_000; ++i) {
            try {
                if ((i % errorIt) == 0) {
                    obj = null;
                }
                else {
                    obj = dump;
                }
                obj.toString();
            }
            catch (NullPointerException npEx) {
                data.add(i);
            }
        }


        bh.consume(data);
    }

    @Benchmark
    public void controlFlow(Blackhole bh) {
        List<Integer> data = new ArrayList<>();
        final int errorIt = 3;

        for (int i = 0; i < 10_000; ++i) {
            try {
                if ((i % errorIt) == 0) {
                    data.add(i);
                }
            }
            catch (IllegalArgumentException ioEx) {
                data.add(i);
            }
        }

        bh.consume(data);
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
     *    $ java -jar target/benchmarks.jar ExceptionVsControlFlowBenchmark
     */

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
            .include(ExceptionVsControlFlowBenchmark.class.getSimpleName())
//            .threads(Runtime.getRuntime().availableProcessors())
//            .jvmArgs("-ea")
            .build();

        new Runner(opt).run();
    }

}
