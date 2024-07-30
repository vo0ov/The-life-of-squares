package ru.vo0ov.TheLifeOfSquares;// ChartGenerator.java

import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.Map;

public class ChartGenerator {

    public static void generateChart(Map<Color, Integer[]> teams) {
        // Создание осей
        CategoryAxis xAxis = new CategoryAxis();
        NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Шаги");
        yAxis.setLabel("Популяция");

        // Создание графика
        LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle("Статистика команд");
        lineChart.setCreateSymbols(false);
        lineChart.setAnimated(true);

        // Добавление данных в график
        int colorCounter = 0;
        for (Map.Entry<Color, Integer[]> entry : teams.entrySet()) {
            Color color = entry.getKey();
            String colorName = String.format("#%02X%02X%02X", (int) (color.getRed() * 255), (int) (color.getGreen() * 255), (int) (color.getBlue() * 255));
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(colorName);
            // lineChart.setStyle(lineChart.getStyle() + ".default-color" + colorCounter + ".chart-series-line { -fx-stroke:" + "#000000" + "; }\n");
            for (int i = 0; i < entry.getValue().length; i++) {
                series.getData().add(new XYChart.Data<>("Шаг " + (i + 1), entry.getValue()[i]));
            }
            lineChart.getData().add(series);

            Node seriesNone = series.getNode();
            seriesNone.setStyle("-fx-stroke: " + colorName + ";");

            colorCounter++;
        }

        // Создание и отображение окна
        Stage stage = new Stage();
        stage.setTitle("Статистика игры");
        Scene scene = new Scene(lineChart, 800, 600);
        stage.setScene(scene);
        stage.show();
    }
}
