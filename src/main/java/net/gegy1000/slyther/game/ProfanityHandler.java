package net.gegy1000.slyther.game;

public final class ProfanityHandler {
    private ProfanityHandler() {}

    public static final String[] FILTER_SINGLE_SPACING = "ler did no;gas the;gas all;gas every;panis;panus;paynis;my ass;cut your;heil hit;flick your;fingerba;arse;naked;menstr;eat my;eat as;lick as;suck as;suck my;fuk;dong;cunn;penil;suck a;foresk;puto;puta;suck;mierd;bit.ly;ween;wein;wien;peen;turd;wank;crap;ur mom;tu madre;chinga;pu$$;phalus;phallus;verga;culo;kurwa;erect;schlong;ureth;taint;pene".split(";");

    public static final String[] FILTER_NO_SPACING = "buttlov buttf smegm therplu eatmy suckm sucka chither chlther ch1ther erioorg eri0org erio0rg eri00rg erloorg erl0org erlo0rg erl00rg erioco ragapw mydik urdik heriobo mistik ki11all agarbots rcomwith brazz iomods cunt suckdik slibot siibot garb0t herioha itherhac sucksdik sukdik deltaloves suksdik hitler assmunch lickmy fuqall fukall tobils yourmom yourmother muslimsare allmuslims jewsare alljews".split(" ");

    public static final String[] FILTER_SINGLE_WORDS = new String[] { "ass", "kkk" };

    public static boolean isClean(String text) {
        StringBuilder correctlySpaced = new StringBuilder();
        StringBuilder withoutSpacing = new StringBuilder();
        StringBuilder withSpacing = new StringBuilder();
        int numberLength = 0;
        boolean isNumber;
        boolean requiresCharacter = false;
        for (int i = 0; i < text.length(); i++) {
            char character = text.charAt(i);
            if (character == ' ') {
                if (!requiresCharacter) {
                    requiresCharacter = true;
                    correctlySpaced.append(' ');
                }
            } else {
                requiresCharacter = false;
                correctlySpaced.append(character);
            }
        }
        requiresCharacter = false;
        for (int i = 0; i < text.length(); i++) {
            char character = text.charAt(i);
            if ((isNumber = character >= '0' && character <= '9') || (character >= 'A' && character <= 'Z' || character >= 'a' && character <= 'z')) {
                withoutSpacing.append(character);
                withSpacing.append(character);
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
                withSpacing.append(' ');
            }
        }
        text = correctlySpaced.toString().toLowerCase();
        for (String filter : FILTER_SINGLE_SPACING) {
            if (text.contains(filter)) {
                return false;
            }
        }
        text = withoutSpacing.toString().toLowerCase();
        for (String filter : FILTER_NO_SPACING) {
            if (text.contains(filter)) {
                return false;
            }
        }
        for (String word : withSpacing.toString().toLowerCase().split(" ")) {
            for (String filter : FILTER_SINGLE_WORDS) {
                if (word.equals(filter)) {
                    return false;
                }
            }
        }
        return true;
    }
}
