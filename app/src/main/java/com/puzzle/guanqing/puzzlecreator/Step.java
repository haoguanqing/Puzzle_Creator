package com.puzzle.guanqing.puzzlecreator;


public class Step extends Node {

    protected Step(int x, int y, int value) {
        super(x, y, value);
        this.isShadow = true;
    }

    @Override
    public String toString() {
        if (value <= 0) {
            return "[_]";
        } else {
            return "[" + value + "]";
        }
    }
}
