package net.gegy1000.slyther.server.game;

import net.gegy1000.slyther.game.Color;
import net.gegy1000.slyther.network.message.MessageNewFood;
import net.gegy1000.slyther.network.message.MessageRemoveFood;
import net.gegy1000.slyther.server.ConnectedClient;
import net.gegy1000.slyther.server.SlytherServer;

public class Food extends Entity {
    public int size;
    public Color color;

    public boolean isNatural;
    public Snake eater;
    public boolean eaten;

    public Food(SlytherServer server, int posX, int posY, int size, boolean isNatural, Color color) {
        super(server, posX, posY);
        this.isNatural = isNatural;
        this.size = size;
        this.color = color;
    }

    public void update() {
    }

    @Override
    public boolean shouldTrack(ConnectedClient client) {
        float deltaX = posX - client.snake.posX;
        float deltaY = posY - client.snake.posY;
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY) <= client.viewDistance;
    }

    @Override
    public void startTracking(ConnectedClient tracker) {
        tracker.send(new MessageNewFood(this));
    }

    @Override
    public void stopTracking(ConnectedClient tracker) {
        tracker.send(new MessageRemoveFood(this));
    }
}
