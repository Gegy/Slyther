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

public class Sector extends Entity {
    public List<Food> foods = new ArrayList<>();
    public long lastSpawnTime;

    public Sector(Game game, float posX, float posY) {
        super(game, posX, posY);
    }

    @Override
    public void updateClient(float vfr, float vfrb, float vfrb2) {

    }

    public void updateServer() {
        SlytherServer server = (SlytherServer) game;
        for (Food food : foods) {
            food.updateServer();
        }
        if (foods.size() < server.configuration.maxSpawnFoodPerSector) {
            if (System.currentTimeMillis() - lastSpawnTime > server.configuration.respawnFoodDelay) {
                int sectorSize = game.getSectorSize();
                int sectorX = (int) (posX * sectorSize);
                int sectorY = (int) (posY * sectorSize);
                int posX = sectorX + server.rng.nextInt(sectorSize);
                int posY = sectorY + server.rng.nextInt(sectorSize);
                int size = server.rng.nextInt(server.configuration.maxNaturalFoodSize - server.configuration.minNaturalFoodSize) + server.configuration.minNaturalFoodSize;
                Color color = Color.values()[server.rng.nextInt(Color.values().length)];
                Food food = new Food(game, posX, posY, size, true, color);
                addFood(food);
            }
        }
    }

    @Override
    public boolean shouldTrack(ConnectedClient client) {
        int sectorSize = ((SlytherServer) game).configuration.sectorSize;
        float deltaX = posX * sectorSize + sectorSize / 2.0F - client.snake.posX;
        float deltaY = posY * sectorSize + sectorSize / 2.0F - client.snake.posY;
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY) <= client.viewDistance;
    }

    @Override
    public void startTracking(ConnectedClient tracker) {
        tracker.send(new MessageAddSector(this));
        tracker.send(new MessagePopulateSector(this));
        for (Food food : foods) {
            tracker.tracking.add(food);
        }
    }

    @Override
    public void stopTracking(ConnectedClient tracker) {
        tracker.send(new MessageRemoveSector(this));
    }

    @Override
    public void addChildren() {
        for (Food food : foods) {
            game.addEntity(food);
        }
    }

    @Override
    public void removeChildren() {
        for (Food food : foods) {
            game.removeEntity(food);
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
