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
import javafx.scene.text.TextAlignment;
import javafx.stage.Stage;
import javafx.animation.AnimationTimer;

import java.util.*;
import java.util.stream.Collectors;

public class Main extends Application {
    private final Random random = Config.random;
    private long lastUpdateTime = System.nanoTime();
    private boolean isEnd = false;
    private long year = 0;

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
                    if (Config.fps > 1) {
                        Config.fps--;
                    }
                    break;
                case RIGHT:
                    Config.fps++;
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
                    break;

                // Bot count
                case DOWN:
                    Bot.removeRandom();
                    break;
                case UP:
                    Bot.summon();
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
                        year++;

                        cleanAll(canvas, gc);
                        updateAll();
                        drawAll(gc);

                        // Leaderboard
                        Map<Color, Long> botColorsAndCounts = Config.bots.stream()
                                .collect(Collectors.groupingBy(
                                        bot -> Color.web(bot.color.toString().replace("0x", "")),
                                        Collectors.counting()
                                ));

                        Map<Color, Long> sortedBotColorsAndCounts = botColorsAndCounts.entrySet().stream()
                                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));


                        // Auto stop
                        if (sortedBotColorsAndCounts.size() == 1) {
                            if (!isEnd) {
                                Config.startWithPause = !Config.startWithPause;
                                isEnd = true;
                            }
                        } else {
                            isEnd = false;
                        }


                        double scWidth = 10.4 * ((sortedBotColorsAndCounts.size() > 9 ? 23 : 22) + (sortedBotColorsAndCounts.entrySet().iterator().next().getValue() + "").length());
                        gc.setFill(Color.rgb(50, 50, 50, 0.2));
                        gc.fillRect(Config.width - scWidth, 0, Config.width, Math.min(25 * sortedBotColorsAndCounts.size(), 250) + 20);


                        int counter = 1;
                        for (Map.Entry<Color, Long> entry : sortedBotColorsAndCounts.entrySet()) {
                            if (counter > 10) {
                                break;
                            }
                            gc.setFont(new Font("Arial", 20));
                            gc.setTextAlign(TextAlignment.RIGHT);
                            gc.setFill(entry.getKey());
                            gc.fillText(counter + ". Существ в команде: " + entry.getValue(), Config.width - 5, 5 + (25 * counter));
                            counter++;
                        }

                        String[] texts = {
                                "Скорость: " + (int) Config.fps,
                                "Существ: " + Config.bots.size(),
                                "Турбо: " + (Config.turbo ? "Да" : "Нет"),
                                "Пауза: " + (Config.startWithPause ? "Да" : "Нет"),
                                "Год: " + year,
                                "Еда: " + Config.eats.size(),
                                "Яд: " + Config.poisons.size()
                        };
                        int textsCounter = 1;
                        for (String text : texts) {
                            gc.setFont(new Font("Arial", 20));
                            gc.setTextAlign(TextAlignment.LEFT);
                            gc.setFill(Color.WHITE);
                            gc.fillText(text, 5, 25 * textsCounter);
                            textsCounter++;
                        }
                    } else {
                        gc.setFont(new Font("Arial", 20));
                        gc.setTextAlign(TextAlignment.CENTER);
                        if (isEnd) {
                            gc.setFill(Color.RED);
                            gc.fillText("Конец игры", (double) Config.width / 2, 25);
                        } else {
                            gc.setFill(Color.WHITE);
                            gc.fillText("Пауза", (double) Config.width / 2, 25);
                        }
                    }

                    lastUpdateTime = now;
                }
            }
        };
    }

    private void setupEntities() {
        if (Config.useCommands) {
            for (Map.Entry<Color, Integer> command : Config.commands.entrySet()) {
                for (int i = 0; i < command.getValue(); i++) {
                    Bot.summon(command.getKey());
                }
            }
        } else {
            for (int i = 0; i < Config.botsCount; i++) {
                Bot.summon();
            }
        }

        for (int i = 0; i < Config.eatsCount; i++) {
            Eat.summon();
        }

        for (int i = 0; i < Config.poisonsCount; i++) {
            Poison.summon();
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

        for (Bot bot : Config.bots) {
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
            for (Eat eat : Config.eats) {
                if (bot.netX == eat.netX && bot.netY == eat.netY) {
                    bot.satiety++;
                    Config.eats.remove(eat);
                    Eat.summon();
                    break;
                }
            }

            // Bot eat Poison()
            for (Poison poison : Config.poisons) {
                if (bot.netX == poison.netX && bot.netY == poison.netY) {
                    bot.satiety--;
                    Config.poisons.remove(poison);
                    Poison.summon();
                    break;
                }
            }

            // Bot attack
            for (Bot _bot : Config.bots) {
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
            Config.bots.remove(bot);
        }

        // Create new bots
        for (Bot bot : newBots) {
            Bot.summon(bot.netX, bot.netY, bot.color);
        }
    }

    private void drawBots(GraphicsContext gc) {
        for (Bot bot : Config.bots) {
            gc.setFill(bot.color);
            gc.fillRect(bot.netX * Config.cellSize, bot.netY * Config.cellSize, Config.cellSize, Config.cellSize);
        }
    }

    private void drawEats(GraphicsContext gc) {
        for (Eat eat : Config.eats) {
            gc.setFill(Config.eatColor);
            gc.fillArc(eat.netX * Config.cellSize, eat.netY * Config.cellSize, Config.cellSize, Config.cellSize, 0, 360, ArcType.OPEN);
        }
    }

    private void drawPoisons(GraphicsContext gc) {
        for (Poison poison : Config.poisons) {
            gc.setFill(Config.poisonColor);
            gc.fillArc(poison.netX * Config.cellSize, poison.netY * Config.cellSize, Config.cellSize, Config.cellSize, 0, 360, ArcType.OPEN);
        }
    }
}
