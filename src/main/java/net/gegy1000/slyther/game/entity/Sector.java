package net.gegy1000.slyther.game.entity;

import net.gegy1000.slyther.game.Game;
import net.gegy1000.slyther.network.message.server.MessageAddSector;
import net.gegy1000.slyther.network.message.server.MessagePopulateSector;
import net.gegy1000.slyther.network.message.server.MessageRemoveSector;
import net.gegy1000.slyther.server.ConnectedClient;
import net.gegy1000.slyther.server.SlytherServer;

public abstract class Sector<GME extends Game<?, ?>> {
    public GME game;
    public int posX;
    public int posY;

    public Sector(GME game, int posX, int posY) {
        this.game = game;
        this.posX = posX;
        this.posY = posY;
    }

    public abstract void update(float delta, float lastDelta, float lastDelta2);

    public boolean shouldTrack(ConnectedClient client) {
        int sectorSize = ((SlytherServer) game).configuration.sectorSize;
        float deltaX = (posX * sectorSize) + (sectorSize / 2.0F) - client.snake.posX;
        float deltaY = (posY * sectorSize) + (sectorSize / 2.0F) - client.snake.posY;
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
                    tracker.trackingEntities.add(entity);
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
                    tracker.trackingEntities.remove(entity);
                } else {
                    tracker.untrack(entity);
                }
            }
        }
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Sector && ((Sector) o).posX == posX && ((Sector) o).posY == posY;
    }
}
