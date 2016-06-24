package net.gegy1000.slyther.client.gui;

import net.gegy1000.slyther.client.gui.element.ArrowElement;
import net.gegy1000.slyther.client.gui.element.ButtonElement;
import net.gegy1000.slyther.client.gui.element.TextBoxElement;
import net.gegy1000.slyther.network.ServerHandler;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;

import java.util.Collections;
import java.util.List;

public class GuiSelectServer extends Gui {
    private static final int ENTRIES_PER_PAGE = 10;

    private GuiMainMenu menu;
    private int page;

    private TextBoxElement serverTextBox;

    public GuiSelectServer(GuiMainMenu menu) {
        this.menu = menu;
    }

    @Override
    public void init() {
        elements.add(new ArrowElement(this, renderResolution.getWidth() / 6.0F, renderResolution.getHeight() / 2.0F, false, (arrow) -> {
            updateSelection(-1);
            return true;
        }));
        elements.add(new ArrowElement(this, renderResolution.getWidth() - renderResolution.getWidth() / 6.0F, renderResolution.getHeight() / 2.0F, true, (arrow) -> {
            updateSelection(1);
            return true;
        }));
        elements.add(new ButtonElement(this, "Done", renderResolution.getWidth() / 2.0F, renderResolution.getHeight() - 40.0F, 100.0F, 40.0F, (button) -> {
            exit();
            return true;
        }));
        elements.add(serverTextBox = new TextBoxElement(this, client.temporaryServerSelection != null ? client.temporaryServerSelection : "", renderResolution.getWidth() / 2.0F, renderResolution.getHeight() - 100.0F, 200.0F, 40.0F, (textBox) -> {
            if (textBox.getText().length() == 0) {
                client.temporaryServerSelection = null;
            } else {
                client.temporaryServerSelection = textBox.getText();
            }
            return null;
        }));
    }

    private void updateSelection(int i) {
        page += i;
        List<ServerHandler.Server> pingedServers = ServerHandler.INSTANCE.getPingedServers();
        int pageCount = (int) Math.ceil(pingedServers.size() / (float) ENTRIES_PER_PAGE);
        if (page < 0) {
            page = pageCount - 1;
        } else if (page > pageCount - 1) {
            page = 0;
        }
    }

    @Override
    public void render(float mouseX, float mouseY) {
        textureManager.bindTexture("/textures/background.png");
        GL11.glColor4f(0.5F, 0.5F, 0.5F, 1.0F);
        drawTexture(0.0F, 0.0F, client.frameTicks * 2.0F, 0, renderResolution.getWidth(), renderResolution.getHeight(), 599, 519);
        GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
        List<ServerHandler.Server> pingedServers = ServerHandler.INSTANCE.getPingedServers();
        Collections.sort(pingedServers);
        float centerX = renderResolution.getWidth() / 2.0F;
        float centerY = renderResolution.getHeight() / 2.0F;
        if (pingedServers.size() == 0) {
            drawCenteredLargeString("Pinging...", centerX, centerY, 0.5F, 0xFFFFFF);
        } else {
            long smallestPing = pingedServers.get(0).getPing();
            long largestPing = pingedServers.get(pingedServers.size() - 1).getPing();
            int pingDelta = (int) (largestPing - smallestPing);
            double pingPerBar = pingDelta / 5.0;

            drawCenteredString((page + 1) + ".", centerX, 20.0F, 0.5F, 0xFFFFFF);

            float yOffset = -175.0F;

            int start = page * ENTRIES_PER_PAGE;
            int midway = start + (ENTRIES_PER_PAGE / 2);
            for (int i = start; i < start + ENTRIES_PER_PAGE; i++) {
                if (i >= pingedServers.size()) {
                    break;
                }
                if (i == midway) {
                    yOffset = -175.0F;
                }
                ServerHandler.Server server = pingedServers.get(i);
                drawServerEntry(centerX + (i >= midway ? 100.0F : -100.0F), centerY + yOffset, server, mouseX, mouseY, (int) ((largestPing - server.getPing()) / pingPerBar));
                yOffset += 65.0F;
            }
        }
    }

    private void drawServerEntry(float x, float y, ServerHandler.Server server, float mouseX, float mouseY, int bars) {
        boolean selected = serverTextBox.getText().startsWith(server.getClusterIp());
        if (mouseX >= x - 95.0F && mouseX <= x + 95.0F && mouseY >= y - 30.0F && mouseY <= y + 30.0F || selected) {
            drawRectAlpha(x - 95.0F, y - 30.0F, 190.0F, 60.0F, selected ? 0xAA444444 : 0xAA555555);
        }
        drawCenteredLargeString(server.getClusterIp(), x, y, 0.4F, 0xFFFFFF);
        drawCenteredString(server.getCountryCode(), x, y - 15.0F, 0.4F, 0x00FFFF);
        int ping = server.getPing();
        drawCenteredString("Ping: " + ping + "ms", x - 18.0F, y + 20.0F, 0.4F, 0xFFFF00);
        float barHeight = 2;
        for (int i = 0; i < 5; i++) {
            drawRect(x + (i * 3.5F) + 20.0F, y + 24.0F - barHeight, 2.0F, barHeight, i > bars ? 0xAAAAAA : 0x00FF00);
            barHeight += 2.5F;
        }
    }

    @Override
    public void update() {

    }

    @Override
    public void keyPressed(int key, char character) {
        if (key == Keyboard.KEY_RIGHT || key == Keyboard.KEY_LEFT) {
            updateSelection(key == Keyboard.KEY_RIGHT ? 1 : -1);
        } else if (key == Keyboard.KEY_ESCAPE) {
            exit();
        }
    }

    @Override
    public void mouseClicked(float mouseX, float mouseY, int button) {
        List<ServerHandler.Server> pingedServers = ServerHandler.INSTANCE.getPingedServers();
        Collections.sort(pingedServers);
        float centerX = renderResolution.getWidth() / 2.0F;
        float centerY = renderResolution.getHeight() / 2.0F;
        if (pingedServers.size() != 0) {
            float yOffset = -175.0F;

            int start = page * ENTRIES_PER_PAGE;
            int midway = start + (ENTRIES_PER_PAGE / 2);
            for (int i = start; i < start + ENTRIES_PER_PAGE; i++) {
                if (i >= pingedServers.size()) {
                    break;
                }
                if (i == midway) {
                    yOffset = -175.0F;
                }
                float x = centerX + (i >= midway ? 100.0F : -100.0F);
                float y = centerY + yOffset;
                if (mouseX >= x - 95.0F && mouseX <= x + 95.0F && mouseY >= y - 30.0F && mouseY <= y + 30.0F) {
                    ServerHandler.Server server = pingedServers.get(i);
                    serverTextBox.setText(server.getIp());
                }
                yOffset += 65.0F;
            }
        }
    }

    private void exit() {
        closeGui();
        renderHandler.openGui(menu);
    }
}
