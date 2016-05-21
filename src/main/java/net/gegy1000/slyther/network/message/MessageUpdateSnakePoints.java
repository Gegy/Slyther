package net.gegy1000.slyther.network.message;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.game.Snake;
import net.gegy1000.slyther.game.SnakePoint;
import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.server.SlytherServer;

import java.util.Arrays;

public class MessageUpdateSnakePoints extends SlytherServerMessageBase {
    @Override
    public void write(MessageByteBuffer buffer, SlytherServer server) {
    }

    @Override
    public void read(MessageByteBuffer buffer, SlytherClient client) {
        boolean alive = this.messageId == 'n' || this.messageId == 'N';
        int id = buffer.readShort();
        Snake snake = client.getSnake(id);
        if (snake != null) {
            if (alive) {
                snake.sct++;
            } else {
                for (SnakePoint point : snake.pts) {
                    if (!point.dying) {
                        point.dying = true;
                        break;
                    }
                }
            }
            SnakePoint head = snake.pts.get(snake.pts.size() - 1);
            float x;
            float y;
            if (this.messageId == 'g' || this.messageId == 'n') {
                x = buffer.readShort();
                y = buffer.readShort();
            } else {
                x = head.posX + buffer.readByte() - 128;
                y = head.posY + buffer.readByte() - 128;
            }
            if (alive) {
                snake.fam = (double) buffer.readInt24() / 0xFFFFFF;
            }
            SnakePoint point = new SnakePoint();
            point.posX = x;
            point.posY = y;
            point.ebx = point.posX - head.posX;
            point.eby = point.posY - head.posY;
            snake.pts.add(point);
            if (snake.iiv) {
                float fx = (snake.posX + snake.fx) - point.posX;
                float fy = (snake.posY + snake.fy) - point.posY;
                point.fx += fx;
                point.fy += fy;
                point.exs[point.eiu] = fx;
                point.eys[point.eiu] = fy;
                point.efs[point.eiu] = 0;
                point.ems[point.eiu] = 1.0F;
                point.eiu++;
            }
            if (snake.pts.size() - 3 >= 1) {
                SnakePoint prevPoint = snake.pts.get(snake.pts.size() - 3);
                float distMultiplier = 0;
                int i = 1;
                for (int pointIndex = snake.pts.size() - 4; pointIndex >= 0; pointIndex--) {
                    point = snake.pts.get(pointIndex);
                    float fx = point.posX;
                    float fy = point.posY;
                    if (i <= 4) {
                        distMultiplier = client.CST * i / 4.0F;
                    }
                    point.posX += (prevPoint.posX - point.posX) * distMultiplier;
                    point.posY += (prevPoint.posY - point.posY) * distMultiplier;
                    if (snake.iiv) {
                        fx -= point.posX;
                        fy -= point.posY;
                        point.fx += fx;
                        point.fy += fy;
                        point.exs[point.eiu] = fx;
                        point.eys[point.eiu] = fy;
                        point.efs[point.eiu] = 0;
                        point.ems[point.eiu] = 2.0F;
                        point.eiu++;
                    }
                    prevPoint = point;
                    i++;
                }
            }
            snake.sc = Math.min(6.0F, (snake.sct - 2.0F) / 106.0F + 1.0F);
            snake.scang = (float) (Math.pow((7.0F - snake.sc) / 6.0F, 2.0F) * 0.87F + 0.13F);
            snake.ssp = client.NSP1 + client.NSP2 * snake.sp;
            snake.fsp = snake.ssp + 0.1F;
            snake.wsep = snake.sc * 6.0F;
            float min = SlytherClient.NSEP / client.gsc;
            if (snake.wsep < min) {
                snake.wsep = min;
            }
            if (alive) {
                snake.snl();
            }
            snake.lnp = point;
            if (snake == client.player) {
                client.ovxx = snake.posX + snake.fx;
                client.ovyy = snake.posY + snake.fy;
            }
            float moveAmount = client.etm / 8.0F * snake.sp / 4.0F;
            moveAmount *= client.lagMultiplier;
            float prevChl = snake.chl - 1;
            snake.chl = moveAmount / snake.msl;
            float prevX = snake.posX;
            float prevY = snake.posY;
            snake.posX = (float) (x + Math.cos(snake.ang) * moveAmount);
            snake.posY = (float) (y + Math.sin(snake.ang) * moveAmount);
            float moveX = snake.posX - prevX;
            float moveY = snake.posY - prevY;
            float chlDiff = snake.chl - prevChl;
            int fpos = snake.fpos;
            for (int i = 0; i < SlytherClient.RFC; i++) {
                float rfas = SlytherClient.RFAS[i];
                snake.fxs[fpos] -= moveX * rfas;
                snake.fys[fpos] -= moveY * rfas;
                snake.fchls[fpos] -= chlDiff * rfas;
                fpos++;
                if (fpos >= SlytherClient.RFC) {
                    fpos = 0;
                }
            }
            snake.fx = snake.fxs[snake.fpos];
            snake.fy = snake.fys[snake.fpos];
            snake.fchl = snake.fchls[snake.fpos];
            snake.ftg = SlytherClient.RFC;
            snake.ehl = 0;
            if (snake == client.player) {
                client.viewX = snake.posX + snake.fx;
                client.viewY = snake.posY + snake.fy;
                float viewDiffX = client.viewX - client.ovxx;
                float viewDiffY = client.viewY - client.ovyy;
                int fvpos = client.fvpos;
                for (int i = 0; i < SlytherClient.VFC; i++) {
                    double vfas = SlytherClient.VFAS[i];
                    client.fvxs[fvpos] -= viewDiffX * vfas;
                    client.fvys[fvpos] -= viewDiffY * vfas;
                    fvpos++;
                    if (fvpos >= SlytherClient.VFC) {
                        fvpos = 0;
                    }
                }
                client.fvtg = SlytherClient.VFC;
            }
        }
    }

    @Override
    public int[] getMessageIds() {
        return new int[] { 'g', 'n', 'G', 'N' };
    }
}
