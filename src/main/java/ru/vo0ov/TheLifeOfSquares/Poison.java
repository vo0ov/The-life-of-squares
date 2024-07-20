package ru.vo0ov.TheLifeOfSquares;

import java.util.List;
import java.util.Random;

public class Poison extends Entity {
    public Poison(int netX, int netY) {
        this.netX = netX;
        this.netY = netY;
    }

    public static void summon(int x, int y) {
        Config.poisons.add(new Poison(x, y));
    }

    public static void summon() {
        summon(Config.random.nextInt(0, Config.netWidth + 1), Config.random.nextInt(0, Config.netHeight + 1));
    }

    public static void removeRandom() {
        if (!Config.poisons.isEmpty()) {
            Config.poisons.remove(Config.random.nextInt(Config.poisons.size()));
        }
    }
}
