package org.max.jmh.lambda;

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
 * Benchmark for anonymous classes vs lambdas vs static field with anonymous classes.
 *
 * Benchmark                                Mode  Cnt  Score   Error  Units
 * LambdaVsAnonymousBenchmark.anonymous     avgt    5  0.400 ± 0.034  ns/op
 * LambdaVsAnonymousBenchmark.lambda        avgt    5  0.386 ± 0.047  ns/op
 * LambdaVsAnonymousBenchmark.staticFields  avgt    5  0.383 ± 0.023  ns/op
 *
 */
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
public class LambdaVsAnonymousBenchmark {

    interface IntProducer {
        int get();
    }

    static final IntProducer pstat1 = new IntProducer() {
        @Override
        public int get() {
            return 1;
        }
    };

    static final IntProducer pstat2 = new IntProducer() {
        @Override
        public int get() {
            return 2;
        }
    };

    static final IntProducer pstat3 = new IntProducer() {
        @Override
        public int get() {
            return 3;
        }
    };

    @Benchmark
    public void anonymous(Blackhole bh) {
        IntProducer p1 = new IntProducer() {
            @Override
            public int get() {
                return 1;
            }
        };

        IntProducer p2 = new IntProducer() {
            @Override
            public int get() {
                return 2;
            }
        };

        IntProducer p3 = new IntProducer() {
            @Override
            public int get() {
                return 3;
            }
        };


        bh.consume(p1.get() + p2.get() + p3.get());
    }

    @Benchmark
    public void lambda(Blackhole bh) {
        IntProducer p1 = () -> 1;
        IntProducer p2 = () -> 2;
        IntProducer p3 = () -> 3;

        bh.consume(p1.get() + p2.get() + p3.get());
    }

    @Benchmark
    public void staticFields(Blackhole bh) {
        bh.consume(pstat1.get() + pstat2.get() + pstat3.get());
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
     *    $ java -jar target/benchmarks.jar LambdaVsAnonymousBenchmark
     */

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
            .include(LambdaVsAnonymousBenchmark.class.getSimpleName())
//            .threads(Runtime.getRuntime().availableProcessors())
//            .jvmArgs("-ea")
            .build();

        new Runner(opt).run();
    }

}
