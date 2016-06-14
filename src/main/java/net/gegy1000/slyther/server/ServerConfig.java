package net.gegy1000.slyther.server;

import net.gegy1000.slyther.game.Configuration;

public class ServerConfig implements Configuration {
    public int gameRadius = 21600;
    public int mscps = 411;
    public int sectorSize = 300;
    public int sectorsAlongEdge = 114;
    public float spangDv = 4.8F;
    public float nsp1 = 5.39F;
    public float nsp2 = 0.4F;
    public float nsp3 = 14.0F;
    public float snakeTurnSpeed = 0.033F;
    public float preyTurnSpeed = 0.028F;
    public float cst = 0.43F;
    public int serverPort = 444;
    public int leaderboardLength = 10;
    public long leaderboardUpdateFrequency = 5000;
    public int maxSpawnFoodPerSector = 10;
    public int minNaturalFoodSize = 5;
    public int maxNaturalFoodSize = 8;
    public long respawnFoodDelay = 10000;
}
