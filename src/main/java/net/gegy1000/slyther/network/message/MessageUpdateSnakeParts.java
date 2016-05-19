package net.gegy1000.slyther.network.message;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.game.Snake;
import net.gegy1000.slyther.game.SnakePart;
import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.server.SlytherServer;

public class MessageUpdateSnakeParts extends SlytherServerMessageBase {
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
                for (SnakePart part : snake.pts) {
                    if (!part.dying) {
                        part.dying = true;
                        break;
                    }
                }
            }
            SnakePart head = snake.pts.get(snake.pts.size() - 1);
            float x;
            float y;
            if (this.messageId == 'g' || this.messageId == 'n') {
                x = buffer.readShort();
                y = buffer.readShort();
            } else {
                x = head.posX + (buffer.readByte() - 128);
                y = head.posY + (buffer.readByte() - 128);
            }
            if (alive) {
                snake.fam = (float) buffer.readInt24() / 0xFFFFFF;
            }
            SnakePart part = new SnakePart();
            part.posX = x;
            part.posY = y;
            part.ebx = part.posX - head.posX;
            part.eby = part.posY - head.posY;
            snake.pts.add(part);
            if (snake.iiv) {
                float fx = (snake.posX + snake.fx) - part.posX;
                float fy = (snake.posY + snake.fy) - part.posY;
                part.fx += fx;
                part.fy += fy;
                part.exs[part.eiu] = fx;
                part.eys[part.eiu] = fy;
                part.efs[part.eiu] = 0;
                part.ems[part.eiu] = 1.0F;
                part.eiu++;
            }
            if (snake.pts.size() - 3 >= 1) {
                SnakePart prevPart = snake.pts.get(snake.pts.size() - 3);
                float distMultiplier = 0;
                int i = 1;
                for (int partIndex = snake.pts.size() - 4; partIndex >= 0; partIndex--) {
                    part = snake.pts.get(partIndex);
                    float fx = part.posX;
                    float fy = part.posY;
                    if (i <= 4) {
                        distMultiplier = client.CST * i / 4.0F;
                    }
                    part.posX += (prevPart.posX - part.posX) * distMultiplier;
                    part.posY += (prevPart.posY - part.posY) * distMultiplier;
                    if (snake.iiv) {
                        fx -= part.posX;
                        fy -= part.posY;
                        part.fx += fx;
                        part.fy += fy;
                        part.exs[part.eiu] = fx;
                        part.eys[part.eiu] = fy;
                        part.efs[part.eiu] = 0;
                        part.ems[part.eiu] = 2.0F;
                        part.eiu++;
                    }
                    prevPart = part;
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
            snake.lnp = part;
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
