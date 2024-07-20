package ru.vo0ov.TheLifeOfSquares;

import java.util.List;
import java.util.Random;

public class Eat extends Entity {
    public Eat(int netX, int netY) {
        this.netX = netX;
        this.netY = netY;
    }

    public static void summon(List<Eat> eats, Random random, int x, int y) {
        eats.add(new Eat(x, y));
    }

    public static void summon(List<Eat> eats, Random random) {
        summon(eats, random, random.nextInt(0, Config.netWidth + 1), random.nextInt(0, Config.netHeight + 1));
    }

    public static void removeRandom(List<Eat> eats, Random random) {
        if (!eats.isEmpty()) {
            eats.remove(random.nextInt(eats.size()));
        }
    }
}
