package seedu.address.ui;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;

/**
 * Shows an empty layout for a feature that is not implemented yet.
 */
public class FeaturePreview extends UiPart<Region> {
    @FXML
    private Label title;
    @FXML
    private Label description;
    @FXML
    private FlowPane filters;
    @FXML
    private TableView<Object> records;

    /**
     * Creates a clearly labeled preview without fabricated records.
     */
    public FeaturePreview(String heading, String explanation, String... columns) {
        super("FeaturePreview.fxml");
        title.setText(heading);
        description.setText(explanation);
        filters.setManaged(false);
        filters.setVisible(false);
        for (String name : columns) {
            TableColumn<Object, String> column = new TableColumn<>(name);
            column.setPrefWidth(220);
            column.setSortable(false);
            column.setReorderable(false);
            records.getColumns().add(column);
        }
        records.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    /**
     * Shows the planned week selector as unavailable until weekly records exist.
     */
    public void showWeeks() {
        filters.setManaged(true);
        filters.setVisible(true);
        filters.getChildren().add(new Label("Week"));
        for (int i = 1; i <= 13; i++) {
            Button week = new Button(Integer.toString(i));
            week.getStyleClass().add("week-chip");
            week.setDisable(true);
            filters.getChildren().add(week);
        }
    }
}
