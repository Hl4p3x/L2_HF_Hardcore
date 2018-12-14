package com.l2jserver.localization;

import java.util.Objects;
import java.util.StringJoiner;

public class Language {

    private static final Language EN = new Language("en");

    private String code;

    public Language(String code) {
        this.code = code;
    }

    public static Language of(String languageString) {
        return new Language(languageString);
    }

    public String getCode() {
        return code;
    }

    public static Language english() {
        return EN;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Language language = (Language) o;
        return Objects.equals(code, language.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Language.class.getSimpleName() + "[", "]")
                .add(code)
                .toString();
    }

}
