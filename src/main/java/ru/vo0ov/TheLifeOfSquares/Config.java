package ru.vo0ov.TheLifeOfSquares;

import javafx.scene.paint.Color;

public class Config {
    public static final int width = 1920;
    public static final int height = 1080;
    public static final int cellSize = 50;
    public static final boolean inFullScreen = true;

    public static final int netWidth = width / cellSize - 1;
    public static final int netHeight = height / cellSize - 1;

    public static float fps = 35f;

    // Defaults
    public static final int defaultSatiety = 1;

    // Settings
    public static boolean startWithPause = false;
    public static final boolean drawGrid = false;
    public static boolean turbo = false;
    public static final int reproductionSatiety = 2;

    // Counts
    public static final int botsCount = 5;
    public static final int eatsCount = 1;
    public static final int poisonsCount = 2;

    // Colors
    public static final Color eatColor = Color.GREEN;
    public static final Color poisonColor = Color.DARKRED;

    private Config() {
    }
}
