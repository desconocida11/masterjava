package ru.javaops.masterjava.matrix;

import java.util.*;
import java.util.concurrent.*;

/**
 * gkislin
 * 03.07.2016
 */
public class MatrixUtil {

    public static int[][] concurrentMultiply3(int[][] matrixA, int[][] matrixB, ExecutorService executor) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        class ForkJoinMatrix extends RecursiveAction {
            private final int j;
            public ForkJoinMatrix(int j) {
                this.j = j;
            }
            @Override
            protected void compute() {
                int[] thatColumn = new int[matrixSize];
                for (int k = 0; k < matrixSize; k++) {
                    thatColumn[k] = matrixB[k][j];
                }
                for (int i = 0; i < matrixSize; i++) {
                    int[] thisRow = matrixA[i];
                    int summand = 0;
                    for (int k = 0; k < matrixSize; k++) {
                        summand += thisRow[k] * thatColumn[k];
                    }
                    matrixC[i][j] = summand;
                }
            }
        }

        Collection<ForkJoinMatrix> tasks = new ArrayList<>();
        for (int j = 0; j < matrixSize; j++) {
            final int finalJ = j;
            tasks.add(new ForkJoinMatrix(finalJ));
        }
        ForkJoinTask.invokeAll(tasks);
        return matrixC;
    }

    // implement parallel multiplication matrixA*matrixB
    public static int[][] concurrentMultiply2(int[][] matrixA, int[][] matrixB, ExecutorService executor) throws ExecutionException {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        final CompletionService<Map.Entry<Integer, int[]>> completionService =
                new ExecutorCompletionService<>(executor);
        List<Future<Map.Entry<Integer, int[]>>> futures = new ArrayList<>();

        for (int j = 0; j < matrixSize; j++) {
            final int finalJ = j;

            final Future<Map.Entry<Integer, int[]>> contentFuture = completionService.submit(() -> {

                int[] thatColumn = new int[matrixSize];
                int[] matrixColumn = new int[matrixSize];
                for (int k = 0; k < matrixSize; k++) {
                    thatColumn[k] = matrixB[k][finalJ];
                }
                for (int i = 0; i < matrixSize; i++) {
                    int[] thisRow = matrixA[i];
                    int sum = 0;
                    for (int k = 0; k < matrixSize; k++) {
                        sum += thisRow[k] * thatColumn[k];
                    }
                    matrixColumn[i] = sum;
                }
                return new AbstractMap.SimpleEntry<>(finalJ, matrixColumn);
            });
            futures.add(contentFuture);
        }

        while (!futures.isEmpty()) {
            try {
                Future<Map.Entry<Integer, int[]>> future = completionService.poll(10, TimeUnit.SECONDS);
                if (future == null) {
                    return matrixC;
                }
                futures.remove(future);
                final Map.Entry<Integer, int[]> entry = future.get();
                for (int i = 0; i < matrixSize; i++) {
                    matrixC[i][entry.getKey()] = entry.getValue()[i];
                }
            } catch (InterruptedException e) {
                return matrixC;
            }
        }
        return matrixC;
    }


    // implement parallel multiplication matrixA*matrixB
    public static int[][] concurrentMultiply(int[][] matrixA, int[][] matrixB, ExecutorService executor) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        final CompletionService<Integer> completionService =
                new ExecutorCompletionService<>(executor);
        List<Future<Integer>> futures = new ArrayList<>();

        for (int j = 0; j < matrixSize; j++) {
            final int finalJ = j;

            final Future<Integer> contentFuture = completionService.submit(() -> {
                int[] thatColumn = new int[matrixSize];
                for (int k = 0; k < matrixSize; k++) {
                    thatColumn[k] = matrixB[k][finalJ];
                }
                for (int i = 0; i < matrixSize; i++) {
                    int[] thisRow = matrixA[i];
                    int summand = 0;
                    for (int k = 0; k < matrixSize; k++) {
                        summand += thisRow[k] * thatColumn[k];
                    }
                    matrixC[i][finalJ] = summand;
                }
                return finalJ;
            });
            futures.add(contentFuture);
        }

        while (!futures.isEmpty()) {
            try {
                Future<Integer> future = completionService.poll(10, TimeUnit.SECONDS);
                if (future == null) {
                    return matrixC;
                }
                futures.remove(future);
            } catch (InterruptedException e) {
                return matrixC;
            }
        }
        return matrixC;
    }

    //  optimize by https://habrahabr.ru/post/114797/
    public static int[][] singleThreadMultiply(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        final int[][] matrixC = new int[matrixSize][matrixSize];

        int[] thatColumn = new int[matrixSize];

        for (int j = 0; j < matrixSize; j++) {
            for (int k = 0; k < matrixSize; k++) {
                thatColumn[k] = matrixB[k][j];
            }
            for (int i = 0; i < matrixSize; i++) {
                int[] thisRow = matrixA[i];
                int summand = 0;
                for (int k = 0; k < matrixSize; k++) {
                    summand += thisRow[k] * thatColumn[k];
                }
                matrixC[i][j] = summand;
            }
        }
        return matrixC;
    }

    public static int[][] create(int size) {
        int[][] matrix = new int[size][size];
        Random rn = new Random();

        for (int i = 0; i < size; i++) {
            for (int j = 0; j < size; j++) {
                matrix[i][j] = rn.nextInt(10);
            }
        }
        return matrix;
    }

    public static boolean compare(int[][] matrixA, int[][] matrixB) {
        final int matrixSize = matrixA.length;
        for (int i = 0; i < matrixSize; i++) {
            for (int j = 0; j < matrixSize; j++) {
                if (matrixA[i][j] != matrixB[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }
}
