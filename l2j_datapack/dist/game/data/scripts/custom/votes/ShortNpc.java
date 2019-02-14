package custom.votes;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Objects;
import java.util.StringJoiner;

public class ShortNpc {

    private int id;

    @JsonProperty("display_id")
    private int displayId;
    private String name;
    private String title;
    @JsonProperty("collision_radius")
    private double collisionRadius;
    @JsonProperty("collision_height")
    private double collisionHeight;

    public int getId() {
        return id;
    }

    public int getDisplayId() {
        return displayId;
    }

    public String getName() {
        return name;
    }

    public String getTitle() {
        return title;
    }

    public double getCollisionRadius() {
        return collisionRadius;
    }

    public double getCollisionHeight() {
        return collisionHeight;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", ShortNpc.class.getSimpleName() + "[", "]")
                .add(Objects.toString(id))
                .add(Objects.toString(displayId))
                .add(name)
                .add(title)
                .toString();
    }

}
