package net.gegy1000.slyther.util;

import java.io.File;

public final class SystemUtils {
    public enum OperatingSystem {
        WINDOWS, LINUX, MACOSX, SOLARIS, UNKNOWN
    }

    private static File GAME_FOLDER;

    public static OperatingSystem getOS() {
        String os = System.getProperty("os.name").toLowerCase();
        if (os.contains("win")) {
            return OperatingSystem.WINDOWS;
        } else if (os.contains("sunos") || os.contains("solaris")) {
            return OperatingSystem.SOLARIS;
        } else if (os.contains("unix")) {
            return OperatingSystem.LINUX;
        } else if (os.contains("linux")) {
            return OperatingSystem.LINUX;
        } else if (os.contains("mac")) {
            return OperatingSystem.MACOSX;
        } else {
            return OperatingSystem.UNKNOWN;
        }
    }

    public static File getGameFolder() {
        if (GAME_FOLDER == null) {
            String appdata = System.getenv("APPDATA");
            if (appdata != null) {
                GAME_FOLDER = new File(appdata, ".slyther");
            } else {
                GAME_FOLDER = new File(System.getProperty("user.home"), ".slyther");
            }
        }
        return GAME_FOLDER;
    }

    public static void setGameFolder(File file) {
        GAME_FOLDER = file;
    }
}
