package net.gegy1000.slyther.server;

import net.gegy1000.slyther.game.*;
import net.gegy1000.slyther.game.entity.*;
import net.gegy1000.slyther.network.message.server.MessageUpdateLeaderboard;
import net.gegy1000.slyther.server.game.entity.ServerFood;
import net.gegy1000.slyther.server.game.entity.ServerPrey;
import net.gegy1000.slyther.server.game.entity.ServerSector;
import net.gegy1000.slyther.server.game.entity.ServerSnake;
import net.gegy1000.slyther.util.Log;
import net.gegy1000.slyther.util.SystemUtils;

import org.java_websocket.WebSocket;

import java.io.File;
import java.net.UnknownHostException;
import java.util.*;

public class SlytherServer extends Game<ServerNetworkManager, ServerConfig, ServerSnake, ServerSector, ServerFood, ServerPrey> {
    public static final double PI_2 = Math.PI * 2;

    private static final File CONFIGURATION_FILE = new File(SystemUtils.getGameFolder(), "server/config.json");
    public List<ConnectedClient> clients = new ArrayList<>();

    private long lastLeaderboardUpdateTime;

    private int currentSnakeId;

    private float[] fpsls;
    private float[] fmlts;

    public final Random rng = new Random();
    private long lastTickTime;

    public float lastTicks;
    public float ticks;
    public float lastTicks2;
    public float ticks2;

    public SlytherServer() {
        try {
            configuration = ConfigHandler.INSTANCE.readConfig(CONFIGURATION_FILE, ServerConfig.class);
            ConfigHandler.INSTANCE.saveConfig(CONFIGURATION_FILE, configuration);
        } catch (Exception e) {
            Log.catching(e);
        }
        try {
            networkManager = new ServerNetworkManager(this, configuration.serverPort);
        } catch (UnknownHostException e) {
            Log.catching(e);
        }
        int mscps = getMSCPS();
        fmlts = new float[mscps];
        fpsls = new float[mscps + 1];
        for (int i = 0; i <= mscps; i++) {
            if (i < mscps) {
                fmlts[i] = (float) Math.pow(1.0F - i / (float) mscps, 2.25F);
            }
            if (i != 0) {
                fpsls[i] = fpsls[i - 1] + 1.0F / fmlts[i - 1];
            }
        }
        int gameRadius = configuration.gameRadius;
        int sectorSize = configuration.sectorSize;
        for (int x = -gameRadius; x < gameRadius; x += sectorSize) {
            for (int y = -gameRadius; y < gameRadius; y += sectorSize) {
                ServerSector sector = new ServerSector(this, x / sectorSize, y / sectorSize);
                populateSector(sector);
                addSector(sector);
            }
        }
        double delta = 0;
        long previousTime = System.nanoTime();
        long timer = System.currentTimeMillis();
        double nanoUpdates = 1000000000.0 / 60.0;
        while (true) {
            long currentTime = System.nanoTime();
            delta += (currentTime - previousTime) / nanoUpdates;
            previousTime = currentTime;
            while (delta >= 1) {
                update();
                delta--;
            }
            if (System.currentTimeMillis() - timer > 1000) {
                timer += 1000;
            }
        }
    }

    public void populateSector(ServerSector sector) {
        int sectorX = sector.posX * configuration.sectorSize;
        int sectorY = sector.posY * configuration.sectorSize;
        for (int i = 0; i < rng.nextInt(configuration.maxSpawnFoodPerSector); i++) {
            int posX = sectorX + rng.nextInt(configuration.sectorSize);
            int posY = sectorY + rng.nextInt(configuration.sectorSize);
            int size = rng.nextInt(configuration.maxNaturalFoodSize - configuration.minNaturalFoodSize) + configuration.minNaturalFoodSize;
            Color color = Color.values()[rng.nextInt(Color.values().length)];
            sector.addFood(new ServerFood(this, posX, posY, size, true, color));
        }
        sector.lastSpawnTime = System.currentTimeMillis();
    }

    public void update() {
        runTasks();
        long time = System.currentTimeMillis();
        float lastDelta, lastDelta2;
        float delta = (time - lastTickTime) / 8;
        lastTickTime = time;
        lastTicks = ticks;
        ticks += delta;
        lastDelta = (float) (Math.floor(ticks) - Math.floor(lastTicks));
        lastTicks2 = ticks2;
        ticks2 += delta * 2;
        lastDelta2 = (float) (Math.floor(ticks2) - Math.floor(lastTicks2));
        for (ConnectedClient client : clients) {
            client.update();
        }
        for (Entity entity : getEntities()) {
            entity.update(delta, lastDelta, lastDelta2);
        }
        if (time - lastLeaderboardUpdateTime > configuration.leaderboardUpdateFrequency) {
            leaderboard.clear();
            List<Snake> biggestSnakes = new ArrayList<>(getSnakes());
            Collections.sort(biggestSnakes);
            int i = 0;
            for (Snake snake : biggestSnakes) {
                if (snake instanceof ServerSnake) {
                    ServerSnake serverSnake = (ServerSnake) snake;
                    serverSnake.client.rank = i + 1;
                }
                i++;
            }
            biggestSnakes = biggestSnakes.subList(0, Math.min(configuration.leaderboardLength, biggestSnakes.size()));
            for (Snake snake : biggestSnakes) {
                if (snake instanceof ServerSnake) {
                    ServerSnake serverSnake = (ServerSnake) snake;
                    leaderboard.add(new LeaderboardEntry(serverSnake.client));
                }
            }
            for (ConnectedClient client : clients) {
                client.send(new MessageUpdateLeaderboard());
            }
            lastLeaderboardUpdateTime = time;
        }
    }

    public ServerSnake createSnake(ConnectedClient client) {
        int spawnFuzz = configuration.gameRadius / 4;
        int posX = (rng.nextInt(spawnFuzz) - spawnFuzz / 2);
        int posY = (rng.nextInt(spawnFuzz) - spawnFuzz / 2);
        List<SnakePoint> points = new ArrayList<>();
        points.add(new SnakePoint(posX, posY));
        ServerSnake snake = new ServerSnake(this, client, currentSnakeId++, posX, posY, 0.0F, points);
        snake.client = client;
        addEntity(snake);
        return snake;
    }

    public void removeClient(WebSocket socket) {
        scheduleTask(() -> {
            ConnectedClient client = getConnectedClient(socket);
            clients.remove(client);
            if (client != null) {
                Log.info("{} disconnected.", client.name);
                removeEntity(client.snake);
            }
            return null;
        });
    }

    public ConnectedClient getConnectedClient(WebSocket socket) {
        for (ConnectedClient client : clients) {
            if (client.socket.equals(socket)) {
                return client;
            }
        }
        return null;
    }

    public List<ConnectedClient> getTrackingClients(Entity entity) {
        List<ConnectedClient> tracking = new ArrayList<>();
        for (ConnectedClient client : clients) {
            if (client.tracking.contains(entity)) {
                tracking.add(client);
            }
        }
        return tracking;
    }

    @Override
    public int getGameRadius() {
        return configuration.gameRadius;
    }

    @Override
    public int getMSCPS() {
        return configuration.mscps;
    }

    @Override
    public int getSectorSize() {
        return configuration.sectorSize;
    }

    @Override
    public int getSectorsAlongEdge() {
        return configuration.sectorsAlongEdge;
    }

    @Override
    public float getSpangDv() {
        return configuration.spangDv;
    }

    @Override
    public float getNsp1() {
        return configuration.nsp1;
    }

    @Override
    public float getNsp2() {
        return configuration.nsp2;
    }

    @Override
    public float getNsp3() {
        return configuration.nsp3;
    }

    @Override
    public float getMamu() {
        return configuration.mamu;
    }

    @Override
    public float getMamu2() {
        return configuration.mamu2;
    }

    @Override
    public float getCST() {
        return configuration.cst;
    }

    @Override
    public float getFMLT(int i) {
        return fmlts[Math.min(i, fmlts.length - 1)];
    }

    @Override
    public float getFPSL(int i) {
        return fpsls[Math.min(i, fpsls.length - 1)];
    }
}
