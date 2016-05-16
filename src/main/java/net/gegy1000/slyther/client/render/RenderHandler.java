package net.gegy1000.slyther.client.render;

import net.gegy1000.slyther.client.SlytherClient;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;

public class RenderHandler {
    private TextureManager textureManager;
    private RenderResolution renderResolution;

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
        } catch (LWJGLException e) {
            e.printStackTrace();
        }
    }

    public void resetResolution() {
        renderResolution = new RenderResolution();
    }

    public void render(SlytherClient client) {
        renderResolution.applyScale();
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
