package net.gegy1000.slyther.client.gui;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.client.gui.element.ArrowElement;
import net.gegy1000.slyther.client.gui.element.ButtonElement;
import net.gegy1000.slyther.game.Skin;
import net.gegy1000.slyther.game.SkinColor;
import net.gegy1000.slyther.game.entity.Snake;
import net.gegy1000.slyther.game.entity.SnakePoint;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class GuiSelectSkin extends Gui {
    private int backgroundX;
    private GuiMainMenu menu;
    private Snake snake;

    public GuiSelectSkin(GuiMainMenu menu) {
        this.menu = menu;
    }

    private void createSnake() {
        List<SnakePoint> points = new ArrayList<>();
        for (int i = 0; i < 22; i++) {
            SnakePoint point = new SnakePoint(i * 10.0F, 0.0F);
            point.deltaX = i == 0 ? 0.0F : 10.0F;
            points.add(point);
        }
        snake = new Snake(client, "", 0, points.get(points.size() - 1).posX, 0.0F, client.configuration.skin, 0.0F, points);
        snake.speed = 4.8F;
        snake.speedTurnMultiplier = snake.speed / client.getSpangDv();
        if (snake.speedTurnMultiplier > 1) {
            snake.speedTurnMultiplier = 1;
        }
        snake.scale = 1.0F;
        snake.scaleTurnMultiplier = 1.0F;
        snake.moveSpeed = client.getNsp1() + client.getNsp2() * snake.scale;
        snake.accelleratingSpeed = snake.moveSpeed + 0.1F;
        snake.wantedSeperation = snake.scale * 6.0F;
        float nsep = SlytherClient.NSEP;
        if (snake.wantedSeperation < nsep) {
            snake.wantedSeperation = nsep;
        }
        snake.partSeparation = snake.wantedSeperation;
        snake.snl();
        snake.aliveAmt = 1.0F;
        snake.rex = 1.66F;
        update();
    }

    @Override
    public void init() {
        createSnake();
        elements.add(new ArrowElement(this, renderResolution.getWidth() / 6.0F, renderResolution.getHeight() / 2.0F, false, (arrow) -> {
            updateSkin(false);
            return true;
        }));
        elements.add(new ArrowElement(this, renderResolution.getWidth() - renderResolution.getWidth() / 6.0F, renderResolution.getHeight() / 2.0F, true, (arrow) -> {
            updateSkin(true);
            return true;
        }));
        elements.add(new ButtonElement(this, "Done", renderResolution.getWidth() / 2.0F, renderResolution.getHeight() - 40.0F, 100.0F, 40.0F, (button) -> {
            exit();
            return true;
        }));
    }

    @Override
    public void render(float mouseX, float mouseY) {
        textureManager.bindTexture("/textures/background.png");
        drawTexture(0.0F, 0.0F, backgroundX, 0, renderResolution.getWidth(), renderResolution.getHeight(), 599, 519);
        drawCenteredLargeString("Select Skin", renderResolution.getWidth() / 2.0F, 25.0F, 1.0F, 0xFFFFFF);
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glTranslatef(client.mww2 - (snake.points.get(snake.points.size() / 2).posX), client.mhh2, 0.0F);
        float originX = snake.posX + snake.fx;
        float originY = snake.posY + snake.fy;
        float ehang = snake.ehang;
        float scale = snake.scale;
        List<Float> xs = new ArrayList<>();
        List<Float> ys = new ArrayList<>();
        float lastX;
        float lastY;
        float lastPointX;
        float lastPointY;
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
        float pointX = originX;
        float pointY = originY;
        float G = (float) (snake.cfl + (1.0F - Math.ceil((snake.chl + snake.fchl) / 0.25F) * 0.25F));
        float K = 0;
        float O = snake.wantedSeperation * client.qsm;
        SkinColor[] pattern = snake.pattern;
        for (int pointIndex = snake.points.size() - 1; pointIndex >= 0; pointIndex--) {
            SnakePoint point = snake.points.get(pointIndex);
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
                    float positionScale = n + q;
                    float e = lastAverageX + (lastX - lastAverageX) * positionScale;
                    float w = lastAverageY + (lastY - lastAverageY) * positionScale;
                    float J = lastX + (averageX - lastX) * positionScale;
                    float M = lastY + (averageY - lastY) * positionScale;
                    lastPointX = pointX;
                    lastPointY = pointY;
                    pointX = e + (J - e) * positionScale;
                    pointY = w + (M - w) * positionScale;
                    if (G < 0) {
                        pointX += -(lastPointX - pointX) * G / 0.25F;
                        pointY += -(lastPointY - pointY) * G / 0.25F;
                    }
                    float partDistance = (float) Math.sqrt(Math.pow(pointX - lastPointX, 2) + Math.pow(pointY - lastPointY, 2));
                    if (K + partDistance < O) {
                        K += partDistance;
                    } else {
                        K = -K;
                        for (int a = (int) ((partDistance - K) / O); a >= 1; a--) {
                            K += O;
                            xs.add(lastPointX + (pointX - lastPointX) * K / partDistance);
                            ys.add(lastPointY + (pointY - lastPointY) * K / partDistance);
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
        xs.add(pointX);
        ys.add(pointY);
        textureManager.bindTexture("/textures/snake_point.png");
        for (int pointIndex = xs.size() - 1; pointIndex >= 0; pointIndex--) {
            pointX = (xs.get(pointIndex));
            pointY = (ys.get(pointIndex));
            SkinColor color = pattern[pointIndex % pattern.length];
            GL11.glColor4f(color.red, color.green, color.blue, 1.0F);
            GL11.glPushMatrix();
            GL11.glTranslatef(pointX, pointY, 0);
            GL11.glScalef(snake.scale * 0.25F, snake.scale * 0.25F, 1);
            drawTexture(-64, -64, 0, 0, 128, 128, 128, 128);
            GL11.glPopMatrix();
        }
        if (snake.faceTexture == null && !snake.oneEye) {
            GL11.glPushMatrix();
            float eyeForward = 2.0F * scale;
            float eyeSideDistance = 6.0F * scale;
            GL11.glTranslatef(originX, originY, 0.0F);
            float eyeOffsetX = (float) (Math.cos(ehang) * eyeForward + Math.cos(ehang - Math.PI / 2.0F) * (eyeSideDistance + 0.5F));
            float eyeOffsetY = (float) (Math.sin(ehang) * eyeForward + Math.sin(ehang - Math.PI / 2.0F) * (eyeSideDistance + 0.5F));
            drawCircle(eyeOffsetX, eyeOffsetY, snake.er * scale, snake.eyeColor);
            eyeOffsetX = (float) (Math.cos(ehang) * eyeForward + Math.cos(ehang + Math.PI / 2.0F) * (eyeSideDistance + 0.5F));
            eyeOffsetY = (float) (Math.sin(ehang) * eyeForward + Math.sin(ehang + Math.PI / 2.0F) * (eyeSideDistance + 0.5F));
            drawCircle(eyeOffsetX, eyeOffsetY, snake.er * scale, snake.eyeColor);
            eyeOffsetX = (float) (Math.cos(ehang) * (eyeForward + 0.5F) + snake.rex * scale + Math.cos(ehang + Math.PI / 2.0F) * eyeSideDistance);
            eyeOffsetY = (float) (Math.sin(ehang) * (eyeForward + 0.5F) + snake.rey * scale + Math.sin(ehang + Math.PI / 2.0F) * eyeSideDistance);
            drawCircle(eyeOffsetX, eyeOffsetY, 3.5F * scale, snake.ppc);
            eyeOffsetX = (float) (Math.cos(ehang) * (eyeForward + 0.5F) + snake.rex * scale + Math.cos(ehang - Math.PI / 2.0F) * eyeSideDistance);
            eyeOffsetY = (float) (Math.sin(ehang) * (eyeForward + 0.5F) + snake.rey * scale + Math.sin(ehang - Math.PI / 2.0F) * eyeSideDistance);
            drawCircle(eyeOffsetX, eyeOffsetY, 3.5F * scale, snake.ppc);
            GL11.glPopMatrix();
        } else if (snake.faceTexture != null) {
            GL11.glPushMatrix();
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
            GL11.glTranslatef(originX, originY, 0.0F);
            float faceScale = 0.2F;
            GL11.glScalef(snake.scale * faceScale, snake.scale * faceScale, 1.0F);
            GL11.glRotatef((float) Math.toDegrees(snake.angle + (snake.eyeAngle / 10.0F)), 0.0F, 0.0F, 1.0F);
            GL11.glTranslatef(-5.0F / faceScale, 0.0F, 0.0F);
            textureManager.bindTexture("/textures/" + snake.faceTexture + ".png");
            drawTexture(-64.0F, -64.0F, 0.0F, 0.0F, 128.0F, 128.0F, 128.0F, 128.0F);
            GL11.glPopMatrix();
        }
        if (snake.antenna) {
            GL11.glPushMatrix();
            float e = (float) Math.cos(snake.angle);
            float w = (float) Math.sin(snake.angle);
            pointX = originX - 8 * e * snake.scale;
            pointY = originY - 8 * w * snake.scale;
            snake.antennaX[0] = pointX;
            snake.antennaY[0] = pointY;
            float antennaScale = snake.scale;
            int fj = snake.antennaX.length - 1;
            if (!snake.antennaShown) {
                snake.antennaShown = true;
                for (int t = 1; t <= fj; t++) {
                    snake.antennaX[t] = pointX - e * t * 4 * snake.scale;
                    snake.antennaY[t] = pointY - w * t * 4 * snake.scale;
                }
            }
            for (int t = 1; t <= fj; t++) {
                snake.antennaVelocityX[t] -= 0.3F;
                snake.antennaVelocityY[t] += Math.cos(client.ticks / 23.0F - 7.0F * y / fj) * 0.14F;
                x = (float) (snake.antennaX[t - 1] + (Math.random() * 2.0F - 1));
                y = (float) (snake.antennaY[t - 1] + (Math.random() * 2.0F - 1));
                e = snake.antennaX[t] - x;
                w = snake.antennaY[t] - y;
                float ang = (float) Math.atan2(w, e);
                x += Math.cos(ang) * snake.scale * 4.0F;
                y += Math.sin(ang) * snake.scale * 4.0F;
                snake.antennaVelocityX[t] += (x - snake.antennaX[t]) * 0.1F;
                snake.antennaVelocityY[t] += (y - snake.antennaY[t]) * 0.1F;
                snake.antennaX[t] += snake.antennaVelocityX[t];
                snake.antennaY[t] += snake.antennaVelocityY[t];
                snake.antennaVelocityX[t] *= 0.88F;
                snake.antennaVelocityY[t] *= 0.88F;
                e = snake.antennaX[t] - snake.antennaX[t - 1];
                w = snake.antennaY[t] - snake.antennaY[t - 1];
                float J = (float) Math.sqrt(e * e + w * w);
                if (J > snake.scale * 4.0F) {
                    ang = (float) Math.atan2(w, e);
                    snake.antennaX[t] = (float) (snake.antennaX[t - 1] + Math.cos(ang) * 4 * snake.scale);
                    snake.antennaY[t] = (float) (snake.antennaY[t - 1] + Math.sin(ang) * 4 * snake.scale);
                }
            }
            fj = snake.antennaX.length;
            float prevX = snake.antennaX[fj - 1];
            float prevY = snake.antennaY[fj - 1];
            beginConnectedLines(antennaScale * 5.0F, snake.atc1);
            for (int t = 0; t < fj; t++) {
                x = snake.antennaX[t];
                y = snake.antennaY[t];
                if (Math.abs(x - prevX) + Math.abs(y - prevY) >= 1) {
                    drawConnectedLine(prevX, prevY, x, y);
                    prevX = x;
                    prevY = y;
                }
            }
            endConnectedLines();
            beginConnectedLines(antennaScale * 4.0F, snake.atc2);
            for (int t = 0; t < fj; t++) {
                x = snake.antennaX[t];
                y = snake.antennaY[t];
                if (Math.abs(x - prevX) + Math.abs(y - prevY) >= 1) {
                    drawConnectedLine(prevX, prevY, x, y);
                    prevX = x;
                    prevY = y;
                }
            }
            endConnectedLines();
            if (snake.antennaTexture != null) {
                GL11.glTranslatef(snake.antennaX[fj - 1], snake.antennaY[fj - 1], 0.0F);
                if (snake.antennaBottomRotate) {
                    float vang = (float) (Math.atan2(snake.antennaY[fj - 1] - snake.antennaY[fj - 2], snake.antennaX[fj - 1] - snake.antennaX[fj - 2]) - snake.atba);
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
                antennaScale = snake.scale * snake.antennaScale * 0.25F;
                GL11.glScalef(antennaScale, antennaScale, 1.0F);
                textureManager.bindTexture("/textures/" + snake.antennaTexture + ".png");
                drawTexture(-64.0F, -64.0F, 0.0F, 0.0F, 128.0F, 128.0F, 128.0F, 128.0F);
            }
            GL11.glPopMatrix();
        }
        GL11.glPopMatrix();
    }

    @Override
    public void update() {
        backgroundX++;
        int i = 0;
        for (SnakePoint point : snake.points) {
            point.posY = (float) (15.0F * Math.cos(i / 4.0F + client.ticks / 19.0F) * (1.0F - i / snake.points.size()));
            i++;
        }
        snake.posY = snake.points.get(snake.points.size() - 1).posY;
    }

    @Override
    public void keyPressed(int key, char character) {
        if (key == Keyboard.KEY_RIGHT || key == Keyboard.KEY_LEFT) {
            updateSkin(key == Keyboard.KEY_RIGHT);
        } else if (key == Keyboard.KEY_ESCAPE || key == Keyboard.KEY_BACK) {
            exit();
        }
    }

    private void exit() {
        closeGui();
        renderHandler.openGui(menu);
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, int button) {

    }

    public void updateSkin(boolean side) {
        int skin = client.configuration.skin.ordinal();
        if (side) {
            skin++;
        } else {
            skin--;
        }
        Skin[] values = Skin.values();
        skin %= values.length;
        if (skin < 0) {
            skin += values.length;
        }
        client.configuration.skin = values[skin];
        client.saveConfig();
        createSnake();
    }
}
