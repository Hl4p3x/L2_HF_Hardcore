package custom.daily;

public class LoginReward {

    private final int itemId;
    private final long amount;

    public LoginReward(int itemId, long amount) {
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
        return "LoginReward{" +
                "itemId=" + itemId +
                ", amount=" + amount +
                '}';
    }

}
