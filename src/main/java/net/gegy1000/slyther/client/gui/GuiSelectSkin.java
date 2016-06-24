package net.gegy1000.slyther.client.gui;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.client.game.entity.ClientSnake;
import net.gegy1000.slyther.client.gui.element.ArrowElement;
import net.gegy1000.slyther.client.gui.element.ButtonElement;
import net.gegy1000.slyther.game.Color;
import net.gegy1000.slyther.game.Skin;
import net.gegy1000.slyther.game.entity.SnakePoint;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class GuiSelectSkin extends Gui {
    private GuiMainMenu menu;
    private ClientSnake snake;

    private int backgroundX;

    public GuiSelectSkin(GuiMainMenu menu) {
        this.menu = menu;
    }

    private void createSnake() {
        List<SnakePoint> points = new ArrayList<>();
        for (int i = 0; i < 23; i++) {
            SnakePoint point = new SnakePoint(client, i * 10.0F, 0.0F);
            point.deltaX = i == 0 ? 0.0F : 10.0F;
            points.add(point);
        }
        snake = new ClientSnake(client, "", 0, points.get(points.size() - 1).posX, 0.0F, client.configuration.skin, 0.0F, points);
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
        snake.updateLength();
        snake.aliveAmt = 1.0F;
        snake.relativeEyeX = 1.66F;
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
        backgroundX++;
        int snakePointIndex = 0;
        for (SnakePoint point : snake.points) {
            point.posY = (float) (15.0F * Math.cos(snakePointIndex / 4.0F + (client.frameTicks) / 4.0F) * (1.0F - ((float) snakePointIndex / snake.points.size())));
            snakePointIndex++;
        }
        textureManager.bindTexture("/textures/background.png");
        drawTexture(0.0F, 0.0F, backgroundX * 2.0F, 0, renderResolution.getWidth(), renderResolution.getHeight(), 599, 519);
        drawCenteredLargeString("Select Skin", renderResolution.getWidth() / 2.0F, 25.0F, 0.5F, 0xFFFFFF);
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glEnable(GL11.GL_ALPHA_TEST);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glTranslatef(renderHandler.centerX - (snake.points.get(snake.points.size() / 2).posX), renderHandler.centerY, 0.0F);
        float originX = snake.posX;
        float originY = snake.posY;
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
        Color[] pattern = snake.pattern;
        for (int pointIndex = snake.points.size() - 1; pointIndex >= 0; pointIndex--) {
            SnakePoint point = snake.points.get(pointIndex);
            lastX = x;
            lastY = y;
            x = point.posX;
            y = point.posY;
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
        textureManager.bindTexture("/textures/shadow.png");
        for (int pointIndex = xs.size() - 1; pointIndex >= 0; pointIndex--) {
            pointX = (xs.get(pointIndex));
            pointY = (ys.get(pointIndex));
            GL11.glPushMatrix();
            GL11.glTranslatef(pointX, pointY, 0);
            float pointScale = snake.scale * 0.35F;
            if (pointIndex < 4) {
                pointScale *= 1 + (4 - pointIndex) * snake.headSwell;
            }
            GL11.glScalef(pointScale, pointScale, 1.0F);
            drawTexture(-64, -64, 0, 0, 128, 128, 128, 128);
            GL11.glPopMatrix();
        }
        float prevPointX = 0.0F;
        float prevPointY = 0.0F;
        for (int pointIndex = xs.size() - 1; pointIndex >= 0; pointIndex--) {
            pointX = (xs.get(pointIndex));
            pointY = (ys.get(pointIndex));
            Color color = pattern[pointIndex % pattern.length];
            int i = pointIndex % 12;
            if (i > 6) {
                i = 6 - (i - 6);
            }
            textureManager.bindTexture("/textures/colors/snake_" + color.name().toLowerCase() + "_" + i + ".png");
            float colorMultiplier = 1.0F;
            float offset = (pointIndex / 3.0F % 6.0F);
            if (offset >= 3.0F) {
                offset = 3.0F - (offset - 3.0F);
            }
            colorMultiplier -= offset / 15.0F;
            GL11.glColor4f(colorMultiplier, colorMultiplier, colorMultiplier, 1.0F);
            GL11.glPushMatrix();
            GL11.glTranslatef(pointX, pointY, 0);
            float pointScale = snake.scale * 0.25F;
            if (pointIndex < 4) {
                pointScale *= 1 + (4 - pointIndex) * snake.headSwell;
            }
            GL11.glScalef(pointScale, pointScale, 1.0F);
            GL11.glRotatef((float) Math.toDegrees(pointIndex == xs.size() - 1 ? Math.atan2(pointY - ys.get(pointIndex - 1), pointX - xs.get(pointIndex - 1)) : Math.atan2(pointY - prevPointY, pointX - prevPointX)) - 180.0F, 0.0F, 0.0F, 1.0F);
            drawTexture(-64, -64, 0, 0, 128, 128, 128, 128);
            GL11.glPopMatrix();
            prevPointX = pointX;
            prevPointY = pointY;
        }
        if (snake.faceTexture == null && !snake.oneEye) {
            GL11.glPushMatrix();
            float eyeForward = 2.0F * scale;
            float eyeSideDistance = 6.0F * scale;
            GL11.glTranslatef(originX, originY, 0.0F);
            float eyeOffsetX = (float) (Math.cos(ehang) * eyeForward + Math.cos(ehang - Math.PI / 2.0F) * (eyeSideDistance + 0.5F));
            float eyeOffsetY = (float) (Math.sin(ehang) * eyeForward + Math.sin(ehang - Math.PI / 2.0F) * (eyeSideDistance + 0.5F));
            drawCircle(eyeOffsetX, eyeOffsetY, snake.eyeRadius * scale, snake.eyeColor);
            eyeOffsetX = (float) (Math.cos(ehang) * eyeForward + Math.cos(ehang + Math.PI / 2.0F) * (eyeSideDistance + 0.5F));
            eyeOffsetY = (float) (Math.sin(ehang) * eyeForward + Math.sin(ehang + Math.PI / 2.0F) * (eyeSideDistance + 0.5F));
            drawCircle(eyeOffsetX, eyeOffsetY, snake.eyeRadius * scale, snake.eyeColor);
            eyeOffsetX = (float) (Math.cos(ehang) * (eyeForward + 0.5F) + snake.relativeEyeX * scale + Math.cos(ehang + Math.PI / 2.0F) * eyeSideDistance);
            eyeOffsetY = (float) (Math.sin(ehang) * (eyeForward + 0.5F) + snake.relativeEyeY * scale + Math.sin(ehang + Math.PI / 2.0F) * eyeSideDistance);
            drawCircle(eyeOffsetX, eyeOffsetY, 3.5F * scale, snake.pupilColor);
            eyeOffsetX = (float) (Math.cos(ehang) * (eyeForward + 0.5F) + snake.relativeEyeX * scale + Math.cos(ehang - Math.PI / 2.0F) * eyeSideDistance);
            eyeOffsetY = (float) (Math.sin(ehang) * (eyeForward + 0.5F) + snake.relativeEyeY * scale + Math.sin(ehang - Math.PI / 2.0F) * eyeSideDistance);
            drawCircle(eyeOffsetX, eyeOffsetY, 3.5F * scale, snake.pupilColor);
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
            float directionX = (float) Math.cos(snake.angle);
            float directionY = (float) Math.sin(snake.angle);
            pointX = originX - 8 * directionX * snake.scale;
            pointY = originY - 8 * directionY * snake.scale;
            snake.antennaX[0] = pointX;
            snake.antennaY[0] = pointY;
            float antennaScale = snake.scale;
            int antennaLength = snake.antennaX.length - 1;
            if (!snake.antennaShown) {
                snake.antennaShown = true;
                for (int i = 1; i <= antennaLength; i++) {
                    snake.antennaX[i] = pointX - directionX * i * 4 * snake.scale;
                    snake.antennaY[i] = pointY - directionY * i * 4 * snake.scale;
                }
            }
            for (int i = 1; i <= antennaLength; i++) {
                snake.antennaVelocityX[i] -= 0.3F;
                snake.antennaVelocityY[i] += Math.cos(client.frameTicks / 5.0F - 7.0F * i / antennaLength) * 0.14F;
                x = (float) (snake.antennaX[i - 1] + (Math.random() * 2.0F - 1));
                y = (float) (snake.antennaY[i - 1] + (Math.random() * 2.0F - 1));
                float diffX = snake.antennaX[i] - x;
                float diffY = snake.antennaY[i] - y;
                float ang = (float) Math.atan2(diffY, diffX);
                x += Math.cos(ang) * snake.scale * 4.0F;
                y += Math.sin(ang) * snake.scale * 4.0F;
                snake.antennaVelocityX[i] += (x - snake.antennaX[i]) * 0.1F;
                snake.antennaVelocityY[i] += (y - snake.antennaY[i]) * 0.1F;
                snake.antennaX[i] += snake.antennaVelocityX[i];
                snake.antennaY[i] += snake.antennaVelocityY[i];
                snake.antennaVelocityX[i] *= 0.88F;
                snake.antennaVelocityY[i] *= 0.88F;
                diffX = snake.antennaX[i] - snake.antennaX[i - 1];
                diffY = snake.antennaY[i] - snake.antennaY[i - 1];
                float J = (float) Math.sqrt(diffX * diffX + diffY * diffY);
                if (J > snake.scale * 4.0F) {
                    ang = (float) Math.atan2(diffY, diffX);
                    snake.antennaX[i] = (float) (snake.antennaX[i - 1] + Math.cos(ang) * 4 * snake.scale);
                    snake.antennaY[i] = (float) (snake.antennaY[i - 1] + Math.sin(ang) * 4 * snake.scale);
                }
            }
            antennaLength = snake.antennaX.length;
            float prevX = snake.antennaX[0];
            float prevY = snake.antennaY[0];
            beginConnectedLines(antennaScale * 5.0F, snake.antennaPrimaryColor);
            for (int i = 0; i < antennaLength; i++) {
                x = snake.antennaX[i];
                y = snake.antennaY[i];
                if (Math.abs(x - prevX) + Math.abs(y - prevY) >= 1) {
                    drawConnectedLine(prevX, prevY, x, y);
                    prevX = x;
                    prevY = y;
                }
            }
            endConnectedLines();
            prevX = snake.antennaX[0];
            prevY = snake.antennaY[0];
            beginConnectedLines(antennaScale * 4.0F, snake.antennaSecondaryColor);
            for (int i = 0; i < antennaLength; i++) {
                x = snake.antennaX[i];
                y = snake.antennaY[i];
                if (Math.abs(x - prevX) + Math.abs(y - prevY) >= 1) {
                    drawConnectedLine(prevX, prevY, x, y);
                    prevX = x;
                    prevY = y;
                }
            }
            endConnectedLines();
            if (snake.antennaTexture != null) {
                GL11.glTranslatef(snake.antennaX[antennaLength - 1], snake.antennaY[antennaLength - 1], 0.0F);
                antennaScale = snake.scale * snake.antennaScale * 0.25F;
                if (snake.antennaBottomRotate) {
                    float bottomAngle = (float) (Math.atan2(snake.antennaY[antennaLength - 1] - snake.antennaY[antennaLength - 2], snake.antennaX[antennaLength - 1] - snake.antennaX[antennaLength - 2]) - snake.antennaBottomAngle);
                    if (bottomAngle < 0 || bottomAngle >= SlytherClient.PI_2) {
                        bottomAngle %= SlytherClient.PI_2;
                    }
                    if (bottomAngle < -Math.PI) {
                        bottomAngle += SlytherClient.PI_2;
                    } else {
                        if (bottomAngle > Math.PI) {
                            bottomAngle -= SlytherClient.PI_2;
                        }
                    }
                    snake.antennaBottomAngle = (float) ((snake.antennaBottomAngle + 0.15F * bottomAngle) % SlytherClient.PI_2);
                    GL11.glRotatef((float) Math.toDegrees(snake.antennaBottomAngle), 0.0F, 0.0F, 1.0F);
                    GL11.glTranslatef(32.0F * antennaScale, 0.0F, 0.0F);
                }
                GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
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
