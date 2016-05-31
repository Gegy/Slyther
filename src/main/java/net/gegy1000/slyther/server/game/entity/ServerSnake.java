package net.gegy1000.slyther.server.game.entity;

import net.gegy1000.slyther.client.game.entity.ClientSnake;
import net.gegy1000.slyther.game.entity.Snake;
import net.gegy1000.slyther.game.entity.SnakePoint;
import net.gegy1000.slyther.network.message.server.MessageUpdateSnake;
import net.gegy1000.slyther.network.message.server.MessageUpdateSnakePoints;
import net.gegy1000.slyther.server.ConnectedClient;
import net.gegy1000.slyther.server.SlytherServer;

import java.util.List;

public class ServerSnake extends Snake<SlytherServer> {
    public ConnectedClient client;

    public ServerSnake(SlytherServer game, ConnectedClient client, int id, float posX, float posY, float angle, List<SnakePoint> points) {
        super(game, client.name, id, posX, posY, angle, points);
    }

    @Override
    public boolean update(float delta, float lastDelta, float lastDelta2) {
        if (angle < 0 || angle >= SlytherServer.PI_2) {
            angle %= SlytherServer.PI_2;
        }
        if (angle < 0) {
            angle += SlytherServer.PI_2;
        }
        if (wantedAngle < 0 || angle >= SlytherServer.PI_2) {
            wantedAngle %= SlytherServer.PI_2;
        }
        if (wantedAngle < 0) {
            wantedAngle += SlytherServer.PI_2;
        }
        float moveX = (float) (Math.cos(angle));
        float moveY = (float) (Math.sin(angle));
        posX += moveX;
        posY += moveY;
        for (SnakePoint point : points) {
            point.posX += moveX;
            point.posY += moveY;
        }
        boolean angleChange = angle != prevAngle;
        boolean wantedAngleChange = wantedAngle != prevWantedAngle;
        boolean speedChange = speed != prevSpeed;
        boolean turnDirectionChange = turnDirection != prevTurnDirection;
        if (angleChange || wantedAngleChange || speedChange || turnDirectionChange) {
            prevAngle = angle;
            prevWantedAngle = wantedAngle;
            prevSpeed = speed;
            prevTurnDirection = turnDirection;
            for (ConnectedClient client : game.getTrackingClients(this)) {
                client.send(new MessageUpdateSnake(this, turnDirectionChange, angleChange, wantedAngleChange, speedChange));
            }
        }
        if (prevPointCount != points.size()) {
            for (ConnectedClient client : game.getTrackingClients(this)) {
                client.send(new MessageUpdateSnakePoints(this, false, false)); //TODO decide whether to use relative position or absolute position and choose incrementSct
            }
            prevPointCount = points.size();
        }
        speedTurnMultiplier = speed / game.getSpangDv();
        if (speedTurnMultiplier > 1.0F) {
            speedTurnMultiplier = 1.0F;
        }
        float turnSpeed = game.getMamu() * scaleTurnMultiplier * speedTurnMultiplier;
        if (angle > wantedAngle) {
            turnDirection = 1;
        } else if (angle < wantedAngle) {
            turnDirection = 2;
        }
        if (turnDirection == 1) {
            angle -= turnSpeed;
            if (angle < 0 || angle >= SlytherServer.PI_2) {
                angle %= SlytherServer.PI_2;
            }
            if (angle < 0) {
                angle += SlytherServer.PI_2;
            }
            float turnAmount = (float) ((wantedAngle - angle) % SlytherServer.PI_2);
            if (turnAmount < 0) {
                turnAmount += SlytherServer.PI_2;
            }
            if (turnAmount > Math.PI) {
                turnAmount -= SlytherServer.PI_2;
            }
            if (turnAmount > 0) {
                angle = wantedAngle;
                turnDirection = 0;
            }
        } else if (turnDirection == 2) {
            angle += turnSpeed;
            if (angle < 0 || angle >= SlytherServer.PI_2) {
                angle %= SlytherServer.PI_2;
            }
            if (angle < 0) {
                angle += SlytherServer.PI_2;
            }
            float turnAmount = (float) ((wantedAngle - angle) % SlytherServer.PI_2);
            if (turnAmount < 0) {
                turnAmount += SlytherServer.PI_2;
            }
            if (turnAmount > Math.PI) {
                turnAmount -= SlytherServer.PI_2;
            }
            if (turnAmount < 0) {
                angle = wantedAngle;
                turnDirection = 0;
            }
        } else {
            angle = wantedAngle;
        }
        for (SnakePoint point : points) {
            point.update();
        }
        return false;
    }
}
