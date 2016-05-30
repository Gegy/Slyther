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

    public SkinDetails(SkinColor[] pattern, boolean hasAntenna, int antennaLength, int antennaPrimaryColor, int antennaSecondaryColor, float antennaScale, float eca, int eyeColor, boolean abrot, float atia, boolean atwg, boolean oneEye, float pma, float swell, String antennaTexture) {
        this.pattern = pattern;
        this.hasAntenna = hasAntenna;
        this.antennaLength = antennaLength;
        this.antennaPrimaryColor = antennaPrimaryColor;
        this.antennaSecondaryColor = antennaSecondaryColor;
        this.antennaScale = antennaScale;
        this.eca = eca;
        this.eyeColor = eyeColor;
        this.abrot = abrot;
        this.atia = atia;
        this.atwg = atwg;
        this.oneEye = oneEye;
        this.pma = pma;
        this.swell = swell;
        this.antennaTexture = antennaTexture;
    }

    public SkinDetails(SkinColor[] pattern, String faceTexture) {
        this.pattern = pattern;
        this.faceTexture = faceTexture;
    }
}