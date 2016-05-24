package net.gegy1000.slyther.game;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.gegy1000.slyther.util.SystemUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;

public enum ConfigHandler {
    INSTANCE;

    private static final File CONFIGURATION_FILE = new File(SystemUtils.getGameFolder(), "config.json");
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public <T> void saveConfig(T configuration) throws Exception {
        if (!CONFIGURATION_FILE.exists()) {
            CONFIGURATION_FILE.createNewFile();
        }
        PrintWriter out = new PrintWriter(new FileWriter(CONFIGURATION_FILE));
        out.print(GSON.toJson(configuration));
        out.close();
    }

    public <T> T readConfig(Class<T> configClass) throws Exception {
        if (CONFIGURATION_FILE.exists()) {
            return GSON.fromJson(new FileReader(CONFIGURATION_FILE), configClass);
        }
        return configClass.getDeclaredConstructor().newInstance();
    }
}
