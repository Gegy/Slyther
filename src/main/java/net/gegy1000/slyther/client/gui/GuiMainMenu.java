package net.gegy1000.slyther.client.gui;

import net.gegy1000.slyther.client.gui.element.ButtonElement;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;

public class GuiMainMenu extends Gui {
    private float backgroundX;
    private float backgroundY;

    @Override
    public void init() {
        this.elements.add(new ButtonElement(this, "Play", renderResolution.getWidth() / 2.0F, renderResolution.getHeight() / 2.0F, 100.0F, 40.0F, (button) -> {
            this.closeGui();
            this.client.connect();
            this.renderHandler.openGui(new GuiGame());
            return true;
        }));
    }

    @Override
    public void update() {
        float x = Mouse.getX() - (Display.getWidth() / 2.0F);
        float y = (Display.getHeight() / 2.0F) - Mouse.getY();
        float angle = (float) Math.atan2(y, x);
        backgroundX += Math.cos(angle) * 1.5F;
        backgroundY += Math.sin(angle) * 1.5F;
    }

    @Override
    public void render(float mouseX, float mouseY) {
        textureManager.bindTexture("/textures/background.png");
        this.drawTexture(0.0F, 0.0F, backgroundX, backgroundY, renderResolution.getWidth() / client.gsc, renderResolution.getHeight() / client.gsc, 599, 519);
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, int button) {

    }
}
