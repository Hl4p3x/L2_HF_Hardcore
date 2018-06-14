package ai;

import com.l2jserver.gameserver.model.actor.L2Npc;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class SpawnManager {

    private List<L2Npc> npcs = new CopyOnWriteArrayList<>();

    public SpawnManager registerSpawn(L2Npc spawn) {
        npcs.add(spawn);
        return this;
    }

    public SpawnManager decayAll() {
        npcs.forEach(L2Npc::decayMe);
        return this;
    }

    public SpawnManager deleteAll() {
        npcs.forEach(L2Npc::deleteMe);
        return this;
    }

    public List<L2Npc> getSpawned() {
        return npcs;
    }



}
