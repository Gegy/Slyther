package net.gegy1000.slyther.client;

import net.gegy1000.slyther.client.render.RenderHandler;
import net.gegy1000.slyther.game.*;
import net.gegy1000.slyther.network.message.MessageAccelerate;
import net.gegy1000.slyther.network.message.MessageSetAngle;
import net.gegy1000.slyther.network.message.MessageSetTurn;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

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
    public byte PROTOCOL_VERSION;

    public static final int RFC = 43; // C = Count?
    public static final int AFC = 26;
    public static final int LFC = 128;
    public static final int HFC = 92;
    public static final int VFC = 62;
    public static final float NSEP = 4.5F;
    public static final float INITIAL_GSC = 0.9F;
    public static final double PI_2 = Math.PI * 2.0;
    public static final float[] LFAS = new float[LFC];
    public static final float[] HFAS = new float[HFC];
    public static final float[] AFAS = new float[AFC];
    public static final float[] RFAS = new float[RFC];
    public static final float[] VFAS = new float[VFC];

    private int fps;
    private double delta;

    private RenderHandler renderHandler;

    private ClientNetworkManager networkManager;

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
    public float gla;
    public float qsm;
    public long locationUpdateTime;
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

    public float ovxx;
    public float ovyy;

    public float gsc = INITIAL_GSC; // Global Scale

    public List<Snake> snakes = new ArrayList<>();
    public List<Prey> preys = new ArrayList<>();
    public List<Food> foods = new ArrayList<>();
    public List<Sector> sectors = new ArrayList<>();

    public Deadpool deadpool = new Deadpool();

    public String nickname;

    public int fvpos;
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
    }

    public SlytherClient(String nickname) throws Exception {
        this.nickname = nickname;
        this.setup();
    }

    private void setup() {
        this.renderHandler = new RenderHandler();
        this.renderHandler.setupDisplay();

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
                this.renderHandler.resetResolution();
            }

            long currentTime = System.nanoTime();
            this.delta += (currentTime - previousTime) / nanoUpdates;
            previousTime = currentTime;

            while (this.delta >= 1) {
                this.update();
                this.delta--;
                ups++;
            }

            GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
            GL11.glPushMatrix();

            GL11.glTexParameteri(GL11.GL_TEXTURE_2D, GL11.GL_TEXTURE_MAG_FILTER, GL11.GL_NEAREST);

            this.renderHandler.render(this);

            this.fps++;

            if (System.currentTimeMillis() - timer > 1000) {
                Display.setTitle("Slyther - FPS: " + fps + " - UPS: " + ups);
                this.fps = 0;

                timer += 1000;
                ups = 0;
            }

            GL11.glPopMatrix();
            Display.update();
        }

        System.exit(-1);
    }

    public void setup(int gameRadius, short mscps, short sectorSize, short sectorCountAlongEdge, float spangDV, float nsp1, float nsp2, float nsp3, float mamu, float mamu2, float cst, byte protocolVersion) {
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

        if (PROTOCOL_VERSION < 6) {
            throw new RuntimeException("Unsupported protocol version (" + PROTOCOL_VERSION + ")" + "!");
        }
    }

    public void setMSCPS(int mscps) {
        if (this.MSCPS != mscps) {
            this.MSCPS = mscps;
            this.fmlts = new float[mscps + 1];
            this.fpsls = new float[mscps + 1];
            for (int i = 0; i <= mscps; i++) {
                if (i >= mscps) {
                    fmlts[i] = fmlts[i - 1];
                } else {
                    fmlts[i] = (float) Math.pow(1.0F - i / mscps, 2.25F);
                }
                if (i != 0) {
                    fpsls[i] = fpsls[i - 1] + 1 / fmlts[i - 1];
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
        if (this.networkManager == null) {
            try {
                while ((this.networkManager = ClientNetworkManager.create(this)) == null) ;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            long time = System.currentTimeMillis();
            float vfr;
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
            if (gla < 1) {
                gla += 0.0075F * vfr;
                if (gla > 1) {
                    gla = 1;
                }
            }
            if (qsm > 1) {
                qsm -= 0.00004F * vfr;
                if (qsm < 1) {
                    qsm = 1;
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
                    if (time - lastPacketTime > 250) {
                        lastPacketTime = time;
                        wfpr = true;
                        networkManager.ping();
                        lastSendAngleTime = time;
                    }
                }
                if (GAME_RADIUS != 2147483647) {
                    if (time - locationUpdateTime > 1000) {
                        locationUpdateTime = System.currentTimeMillis();
                        /**
                         * TODO
                         * myloc.style.left = Math.round(52 + 40 * (snake.xx - grd) / grd - 7) + "px";
                         * myloc.style.top = Math.round(52 + 40 * (snake.yy - grd) / grd - 7) + "px";
                         */
                    }
                }
                etm *= Math.pow(0.993, vfrb);
                if (time - lastAccelerateUpdateTime > 150) {
                    if (player.md != player.wmd) {
                        player.md = player.wmd;
                        this.lastAccelerateUpdateTime = time;
                        this.networkManager.send(new MessageAccelerate(player.md));
                    }
                }
                int mouseX = Mouse.getX();
                int mouseY = Mouse.getY();
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
                for (Snake snake : this.snakes) {
                    snake.update(vfr, vfrb, vfrb2);
                }
                for (Prey prey : this.preys) {
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

    public class Deadpool {
        private List<SnakePart> list = new ArrayList<>();

        public void add(SnakePart part) {
            this.list.add(part);
        }

        public SnakePart get() {
            if (list.size() >= 1) {
                SnakePart part = list.get(list.size() - 1);
                list.remove(list.size() - 1);
                return part;
            }
            return null;
        }
    }
}