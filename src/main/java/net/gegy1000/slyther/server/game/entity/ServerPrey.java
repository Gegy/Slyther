package net.gegy1000.slyther.server.game.entity;

import net.gegy1000.slyther.game.Color;
import net.gegy1000.slyther.game.entity.Prey;
import net.gegy1000.slyther.server.SlytherServer;

public class ServerPrey extends Prey<SlytherServer> {
    public ServerPrey(SlytherServer game, int id, float posX, float posY, float size, Color color, int turningDirection, float wantedAngle, float angle, float speed) {
        super(game, id, posX, posY, size, color, turningDirection, wantedAngle, angle, speed);
    }

    @Override
    public boolean update(float delta, float lastDelta, float lastDelta2) {
        return false;
    }
}
