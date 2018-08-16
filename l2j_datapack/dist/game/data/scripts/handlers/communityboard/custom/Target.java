package handlers.communityboard.custom;

import java.util.Optional;
import java.util.stream.Stream;

public enum Target {

    PLAYER, SUMMON;

    public static Optional<Target> parse(String raw) {
        return Stream.of(values())
                .filter(target -> target.name().toLowerCase().equals(raw.toLowerCase()))
                .findFirst();
    }

}
