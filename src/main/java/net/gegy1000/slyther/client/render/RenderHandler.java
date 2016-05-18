package net.gegy1000.slyther.client.render;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.game.Color;
import net.gegy1000.slyther.game.*;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.TrueTypeFont;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class RenderHandler {
    private TextureManager textureManager;
    private RenderResolution renderResolution;

    private TrueTypeFont font;
    private TrueTypeFont largeFont;

    public RenderHandler() {
        this.textureManager = new TextureManager();
    }

    public void setupDisplay() {
        try {
            Display.setDisplayMode(new DisplayMode(854, 480));
            Display.setTitle("Slyther");
            Display.setResizable(true);
            Display.create();
            Keyboard.create();
            Mouse.create();
            try {
                Font awtFont = Font.createFont(Font.TRUETYPE_FONT, SlytherClient.class.getResourceAsStream("/fonts/font.ttf"));
                this.font = new TrueTypeFont(awtFont.deriveFont(15.0F), true);
                this.largeFont = new TrueTypeFont(awtFont.deriveFont(30.0F), true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (LWJGLException e) {
            e.printStackTrace();
        }
    }

    public void resetResolution() {
        renderResolution = new RenderResolution();
    }

    public void render(SlytherClient client) {
        renderResolution.applyScale();
        textureManager.bindTexture("/textures/background.png");
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
            client.bpx1 = client.viewX - (client.mww2 / client.gsc + 84);
            client.bpy1 = client.viewY - (client.mhh2 / client.gsc + 84);
            client.bpx2 = client.viewX + (client.mww2 / client.gsc + 84);
            client.bpy2 = client.viewY + (client.mhh2 / client.gsc + 84);
            client.fpx1 = client.viewX - (client.mww2 / client.gsc + 24);
            client.fpy1 = client.viewY - (client.mhh2 / client.gsc + 24);
            client.fpx2 = client.viewX + (client.mww2 / client.gsc + 24);
            client.fpy2 = client.viewY + (client.mhh2 / client.gsc + 24);
            client.apx1 = client.viewX - (client.mww2 / client.gsc + 210);
            client.apy1 = client.viewY - (client.mhh2 / client.gsc + 210);
            client.apx2 = client.viewX + (client.mww2 / client.gsc + 210);
            client.apy2 = client.viewY + (client.mhh2 / client.gsc + 210);
            GL11.glPushMatrix();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            this.drawTexture(0.0F, 0.0F, client.viewX, client.viewY, Display.getWidth(), Display.getHeight(), 1198, 1038);
            GL11.glPopMatrix();
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
                    Color color = food.cv;
                    float radius = food.rad * 0.5F;
                    GL11.glPushMatrix();
                    GL11.glTranslatef(client.mww2, client.mhh2, 0.0F);
                    GL11.glScalef(scale, scale, 1.0F);
                    GL11.glColor4f(color.getRed(), color.getGreen(), color.getBlue(), globalAlpha * food.fr);
                    GL11.glScalef(radius, radius, 1.0F);
                    float x = ((food.rx - client.viewX) - 64.0F * radius) / radius / scale;
                    float y = ((food.ry - client.viewY) - 64.0F * radius) / radius / scale;
                    this.drawTexture(x, y, 0.0F, 0.0F, 128.0F, 128.0F, 128.0F, 128.0F);
                    GL11.glPopMatrix();
                }
            }
            this.textureManager.bindTexture("/textures/snake_part.png");
            for (int i = 0; i < client.snakes.size(); i++) {
                Snake snake = client.snakes.get(i);
                snake.iiv = false;
                for (int t = snake.pts.size() - 1; t >= 0; t--) {
                    SnakePart part = snake.pts.get(t);
                    float partX = part.posX + part.fx;
                    float partY = part.posY + part.fy;
                    if (partX >= client.bpx1 && partX <= client.bpx2 && partY >= client.bpy1 && partY <= client.bpy2) {
                        snake.iiv = true;
                        break;
                    }
                }
            }
            for (int i = 0; i < client.snakes.size(); i++) {
                Snake snake = client.snakes.get(i);
                if (snake != null) {
                    float originX = snake.posX + snake.fx;
                    float originY = snake.posY + snake.fy;
                    float px = originX;
                    float py = originY;
                    float D = snake.ehang;
                    float sc = snake.sc;
                    float B = sc * 29.0F;
                    float G = snake.cfl;
                    B *= 0.5F;
                    float I = px;
                    float L = py;
                    float n = (snake.chl + snake.fchl) % 0.25F;
                    if (n < 0) {
                        n += 0.25F;
                    }
                    n = 0.25F - n;
                    G += 1.0F - 0.25F * Math.ceil((snake.chl + snake.fchl) / 0.25F);
                    float ax = px;
                    float ay = py;
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
//                    float O = snake.sep * client.qsm;
//                    float K = 0;
//                    int z = 1;
//                    float lax = ax;
//                    float lay = ay;
//                    for (int t = snake.pts.size() - 1; t >= 0; t--) {
//                        SnakePart part = snake.pts.get(t);
//                        float lpx = px;
//                        float lpy = py;
//                        px = part.posX + part.fx;
//                        py = part.posY + part.fy;
//                        if (G > -0.25F) {
//                            float F = I;
//                            float A = L;
//                            I = (px + lpx) / 2.0F;
//                            L = (py + lpy) / 2.0F;
//                            float N = lpx;
//                            float C = lpy;
//                            for (float q = 0.0F; q < 1.0F; q += 0.25F) {
//                                float E = n + q;
//                                float e = (F + (N - F)) * E;
//                                float w = (A + (C - A)) * E;
//                                float J = (N + (I - N)) * E;
//                                float M = (C + (L - C)) * E;
//                                lax = ax;
//                                lay = ay;
//                                ax = e + (J - e) * E;
//                                ay = w + (M - w) * E;
//                                if (G < 0.0F) {
//                                    ax += -(lax - ax) * G / 0.25F;
//                                    ay += -(lay - ay) * G / 0.25F;
//                                }
//                                J = (float) Math.sqrt(Math.pow(ax - lax, 2) + Math.pow(ay - lay, 2));
//                                if (K + J < O) {
//                                    K += J;
//                                } else {
//                                    K = -K;
//                                    for (int k = (int) ((J - K) / O); k >= 1; k--) {
//                                        K += O;
//                                        float pax = lax + (ax - lax) * K / J;
//                                        float pay = lay + (ay - lay) * K / J;
//                                        if (pax >= client.bpx1 && pay >= client.bpy1 && pax <= client.bpx2 && pay <= client.bpy2) {
//                                            client.pbx[z] = pax;
//                                            client.pby[z] = pay;
//                                            client.pbu[z] = 1;
//                                            e = ax - lax;
//                                            w = ay - lay;
//                                            if (e >= -15 && w >= -15 && e < 15 && w < 15) {
//                                                client.pba[z] = SlytherClient.AT2LT[(int) (8 * w) + 128 << 8 | 8 * (int) e + 128];
//                                            } else if (e >= -127 && w >= -127 && e < 127 && w < 127) {
//                                                client.pba[z] = SlytherClient.AT2LT[(int) w + 128 << 8 | (int) e + 128];
//                                            } else {
//                                                client.pba[z] = (float) Math.atan2(w, e);
//                                            }
//                                        } else {
//                                            client.pbu[z] = 0;
//                                        }
//                                    }
//                                    z++;
//                                }
//                                K = J - K;
//                            }
//                            if (G < 1) {
//                                G -= 0.25F;
//                                if (G <= -0.25F) {
//                                    break;
//                                }
//                            }
//                            if (G >= 1) {
//                                G--;
//                            }
//                        }
//                    }
//                    if (ax >= client.bpx1 && ax <= client.bpx2 && ay >= client.bpy1 && ay <= client.bpy2) {
//                        client.pbu[z] = 1;
//                        client.pbx[z] = ax;
//                        client.pby[z] = ay;
//                        client.pba[z] = (float) Math.atan2(ay - lay, ax - lax);
//                    } else {
//                        client.pbu[z] = 0;
//                    }
//                    z++;
//                    float q = client.gsc * B * 52 / 32;
//                    I = client.gsc * B * 62 / 32;
//                    G = snake.aliveAmt * (1 - snake.deadAmt);
//                    G *= G;
//                    float E = 1;
//                    GL11.glPushMatrix();
//                    scale = client.gsc;
//                    GL11.glTranslatef(client.viewX, client.viewY, 0.0F);
//                    GL11.glScalef(scale, scale, 1.0F);
//                    E = snake.aliveAmt * (1.0F - snake.deadAmt) * Math.max(0, Math.min(1, (snake.tsp - snake.ssp) / (snake.msp - snake.ssp)));
//                    K = (float) Math.pow(E, 0.5F);
//                    float F = client.gsc * B * (K * 0.9375F + 1.0F);
//                    SkinColor[] rcbs = snake.rbcs;
//                    O = rcbs.length;
//                    for (int t = z - 1; t >= 0; t--) {
//                        if (client.pbu[t] == 1) {
//                            px = client.pbx[t];
//                            py = client.pby[t];
//                            SkinColor color = rcbs[(int) (t % O)];
//                            GL11.glColor4f(color.red, color.green, color.blue, (float) (G * K * 0.38F * (0.6F + 0.4F * Math.cos(t / 4 - 1.15 * snake.sfr))));
//                            if (t < 4) {
//                                float e = F * ((4.0F - t) * snake.swell + 1.0F);
//                                this.drawTexture((e + px) / scale, (e + py) / scale, 0.0F, 0.0F, 128.0F, 128.0F, 128.0F, 128.0F);
//                            } else {
//                                this.drawTexture((F + px) / scale, (F + py) / scale, 0.0F, 0.0F, 128.0F, 128.0F, 128.0F, 128.0F);
//                            }
//                        }
//                    }
//                    E = 1 - E;
//                    GL11.glPopMatrix();
//                    E *= G;
                    if (snake.iiv) {
                        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
                        GL11.glPushMatrix();
                        scale = client.gsc * 0.25F * (snake.sc);
                        GL11.glTranslatef(client.mww2, client.mhh2, 0.0F);
                        GL11.glScalef(scale, scale, 1.0F);
                        SkinColor[] pattern = snake.rbcs;
                        for (int t = snake.pts.size() - 1; t >= 0; t--) {
                            SnakePart part = snake.pts.get(t);
                            float partX = client.viewX - (part.posX + part.fx);
                            float partY = client.viewY - (part.posY + part.fy);
                            SkinColor color = pattern[t % pattern.length];
                            GL11.glColor4f(color.red, color.green, color.blue, 1.0F);
                            this.drawTexture(partX, partY, 0.0F, 0.0F, 128.0F, 128.0F, 128.0F, 128.0F);
                        }
                        GL11.glPopMatrix();
                        if (!snake.oneEye) {
                            GL11.glPushMatrix();
                            float t = 6.0F;
                            float x = 6.0F;
                            scale = client.gsc * 0.25F * (snake.sc);
                            GL11.glTranslatef(client.mww2, client.mhh2, 0.0F);
                            GL11.glScalef(scale, scale, 1.0F);
                            float eyeOffsetX = (float) (Math.cos(D) * t + Math.cos(D - Math.PI / 2.0F) * (x + 0.5F));
                            float eyeOffsetY = (float) (Math.sin(D) * t + Math.sin(D - Math.PI / 2.0F) * (x + 0.5F));
                            this.drawCircle((client.viewX - (eyeOffsetX + originX)) / scale, (client.viewY - (eyeOffsetY + originY)) / scale, snake.er * sc * client.gsc, snake.ec);
                            eyeOffsetX = (float) (Math.cos(D) * t + Math.cos(D + Math.PI / 2.0F) * (x + 0.5F));
                            eyeOffsetY = (float) (Math.sin(D) * t + Math.sin(D + Math.PI / 2.0F) * (x + 0.5F));
                            this.drawCircle((client.viewX - (eyeOffsetX + originX)) / scale, (client.viewY - (eyeOffsetY + originY)) / scale, snake.er * sc * client.gsc, snake.ec);
                            eyeOffsetX = (float) (Math.cos(D) * (t + 0.5F) + snake.rex * sc + Math.cos(D + Math.PI / 2.0F) * x);
                            eyeOffsetY = (float) (Math.sin(D) * (t + 0.5F) + snake.rey * sc + Math.sin(D + Math.PI / 2.0F) * x);
                            this.drawCircle((client.viewX - (eyeOffsetX + originX)) / scale, (client.viewY - (eyeOffsetY + originY)) / scale, 3.5F * sc * client.gsc, snake.ppc);
                            eyeOffsetX = (float) (Math.cos(D) * (t + 0.5F) + snake.rex * sc + Math.cos(D - Math.PI / 2.0F) * x);
                            eyeOffsetY = (float) (Math.sin(D) * (t + 0.5F) + snake.rey * sc + Math.sin(D - Math.PI / 2.0F) * x);
                            this.drawCircle((client.viewX - (eyeOffsetX + originX)) / scale, (client.viewY - (eyeOffsetY + originY)) / scale, 3.5F * sc * client.gsc, snake.ppc);
                            GL11.glPopMatrix();
                        }
                    }
                }
            }
            this.drawString("Your length: " + (int) Math.floor(15.0F * (client.getFPSL(player.sct) + player.fam / client.getFMLT(player.sct) - 1.0F) - 5.0F), 3.0F, renderResolution.getHeight() - 35.0F, 1.0F, 0xFFFFFF);
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

    public void drawCircle(float centerX, float centerY, float radius, int color) {
        GL11.glPushMatrix();
        GL11.glColor4f((color >> 16 & 0xFF) / 255.0F, (color >> 8 & 0xFF) / 255.0F, (color & 0xFF) / 255.0F, 1.0F);
        this.textureManager.bindTexture("/textures/circle.png");
        centerX -= radius / 2.0F;
        centerY -= radius / 2.0F;
        float scale = radius / 3.0F;
        GL11.glScalef(scale, scale, 1.0F);
        this.drawTexture(centerX / scale, centerY / scale, 0.0F, 0.0F, 64.0F, 64.0F, 64.0F, 64.0F);
        GL11.glPopMatrix();
    }

    private void drawString(String text, float x, float y, float scale, int color) {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glScalef(scale, scale, 0.0F);
        font.drawString(x / scale, y / scale, text, new org.newdawn.slick.Color(color));
        GL11.glPopMatrix();
    }

    private void drawLargeString(String text, float x, float y, float scale, int color) {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glScalef(scale, scale, 0.0F);
        largeFont.drawString(x / scale, y / scale, text, new org.newdawn.slick.Color(color));
        GL11.glPopMatrix();
    }

    private void drawTexture(float x, float y, float u, float v, float width, float height, float textureWidth, float textureHeight) {
        float uMultiplier = 1.0F / textureWidth;
        float vMultiplier = 1.0F / textureHeight;

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glBegin(GL11.GL_QUADS);

        this.drawVertex(x, y + height, u, v + height, uMultiplier, vMultiplier);
        this.drawVertex(x + width, y + height, u + width, v + height, uMultiplier, vMultiplier);
        this.drawVertex(x + width, y, u + width, v, uMultiplier, vMultiplier);
        this.drawVertex(x, y, u, v, uMultiplier, vMultiplier);

        GL11.glEnd();
    }

    private void drawVertex(float x, float y, float u, float v, float uMultiplier, float vMultiplier) {
        GL11.glTexCoord2f(u * uMultiplier, v * vMultiplier);
        GL11.glVertex2f(x, y);
    }
}
