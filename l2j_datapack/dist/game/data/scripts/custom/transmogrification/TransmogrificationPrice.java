package custom.transmogrification;

import com.l2jserver.gameserver.model.holders.ItemHolder;

import java.util.List;
import java.util.Objects;
import java.util.StringJoiner;

public class TransmogrificationPrice {

    private List<ItemHolder> weapon;
    private List<ItemHolder> armor;

    public TransmogrificationPrice() {
    }

    public List<ItemHolder> getWeapon() {
        return weapon;
    }

    public List<ItemHolder> getArmor() {
        return armor;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", TransmogrificationPrice.class.getSimpleName() + "[", "]")
                .add(Objects.toString(weapon))
                .add(Objects.toString(armor))
                .toString();
    }

}
