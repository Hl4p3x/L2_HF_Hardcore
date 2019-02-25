package custom.common;

import com.fasterxml.jackson.core.type.TypeReference;
import com.l2jserver.common.Log;
import com.l2jserver.util.YamlMapper;

import java.io.IOException;

public class Config {

    public static <T> T load(String resourcePath, Class<?> clazz, Log log) {
        try {
            T result = YamlMapper.getInstance().readValue(clazz.getResourceAsStream(resourcePath), new TypeReference<T>() {
            });
            log.info("Config {} loaded", resourcePath);
            return result;
        } catch (IOException e) {
            throw new IllegalStateException("Could not load config from " + resourcePath, e);
        }
    }

}
