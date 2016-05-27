package net.gegy1000.slyther.client.gui.element;

import net.gegy1000.slyther.client.gui.Gui;

public abstract class Element {
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
}
