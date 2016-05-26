package net.gegy1000.slyther.client.gui.element;

import net.gegy1000.slyther.client.gui.Gui;
import org.lwjgl.opengl.GL11;

import java.util.function.Function;

public class ButtonElement extends Element {
    private static final String BUTTON_TEXTURE = "/textures/button.png";
    private String text;
    private Function<ButtonElement, Boolean> function;

    public ButtonElement(Gui gui, String text, float posX, float posY, float width, float height, Function<ButtonElement, Boolean> function) {
        super(gui, posX - (width / 2.0F), posY - (height / 2.0F), width, height);
        this.text = text;
        this.function = function;
    }

    @Override
    public void render(float mouseX, float mouseY) {
        this.gui.textureManager.bindTexture(BUTTON_TEXTURE);
        boolean selected = this.isSelected(mouseX, mouseY);
        if (selected) {
            GL11.glColor4f(0.8F, 0.8F, 0.8F, 1.0F);
        } else {
            GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        }
        float effectiveWidth = width - 19.0F;
        float effectiveHeight = height - 32.0F;
        this.gui.drawTexture(posX, posY, 0.0F, 0.0F, 19.0F, 16.0F, 64.0F, 64.0F);
        this.gui.drawTexture(posX + effectiveWidth, posY, 20.0F, 0.0F, 19.0F, 16.0F, 64.0F, 64.0F);
        this.gui.drawTexture(posX, posY + effectiveHeight + 16.0F, 0.0F, 17.0F, 19.0F, 16.0F, 64.0F, 64.0F);
        this.gui.drawTexture(posX + effectiveWidth, posY + effectiveHeight + 16.0F, 20.0F, 17.0F, 19.0F, 16.0F, 64.0F, 64.0F);
        this.gui.drawRect(posX + 16.0F, posY, effectiveWidth - 16.0F, height, selected ? 0x3A7E5C : 0x489E73);
        this.gui.drawRect(posX, posY + 16.0F, width, effectiveHeight, selected ? 0x3A7E5C : 0x489E73);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        this.gui.drawCenteredString(this.text, this.posX + (this.width / 2.0F), this.posY + (this.height / 2.0F), 1.0F, 0xFFFFFF);
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
