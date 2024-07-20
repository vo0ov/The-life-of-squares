package ru.vo0ov.TheLifeOfSquares;

import java.util.List;
import java.util.Random;

public class Poison extends Entity {
    public Poison(int netX, int netY) {
        this.netX = netX;
        this.netY = netY;
    }

    public static void summon(List<Poison> poisons, Random random, int x, int y) {
        poisons.add(new Poison(x, y));
    }

    public static void summon(List<Poison> poisons, Random random) {
        summon(poisons, random, random.nextInt(0, Config.netWidth + 1), random.nextInt(0, Config.netHeight + 1));
    }

    public static void removeRandom(List<Poison> poisons, Random random) {
        if (!poisons.isEmpty()) {
            poisons.remove(random.nextInt(poisons.size()));
        }
    }
}
