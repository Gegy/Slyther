package net.gegy1000.slyther.game.entity;

import net.gegy1000.slyther.game.Color;
import net.gegy1000.slyther.game.Game;
import net.gegy1000.slyther.network.message.server.MessageNewFood;
import net.gegy1000.slyther.network.message.server.MessageRemoveFood;
import net.gegy1000.slyther.server.ConnectedClient;

public abstract class Food<GME extends Game<?, ?, ?, ?, ?, ?>> extends Entity<GME> {
    public int id;
    public Color color;
    public float size;
    public boolean eaten;
    public int sectorX;
    public int sectorY;
    public boolean isNatural;

    public Food(GME game, int posX, int posY, float size, boolean isNatural, Color color) {
        super(game, posX, posY);
        this.posX = posX;
        this.posY = posY;
        this.isNatural = isNatural;
        this.color = color;
        this.size = size;
        id = posY * game.getGameRadius() * 3 + posX;
        sectorX = (int) Math.floor(posX / game.getSectorSize());
        sectorY = (int) Math.floor(posY / game.getSectorSize());
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Food && id == ((Food) object).id;
    }

    @Override
    public void startTracking(ConnectedClient tracker) {
    }

    @Override
    public void stopTracking(ConnectedClient tracker) {
    }
}
