package net.gegy1000.slyther.game;

public class LeaderboardEntry {
    public String name;
    public int score;
    public Color color;

    public LeaderboardEntry(String name, int score, Color color) {
        this.name = name;
        this.score = score;
        this.color = color;
    }

    @Override
    public String toString() {
        return this.name + " - " + this.score;
    }
}
