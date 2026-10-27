package seedu.address.model.group;

import static java.util.Objects.requireNonNull;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;

import javafx.collections.ObservableList;
import seedu.address.commons.util.ToStringBuilder;
import seedu.address.model.assignment.Assignment;
import seedu.address.model.assignment.AssignmentName;
import seedu.address.model.assignment.UniqueAssignmentList;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentId;
import seedu.address.model.student.StudentName;
import seedu.address.model.student.UniqueStudentList;
import seedu.address.model.student.WeeklyRecord;
import seedu.address.model.student.WeeklyRecordEntry;

/**
 * Represents a tutorial group, the students in it, and the assignments created for it.
 * Guarantees: the name is present and not null; student IDs and assignment names are unique within the group.
 */
public class Group {

    private final GroupName name;
    private final UniqueStudentList students = new UniqueStudentList();
    private final UniqueAssignmentList assignments = new UniqueAssignmentList();

    /**
     * Creates an empty group with the given {@code name}.
     */
    public Group(GroupName name) {
        requireNonNull(name);
        this.name = name;
    }

    /**
     * Creates a copy of {@code toCopy}, so that changes to the copy do not affect the original.
     */
    public Group(Group toCopy) {
        this(requireNonNull(toCopy).name);
        students.setStudents(toCopy.getStudentList());
        assignments.setAssignments(toCopy.getAssignmentList());
    }

    /**
     * Returns a copy of this group with the given {@code newName}, and the same students and assignments.
     */
    public Group withName(GroupName newName) {
        Group renamed = new Group(newName);
        renamed.students.setStudents(getStudentList());
        renamed.assignments.setAssignments(getAssignmentList());
        return renamed;
    }

    public GroupName getName() {
        return name;
    }

    /**
     * Returns an unmodifiable view of the students in this group.
     */
    public ObservableList<Student> getStudentList() {
        return students.asUnmodifiableObservableList();
    }

    /**
     * Returns an unmodifiable view of the assignments created for this group, in creation order.
     */
    public ObservableList<Assignment> getAssignmentList() {
        return assignments.asUnmodifiableObservableList();
    }

    /**
     * Returns a row for every student and the student's record for {@code week}.
     *
     * @param week The teaching week to list.
     * @param recordSelector Selects the record kind from a student.
     * @param <T> The type of value held by the selected record.
     * @return An immutable list in the group's student order.
     */
    public <T> List<WeeklyRecordEntry<T>> getWeeklyRecordEntries(int week,
            Function<Student, WeeklyRecord<T>> recordSelector) {
        WeeklyRecord.validateWeek(week);
        requireNonNull(recordSelector);
        return getStudentList().stream()
                .map(student -> new WeeklyRecordEntry<>(student,
                        requireNonNull(recordSelector.apply(student)).getRecord(week)))
                .toList();
    }

    /**
     * Returns true if a student with {@code studentId} is in this group.
     */
    public boolean hasStudent(StudentId studentId) {
        return students.contains(studentId);
    }

    /**
     * Returns true if a student named {@code name}, ignoring capitalization, is in this group.
     */
    public boolean hasStudentNamed(StudentName name) {
        return getStudentList().stream()
                .anyMatch(student -> student.getName().isSameName(name));
    }

    /**
     * Returns the student with {@code studentId}, or an empty {@code Optional} if no such student is in this group.
     */
    public Optional<Student> findStudent(StudentId studentId) {
        return students.find(studentId);
    }

    /**
     * Adds {@code student} to this group.
     * No student in this group may have the same student ID.
     */
    public void addStudent(Student student) {
        students.add(student);
    }

    /**
     * Removes the student with {@code studentId} from this group and returns the removed student.
     * The student must be in this group.
     */
    public Student removeStudent(StudentId studentId) {
        Student removedStudent = students.remove(studentId);
        for (Assignment assignment : List.copyOf(getAssignmentList())) {
            if (assignment.getStudentRecords().containsKey(removedStudent.getStudentId())) {
                setAssignment(assignment.withoutStudentRecord(removedStudent.getStudentId()));
            }
        }
        return removedStudent;
    }

    /**
     * Replaces an existing student with the same ID, preserving that student's position in the group.
     *
     * @param student The updated student.
     */
    public void setStudent(Student student) {
        students.setStudent(requireNonNull(student));
    }

    /**
     * Returns true if an assignment named {@code assignmentName} (ignoring case) is in this group.
     */
    public boolean hasAssignment(AssignmentName assignmentName) {
        return assignments.contains(assignmentName);
    }

    /**
     * Returns the assignment named {@code assignmentName} (ignoring case),
     * or an empty {@code Optional} if no such assignment is in this group.
     */
    public Optional<Assignment> findAssignment(AssignmentName assignmentName) {
        return assignments.find(assignmentName);
    }

    /**
     * Adds {@code assignment} to this group.
     * No assignment in this group may have the same name, ignoring case.
     */
    public void addAssignment(Assignment assignment) {
        assignments.add(assignment);
    }

    /**
     * Replaces an existing assignment with the same name.
     */
    public void setAssignment(Assignment assignment) {
        assignments.setAssignment(requireNonNull(assignment));
    }

    /**
     * Returns true if both groups have the same name, ignoring case.
     * This defines a weaker notion of equality between two groups.
     */
    public boolean isSameGroup(Group otherGroup) {
        if (otherGroup == this) {
            return true;
        }

        return otherGroup != null
                && otherGroup.getName().equals(getName());
    }

    /**
     * Returns true if both groups have the same name, students and assignments.
     * This defines a stronger notion of equality between two groups.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Group otherGroup)) {
            return false;
        }

        return name.equals(otherGroup.name)
                && students.equals(otherGroup.students)
                && assignments.equals(otherGroup.assignments);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, students, assignments);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("students", students)
                .add("assignments", assignments)
                .toString();
    }

}
