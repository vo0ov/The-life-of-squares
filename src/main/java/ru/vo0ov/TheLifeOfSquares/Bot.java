package ru.vo0ov.TheLifeOfSquares;

import javafx.scene.paint.Color;

import java.util.List;
import java.util.Random;

public class Bot extends Entity {
    int satiety;
    Color color;

    public Bot(int x, int y, int satiety, Color color) {
        this.netX = x;
        this.netY = y;
        this.satiety = satiety;
        this.color = color;
    }

    public static Color getRandomColor(Random random) {
        int red = random.nextInt(256);
        int green = random.nextInt(256);
        int blue = random.nextInt(256);

        return Color.rgb(red, green, blue);
    }

    public boolean up() {
        if (this.netY != 0) {
            this.netY--;
            return true;
        }
        return false;
    }

    public boolean down() {
        if (this.netY != Config.netHeight) {
            this.netY++;
            return true;
        }
        return false;
    }

    public boolean left() {
        if (this.netX != 0) {
            this.netX--;
            return true;
        }
        return false;
    }

    public boolean right() {
        if (this.netX != Config.netWidth) {
            this.netX++;
            return true;
        }
        return false;
    }

    public static void summon(List<Bot> bots, Random random, int x, int y, int satiety, Color color) {
        bots.add(new Bot(x, y, satiety, color));
    }

    public static void summon(List<Bot> bots, Random random, int x, int y, int satiety) {
        summon(bots, random, x, y, satiety, getRandomColor(random));
    }

    public static void summon(List<Bot> bots, Random random, int x, int y, Color color) {
        summon(bots, random, x, y, Config.defaultSatiety, color);
    }

    public static void summon(List<Bot> bots, Random random, int x, int y) {
        summon(bots, random, x, y, Config.defaultSatiety, getRandomColor(random));
    }

    public static void summon(List<Bot> bots, Random random, int satiety) {
        summon(bots, random, random.nextInt(0, Config.netWidth + 1), random.nextInt(0, Config.netHeight + 1), Config.defaultSatiety);
    }

    public static void summon(List<Bot> bots, Random random, Color color) {
        summon(bots, random, random.nextInt(0, Config.netWidth + 1), random.nextInt(0, Config.netHeight + 1), Config.defaultSatiety, color);
    }

    public static void summon(List<Bot> bots, Random random) {
        summon(bots, random, random.nextInt(0, Config.netWidth + 1), random.nextInt(0, Config.netHeight + 1), Config.defaultSatiety, getRandomColor(random));
    }

    public static void removeRandom(List<Bot> bots, Random random) {
        if (!bots.isEmpty()) {
            bots.remove(random.nextInt(bots.size()));
        }
    }
}
