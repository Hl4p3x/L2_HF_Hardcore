package handlers.communityboard.custom.teleport;

import com.l2jserver.gameserver.model.Location;

import java.util.Objects;
import java.util.StringJoiner;

public class TeleportDestination {

    private String name;
    private String code;
    private Location coordinates;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public Location getCoordinates() {
        return coordinates;
    }

    public void setCoordinates(Location coordinates) {
        this.coordinates = coordinates;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        TeleportDestination that = (TeleportDestination) o;
        return Objects.equals(code, that.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", TeleportDestination.class.getSimpleName() + "[", "]")
                .add(name)
                .add(code)
                .add(Objects.toString(coordinates))
                .toString();
    }

}
