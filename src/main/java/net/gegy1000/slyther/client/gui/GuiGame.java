package net.gegy1000.slyther.client.gui;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.game.Color;
import net.gegy1000.slyther.game.Food;
import net.gegy1000.slyther.game.LeaderboardEntry;
import net.gegy1000.slyther.game.SkinColor;
import net.gegy1000.slyther.game.Snake;
import net.gegy1000.slyther.game.SnakePoint;
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
        textureManager.bindTexture("/textures/background.png");
        GL11.glPushMatrix();
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.drawTexture(0.0F, 0.0F, loading ? backgroundX : client.viewX, client.viewY, renderResolution.getWidth(), renderResolution.getHeight(), 599, 519);
        GL11.glPopMatrix();
        if (loading) {
            this.drawCenteredLargeString("Connecting to server...", renderResolution.getWidth() / 2.0F, renderResolution.getHeight() / 2.0F, 2.0F, 0xFFFFFF);
        }
        Snake player = client.player;
        if (player != null) {
            float newScale = 0.4F / Math.max(1.0F, (player.sct + 16.0F) / 36.0F) + 0.5F;
            if (client.gsc != newScale) {
                if (client.gsc < newScale) {
                    client.gsc += 0.0002F;
                    if (client.gsc >= newScale) {
                        client.gsc = newScale;
                    }
                } else if (client.gsc > newScale) {
                    client.gsc -= 0.0002F;
                    if (client.gsc <= newScale) {
                        client.gsc = newScale;
                    }
                }
            }
            float prevViewX = client.viewX;
            float prevviewY = client.viewY;
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
            client.bpx1 = client.viewX - (client.mww2 / client.gsc - 84);
            client.bpy1 = client.viewY - (client.mhh2 / client.gsc - 84);
            client.bpx2 = client.viewX + (client.mww2 / client.gsc + 84);
            client.bpy2 = client.viewY + (client.mhh2 / client.gsc + 84);
            client.fpx1 = client.viewX - (client.mww2 / client.gsc - 24);
            client.fpy1 = client.viewY - (client.mhh2 / client.gsc - 24);
            client.fpx2 = client.viewX + (client.mww2 / client.gsc + 24);
            client.fpy2 = client.viewY + (client.mhh2 / client.gsc + 24);
            client.apx1 = client.viewX - (client.mww2 / client.gsc - 210);
            client.apy1 = client.viewY - (client.mhh2 / client.gsc - 210);
            client.apx2 = client.viewX + (client.mww2 / client.gsc + 210);
            client.apy2 = client.viewY + (client.mhh2 / client.gsc + 210);
            GL11.glEnable(GL11.GL_BLEND);
            GL11.glEnable(GL11.GL_ALPHA_TEST);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_CONSTANT_ALPHA);
            textureManager.bindTexture("/textures/food.png");
            float globalAlpha = 1.75F;
            if (client.gla != 1.0F) {
                globalAlpha = 1.75F * client.gla;
            }
            float scale = client.gsc * 0.5F;
            for (int i = 0; i < client.foods.size(); i++) {
                Food food = client.foods.get(i);
                if (food != null) {
                    if (food.rx >= client.bpx1 && food.rx <= client.bpx2 && food.ry >= client.bpy1 && food.ry <= client.bpy2) {
                        Color color = food.cv;
                        float size = (food.sz / 5.0F) * food.rad * 0.5F;
                        GL11.glPushMatrix();
                        GL11.glTranslatef(client.mww2, client.mhh2, 0.0F);
                        GL11.glScalef(scale, scale, 1.0F);
                        GL11.glColor4f(color.getRed(), color.getGreen(), color.getBlue(), globalAlpha * food.fr);
                        GL11.glScalef(size, size, 1.0F);
                        float x = ((food.rx - client.viewX) - 64.0F * size) / size / scale;
                        float y = ((food.ry - client.viewY) - 64.0F * size) / size / scale;
                        this.drawTexture(x, y, 0.0F, 0.0F, 128.0F, 128.0F, 128.0F, 128.0F);
                        GL11.glPopMatrix();
                    }
                }
            }
            for (int i = 0; i < client.snakes.size(); i++) {
                Snake snake = client.snakes.get(i);
                snake.iiv = false;
                for (int t = snake.pts.size() - 1; t >= 0; t--) {
                    SnakePoint point = snake.pts.get(t);
                    float pointX = point.posX + point.fx;
                    float pointY = point.posY + point.fy;
                    if (pointX >= client.bpx1 && pointX <= client.bpx2 && pointY >= client.bpy1 && pointY <= client.bpy2) {
                        snake.iiv = true;
                        break;
                    }
                }
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
                        for (int pointIndex = snake.pts.size() - 1; pointIndex >= 1; pointIndex--) {
                            SnakePoint point = snake.pts.get(pointIndex);
                            if (G > -0.25F) {
                                lastX = x;
                                lastY = y;
                                x = point.posX + point.fx;
                                y = point.posY + point.fy;
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
                                        ax += -(lax - ax) * G / 0.25;
                                        ay += -(lay - ay) * G / 0.25;
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
                                            if (pax >= client.bpx1 && pax <= client.bpx2 && pay >= client.bpy1 && pay <= client.bpy2) {
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
                        GL11.glPushMatrix();
                        this.textureManager.bindTexture("/textures/snake_point.png");
                        scale = client.gsc * 0.25F * (snake.sc);
                        GL11.glTranslatef(client.mww2, client.mhh2, 0.0F);
                        GL11.glScalef(scale, scale, 1.0F);
                        for (int pointIndex = xs.size() - 1; pointIndex >= 0; pointIndex--) {
                            float pointX = (xs.get(pointIndex)) - client.viewX;
                            float pointY = (ys.get(pointIndex)) - client.viewY;
                            SkinColor color = pattern[(pointIndex / 2) % pattern.length];
                            GL11.glColor4f(color.red, color.green, color.blue, 1.0F);
                            this.drawTexture(pointX, pointY, 0.0F, 0.0F, 128.0F, 128.0F, 128.0F, 128.0F);
                        }
                        GL11.glPopMatrix();
                        if (!snake.oneEye) {
                            GL11.glPushMatrix();
                            float t = 6.0F;
                            float a = 6.0F;
                            scale = client.gsc * 0.125F * (snake.sc);
                            GL11.glTranslatef(client.mww2, client.mhh2, 0.0F);
                            GL11.glScalef(scale, scale, 1.0F);
                            float eyeOffsetX = (float) (Math.cos(ehang) * t + Math.cos(ehang - Math.PI / 2.0F) * (a + 0.5F));
                            float eyeOffsetY = (float) (Math.sin(ehang) * t + Math.sin(ehang - Math.PI / 2.0F) * (a + 0.5F));
                            this.drawCircle((client.viewX - (eyeOffsetX + originX)) / scale, (client.viewY - (eyeOffsetY + originY)) / scale, snake.er * sc * client.gsc, snake.ec);
                            eyeOffsetX = (float) (Math.cos(ehang) * t + Math.cos(ehang + Math.PI / 2.0F) * (a + 0.5F));
                            eyeOffsetY = (float) (Math.sin(ehang) * t + Math.sin(ehang + Math.PI / 2.0F) * (a + 0.5F));
                            this.drawCircle((client.viewX - (eyeOffsetX + originX)) / scale, (client.viewY - (eyeOffsetY + originY)) / scale, snake.er * sc * client.gsc, snake.ec);
                            eyeOffsetX = (float) (Math.cos(ehang) * (t + 0.5F) + snake.rex * sc + Math.cos(ehang + Math.PI / 2.0F) * a);
                            eyeOffsetY = (float) (Math.sin(ehang) * (t + 0.5F) + snake.rey * sc + Math.sin(ehang + Math.PI / 2.0F) * a);
                            this.drawCircle((client.viewX - (eyeOffsetX + originX)) / scale, (client.viewY - (eyeOffsetY + originY)) / scale, 3.5F * sc * client.gsc, snake.ppc);
                            eyeOffsetX = (float) (Math.cos(ehang) * (t + 0.5F) + snake.rex * sc + Math.cos(ehang - Math.PI / 2.0F) * a);
                            eyeOffsetY = (float) (Math.sin(ehang) * (t + 0.5F) + snake.rey * sc + Math.sin(ehang - Math.PI / 2.0F) * a);
                            this.drawCircle((client.viewX - (eyeOffsetX + originX)) / scale, (client.viewY - (eyeOffsetY + originY)) / scale, 3.5F * sc * client.gsc, snake.ppc);
                            GL11.glPopMatrix();
                        }
                    }
                }
            }
            this.drawString("Your length: " + (int) Math.floor(15.0 * (client.getFPSL(player.sct) + player.fam / client.getFMLT(player.sct) - 1.0) - 5.0), 3.0F, renderResolution.getHeight() - 35.0F, 1.0F, 0xFFFFFF);
            this.drawString("Rank " + client.rank + "/" + client.snakeCount, 3.0F, renderResolution.getHeight() - 18.0F, 1.0F, 0xAAAAAA);

            this.drawLargeString("Leaderboard:", renderResolution.getWidth() - largeFont.getWidth("Leaderboard:") - 10.0F, 2.0F, 1.0F, 0xFFFFFF);

            int y = largeFont.getHeight() + 4;

            List<LeaderboardEntry> leaderboard = new ArrayList<>(client.leaderboard);

            int alpha = 255;
            int alphaChange = (int) ((leaderboard.size() / 10.0F) * 20);

            for (int i = 1; i <= leaderboard.size(); i++) {
                LeaderboardEntry leaderboardEntry = leaderboard.get(i - 1);
                String text = i + ". " + leaderboardEntry.toString();
                this.drawString(text, renderResolution.getWidth() - font.getWidth(text) - 8.0F, y, 1.0F, leaderboardEntry.color.toHex() | alpha << 24);

                y += font.getHeight() + 2;
                alpha -= alphaChange;
            }
        }
    }

    @Override
    public void update() {
        backgroundX++;
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, int button) {

    }
}
