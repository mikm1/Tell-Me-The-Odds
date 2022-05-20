package com.example.tellmetheodds;

import androidx.annotation.NonNull;

import java.util.Arrays;

public class ThrowRecord {
    private final int player; // 0 - unknown, 1 - p1, 2 - p2
    private final int[] results; // red throw {hits,crits,eyes,blanks}, green throw {evades,eyes,blanks}
    private final boolean isRedDieThrow;

    public int getPlayer() {
        return player;
    }

    public int[] getResults() {
        return results;
    }

    public boolean isRedDieThrow() {
        return isRedDieThrow;
    }

    public ThrowRecord(int player, int[] results, boolean isRedDieThrow){
        this.player = player;
        this.results = results;
        this.isRedDieThrow = isRedDieThrow;
    }

    @NonNull
    @Override
    public String toString() {
        String string = "";
        if (isRedDieThrow){
            string = string + "(r) ";
            if(results[0] > 0){
                string = string + results[0] + "x hit; ";
            }
            if (results[1] > 0){
                string = string + results[1] + "x crit; ";
            }
            if (results[2] > 0){
                string = string + results[2] + "x focus; ";
            }
            if (results[3] > 0){
                string = string + results[3] + "x blank; ";
            }
        } else {
            string = string + "(g) ";
            if(results[0] > 0){
                string = string + results[0] + "x evade; ";
            }
            if (results[1] > 0){
                string = string + results[1] + "x focus; ";
            }
            if (results[2] > 0){
                string = string + results[2] + "x blank; ";
            }
        }
        return string;
    }
}
