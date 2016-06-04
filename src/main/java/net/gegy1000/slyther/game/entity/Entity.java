package net.gegy1000.slyther.game.entity;

import net.gegy1000.slyther.game.Game;
import net.gegy1000.slyther.server.ConnectedClient;

public abstract class Entity<GME extends Game<?, ?>> {
    public GME game;
    public float posX;
    public float posY;
    public float prevPosX;
    public float prevPosY;
    public int previousSectorX;
    public int previousSectorY;

    public Entity(GME game, float posX, float posY) {
        this.game = game;
        this.posX = posX;
        this.posY = posY;
    }

    public final boolean updateBase(float delta, float lastDelta, float lastDelta2) {
        prevPosX = posX;
        prevPosY = posY;
        return update(delta, lastDelta, lastDelta2);
    }

    public void updateTrackers(ConnectedClient client) {
        int sectorX = (int) (posX / game.getSectorSize());
        int sectorY = (int) (posY / game.getSectorSize());
        if (sectorX != previousSectorX || sectorY != previousSectorY) {
            Sector previousSector = null;
            Sector newSector = null;
            for (Sector sector : client.trackingSectors) {
                if (shouldTrack(sector, sectorX, sectorY)) {
                    newSector = sector;
                } else if (shouldTrack(sector, previousSectorX, previousSectorY)) {
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

    public boolean shouldTrack(Sector sector, int sectorX, int sectorY) {
        return sectorX == sector.posX && sectorY == sector.posY;
    }

    public float getRenderX(double frameDelta) {
        return (float) (prevPosX + frameDelta * (posX - prevPosX));
    }

    public float getRenderY(double frameDelta) {
        return (float) (prevPosY + frameDelta * (posY - prevPosY));
    }

    public abstract void startTracking(ConnectedClient tracker);

    public abstract void stopTracking(ConnectedClient tracker);

    public abstract boolean update(float delta, float lastDelta, float lastDelta2);
}
