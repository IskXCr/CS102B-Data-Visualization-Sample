package com.example.demo.controller;

import com.example.demo.DataManager;
import com.example.demo.model.CountryInfoEntry;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.WritableImage;
import javafx.stage.FileChooser;

import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class TablePageController {
    @FXML
    private SplitPane tablePage;
    @FXML
    private ComboBox<LocalDate> tableDateComboBox;

    @FXML
    private TableView<CountryInfoEntry> dataTable;

    @FXML
    private TextField searchTextField;

    @FXML
    private Label searchIndicator;

    @FXML
    private Button tableResetButton;

    @FXML
    private Button tableSaveButton;

    @FXML
    private Button tableLoadButton;

    private void initComponents() {
        tableDateComboBox.getItems().clear();
        tableDateComboBox.getItems().addAll(DataManager.getEntryMap().keySet());
    }

    /**
     * When initializing listeners, the following components shall be updated to ensure the correctness of the filtering result.
     */
    private void initListeners() {
        // Function: Allow the user to change the date of the table.
        tableDateComboBox.valueProperty().addListener((observable, oldVal, newVal) -> {
            if (newVal != oldVal && newVal != null) {
                // Reset search text field
                searchTextField.setText("");
                searchTextField.setDisable(false);
                searchIndicator.setText("Not found");

                // Update data table
                dataTable.getItems().clear();
                DataManager.getEntryMap().get(newVal).forEach(e -> dataTable.getItems().add(e));
            }
        });

        // Function: Make sure the search result updates automatically according to the filter.
        searchTextField.textProperty().addListener(((observableValue, oldVal, newVal) -> {
            List<CountryInfoEntry> filtered = new ArrayList<>();

            DataManager.getEntryMap().get(tableDateComboBox.getSelectionModel().getSelectedItem()).stream()
                    .filter(e -> e.name().toLowerCase().contains(newVal.toLowerCase()))
                    .forEach(filtered::add);
            if (filtered.size() > 0)
                searchIndicator.setText("Found " + filtered.size() + " entries.");
            else
                searchIndicator.setText("Not found");

            dataTable.getItems().clear();
            dataTable.getItems().addAll(filtered);
        }));

        tableResetButton.setOnAction(e -> {
            searchTextField.setText("");
            searchTextField.setDisable(false);
            searchIndicator.setText("Not found");

            List<CountryInfoEntry> filtered = new ArrayList<>(DataManager.getEntryMap().get(tableDateComboBox.getSelectionModel().getSelectedItem()));

            dataTable.getItems().clear();
            dataTable.getItems().addAll(filtered);
        });

        // Function: Save current data
        tableSaveButton.setOnAction(e -> {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save table to...");
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Comma-separated values", "*.csv"));
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            File dest = fileChooser.showSaveDialog(((Node) e.getTarget()).getScene().getWindow());
            if (dest != null)
                DataManager.writeEntryToFile(dest.toPath(), dataTable.getItems().stream().toList(), "Saved Datasheet Without Title");
        });

        tableLoadButton.setOnAction(e -> {
            searchTextField.setText("");
            searchTextField.setDisable(true);
            searchIndicator.setText("Unavailable");

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open table from...");
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Comma-separated values", "*.csv"));
            fileChooser.setInitialDirectory(new File(System.getProperty("user.home")));
            File dest = fileChooser.showOpenDialog(((Node) e.getTarget()).getScene().getWindow());
            if (dest != null) {
                try {
                    dataTable.getItems().clear();
                    dataTable.getItems().addAll(DataManager.readEntryFromFile(dest.toPath()).stream().toList());
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
            }
        });
    }

    @FXML
    private void initialize() {
        initComponents();
        initListeners();

        tableDateComboBox.getSelectionModel().select(tableDateComboBox.getItems().size() - 1);
    }
}