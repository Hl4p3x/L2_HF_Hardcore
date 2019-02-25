package custom.transmogrification;

import com.l2jserver.gameserver.model.holders.ItemHolder;
import custom.common.ShortNpc;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public class TransmogrificationConfig {

    private ShortNpc npc;
    private List<ItemHolder> price;

    public ShortNpc getNpc() {
        return npc;
    }

    public List<ItemHolder> getPrice() {
        return price;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", TransmogrificationConfig.class.getSimpleName() + "[", "]")
                .add(Objects.toString(npc))
                .add(Objects.toString(price))
                .toString();
    }

}
