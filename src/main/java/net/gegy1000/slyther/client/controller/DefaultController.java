package net.gegy1000.slyther.client.controller;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.client.game.entity.ClientSnake;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

public class DefaultController implements IController {
    private float targetAngle;
    private boolean accelerating;

    private int lastMouseX;
    private int lastMouseY;

    @Override
    public void update(SlytherClient client) {
        ClientSnake player = client.player;
        accelerating = Mouse.isButtonDown(0) || Mouse.isButtonDown(1);
        int mouseX = Mouse.getX() - (Display.getWidth() / 2);
        int mouseY = (Display.getHeight() - Mouse.getY()) - (Display.getHeight() / 2);
        if (mouseX != lastMouseX || mouseY != lastMouseY) {
            lastMouseX = mouseX;
            lastMouseY = mouseY;
            int dist = mouseX * mouseX + mouseY * mouseY;
            if (dist > 256) {
                targetAngle = (float) Math.atan2(mouseY, mouseX);
                player.eyeAngle = targetAngle;
            } else {
                targetAngle = player.wantedAngle;
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
