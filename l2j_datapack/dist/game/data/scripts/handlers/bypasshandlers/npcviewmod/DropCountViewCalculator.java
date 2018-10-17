package handlers.bypasshandlers.npcviewmod;

public class DropCountViewCalculator {

    public static DropCountViewCalculator.MinMax getPreciseMinMax(double chance, long min, long max, boolean isPrecise) {
        if (!isPrecise || (chance <= 100)) {
            return new DropCountViewCalculator.MinMax(min, max);
        }

        int mult = (int) (chance) / 100;
        return new DropCountViewCalculator.MinMax(mult * min, (chance % 100) > 0 ? (mult + 1) * max : mult * max);
    }

    public static class MinMax {

        private final long min, max;

        MinMax(long min, long max) {
            this.min = min;
            this.max = max;
        }

        public long getMin() {
            return min;
        }

        public long getMax() {
            return max;
        }
    }

}
