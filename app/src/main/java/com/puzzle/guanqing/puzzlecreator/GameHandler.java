package com.puzzle.guanqing.puzzlecreator;

import android.util.Log;
import android.widget.TextView;

public class GameHandler {

    public static Board generateSolution(final int size, int step) {
        Board b = new Board(size);
        Node node = b.getRandomEmpty(b.getCenter());
        Log.e("HGQ", "\n"+b.toString()+"\n==============================");
        for (int i = 1; i < step; i++) {
            if (node == null) {
                Log.e("HGQ", "node is null");
                return b;
            }
            node = b.getRandomEmpty();
            Log.e("HGQ", "\n"+b.toString()+"\n==============================");
        }
        return b;
    }
}
