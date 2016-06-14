package net.gegy1000.slyther.server.game.entity;

import net.gegy1000.slyther.game.Color;
import net.gegy1000.slyther.game.entity.Food;
import net.gegy1000.slyther.game.entity.Sector;
import net.gegy1000.slyther.server.ConnectedClient;
import net.gegy1000.slyther.server.ServerConfig;
import net.gegy1000.slyther.server.SlytherServer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class ServerSector extends Sector<SlytherServer> {
    public List<Food> foods = new ArrayList<>();
    public long lastSpawnTime;
    public boolean populated;

    public ServerSector(SlytherServer game, int posX, int posY) {
        super(game, posX, posY);
    }

    public void populateSector() {
        int sectorSize = game.getSectorSize();
        int sectorX = posX * sectorSize;
        int sectorY = posY * sectorSize;
        Random rng = game.rng;
        ServerConfig configuration = game.configuration;
        for (int i = 0; i < rng.nextInt(configuration.maxSpawnFoodPerSector); i++) {
            int posX = sectorX + rng.nextInt(sectorSize);
            int posY = sectorY + rng.nextInt(sectorSize);
            int size = rng.nextInt(configuration.maxNaturalFoodSize - configuration.minNaturalFoodSize) + configuration.minNaturalFoodSize;
            Color color = Color.values()[rng.nextInt(9)];
            addFood(new ServerFood(game, posX, posY, size, true, color));
        }
        lastSpawnTime = System.currentTimeMillis();
    }

    @Override
    public void update(float delta, float lastDelta, float lastDelta2) {
        if (foods.size() < game.configuration.maxSpawnFoodPerSector) {
            if (System.currentTimeMillis() - lastSpawnTime > game.configuration.respawnFoodDelay) {
                int sectorSize = game.getSectorSize();
                int sectorX = posX * sectorSize;
                int sectorY = posY * sectorSize;
                int posX = sectorX + game.rng.nextInt(sectorSize);
                int posY = sectorY + game.rng.nextInt(sectorSize);
                int size = game.rng.nextInt(game.configuration.maxNaturalFoodSize - game.configuration.minNaturalFoodSize) + game.configuration.minNaturalFoodSize;
                Color color = Color.values()[game.rng.nextInt(Color.values().length)];
                addFood(new ServerFood(game, posX, posY, size, true, color));
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

    @Override
    public void startTracking(ConnectedClient tracker) {
        if (!populated) {
            populateSector();
            populated = true;
        }
        super.startTracking(tracker);
    }
}
