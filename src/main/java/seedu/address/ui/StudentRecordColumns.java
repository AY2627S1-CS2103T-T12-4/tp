package seedu.address.ui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.Tooltip;
import seedu.address.model.assignment.Assignment;
import seedu.address.model.assignment.AssignmentName;
import seedu.address.model.assignment.AssignmentRecord;
import seedu.address.model.group.Group;
import seedu.address.model.student.AttendanceStatus;
import seedu.address.model.student.Student;

/**
 * Keeps the roster's record columns in sync with the active group's weekly and assignment records.
 */
class StudentRecordColumns {
    private static final String NOT_RECORDED = "Not recorded";
    private static final double WEEK_COLUMN_WIDTH = 180;
    private static final double ASSIGNMENT_COLUMN_WIDTH = 240;

    private final TableView<Student> table;
    private final List<TableColumn<Student, ?>> identityColumns;
    private final ObservableValue<Optional<Group>> activeGroup;
    private final ObservableList<Group> groups;
    private final ListChangeListener<Student> studentListener = change -> refreshColumns();
    private final ListChangeListener<Assignment> assignmentListener = change -> refreshColumns();
    private Group trackedGroup;

    /**
     * Follows the active group, including replacements caused by undo, redo, and failed-save rollback.
     */
    StudentRecordColumns(TableView<Student> table, ObservableValue<Optional<Group>> activeGroup,
            ObservableList<Group> groups) {
        this.table = table;
        this.identityColumns = List.copyOf(table.getColumns());
        this.activeGroup = activeGroup;
        this.groups = groups;
        activeGroup.addListener((observable, oldGroup, newGroup) -> followGroup());
        groups.addListener((ListChangeListener<Group>) change -> followGroup());
        followGroup();
    }

    /**
     * Resolves the current group from the live group list: the active property may still hold an equal old copy.
     */
    private void followGroup() {
        if (trackedGroup != null) {
            trackedGroup.getStudentList().removeListener(studentListener);
            trackedGroup.getAssignmentList().removeListener(assignmentListener);
        }
        trackedGroup = activeGroup.getValue().flatMap(active -> groups.stream()
                .filter(group -> group.getName().equals(active.getName())).findFirst()).orElse(null);
        if (trackedGroup != null) {
            trackedGroup.getStudentList().addListener(studentListener);
            trackedGroup.getAssignmentList().addListener(assignmentListener);
        }
        refreshColumns();
    }

    /**
     * Rebuilds headings only when the set of recorded weeks or assignment names changes.
     * Otherwise a refresh updates values while preserving resized columns and horizontal scroll position.
     */
    private void refreshColumns() {
        List<TableColumn<Student, ?>> columns = new ArrayList<>(identityColumns);
        if (trackedGroup != null) {
            addWeeklyColumns(columns, "Attendance", student -> student.getAttendanceRecords().getRecords(),
                    AttendanceStatus::formatStoredValue);
            addWeeklyColumns(columns, "Participation", student -> student.getParticipationRecords().getRecords(),
                    Object::toString);
            for (Assignment assignment : trackedGroup.getAssignmentList()) {
                AssignmentName name = assignment.getName();
                columns.add(createColumn("Assignment: " + name, ASSIGNMENT_COLUMN_WIDTH,
                        student -> describeAssignment(name, student)));
            }
        }
        List<String> headings = columns.stream().map(StudentRecordColumns::getHeading).toList();
        if (!headings.equals(table.getColumns().stream().map(StudentRecordColumns::getHeading).toList())) {
            table.getColumns().setAll(columns);
        }
        // Record columns keep readable widths and scroll horizontally; an empty record set keeps the wide name column.
        if (columns.size() == identityColumns.size()) {
            table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        } else if (table.getColumnResizePolicy() != TableView.UNCONSTRAINED_RESIZE_POLICY) {
            table.setColumnResizePolicy(TableView.UNCONSTRAINED_RESIZE_POLICY);
            identityColumns.forEach(column -> table.resizeColumn(column, column.getMinWidth() - column.getWidth()));
        }
        table.refresh();
    }

    private <T> void addWeeklyColumns(List<TableColumn<Student, ?>> columns, String title,
            Function<Student, Map<Integer, T>> records, Function<T, String> format) {
        trackedGroup.getStudentList().stream().flatMap(student -> records.apply(student).keySet().stream())
                .distinct().sorted().forEach(week -> columns.add(createColumn(title + " W" + week,
                        WEEK_COLUMN_WIDTH, student -> Optional.ofNullable(records.apply(student).get(week))
                                .map(format).orElse(NOT_RECORDED))));
    }

    private String describeAssignment(AssignmentName name, Student student) {
        AssignmentRecord record = Optional.ofNullable(trackedGroup).flatMap(group -> group.findAssignment(name))
                .map(assignment -> assignment.getStudentRecord(student.getStudentId()))
                .orElse(AssignmentRecord.notSubmitted());
        if (!record.isSubmitted()) {
            return "Not submitted";
        }
        return "Submitted | " + record.getGrade().map(grade -> grade + "/100").orElse("Not graded");
    }

    private TableColumn<Student, String> createColumn(String heading, double width,
            Function<Student, String> value) {
        TableColumn<Student, String> column = new TableColumn<>(heading);
        column.setMinWidth(width);
        column.setPrefWidth(width);
        column.setSortable(false);
        column.setReorderable(false);
        column.setCellValueFactory(cell -> new ReadOnlyStringWrapper(value.apply(cell.getValue())));
        column.setCellFactory(unused -> new StudentListPanel.StudentCell());
        // Assignment names may be long; the full heading is also available without widening the column.
        Label label = new Label(heading);
        label.setMaxWidth(width - 20);
        label.setTooltip(new Tooltip(heading));
        column.setText(null);
        column.setGraphic(label);
        column.setUserData(heading);
        return column;
    }

    private static String getHeading(TableColumn<Student, ?> column) {
        return column.getUserData() instanceof String heading ? heading : column.getText();
    }
}
