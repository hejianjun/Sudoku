package com.rx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.RecursiveTask;


/**
 *
 */
public class Sudoku extends RecursiveTask<List<int[]>> {
    private static int[] numbs = new int[9];

    static {
        for (int i = 0; i < numbs.length; i++) {
            numbs[i] = 0b1 << i;
        }
    }

    private int[] matrix = new int[81];
    private int index;
    private int num;

    public Sudoku(int index, int num) {
        Arrays.fill(matrix, 0b111111111);
        this.index = index;
        this.num = num;
    }

    public Sudoku(int[] matrix, int index, int num) {
        this.matrix = matrix;
        this.index = index;
        this.num = num;
    }

    public Sudoku(int[][] m) throws UnsolvableException {
        this(-2, 0);
        for (int y = 0; y < m.length; y++) {
            for (int x = 0; x < m[y].length; x++) {
                if (m[y][x] > 0 && m[y][x] < 10) {
                    setMatrixPri(matrix, x, y, m[y][x]);
                }
            }
        }

    }

    @Override
    protected List<int[]> compute() {
        List<int[]> matrixList = new ArrayList<>();
        try {
            if (index > -1) {
                int x = index / 9;
                int y = Math.floorMod(index, 9);
                setMatrixPri(matrix, x, y, num);
            }
            index = findMinCell(matrix);
            if (index == -1) {
                matrixList.add(matrix);
            } else {
                List<Integer> numList = num2List(matrix[index]);
                List<Sudoku> forkList = new ArrayList<>();
                for (Integer n : numList) {
                    Sudoku sudoku = new Sudoku(matrix.clone(), index, n);
                    sudoku.fork();
                    forkList.add(sudoku);
                }
                for (Sudoku sudoku : forkList) {
                    matrixList.addAll(sudoku.join());
                }
            }
        } catch (UnsolvableException e) {
            //System.out.println(e);
        }
        return matrixList;
    }


    public static void setMatrixPri(int[] matrix, int x, int y, int num) throws UnsolvableException {
        int index = x * 9 + y;
        matrix[index] = ~num;
        int num2 = numbs[num - 1];
        for (int k = 0; k < 9; k++) {
            if (removeNum(matrix, x, k, num2) == 0) {
                throw new UnsolvableException(x, k);
            }
            if (removeNum(matrix, k, y, num2) == 0) {
                throw new UnsolvableException(k, y);
            }
        }
        int x1 = Math.floorDiv(x, 3) * 3;
        int y1 = Math.floorDiv(y, 3) * 3;
        for (int i = x1; i < x1 + 3; i++) {
            for (int j = y1; j < y1 + 3; j++) {
                if (removeNum(matrix, i, j, num2) == 0) {
                    throw new UnsolvableException(i, j);
                }
            }
        }
    }

    public static void setMatrixPri(int[] matrix, int index, int num2) throws UnsolvableException {
        int x = index / 9;
        int y = Math.floorMod(index, 9);
        int num;
        for (num = 1; num < 10; num++) {
            if (numbs[num - 1] == num2) {
                break;
            }
        }
        setMatrixPri(matrix, x, y, num);
    }

    public static int findMinCell(int[] matrix) throws UnsolvableException {
        int index = -1, min = 9;
        for (int i = 0; i < matrix.length; i++) {
            if (matrix[i] > 0) {
                int count = count1OfValue(matrix[i]);
                if (count == 1) {
                    setMatrixPri(matrix, i, matrix[i]);
                    if (index == i) {
                        index = -1;
                        min = 9;
                    }
                    i = -1;
                } else if (count < min) {
                    index = i;
                    min = count;
                }
            }
        }
        return index;
    }


    public static int removeNum(int[] matrix, int x, int y, int num) {
        int index = x * 9 + y;
        if (matrix[index] > 0) {
            matrix[index] &= ~num;
        }
        return matrix[index];
    }

    private static int count1OfValue(int value) {
        int count;
        for (count = 0; value > 0; count++) {
            value &= (value - 1);
        }
        return count;
    }

    public static List<Integer> num2List(int num) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            if ((numbs[i] & num) != 0) {
                list.add(i + 1);
            }
        }
        return list;
    }

    public static String num2String(int num) {
        if (num < 0) {
            return String.format("%2s", ~num);
        } else {
            return String.format("%2s", num2List(num));
        }
    }

    public static String toString(int[] matrix) {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < 9; y++) {
            sb.append("[");
            for (int x = 0; x < 9; x++) {
                int index = x * 9 + y;
                if (x != 0) {
                    sb.append(",");
                }
                sb.append(num2String(matrix[index]));
            }
            sb.append("]\n");
        }
        return sb.toString();
    }

}
