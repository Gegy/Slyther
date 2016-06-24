package net.gegy1000.slyther.client.render;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.client.gui.Gui;
import net.gegy1000.slyther.client.gui.GuiMainMenu;
import net.gegy1000.slyther.game.Color;
import net.gegy1000.slyther.util.Log;
import org.lwjgl.LWJGLException;
import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.newdawn.slick.TrueTypeFont;
import org.newdawn.slick.opengl.ImageIOImageData;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

public class RenderHandler {
    public SlytherClient client;

    public TextureManager textureManager;
    public RenderResolution renderResolution;

    public TrueTypeFont font;
    public TrueTypeFont largeFont;

    public float centerX;
    public float centerY;

    public float snakeMinX;
    public float snakeMinY;
    public float snakeMaxX;
    public float snakeMaxY;
    public float foodMinX;
    public float foodMinY;
    public float foodMaxX;
    public float foodMaxY;
    public float apx1;
    public float apy1;
    public float apx2;
    public float apy2;

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
            try {
                ByteBuffer[] icons = new ByteBuffer[] { toBuffer("/textures/icon_16.png"), toBuffer("/textures/icon_32.png"), toBuffer("/textures/icon.png") };
                Display.setIcon(icons);
            } catch (Exception e) {
                Log.catching(e);
            }
            Display.create();
            Keyboard.create();
            Mouse.create();
            try {
                Font awtFont = new Font("Arial Rounded MT Bold", Font.PLAIN, 0);
                font = new TrueTypeFont(awtFont.deriveFont(28.0F), true);
                largeFont = new TrueTypeFont(awtFont.deriveFont(55.0F), true);
            } catch (Exception e) {
                Log.catching(e);
            }
            init();
            for (Color color : Color.values()) {
                String name = color.name().toLowerCase();
                for (int i = 0; i < 6; i++) {
                    textureManager.bindTexture("/textures/colors/snake_" + name + "_" + i + ".png");
                }
                Log.debug("Load " + color.name() + " textures.");
            }
            openGui(new GuiMainMenu());
        } catch (LWJGLException e) {
            Log.catching(e);
        }
    }

    private ByteBuffer toBuffer(String directory) throws IOException {
        return new ImageIOImageData().imageToByteBuffer(ImageIO.read(RenderHandler.class.getResourceAsStream(directory)), false, false, null);
    }

    public void init() {
        renderResolution = new RenderResolution();
        centerX = renderResolution.getWidth() / 2.0F;
        centerY = renderResolution.getHeight() / 2.0F;
        for (Gui gui : getGuis()) {
            gui.initBase(client, this);
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
        client.frameTicks = (float) (client.ticks + client.frameDelta);
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
            gui.initBase(client, this);
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
