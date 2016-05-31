package net.gegy1000.slyther.game.entity;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.game.Game;
import net.gegy1000.slyther.game.Skin;
import net.gegy1000.slyther.network.message.server.MessageNewSnake;
import net.gegy1000.slyther.server.ConnectedClient;

import java.util.ArrayList;
import java.util.List;

public abstract class Snake<GME extends Game<?, ?, ?, ?, ?, ?>> extends Entity<GME> implements Comparable<Snake> {
    public String name;
    public int id;
    public Skin skin;

    public int na;
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
    public SnakePoint lnp; // Tail point or Head point (Last point entry)
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

    public Snake(GME game, String name, int id, float posX, float posY, float angle, List<SnakePoint> points) {
        super(game, posX, posY);
        this.name = name;
        this.id = id;
        na = 1;
        scale = 1.0F;
        moveSpeed = game.getNsp1() + game.getNsp2() * scale;
        accelleratingSpeed = moveSpeed + 0.1F;
        msp = game.getNsp3();
        ehl = 1;
        msl = 42;
        this.angle = angle;
        eyeAngle = angle;
        wantedAngle = angle;
        speed = 2;

        if (points != null) {
            lnp = points.get(points.size() - 1);
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