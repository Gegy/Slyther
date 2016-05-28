package net.gegy1000.slyther.server.game;

import net.gegy1000.slyther.game.Color;
import net.gegy1000.slyther.network.message.MessageAddSector;
import net.gegy1000.slyther.network.message.MessageRemoveSector;
import net.gegy1000.slyther.network.message.MessageUpdateSectorFoods;
import net.gegy1000.slyther.server.ConnectedClient;
import net.gegy1000.slyther.server.SlytherServer;

import java.util.ArrayList;
import java.util.List;

public class Sector extends Entity {
    public List<Food> foods = new ArrayList<>();
    public long lastSpawnTime;

    public Sector(SlytherServer server, float posX, float posY) {
        super(server, posX, posY);
    }

    public void update() {
        for (Food food : foods) {
            food.update();
        }
        if (foods.size() < server.configuration.maxSpawnFoodPerSector) {
            if (System.currentTimeMillis() - lastSpawnTime > server.configuration.respawnFoodDelay) {
                int sectorSize = server.configuration.sectorSize;
                int sectorX = (int) (posX * sectorSize);
                int sectorY = (int) (posY * sectorSize);
                int posX = sectorX + server.rng.nextInt(sectorSize);
                int posY = sectorY + server.rng.nextInt(sectorSize);
                int size = server.rng.nextInt(server.configuration.maxNaturalFoodSize - server.configuration.minNaturalFoodSize) + server.configuration.minNaturalFoodSize;
                Color color = Color.values()[server.rng.nextInt(Color.values().length)];
                Food food = new Food(server, posX, posY, size, true, color);
                foods.add(food);
            }
        }
    }

    @Override
    public boolean shouldTrack(ConnectedClient client) {
        int sectorSize = server.configuration.sectorSize;
        float deltaX = posX * sectorSize + sectorSize / 2.0F - client.snake.posX;
        float deltaY = posY * sectorSize + sectorSize / 2.0F - client.snake.posY;
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY) <= client.viewDistance;
    }

    @Override
    public void startTracking(ConnectedClient tracker) {
        tracker.send(new MessageAddSector(this));
        tracker.send(new MessageUpdateSectorFoods(this));
        for (Food food : foods) {
            tracker.tracking.add(food);
        }
    }

    @Override
    public void stopTracking(ConnectedClient tracker) {
        tracker.send(new MessageRemoveSector(this));
    }

    public void addFood(Food food) {
        if (!foods.contains(food)) {
            foods.add(food);
            server.addEntity(food);
        }
    }

    public void removeFood(Food food) {
        foods.remove(food);
        server.removeEntity(food);
    }
}
