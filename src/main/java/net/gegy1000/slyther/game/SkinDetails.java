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
    public int blbx;
    public int blby;
    public int blbw;
    public int blbh;
    public float bsc;
    public float blba;
    public boolean oneEye;
    public int ebiw;
    public int ebih;
    public int ebisz;
    public int epiw;
    public int epih;
    public int episz;
    public float pma = 2.3F;
    public float swell;
    public String antennaTexture;

    public SkinDetails(SkinColor[] pattern) {
        this.pattern = pattern;
    }

    public SkinDetails(SkinColor[] pattern, boolean hasAntenna, int antennaLength, int antennaPrimaryColor, int antennaSecondaryColor, float eca, int eyeColor, boolean abrot, float atia, boolean atwg, int blbx, int blby, int blbw, int blbh, float blba, float bsc, boolean oneEye, int ebiw, int ebih, int ebisz, int epiw, int epih, int episz, float pma, float swell, String antennaTexture) {
        this.pattern = pattern;
        this.hasAntenna = hasAntenna;
        this.antennaLength = antennaLength;
        this.antennaPrimaryColor = antennaPrimaryColor;
        this.antennaSecondaryColor = antennaSecondaryColor;
        this.eca = eca;
        this.eyeColor = eyeColor;
        this.abrot = abrot;
        this.atia = atia;
        this.atwg = atwg;
        this.blbx = blbx;
        this.blby = blby;
        this.blbw = blbw;
        this.blbh = blbh;
        this.bsc = bsc;
        this.blba = blba;
        this.oneEye = oneEye;
        this.ebiw = ebiw;
        this.ebih = ebih;
        this.ebisz = ebisz;
        this.epiw = epiw;
        this.epih = epih;
        this.episz = episz;
        this.pma = pma;
        this.swell = swell;
        this.antennaTexture = antennaTexture;
    }
}