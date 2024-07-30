package ru.vo0ov.TheLifeOfSquares;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;

import static ru.vo0ov.TheLifeOfSquares.Functions.*;

public class MyUpdateTimer {
    private static long lastUpdateTime = System.nanoTime();

    public static AnimationTimer getUpdateTimer() {
        return new AnimationTimer() {
            @Override
            public void handle(long now) {
                long deltaTime = now - lastUpdateTime;
                if (deltaTime >= 1000F / Config.stepsPerFrame * 1000000F || Config.turbo) {
                    updateAll();

                    lastUpdateTime = now;
                }
            }
        };
    }
}
