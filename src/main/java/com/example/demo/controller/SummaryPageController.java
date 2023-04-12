package com.example.demo.controller;

import com.example.demo.DataManager;
import com.example.demo.model.CountryInfoEntry;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Comparator;

public class SummaryPageController {
    @FXML
    private GridPane summaryPage;

    @FXML
    private ComboBox<LocalDate> smryDateComboBox;

    @FXML
    private Button saveImageButton;

    @FXML
    private PieChart cumlCaseChart;

    @FXML
    private PieChart cumlDeathChart;

    @FXML
    private BarChart<String, Double> cumlCaseNormChart;

    private XYChart.Series<String, Double> cumlCaseNormSeries;
    private XYChart.Series<String, Double> deathNormSeries;

    private void initComponents() {
        smryDateComboBox.getItems().clear();
        smryDateComboBox.getItems().addAll(DataManager.getEntryMap().keySet());

        cumlCaseNormSeries = new XYChart.Series<>();
        cumlCaseNormSeries.setName("Norm Case");
        deathNormSeries = new XYChart.Series<>();
        deathNormSeries.setName("Norm Death");
        cumlCaseNormChart.getData().add(cumlCaseNormSeries);
        cumlCaseNormChart.getData().add(deathNormSeries);
    }

    /**
     * When initializing listeners, the following components shall be updated.
     */
    private void initListeners() {
        smryDateComboBox.valueProperty().addListener(((observableValue, oldVal, newVal) -> {
            if (newVal != null && newVal != oldVal) {
                // Update cumulative case chart
                cumlCaseChart.getData().clear();
                DataManager.getEntryMap().get(newVal).stream().filter(e -> !e.name().equals("Global"))
                        .sorted(Comparator.comparing(CountryInfoEntry::caseCmltTotal).reversed().thenComparing(CountryInfoEntry::name))
                        .limit(10).forEach(e -> cumlCaseChart.getData().add(new PieChart.Data(e.name(), e.caseCmltTotal())));

                // Update cumulative death chart
                cumlDeathChart.getData().clear();
                DataManager.getEntryMap().get(newVal).stream().filter(e -> !e.name().equals("Global"))
                        .sorted(Comparator.comparing(CountryInfoEntry::deathCmltTotal).reversed().thenComparing(CountryInfoEntry::name))
                        .limit(5).forEach(e -> cumlDeathChart.getData().add(new PieChart.Data(e.name(), e.deathCmltTotal())));

                // Update cumulative normalized case chart
                cumlCaseNormSeries.getData().clear();
                DataManager.getEntryMap().get(newVal).stream().filter(e -> !e.name().equals("Global"))
                        .sorted(Comparator.comparing(CountryInfoEntry::caseCmltNorm).reversed().thenComparing(CountryInfoEntry::name))
                        .limit(5).forEach(e -> cumlCaseNormSeries.getData().add(new XYChart.Data<>(e.name(), e.caseCmltNorm())));

                deathNormSeries.getData().clear();
                DataManager.getEntryMap().get(newVal).stream().filter(e -> !e.name().equals("Global"))
                        .sorted(Comparator.comparing(CountryInfoEntry::deathCmltNorm).reversed().thenComparing(CountryInfoEntry::name))
                        .limit(5).forEach(e -> deathNormSeries.getData().add(new XYChart.Data<>(e.name(), e.deathCmltNorm())));
            }
        }));

        saveImageButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save visualization image to...");
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("PNG", "*.png"));
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            File dest = fileChooser.showSaveDialog(((Node) e.getTarget()).getScene().getWindow());
            if (dest != null)
            {
                WritableImage image = ((Node) e.getTarget()).getScene().snapshot(null);
                try {
                    ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", dest);
                } catch (IOException ex) {
                    System.err.println("Failed to write chart image.");
                    ex.printStackTrace();
                }
            }
        });
    }

    @FXML
    private void initialize() {
        initComponents();
        initListeners();

        smryDateComboBox.getSelectionModel().select(smryDateComboBox.getItems().size() - 1);
    }
}
