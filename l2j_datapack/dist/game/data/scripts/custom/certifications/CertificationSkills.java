package custom.certifications;

import java.util.*;
import java.util.stream.Collectors;

public class CertificationSkills {

    private Map<CertificationSkillType, Set<Integer>> certificationSkillsByType = new HashMap<>();

    public CertificationSkills(Map<CertificationSkillType, Set<Integer>> certificationSkillsByType) {
        this.certificationSkillsByType = certificationSkillsByType;
    }

    public Set<Integer> getCertificationSkillIdsByType(CertificationSkillType type) {
        return certificationSkillsByType.getOrDefault(type, Collections.emptySet());
    }

    public Set<Integer> getAllSkillIds() {
        return certificationSkillsByType.values().stream().flatMap(Collection::stream).collect(Collectors.toSet());
    }

}
