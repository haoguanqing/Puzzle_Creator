package com.puzzle.guanqing.puzzlecreator;

abstract class Node {
    protected static final int EMPTY = -10194112;
    protected static final int CENTER = -40930057;

    public int x;
    public int y;
    public int value;
    public int dx = 0;
    public int dy = 0;
    public boolean isEmpty = false;
    public boolean isShadow = false;

    protected Node(int x, int y, int value) {
        this.x = x;
        this.y = y;
        this.value = value;
    }

    public boolean isSameRow(Node n) {
        return x == n.x;
    }

    public boolean isSameCol(Node n) {
        return y == n.y;
    }

    public boolean isFull() {
        return dx != 0 && dy != 0;
    }

    @Override
    public String toString() {
        {
            return " [" + value+ "] ";
        }
    }

    public String toInfoString() {
        return "["+x+", "+y+", value="+value+"]";
    }
}
