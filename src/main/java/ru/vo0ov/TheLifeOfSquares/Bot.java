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

    public static Color getRandomColor() {
        int red = Config.random.nextInt(256);
        int green = Config.random.nextInt(256);
        int blue = Config.random.nextInt(256);

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

    public static void summon(int x, int y, int satiety, Color color) {
        Config.bots.add(new Bot(x, y, satiety, color));
    }

    public static void summon(int x, int y, int satiety) {
        summon(x, y, satiety, getRandomColor());
    }

    public static void summon(int x, int y, Color color) {
        summon(x, y, Config.defaultSatiety, color);
    }

    public static void summon(int x, int y) {
        summon(x, y, Config.defaultSatiety, getRandomColor());
    }

    public static void summon(int satiety) {
        summon(Config.random.nextInt(0, Config.netWidth + 1), Config.random.nextInt(0, Config.netHeight + 1), satiety);
    }

    public static void summon(Color color) {
        summon(Config.random.nextInt(0, Config.netWidth + 1), Config.random.nextInt(0, Config.netHeight + 1), Config.defaultSatiety, color);
    }

    public static void summon() {
        summon(Config.random.nextInt(0, Config.netWidth + 1), Config.random.nextInt(0, Config.netHeight + 1), Config.defaultSatiety, getRandomColor());
    }

    public static void removeRandom() {
        if (!Config.bots.isEmpty()) {
            Config.bots.remove(Config.random.nextInt(Config.bots.size()));
        }
    }
}
