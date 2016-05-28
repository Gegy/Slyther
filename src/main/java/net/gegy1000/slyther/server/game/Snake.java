package net.gegy1000.slyther.server.game;

import net.gegy1000.slyther.game.SnakePoint;
import net.gegy1000.slyther.network.message.MessageNewSnake;
import net.gegy1000.slyther.network.message.MessageUpdateSnake;
import net.gegy1000.slyther.network.message.MessageUpdateSnakePoints;
import net.gegy1000.slyther.server.ConnectedClient;
import net.gegy1000.slyther.server.SlytherServer;

import java.util.List;

public class Snake extends Entity implements Comparable<Snake> {
    public int id;
    public ConnectedClient client;
    public float ang;
    public float prevAng;
    public float wang;
    public float prevWang;
    public float sp;
    public float prevSp;
    public double fam;
    public List<SnakePoint> points;
    public boolean accelerating;
    public int turnDirection;
    public int prevTurnDirection;
    public float scang;
    public float spang;
    public boolean dying;
    public int prevPointCount;

    public Snake(SlytherServer server, int id, float posX, float posY, ConnectedClient client, List<SnakePoint> points) {
        super(server, posX, posY);
        this.id = id;
        this.client = client;
        this.points = points;
    }

    public void update() {
        if (ang < 0 || ang >= SlytherServer.PI_2) {
            ang %= SlytherServer.PI_2;
        }
        if (ang < 0) {
            ang += SlytherServer.PI_2;
        }
        if (wang < 0 || ang >= SlytherServer.PI_2) {
            wang %= SlytherServer.PI_2;
        }
        if (wang < 0) {
            wang += SlytherServer.PI_2;
        }
        float moveX = (float) (Math.cos(ang));
        float moveY = (float) (Math.sin(ang));
        posX += moveX;
        posY += moveY;
        for (SnakePoint point : points) {
            point.posX += moveX;
            point.posY += moveY;
        }
        boolean angChange = ang != prevAng;
        boolean wangChange = wang != prevWang;
        boolean spChange = sp != prevSp;
        boolean turnDirectionChange = turnDirection != prevTurnDirection;
        if (angChange || wangChange || spChange || turnDirectionChange) {
            prevAng = ang;
            prevWang = wang;
            prevSp = sp;
            prevTurnDirection = turnDirection;
            for (ConnectedClient client : server.getTrackingClients(this)) {
                client.send(new MessageUpdateSnake(this, turnDirectionChange, angChange, wangChange, spChange));
            }
        }
        if (prevPointCount != points.size()) {
            for (ConnectedClient client : server.getTrackingClients(this)) {
                client.send(new MessageUpdateSnakePoints(this, false)); //TODO decide whether to use relative position or absolute position
            }
            prevPointCount = points.size();
        }
        spang = sp / server.configuration.spangDv;
        if (spang > 1.0F) {
            spang = 1.0F;
        }
        float turnSpeed = server.configuration.mamu * scang * spang;
        if (ang > wang) {
            turnDirection = 1;
        } else if (ang < wang) {
            turnDirection = 2;
        }
        if (turnDirection == 1) {
            ang -= turnSpeed;
            if (ang < 0 || ang >= SlytherServer.PI_2) {
                ang %= SlytherServer.PI_2;
            }
            if (ang < 0) {
                ang += SlytherServer.PI_2;
            }
            float turnAmount = (float) ((wang - ang) % SlytherServer.PI_2);
            if (turnAmount < 0) {
                turnAmount += SlytherServer.PI_2;
            }
            if (turnAmount > Math.PI) {
                turnAmount -= SlytherServer.PI_2;
            }
            if (turnAmount > 0) {
                ang = wang;
                turnDirection = 0;
            }
        } else if (turnDirection == 2) {
            ang += turnSpeed;
            if (ang < 0 || ang >= SlytherServer.PI_2) {
                ang %= SlytherServer.PI_2;
            }
            if (ang < 0) {
                ang += SlytherServer.PI_2;
            }
            float turnAmount = (float) ((wang - ang) % SlytherServer.PI_2);
            if (turnAmount < 0) {
                turnAmount += SlytherServer.PI_2;
            }
            if (turnAmount > Math.PI) {
                turnAmount -= SlytherServer.PI_2;
            }
            if (turnAmount < 0) {
                ang = wang;
                turnDirection = 0;
            }
        } else {
            ang = wang;
        }
        for (SnakePoint point : points) {
            point.update();
        }
    }

    @Override
    public int compareTo(Snake snake) {
        return Double.compare(fam, snake.fam);
    }

    @Override
    public boolean shouldTrack(ConnectedClient client) {
        for (SnakePoint point : points) {
            if (point.shouldTrack(client)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void startTracking(ConnectedClient tracker) {
        tracker.send(new MessageNewSnake(this));
    }

    @Override
    public void stopTracking(ConnectedClient tracker) {
        tracker.send(new MessageNewSnake(this, false));
    }
}
