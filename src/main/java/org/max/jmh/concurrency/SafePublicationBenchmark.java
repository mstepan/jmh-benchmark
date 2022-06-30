package org.max.jmh.concurrency;

import java.util.Arrays;
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
 * Benchmark                                   Mode  Cnt   Score   Error  Units
 * SafePublicationBenchmark.fullySynchronized  avgt    5  40.300 ± 4.419  ns/op
 * SafePublicationBenchmark.immutableObject    avgt    5  14.853 ± 1.432  ns/op
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Fork(1)
@Warmup(iterations = 5, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
public class SafePublicationBenchmark {

    @State(Scope.Benchmark)
    public static class BenchmarkState {
        final ImmutableRes immutableRes = new ImmutableRes();
        final SynchronizedRes synchRes = new SynchronizedRes();
    }

    @State(Scope.Thread)
    public static class ThreadState {
        final ThreadLocalRandom rand = ThreadLocalRandom.current();
    }


    @Benchmark
    public void immutableObject(BenchmarkState state, ThreadState threadState, Blackhole bh) {
        bh.consume(state.immutableRes.getResult());
        int randVal = threadState.rand.nextInt();
        state.immutableRes.setResult(randVal, new int[] {randVal});
    }

    @Benchmark
    public void fullySynchronized(BenchmarkState state, ThreadState threadState, Blackhole bh) {
        bh.consume(state.synchRes.getResult());
        int randVal = threadState.rand.nextInt();
        state.synchRes.setResult(randVal, new int[] {randVal});
    }

    static final class SynchronizedRes {

        /**
         * Guarded by this
         */
        private int value;
        /**
         * Guarded by this
         */
        private int[] factors = new int[] {0};

        public synchronized void setResult(int value, int[] factors) {
            this.value = value;
            this.factors = factors;
        }

        public synchronized ValueAndFactors getResult() {
            return new ValueAndFactors(value, factors);
        }


    }


    // Fully immutable object
    static final class ImmutableRes {
        private volatile ValueAndFactors result = new ValueAndFactors(0, new int[] {0});

        public void setResult(int value, int[] factors) {
            result = new ValueAndFactors(value, Arrays.copyOf(factors, factors.length));
        }

        public ValueAndFactors getResult() {
            return result;
        }

    }

    static final class ValueAndFactors {

        final int value;
        final int[] factors;

        public ValueAndFactors(int value, int[] factors) {
            this.value = value;
            this.factors = factors;
        }

        int value() {
            return value;
        }

        int[] factors() {
            return factors;
        }
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
     *    $ java -jar target/benchmarks.jar SafePublicationBenchmark
     */
    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
            .include(SafePublicationBenchmark.class.getSimpleName())
            .threads(Runtime.getRuntime().availableProcessors())
//            .jvmArgs("-ea")
            .build();

        new Runner(opt).run();
    }

}
