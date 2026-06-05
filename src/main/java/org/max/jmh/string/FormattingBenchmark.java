package org.max.jmh.string;

import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.TimeUnit;

/**
 * Benchmark String.format(...) effect
 *
 * <p>Benchmark Mode Cnt Score Error Units FormattingBenchmark.format avgt 5 208.383 ± 4.416 ns/op
 * FormattingBenchmark.usingStringBuilder avgt 5 86.615 ± 1.125 ns/op
 */
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
public class FormattingBenchmark {

    private static final int ORDER_ID = 133;
    private static final double PRICE = 99.17;
    private static final int CUSTOMER_ID = 177;

    @Benchmark
    public void format(Blackhole bh) {
        String result =
                String.format(
                        "order: %d, price: $ %.2f, customer: %d", ORDER_ID, PRICE, CUSTOMER_ID);
        bh.consume(result);
    }

    @Benchmark
    public void usingStringBuilder(Blackhole bh) {
        StringBuilder result = new StringBuilder();

        result.append("order: ").append(ORDER_ID);

        result.append(", price: ").append(String.format("$ %.2f", PRICE));

        result.append(", customer: ").append(CUSTOMER_ID);

        bh.consume(result);
    }

    /*
     * ============================== HOW TO RUN THIS BENCHMARK ====================================
     *
     * To run benchmark and see the results do the following:
     *    $ ./mvnw clean package
     *    $ java -jar target/benchmarks.jar FormattingBenchmark
     *
     * ============================== HOW TO RUN THIS BENCHMARK ====================================
     */

    static void main() throws RunnerException {
        Options opt =
                new OptionsBuilder()
                        .include(FormattingBenchmark.class.getSimpleName())
                        //            .threads(Runtime.getRuntime().availableProcessors())
                        .jvmArgs("-Xms1G", "-Xmx1G")
                        .build();

        new Runner(opt).run();
    }
}
