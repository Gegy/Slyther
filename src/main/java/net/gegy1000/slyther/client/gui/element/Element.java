package net.gegy1000.slyther.client.gui.element;

import net.gegy1000.slyther.client.gui.Gui;

public abstract class Element {
    private static final String BUTTON_TEXTURE = "/textures/button.png";

    protected Gui gui;
    protected float posX;
    protected float posY;
    protected float width;
    protected float height;

    public Element(Gui gui, float posX, float posY, float width, float height) {
        this.gui = gui;
        this.posX = posX;
        this.posY = posY;
        this.width = width;
        this.height = height;
    }

    public abstract void keyPressed(int key, char character);

    public abstract void update();

    public abstract void render(float mouseX, float mouseY);

    public abstract boolean mouseClicked(float mouseX, float mouseY, int button);

    public boolean isSelected(float mouseX, float mouseY) {
        return mouseX >= posX && mouseX <= posX + width && mouseY >= posY && mouseY <= posY + height;
    }

    protected void drawButton(float x, float y, float width, float height) {
        gui.textureManager.bindTexture(BUTTON_TEXTURE);
        float effectiveWidth = width - 19.0F;
        float effectiveHeight = height - 32.0F;
        gui.drawTexture(x, y, 0.0F, 0.0F, 19.0F, 16.0F, 64.0F, 64.0F);
        gui.drawTexture(x + effectiveWidth, y, 20.0F, 0.0F, 19.0F, 16.0F, 64.0F, 64.0F);
        gui.drawTexture(x, y + effectiveHeight + 16.0F, 0.0F, 17.0F, 19.0F, 16.0F, 64.0F, 64.0F);
        gui.drawTexture(x + effectiveWidth, y + effectiveHeight + 16.0F, 20.0F, 17.0F, 19.0F, 16.0F, 64.0F, 64.0F);
        gui.drawRect(x + 16.0F, y, effectiveWidth - 16.0F, height);
        gui.drawRect(x, y + 16.0F, width, effectiveHeight);
    }
}
