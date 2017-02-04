package com.rx;

/**
 * Created by hejianjun on 2017/2/4.
 */
public class UnsolvableException extends Exception {
    public int x;
    public int y;

    public UnsolvableException(int x, int y) {
        super(String.format("单元格 (%s,%s) 无解",x,y));
        this.x = x;
        this.y = y;
    }
}
