package net.gegy1000.slyther.client.controller;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.client.game.entity.ClientSnake;
import net.gegy1000.slyther.network.message.client.MessageSetTurn;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

public class DefaultController implements IController {
    private float targetAngle;
    private boolean accelerating;

    @Override
    public void update(SlytherClient client) {
        ClientSnake player = client.player;
        long time = System.currentTimeMillis();
        if (client.keyDownLeftTicks > 0 || client.keyDownRightTicks > 0) {
            if (time - client.lastKeyTime > 150) {
                client.lastKeyTime = time;
                if (client.keyDownRightTicks > 0) {
                    if (client.keyDownRightTicks < client.keyDownLeftTicks) {
                        client.keyDownLeftTicks -= client.keyDownRightTicks;
                        client.keyDownRightTicks = 0;
                    }
                }
                if (client.keyDownLeftTicks > 0) {
                    if (client.keyDownLeftTicks < client.keyDownRightTicks) {
                        client.keyDownRightTicks -= client.keyDownLeftTicks;
                        client.keyDownLeftTicks = 0;
                    }
                }
                int direction;
                if (client.keyDownLeftTicks > 0) {
                    direction = (int) client.keyDownLeftTicks;
                    if (direction > 127) {
                        direction = 127;
                    }
                    client.keyDownLeftTicks -= direction;
                    player.eyeAngle -= client.getMamu() * direction * player.scaleTurnMultiplier * player.speedTurnMultiplier;
                } else {
                    direction = (int) client.keyDownRightTicks;
                    if (direction > 127) {
                        direction = 127;
                    }
                    client.keyDownRightTicks -= direction;
                    player.eyeAngle += client.getMamu() * direction * player.scaleTurnMultiplier * player.speedTurnMultiplier;
                }
                client.networkManager.send(new MessageSetTurn((byte) direction));
            }
        }
        accelerating = Mouse.isButtonDown(0) || Mouse.isButtonDown(1);
        int mouseX = Mouse.getX() - (Display.getWidth() / 2);
        int mouseY = (Display.getHeight() - Mouse.getY()) - (Display.getHeight() / 2);
        if (mouseX != client.lastMouseX || mouseY != client.lastMouseY) {
            client.mouseMoved = true;
        }
        if (client.mouseMoved) {
            client.mouseMoved = false;
            client.lastMouseX = mouseX;
            client.lastMouseY = mouseY;
            int dist = mouseX * mouseX + mouseY * mouseY;
            if (dist > 256) {
                targetAngle = (float) Math.atan2(mouseY, mouseX);
                player.eyeAngle = targetAngle;
            } else {
                targetAngle = player.wantedAngle;
            }
            targetAngle %= SlytherClient.PI_2;
            if (targetAngle < 0) {
                targetAngle += SlytherClient.PI_2;
            }
        }
    }

    @Override
    public float getTargetAngle() {
        return targetAngle;
    }

    @Override
    public boolean shouldAccelerate() {
        return accelerating;
    }
}
