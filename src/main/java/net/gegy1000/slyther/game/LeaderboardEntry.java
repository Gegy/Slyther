package net.gegy1000.slyther.game;

import net.gegy1000.slyther.server.ConnectedClient;

public class LeaderboardEntry {
    public String name;
    public int score;
    public Color color;
    public ConnectedClient client;
    public boolean player;

    public LeaderboardEntry(String name, int score, Color color, boolean player) {
        this.name = name;
        this.score = score;
        this.color = color;
        this.player = player;
    }

    public LeaderboardEntry(ConnectedClient client) {
        this.client = client;
    }

    @Override
    public String toString() {
        return name + " - " + score;
    }
}
