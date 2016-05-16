package net.gegy1000.slyther.game;

import java.util.HashMap;
import java.util.Map;

public enum SkinColorHandler {
    INSTANCE;

    private final Map<Skin, int[]> PATTERNS = new HashMap<>();

    SkinColorHandler() {
        PATTERNS.put(Skin.AMERICA, new int[] { 7, 9, 7, 9, 7, 9, 7, 9, 7, 9, 7, 10, 10, 10, 10, 10, 10, 10, 10, 10 });
        PATTERNS.put(Skin.BLUE_WHITE_RED_STRIPE, new int[] { 9, 9, 9, 9, 9, 1, 1, 1, 1, 1, 7, 7, 7, 7, 7 });
        PATTERNS.put(Skin.GERMANY, new int[] { 11, 11, 11, 11, 11, 7, 7, 7, 7, 7, 12, 12, 12, 12, 12 });
        PATTERNS.put(Skin.WHITE_RED_GREEN_STRIPE, new int[] { 7, 7, 7, 7, 7, 9, 9, 9, 9, 9, 13, 13, 13, 13, 13 });
        PATTERNS.put(Skin.WHITE_BLUE_RED_STRIPE, new int[] { 14, 14, 14, 14, 14, 9, 9, 9, 9, 9, 7, 7, 7, 7, 7 });
        PATTERNS.put(Skin.WHITE_RED_STRIPE, new int[] { 9, 9, 9, 9, 9, 9, 9, 7, 7, 7, 7, 7, 7, 7 });
        PATTERNS.put(Skin.RAINBOW, new int[] { 0, 1, 2, 3, 4, 5, 6, 7, 8 });
        PATTERNS.put(Skin.SWEDEN, new int[] { 15, 15, 15, 15, 15, 15, 15, 4, 4, 4, 4, 4, 4, 4 });
        PATTERNS.put(Skin.BLUE_WHITE_STRIPE, new int[] { 9, 9, 9, 9, 9, 9, 9, 16, 16, 16, 16, 16, 16, 16 });
        PATTERNS.put(Skin.RED_WHITE_STRIPE, new int[] { 7, 7, 7, 7, 7, 7, 7, 9, 9, 9, 9, 9, 9, 9 });
        PATTERNS.put(Skin.WHITE, new int[] { 9 });
        PATTERNS.put(Skin.PURPLE_GREEN_STRIPE, new int[] { 3, 3, 3, 3, 3, 0, 0, 0, 0, 0 });
        PATTERNS.put(Skin.YELLOW_GREEN_BLUE_STRIPE, new int[] { 3, 3, 3, 3, 3, 3, 3, 18, 18, 18, 18, 18, 18, 20, 19, 20, 19, 20, 19, 20, 18, 18, 18, 18, 18, 18 });
        PATTERNS.put(Skin.YELLOW_WHITE_ORANGE_STRIPE, new int[] { 5, 5, 5, 5, 5, 5, 5, 9, 9, 9, 9, 9, 9, 9, 13, 13, 13, 13, 13, 13, 13 });
        PATTERNS.put(Skin.RED_YELLOW_BLUE_STRIPE, new int[] { 16, 16, 16, 16, 16, 16, 16, 18, 18, 18, 18, 18, 18, 18, 7, 7, 7, 7, 7, 7, 7 });
        PATTERNS.put(Skin.ARCADE_GO, new int[] { 23, 23, 23, 23, 23, 23, 23, 23, 23, 18, 18, 18, 18, 18, 18, 18, 18, 18 });
        PATTERNS.put(Skin.ORANGE_BLUE_STRIPE_HEAD_TAIL, new int[] { 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 21, 22, 22, 22, 22, 22, 22, 22, 22, 22 });
        PATTERNS.put(Skin.SILVER_GOLD_OUTLINE, new int[] { 24 });
        PATTERNS.put(Skin.GREEN_EYEBALL, new int[] { 25 });
        PATTERNS.put(Skin.RED_GREEN_YELLOW, new int[] { 18, 18, 18, 18, 18, 18, 18, 25, 25, 25, 25, 25, 25, 25, 7, 7, 7, 7, 7, 7, 7 });
        PATTERNS.put(Skin.BLACK_SMALL_YELLOW_STRIPE, new int[] { 11, 11, 4, 11, 11, 11, 11, 4, 11, 11 });
        PATTERNS.put(Skin.LIGHT_BLUE_BLUE_STRIPE_STARS, new int[] { 10, 10, 19, 20, 10, 10, 20, 19 });
        PATTERNS.put(Skin.LIGHT_BLUE_STARS, new int[] { 10, 10 });
        PATTERNS.put(Skin.BLUE_STARS, new int[] { 20, 20 });
        PATTERNS.put(Skin.BUMBLEBEE, new int[] { 12, 11, 11 });
        PATTERNS.put(Skin.COLORFUL_STRIPES, new int[] { 7, 7, 9, 13, 13, 9, 16, 16, 9, 12, 12, 9, 7, 7, 9, 16, 16, 9 });
        PATTERNS.put(Skin.PINK_WHITE_RED_STRIPE, new int[] { 7, 7, 9, 9, 6, 6, 9, 9 });
        PATTERNS.put(Skin.LIGHT_BLUE_WHITE_BLUE_STRIPE, new int[] { 16, 16, 9, 9, 15, 15, 9, 9 });
        PATTERNS.put(Skin.BRIGHT_ORANGE, new int[] { 22 });
        PATTERNS.put(Skin.BRIGHT_YELLOW, new int[] { 18 });
    }

    public int[] getPattern(Skin skin) {
        return PATTERNS.get(skin);
    }
}
