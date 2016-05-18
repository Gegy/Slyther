package net.gegy1000.slyther.game;

public enum SkinColor {
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
    CORNFLOWER_BLUE(144, 153, 255),
    GRAY_1(80, 80, 80),
    YELLOW(255, 192, 80),
    BLUEY_GREEN(40, 136, 96),
    LIGHT_BLUE_2(100, 117, 255),
    LIGHT_BLUE_3(120, 134, 255),
    LIGHT_BLUE_4(72, 84, 255),
    LIGHT_PURPLE_2(160, 80, 255),
    LIGHT_YELLOW_2(255, 224, 64),
    BLUE_1(56, 68, 255),
    BLUE_2(56, 68, 255),
    DARK_PURPLE(78, 35, 192),
    ORANGE(255, 86, 0),
    LIGHT_BLUE_5(101, 200, 232),
    GRAY_2(128, 132, 144),
    GREEN(60, 192, 72);

    public float red, green, blue;

    SkinColor(int red, int green, int blue) {
        this.red = red / 255.0F;
        this.green = green / 255.0F;
        this.blue = blue / 255.0F;
    }
}
