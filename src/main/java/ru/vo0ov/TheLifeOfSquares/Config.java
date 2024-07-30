package ru.vo0ov.TheLifeOfSquares;

import javafx.scene.paint.Color;

import java.util.*;

public class Config {
    ////////////////////////////
    //   Системные настройки  //
    ////////////////////////////
    public static float fps = 60f;
    public static float stepsPerFrame = 25f;
    public static final int width = 1920;
    public static final int height = 1080;
    public static final int cellSize = 50;
    public static final boolean inFullScreen = true;
    public static final int netWidth = width / cellSize - 1;
    public static final int netHeight = height / cellSize - 1;
    public static final Random random = new Random();

    ////////////////////////////
    //    Настройки гемплея   //
    ////////////////////////////
    public static boolean startWithPause = false;
    public static final boolean drawGrid = false;
    public static boolean turbo = false;
    public static final int defaultSatiety = 1;
    public static final int reproductionSatiety = 2;

    ////////////////////////////
    //   Количество объектов  //
    ////////////////////////////
    public static final int botsCount = 5; // IGNORE IF useCommands == true
    public static final int eatsCount = 1;
    public static final int poisonsCount = 2;

    ////////////////////////////
    //          Цвета         //
    ////////////////////////////
    public static final Color eatColor = Color.GREEN;
    public static final Color poisonColor = Color.DARKRED;

    ////////////////////////////
    //    Настройки команд    //
    ////////////////////////////
    public static final boolean useCommands = true;
    public static final HashMap<Color, Integer> commands = new HashMap<>();

    static {
        commands.put(Color.RED, 10);
        commands.put(Color.LIME, 10);
    }

    ////////////////////////////
    //       НЕ ТРОГАТЬ       //
    ////////////////////////////
    public static List<Bot> bots = new ArrayList<>();
    public static List<Eat> eats = new ArrayList<>();
    public static List<Poison> poisons = new ArrayList<>();
    public static long step = 0;
    public static boolean isEnd = false;
    public static boolean textRendered = false;
    public static Map<Color, Integer[]> teams = new HashMap<>();
}
