package net.gegy1000.slyther.game.entity;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.game.*;
import net.gegy1000.slyther.network.message.server.MessageNewSnake;
import net.gegy1000.slyther.server.ConnectedClient;

import java.util.ArrayList;
import java.util.List;

public abstract class Snake<GME extends Game<?, ?>> extends Entity<GME> implements Comparable<Snake> {
    public String name;
    public int id;
    public Skin skin;

    public float chl;
    public float tsp;
    public int sfr;
    public float scale;
    public float moveSpeed;
    public float accelleratingSpeed;
    public float msp;
    public int ehl;
    public int msl;
    public double fam;
    public float angle;
    public float prevAngle;
    public float eyeAngle;
    public float wantedAngle;
    public float prevWantedAngle;
    public float rex;
    public float rey;
    public float speed;
    public float prevSpeed;
    public List<SnakePoint> points;
    public int sct;
    public int flpos;
    public float[] fls;
    public float fl;
    public int fltg;
    public double totalLength;
    public double cfl;
    public float scaleTurnMultiplier;
    public float speedTurnMultiplier;
    public float deadAmt;
    public float aliveAmt;
    public boolean mouseDown;
    public boolean wasMouseDown;
    public boolean dead;
    public boolean accelerating;
    public int turnDirection;
    public int prevTurnDirection;
    public int edir;
    public float partSeparation;
    public float wantedSeperation;
    public boolean dying;
    public int prevPointCount;

    public boolean antenna;
    public boolean oneEye;
    public float headSwell;
    public float antennaBottomAngle;
    public int antennaPrimaryColor;
    public int antennaSecondaryColor;
    public boolean atwg;
    public float atia;
    public boolean antennaBottomRotate;
    public float[] antennaX;
    public float[] antennaY;
    public float[] antennaVelocityX;
    public float[] antennaVelocityY;
    public float[] atax;
    public float[] atay;
    public float antennaScale = 1.0F;
    public String faceTexture;
    public boolean isInView;
    public boolean antennaShown;
    public String antennaTexture;
    public SkinColor[] pattern;
    public SkinDetails skinDetails;
    public SkinColor color;
    public int eyeRadius;
    public float pupilRadius;
    public float pma;
    public int eyeColor;
    public int pupilColor = 0x000000;
    public float[] fxs;
    public float[] fys;
    public float[] fchls;
    public int fpos;
    public int ftg;
    public float fx;
    public float fy;
    public float fchl;
    public float[] foodAngles;
    public int foodAngleIndex;
    public int foodAnglesToGo;
    public float foodAngle;
    public float ehang;
    public float wehang;

    public Snake(GME game, String name, int id, float posX, float posY, Skin skin, float angle, List<SnakePoint> points) {
        super(game, posX, posY);
        this.name = name;
        this.id = id;
        this.angle = angle;
        scale = 1.0F;
        moveSpeed = game.getNsp1() + game.getNsp2() * scale;
        accelleratingSpeed = moveSpeed + 0.1F;
        msp = game.getNsp3();
        ehl = 1;
        msl = 42;
        eyeAngle = angle;
        wantedAngle = angle;
        speed = 2;

        if (points != null) {
            this.points = points;
            sct = points.size();
            if (points.get(0).dying) {
                sct--;
            }
        } else {
            this.points = new ArrayList<>();
        }

        fls = new float[SlytherClient.LFC];
        totalLength = sct + fam;
        cfl = totalLength;
        scaleTurnMultiplier = 1;
        deadAmt = 0;
        aliveAmt = 0;

        setSkin(skin);
        fxs = new float[SlytherClient.RFC];
        fys = new float[SlytherClient.RFC];
        fchls = new float[SlytherClient.RFC];
        foodAngles = new float[SlytherClient.AFC];
        ehang = angle;
        wehang = angle;
    }

    public void setSkin(Skin skin) {
        this.skin = skin;
        eyeRadius = 6;
        pupilRadius = 3.5F;
        pma = 2.3F;
        eyeColor = 0xFFFFFF;

        SkinDetails details = SkinHandler.INSTANCE.getDetails(skin);

        SkinColor[] pattern = new SkinColor[] { SkinColor.values()[skin.ordinal() % SkinColor.values().length] };

        if (details != null) {
            antenna = details.hasAntenna;
            antennaPrimaryColor = details.antennaPrimaryColor;
            antennaSecondaryColor = details.antennaSecondaryColor;
            atwg = details.atwg;
            atia = details.atia;
            antennaBottomRotate = details.abrot;
            int antennaLength = details.antennaLength;
            antennaX = new float[antennaLength];
            antennaY = new float[antennaLength];
            antennaVelocityX = new float[antennaLength];
            antennaVelocityY = new float[antennaLength];
            atax = new float[antennaLength];
            atay = new float[antennaLength];
            for (int i = 0; i < antennaLength; i++) {
                antennaX[i] = posX;
                antennaY[i] = posY;
            }
            eyeColor = details.eyeColor;
            oneEye = details.oneEye;
            pma = details.pma;
            headSwell = details.swell;
            antennaTexture = details.antennaTexture;
            antennaScale = details.antennaScale;
            pattern = details.pattern;
            skinDetails = details;
            faceTexture = details.faceTexture;
        }

        this.pattern = pattern;
        color = pattern[0];
    }

    //Set new length
    public void snl() {
        double tl = this.totalLength;
        this.totalLength = sct + fam;
        tl = this.totalLength - tl;
        int flpos = this.flpos;
        for (int i = 0; i < SlytherClient.LFC; i++) {
            fls[flpos] -= tl * SlytherClient.LFAS[i];
            flpos++;
            if (flpos >= SlytherClient.LFC) {
                flpos = 0;
            }
        }
        fl = fls[this.flpos];
        fltg = SlytherClient.LFC;
        SlytherClient client = (SlytherClient) game;
        if (this == client.player) {
            client.wumsts = true;
        }
    }

    public int getLength() {
        return (int) Math.floor(15.0F * (game.getFPSL(sct) + fam / game.getFMLT(sct) - 1.0F) - 5.0F);
    }

    @Override
    public int compareTo(Snake snake) {
        return Integer.compare(getLength(), snake.getLength());
    }

    @Override
    public void startTracking(ConnectedClient tracker) {
        tracker.send(new MessageNewSnake(this));
    }

    @Override
    public void stopTracking(ConnectedClient tracker) {
        tracker.send(new MessageNewSnake(this, false));
    }

    @Override
    public boolean equals(Object object) {
        return object instanceof Snake && id == ((Snake) object).id;
    }
}