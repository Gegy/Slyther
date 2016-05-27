package net.gegy1000.slyther.game;

public enum Color {
    PURPLE(0x9362C3),
    BLUE(0x8D96F7),
    CYAN(0x7CC9C9),
    LIME(0x80FE80),
    YELLOW(0xE5E56B),
    ORANGE(0xFA9D5E),
    PINK(0xF58A8A),
    RED(0xFC3F3F),
    MAGENTA(0xE42BFD);

    float red, green, blue;

    Color(int color) {
        red = ((color >> 16) & 0x0000FF) / 255.0F;
        green = ((color >> 8) & 0x0000FF) / 255.0F;
        blue = (color & 0x0000FF) / 255.0F;
    }

    public float getRed() {
        return red;
    }

    public float getGreen() {
        return green;
    }

    public float getBlue() {
        return blue;
    }

    public int toHex() {
        int red = (int) (this.red * 255);
        int green = (int) (this.green * 255);
        int blue = (int) (this.blue * 255);
        return red << 16 | green << 8 | blue;
    }
}
