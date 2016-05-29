package net.gegy1000.slyther.game.entity;

import net.gegy1000.slyther.game.Color;
import net.gegy1000.slyther.game.Game;
import net.gegy1000.slyther.network.message.server.MessageAddSector;
import net.gegy1000.slyther.network.message.server.MessagePopulateSector;
import net.gegy1000.slyther.network.message.server.MessageRemoveSector;
import net.gegy1000.slyther.server.ConnectedClient;
import net.gegy1000.slyther.server.SlytherServer;

import java.util.ArrayList;
import java.util.List;

public class Sector {
    public Game<?, ?> game;
    public int posX;
    public int posY;
    public List<Food> foods = new ArrayList<>();
    public long lastSpawnTime;

    public Sector(Game<?, ?> game, int posX, int posY) {
        this.game = game;
        this.posX = posX;
        this.posY = posY;
    }

    public void updateServer() {
        SlytherServer server = (SlytherServer) game;
        for (Food food : foods) {
            food.updateServer();
        }
        if (foods.size() < server.configuration.maxSpawnFoodPerSector) {
            if (System.currentTimeMillis() - lastSpawnTime > server.configuration.respawnFoodDelay) {
                int sectorSize = game.getSectorSize();
                int sectorX = posX * sectorSize;
                int sectorY = posY * sectorSize;
                int posX = sectorX + server.rng.nextInt(sectorSize);
                int posY = sectorY + server.rng.nextInt(sectorSize);
                int size = server.rng.nextInt(server.configuration.maxNaturalFoodSize - server.configuration.minNaturalFoodSize) + server.configuration.minNaturalFoodSize;
                Color color = Color.values()[server.rng.nextInt(Color.values().length)];
                Food food = new Food(game, posX, posY, size, true, color);
                addFood(food);
            }
        }
    }

    public boolean shouldTrack(ConnectedClient client) {
        int sectorSize = ((SlytherServer) game).configuration.sectorSize;
        float deltaX = posX * sectorSize + sectorSize / 2.0F - client.snake.posX;
        float deltaY = posY * sectorSize + sectorSize / 2.0F - client.snake.posY;
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY) <= client.viewDistance;
    }

    public void startTracking(ConnectedClient tracker) {
        tracker.send(new MessageAddSector(this));
        tracker.send(new MessagePopulateSector(this));
        for (Entity entity : game.getEntities()) {
            int sectorX = (int) (entity.posX / game.getSectorSize());
            int sectorY = (int) (entity.posY / game.getSectorSize());
            if (sectorX == posX && sectorY == posY) {
                if (entity instanceof Food) {
                    tracker.tracking.add(entity);
                } else {
                    tracker.track(entity);
                }
            }
        }
    }

    public void stopTracking(ConnectedClient tracker) {
        tracker.send(new MessageRemoveSector(this));
        for (Entity entity : game.getEntities()) {
            int sectorX = (int) (entity.posX / game.getSectorSize());
            int sectorY = (int) (entity.posY / game.getSectorSize());
            if (sectorX == posX && sectorY == posY) {
                if (entity instanceof Food) {
                    tracker.tracking.remove(entity);
                } else {
                    tracker.untrack(entity);
                }
            }
        }
    }

    public void addFood(Food food) {
        if (!foods.contains(food)) {
            foods.add(food);
            game.addEntity(food);
        }
    }

    public void removeFood(Food food) {
        foods.remove(food);
        game.removeEntity(food);
    }
}
