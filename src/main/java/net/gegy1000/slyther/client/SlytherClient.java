package net.gegy1000.slyther.client;

import net.gegy1000.slyther.client.controller.Controller;
import net.gegy1000.slyther.client.controller.DefaultController;
import net.gegy1000.slyther.client.controller.IController;
import net.gegy1000.slyther.client.game.entity.ClientFood;
import net.gegy1000.slyther.client.game.entity.ClientPrey;
import net.gegy1000.slyther.client.game.entity.ClientSnake;
import net.gegy1000.slyther.client.gui.Gui;
import net.gegy1000.slyther.client.gui.GuiMainMenu;
import net.gegy1000.slyther.client.render.RenderHandler;
import net.gegy1000.slyther.game.ConfigHandler;
import net.gegy1000.slyther.game.Game;
import net.gegy1000.slyther.game.entity.*;
import net.gegy1000.slyther.network.ServerHandler;
import net.gegy1000.slyther.network.message.client.MessageAccelerate;
import net.gegy1000.slyther.network.message.client.MessageSetAngle;
import net.gegy1000.slyther.util.Log;
import net.gegy1000.slyther.util.SystemUtils;
import net.gegy1000.slyther.util.UIUtils;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import org.reflections.Reflections;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class SlytherClient extends Game<ClientNetworkManager, ClientConfig> implements Runnable {
    public int GAME_RADIUS;
    public int MSCPS;
    public int SECTOR_SIZE;
    public int SECTORS_ALONG_EDGE;
    public float SPANG_DV;
    public float NSP1;
    public float NSP2;
    public float NSP3;
    public float SNAKE_TURN_SPEED;
    public float PREY_TURN_SPEED;
    public float CST;
    public int PROTOCOL_VERSION;

    public static final int RFC = 43; // C = Count?
    public static final int AFC = 26;
    public static final int LFC = 128;
    public static final int HFC = 92;
    public static final int VFC = 62;
    public static final float NSEP = 4.5F;
    public static final float INITIAL_SCALE = 0.9F;
    public static float MAX_QSM = 1.7F;
    public static final double PI_2 = Math.PI * 2.0;
    public static final float[] LFAS = new float[LFC];
    public static final float[] HFAS = new float[HFC];
    public static final float[] AFAS = new float[AFC];
    public static final float[] RFAS = new float[RFC];
    public static final float[] VFAS = new float[VFC];
    public static final float[] AT2LT = new float[65536];

    private int fps;

    private RenderHandler renderHandler;
    private ClientNetworkManager networkManager;

    public ClientSnake player;

    public long lastTickTime;
    public boolean lagging;
    public float lagMultiplier = 1.0F;
    public float errorTime;
    public float lastTicks;
    public float ticks;
    public float lastTicks2;
    public float ticks2;

    public float frameTicks;

    public float globalAlpha;
    public float qsm = 1.0F;

    private long lastAccelerateUpdateTime;
    public float lastSendAngle = Float.MIN_VALUE;
    public long lastSendAngleTime;

    public float viewX;
    public float viewY;

    public float viewAngle;
    public float viewDist;

    public float originalViewX;
    public float originalViewY;

    public float globalScale = INITIAL_SCALE;

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

    public ClientConfig configuration;

    public String temporaryServerSelection;

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

    public float delta;
    public float lastDelta;
    public double frameDelta;

    private boolean allowUserInput = true;

    public float zoomOffset;

    private IController controller = new DefaultController();

    private static final File CONFIGURATION_FILE = new File(SystemUtils.getGameFolder(), "config.json");
    private String server;

    public SlytherClient() {
        try {
            configuration = ConfigHandler.INSTANCE.readConfig(CONFIGURATION_FILE, ClientConfig.class);
            saveConfig();
        } catch (IOException e) {
            UIUtils.displayException("Unable to read config", e);
            Log.catching(e);
        }
        temporaryServerSelection = configuration.server;
        Reflections reflections = new Reflections("");
        Set<Class<?>> annotated = reflections.getTypesAnnotatedWith(Controller.class);
        for (Class<?> controller : annotated) {
            if (IController.class.isAssignableFrom(controller)) {
                try {
                    Controller annotation = controller.getAnnotation(Controller.class);
                    setController((IController) controller.getDeclaredConstructor().newInstance());
                    Log.info("Using controller \"{}\" ({})", annotation.name(), controller.getSimpleName());
                    break;
                } catch (Exception e) {
                }
            }
        }
        renderHandler = new RenderHandler(this);
        renderHandler.setup();
        setup();
    }

    @Override
    public void run() {
        double delta = 0;
        long previousTime = System.nanoTime();
        long timer = System.currentTimeMillis();
        int ups = 0;
        double nanoUpdates = 1000000000.0 / 30.0;

        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glShadeModel(GL11.GL_SMOOTH);
        GL11.glDisable(GL11.GL_DEPTH_TEST);
        GL11.glDisable(GL11.GL_LIGHTING);

        GL11.glClearColor(0.0F, 0.0F, 0.0F, 0.0F);
        GL11.glClearDepth(1);

        GL11.glEnable(GL11.GL_BLEND);
        GL11.glBlendFunc(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);

        setupDisplay();

        boolean doResize = false;

        while (!Display.isCloseRequested()) {
            if (Display.wasResized() && doResize) {
                setupDisplay();
            }
            doResize = true;

            long currentTime = System.nanoTime();
            double currentTickDelta = (currentTime - previousTime) / nanoUpdates;
            delta += currentTickDelta;
            frameDelta = (frameDelta + currentTickDelta) % 1.0;
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
                int packetsPerSecond = 0;
                if (networkManager != null) {
                    bytesPerSecond = networkManager.bytesPerSecond;
                    packetsPerSecond = networkManager.packetsPerSecond;
                    networkManager.bytesPerSecond = 0;
                    networkManager.packetsPerSecond = 0;
                }
                Display.setTitle("Slyther - FPS: " + fps + " - UPS: " + ups + " - BPS: " + bytesPerSecond + " - PPS: " + packetsPerSecond);
                fps = 0;

                timer += 1000;
                ups = 0;
            }

            GL11.glPopMatrix();
            Display.sync(60);
            Display.update();
        }
        if (networkManager != null && networkManager.isOpen()) {
            networkManager.closeConnection(ClientNetworkManager.SHUTDOWN_CODE, "");
        }
        try {
            ConfigHandler.INSTANCE.saveConfig(CONFIGURATION_FILE, configuration);
        } catch (IOException e) {
            Log.error("Failed to save config");
            Log.catching(e);
        }
        Display.destroy();
    }

    private void setupDisplay() {
        int width = Display.getWidth();
        int height = Display.getHeight();
        GL11.glMatrixMode(GL11.GL_PROJECTION);
        GL11.glLoadIdentity();
        GL11.glOrtho(0, Display.getWidth(), Display.getHeight(), 0, 1, -1);
        GL11.glMatrixMode(GL11.GL_MODELVIEW);
        GL11.glScissor(0, 0, width, height);
        GL11.glViewport(0, 0, width, height);
        renderHandler.init();
    }

    private void setup() {
        clearEntities();
        delta = 0;
        ticks = 0;
        lastTicks = 0;
        player = null;
        lagging = false;
        globalScale = INITIAL_SCALE;
        lagMultiplier = 1.0F;
        zoomOffset = 0.0F;
        globalAlpha = 0.0F;
        ServerHandler.INSTANCE.pingServers();
    }

    public void connect() {
        allowUserInput = true;
        new Thread(() -> {
            try {
                if (temporaryServerSelection == null) {
                    ServerHandler.Server server = ServerHandler.INSTANCE.getServerForPlay();
                    networkManager = ClientNetworkManager.create(SlytherClient.this, server, configuration.shouldRecord);
                } else {
                    networkManager = ClientNetworkManager.create(SlytherClient.this, temporaryServerSelection, configuration.shouldRecord);
                }
                server = networkManager.getIp();
            } catch (Exception e) {
                UIUtils.displayException("Connection failed", e);
            }
        }).start();
    }

    public void replay() {
        allowUserInput = false;
        try {
            networkManager = ClientNetworkManager.create(this);
        } catch (Exception e) {
            UIUtils.displayException("Replay failed", e);
        }
    }

    public void setup(int gameRadius, int mscps, int sectorSize, int sectorCountAlongEdge, float spangDV, float nsp1, float nsp2, float nsp3, float snakeTurnSpeed, float preyTurnSpeed, float cst, int protocolVersion) {
        GAME_RADIUS = gameRadius;
        SECTOR_SIZE = sectorSize;
        SECTORS_ALONG_EDGE = sectorCountAlongEdge;
        SPANG_DV = spangDV;
        NSP1 = nsp1;
        NSP2 = nsp2;
        NSP3 = nsp3;
        SNAKE_TURN_SPEED = snakeTurnSpeed;
        PREY_TURN_SPEED = preyTurnSpeed;
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

    @Override
    public float getFMLT(int i) {
        return fmlts[Math.min(i, fmlts.length - 1)];
    }

    @Override
    public float getFPSL(int i) {
        return fpsls[Math.min(i, fpsls.length - 1)];
    }

    public void update() {
        runTasks();
        if (networkManager != null) {
            long time = System.currentTimeMillis();
            float lastDelta2;
            delta = (time - lastTickTime) / 8.0F;
            lastTickTime = time;
            if (!lagging && networkManager.waitingForPingReturn && time - networkManager.lastPacketTime > 420) {
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
            if (delta > 120) {
                delta = 120;
            }
            delta *= lagMultiplier;
            errorTime *= lagMultiplier;
            lastTicks = ticks;
            ticks += delta;
            lastDelta = (float) (Math.floor(ticks) - Math.floor(lastTicks));
            lastTicks2 = ticks2;
            ticks2 += delta * 2;
            lastDelta2 = (float) (Math.floor(ticks2) - Math.floor(lastTicks2));
            if (globalAlpha < 1.0F) {
                globalAlpha += 0.0075F * delta;
                if (globalAlpha > 1.0F) {
                    globalAlpha = 1.0F;
                }
            } else if (globalAlpha > 0.0F) {
                globalAlpha -= 0.0075F * delta;
                if (globalAlpha < 0.0F) {
                    globalAlpha = 0.0F;
                }
            }
            if (qsm > 1.0F) {
                qsm -= 0.00004F * delta;
                if (qsm < 1.0F) {
                    qsm = 1.0F;
                }
            } else if (qsm < MAX_QSM) {
                qsm += 0.00004F;
                if (qsm > MAX_QSM) {
                    qsm = MAX_QSM;
                }
            }
            if (player != null) {
                if (!networkManager.waitingForPingReturn) {
                    if (time - networkManager.lastPingSendTime > 250) {
                        networkManager.lastPingSendTime = time;
                        networkManager.ping();
                    }
                }
                errorTime *= Math.pow(0.993, lastDelta);
                if (allowUserInput) {
                    controller.update(this);
                    float targetAngle = controller.getTargetAngle();
                    targetAngle %= PI_2;
                    if (targetAngle < 0) {
                        targetAngle += PI_2;
                    }
                    if (targetAngle != lastSendAngle || lastSendAngleTime == 0) {
                        if (time - lastSendAngleTime > 100) {
                            lastSendAngle = targetAngle;
                            networkManager.send(new MessageSetAngle(targetAngle));
                        }
                    }
                    player.accelerating = controller.shouldAccelerate();
                    if (time - lastAccelerateUpdateTime > 150) {
                        if (player.accelerating != player.wasAccelerating) {
                            lastAccelerateUpdateTime = time;
                            networkManager.send(new MessageAccelerate(player.accelerating));
                            player.wasAccelerating = player.accelerating;
                        }
                    }
                }
                Iterator<Entity> entityIter = entityIterator();
                while (entityIter.hasNext()) {
                    Entity entity = entityIter.next();
                    if (entity.updateBase(delta, lastDelta, lastDelta2)) {
                        entityIter.remove();
                    }
                }
            }
        } else {
            ticks++;
        }
    }

    public float getAngleTo(float x, float y) {
        return (float) Math.atan2(y - player.posY, x - player.posX);
    }

    public ClientSnake getSnake(int id) {
        for (Snake snake : getSnakes()) {
            if (snake.id == id) {
                return (ClientSnake) snake;
            }
        }
        return null;
    }

    public ClientPrey getPrey(int id) {
        for (Prey prey : getPreys()) {
            if (prey.id == id) {
                return (ClientPrey) prey;
            }
        }
        return null;
    }

    public ClientFood getFood(int id) {
        for (Food food : getFoods()) {
            if (food.id == id) {
                return (ClientFood) food;
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
        networkManager = null;
        setup();
    }

    public void setController(IController controller) {
        this.controller = controller;
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
    public float getBaseSnakeTurnSpeed() {
        return SNAKE_TURN_SPEED;
    }

    @Override
    public float getBasePreyTurnSpeed() {
        return PREY_TURN_SPEED;
    }

    @Override
    public float getCST() {
        return CST;
    }

    public void saveConfig() {
        try {
            ConfigHandler.INSTANCE.saveConfig(CONFIGURATION_FILE, configuration);
        } catch (Exception e) {
            Log.catching(e);
        }
    }

    @Override
    public void removeSector(Sector sector) {
        super.removeSector(sector);
        if (sector != null) {
            int sectorSize = getSectorSize();
            List<Entity> entitiesInSector = new ArrayList<>();
            for (Entity entity : getFoods()) {
                int sectorX = (int) (entity.posX / sectorSize);
                int sectorY = (int) (entity.posY / sectorSize);
                if (sectorX == sector.posX && sectorY == sector.posY) {
                    entitiesInSector.add(entity);
                }
            }
            for (Entity entity : entitiesInSector) {
                removeEntity(entity);
            }
        }
    }

    public void close() {
        if (networkManager != null) {
            networkManager.close(1000, "Forcefully closed by player");
        }
        player = null;
        networkManager = null;
    }

    public String getServer() {
        return server;
    }
}