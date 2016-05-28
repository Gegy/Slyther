package net.gegy1000.slyther.client.gui.element;

import net.gegy1000.slyther.client.gui.Gui;
import org.lwjgl.opengl.GL11;

import java.util.function.Function;

public class ArrowElement extends Element {
    private static final String LEFT_TEXTURE = "/textures/previous_arrow.png";
    private static final String RIGHT_TEXTURE = "/textures/next_arrow.png";
    private boolean type;
    private Function<ArrowElement, Boolean> function;

    public ArrowElement(Gui gui, float posX, float posY, boolean type, Function<ArrowElement, Boolean> function) {
        super(gui, posX - 44, posY - 44, 88, 88);
        this.type = type;
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
        gui.textureManager.bindTexture(type ? RIGHT_TEXTURE : LEFT_TEXTURE);
        boolean selected = isSelected(mouseX, mouseY);
        int color = selected ? 0x3A7E5C : 0x489E73;
        GL11.glColor4f((color >> 16 & 0xFF) / 255.0F, (color >> 8 & 0xFF) / 255.0F, (color & 0xFF) / 255.0F, 1.0F);
        gui.drawTexture(posX, posY, 0.0F, 0.0F, 128.0F, 128.0F, 128.0F, 128.0F);
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
