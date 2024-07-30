package ru.vo0ov.TheLifeOfSquares;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;

import java.util.*;
import java.util.stream.Collectors;

public class Functions {
    public static void setupEntities() {
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

    public static void cleanAll(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.clearRect(0, 0, Config.width, Config.height);
        Config.textRendered = false;
    }

    public static void drawAll(GraphicsContext gc) {
        drawEats(gc);
        drawPoisons(gc);
        drawBots(gc);
        if (Config.drawGrid) {
            drawGrid(gc);
        }
        drawText(gc);
    }

    public static void drawGrid(GraphicsContext gc) {
        gc.setStroke(Color.WHITE);
        gc.setLineWidth(1);
        for (double x = 0; x <= Config.width; x += 50) {
            gc.strokeLine(x, 0, x, Config.height);
        }
        for (double y = 0; y <= Config.height; y += 50) {
            gc.strokeLine(0, y, Config.width, y);
        }
    }

    public static void updateAll() {
        List<Bot> diedBots = new ArrayList<>();
        List<Bot> newBots = new ArrayList<>();

        for (Bot bot : Config.bots) {
            // Bot move
            if (Config.random.nextInt(2) == 1) {
                boolean stepNotOk = true;
                while (stepNotOk) {
                    int step = Config.random.nextInt(1, 5);
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

    public static void drawBots(GraphicsContext gc) {
        for (Bot bot : Config.bots) {
            gc.setFill(bot.color);
            gc.fillRect(bot.netX * Config.cellSize, bot.netY * Config.cellSize, Config.cellSize, Config.cellSize);
        }
    }

    public static void drawEats(GraphicsContext gc) {
        for (Eat eat : Config.eats) {
            gc.setFill(Config.eatColor);
            gc.fillArc(eat.netX * Config.cellSize, eat.netY * Config.cellSize, Config.cellSize, Config.cellSize, 0, 360, ArcType.OPEN);
        }
    }

    public static void drawPoisons(GraphicsContext gc) {
        for (Poison poison : Config.poisons) {
            gc.setFill(Config.poisonColor);
            gc.fillArc(poison.netX * Config.cellSize, poison.netY * Config.cellSize, Config.cellSize, Config.cellSize, 0, 360, ArcType.OPEN);
        }
    }

    public static void drawText(GraphicsContext gc) {
        // Leaderboard
        Map<Color, Long> botColorsAndCounts = Config.bots.stream()
                .collect(Collectors.groupingBy(
                        bot -> Color.web(bot.color.toString().replace("0x", "")),
                        Collectors.counting()
                ));

        Map<Color, Long> sortedBotColorsAndCounts = botColorsAndCounts.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (oldValue, newValue) -> oldValue, LinkedHashMap::new));

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
            addValueToKey(Config.teams, entry.getKey(), entry.getValue().intValue());
            counter++;
        }


        // Auto stop
        if (sortedBotColorsAndCounts.size() == 1) {
            if (!Config.isEnd) {
                Config.startWithPause = !Config.startWithPause;
                Config.isEnd = true;
            }
        } else {
            Config.isEnd = false;
        }


        // Information
        String[] texts = {
                "Скорость: " + (int) Config.stepsPerFrame,
                "Существ: " + Config.bots.size(),
                "Турбо: " + (Config.turbo ? "Да" : "Нет"),
                "Пауза: " + (Config.startWithPause ? "Да" : "Нет"),
                "Шаг: " + Config.step,
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

        gc.setFont(new Font("Arial", 20));
        gc.setTextAlign(TextAlignment.CENTER);
        if (Config.isEnd) {
            gc.setFill(Color.RED);
            gc.fillText("Конец игры", (double) Config.width / 2, 25);
        } else {
            gc.setFill(Color.WHITE);
            gc.fillText("Пауза", (double) Config.width / 2, 25);
        }
    }

    private static void addValueToKey(Map<Color, Integer[]> map, Color key, int value) {
        if (!map.containsKey(key)) {
            // Если ключ отсутствует, создаем новый массив с одним элементом
            map.put(key, new Integer[]{value});
        } else {
            // Если ключ уже существует, добавляем новое значение в массив
            Integer[] array = map.get(key);
            Integer[] newArray = Arrays.copyOf(array, array.length + 1); // Создаем новый массив большего размера
            newArray[array.length] = value; // Добавляем новое значение в конец
            map.put(key, newArray); // Обновляем значение для этого ключа
        }
    }
}
