package org.max.jmh.matrix;

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

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * Benchmark matrix multiplication using row-wise vs column-wise traversal.
 * <p>
 * More details can be found at section '6.2 Cache Access' from publication
 * https://people.freebsd.org/~lstewart/articles/cpumemory.pdf
 *
 * <p>
 * <p>
 * Size = 500...1000
 * <p>
 * Benchmark                         Mode  Cnt          Score           Error  Units
 * MatrixMulBenchmark.mulClassic     avgt    5  618797987.000 ± 138543613.688  ns/op
 * MatrixMulBenchmark.mulOptimized   avgt    5  188814047.667 ±   7094103.201  ns/op
 * MatrixMulBenchmark.mulTransposed  avgt    5  228318833.800 ±  12346777.314  ns/op
 * <p>
 * Size = 1000...2000
 * <p>
 * Benchmark                         Mode  Cnt            Score            Error  Units
 * MatrixMulBenchmark.mulClassic     avgt    5  11204080605.800 ±  781113076.684  ns/op
 * MatrixMulBenchmark.mulOptimized   avgt    5   3800444510.800 ± 1974161600.830  ns/op
 * MatrixMulBenchmark.mulTransposed  avgt    5   3957493544.800 ±  227665138.201  ns/op
 */
@Fork(1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@Warmup(iterations = 5, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
@Measurement(iterations = 5, time = 1000, timeUnit = TimeUnit.MILLISECONDS)
public class MatrixMulBenchmark {

    private static int MATRIX_MIN_SIZE = 1000;
    private static int MATRIX_MAX_SIZE = 2000;

    @State(Scope.Benchmark)
    public static class BenchmarkState {

        final ThreadLocalRandom rand = ThreadLocalRandom.current();

        final int m1Rows = MATRIX_MIN_SIZE + rand.nextInt(MATRIX_MAX_SIZE - MATRIX_MIN_SIZE);
        final int sameDim = MATRIX_MIN_SIZE + rand.nextInt(MATRIX_MAX_SIZE - MATRIX_MIN_SIZE);

        final int m2Cols = MATRIX_MIN_SIZE + rand.nextInt(MATRIX_MAX_SIZE - MATRIX_MIN_SIZE);

        final double[][] m1 = generateRandomMatrix(m1Rows, sameDim);

        final double[][] m2 = generateRandomMatrix(sameDim, m2Cols);

        private static double[][] generateRandomMatrix(int rows, int cols) {
            assert rows >= 0 && cols >= 0;

            ThreadLocalRandom rand = ThreadLocalRandom.current();
            double[][] matrix = new double[rows][cols];

            for (int i = 0; i < rows; ++i) {
                for (int j = 0; j < cols; ++j) {
                    matrix[i][j] = rand.nextDouble() * 100.0;
                }
            }

            return matrix;
        }
    }

    @Benchmark
    public void mulClassic(BenchmarkState benchmarkState, Blackhole bh) {
        double[][] res = mulClassic(benchmarkState.m1, benchmarkState.m2);
        bh.consume(res);
    }

    /**
     * Classic marix multiplication algorithm. 'm1' traversed in row-wise order, meanwhile
     * 'm2' traversed in column-wise order.
     */
    private static double[][] mulClassic(double[][] m1, double[][] m2) {

        final int m1Rows = m1.length;
        final int m1Cols = m1[0].length;

        final int m2Rows = m2.length;
        final int m2Cols = m2[0].length;

        assert m1Cols == m2Rows;

        double[][] res = new double[m1Rows][m2Cols];

        for (int row = 0; row < m1Rows; ++row) {
            for (int col = 0; col < m2Cols; ++col) {

                for (int k = 0; k < m1Cols; ++k) {
                    res[row][col] += (m1[row][k] * m2[k][col]);
                }

            }
        }

        return res;
    }

    @Benchmark
    public void mulOptimized(BenchmarkState benchmarkState, Blackhole bh) {
        double[][] res = mulOptimized(benchmarkState.m1, benchmarkState.m2);
        bh.consume(res);
    }

    /**
     * We can travers both 'm1' and 'm2' in row-wise order, b/c multiplication result doesn't
     * depend on order of sums. So we traverse both 'm1' and 'm2' in row-wise order and store
     * results to 'res' matrix in row fashion too.
     */
    private static double[][] mulOptimized(double[][] m1, double[][] m2) {

        final int m1Rows = m1.length;
        final int m1Cols = m1[0].length;

        final int m2Rows = m2.length;
        final int m2Cols = m2[0].length;

        assert m1Cols == m2Rows;

        double[][] res = new double[m1Rows][m2Cols];

        for (int i = 0; i < m1Rows; ++i) {
            for (int j = 0; j < m1Cols; ++j) {

                for (int k = 0; k < m2Cols; ++k) {
                    res[i][k] += (m1[i][j] * m2[j][k]);
                }

            }
        }

        return res;
    }

    @Benchmark
    public void mulTransposed(BenchmarkState benchmarkState, Blackhole bh) {
        double[][] res = mulTransposed(benchmarkState.m1, benchmarkState.m2);
        bh.consume(res);
    }

    /**
     * This algorithm is similar to 'mulOptimized' but explicitly transpose 'm2' before multiplication.
     * So we have slightly increased space complexity, but both 'm1' and 'm2' are also traversed in row-wise fashion.
     */
    private static double[][] mulTransposed(double[][] m1, double[][] m2) {

        int m1Rows = m1.length;
        int sameDim = m1[0].length;
        int m2Cols = m2[0].length;

        double[][] m2Transposed = transpose(m2);

        int m2Rows = m2Transposed.length;

        double[][] res = new double[m1Rows][m2Cols];

        for (int i = 0; i < m1Rows; ++i) {
            for (int j = 0; j < m2Rows; ++j) {
                for (int k = 0; k < sameDim; ++k) {
                    res[i][j] += (m1[i][k] * m2Transposed[j][k]);
                }
            }
        }

        return res;
    }

    /**
     * Transpose matrix. Just change rows with cols.
     */
    private static double[][] transpose(double[][] m) {

        int rows = m.length;
        int cols = m[0].length;

        double[][] res = new double[cols][rows];

        for (int row = 0; row < rows; ++row) {
            for (int col = 0; col < cols; ++col) {
                res[col][row] = m[row][col];
            }
        }

        return res;
    }

    // ============================== DEBUG section ====================================

//    public static void main(String[] args) {
//
//        ThreadLocalRandom rand = ThreadLocalRandom.current();
//
//        int m1Rows = 1 + rand.nextInt(10);
//        int sameDim = 1 + rand.nextInt(10);
//        int m2Cols = 1 + rand.nextInt(10);
//
////            final double[][] m1 = {
////                    {1, 2},
////                    {3, 4}
////            };
//
//        final double[][] m1 = BenchmarkState.generateRandomMatrix(m1Rows, sameDim);
//
////            final double[][] m2 = {
////                    {4,5,6},
////                    {7,8,9}
////            };
//
//        final double[][] m2 = BenchmarkState.generateRandomMatrix(sameDim, m2Cols);
//
//        printMatrix(mulClassic(m1, m2));
//        printMatrix(mulOptimized(m1, m2));
//        printMatrix(mulTransposed(m1, m2));
//    }
//
//    private static void printMatrix(double[][] res) {
//        StringBuilder buf = new StringBuilder();
//        for (int row = 0; row < res.length; ++row) {
//            for (int col = 0; col < res[0].length; ++col) {
//                if (col != 0) {
//                    buf.append(" ");
//                }
//                buf.append(res[row][col]);
//            }
//
//            buf.append(System.lineSeparator());
//        }
//
//        System.out.println(buf);
//    }

    /*
     * ============================== HOW TO RUN THIS TEST: ====================================
     *
     * You can see the benchmark runs as usual.
     *
     * You can run this test:
     *
     * a) Via the command line:
     *    $ ./mvnw clean package
     *    $ java -jar target/benchmarks.jar MatrixMulBenchmark
     */

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(MatrixMulBenchmark.class.getSimpleName())
//            .threads(Runtime.getRuntime().availableProcessors())
//            .jvmArgs("-ea")
                .build();

        new Runner(opt).run();
    }

}
