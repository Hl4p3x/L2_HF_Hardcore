package handlers.communityboard.custom.bufflists;

public class SkillDisplayNameHelper {

    public static String changeDisplayName(String originalName) {
        if (originalName.equals("Under the Protection of Pa'agrio")) {
            return "Protection of Pa'agrio";
        }
        return originalName;
    }

}
