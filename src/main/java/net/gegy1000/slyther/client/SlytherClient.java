package net.gegy1000.slyther.client;

import net.gegy1000.slyther.client.gui.Gui;
import net.gegy1000.slyther.client.render.RenderHandler;
import net.gegy1000.slyther.game.*;
import net.gegy1000.slyther.network.ServerListHandler;
import net.gegy1000.slyther.network.message.MessageAccelerate;
import net.gegy1000.slyther.network.message.MessageSetAngle;
import net.gegy1000.slyther.network.message.MessageSetTurn;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Queue;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingDeque;

public class SlytherClient {
    public int GAME_RADIUS;
    public int MSCPS;
    public int SECTOR_SIZE;
    public int SECTORS_ALONG_EDGE;
    public float SPANG_DIV;
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

    private Queue<FutureTask<?>> tasks = new LinkedBlockingDeque<>();

    public boolean wumsts;
    public Snake player;

    public long ltm;
    public boolean lagging;
    public float lagMultiplier;
    public boolean wfpr; // Waiting for ping return?
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

    public List<Snake> snakes = new ArrayList<>();
    public List<Prey> preys = new ArrayList<>();
    public List<Food> foods = new ArrayList<>();
    public List<Sector> sectors = new ArrayList<>();

    public int fvpos;
    public float fvx;
    public float fvy;
    public float[] fvxs = new float[VFC];
    public float[] fvys = new float[VFC];
    public int fvtg;

    private float[] fpsls;
    private float[] fmlts;

    public int rank = -1;
    public int bestRank = -1;
    public int snakeCount;

    public List<LeaderboardEntry> leaderboard = new ArrayList<>();

    public String longestPlayerName;
    public int longestPlayerScore = -1;
    public String longestPlayerMessage;

    public long lastPingTime;

    public float mww2;
    public float mhh2;

    public float[] pbx = new float[32767];
    public float[] pby = new float[32767];
    public float[] pba = new float[32767];
    public int[] pbu = new int[32767];

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

    public boolean[][] map = new boolean[80][80];

    public ClientConfig configuration;

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

    public SlytherClient() throws Exception {
        this.setup();
    }

    private void setup() {
        try {
            this.configuration = ConfigHandler.INSTANCE.readConfig(ClientConfig.class);
            ConfigHandler.INSTANCE.saveConfig(this.configuration);
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.renderHandler = new RenderHandler(this);
        this.renderHandler.setup();

        try {
            ServerPingManager.pingServers();
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.delta = 0;
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
                this.renderHandler.init();
                this.mww2 = this.renderHandler.renderResolution.getWidth() / 2.0F;
                this.mhh2 = this.renderHandler.renderResolution.getHeight() / 2.0F;
            }

            long currentTime = System.nanoTime();
            this.delta += (currentTime - previousTime) / nanoUpdates;
            previousTime = currentTime;

            while (this.delta >= 1) {
                this.update();
                this.renderHandler.update();
                this.delta--;
                ups++;
            }

            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
            GL11.glPushMatrix();

            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

            this.renderHandler.render();

            this.fps++;

            if (System.currentTimeMillis() - timer > 1000) {
                int bytesPerSecond = 0;
                if (networkManager != null) {
                    bytesPerSecond = networkManager.bytesPerSecond;
                    networkManager.bytesPerSecond = 0;
                }
                Display.setTitle("Slyther - FPS: " + fps + " - UPS: " + ups + " - BPS: " + bytesPerSecond);
                this.fps = 0;

                timer += 1000;
                ups = 0;
            }

            GL11.glPopMatrix();
            Display.sync(60);
            Display.update();
        }

        System.exit(-1);
    }

    public void connect() {
        new Thread(() -> {
            try {
                if (configuration.server == null) {
                    while (ServerListHandler.INSTANCE.getPingedCount() < 5) ;
                    List<ServerListHandler.Server> servers = ServerListHandler.INSTANCE.getServerList();
                    Collections.sort(servers);
                    while ((SlytherClient.this.networkManager = ClientNetworkManager.create(SlytherClient.this, servers.get(new Random().nextInt(5)))) == null);
                } else {
                    SlytherClient.this.networkManager = ClientNetworkManager.create(SlytherClient.this, configuration.server);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }

    public void setup(int gameRadius, int mscps, int sectorSize, int sectorCountAlongEdge, float spangDV, float nsp1, float nsp2, float nsp3, float mamu, float mamu2, float cst, int protocolVersion) {
        this.GAME_RADIUS = gameRadius;
        this.SECTOR_SIZE = sectorSize;
        this.SECTORS_ALONG_EDGE = sectorCountAlongEdge;
        this.SPANG_DIV = spangDV;
        this.NSP1 = nsp1;
        this.NSP2 = nsp2;
        this.NSP3 = nsp3;
        this.MAMU = mamu;
        this.MAMU2 = mamu2;
        this.CST = cst;
        this.PROTOCOL_VERSION = protocolVersion;
        this.setMSCPS(mscps);

        if (PROTOCOL_VERSION < 8) {
            throw new RuntimeException("Unsupported protocol version (" + PROTOCOL_VERSION + ")" + "!");
        }
    }

    public void setMSCPS(int mscps) {
        if (this.MSCPS != mscps) {
            this.MSCPS = mscps;
            this.fmlts = new float[mscps];
            this.fpsls = new float[mscps + 1];
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
        if (this.networkManager != null) {
            runTasks();
            long time = System.currentTimeMillis();
            vfr = 0;
            float vfrb;
            float avfr = vfr = (time - ltm) / 8;
            this.ltm = time;
            if (!lagging && wfpr && time - lastPacketTime > 420) {
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
                        this.networkManager.send(new MessageSetTurn((byte) direction));
                    }
                }
                if (!wfpr) {
                    if (time - lastPingTime > 250) {
                        lastPingTime = time;
                        networkManager.ping();
                        lastSendAngleTime = time;
                    }
                }
                etm *= Math.pow(0.993, vfrb);
                if (time - lastAccelerateUpdateTime > 150) {
                    if (player.md != player.prevMd) {
                        this.lastAccelerateUpdateTime = time;
                        this.networkManager.send(new MessageAccelerate(player.md));
                        player.prevMd = player.md;
                    }
                }
                int mouseX = Mouse.getX() - (Display.getWidth() / 2);
                int mouseY = (Display.getHeight() - Mouse.getY()) - (Display.getHeight() / 2);
                if (mouseX != lastMouseX || mouseY != lastMouseY) {
                    this.mouseMoved = true;
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
                        this.networkManager.send(new MessageSetAngle(ang));
                    }
                }
                for (Snake snake : new ArrayList<>(this.snakes)) {
                    snake.update(vfr, vfrb, vfrb2);
                }
                for (Prey prey : new ArrayList<>(this.preys)) {
                    prey.update(vfr, vfrb);
                }
                for (int i = this.foods.size() - 1; i >= 0; i--) {
                    Food food = this.foods.get(i);
                    food.gfr += vfr * food.gr;
                    if (food.eaten) {
                        food.eatenFr += vfr / 41.0F;
                        if (food.eatenFr >= 1.0F || food.eater == null) {
                            this.foods.remove(i);
                        } else {
                            Snake eater = food.eater;
                            float h = food.eatenFr * food.eatenFr;
                            food.rad = food.lrrad * (1.0F - food.eatenFr * h);
                            food.rx = (int) (food.posX + (eater.posX + eater.fx + Math.cos(eater.ang + eater.fa) * (43.0F - 24.0F * h) * (1.0F - h) - food.posX) * h);
                            food.ry = (int) (food.posY + (eater.posY + eater.fy + Math.cos(eater.ang + eater.fa) * (43.0F - 24.0F * h) * (1.0F - h) - food.posY) * h);
                            food.rx += Math.cos(food.wsp * food.gfr) * (1.0F - food.eatenFr) * 6.0F;
                            food.ry += Math.sin(food.wsp * food.gfr) * (1.0F - food.eatenFr) * 6.0F;
                        }
                    } else {
                        if (food.fr != 1.0F) {
                            food.fr += food.rsp * vfr / 150.0F;
                            if (food.fr >= 1.0F) {
                                food.fr = 1.0F;
                                food.rad = 1.0F;
                            } else {
                                food.rad = (float) ((1.0F - Math.cos(Math.PI * food.fr)) * 0.5F);
                                food.rad += 0.66F * (0.5F * (1.0F - Math.cos(Math.PI * food.rad)) - food.rad);
                            }
                            food.lrrad = food.rad;
                        }
                        food.rx = (int) (food.posX + 6.0F * Math.cos(food.wsp * food.gfr));
                        food.ry = (int) (food.posY + 6.0F * Math.sin(food.wsp * food.gfr));
                    }
                }
            }
        }
        this.ticks++;
    }

    public void scheduleTask(Callable<?> callable) {
        tasks.add(new FutureTask<>(callable));
    }

    private void runTasks() {
        while (tasks.size() > 0) {
            FutureTask<?> task = tasks.poll();
            try {
                task.run();
                task.get();
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
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
            this.networkManager.send(new MessageSetAngle(ang));
        }
    }

    public Snake getSnake(int id) {
        for (Snake snake : this.snakes) {
            if (snake.id == id) {
                return snake;
            }
        }
        return null;
    }

    public Prey getPrey(int id) {
        for (Prey prey : this.preys) {
            if (prey.id == id) {
                return prey;
            }
        }
        return null;
    }

    public Food getFood(int id) {
        for (Food food : this.foods) {
            if (food.id == id) {
                return food;
            }
        }
        return null;
    }

    public void openGui(Gui gui) {
        this.renderHandler.openGui(gui);
    }

    public void closeGui(Gui gui) {
        this.renderHandler.closeGui(gui);
    }
}