package handlers.effecthandlers;

import java.util.stream.Stream;

public enum DamageType {

    PHYSICAL, MAGICAL, ANY;

    public static DamageType fromString(String text) {
        return Stream.of(values()).filter(value -> value.name().toLowerCase().equals(text.toLowerCase())).findFirst().orElse(ANY);
    }

}
