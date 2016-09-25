package com.puzzle.guanqing.puzzlecreator;

import android.util.Log;

import java.util.ArrayList;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import static com.puzzle.guanqing.puzzlecreator.Board.Direction.BOTTOM;
import static com.puzzle.guanqing.puzzlecreator.Board.Direction.LEFT;
import static com.puzzle.guanqing.puzzlecreator.Board.Direction.RIGHT;
import static com.puzzle.guanqing.puzzlecreator.Board.Direction.TOP;

public class Board {
    public Node[][] board;
    public Set<Node> shadows;
    public @interface Direction {
        int LEFT = 0;
        int RIGHT = 1;
        int BOTTOM = 2;
        int TOP = 3;
    }

    private int n;
    private Random rand = new Random();


    public Board(int n) {
        this.n = n;
        board = new Node[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                board[i][j] = new Empty(i, j);
            }
        }
        board[n/2][n/2] = new Center(n/2, n/2);
        shadows = new HashSet<>();
    }

    public Node getCenter() {
        return board[n/2][n/2];
    }

    public Node getRandomEmpty() {
        List<Node> list = new ArrayList<>(shadows);
        if (list.isEmpty()) {
            return null;
        }
        Node shadow = list.remove(rand.nextInt(list.size()));
        Node result = getRandomEmpty(shadow);
        while ((shadow.isFull() || result==null) && !list.isEmpty()) {
            shadow = list.remove(rand.nextInt(list.size()));
            result = getRandomEmpty(shadow);
        }
        if (shadow.isFull()) {
            Log.e("HGQ", "no available shadow");
        }
        return result;
    }

    public Node getRandomEmpty(Node shadow) {
        if (shadow.x > n || shadow.y > n) {
            throw new IllegalArgumentException();
        }
        List<Node> list = getAvailableEmpties(shadow);
        if (list.isEmpty()) {
            Log.e("HGQ", "shadow=" +shadow.toInfoString() +" no available empty");
            return null;
        }
        Node n = list.remove(rand.nextInt(list.size()));
        int value = getValue(n, shadow);
        while (shadow instanceof Center && value == 1 && !list.isEmpty()) {
            n = list.remove(rand.nextInt(list.size()));
            value = getValue(n, shadow);
        }
        while ((!(shadow instanceof Center)) && !list.isEmpty() && canReachCenter(n, value)) {
            n = list.remove(rand.nextInt(list.size()));
            value = getValue(n, shadow);
        }
        board[n.x][n.y] = new Step(n.x, n.y, value);
        updateSource(shadow);
        setShadow(n, shadow);
        shadows.remove(board[n.x][n.y]);
        return board[n.x][n.y];
    }

    void setShadow(Node n, Node destination) {
        @Direction int direction = getDirectionToDestination(n, destination);
        switch (direction) {
            case RIGHT:
                for (int i = n.y; i <= destination.y; i++) {
                    board[n.x][i].isShadow = true;
                    board[n.x][i].dy = 1;
                    shadows.add(board[n.x][i]);
                }
                break;
            case LEFT:
                for (int i = n.y; i >= destination.y; i--) {
                    board[n.x][i].isShadow = true;
                    board[n.x][i].dy = -1;
                    shadows.add(board[n.x][i]);
                }
                break;
            case BOTTOM:
                for (int i = n.x; i <= destination.x; i++) {
                    board[i][n.y].isShadow = true;
                    board[i][n.y].dx = 1;
                    shadows.add(board[i][n.y]);
                }
                break;
            case TOP:
                for (int i = n.x; i >= destination.x; i--) {
                    board[i][n.y].isShadow = true;
                    board[i][n.y].dx = -1;
                    shadows.add(board[i][n.y]);
                }
                break;
        }
    }

    public int getValue(Node n1, Node n2) {
        if (n1.isSameRow(n2)) {
            return Math.abs(n1.y - n2.y);
        } else if (n1.isSameCol(n2)) {
            return Math.abs(n1.x - n2.x);
        } else {
            return -1;
        }
    }

    public List<Node> getAvailableEmpties(Node shadow) {
        if (shadow == null) {
            return new ArrayList<>();
        }
        List<Node> list = new ArrayList<>();
        final int x = shadow.x;
        final int y = shadow.y;
        for (int i = x-1; i >= 0; i--) {
            if (!board[i][y].isEmpty || board[i][y].isShadow) {
                break;
            }
            if (isAvailableEmpty(board[i][y], shadow)) {
                list.add(board[i][y]);
            }
        }
        for (int i = x+1; i < n; i++) {
            if (!board[i][y].isEmpty || board[i][y].isShadow) {
                break;
            }
            if (isAvailableEmpty(board[i][y], shadow)) {
                list.add(board[i][y]);
            }
        }
        for (int i = y-1; i >= 0; i--) {
            if (!board[x][i].isEmpty || board[i][y].isShadow) {
                break;
            }
            if (isAvailableEmpty(board[x][i], shadow)) {
                list.add(board[x][i]);
            }
        }
        for (int i = y+1; i < n; i++) {
            if (!board[x][i].isEmpty || board[i][y].isShadow) {
                break;
            }
            if (isAvailableEmpty(board[x][i], shadow)) {
                list.add(board[x][i]);
            }
        }
        return list;
    }

    boolean isAvailableEmpty(Node node, Node dest) {
        if (node.isShadow) {
            return false;
        }
        int direct = getDirectionToDestination(node, dest);
        if (direct == TOP && dest.dx == 1) {
            return false;
        }
        if (direct == BOTTOM && dest.dx == -1) {
            return false;
        }
        if (direct == RIGHT && dest.dy == -1) {
            return false;
        }
        if (direct == LEFT && dest.dx == 1) {
            return false;
        }
        return true;
    }

    public @Direction int getDirectionToDestination(Node n, Node destination) {
        if (n.isSameRow(destination)) {
            return n.y - destination.y < 0 ? RIGHT : LEFT;
        } else if (n.isSameCol(destination)) {
            return n.x - destination.x < 0 ? BOTTOM : TOP;
        } else {
            return -1;
        }
    }

    private boolean canReachCenter(Node node, int value) {
        int xmin = node.x - value < 0 ? 0 : node.x - value;
        int xmax = node.x + value > n ? n : node.x + value;
        int ymin = node.y - value < 0 ? 0 : node.y - value;
        int ymax = node.y + value > n ? n : node.y + value;
        for (int i = xmin; i < xmax; i++) {
            if (board[i][node.y] instanceof Center) {
                return true;
            }
        }
        for (int i = ymin; i < ymax; i++) {
            if (board[node.x][i] instanceof Center) {
                return true;
            }
        }
        Log.e("HGQ", node.toInfoString());
        return false;
    }

    private void updateSource(Node shadow) {
        if (shadow.isFull()) {
            return;
        }
        if (shadow.dx == 1) {
            for (int i = shadow.x-1; i > 0; i--) {
                if (board[i][shadow.y] instanceof Step) {
                    board[i][shadow.y].value--;
                    return;
                }
            }
        } else if (shadow.dx == -1) {
            for (int i = shadow.x+1; i < n; i++) {
                if (board[i][shadow.y] instanceof Step) {
                    board[i][shadow.y].value--;
                    return;
                }
            }
        } else if (shadow.dy == 1) {
            for (int i = shadow.y-1; i > 0; i--) {
                if (board[shadow.x][i] instanceof Step) {
                    board[shadow.x][i].value--;
                    return;
                }
            }
        } else if (shadow.dy == -1) {
            for (int i = shadow.y+1; i < n; i++) {
                if (board[shadow.x][i] instanceof Step) {
                    board[shadow.x][i].value--;
                    return;
                }
            }
        }
    }

    @Override
    public String toString() {
        String s = "";
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                s += board[i][j].toString();
            }
            s += "\n";
        }
        return s;
    }
}
