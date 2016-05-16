package net.gegy1000.slyther.util;

import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class LWJGLSetup {
    public static void load(File folder) throws IOException {
        if (!folder.exists()) {
            folder.mkdirs();
        }
        if (folder.isDirectory()) {
            SystemUtils.OperatingSystem os = SystemUtils.getOS();
            if (os == SystemUtils.OperatingSystem.WINDOWS) {
                if (!new File(folder.getPath() + "/jinput-dx8_64.dll").exists()) {
                    extractFromClasspath("jinput-dx8_64.dll", folder);
                    extractFromClasspath("jinput-dx8.dll", folder);
                    extractFromClasspath("jinput-raw_64.dll", folder);
                    extractFromClasspath("jinput-raw.dll", folder);
                    extractFromClasspath("lwjgl.dll", folder);
                    extractFromClasspath("lwjgl64.dll", folder);
                    extractFromClasspath("OpenAL32.dll", folder);
                    extractFromClasspath("OpenAL64.dll", folder);
                }
            } else if (os == SystemUtils.OperatingSystem.SOLARIS) {
                if (!new File(folder.getPath() + "/liblwjgl.so").exists()) {
                    extractFromClasspath("liblwjgl.so", folder);
                    extractFromClasspath("liblwjgl64.so", folder);
                    extractFromClasspath("libopenal.so", folder);
                    extractFromClasspath("libopenal64.so", folder);
                }
            } else if (os == SystemUtils.OperatingSystem.LINUX) {
                if (!new File(folder.getPath() + "/liblwjgl.so").exists()) {
                    extractFromClasspath("liblwjgl.so", folder);
                    extractFromClasspath("liblwjgl64.so", folder);
                    extractFromClasspath("libopenal.so", folder);
                    extractFromClasspath("libopenal64.so", folder);
                }
            } else if (os == SystemUtils.OperatingSystem.MACOSX) {
                if (!new File(folder.getPath() + "/openal.dylib").exists()) {
                    extractFromClasspath("liblwjgl.dylib", folder);
                    extractFromClasspath("libjinput-osx.jnilib", folder);
                    extractFromClasspath("openal.dylib", folder);
                }
            }
            System.setProperty("net.java.games.input.librarypath", folder.getAbsolutePath());
            System.setProperty("org.lwjgl.librarypath", folder.getAbsolutePath());
        }
    }

    private static void extractFromClasspath(String fileName, File folder) throws IOException {
        FileOutputStream out = new FileOutputStream(new File(folder, fileName));
        IOUtils.copy(LWJGLSetup.class.getResourceAsStream("/" + fileName), out);
        out.flush();
        out.close();
    }
}