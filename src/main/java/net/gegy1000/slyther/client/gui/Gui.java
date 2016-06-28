package net.gegy1000.slyther.client.gui;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.client.gui.element.Element;
import net.gegy1000.slyther.client.render.RenderHandler;
import net.gegy1000.slyther.client.render.RenderResolution;
import net.gegy1000.slyther.client.render.TextureManager;
import net.gegy1000.slyther.util.Log;
import org.lwjgl.opengl.GL11;
import org.newdawn.slick.TrueTypeFont;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class Gui {
    public RenderHandler renderHandler;
    public RenderResolution renderResolution;
    public SlytherClient client;
    public TextureManager textureManager;
    public TrueTypeFont font;
    public TrueTypeFont largeFont;

    protected List<Element> elements = new ArrayList<>();

    public final void initBase(SlytherClient client, RenderHandler renderHandler) {
        this.client = client;
        this.renderHandler = renderHandler;
        renderResolution = renderHandler.renderResolution;
        textureManager = renderHandler.textureManager;
        font = renderHandler.font;
        largeFont = renderHandler.largeFont;
        elements.clear();
        init();
    }

    public abstract void init();

    public final void renderBase(float mouseX, float mouseY) {
        try {
            render(mouseX, mouseY);
            for (Element element : elements) {
                element.render(mouseX, mouseY);
            }
        } catch (Exception e) {
            Log.error("Error while rendering " + this);
            Log.catching(e);
        }
    }

    public abstract void render(float mouseX, float mouseY);

    public final void updateBase() {
        for (Element element : elements) {
            element.update();
        }
        update();
    }

    public abstract void update();

    public final void keyPressedBase(int key, char character) {
        for (Element element : elements) {
            element.keyPressed(key, character);
        }
        keyPressed(key, character);
    }

    public abstract void keyPressed(int key, char character);

    public final void mouseClickedBase(float mouseX, float mouseY, int button) {
        List<Element> elements = new ArrayList<>(this.elements);
        Collections.reverse(elements);
        for (Element element : elements) {
            if (element.mouseClicked(mouseX, mouseY, button)) {
                break;
            }
        }
        mouseClicked(mouseX, mouseY, button);
    }

    public abstract void mouseClicked(float mouseX, float mouseY, int button);

    public void drawLine(float x1, float y1, float x2, float y2, float width, int color) {
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f((color >> 16 & 0xFF) / 255.0F, (color >> 8 & 0xFF) / 255.0F, (color & 0xFF) / 255.0F, 1.0F);
        GL11.glLineWidth(width * renderResolution.getScale());
        GL11.glBegin(GL11.GL_LINES);
        GL11.glVertex2f(x1, y1);
        GL11.glVertex2f(x2, y2);
        GL11.glEnd();
        GL11.glPopMatrix();
    }

    public void beginConnectedLines(float width, int color) {
        GL11.glPushMatrix();
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glColor4f((color >> 16 & 0xFF) / 255.0F, (color >> 8 & 0xFF) / 255.0F, (color & 0xFF) / 255.0F, 1.0F);
        GL11.glLineWidth(width * renderResolution.getScale());
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glBegin(GL11.GL_LINES);
    }

    public void drawConnectedLine(float x1, float y1, float x2, float y2) {
        GL11.glVertex2f(x1, y1);
        GL11.glVertex2f(x2, y2);
    }

    public void endConnectedLines() {
        GL11.glEnd();
        GL11.glPopMatrix();
    }

    public void drawCircle(float centerX, float centerY, float radius, int color) {
        GL11.glColor4f((color >> 16 & 0xFF) / 255.0F, (color >> 8 & 0xFF) / 255.0F, (color & 0xFF) / 255.0F, 1.0F);
        GL11.glPushMatrix();
        GL11.glTranslatef(centerX - radius, centerY - radius, 0.0F);
        radius /= 128.0F;
        GL11.glScalef(radius, radius, 1.0F);
        renderHandler.textureManager.bindTexture("/textures/circle.png");
        drawTexture(0.0F, 0.0F, 0.0F, 0.0F, 256.0F, 256.0F, 256.0F, 256.0F);
        GL11.glPopMatrix();
    }

    public void drawString(String text, float x, float y, float scale, int color) {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glScalef(scale, scale, 1.0F);
        font.drawString(x / scale, y / scale, text, new org.newdawn.slick.Color(color));
        GL11.glPopMatrix();
    }

    public void drawCenteredString(String text, float x, float y, float scale, int color) {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glScalef(scale, scale, 1.0F);
        font.drawString(x / scale - font.getWidth(text) / 2.0F, y / scale - font.getHeight() / 2.0F, text, new org.newdawn.slick.Color(color));
        GL11.glPopMatrix();
    }

    public void drawLargeString(String text, float x, float y, float scale, int color) {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glScalef(scale, scale, 1.0F);
        largeFont.drawString(x / scale, y / scale, text, new org.newdawn.slick.Color(color));
        GL11.glPopMatrix();
    }

    public void drawCenteredLargeString(String text, float x, float y, float scale, int color) {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glScalef(scale, scale, 1.0F);
        largeFont.drawString(x / scale - largeFont.getWidth(text) / 2.0F, y / scale - largeFont.getHeight() / 2.0F, text, new org.newdawn.slick.Color(color));
        GL11.glPopMatrix();
    }

    public void drawTexture(float x, float y, float u, float v, float width, float height, float textureWidth, float textureHeight) {
        float uMultiplier = 1.0F / textureWidth;
        float vMultiplier = 1.0F / textureHeight;

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glShadeModel(GL11.GL_FLAT);

        drawVertex(x, y + height, u, v + height, uMultiplier, vMultiplier);
        drawVertex(x + width, y + height, u + width, v + height, uMultiplier, vMultiplier);
        drawVertex(x + width, y, u + width, v, uMultiplier, vMultiplier);
        drawVertex(x, y, u, v, uMultiplier, vMultiplier);

        GL11.glEnd();
    }

    public void drawTexture(float x, float y, float width, float height) {
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glShadeModel(GL11.GL_FLAT);
        GL11.glTexCoord2f(0, 0);
        GL11.glVertex2f(x, y);
        GL11.glTexCoord2f(1, 0);
        GL11.glVertex2f(x + width, y);
        GL11.glTexCoord2f(1, 1);
        GL11.glVertex2f(x + width, y + height);
        GL11.glTexCoord2f(0, 1);
        GL11.glVertex2f(x, y + height);
        GL11.glEnd();
    }

    public void drawTexture(float x, float y, float u, float v, float width, float height, float actualWidth, float actualHeight, float textureWidth, float textureHeight) {
        float uMultiplier = 1.0F / textureWidth;
        float vMultiplier = 1.0F / textureHeight;

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glBegin(GL11.GL_QUADS);
        GL11.glShadeModel(GL11.GL_FLAT);

        drawVertex(x, y, u, v, uMultiplier, vMultiplier);
        drawVertex(x + actualWidth, y, u + width, v, uMultiplier, vMultiplier);
        drawVertex(x + actualWidth, y + actualHeight, u + width, v + height, uMultiplier, vMultiplier);
        drawVertex(x, y + actualHeight, u, v + height, uMultiplier, vMultiplier);

        GL11.glEnd();
    }

    public void drawRect(float x, float y, float width, float height) {
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glRectf(x, y, x + width, y + height);
    }

    public void drawRect(float x, float y, float width, float height, int color) {
        GL11.glColor4f((color >> 16 & 0xFF) / 255.0F, (color >> 8 & 0xFF) / 255.0F, (color & 0xFF) / 255.0F, 1.0F);
        drawRect(x, y, width, height);
    }

    public void drawRectAlpha(float x, float y, float width, float height, int color) {
        GL11.glColor4f((color >> 16 & 0xFF) / 255.0F, (color >> 8 & 0xFF) / 255.0F, (color & 0xFF) / 255.0F, (color >> 24 & 0xFF) / 255.0F);
        drawRect(x, y, width, height);
    }

    public void drawVertex(float x, float y, float u, float v, float uMultiplier, float vMultiplier) {
        GL11.glTexCoord2f(u * uMultiplier, v * vMultiplier);
        GL11.glVertex2f(x, y);
    }

    public final void closeGui() {
        renderHandler.closeGui(this);
    }
}
