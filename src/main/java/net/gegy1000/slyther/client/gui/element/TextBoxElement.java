package net.gegy1000.slyther.client.gui.element;

import net.gegy1000.slyther.client.gui.Gui;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.function.Function;

public class TextBoxElement extends Element {
    private String text;
    private Function<TextBoxElement, Void> function;
    private boolean selected;
    private int tick;
    private int selectionIndex;

    public TextBoxElement(Gui gui, String text, float posX, float posY, float width, float height, Function<TextBoxElement, Void> function) {
        super(gui, posX - (width / 2.0F), posY - (height / 2.0F), width, height);
        this.text = text;
        this.function = function;
        selectionIndex = text.length();
    }

    @Override
    public void keyPressed(int key, char character) {
        if (selected) {
            boolean modified = false;
            if (key == Keyboard.KEY_BACK) {
                if (text.length() > 0 && selectionIndex > 0) {
                    text = text.substring(0, Math.max(0, selectionIndex - 1)) + text.substring(selectionIndex);
                    selectionIndex--;
                    modified = true;
                }
            } else if (character != 167 && character >= 32 && character != 127) {
                text = text.substring(0, selectionIndex) + character + text.substring(selectionIndex);
                selectionIndex++;
                modified = true;
            } else if (key == Keyboard.KEY_LEFT && selectionIndex > 0) {
                selectionIndex--;
            } else if (key == Keyboard.KEY_RIGHT && selectionIndex < text.length()) {
                selectionIndex++;
            }
            if (modified) {
                function.apply(this);
            }
        }
    }

    @Override
    public void update() {
        tick++;
    }

    @Override
    public void render(float mouseX, float mouseY) {
        int color = selected ? 0x684782 : 0x8D60AF;
        GL11.glColor4f((color >> 16 & 0xFF) / 255.0F, (color >> 8 & 0xFF) / 255.0F, (color & 0xFF) / 255.0F, 1.0F);
        drawButton(posX, posY, width, height);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        gui.drawCenteredString(text, posX + (width / 2.0F), posY + (height / 2.0F), 0.5F, 0xFFFFFF);
        if (selected && tick % 40 > 20) {
            float x = (gui.font.getWidth(text.substring(0, selectionIndex)) / 2.0F) - gui.font.getWidth(text) / 4.0F;
            gui.drawRect(posX + (width / 2.0F) + x, posY + (height / 2.0F) - (gui.font.getHeight() / 2.0F), 1.0F, gui.font.getHeight());
        }
    }

    @Override
    public boolean mouseClicked(float mouseX, float mouseY, int button) {
        selected = button == 0 && super.isSelected(mouseX, mouseY);
        selectionIndex = text.length();
        return false;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        String previous = this.text;
        this.text = text;
        if (!previous.equals(text)) {
            function.apply(this);
        }
    }
}
