package custom.certifications;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;

import java.util.stream.Stream;

public enum CertificationSkillType {

    EMERGENT, MASTERY, NONE;

    @JsonCreator
    public CertificationSkillType fromString(String text) {
        return Stream.of(values()).filter(value -> value.name().toLowerCase().equals(text.toLowerCase())).findFirst().orElse(NONE);
    }

    @JsonValue
    public String asString() {
        return name().toLowerCase();
    }

}
