package net.gegy1000.slyther.client.gui;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.client.game.Food;
import net.gegy1000.slyther.client.game.Prey;
import net.gegy1000.slyther.client.game.Snake;
import net.gegy1000.slyther.game.*;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class GuiGame extends Gui {
    private int backgroundX;

    @Override
    public void init() {
    }

    @Override
    public void render(float mouseX, float mouseY) {
        boolean loading = client.networkManager == null || client.player == null;
        GL11.glPushMatrix();
        textureManager.bindTexture("/textures/background.png");
        float gsc = client.gsc + client.zoomOffset;
        GL11.glScalef(gsc, gsc, 1.0F);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        drawTexture(0.0F, 0.0F, loading ? backgroundX : client.viewX, client.viewY, renderResolution.getWidth() / gsc, renderResolution.getHeight() / gsc, 599, 519);
        GL11.glTranslatef(-client.viewX, -client.viewY, 0.0F);
        if (!loading) {
            GL11.glTranslatef(client.mww2 /  gsc, client.mhh2 / gsc, 0.0F);
        }
        Snake player = client.player;
        if (!loading) {
            float newScale = 0.4F / Math.max(1.0F, (player.sct + 16.0F) / 36.0F) + 0.5F;
            if (client.gsc != newScale) {
                if (client.gsc < newScale) {
                    client.gsc += 0.0001F;
                    if (client.gsc > newScale) {
                        client.gsc = newScale;
                    }
                } else if (client.gsc > newScale) {
                    client.gsc -= 0.0001F;
                    if (client.gsc < newScale) {
                        client.gsc = newScale;
                    }
                }
            }
            if (client.fvtg > 0) {
                client.fvtg--;
                client.fvx = client.fvxs[client.fvpos];
                client.fvy = client.fvys[client.fvpos];
                client.fvxs[client.fvpos] = 0;
                client.fvys[client.fvpos] = 0;
                if (client.fvpos > SlytherClient.VFC) {
                    client.fvpos = 0;
                }
            }
            client.viewX = player.posX + player.fx + client.fvx;
            client.viewY = player.posY + player.fy + client.fvy;
            client.viewAng = (float) Math.atan2(client.viewY - client.GAME_RADIUS, client.viewX - client.GAME_RADIUS);
            client.viewDist = (float) Math.sqrt((client.viewX - client.GAME_RADIUS) * (client.viewX - client.GAME_RADIUS) + (client.viewY - client.GAME_RADIUS) * (client.viewY - client.GAME_RADIUS));
            client.bpx1 = client.viewX - (client.mww2 / gsc - 84);
            client.bpy1 = client.viewY - (client.mhh2 / gsc - 84);
            client.bpx2 = client.viewX + (client.mww2 / gsc - 84);
            client.bpy2 = client.viewY + (client.mhh2 / gsc - 84);
            client.fpx1 = client.viewX - (client.mww2 / gsc - 24);
            client.fpy1 = client.viewY - (client.mhh2 / gsc - 24);
            client.fpx2 = client.viewX + (client.mww2 / gsc - 24);
            client.fpy2 = client.viewY + (client.mhh2 / gsc - 24);
            client.apx1 = client.viewX - (client.mww2 / gsc - 210);
            client.apy1 = client.viewY - (client.mhh2 / gsc - 210);
            client.apx2 = client.viewX + (client.mww2 / gsc - 210);
            client.apy2 = client.viewY + (client.mhh2 / gsc - 210);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_CONSTANT_ALPHA);
            textureManager.bindTexture("/textures/food.png");
            float globalAlpha = 1.75F;
            if (client.gla != 1.0F) {
                globalAlpha = 1.75F * client.gla;
            }
            for (int i = 0; i < client.foods.size(); i++) {
                Food food = client.foods.get(i);
                if (food != null) {
                    if (food.rx >= client.fpx1 && food.rx <= client.fpx2 && food.ry >= client.fpy1 && food.ry <= client.fpy2) {
                        Color color = food.cv;
                        float size = (food.sz / 5.0F) * food.rad * 0.25F;
                        GL11.glPushMatrix();
                        GL11.glColor4f(color.getRed(), color.getGreen(), color.getBlue(), globalAlpha * food.fr);
                        GL11.glScalef(size, size, 1.0F);
                        float x = food.rx / size;
                        float y = food.ry / size;
                        drawTexture(x - 64.0F, y - 64.0F, 0.0F, 0.0F, 128.0F, 128.0F, 128.0F, 128.0F);
                        GL11.glPopMatrix();
                    }
                }
            }
            for (int i = 0; i < client.preys.size(); i++) {
                Prey prey = client.preys.get(i);
                if (prey != null) {
                    float posX = prey.posX + prey.fx;
                    float posY = prey.posY + prey.fy;
                    if (posX >= client.fpx1 && posX <= client.fpx2 && posY >= client.fpy1 && posY <= client.fpy2) {
                        Color color = prey.cv;
                        float size = (prey.sz / 10.0F) * prey.rad;
                        GL11.glPushMatrix();
                        GL11.glColor4f(color.getRed(), color.getGreen(), color.getBlue(), globalAlpha * prey.fr);
                        GL11.glScalef(size, size, 1.0F);
                        float x = posX / size;
                        float y = posY / size;
                        drawTexture(x - 64.0F, y - 64.0F, 0.0F, 0.0F, 128.0F, 128.0F, 128.0F, 128.0F);
                        GL11.glPopMatrix();
                    }
                }
            }
            for (int i = 0; i < client.snakes.size(); i++) {
                Snake snake = client.snakes.get(i);
                snake.iiv = true;
                /*for (int t = snake.pts.size() - 1; t >= 0; t--) {
                    SnakePoint point = snake.pts.get(t);
                    float pointX = point.posX + point.fx;
                    float pointY = point.posY + point.fy;
                    if (pointX >= client.bpx1 && pointX <= client.bpx2 && pointY >= client.bpy1 && pointY <= client.bpy2) {
                        snake.iiv = true;
                        break;
                    }
                }*/
            }
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
            for (int i = 0; i < client.snakes.size(); i++) {
                Snake snake = client.snakes.get(i);
                if (snake != null) {
                    float originX = snake.posX + snake.fx;
                    float originY = snake.posY + snake.fy;
                    float ehang = snake.ehang;
                    float sc = snake.sc;
                    if (snake.sep != snake.wsep) {
                        if (snake.sep < snake.wsep) {
                            snake.sep += 0.01F;
                            if (snake.sep >= snake.wsep) {
                                snake.sep = snake.wsep;
                            }
                        } else if (snake.sep > snake.wsep) {
                            snake.sep -= 0.01F;
                            if (snake.sep <= snake.wsep) {
                                snake.sep = snake.wsep;
                            }
                        }
                    }
                    if (snake.iiv) {
                        List<Float> xs = new ArrayList<>();
                        List<Float> ys = new ArrayList<>();
                        float lastX;
                        float lastY;
                        float lax;
                        float lay;
                        float n = (snake.chl + snake.fchl) % 0.25F;
                        if (n < 0) {
                            n += 0.25F;
                        }
                        n = 0.25F - n;
                        float lastAverageX;
                        float lastAverageY;
                        float x = originX;
                        float y = originY;
                        float averageX = originX;
                        float averageY = originY;
                        float ax = originX;
                        float ay = originY;
                        float G = (float) (snake.cfl + (1.0F - Math.ceil((snake.chl + snake.fchl) / 0.25F) * 0.25F));
                        float K = 0;
                        float O = snake.sep * client.qsm;
                        SkinColor[] pattern = snake.rbcs;
                        for (int pointIndex = snake.pts.size() - 1; pointIndex >= 0; pointIndex--) {
                            SnakePoint point = snake.pts.get(pointIndex);
                            lastX = x;
                            lastY = y;
                            x = point.posX + point.fx;
                            y = point.posY + point.fy;
                            if (G > -0.25F) {
                                lastAverageX = averageX;
                                lastAverageY = averageY;
                                averageX = (x + lastX) / 2.0F;
                                averageY = (y + lastY) / 2.0F;
                                for (float q = 0.0F; q < 1.0F; q += 0.25F) {
                                    float E = n + q;
                                    float e = lastAverageX + (lastX - lastAverageX) * E;
                                    float w = lastAverageY + (lastY - lastAverageY) * E;
                                    float J = lastX + (averageX - lastX) * E;
                                    float M = lastY + (averageY - lastY) * E;
                                    lax = ax;
                                    lay = ay;
                                    ax = e + (J - e) * E;
                                    ay = w + (M - w) * E;
                                    if (G < 0) {
                                        ax += -(lax - ax) * G / 0.25F;
                                        ay += -(lay - ay) * G / 0.25F;
                                    }
                                    float partDistance = (float) Math.sqrt(Math.pow(ax - lax, 2) + Math.pow(ay - lay, 2));
                                    if (K + partDistance < O) {
                                        K += partDistance;
                                    } else {
                                        K = -K;
                                        for (int a = (int) ((partDistance - K) / O); a >= 1; a--) {
                                            K += O;
                                            float pax = lax + (ax - lax) * K / partDistance;
                                            float pay = lay + (ay - lay) * K / partDistance;
                                            if (true || pax >= client.bpx1 && pax <= client.bpx2 && pay >= client.bpy1 && pay <= client.bpy2) {
                                                xs.add(pax);
                                                ys.add(pay);
                                            }
                                        }
                                        K = partDistance - K;
                                    }
                                    if (G < 1.0F) {
                                        G -= 0.25F;
                                        if (G <= -0.25F) {
                                            break;
                                        }
                                    }
                                }
                                if (G >= 1.0F) {
                                    G--;
                                }
                            }
                        }
                        if (true || ax >= client.bpx1 && ax <= client.bpx2 && ay >= client.bpy1 && ay <= client.bpy2) {
                            xs.add(ax);
                            ys.add(ay);
                        }
                        textureManager.bindTexture("/textures/snake_point.png");
                        for (int pointIndex = xs.size() - 1; pointIndex >= 0; pointIndex--) {
                            float pointX = (xs.get(pointIndex));
                            float pointY = (ys.get(pointIndex));
                            SkinColor color = pattern[pointIndex % pattern.length];
                            float colorMultipler = 1.0F;
                            if (snake.sp > snake.fsp) {
                                float offset = (((pointIndex + client.ticks + client.vfr) / 2.0F) % 20.0F);
                                if (offset > 10.0F) {
                                    offset = 10.0F - (offset - 10.0F);
                                }
                                colorMultipler += offset / 10.0F;
                            }
                            GL11.glColor4f(color.red * colorMultipler, color.green * colorMultipler, color.blue * colorMultipler, 1.0F);
                            GL11.glPushMatrix();
                            GL11.glTranslatef(pointX, pointY, 0);
                            GL11.glScalef(snake.sc * 0.25F, snake.sc * 0.25F, 1);
                            drawTexture(-64, -64, 0, 0, 128, 128, 128, 128);
                            GL11.glPopMatrix();
                        }
                        if (!snake.oneEye) {
                            GL11.glPushMatrix();
                            float eyeForward = 2.0F * sc;
                            float eyeSideDistance = 6.0F * sc;
                            GL11.glTranslatef(originX, originY, 0.0F);
                            float eyeOffsetX = (float) (Math.cos(ehang) * eyeForward + Math.cos(ehang - Math.PI / 2.0F) * (eyeSideDistance + 0.5F));
                            float eyeOffsetY = (float) (Math.sin(ehang) * eyeForward + Math.sin(ehang - Math.PI / 2.0F) * (eyeSideDistance + 0.5F));
                            drawCircle(eyeOffsetX, eyeOffsetY, snake.er * sc, snake.ec);
                            eyeOffsetX = (float) (Math.cos(ehang) * eyeForward + Math.cos(ehang + Math.PI / 2.0F) * (eyeSideDistance + 0.5F));
                            eyeOffsetY = (float) (Math.sin(ehang) * eyeForward + Math.sin(ehang + Math.PI / 2.0F) * (eyeSideDistance + 0.5F));
                            drawCircle(eyeOffsetX, eyeOffsetY, snake.er * sc, snake.ec);
                            eyeOffsetX = (float) (Math.cos(ehang) * (eyeForward + 0.5F) + snake.rex * sc + Math.cos(ehang + Math.PI / 2.0F) * eyeSideDistance);
                            eyeOffsetY = (float) (Math.sin(ehang) * (eyeForward + 0.5F) + snake.rey * sc + Math.sin(ehang + Math.PI / 2.0F) * eyeSideDistance);
                            drawCircle(eyeOffsetX, eyeOffsetY, 3.5F * sc, snake.ppc);
                            eyeOffsetX = (float) (Math.cos(ehang) * (eyeForward + 0.5F) + snake.rex * sc + Math.cos(ehang - Math.PI / 2.0F) * eyeSideDistance);
                            eyeOffsetY = (float) (Math.sin(ehang) * (eyeForward + 0.5F) + snake.rey * sc + Math.sin(ehang - Math.PI / 2.0F) * eyeSideDistance);
                            drawCircle(eyeOffsetX, eyeOffsetY, 3.5F * sc, snake.ppc);
                            GL11.glPopMatrix();
                        }
                        if (snake.antenna) {
                            GL11.glPushMatrix();
                            float e = (float) Math.cos(snake.ang);
                            float w = (float) Math.sin(snake.ang);
                            ax = originX - 8 * e * snake.sc;
                            ay = originY - 8 * w * snake.sc;
                            snake.atx[0] = ax;
                            snake.aty[0] = ay;
                            float E = snake.sc * gsc;
                            int fj = snake.atx.length - 1;
                            if (!snake.antennaShown) {
                                snake.antennaShown = true;
                                for (int t = 1; t <= fj; t++) {
                                    snake.atx[t] = ax - e * t * 4 * snake.sc;
                                    snake.aty[t] = ay - w * t * 4 * snake.sc;
                                }
                            }
                            for (int t = 1; t <= fj; t++) {
                                x = (float) (snake.atx[t - 1] + (Math.random() * 2.0F - 1));
                                y = (float) (snake.aty[t - 1] + (Math.random() * 2.0F - 1));
                                e = snake.atx[t] - x;
                                w = snake.aty[t] - y;
                                float ang = (float) Math.atan2(w, e);
                                x += Math.cos(ang) * snake.sc * 4.0F;
                                y += Math.sin(ang) * snake.sc * 4.0F;
                                snake.atvx[t] += (x - snake.atx[t]) * 0.1F;
                                snake.atvy[t] += (y - snake.aty[t]) * 0.1F;
                                snake.atx[t] += snake.atvx[t];
                                snake.aty[t] += snake.atvy[t];
                                snake.atvx[t] *= 0.88F;
                                snake.atvy[t] *= 0.88F;
                                e = snake.atx[t] - snake.atx[t - 1];
                                w = snake.aty[t] - snake.aty[t - 1];
                                float J = (float) Math.sqrt(e * e + w * w);
                                if (J > snake.sc * 4.0F) {
                                    ang = (float) Math.atan2(w, e);
                                    snake.atx[t] = (float) (snake.atx[t - 1] + Math.cos(ang) * 4 * snake.sc);
                                    snake.aty[t] = (float) (snake.aty[t - 1] + Math.sin(ang) * 4 * snake.sc);
                                }
                            }
                            fj = snake.atx.length;
                            float prevX = snake.atx[fj - 1];
                            float prevY = snake.aty[fj - 1];
                            beginConnectedLines(E * 5.0F, snake.atc1);
                            for (int t = 0; t < fj; t++) {
                                x = snake.atx[t];
                                y = snake.aty[t];
                                if (Math.abs(x - prevX) + Math.abs(y - prevY) >= 1) {
                                    drawConnectedLine(prevX, prevY, x, y);
                                    prevX = x;
                                    prevY = y;
                                }
                            }
                            endConnectedLines();
                            beginConnectedLines(E * 4.0F, snake.atc2);
                            for (int t = 0; t < fj; t++) {
                                x = snake.atx[t];
                                y = snake.aty[t];
                                if (Math.abs(x - prevX) + Math.abs(y - prevY) >= 1) {
                                    drawConnectedLine(prevX, prevY, x, y);
                                    prevX = x;
                                    prevY = y;
                                }
                            }
                            endConnectedLines();
                            if (snake.antennaTexture != null) {
                                GL11.glTranslatef(snake.atx[fj - 1], snake.aty[fj - 1], 0.0F);
                                if (snake.abrot) {
                                    float vang = (float) (Math.atan2(snake.aty[fj - 1] - snake.aty[fj - 2], snake.atx[fj - 1] - snake.atx[fj - 2]) - snake.atba);
                                    if (vang < 0 || vang >= SlytherClient.PI_2) {
                                        vang %= SlytherClient.PI_2;
                                    }
                                    if (vang < -Math.PI) {
                                        vang += SlytherClient.PI_2;
                                    } else {
                                        if (vang > Math.PI) {
                                            vang -= SlytherClient.PI_2;
                                        }
                                    }
                                    snake.atba = (float) ((snake.atba + 0.15F * vang) % SlytherClient.PI_2);
                                    GL11.glRotatef((float) Math.toDegrees(snake.atba), 0.0F, 0.0F, 1.0F);
                                }
                                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
                                GL11.glScalef(snake.sc * 0.25F, snake.sc * 0.25F, 1.0F);
                                textureManager.bindTexture("/textures/" + snake.antennaTexture + ".png");
                                drawTexture(-64.0F, -64.0F, 0.0F, 0.0F, 128.0F, 128.0F, 128.0F, 128.0F);
                            }
                            GL11.glPopMatrix();
                        }
                    }
                }
            }

            GL11.glPopMatrix();

            drawString("Your length: " + (int) Math.floor(15.0 * (client.getFPSL(player.sct) + player.fam / client.getFMLT(player.sct) - 1.0) - 5.0), 3.0F, renderResolution.getHeight() - 35.0F, 1.0F, 0xFFFFFF);
            drawString("Rank " + client.rank + "/" + client.snakeCount, 3.0F, renderResolution.getHeight() - 18.0F, 1.0F, 0xAAAAAA);

            drawLargeString("Leaderboard:", renderResolution.getWidth() - largeFont.getWidth("Leaderboard:") - 10.0F, 2.0F, 1.0F, 0xFFFFFF);

            int leaderboardY = largeFont.getHeight() + 4;

            List<LeaderboardEntry> leaderboard = new ArrayList<>(client.leaderboard);

            int alpha = 255;
            int alphaChange = (int) ((leaderboard.size() / 10.0F) * 20);

            for (int i = 1; i <= leaderboard.size(); i++) {
                LeaderboardEntry leaderboardEntry = leaderboard.get(i - 1);
                String text = i + ". " + leaderboardEntry;
                drawString(text, renderResolution.getWidth() - font.getWidth(text) - 8.0F, leaderboardY, 1.0F, leaderboardEntry.color.toHex() | alpha << 24);

                leaderboardY += font.getHeight() + 2;
                alpha -= alphaChange;
            }

            float mapX = renderResolution.getWidth() - 100.0F + 40.0F;
            float mapY = renderResolution.getHeight() - 100.0F + 40.0F;
            drawCircle(mapX, mapY, 42.5F, 0x222222);
            drawCircle(mapX, mapY, 40.0F, 0x555555);
            GL11.glColor4f(0.8F, 0.8F, 0.8F, 1.0F);
            for (int x = 0; x < 80; x++) {
                for (int y = 0; y < 80; y++) {
                    if (client.map[x][y]) {
                        drawRect((renderResolution.getWidth() - 100.0F) + x, (renderResolution.getHeight() - 100.0F) + y, 1.0F, 1.0F);
                    }
                }
            }
            float locationMarkerX = (renderResolution.getWidth() - 100.0F) + Math.round((client.player.posX - client.GAME_RADIUS) * 40 / client.GAME_RADIUS + 52 - 7);
            float locationMarkerY = (renderResolution.getHeight() - 100.0F) + Math.round((client.player.posY - client.GAME_RADIUS) * 40 / client.GAME_RADIUS + 52 - 7);
            drawCircle(locationMarkerX, locationMarkerY, 3.0F, 0x202020);
            drawCircle(locationMarkerX, locationMarkerY, 2.0F, 0xFFFFFF);
        } else {
            GL11.glPopMatrix();
            drawCenteredLargeString("Connecting to server...", renderResolution.getWidth() / 2.0F, renderResolution.getHeight() / 2.0F, 2.0F, 0xFFFFFF);
        }
    }

    @Override
    public void update() {
        backgroundX++;
        client.zoomOffset += Mouse.getDWheel() * 0.0005F;
        if (client.zoomOffset > 1.0F) {
            client.zoomOffset = 1.0F;
        } else if (client.zoomOffset < -0.75F) {
            client.zoomOffset = -0.75F;
        }
    }

    @Override
    public void keyPressed(int key, char character) {

    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, int button) {

    }
}
