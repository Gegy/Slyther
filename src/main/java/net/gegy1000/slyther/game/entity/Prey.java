package net.gegy1000.slyther.game.entity;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.game.Color;
import net.gegy1000.slyther.game.Game;
import net.gegy1000.slyther.network.message.server.MessageNewFood;
import net.gegy1000.slyther.network.message.server.MessageNewPrey;
import net.gegy1000.slyther.network.message.server.MessageRemoveFood;
import net.gegy1000.slyther.server.ConnectedClient;

public class Prey extends Entity {
    public int id;
    public float rad;
    public float size;
    public Color color;
    public int turningDirection;
    public float wang;
    public float ang;
    public float speed;
    public float fr;
    public int gfr;
    public float[] fxs;
    public float[] fys;
    public int fpos;
    public int ftg;
    public float fx;
    public float fy;
    public boolean eaten;
    public float eatenFR;
    public Snake eater;
    public float gr;

    public Prey(Game game, int id, float posX, float posY, float size, Color color, int turningDirection, float wang, float ang, float speed) {
        super(game, posX, posY);
        this.id = id;
        this.size = size;
        this.color = color;
        this.turningDirection = turningDirection;
        this.wang = wang;
        this.ang = ang;
        this.speed = speed;
        rad = 0.00001F;
        gfr = (int) (64 * Math.random());
        fxs = new float[SlytherClient.RFC];
        fys = new float[SlytherClient.RFC];
    }

    @Override
    public boolean shouldTrack(ConnectedClient client) {
        float deltaX = posX - client.snake.posX;
        float deltaY = posY - client.snake.posY;
        return Math.sqrt(deltaX * deltaX + deltaY * deltaY) <= client.viewDistance;
    }

    @Override
    public void startTracking(ConnectedClient tracker) {
        tracker.send(new MessageNewPrey(this));
    }

    @Override
    public void stopTracking(ConnectedClient tracker) {
        tracker.send(new MessageNewPrey(this));
    }

    @Override
    public void updateServer() {
    }

    @Override
    public void updateClient(float vfr, float vfrb, float vfrb2) {
        float turnSpeed = game.getMamu2() * vfr;
        float moveAmount = speed * vfr / 4;
        if (vfrb > 0) {
            if (ftg > 0) {
                float h = vfrb;
                if (h > ftg) {
                    h = ftg;
                }
                ftg -= h;
                for (int qq = 1; qq <= h; qq++) {
                    if (qq == h) {
                        fx = fxs[fpos];
                        fy = fys[fpos];
                    }
                    fxs[fpos] = 0;
                    fys[fpos] = 0;
                    fpos++;
                    if (fpos >= SlytherClient.RFC) {
                        fpos = 0;
                    }
                }
            } else if (ftg == 0) {
                fx = 0;
                fy = 0;
                ftg = -1;
            }
        }
        if (turningDirection == 1) {
            ang -= turnSpeed;
            if (ang < 0 || ang >= SlytherClient.PI_2) {
                ang %= SlytherClient.PI_2;
            }
            if (ang < 0) {
                ang += SlytherClient.PI_2;
            }
            float h = (float) ((wang - ang) % SlytherClient.PI_2);
            if (h < 0) {
                h += SlytherClient.PI_2;
            }
            if (h > Math.PI) {
                h -= SlytherClient.PI_2;
            }
            if (h > 0) {
                ang = wang;
                turningDirection = 0;
            }
        } else if (turningDirection == 2) {
            ang += turnSpeed;
            if (ang < 0 || ang >= SlytherClient.PI_2) {
                ang %= SlytherClient.PI_2;
            }
            if (ang < 0) {
                ang += SlytherClient.PI_2;
            }
            float h = (float) ((wang - ang) % SlytherClient.PI_2);
            if (h < 0) {
                h += SlytherClient.PI_2;
            }
            if (h > Math.PI) {
                h -= SlytherClient.PI_2;
            }
            if (h < 0) {
                ang = wang;
                turningDirection = 0;
            }
        } else {
            ang = wang;
        }
        posX += Math.cos(ang) * moveAmount;
        posY += Math.sin(ang) * moveAmount;
        gfr += vfr * gr;
        if (eaten) {
            if (fr != 1.5F) {
                fr += vfr / 150.0F;
                if (fr >= 1.5F) {
                    fr = 1.5F;
                }
            }
            eatenFR += vfr / 47.0F;
            gfr += vfr;
            if (eatenFR >= 1 || eater == null) {
                game.removeEntity(this);
            } else {
                rad = (float) (1 - Math.pow(eatenFR, 3));
            }
        } else {
            if (fr != 1.0F) {
                fr += vfr / 150.0F;
                if (fr >= 1.0F) {
                    fr = 1.0F;
                    rad = 1.0F;
                } else {
                    rad = (float) (0.5F * (1.0F - Math.cos(Math.PI * fr)));
                    rad += 0.66F * (0.5F * (1.0F - Math.cos(Math.PI * rad)) - rad);
                }
            }
        }
    }
}
