package net.gegy1000.slyther.client;

import net.gegy1000.slyther.util.LWJGLSetup;
import net.gegy1000.slyther.util.SystemUtils;

import java.io.File;
import java.io.IOException;

public class ClientMain {
    public static void main(String[] args) throws Exception {
        try {
            LWJGLSetup.load(new File(SystemUtils.getGameFolder(), "natives"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        new SlytherClient();
    }
}
