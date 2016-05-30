package net.gegy1000.slyther.client;

import net.gegy1000.slyther.game.Configuration;
import net.gegy1000.slyther.game.Skin;

public class ClientConfig implements Configuration {
    public String nickname = "Slyther";
    public Skin skin = Skin.RAINBOW;
    public String server;
    public boolean shouldRecord = true;
    public boolean debugMode = false;
}
