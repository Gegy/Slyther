package net.gegy1000.slyther.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.util.Supplier;

public final class Log {
    private Log() {}

    private static final Logger LOGGER = LogManager.getLogger("Slyther");

    public static void catching(Throwable t) {
        LOGGER.catching(t);
    }

    public static void debug(String message, Object... params) {
        LOGGER.debug(message, params);
    }

    public static void error(String message, Object... params) {
        LOGGER.error(message, params);
    }

    public static void fatal(String message, Object... params) {
        LOGGER.fatal(message, params);
    }

    public static void info(String message, Object... params) {
        LOGGER.info(message, params);
    }

    public static void trace(String message, Object... params) {
        LOGGER.trace(message, params);
    }

    public static void warn(String message, Object... params) {
        LOGGER.warn(message, params);
    }

    public static void warn(String message, Supplier<?> supplier, Object... params) {
        Supplier<?>[] paramSuppliers = new Supplier<?>[params.length + 1];
        paramSuppliers[0] = supplier;
        for (int i = 0; i < params.length;) {
            Object obj = params[i];
            paramSuppliers[++i] = () -> obj;
        }
        LOGGER.warn(message, paramSuppliers);
    }

    public static String bytes(byte[] arr) {
        StringBuilder str = new StringBuilder();
        for (int i = 0, end = arr.length - 1, to = Math.min(end, 39);; i++) {
            String h = Integer.toHexString(arr[i] & 0xFF);
            if (h.length() == 1) {
                str.append('0');
            }
            str.append(h);
            if (i == to) {
                if (to < end) {
                    str.append("... ").append(arr.length - to + 1).append(" more");
                }
                return str.toString();
            }
            if (i % 2 == 1) {
                str.append(' ');
            }
        }
    }
}
