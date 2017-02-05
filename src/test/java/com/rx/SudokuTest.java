package com.rx;

import org.junit.Test;

import java.util.List;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;

/**
 * Created by hejianjun on 2017/2/3.
 */
public class SudokuTest {

    private Sudoku sudoku;

    public SudokuTest() {
        int[][] m = {
                {0, 7, 0, 8, 1, 0, 0, 6, 0},
                {2, 6, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 1, 0, 0, 0, 0, 4, 2},
                {0, 2, 0, 0, 5, 4, 0, 8, 0},
                {0, 1, 9, 6, 0, 0, 3, 0, 0},
                {0, 0, 8, 1, 0, 0, 2, 9, 0},
                {0, 0, 0, 0, 0, 0, 0, 3, 8},
                {0, 8, 0, 4, 0, 6, 0, 2, 0},
                {1, 4, 0, 0, 0, 0, 6, 0, 0},
        };
        try {
            sudoku = new Sudoku(m);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void testToString() throws Exception {
        System.out.println(sudoku);
    }

    @Test
    public void testCalculate() throws Exception {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        ForkJoinTask<List<int[]>> result = forkJoinPool.submit(sudoku);
        for (int[] ints : result.get()) {
            System.out.println(Sudoku.toString(ints));
        }
        forkJoinPool.shutdown();
    }
}