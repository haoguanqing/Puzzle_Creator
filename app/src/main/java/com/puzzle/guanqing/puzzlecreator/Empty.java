package com.puzzle.guanqing.puzzlecreator;

class Empty extends Node {

    public Empty(int x, int y) {
        super(x, y, EMPTY);
        this.isEmpty = true;
    }

    @Override
    public String toString() {
        /*String s = " [";
        if (dx == 1) {
            s += "d";
        }
        if (dx == -1) {
            s += "t";
        }
        if (dy == 1) {
            s += "l";
        }
        if (dy == -1) {
            s += "r";
        }
        s += "] ";
        return isShadow ? s : " [  ] ";*/
        return "[_]";
    }
}
