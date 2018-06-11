package ai;

import com.l2jserver.gameserver.data.xml.impl.NpcData;
import com.l2jserver.gameserver.instancemanager.GrandBossManager;
import com.l2jserver.gameserver.model.actor.instance.L2GrandBossInstance;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class GrandBossStatusManager {

    private final int npcId;
    private final L2GrandBossInstance npcInstance;
    private final List<Status> availableStatuses;

    public GrandBossStatusManager(int npcId, List<Status> availableStatuses) {
        this.npcId = npcId;
        this.availableStatuses = availableStatuses;
        this.npcInstance = new L2GrandBossInstance(NpcData.getInstance().getTemplate(npcId));
        GrandBossManager.getInstance().addBoss(npcInstance);
    }

    public GrandBossStatusManager(int npcId, L2GrandBossInstance npcInstance, List<Status> availableStatuses) {
        this.npcId = npcId;
        this.npcInstance = npcInstance;
        this.availableStatuses = availableStatuses;
        GrandBossManager.getInstance().addBoss(npcInstance);
    }

    public Status transitionToNextStatus() {
        int currentStatusIndex = availableStatuses.indexOf(getStatus());
        if ((availableStatuses.size() - 1) < currentStatusIndex) {
            Status nextStatus = availableStatuses.get(currentStatusIndex + 1);
            setStatus(nextStatus);
            return nextStatus;
        } else {
            return getStatus();
        }
    }

    public boolean isStatus(Status status) {
        return getStatus().equals(status);
    }

    public Status getStatus() {
        int actualStatus = GrandBossManager.getInstance().getBossStatus(npcId);
        return findStatus(actualStatus);
    }

    public void setStatus(Status status) {
        Status availableStatus = findStatus(status.getId());
        GrandBossManager.getInstance().setBossStatus(npcId, availableStatus.getId());
    }

    private Status findStatus(int statusId) {
        Optional<Status> matchingStatus = availableStatuses.stream().filter(status -> status.getId() == statusId)
            .findFirst();
        if (matchingStatus.isPresent()) {
            return matchingStatus.get();
        } else {
            throw new IllegalStateException(
                String.format("Status [%s] is not available for grand boss %s [%s], available statuses are: %s",
                    statusId,
                    npcInstance.getTemplate().getName(),
                    npcId,
                    availableStatuses.stream().map(Object::toString).collect(Collectors.joining(",")))
            );
        }
    }

}
