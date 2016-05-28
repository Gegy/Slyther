package net.gegy1000.slyther.client.gui.element;

import net.gegy1000.slyther.client.gui.Gui;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.function.Function;

public class TextBoxElement extends Element {
    private static final String TEXTURE = "/textures/button.png";
    private String text;
    private Function<TextBoxElement, Void> function;
    private boolean selected;
    private int tick;

    public TextBoxElement(Gui gui, String text, float posX, float posY, float width, float height, Function<TextBoxElement, Void> function) {
        super(gui, posX - (width / 2.0F), posY - (height / 2.0F), width, height);
        this.text = text;
        this.function = function;
    }

    @Override
    public void keyPressed(int key, char character) {
        if (selected) {
            if (key == Keyboard.KEY_BACK) {
                if (text.length() > 0) {
                    text = text.substring(0, text.length() - 1);
                }
            } else if (character != 167 && character >= 32 && character != 127) {
                text += character;
            }
            function.apply(this);
        }
    }

    @Override
    public void update() {
        tick++;
    }

    @Override
    public void render(float mouseX, float mouseY) {
        gui.textureManager.bindTexture(TEXTURE);
        int color = selected ? 0x684782 : 0x8D60AF;
        GL11.glColor4f((color >> 16 & 0xFF) / 255.0F, (color >> 8 & 0xFF) / 255.0F, (color & 0xFF) / 255.0F, 1.0F);
        float effectiveWidth = width - 19.0F;
        float effectiveHeight = height - 32.0F;
        gui.drawTexture(posX, posY, 0.0F, 0.0F, 19.0F, 16.0F, 64.0F, 64.0F);
        gui.drawTexture(posX + effectiveWidth, posY, 20.0F, 0.0F, 19.0F, 16.0F, 64.0F, 64.0F);
        gui.drawTexture(posX, posY + effectiveHeight + 16.0F, 0.0F, 17.0F, 19.0F, 16.0F, 64.0F, 64.0F);
        gui.drawTexture(posX + effectiveWidth, posY + effectiveHeight + 16.0F, 20.0F, 17.0F, 19.0F, 16.0F, 64.0F, 64.0F);
        gui.drawRect(posX + 16.0F, posY, effectiveWidth - 16.0F, height);
        gui.drawRect(posX, posY + 16.0F, width, effectiveHeight);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        gui.drawCenteredString(text, posX + (width / 2.0F), posY + (height / 2.0F), 1.0F, 0xFFFFFF);
        if (selected && tick % 40 > 20) {
            gui.drawRect(posX + (width / 2.0F) + (gui.font.getWidth(text) / 2.0F), posY + (height / 2.0F) - (gui.font.getHeight() / 2.0F), 1.0F, gui.font.getHeight());
        }
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, int button) {
        selected = button == 0 && super.isSelected(mouseX, mouseY);
        return false;
    }

    public String getText() {
        return text;
    }
}