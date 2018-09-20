package com.l2jserver.gameserver.model.items.scrolls;

import com.l2jserver.gameserver.model.items.graded.Grade;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Scroll {

    private static final Pattern GRADE_REGEX = Pattern.compile("\\((.+?)-Grade\\)");

    public static Grade gradeFromScrollName(String scrollName) {
        Matcher matcher = GRADE_REGEX.matcher(scrollName);
        if (matcher.find()) {
            return Grade.fromString(matcher.group(1));
        } else {
            return Grade.UNSET;
        }
    }

}
