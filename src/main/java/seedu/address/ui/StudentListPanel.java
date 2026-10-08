package seedu.address.ui;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Optional;
import java.util.function.Function;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import seedu.address.model.group.Group;
import seedu.address.model.student.Student;

/**
 * Shows the students in the active tutorial group, or explains why there are none to show.
 */
public class StudentListPanel extends UiPart<Region> {
    static final String NO_ACTIVE_GROUP = "No active group";
    static final String STUDENTS_TITLE = "Students";
    static final String NO_ACTIVE_GROUP_HINT = "Add a tutorial group and make it active to see its students here.";
    static final String EMPTY_GROUP_TITLE_FORMAT = "No students in %s yet";
    static final String EMPTY_GROUP_HINT_FORMAT = "Students you add to %s will appear here.";

    private static final String FXML = "StudentListPanel.fxml";
    private static final double ROW_NUMBER_WIDTH = 56;
    private static final double TOOLTIP_MAX_WIDTH = 480;

    @FXML
    private TableView<Student> studentTable;
    @FXML
    private Label groupTitle;
    @FXML
    private Label studentCount;
    @FXML
    private VBox emptyState;
    @FXML
    private Label emptyTitle;
    @FXML
    private Label emptyHint;

    /**
     * Creates a table of the active group's students that follows {@code activeGroup} and {@code students}.
     */
    public StudentListPanel(ObservableValue<Optional<Group>> activeGroup, ObservableList<Student> students) {
        super(FXML);
        requireAllNonNull(activeGroup, students);

        studentTable.setItems(students);
        studentTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        addColumn("#", ROW_NUMBER_WIDTH, student -> Integer.toString(students.indexOf(student) + 1))
                .setMaxWidth(ROW_NUMBER_WIDTH);
        addColumn("NAME", 240, student -> student.getName().toString());
        addColumn("STUDENT ID", 140, student -> student.getStudentId().toString());
        // Row numbers depend on positions, which change when another student is removed.
        students.addListener((ListChangeListener<Student>) change -> studentTable.refresh());

        groupTitle.textProperty().bind(Bindings.createStringBinding(() -> describeTitle(activeGroup.getValue()),
                activeGroup));
        Tooltip fullTitle = new Tooltip();
        fullTitle.textProperty().bind(groupTitle.textProperty());
        groupTitle.setTooltip(fullTitle);
        BooleanBinding hasActiveGroup = Bindings.createBooleanBinding(() -> activeGroup.getValue().isPresent(),
                activeGroup);
        studentCount.visibleProperty().bind(hasActiveGroup);
        studentCount.managedProperty().bind(hasActiveGroup);
        studentCount.textProperty().bind(Bindings.createStringBinding(() -> describeCount(students.size()),
                students));
        emptyTitle.textProperty().bind(Bindings.createStringBinding(() -> describeEmptyTitle(activeGroup.getValue()),
                activeGroup));
        emptyHint.textProperty().bind(Bindings.createStringBinding(() -> describeEmptyHint(activeGroup.getValue()),
                activeGroup));
        showEmptyStateInsteadOfTable(students);
    }

    /**
     * Shows the explanation in place of the table, without its column headings, while there are no students.
     */
    private void showEmptyStateInsteadOfTable(ObservableList<Student> students) {
        BooleanBinding hasNoStudents = Bindings.isEmpty(students);
        emptyState.visibleProperty().bind(hasNoStudents);
        emptyState.managedProperty().bind(hasNoStudents);
        studentTable.visibleProperty().bind(hasNoStudents.not());
    }

    /**
     * Adds a column that shows {@code value} for each student, and returns the column.
     */
    private TableColumn<Student, String> addColumn(String title, double minWidth, Function<Student, String> value) {
        TableColumn<Student, String> column = new TableColumn<>(title);
        column.setMinWidth(minWidth);
        column.setPrefWidth(minWidth);
        column.setSortable(false);
        column.setReorderable(false);
        column.setCellValueFactory(cell -> new ReadOnlyStringWrapper(value.apply(cell.getValue())));
        column.setCellFactory(unused -> new StudentCell());
        studentTable.getColumns().add(column);
        return column;
    }

    /**
     * Returns the student count shown in the heading, such as "1 student" or "3 students".
     */
    static String describeCount(int count) {
        assert count >= 0 : "A group cannot have a negative number of students";
        return count + (count == 1 ? " student" : " students");
    }

    /**
     * Returns the card title, which is the name of the active group, or a general title when no group is active.
     */
    static String describeTitle(Optional<Group> activeGroup) {
        return activeGroup.map(group -> group.getName().toString()).orElse(STUDENTS_TITLE);
    }

    /**
     * Returns the title shown in place of the table when there are no students to show.
     */
    static String describeEmptyTitle(Optional<Group> activeGroup) {
        return activeGroup.map(group -> String.format(EMPTY_GROUP_TITLE_FORMAT, group.getName()))
                .orElse(NO_ACTIVE_GROUP);
    }

    /**
     * Returns the hint shown in place of the table when there are no students to show.
     */
    static String describeEmptyHint(Optional<Group> activeGroup) {
        return activeGroup.map(group -> String.format(EMPTY_GROUP_HINT_FORMAT, group.getName()))
                .orElse(NO_ACTIVE_GROUP_HINT);
    }

    /**
     * Shows a value in a table cell, with a tooltip that keeps the full value readable when the cell is too narrow.
     */
    private static class StudentCell extends TableCell<Student, String> {
        @Override
        protected void updateItem(String value, boolean isEmpty) {
            super.updateItem(value, isEmpty);
            boolean hasValue = !isEmpty && value != null;
            setText(hasValue ? value : null);
            setTooltip(hasValue ? createTooltip(value) : null);
        }

        private static Tooltip createTooltip(String value) {
            Tooltip tooltip = new Tooltip(value);
            tooltip.setWrapText(true);
            tooltip.setMaxWidth(TOOLTIP_MAX_WIDTH);
            return tooltip;
        }
    }
}
