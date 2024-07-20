package ru.vo0ov.TheLifeOfSquares;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCombination;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main extends Application {
    private final Random random = new Random();
    private long lastUpdateTime = System.nanoTime();
    private List<Bot> bots = new ArrayList<>();
    private List<Eat> eats = new ArrayList<>();
    private List<Poison> poisons = new ArrayList<>();

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Canvas canvas = new Canvas(Config.width, Config.height);

        stage.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            switch (event.getCode()) {
                // Exit
                case ESCAPE:
                    stage.close();
                    break;

                // Eat count
                case W:
                    Eat.summon(eats, random);
                    break;
                case S:
                    Eat.removeRandom(eats, random);
                    break;

                // Poison count
                case A:
                    Poison.removeRandom(poisons, random);
                    break;
                case D:
                    Poison.summon(poisons, random);
                    break;

                // Turbo mode
                case E:
                    Config.turbo = !Config.turbo;
                    break;

                // Speed
                case LEFT:
                    if (Config.fps > 1) {
                        Config.fps--;
                    }
                    break;
                case RIGHT:
                    Config.fps++;
                    break;

                // Restart
                case R:
                    bots = new ArrayList<>();
                    eats = new ArrayList<>();
                    poisons = new ArrayList<>();
                    setupEntities();
                    break;

                // Pause
                case SPACE:
                    Config.startWithPause = !Config.startWithPause;
                    break;

                // Bot count
                case DOWN:
                    Bot.removeRandom(bots, random);
                    break;
                case UP:
                    Bot.summon(bots, random);
                    break;
            }
        });

        setupEntities();
        getAnimationTimer(canvas).start();

        StackPane root = new StackPane(canvas);
        root.setStyle("-fx-background-color: black;");
        Scene scene = new Scene(root);
        stage.setFullScreen(Config.inFullScreen);
        stage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
        stage.setResizable(false);
        stage.setScene(scene);
        stage.show();
    }

    private AnimationTimer getAnimationTimer(Canvas canvas) {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        return new AnimationTimer() {
            @Override
            public void handle(long now) {
                long deltaTime = now - lastUpdateTime;
                if ((deltaTime >= 1000F / Config.fps * 1000000F || Config.turbo)) {
                    if (!Config.startWithPause) {
                        cleanAll(canvas, gc);
                        updateAll();
                        drawAll(gc);

                        gc.setFont(new Font("Arial", 20));
                        gc.setFill(Color.WHITE);
                        if (!Config.turbo) {
                            gc.fillText((int) Config.fps + " FPS", 5, 25);
                        } else {
                            gc.fillText("??? FPS", 5, 25);
                        }
                    } else {
                        gc.setFont(new Font("Arial", 20));
                        gc.setFill(Color.WHITE);
                        gc.fillText("Пауза", (double) Config.width / 2 - 25, 25);
                    }

                    lastUpdateTime = now;
                }
            }
        };
    }

    private void setupEntities() {
        for (int i = 0; i < Config.botsCount; i++) {
            Bot.summon(bots, random);
        }

        for (int i = 0; i < Config.eatsCount; i++) {
            Eat.summon(eats, random);
        }

        for (int i = 0; i < Config.poisonsCount; i++) {
            Poison.summon(poisons, random);
        }
    }

    private void cleanAll(Canvas canvas, GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private void drawAll(GraphicsContext gc) {
        drawEats(gc);
        drawPoisons(gc);
        drawBots(gc);
        if (Config.drawGrid) {
            drawGrid(gc);
        }
    }

    private void drawGrid(GraphicsContext gc) {
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(1);
        for (double x = 0; x <= Config.width; x += 50) {
            gc.strokeLine(x, 0, x, Config.height);
        }
        for (double y = 0; y <= Config.height; y += 50) {
            gc.strokeLine(0, y, Config.width, y);
        }
    }

    private void updateAll() {
        List<Bot> diedBots = new ArrayList<>();
        List<Bot> newBots = new ArrayList<>();

        for (Bot bot : bots) {
            // Bot move
            if (random.nextInt(2) == 1) {
                boolean stepNotOk = true;
                while (stepNotOk) {
                    int step = random.nextInt(1, 5);
                    stepNotOk = switch (step) {
                        case 1 -> !bot.up();
                        case 2 -> !bot.down();
                        case 3 -> !bot.left();
                        case 4 -> !bot.right();
                        default -> true;
                    };
                }
            }

            // Bot eat Eat()
            for (Eat eat : eats) {
                if (bot.netX == eat.netX && bot.netY == eat.netY) {
                    bot.satiety++;
                    eats.remove(eat);
                    Eat.summon(eats, random);
                    break;
                }
            }

            // Bot eat Poison()
            for (Poison poison : poisons) {
                if (bot.netX == poison.netX && bot.netY == poison.netY) {
                    bot.satiety--;
                    poisons.remove(poison);
                    Poison.summon(poisons, random);
                    break;
                }
            }

            // Bot attack
            for (Bot _bot : bots) {
                if (bot.netX == _bot.netX && bot.netY == _bot.netY) {
                    if (bot.color != _bot.color) {
                        Bot wonBot;
                        Bot dieBot;
                        if (bot.satiety > _bot.satiety) {
                            wonBot = bot;
                            dieBot = _bot;
                        } else {
                            wonBot = _bot;
                            dieBot = bot;
                        }

                        wonBot.satiety += dieBot.satiety;
                        diedBots.add(dieBot);
                    }
                }
            }

            // Bot die if needed it
            if (bot.satiety <= 0) {
                diedBots.add(bot);
            }

            // Reproduction
            if (bot.satiety >= Config.reproductionSatiety) {
                bot.satiety = Config.defaultSatiety;
                newBots.add(bot);
            }
        }

        // Kill died bots
        for (Bot bot : diedBots) {
            bots.remove(bot);
        }

        // Create new bots
        for (Bot bot : newBots) {
            Bot.summon(bots, random, bot.netX, bot.netY, bot.color);
        }
    }

    private void drawBots(GraphicsContext gc) {
        for (Bot bot : bots) {
            gc.setFill(bot.color);
            gc.fillRect(bot.netX * Config.cellSize, bot.netY * Config.cellSize, Config.cellSize, Config.cellSize);
        }
    }

    private void drawEats(GraphicsContext gc) {
        for (Eat eat : eats) {
            gc.setFill(Config.eatColor);
            gc.fillArc(eat.netX * Config.cellSize, eat.netY * Config.cellSize, Config.cellSize, Config.cellSize, 0, 360, ArcType.OPEN);
        }
    }

    private void drawPoisons(GraphicsContext gc) {
        for (Poison poison : poisons) {
            gc.setFill(Config.poisonColor);
            gc.fillArc(poison.netX * Config.cellSize, poison.netY * Config.cellSize, Config.cellSize, Config.cellSize, 0, 360, ArcType.OPEN);
        }
    }
}
