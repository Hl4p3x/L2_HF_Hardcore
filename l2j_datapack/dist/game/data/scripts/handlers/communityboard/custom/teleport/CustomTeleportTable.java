package handlers.communityboard.custom.teleport;

import com.fasterxml.jackson.core.type.TypeReference;
import com.l2jserver.util.ObjectMapperYamlSingleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class CustomTeleportTable {

    private static final Logger LOG = LoggerFactory.getLogger(CustomTeleportTable.class);

    private Set<TeleportDestination> teleportDestinations = new HashSet<>();

    public CustomTeleportTable() {
        load();
    }

    public void load() {
        String file = "teleport_destinations.yml";
        try {
            teleportDestinations = ObjectMapperYamlSingleton.getInstance().readValue(getClass().getResourceAsStream(file), new TypeReference<Set<TeleportDestination>>() {
            });
        } catch (IOException e) {
            String message = "Could not read rewards file from '" + file + "', please check that file exists and is a valid YML";
            LOG.error("{}" + message + " error: {}", "[CustomTeleports]", e.getMessage());
            throw new IllegalStateException(message);
        }
    }

    public Optional<TeleportDestination> findByCode(String teleportDestinationCode) {
        return teleportDestinations.stream().filter(teleportDestination -> teleportDestination.getCode().equals(teleportDestinationCode)).findFirst();
    }

    public static CustomTeleportTable getInstance() {
        return CustomTeleportTable.SingletonHolder.INSTANCE;
    }

    private static class SingletonHolder {
        protected static final CustomTeleportTable INSTANCE = new CustomTeleportTable();
    }

}
