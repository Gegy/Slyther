package net.gegy1000.slyther.network.message.server;

import net.gegy1000.slyther.client.ClientNetworkManager;
import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.client.game.entity.ClientSnake;
import net.gegy1000.slyther.game.entity.Snake;
import net.gegy1000.slyther.game.entity.SnakePoint;
import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.network.message.SlytherServerMessageBase;
import net.gegy1000.slyther.server.ConnectedClient;
import net.gegy1000.slyther.server.SlytherServer;

//4 per second
public class MessageSnakeMovement extends SlytherServerMessageBase {
    private Snake<?> snake;
    private boolean absolute;
    private boolean updateLength;

    public MessageSnakeMovement() {
    }

    public MessageSnakeMovement(Snake snake, boolean absolute, boolean updateLength) {
        this.snake = snake;
        this.absolute = absolute;
        this.updateLength = updateLength;
    }

    @Override
    public void write(MessageByteBuffer buffer, SlytherServer server, ConnectedClient client) {
        buffer.writeUInt16(snake.id);
        SnakePoint head = snake.points.get(snake.points.size() - 1);
        int gameRadius = server.configuration.gameRadius;
        if (absolute) {
            buffer.writeUInt16((int) snake.posX + gameRadius);
            buffer.writeUInt16((int) snake.posY + gameRadius);
        } else {
            buffer.writeUInt8(Math.min(255, Math.max(0, (int) ((snake.posX - head.posX) + 128))));
            buffer.writeUInt8(Math.min(255, Math.max(0, (int) ((snake.posY - head.posY) + 128))));
        }
        if (updateLength) {
            buffer.writeUInt24((int) (snake.fam * 0xFFFFFF));
        }
    }

    @Override
    public void read(MessageByteBuffer buffer, SlytherClient client, ClientNetworkManager networkManager) {
        boolean updateLength = messageId == 'n' || messageId == 'N';
        int id = buffer.readUInt16();
        ClientSnake snake = client.getSnake(id);
        if (snake != null) {
            if (updateLength) {
                snake.sct++;
            } else {
                for (SnakePoint point : snake.points) {
                    if (!point.dying) {
                        point.dying = true;
                        break;
                    }
                }
            }
            SnakePoint head = snake.points.get(snake.points.size() - 1);
            float x;
            float y;
            if (messageId == 'g' || messageId == 'n') {
                x = buffer.readUInt16();
                y = buffer.readUInt16();
            } else {
                x = head.posX + (buffer.readUInt8() - 128);
                y = head.posY + (buffer.readUInt8() - 128);
            }
            if (updateLength) {
                snake.fam = (double) buffer.readUInt24() / 0xFFFFFF;
            }
            SnakePoint point = new SnakePoint(client, x, y);
            point.deltaX = point.posX - head.posX;
            point.deltaY = point.posY - head.posY;
            snake.points.add(point);
            if (snake.isInView) {
                float fx = (snake.posX + snake.fx) - point.posX;
                float fy = (snake.posY + snake.fy) - point.posY;
                point.fx += fx;
                point.fy += fy;
                point.prevFx = point.fx;
                point.prevFy = point.fy;
                point.exs[point.eiu] = fx;
                point.eys[point.eiu] = fy;
                point.efs[point.eiu] = 0;
                point.ems[point.eiu] = 1.0F;
                point.eiu++;
            }
            if (snake.points.size() - 3 >= 1) {
                SnakePoint prevPoint = snake.points.get(snake.points.size() - 3);
                float distMultiplier = 0;
                int i = 1;
                for (int pointIndex = snake.points.size() - 4; pointIndex >= 0; pointIndex--) {
                    point = snake.points.get(pointIndex);
                    float fx = point.posX;
                    float fy = point.posY;
                    if (i <= 4) {
                        distMultiplier = client.getCST() * i / 4.0F;
                    }
                    point.posX += (prevPoint.posX - point.posX) * distMultiplier;
                    point.posY += (prevPoint.posY - point.posY) * distMultiplier;
                    point.prevPosX = point.posX;
                    point.prevPosY = point.posY;
                    if (snake.isInView) {
                        fx -= point.posX;
                        fy -= point.posY;
                        point.fx += fx;
                        point.fy += fy;
                        point.prevFx = fx;
                        point.prevFy = fy;
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
            snake.scale = Math.min(6.0F, (snake.sct - 2.0F) / 106.0F + 1.0F);
            snake.scaleTurnMultiplier = (float) (Math.pow((7.0F - snake.scale) / 6.0F, 2.0F) * 0.87F + 0.13F);
            snake.moveSpeed = client.NSP1 + client.NSP2 * snake.scale;
            snake.accelleratingSpeed = snake.moveSpeed + 0.1F;
            snake.wantedSeperation = snake.scale * 6.0F;
            float min = SlytherClient.NSEP / client.globalScale;
            if (snake.wantedSeperation < min) {
                snake.wantedSeperation = min;
            }
            if (updateLength) {
                snake.updateLength();
            }
            if (snake == client.player) {
                client.originalViewX = snake.posX + snake.fx;
                client.originalViewY = snake.posY + snake.fy;
            }
            float moveAmount = client.errorTime / 8.0F * snake.speed / 4.0F;
            moveAmount *= client.lagMultiplier;
            float prevChl = snake.chl - 1;
            snake.chl = moveAmount / snake.msl;
            float prevX = snake.posX;
            float prevY = snake.posY;
            snake.posX = (float) (x + Math.cos(snake.angle) * moveAmount);
            snake.posY = (float) (y + Math.sin(snake.angle) * moveAmount);
            snake.prevPosX = snake.posX;
            snake.prevPosY = snake.posY;
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
            snake.prevFx = snake.fx;
            snake.prevFy = snake.fy;
            snake.fchl = snake.fchls[snake.fpos];
            snake.ftg = SlytherClient.RFC;
            snake.ehl = 0;
            if (snake == client.player) {
                client.viewX = snake.posX + snake.fx;
                client.viewY = snake.posY + snake.fy;
                float viewDiffX = client.viewX - client.originalViewX;
                float viewDiffY = client.viewY - client.originalViewY;
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

    @Override
    public int getSendMessageId() {
        char id = updateLength ? 'n' : 'g';
        return absolute ? id : Character.toUpperCase(id);
    }
}
