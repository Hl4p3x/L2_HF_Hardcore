package custom.votes;

import java.util.StringJoiner;

public class VoteSource {

    private String name;
    private String code;
    private String url;

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", VoteSource.class.getSimpleName() + "[", "]")
                .add(name)
                .add(code)
                .add(url)
                .toString();
    }

}
