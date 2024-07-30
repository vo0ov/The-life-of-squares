package ru.vo0ov.TheLifeOfSquares;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;

import java.util.*;

import static ru.vo0ov.TheLifeOfSquares.ChartGenerator.generateChart;
import static ru.vo0ov.TheLifeOfSquares.Functions.setupEntities;
import static ru.vo0ov.TheLifeOfSquares.MyAnimationTimer.getAnimationTimer;
import static ru.vo0ov.TheLifeOfSquares.MyUpdateTimer.getUpdateTimer;

public class Main extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        // Missing vars
        Canvas canvas = new Canvas(Config.width, Config.height);
        GraphicsContext gc = canvas.getGraphicsContext2D();

        // Entities
        setupEntities();

        // Timers
        AnimationTimer animationTimer = getAnimationTimer(gc);
        animationTimer.start();

        AnimationTimer updateTimer = getUpdateTimer();
        updateTimer.start();

        // Keybinding
        stage.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            switch (event.getCode()) {
                // Exit
                case ESCAPE:
                    stage.close();
                    break;

                // Eat count
                case W:
                    Eat.summon();
                    break;
                case S:
                    Eat.removeRandom();
                    break;

                // Poison count
                case A:
                    Poison.removeRandom();
                    break;
                case D:
                    Poison.summon();
                    break;

                // Turbo mode
                case E:
                    Config.turbo = !Config.turbo;
                    break;

                // Speed
                case LEFT:
                    if (Config.stepsPerFrame > 1) {
                        Config.stepsPerFrame--;
                    }
                    break;
                case RIGHT:
                    Config.stepsPerFrame++;
                    break;

                // Restart
                case R:
                    Config.bots = new ArrayList<>();
                    Config.eats = new ArrayList<>();
                    Config.poisons = new ArrayList<>();
                    setupEntities();
                    break;

                // Pause
                case SPACE:
                    Config.startWithPause = !Config.startWithPause;
                    if (Config.startWithPause) {
                        updateTimer.stop();
                    } else {
                        updateTimer.start();
                    }
                    break;

                // Bot count
                case DOWN:
                    Bot.removeRandom();
                    break;
                case UP:
                    Bot.summon();
                    break;

                // History
                case H:
                    generateChart(Config.teams);
                    Config.startWithPause = true;
                    break;
            }
        });

        // Window
        StackPane root = new StackPane(canvas);
        root.setStyle("-fx-background-color: black;");
        Scene scene = new Scene(root);
        stage.setFullScreen(Config.inFullScreen);
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }
}
