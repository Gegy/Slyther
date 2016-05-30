package net.gegy1000.slyther.client.render;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.client.gui.Gui;
import net.gegy1000.slyther.client.gui.GuiMainMenu;
import net.gegy1000.slyther.util.Log;

import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.newdawn.slick.TrueTypeFont;

import java.awt.Font;
import java.util.ArrayList;
import java.util.List;

public class RenderHandler {
    public SlytherClient client;

    public TextureManager textureManager;
    public RenderResolution renderResolution;

    public TrueTypeFont font;
    public TrueTypeFont largeFont;

    private List<Gui> guis = new ArrayList<>();

    public RenderHandler(SlytherClient client) {
        this.client = client;
        textureManager = new TextureManager();
    }

    public void setup() {
        try {
            Display.setDisplayMode(new DisplayMode(854, 480));
            Display.setTitle("Slyther");
            Display.setResizable(true);
            Display.create();
            Keyboard.create();
            Mouse.create();
            try {
                Font awtFont = new Font("Arial Rounded MT Bold", Font.PLAIN, 0);
                font = new TrueTypeFont(awtFont.deriveFont(14.0F), true);
                largeFont = new TrueTypeFont(awtFont.deriveFont(30.0F), true);
            } catch (Exception e) {
                Log.catching(e);
            }
            init();
            openGui(new GuiMainMenu());
        } catch (LWJGLException e) {
            Log.catching(e);
        }
    }

    public void init() {
        renderResolution = new RenderResolution();
        for (Gui gui : getGuis()) {
            gui.initBase(client);
        }
    }

    public void update() {
        for (Gui gui : getGuis()) {
            gui.updateBase();
        }
        while (Keyboard.next()) {
            if (Keyboard.getEventKeyState()) {
                int key = Keyboard.getEventKey();
                char character = Keyboard.getEventCharacter();
                for (Gui gui : getGuis()) {
                    gui.keyPressedBase(key, character);
                }
            }
        }
    }

    public void render() {
        renderResolution.applyScale();
        float mouseX = Mouse.getX() / renderResolution.getScale();
        float mouseY = (Display.getHeight() - Mouse.getY()) / renderResolution.getScale();
        for (Gui gui : getGuis()) {
            gui.renderBase(mouseX, mouseY);
        }
        while (Mouse.next()) {
            int button = Mouse.getEventButton();
            if (Mouse.getEventButtonState()) {
                for (Gui gui : getGuis()) {
                    gui.mouseClickedBase(mouseX, mouseY, button);
                }
            }
        }
    }

    public void openGui(Gui gui) {
        if (!guis.contains(gui)) {
            guis.add(gui);
            gui.initBase(client);
        }
    }

    public void closeGui(Gui gui) {
        guis.remove(gui);
    }

    public List<Gui> getGuis() {
        return new ArrayList<>(guis);
    }

    public void closeAllGuis() {
        guis.clear();
    }
}
