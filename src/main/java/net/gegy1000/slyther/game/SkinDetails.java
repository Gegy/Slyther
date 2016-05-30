package net.gegy1000.slyther.game;

public class SkinDetails {
    public SkinColor[] pattern;
    public boolean hasAntenna;
    public int antennaLength;
    public int antennaPrimaryColor;
    public int antennaSecondaryColor;
    public float eca = 0.75F;
    public int eyeColor = 0xFFFFFF;
    public boolean abrot;
    public float atia;
    public boolean atwg;
    public boolean oneEye;
    public float pma = 2.3F;
    public float swell;
    public String antennaTexture;
    public String faceTexture;
    public float antennaScale = 1.0F;

    public SkinDetails(SkinColor[] pattern) {
        this.pattern = pattern;
    }
}