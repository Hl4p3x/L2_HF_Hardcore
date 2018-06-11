package ai.individual.Frintezza;

import ai.Status;
import java.util.Arrays;
import java.util.List;

public enum FrintezzaStatuses implements Status {

    ALIVE, FIRST_ROOM, SECOND_ROOM, REGULAR_HALISHA, FOUR_LEG_HALISHA, FINISH, DEAD;

    @Override
    public int getId() {
        return ordinal();
    }

    @Override
    public String getName() {
        return name();
    }

    public static List<Status> asList() {
        return Arrays.asList(values());
    }

}
