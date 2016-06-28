package net.gegy1000.slyther.server.game.entity;

import net.gegy1000.slyther.game.entity.Food;
import net.gegy1000.slyther.game.entity.Snake;
import net.gegy1000.slyther.game.entity.SnakePoint;
import net.gegy1000.slyther.network.message.server.MessageSnakeMovement;
import net.gegy1000.slyther.network.message.server.MessageUpdateSnake;
import net.gegy1000.slyther.network.message.server.MessageUpdateSnakeLength;
import net.gegy1000.slyther.server.ConnectedClient;
import net.gegy1000.slyther.server.SlytherServer;

import java.util.List;

public class ServerSnake extends Snake<SlytherServer> {
    private static final double UPDATE_FREQUENCY = 1000000000.0 / 10.0;
    private static final double MOVEMENT_FREQUENCY = 1000000000.0 / 4.0;

    public ConnectedClient client;
    private int lengthIncrements;
    private int absolute;

    private double updateDelta;
    private double movementDelta;

    private long previousTime;

    public ServerSnake(SlytherServer game, ConnectedClient client, int id, float posX, float posY, float angle, List<SnakePoint> points) {
        super(game, client.name, id, posX, posY, client.skin, angle, points);
        previousTime = System.nanoTime();
    }

    @Override
    public boolean update(float delta, float lastDelta, float lastDelta2) {
        long time = System.nanoTime();
        scale = Math.min(6.0F, (sct - 2.0F) / 106.0F + 1.0F);
        scaleTurnMultiplier = (float) (Math.pow((7.0F - scale) / 6.0F, 2.0F) * 0.87F + 0.13F);
        moveSpeed = game.getNsp1() + game.getNsp2() * scale;
        accelleratingSpeed = moveSpeed * 2.0F;
        speed = accelerating ? accelleratingSpeed : moveSpeed;
        angle %= SlytherServer.PI_2;
        wantedAngle %= SlytherServer.PI_2;
        if (angle < 0) {
            angle += SlytherServer.PI_2;
        }
        if (wantedAngle < 0) {
            wantedAngle += SlytherServer.PI_2;
        }
        float moveX = (float) (Math.cos(angle) * speed * delta / 4.0F);
        float moveY = (float) (Math.sin(angle) * speed * delta / 4.0F);
        posX += moveX;
        posY += moveY;
        updateDelta += (time - previousTime) / UPDATE_FREQUENCY;
        if (updateDelta >= 1) {
            boolean angleChange = angle != prevAngle;
            boolean wantedAngleChange = wantedAngle != prevWantedAngle;
            boolean speedChange = speed != prevSpeed;
            boolean turnDirectionChange = turnDirection != prevTurnDirection;
            prevAngle = angle;
            prevWantedAngle = wantedAngle;
            prevSpeed = speed;
            prevTurnDirection = turnDirection;
            for (ConnectedClient client : game.getTrackingClients(this)) {
                client.send(new MessageUpdateSnake(this, turnDirectionChange, angleChange, wantedAngleChange, speedChange));
            }
            updateDelta %= 1;
        }
        boolean eaten = false;
        float eatDist = scale * 60.0F;
        for (Food<?> food : game.getFoods()) {
            float deltaX = posX - food.posX;
            float deltaY = posY - food.posY;
            float deltaPos = (float) Math.sqrt(deltaX * deltaX + deltaY * deltaY);
            if (deltaPos < eatDist) {
                food.eaten = true;
                food.eater = this;
                for (ConnectedClient client : game.getTrackingClients(food)) {
                    food.stopTracking(client);
                }
                eaten = true;
            }
        }
        movementDelta += (time - previousTime) / MOVEMENT_FREQUENCY;
        while (movementDelta >= 1) {
            while (fam > game.getFMLT(sct)) {
                fam -= game.getFMLT(sct);
                sct++;
                lengthIncrements++;
            }
            absolute++;
            boolean absolutePosition = absolute > 8;
            if (absolutePosition) {
                absolute = 0;
            }
            boolean lengthIncrement = lengthIncrements > 0;
            if (lengthIncrement) {
                lengthIncrements--;
            }
            for (ConnectedClient client : game.getTrackingClients(this)) {
                client.send(new MessageSnakeMovement(this, absolutePosition, lengthIncrement));
            }
            if (eaten && !lengthIncrement) {
                for (ConnectedClient client : game.getTrackingClients(this)) {
                    client.send(new MessageUpdateSnakeLength(this));
                }
            }
            if (!lengthIncrement) {
                points.remove(points.size() - 1);
                points.add(new SnakePoint(game, posX, posY));
            }
            movementDelta--;
        }
        speedTurnMultiplier = speed / game.getSpangDv();
        if (speedTurnMultiplier > 1.0F) {
            speedTurnMultiplier = 1.0F;
        }
        float turnSpeed = game.getBaseSnakeTurnSpeed() * scaleTurnMultiplier * speedTurnMultiplier;
        if (angle != wantedAngle) {
            turnDirection = angle > wantedAngle ? 2 : 1;
        } else {
            turnDirection = 0;
        }
        angle = wantedAngle;
        for (SnakePoint point : points) {
            point.update();
        }
        previousTime = time;
        return false;
    }
}
