package net.gegy1000.slyther.client.gui.element;

import net.gegy1000.slyther.client.gui.Gui;
import org.lwjgl.opengl.GL11;

import java.util.function.Function;

public class ButtonElement extends Element {
    private String text;
    private Function<ButtonElement, Boolean> function;

    public ButtonElement(Gui gui, String text, float posX, float posY, float width, float height, Function<ButtonElement, Boolean> function) {
        super(gui, posX - (width / 2.0F), posY - (height / 2.0F), width, height);
        this.text = text;
        this.function = function;
    }

    @Override
    public void keyPressed(int key, char character) {
    }

    @Override
    public void update() {
    }

    @Override
    public void render(float mouseX, float mouseY) {
        boolean selected = isSelected(mouseX, mouseY);
        int color = selected ? 0x3A7E5C : 0x489E73;
        GL11.glColor4f((color >> 16 & 0xFF) / 255.0F, (color >> 8 & 0xFF) / 255.0F, (color & 0xFF) / 255.0F, 1.0F);
        drawButton(posX, posY, width, height);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        gui.drawCenteredString(text, posX + (width / 2.0F), posY + (height / 2.0F), 0.5F, 0xFFFFFF);
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, int button) {
        boolean selected = button == 0 && super.isSelected(mouseX, mouseY);
        if (selected) {
            return function.apply(this);
        }
        return false;
    }
}
