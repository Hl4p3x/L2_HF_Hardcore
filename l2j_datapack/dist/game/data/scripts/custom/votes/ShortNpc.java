package custom.votes;

import java.util.Objects;
import java.util.StringJoiner;

public class ShortNpc {

    private int id;
    private int displayId;
    private String name;
    private String title;

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
