package custom.votes;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.StringJoiner;

public class VoteSource {

    private String name;
    private String code;
    @JsonProperty("source_type")
    private String sourceType;
    private String url;

    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }

    public String getSourceType() {
        return sourceType;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", VoteSource.class.getSimpleName() + "[", "]")
                .add(name)
                .add(code)
                .add(sourceType)
                .add(url)
                .toString();
    }

}
