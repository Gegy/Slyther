package net.gegy1000.slyther.game.entity;

import net.gegy1000.slyther.game.Game;
import net.gegy1000.slyther.server.ConnectedClient;

public abstract class Entity {
    public Game<?, ?> game;
    public float posX;
    public float posY;
    public int previousSectorX;
    public int previousSectorY;

    public Entity(Game<?, ?> game, float posX, float posY) {
        this.game = game;
        this.posX = posX;
        this.posY = posY;
    }

    public void updateTrackers(ConnectedClient client) {
        int sectorX = (int) (posX / game.getSectorSize());
        int sectorY = (int) (posY / game.getSectorSize());
        if (sectorX != previousSectorX || sectorY != previousSectorY) {
            Sector previousSector = null;
            Sector newSector = null;
            for (Sector sector : client.trackingSectors) {
                if (sector.posX == sectorX && sector.posY == sectorY) {
                    newSector = sector;
                } else if (sector.posX == previousSectorX && sector.posY == previousSectorY) {
                    previousSector = sector;
                }
            }
            if (previousSector == null && newSector != null) {
                client.track(this);
            } else if (previousSector != null && newSector == null) {
                client.untrack(this);
            }
            previousSectorX = sectorX;
            previousSectorY = sectorY;
        }
    }

    public abstract void startTracking(ConnectedClient tracker);
    public abstract void stopTracking(ConnectedClient tracker);

    public abstract void updateServer();

    public abstract boolean updateClient(float delta, float lastDelta, float lastDelta2);
}
