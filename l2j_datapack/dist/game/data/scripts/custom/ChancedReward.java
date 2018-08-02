package custom;

public class ChancedReward {

    private Reward reward;
    private double chance;

    public ChancedReward(Reward reward, double chance) {
        this.reward = reward;
        this.chance = chance;
    }

    public Reward getReward() {
        return reward;
    }

    public double getChance() {
        return chance;
    }

    @Override
    public String toString() {
        return "ChancedReward{" +
                "reward=" + reward +
                ", chance=" + chance +
                '}';
    }
}
