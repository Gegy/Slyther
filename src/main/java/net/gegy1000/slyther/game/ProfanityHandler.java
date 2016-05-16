package net.gegy1000.slyther.game;

public enum ProfanityHandler {
    INSTANCE;

    public static final String[] FILTER_SINGLE_SPACING = "ler did no;gas the;gas all;gas every;panis;panus;paynis;my ass;cut your;heil hit;flick your;fingerba;arse;naked;menstr;eat my;eat as;lick as;suck as;suck my;fuk;dong;cunn;penil;suck a;foresk;puto;puta;suck;mierd;bit.ly;ween;wein;wien;peen;turd;wank;crap;ur mom;tu madre;chinga;pu$$;phalus;phallus;verga;culo;kurwa;erect;schlong;ureth;taint".split(";");
    public static final String[] FILTER_NO_SPACING = "buttlov buttf smegm therplu eatmy suckm sucka chither chlther erioorg eri0org erio0rg eri00rg erloorg erl0org erlo0rg erl00rg ragapw mydik urdik heriobo mistik ki11all brazz iomods cunt suckdik slibot herioha itherhac sucksdik sukdik deltaloves suksdik hitler assmunch lickmy fuqall fukall tobils".split(" ");
    public static final String[] FILTER_SINGLE_WORDS = new String[] { "ass", "kkk" };

    public boolean isClean(String text) {
        String correctlySpaced = "";
        String withoutSpacing = "";
        String withSpacing = "";
        int numberLength = 0;
        boolean isNumber;
        boolean requiresCharacter = false;
        for (int i = 0; i < text.length(); i++) {
            char character = text.charAt(i);
            if (character == ' ') {
                if (!requiresCharacter) {
                    requiresCharacter = true;
                    correctlySpaced += " ";
                }
            } else {
                requiresCharacter = false;
                correctlySpaced += character;
            }
        }
        requiresCharacter = false;
        for (int i = 0; i < text.length(); i++) {
            char character = text.charAt(i);
            if ((isNumber = character >= '0' && character <= '9') || (character >= 'A' && character <= 'Z' || character >= 'a' && character <= 'z')) {
                withoutSpacing += character;
                withSpacing += character;
                requiresCharacter = false;
                if (isNumber) {
                    if (numberLength++ >= 7) {
                        return false;
                    }
                } else {
                    numberLength = 0;
                }
            } else if (!requiresCharacter) {
                requiresCharacter = true;
                withSpacing += " ";
            }
        }

        text = correctlySpaced.toLowerCase();

        for (String filter : FILTER_SINGLE_SPACING) {
            if (text.contains(filter)) {
                return false;
            }
        }

        withoutSpacing = withoutSpacing.toLowerCase();

        for (String filter : FILTER_NO_SPACING) {
            if (withoutSpacing.contains(filter)) {
                return false;
            }
        }

        for(String word : withSpacing.toLowerCase().split(" ")) {
            for (String filter : FILTER_SINGLE_WORDS) {
                if (word.equals(filter)) {
                    return false;
                }
            }
        }

        return true;
    }
}
