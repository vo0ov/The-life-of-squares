package ru.vo0ov.TheLifeOfSquares;

import javafx.animation.AnimationTimer;
import javafx.scene.canvas.GraphicsContext;

import static ru.vo0ov.TheLifeOfSquares.Functions.*;

public class MyAnimationTimer {
    private static long lastUpdateTime = System.nanoTime();

    public static AnimationTimer getAnimationTimer(GraphicsContext gc) {
        return new AnimationTimer() {
            @Override
            public void handle(long now) {
                long deltaTime = now - lastUpdateTime;
                if (deltaTime >= 1000F / Config.fps * 1000000F) {
                    if (!Config.startWithPause) {
                        Config.step++;

                        cleanAll(gc);
                        drawAll(gc);

                        lastUpdateTime = now;
                    }
                }
            }
        };
    }
}
