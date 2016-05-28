package net.gegy1000.slyther.client;

import net.gegy1000.slyther.game.Game;
import net.gegy1000.slyther.game.entity.Entity;
import net.gegy1000.slyther.game.entity.Food;
import net.gegy1000.slyther.game.entity.Prey;
import net.gegy1000.slyther.game.entity.Snake;
import net.gegy1000.slyther.client.gui.Gui;
import net.gegy1000.slyther.client.gui.GuiMainMenu;
import net.gegy1000.slyther.client.render.RenderHandler;
import net.gegy1000.slyther.game.ConfigHandler;
import net.gegy1000.slyther.network.ServerHandler;
import net.gegy1000.slyther.network.message.client.MessageAccelerate;
import net.gegy1000.slyther.network.message.client.MessageSetAngle;
import net.gegy1000.slyther.network.message.client.MessageSetTurn;
import net.gegy1000.slyther.util.SystemUtils;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class SlytherClient extends Game<ClientNetworkManager, ClientConfig> {
    public int GAME_RADIUS;
    public int MSCPS;
    public int SECTOR_SIZE;
    public int SECTORS_ALONG_EDGE;
    public float SPANG_DV;
    public float NSP1;
    public float NSP2;
    public float NSP3;
    public float MAMU; // Turn speed?
    public float MAMU2;
    public float CST;
    public int PROTOCOL_VERSION;

    public static final int RFC = 43; // C = Count?
    public static final int AFC = 26;
    public static final int LFC = 128;
    public static final int HFC = 92;
    public static final int VFC = 62;
    public static final float NSEP = 4.5F;
    public static final float INITIAL_GSC = 0.9F;
    public static float MQSM = 1.7F;
    public static final double PI_2 = Math.PI * 2.0;
    public static final float[] LFAS = new float[LFC];
    public static final float[] HFAS = new float[HFC];
    public static final float[] AFAS = new float[AFC];
    public static final float[] RFAS = new float[RFC];
    public static final float[] VFAS = new float[VFC];
    public static final float[] AT2LT = new float[65536];

    private int fps;
    private double delta;

    public RenderHandler renderHandler;

    public ClientNetworkManager networkManager;

    public boolean wumsts;
    public Snake player;

    public long ltm;
    public boolean lagging;
    public float lagMultiplier;
    public boolean waitingForPingReturn; // Waiting for ping return?
    public long lastPacketTime;
    public float etm;
    public float lfr;
    public float fr;
    public float lfr2;
    public float fr2;
    public float vfrb2;
    public boolean keyDownLeft;
    public boolean keyDownRight;
    public float keyDownLeftFrb;
    public float keyDownRightFrb;
    public float gla = 1.0F;
    public float qsm = 1.0F;
    public long lastAccelerateUpdateTime;

    public int lastMouseX;
    public int lastMouseY;

    public boolean mouseMoved;
    public long lastTurnTime;
    public int lastSendAngle;
    public long lastSendAngleTime;
    public long lastKeyTime;
    public long currentPacketTime;
    public long packetTimeOffset;

    public float viewX;
    public float viewY;

    public float viewAng;
    public float viewDist;

    public float ovxx; //oldViewX?
    public float ovyy;

    public float gsc = INITIAL_GSC; // Global Scale

    public int fvpos;
    public float fvx;
    public float fvy;
    public float[] fvxs = new float[VFC];
    public float[] fvys = new float[VFC];
    public int fvtg;

    private float[] fpsls;
    private float[] fmlts;

    public int rank;
    public int bestRank;
    public int snakeCount;

    public String longestPlayerName;
    public int longestPlayerScore;
    public String longestPlayerMessage;

    public long lastPingTime;

    public float mww2;
    public float mhh2;

    public float bpx1;
    public float bpy1;
    public float bpx2;
    public float bpy2;
    public float fpx1;
    public float fpy1;
    public float fpx2;
    public float fpy2;
    public float apx1;
    public float apy1;
    public float apx2;
    public float apy2;

    public ClientConfig configuration;

    public static final File RECORD_FILE = new File(SystemUtils.getGameFolder(), "game.record");

    static {
        for (int i = 0; i < LFC; i++) {
            LFAS[i] = (float) (0.5F * (1.0F - Math.cos(Math.PI * (LFC - 1.0F - i) / (LFC - 1.0F))));
        }
        for (int i = 0; i < HFC; i++) {
            HFAS[i] = (float) (0.5F * (1.0F - Math.cos(Math.PI * (HFC - 1.0F - i) / (HFC - 1.0F))));
        }
        for (int i = 0; i < AFC; i++) {
            AFAS[i] = (float) (0.5F * (1.0F - Math.cos(Math.PI * (AFC - 1.0F - i) / (AFC - 1.0F))));
        }
        for (int i = 0; i < RFC; i++) {
            RFAS[i] = (float) (0.5F * (1.0F - Math.cos(Math.PI * (RFC - 1.0F - i) / (RFC - 1.0F))));
        }
        for (int i = 0; i < VFC; i++) {
            float vf = (float) (0.5F * (1.0F - Math.cos(Math.PI * (VFC - 1.0F - i) / (VFC - 1.0F))));
            vf += 0.5F * (0.5F * (1.0F - Math.cos(Math.PI * vf)) - vf);
            VFAS[i] = vf;
        }
        for (int y = 0; y < 256; y++) {
            for (int x = 0; x < 256; x++) {
                AT2LT[y << 8 | x] = (float) Math.atan2(y - 128, x - 128);
            }
        }
    }

    public float vfr;
    public int ticks;
    public boolean tickLoopInitialized;
    public boolean allowUserInput = true;

    public float zoomOffset;

    private static final File CONFIGURATION_FILE = new File(SystemUtils.getGameFolder(), "config.json");

    public SlytherClient() throws Exception {
        setup();
    }

    private void setup() {
        if (configuration == null) {
            try {
                configuration = ConfigHandler.INSTANCE.readConfig(CONFIGURATION_FILE, ClientConfig.class);
                ConfigHandler.INSTANCE.saveConfig(CONFIGURATION_FILE, configuration);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        if (renderHandler == null) {
            renderHandler = new RenderHandler(this);
            renderHandler.setup();
        }

        getSnakes().clear();
        getFoods().clear();
        getPreys().clear();
        getSectors().clear();
        ticks = 0;
        vfr = 0;
        player = null;
        lagging = false;
        waitingForPingReturn = false;
        gsc = INITIAL_GSC;
        lagMultiplier = 0.0F;
        wumsts = false;
        zoomOffset = 0.0F;

        try {
            ServerPingManager.pingServers();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (!tickLoopInitialized) {
            tickLoopInitialized = true;

            delta = 0;
            long previousTime = System.nanoTime();
            long timer = System.currentTimeMillis();
            int ups = 0;
            double nanoUpdates = 1000000000.0 / 60.0;

            GL11.glEnable(GL11.GL_TEXTURE_2D);
            GL11.glShadeModel(GL11.GL_SMOOTH);
            GL11.glDisable(GL11.GL_DEPTH_TEST);
            GL11.glDisable(GL11.GL_LIGHTING);

            GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
            GL11.glClearDepth(1);

            GL11.glEnable(GL11.GL_BLEND);
            GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

            while (!Display.isCloseRequested()) {
                if (Display.wasResized()) {
                    int width = Display.getWidth();
                    int height = Display.getHeight();
                    GL11.glMatrixMode(GL11.GL_PROJECTION);
                    GL11.glLoadIdentity();
                    GL11.glOrtho(0, Display.getWidth(), Display.getHeight(), 0, 1, -1);
                    GL11.glMatrixMode(GL11.GL_MODELVIEW);
                    GL11.glScissor(0, 0, width, height);
                    GL11.glViewport(0, 0, width, height);
                    renderHandler.init();
                    mww2 = renderHandler.renderResolution.getWidth() / 2.0F;
                    mhh2 = renderHandler.renderResolution.getHeight() / 2.0F;
                }

                long currentTime = System.nanoTime();
                delta += (currentTime - previousTime) / nanoUpdates;
                previousTime = currentTime;

                while (delta >= 1) {
                    update();
                    renderHandler.update();
                    delta--;
                    ups++;
                }

                GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
                GL11.glPushMatrix();

                GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

                renderHandler.render();

                fps++;

                if (System.currentTimeMillis() - timer > 1000) {
                    int bytesPerSecond = 0;
                    if (networkManager != null) {
                        bytesPerSecond = networkManager.bytesPerSecond;
                        networkManager.bytesPerSecond = 0;
                    }
                    Display.setTitle("Slyther - FPS: " + fps + " - UPS: " + ups + " - BPS: " + bytesPerSecond);
                    fps = 0;

                    timer += 1000;
                    ups = 0;
                }

                GL11.glPopMatrix();
                Display.sync(60);
                Display.update();
            }
            try {
                ConfigHandler.INSTANCE.saveConfig(CONFIGURATION_FILE, configuration);
            } catch (Exception e) {
            }
            System.exit(1);
        }
    }

    public void connect() {
        allowUserInput = true;
        new Thread(() -> {
            try {
                if (configuration.server == null) {
                    while (ServerHandler.INSTANCE.getPingedCount() < 5) ;
                    List<ServerHandler.Server> servers = ServerHandler.INSTANCE.getServerList();
                    Collections.sort(servers);
                    while ((networkManager = ClientNetworkManager.create(SlytherClient.this, servers.get(new Random().nextInt(5)), configuration.shouldRecord)) == null)
                        ;
                } else {
                    networkManager = ClientNetworkManager.create(SlytherClient.this, configuration.server, configuration.shouldRecord);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void replay() {
        allowUserInput = false;
        try {
            networkManager = ClientNetworkManager.create(this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setup(int gameRadius, int mscps, int sectorSize, int sectorCountAlongEdge, float spangDV, float nsp1, float nsp2, float nsp3, float mamu, float mamu2, float cst, int protocolVersion) {
        GAME_RADIUS = gameRadius;
        SECTOR_SIZE = sectorSize;
        SECTORS_ALONG_EDGE = sectorCountAlongEdge;
        SPANG_DV = spangDV;
        NSP1 = nsp1;
        NSP2 = nsp2;
        NSP3 = nsp3;
        MAMU = mamu;
        MAMU2 = mamu2;
        CST = cst;
        PROTOCOL_VERSION = protocolVersion;
        setMSCPS(mscps);

        if (PROTOCOL_VERSION < 8) {
            throw new RuntimeException("Unsupported protocol version (" + PROTOCOL_VERSION + ")" + "!");
        }
    }

    public void setMSCPS(int mscps) {
        if (MSCPS != mscps) {
            MSCPS = mscps;
            fmlts = new float[mscps];
            fpsls = new float[mscps + 1];
            for (int i = 0; i <= mscps; i++) {
                if (i < mscps) {
                    fmlts[i] = (float) Math.pow(1.0F - i / (float) mscps, 2.25F);
                }
                if (i != 0) {
                    fpsls[i] = fpsls[i - 1] + 1.0F / fmlts[i - 1];
                }
            }
        }
    }

    public float getFMLT(int i) {
        return fmlts[Math.min(i, fmlts.length - 1)];
    }

    public float getFPSL(int i) {
        return fpsls[Math.min(i, fpsls.length - 1)];
    }

    public void update() {
        if (networkManager != null) {
            networkManager.tick();
            runTasks();
            long time = System.currentTimeMillis();
            vfr = 0;
            float vfrb;
            float avfr = vfr = (time - ltm) / 8;
            ltm = time;
            if (!lagging && waitingForPingReturn && time - lastPacketTime > 420) {
                lagging = true;
            }
            if (lagging) {
                lagMultiplier *= 0.85F;
                if (lagMultiplier < 0.01F) {
                    lagMultiplier = 0.01F;
                }
            } else {
                if (lagMultiplier < 1.0F) {
                    lagMultiplier += 0.05F;
                    if (lagMultiplier >= 1.0F) {
                        lagMultiplier = 1.0F;
                    }
                }
            }
            if (vfr > 120) {
                vfr = 120;
            }
            vfr *= lagMultiplier;
            etm *= lagMultiplier;
            lfr = fr;
            fr += vfr;
            vfrb = (float) (Math.floor(fr) - Math.floor(lfr));
            lfr2 = fr2;
            fr2 += vfr * 2;
            vfrb2 = (float) (Math.floor(fr2) - Math.floor(lfr2));
            if (keyDownLeft) {
                keyDownLeftFrb += vfrb;
            }
            if (keyDownRight) {
                keyDownRightFrb += vfrb;
            }
            if (gla < 1.0F) {
                gla += 0.0075F * vfr;
                if (gla > 1.0F) {
                    gla = 1.0F;
                }
            } else if (gla > 0.0F) {
                gla -= 0.0075F * vfr;
                if (gla < 0.0F) {
                    gla = 0.0F;
                }
            }
            if (qsm > 1.0F) {
                qsm -= 0.00004F * vfr;
                if (qsm < 1.0F) {
                    qsm = 1.0F;
                }
            } else if (qsm < MQSM) {
                qsm += 0.00004F;
                if (qsm > MQSM) {
                    qsm = MQSM;
                }
            }
            if (player != null) {
                if (keyDownLeftFrb > 0 || keyDownRightFrb > 0) {
                    if (time - lastKeyTime > 150) {
                        lastKeyTime = time;
                        if (keyDownRightFrb > 0) {
                            if (keyDownRightFrb < keyDownLeftFrb) {
                                keyDownLeftFrb -= keyDownRightFrb;
                                keyDownRightFrb = 0;
                            }
                        }
                        if (keyDownLeftFrb > 0) {
                            if (keyDownLeftFrb < keyDownRightFrb) {
                                keyDownRightFrb -= keyDownLeftFrb;
                                keyDownLeftFrb = 0;
                            }
                        }
                        int direction;
                        if (keyDownLeftFrb > 0) {
                            direction = (int) keyDownLeftFrb;
                            if (direction > 127) {
                                direction = 127;
                            }
                            keyDownLeftFrb -= direction;
                            player.eang -= MAMU * direction * player.scang * player.spang;
                        } else {
                            direction = (int) keyDownRightFrb;
                            if (direction > 127) {
                                direction = 127;
                            }
                            keyDownRightFrb -= direction;
                            player.eang += MAMU * direction * player.scang * player.spang;
                        }
                        networkManager.send(new MessageSetTurn((byte) direction));
                    }
                }
                if (!waitingForPingReturn) {
                    if (time - lastPingTime > 250) {
                        lastPingTime = time;
                        networkManager.ping();
                        lastSendAngleTime = time;
                    }
                }
                etm *= Math.pow(0.993, vfrb);
                if (time - lastAccelerateUpdateTime > 150) {
                    if (player.mouseDown != player.wasMouseDown) {
                        lastAccelerateUpdateTime = time;
                        networkManager.send(new MessageAccelerate(player.mouseDown));
                        player.wasMouseDown = player.mouseDown;
                    }
                }
                if (allowUserInput) {
                    int mouseX = Mouse.getX() - (Display.getWidth() / 2);
                    int mouseY = (Display.getHeight() - Mouse.getY()) - (Display.getHeight() / 2);
                    if (mouseX != lastMouseX || mouseY != lastMouseY) {
                        mouseMoved = true;
                    }
                    if (mouseMoved) {
                        if (time - lastTurnTime > 100) {
                            mouseMoved = false;
                            lastTurnTime = time;
                            lastMouseX = mouseX;
                            lastMouseY = mouseY;
                            int dist = mouseX * mouseX + mouseY * mouseY;
                            float ang;
                            if (dist > 256) {
                                ang = (float) Math.atan2(mouseY, mouseX);
                                player.eang = ang;
                            } else {
                                ang = player.wang;
                            }
                            ang %= PI_2;
                            if (ang < 0) {
                                ang += PI_2;
                            }
                            networkManager.send(new MessageSetAngle(ang));
                        }
                    }
                }
                for (Entity entity : new ArrayList<>(getEntities())) {
                    entity.updateClient(vfr, vfrb, vfrb2);
                }
            }
        }
        ticks++;
    }

    public void moveTo(float x, float y) {
        long time = System.currentTimeMillis();
        if (time - lastTurnTime > 100) {
            mouseMoved = false;
            lastTurnTime = time;
            float ang = (float) Math.atan2(y - player.posY, x - player.posX);
            player.eang = ang;
            ang %= PI_2;
            if (ang < 0) {
                ang += PI_2;
            }
            networkManager.send(new MessageSetAngle(ang));
        }
    }

    public Snake getSnake(int id) {
        for (Snake snake : getSnakes()) {
            if (snake.id == id) {
                return snake;
            }
        }
        return null;
    }

    public Prey getPrey(int id) {
        for (Prey prey : getPreys()) {
            if (prey.id == id) {
                return prey;
            }
        }
        return null;
    }

    public Food getFood(int id) {
        for (Food food : getFoods()) {
            if (food.id == id) {
                return food;
            }
        }
        return null;
    }

    public void openGui(Gui gui) {
        renderHandler.openGui(gui);
    }

    public void closeGui(Gui gui) {
        renderHandler.closeGui(gui);
    }

    public void closeAllGuis() {
        renderHandler.closeAllGuis();
    }

    public void reset() {
        closeAllGuis();
        openGui(new GuiMainMenu());
        if (networkManager != null && networkManager.recorder != null) {
            networkManager.recorder.close();
        }
        networkManager = null;
        setup();
    }

    @Override
    public int getGameRadius() {
        return GAME_RADIUS;
    }

    @Override
    public int getMSCPS() {
        return MSCPS;
    }

    @Override
    public int getSectorSize() {
        return SECTOR_SIZE;
    }

    @Override
    public int getSectorsAlongEdge() {
        return SECTORS_ALONG_EDGE;
    }

    @Override
    public float getSpangDv() {
        return SPANG_DV;
    }

    @Override
    public float getNsp1() {
        return NSP1;
    }

    @Override
    public float getNsp2() {
        return NSP2;
    }

    @Override
    public float getNsp3() {
        return NSP3;
    }

    @Override
    public float getMamu() {
        return MAMU;
    }

    @Override
    public float getMamu2() {
        return MAMU2;
    }

    @Override
    public float getCST() {
        return CST;
    }
}