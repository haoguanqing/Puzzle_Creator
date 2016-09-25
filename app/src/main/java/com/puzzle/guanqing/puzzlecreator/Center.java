package com.puzzle.guanqing.puzzlecreator;

class Center extends Node {

    Center(int x, int y) {
        super(x, y, CENTER);
    }

    @Override
    public boolean isFull() {
        return true;
    }

    @Override
    public String toString() {
        return "{?}";
    }
}
