package net.gegy1000.slyther.server;

import net.gegy1000.slyther.game.Color;
import net.gegy1000.slyther.game.ConfigHandler;
import net.gegy1000.slyther.game.LeaderboardEntry;
import net.gegy1000.slyther.game.SnakePoint;
import net.gegy1000.slyther.network.message.MessageUpdateLeaderboard;
import net.gegy1000.slyther.server.game.Entity;
import net.gegy1000.slyther.server.game.Food;
import net.gegy1000.slyther.server.game.Sector;
import net.gegy1000.slyther.server.game.Snake;
import net.gegy1000.slyther.util.SystemUtils;
import org.java_websocket.WebSocket;

import java.io.File;
import java.net.UnknownHostException;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingDeque;

public class SlytherServer {
    public static final double PI_2 = Math.PI * 2;
    public ServerConfig configuration;

    public ServerNetworkManager networkManager;

    private static final File CONFIGURATION_FILE = new File(SystemUtils.getGameFolder(), "server/config.json");
    public List<ConnectedClient> clients = new ArrayList<>();

    private List<Entity> entities = new ArrayList<>();
    private List<Snake> snakes = new ArrayList<>();
    private List<Sector> sectors = new ArrayList<>();
    private List<Food> foods = new ArrayList<>();

    public List<LeaderboardEntry> leaderboard = new ArrayList<>();
    private long lastLeaderboardUpdateTime;

    private int currentSnakeId;

    private Queue<FutureTask<?>> tasks = new LinkedBlockingDeque<>();

    public final Random rng = new Random();

    public SlytherServer() {
        try {
            configuration = ConfigHandler.INSTANCE.readConfig(CONFIGURATION_FILE, ServerConfig.class);
            ConfigHandler.INSTANCE.saveConfig(CONFIGURATION_FILE, configuration);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            networkManager = new ServerNetworkManager(this, configuration.serverPort);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        int gameRadius = configuration.gameRadius;
        int sectorSize = configuration.sectorSize;
        for (int x = -gameRadius; x < gameRadius; x += sectorSize) {
            for (int y = -gameRadius; y < gameRadius; y += sectorSize) {
                Sector sector = new Sector(this, x / sectorSize, y / sectorSize);
                populateSector(sector);
                addEntity(sector);
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

    public void populateSector(Sector sector) {
        int sectorX = (int) (sector.posX * configuration.sectorSize);
        int sectorY = (int) (sector.posY * configuration.sectorSize);
        for (int i = 0; i < rng.nextInt(configuration.maxSpawnFoodPerSector); i++) {
            int posX = sectorX + rng.nextInt(configuration.sectorSize);
            int posY = sectorY + rng.nextInt(configuration.sectorSize);
            int size = rng.nextInt(configuration.maxNaturalFoodSize - configuration.minNaturalFoodSize) + configuration.minNaturalFoodSize;
            Color color = Color.values()[rng.nextInt(Color.values().length)];
            Food food = new Food(this, posX, posY, size, true, color);
            sector.addFood(food);
        }
        sector.lastSpawnTime = System.currentTimeMillis();
    }

    public void update() {
        runTasks();
        long time = System.currentTimeMillis();
        for (ConnectedClient client : clients) {
            client.update();
        }
        for (Snake snake : snakes) {
            snake.update();
        }
        for (Sector sector : sectors) {
            sector.update();
        }
        if (time - lastLeaderboardUpdateTime > configuration.leaderboardUpdateFrequency) {
            leaderboard.clear();
            List<Snake> biggestSnakes = new ArrayList<>(snakes);
            Collections.sort(biggestSnakes);
            int i = 0;
            for (Snake snake : biggestSnakes) {
                snake.client.rank = i + 1;
                i++;
            }
            biggestSnakes = biggestSnakes.subList(0, Math.min(configuration.leaderboardLength, biggestSnakes.size()));
            for (Snake snake : biggestSnakes) {
                leaderboard.add(new LeaderboardEntry(snake.client));
            }
            for (Snake snake : snakes) {
                snake.client.send(new MessageUpdateLeaderboard());
            }
            lastLeaderboardUpdateTime = time;
        }
    }

    public Snake createSnake(ConnectedClient client) {
        int spawnFuzz = configuration.gameRadius / 4;
        int posX = (rng.nextInt(spawnFuzz) - spawnFuzz / 2);
        int posY = (rng.nextInt(spawnFuzz) - spawnFuzz / 2);
        List<SnakePoint> points = new ArrayList<>();
        points.add(new SnakePoint(posX, posY));
        Snake snake = new Snake(this, currentSnakeId++, posX, posY, client, points);
        addEntity(snake);
        return snake;
    }

    public void removeClient(WebSocket socket) {
        scheduleTask(() -> {
            ConnectedClient client = getConnectedClient(socket);
            clients.remove(client);
            if (client != null) {
                System.out.println(client.name + " disconnected.");
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

    public void scheduleTask(Callable<?> callable) {
        tasks.add(new FutureTask<>(callable));
    }

    private void runTasks() {
        while (tasks.size() > 0) {
            FutureTask<?> task = tasks.poll();
            try {
                task.run();
                task.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public void addEntity(Entity entity) {
        if (!entities.contains(entity)) {
            entities.add(entity);
            if (entity instanceof Snake) {
                snakes.add((Snake) entity);
            } else if (entity instanceof Sector) {
                sectors.add((Sector) entity);
            } else if (entity instanceof Food) {
                foods.add((Food) entity);
            }
        }
    }

    public void removeEntity(Entity entity) {
        if (entities.remove(entity)) {
            if (entity instanceof Snake) {
                snakes.remove(entity);
            } else if (entity instanceof Sector) {
                sectors.remove(entity);
            } else if (entity instanceof Food) {
                foods.remove(entity);
            }
        }
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public List<Snake> getSnakes() {
        return snakes;
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
}
