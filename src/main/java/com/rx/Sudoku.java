package com.rx;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.RecursiveTask;


/**
 * 数独类
 */
public class Sudoku extends RecursiveTask<List<int[]>> {
    /**
     * 二进制缓存数组
     */
    private static int[] numbs = new int[9];

    static {
        for (int i = 0; i < numbs.length; i++) {
            numbs[i] = 0b1 << i;
        }
    }

    /**
     * 数独矩阵，负数代表已填数字，正数是数字的二进制位移量
     */
    private int[] matrix = new int[81];

    public Sudoku() {
        Arrays.fill(matrix, 0b111111111);
    }

    public Sudoku(int[] matrix) {
        this.matrix = matrix;
    }

    public Sudoku(int[][] m) throws UnsolvableException {
        this();
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
            int index = findMinCell(matrix);
            if (index == -1) {
                matrixList.add(matrix);
            } else {
                List<Integer> numList = num2List(matrix[index]);
                List<Sudoku> forkList = new ArrayList<>();
                int last = numList.size() - 1;
                for (int i = last; i >= 0; --i) {
                    Integer n = numList.get(i);
                    int[] matrix2 = matrix.clone();
                    int x = index / 9;
                    int y = Math.floorMod(index, 9);
                    setMatrixPri(matrix2, x, y, n);
                    Sudoku sudoku = new Sudoku(matrix2);
                    if (i != 0) {
                        sudoku.fork();
                        forkList.add(sudoku);
                    } else {
                        matrixList.addAll(sudoku.compute());
                    }
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

    /**
     * 设置矩阵数字
     *
     * @param matrix 矩阵
     * @param x      横向坐标
     * @param y      纵向坐标
     * @param num    填入数字
     * @throws UnsolvableException 求解失败，数组无解
     */
    public static void setMatrixPri(int[] matrix, int x, int y, int num) throws UnsolvableException {
        int num2 = numbs[num - 1];
        int index = x * 9 + y;
        if ((num2 | matrix[index]) != matrix[index]) {
            throw new UnsolvableException(x, y);
        }
        matrix[index] = ~num;
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

    /**
     * 上面方法的重载
     *
     * @param matrix 矩阵
     * @param index  一维索引
     * @param num2   二进制位移量
     * @throws UnsolvableException 无解异常
     */
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

    /**
     * 填入已确定数字并获取最小未确定单元格的索引
     *
     * @param matrix 矩阵
     * @return 最小未确定单元格的索引
     * @throws UnsolvableException 无解异常
     */
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

    /**
     * 移除矩阵单元格某个数字的可能性
     *
     * @param matrix 矩阵
     * @param x      横向坐标
     * @param y      纵向坐标
     * @param num    数字的二进制位移量
     * @return 二进制可能性，0为无解
     */
    public static int removeNum(int[] matrix, int x, int y, int num) {
        int index = x * 9 + y;
        if (matrix[index] > 0) {
            matrix[index] &= ~num;
        }
        return matrix[index];
    }

    /**
     * 获取二进制位移量中的可能性
     *
     * @param value 二进制位移量
     * @return 可能的数量
     */
    private static int count1OfValue(int value) {
        int count;
        for (count = 0; value > 0; count++) {
            value &= (value - 1);
        }
        return count;
    }

    /**
     * 二进制位移量转换为数字列表
     *
     * @param num 二进制位移量
     * @return 数字列表
     */
    public static List<Integer> num2List(int num) {
        List<Integer> list = new ArrayList<>();
        for (int i = 0; i < 9; i++) {
            if ((numbs[i] & num) != 0) {
                list.add(i + 1);
            }
        }
        return list;
    }

    /**
     * 矩阵单元格转换为字符串
     *
     * @param cell 单元格中的数字
     * @return 显示字符串
     */
    public static String cell2String(int cell) {
        if (cell < 0) {
            return String.format("%2s", ~cell);
        } else {
            return String.format("%2s", num2List(cell));
        }
    }

    /**
     * 矩阵转换为字符串
     *
     * @param matrix 矩阵
     * @return 字符串
     */
    public static String toString(int[] matrix) {
        StringBuilder sb = new StringBuilder();
        for (int y = 0; y < 9; y++) {
            sb.append("[");
            for (int x = 0; x < 9; x++) {
                int index = x * 9 + y;
                if (x != 0) {
                    sb.append(",");
                }
                sb.append(cell2String(matrix[index]));
            }
            sb.append("]\n");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        return Sudoku.toString(matrix);
    }
}
