package seedu.address.ui;

import java.util.function.Function;
import java.util.stream.Collectors;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Region;
import seedu.address.model.person.Person;

/**
 * Shows the live contact roster in command index order.
 */
public class PersonListPanel extends UiPart<Region> {
    private static final String FXML = "PersonListPanel.fxml";

    @FXML
    private TableView<Person> personTable;
    @FXML
    private Label recordCount;

    /**
     * Creates a table backed by the filtered person list.
     */
    public PersonListPanel(ObservableList<Person> persons) {
        super(FXML);
        personTable.setItems(persons);
        recordCount.textProperty().bind(Bindings.size(persons).asString("%d shown"));
        addColumn("#", 48, person -> Integer.toString(persons.indexOf(person) + 1));
        addColumn("NAME", 190, person -> person.getName().fullName);
        addColumn("EMAIL", 220, person -> person.getEmail().value);
        addColumn("PHONE", 130, person -> person.getPhone().value);
        addColumn("ADDRESS", 240, person -> person.getAddress().value);
        addColumn("TAGS", 160, person -> person.getTags().stream()
                .map(tag -> tag.tagName).sorted().collect(Collectors.joining(", ")));
        // Indices change when filtering or deleting even if the remaining Person objects do not.
        persons.addListener((ListChangeListener<Person>) change -> personTable.refresh());
    }

    private void addColumn(String title, double width, Function<Person, String> value) {
        TableColumn<Person, String> column = new TableColumn<>(title);
        column.setPrefWidth(width);
        column.setMinWidth(width);
        column.setSortable(false);
        column.setReorderable(false);
        column.setCellValueFactory(cell -> new ReadOnlyStringWrapper(value.apply(cell.getValue())));
        column.setCellFactory(unused -> new RosterCell());
        personTable.getColumns().add(column);
    }

    /**
     * Keeps complete values available when a table column truncates long text.
     */
    private static class RosterCell extends TableCell<Person, String> {
        @Override
        protected void updateItem(String value, boolean isEmpty) {
            super.updateItem(value, isEmpty);
            setText(isEmpty ? null : value);
            Tooltip tooltip = isEmpty || value == null ? null : new Tooltip(value);
            if (tooltip != null) {
                tooltip.setWrapText(true);
                tooltip.setMaxWidth(480);
            }
            setTooltip(tooltip);
        }
    }
}
