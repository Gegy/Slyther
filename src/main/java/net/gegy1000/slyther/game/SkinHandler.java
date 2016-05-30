package net.gegy1000.slyther.game;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import net.gegy1000.slyther.util.Log;
import net.gegy1000.slyther.util.SystemUtils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static net.gegy1000.slyther.game.SkinColor.*;

public enum SkinHandler {
    INSTANCE;

    private final Map<Skin, SkinDetails> PATTERNS = new HashMap<>();
    private final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    SkinHandler() {
        File patternFile = new File(SystemUtils.getGameFolder(), "skin_patterns.json");
        PATTERNS.put(Skin.AMERICA, new SkinDetails(new SkinColor[] { LIGHT_RED, WHITE, LIGHT_RED, WHITE, LIGHT_RED, WHITE, LIGHT_RED, WHITE, LIGHT_RED, WHITE, LIGHT_RED, LIGHT_BLUE_2, LIGHT_BLUE_2, LIGHT_BLUE_2, LIGHT_BLUE_2, LIGHT_BLUE_2, LIGHT_BLUE_2, LIGHT_BLUE_2, LIGHT_BLUE_2, LIGHT_BLUE_2 }));
        PATTERNS.put(Skin.BLUE_WHITE_RED_STRIPE, new SkinDetails(new SkinColor[] { WHITE, WHITE, WHITE, WHITE, WHITE, LIGHT_BLUE_1, LIGHT_BLUE_1, LIGHT_BLUE_1, LIGHT_BLUE_1, LIGHT_BLUE_1, LIGHT_RED, LIGHT_RED, LIGHT_RED, LIGHT_RED, LIGHT_RED }));
        PATTERNS.put(Skin.GERMANY, new SkinDetails(new SkinColor[] { GRAY_1, GRAY_1, GRAY_1, GRAY_1, GRAY_1, LIGHT_RED, LIGHT_RED, LIGHT_RED, LIGHT_RED, LIGHT_RED, YELLOW, YELLOW, YELLOW, YELLOW, YELLOW }));
        PATTERNS.put(Skin.WHITE_RED_GREEN_STRIPE, new SkinDetails(new SkinColor[] { LIGHT_RED, LIGHT_RED, LIGHT_RED, LIGHT_RED, LIGHT_RED, WHITE, WHITE, WHITE, WHITE, WHITE, GREEN_BLUE, GREEN_BLUE, GREEN_BLUE, GREEN_BLUE, GREEN_BLUE }));
        PATTERNS.put(Skin.WHITE_BLUE_RED_STRIPE, new SkinDetails(new SkinColor[] { LIGHT_BLUE_3, LIGHT_BLUE_3, LIGHT_BLUE_3, LIGHT_BLUE_3, LIGHT_BLUE_3, WHITE, WHITE, WHITE, WHITE, WHITE, LIGHT_RED, LIGHT_RED, LIGHT_RED, LIGHT_RED, LIGHT_RED }));
        PATTERNS.put(Skin.WHITE_RED_STRIPE, new SkinDetails(new SkinColor[] { WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, LIGHT_RED, LIGHT_RED, LIGHT_RED, LIGHT_RED, LIGHT_RED, LIGHT_RED, LIGHT_RED }));
        PATTERNS.put(Skin.RAINBOW, new SkinDetails(new SkinColor[] { LIGHT_PURPLE_1, LIGHT_BLUE_1, CYAN, LIGHT_GREEN, LIGHT_YELLOW_1, LIGHT_ORANGE, PINK, LIGHT_RED, MAGENTA }));
        PATTERNS.put(Skin.SWEDEN, new SkinDetails(new SkinColor[] { LIGHT_BLUE_4, LIGHT_BLUE_4, LIGHT_BLUE_4, LIGHT_BLUE_4, LIGHT_BLUE_4, LIGHT_BLUE_4, LIGHT_BLUE_4, LIGHT_YELLOW_1, LIGHT_YELLOW_1, LIGHT_YELLOW_1, LIGHT_YELLOW_1, LIGHT_YELLOW_1, LIGHT_YELLOW_1, LIGHT_YELLOW_1 }));
        PATTERNS.put(Skin.BLUE_WHITE_STRIPE, new SkinDetails(new SkinColor[] { WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, LIGHT_BLUE_5, LIGHT_BLUE_5, LIGHT_BLUE_5, LIGHT_BLUE_5, LIGHT_BLUE_5, LIGHT_BLUE_5, LIGHT_BLUE_5 }));
        PATTERNS.put(Skin.RED_WHITE_STRIPE, new SkinDetails(new SkinColor[] { LIGHT_RED, LIGHT_RED, LIGHT_RED, LIGHT_RED, LIGHT_RED, LIGHT_RED, LIGHT_RED, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE }));
        PATTERNS.put(Skin.WHITE, new SkinDetails(new SkinColor[] { WHITE }));
        PATTERNS.put(Skin.PURPLE_GREEN_STRIPE, new SkinDetails(new SkinColor[] { LIGHT_GREEN, LIGHT_GREEN, LIGHT_GREEN, LIGHT_GREEN, LIGHT_GREEN, LIGHT_PURPLE_1, LIGHT_PURPLE_1, LIGHT_PURPLE_1, LIGHT_PURPLE_1, LIGHT_PURPLE_1 }));
        PATTERNS.put(Skin.YELLOW_GREEN_BLUE_STRIPE, new SkinDetails(new SkinColor[] { LIGHT_GREEN, LIGHT_GREEN, LIGHT_GREEN, LIGHT_GREEN, LIGHT_GREEN, LIGHT_GREEN, LIGHT_GREEN, LIGHT_YELLOW_2, LIGHT_YELLOW_2, LIGHT_YELLOW_2, LIGHT_YELLOW_2, LIGHT_YELLOW_2, LIGHT_YELLOW_2,  BLUE_2, BLUE_1,  BLUE_2, BLUE_1,  BLUE_2, BLUE_1,  BLUE_2, LIGHT_YELLOW_2, LIGHT_YELLOW_2, LIGHT_YELLOW_2, LIGHT_YELLOW_2, LIGHT_YELLOW_2, LIGHT_YELLOW_2 }));
        PATTERNS.put(Skin.YELLOW_WHITE_ORANGE_STRIPE, new SkinDetails(new SkinColor[] { LIGHT_ORANGE, LIGHT_ORANGE, LIGHT_ORANGE, LIGHT_ORANGE, LIGHT_ORANGE, LIGHT_ORANGE, LIGHT_ORANGE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, WHITE, GREEN_BLUE, GREEN_BLUE, GREEN_BLUE, GREEN_BLUE, GREEN_BLUE, GREEN_BLUE, GREEN_BLUE }));
        PATTERNS.put(Skin.RED_YELLOW_BLUE_STRIPE, new SkinDetails(new SkinColor[] { LIGHT_BLUE_5, LIGHT_BLUE_5, LIGHT_BLUE_5, LIGHT_BLUE_5, LIGHT_BLUE_5, LIGHT_BLUE_5, LIGHT_BLUE_5, LIGHT_YELLOW_2, LIGHT_YELLOW_2, LIGHT_YELLOW_2, LIGHT_YELLOW_2, LIGHT_YELLOW_2, LIGHT_YELLOW_2, LIGHT_YELLOW_2, LIGHT_RED, LIGHT_RED, LIGHT_RED, LIGHT_RED, LIGHT_RED, LIGHT_RED, LIGHT_RED }));
        PATTERNS.put(Skin.ARCADE_GO, new SkinDetails(new SkinColor[] { LIGHT_BLUE_6, LIGHT_BLUE_6, LIGHT_BLUE_6, LIGHT_BLUE_6, LIGHT_BLUE_6, LIGHT_BLUE_6, LIGHT_BLUE_6, LIGHT_BLUE_6, LIGHT_BLUE_6, LIGHT_YELLOW_2, LIGHT_YELLOW_2, LIGHT_YELLOW_2, LIGHT_YELLOW_2, LIGHT_YELLOW_2, LIGHT_YELLOW_2, LIGHT_YELLOW_2, LIGHT_YELLOW_2, LIGHT_YELLOW_2 }, true, 8, 0x00688C, 0x64C8E7, 0.75F, 0xFFFFFF, false, 0.35F, true, -10, -10, 20, 20, 0.75F, 1, false, 0, 0, 0, 0, 0, 0, 2.3F, 0.0F, "red_antenna"));
        PATTERNS.put(Skin.ORANGE_PURPLE_STRIPE_ANTENNA, new SkinDetails(new SkinColor[] {  DARK_PURPLE,  DARK_PURPLE,  DARK_PURPLE,  DARK_PURPLE,  DARK_PURPLE,  DARK_PURPLE,  DARK_PURPLE,  DARK_PURPLE,  DARK_PURPLE,  DARK_PURPLE,  DARK_PURPLE,  DARK_PURPLE,  ORANGE,  ORANGE,  ORANGE,  ORANGE,  ORANGE,  ORANGE,  ORANGE,  ORANGE, ORANGE }, true, 9, 0x000000, 0x5630D7, 1.0F, 0xFF5609, true, 1, false, -5, -10, 20, 20, 1, 1.6F, false, 0, 0, 0, 0, 0, 0, 2.3F, 0.0F, "orange_block"));
        PATTERNS.put(Skin.SILVER_GOLD_OUTLINE, new SkinDetails(new SkinColor[] { GRAY_2 }));
        PATTERNS.put(Skin.GREEN_EYEBALL, new SkinDetails(new SkinColor[] { GREEN }, false, 0, 0, 0, 0.75F, 0xFFFFFF, false, 0.0F, false, 0, 0, 0, 0, 0.0F, 0.0F, true, 64, 64, 2, 48, 48, 14, 4.0F, 0.06F, ""));
        PATTERNS.put(Skin.RED_GREEN_YELLOW, new SkinDetails(new SkinColor[] { LIGHT_YELLOW_2, LIGHT_YELLOW_2, LIGHT_YELLOW_2, LIGHT_YELLOW_2, LIGHT_YELLOW_2, LIGHT_YELLOW_2, LIGHT_YELLOW_2,  GREEN,  GREEN,  GREEN,  GREEN,  GREEN,  GREEN,  GREEN, LIGHT_RED, LIGHT_RED, LIGHT_RED, LIGHT_RED, LIGHT_RED, LIGHT_RED, LIGHT_RED }));
        PATTERNS.put(Skin.BLACK_SMALL_YELLOW_STRIPE, new SkinDetails(new SkinColor[] { GRAY_1, GRAY_1, LIGHT_YELLOW_1, GRAY_1, GRAY_1, GRAY_1, GRAY_1, LIGHT_YELLOW_1, GRAY_1, GRAY_1 }));
        PATTERNS.put(Skin.LIGHT_BLUE_BLUE_STRIPE_STARS, new SkinDetails(new SkinColor[] { LIGHT_BLUE_2, LIGHT_BLUE_2, BLUE_1,  BLUE_2, LIGHT_BLUE_2, LIGHT_BLUE_2,  BLUE_2, BLUE_1 }));
        PATTERNS.put(Skin.LIGHT_BLUE_STARS, new SkinDetails(new SkinColor[] { LIGHT_BLUE_2, LIGHT_BLUE_2 }));
        PATTERNS.put(Skin.BLUE_STARS, new SkinDetails(new SkinColor[] {  BLUE_2, BLUE_2 }));
        PATTERNS.put(Skin.BUMBLEBEE, new SkinDetails(new SkinColor[] { YELLOW, GRAY_1, GRAY_1 }));
        PATTERNS.put(Skin.COLORFUL_STRIPES, new SkinDetails(new SkinColor[] { LIGHT_RED, LIGHT_RED, WHITE, GREEN_BLUE, GREEN_BLUE, WHITE, LIGHT_BLUE_5, LIGHT_BLUE_5, WHITE, YELLOW, YELLOW, WHITE, LIGHT_RED, LIGHT_RED, WHITE, LIGHT_BLUE_5, LIGHT_BLUE_5, WHITE }));
        PATTERNS.put(Skin.PINK_WHITE_RED_STRIPE, new SkinDetails(new SkinColor[] { LIGHT_RED, LIGHT_RED, WHITE, WHITE, PINK, PINK, WHITE, WHITE }));
        PATTERNS.put(Skin.LIGHT_BLUE_WHITE_BLUE_STRIPE, new SkinDetails(new SkinColor[] { LIGHT_BLUE_5, LIGHT_BLUE_5, WHITE, WHITE, LIGHT_BLUE_4, LIGHT_BLUE_4, WHITE, WHITE }));
        PATTERNS.put(Skin.BRIGHT_ORANGE, new SkinDetails(new SkinColor[] { ORANGE })); //TODO antenna
        PATTERNS.put(Skin.BRIGHT_YELLOW, new SkinDetails(new SkinColor[] { LIGHT_YELLOW_2 }));
        PATTERNS.put(Skin.PEWDIEPIE, new SkinDetails(new SkinColor[] { LIGHT_BLUE_1 })); //TODO
        if (patternFile.exists()) {
            try {
                SkinPatternsContainer patterns = GSON.fromJson(new FileReader(patternFile), SkinPatternsContainer.class);
                if (patterns != null) {
                    for (SkinJsonContainer container : patterns.patterns) {
                        PATTERNS.put(container.skin, container.details);
                    }
                }
            } catch (Exception e) {
                Log.catching(e);
            }
        }
        if (!patternFile.exists()) {
            try {
                patternFile.createNewFile();
                PrintWriter out = new PrintWriter(patternFile);
                List<SkinJsonContainer> jsonPatterns = new ArrayList<>();
                for (Map.Entry<Skin, SkinDetails> entry : PATTERNS.entrySet()) {
                    SkinJsonContainer container = new SkinJsonContainer();
                    container.skin = entry.getKey();
                    container.details = entry.getValue();
                    jsonPatterns.add(container);
                }
                SkinPatternsContainer patternsContainer = new SkinPatternsContainer();
                patternsContainer.patterns = jsonPatterns;
                out.print(GSON.toJson(patternsContainer));
                out.close();
            } catch (Exception e) {
                Log.catching(e);
            }
        }
    }

    public SkinDetails getDetails(Skin skin) {
        return PATTERNS.get(skin);
    }

    private class SkinPatternsContainer {
        public List<SkinJsonContainer> patterns;
    }

    private class SkinJsonContainer {
        public Skin skin;
        public SkinDetails details;
    }
}
