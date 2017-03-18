package net.gegy1000.slyther.game;

public enum Color {
    LIGHT_PURPLE_1(192, 128, 255),
    LIGHT_BLUE_1(144, 153, 255),
    CYAN(128, 208, 208),
    LIGHT_GREEN(128, 255, 128),
    LIGHT_YELLOW_1(238, 238, 122),
    LIGHT_ORANGE(255, 160, 96),
    PINK(255, 144, 144),
    LIGHT_RED(255, 64, 64),
    MAGENTA(224, 48, 224),
    WHITE(255, 255, 255),
    LIGHT_BLUE_STARS(144, 153, 255),
    GRAY_1(80, 80, 80),
    YELLOW(255, 192, 80),
    GREEN_BLUE(40, 136, 96),
    LIGHT_BLUE_3(100, 117, 255),
    LIGHT_BLUE_4(120, 134, 255),
    LIGHT_BLUE_5(72, 84, 255),
    LIGHT_PURPLE_2(160, 80, 255),
    LIGHT_YELLOW_2(255, 224, 64),
    BLUE_LARGE_STARS(56, 68, 255),
    BLUE_SMALL_STARS(56, 68, 255),
    DARK_PURPLE(78, 35, 192),
    ORANGE(255, 86, 0),
    LIGHT_BLUE_6(101, 200, 232),
    SILVER_GOLD_OUTLINE(128, 132, 144),
    GREEN(60, 192, 72),
    JELLY_GREEN(0, 255, 83),
    SLUG_RED(217, 69, 69),
    UK(255, 64, 64),
    SHINY_SILVER(32, 32, 32),
    SHINY_BLUE(80, 80, 288),
    SHINY_RED(288, 80, 80),
    SHINY_YELLOW(224, 224, 80),
    SHINY_ORANGE(224, 128, 48),
    SHINY_PURPLE(224, 80, 224),
    SHINY_GREEN(80, 224, 80);

    public float red, green, blue;

    Color(int red, int green, int blue) {
        this.red = red / 255.0F;
        this.green = green / 255.0F;
        this.blue = blue / 255.0F;
    }

    public int toHex() {
        int red = (int) (this.red * 255);
        int green = (int) (this.green * 255);
        int blue = (int) (this.blue * 255);
        return red << 16 | green << 8 | blue;
    }
}
