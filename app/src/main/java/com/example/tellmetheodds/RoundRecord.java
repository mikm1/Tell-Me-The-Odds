package com.example.tellmetheodds;

import java.util.ArrayList;

public class RoundRecord {

    public int getRoundNumber() {
        return roundNumber;
    }

    public int getInitiative() {
        return initiative;
    }

    public int[] getPointsP1() {
        return pointsP1;
    }

    public int[] getPointsP2() {
        return pointsP2;
    }

    public long getTimeElapsed() {
        return timeElapsed;
    }

    private final int roundNumber;
    private int initiative; //0 - not set, 1 - player 1, 2 - player 2
    private int[] pointsP1;
    private int[] pointsP2;
    private long timeElapsed;
    private ArrayList<ThrowRecord> diceThrows;

    public RoundRecord(int roundNumber) {
        this.roundNumber = roundNumber;
        this.initiative = 0;
        this.pointsP1 = new int[]{0,0}; // {combat, objectives}
        this.pointsP2 = new int[]{0,0};
        this.timeElapsed = 0;
        this.diceThrows = new ArrayList<>();
    }

    public RoundRecord(int roundNumber, int initiative, int[] pointsP1, int[] pointsP2,
                       long timeElapsed) {
        this.roundNumber = roundNumber;
        this.initiative = initiative;
        this.pointsP1 = pointsP1; // {combat, objectives}
        this.pointsP2 = pointsP2;
        this.timeElapsed = timeElapsed;
        this.diceThrows = new ArrayList<>();
    }

    public RoundRecord(int roundNumber, int initiative, int[] pointsP1, int[] pointsP2,
                       long timeElapsed, ArrayList<ThrowRecord> rolls) {
        this.roundNumber = roundNumber;
        this.initiative = initiative;
        this.pointsP1 = pointsP1; // {combat, objectives}
        this.pointsP2 = pointsP2;
        this.timeElapsed = timeElapsed;
        this.diceThrows = rolls;
    }

    public ArrayList<ThrowRecord> getRolls() {
        return diceThrows;
    }

    public void setThrows(ArrayList<ThrowRecord> diceThrows){
        this.diceThrows = diceThrows;
    }

    public void addThrow(ThrowRecord throwRecord){
        this.diceThrows.add(throwRecord);
    }

    public void setInitiative(int initiative){
        this.initiative = initiative;
    }

    public void setPointsP1(int[] pointsP1) {
        this.pointsP1 = pointsP1;
    }

    public void setPointsP2(int[] pointsP2) {
        this.pointsP2 = pointsP2;
    }

    public void setPoints(int[] pointsP1, int[] pointsP2){
        this.pointsP1 = pointsP1;
        this.pointsP2 = pointsP2;
    }

    public void setTimeElapsed(long timeElapsed) {
        this.timeElapsed = timeElapsed;
    }
}
