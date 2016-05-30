package net.gegy1000.slyther.util;

import javax.swing.JOptionPane;

public final class UIUtils {
    private UIUtils() {}

    public static void displayException(String msg, Exception e) {
        JOptionPane.showMessageDialog(null, msg + ":\n" + e.getClass().getSimpleName() + ": " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
    }
}
