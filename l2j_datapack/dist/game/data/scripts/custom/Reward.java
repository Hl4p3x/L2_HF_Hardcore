package custom;

public class Reward {

    private final int itemId;
    private final long amount;

    public Reward(int itemId, long amount) {
        this.itemId = itemId;
        this.amount = amount;
    }

    public int getItemId() {
        return itemId;
    }

    public long getAmount() {
        return amount;
    }

    @Override
    public String toString() {
        return "Reward{" +
                "itemId=" + itemId +
                ", amount=" + amount +
                '}';
    }

}
