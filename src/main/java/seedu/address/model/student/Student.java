package seedu.address.model.student;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Objects;
import java.util.Optional;

import seedu.address.commons.util.ToStringBuilder;

/**
 * Represents a student in a tutorial group.
 * A student is identified by their student ID, which is unique within a group.
 * Guarantees: details are present and not null, field values are validated, immutable.
 */
public class Student {

    private final StudentName name;
    private final StudentId studentId;
    private final WeeklyRecord<String> attendanceRecords;
    private final WeeklyRecord<Integer> participationRecords;

    /**
     * Every field must be present and not null.
     */
    public Student(StudentName name, StudentId studentId) {
        this(name, studentId, new WeeklyRecord<>(), new WeeklyRecord<>());
    }

    /**
     * Creates a student with the given details and weekly records.
     *
     * @param name The student's name.
     * @param studentId The student's identifier.
     * @param attendanceRecords The student's attendance records.
     * @param participationRecords The student's participation records.
     */
    public Student(StudentName name, StudentId studentId, WeeklyRecord<String> attendanceRecords,
            WeeklyRecord<Integer> participationRecords) {
        requireAllNonNull(name, studentId, attendanceRecords, participationRecords);
        this.name = name;
        this.studentId = studentId;
        this.attendanceRecords = attendanceRecords;
        this.participationRecords = participationRecords;
    }

    public StudentName getName() {
        return name;
    }

    public StudentId getStudentId() {
        return studentId;
    }

    /** Returns this student's attendance record for {@code week}, if one has been entered. */
    public Optional<String> getAttendanceRecord(int week) {
        return attendanceRecords.getRecord(week);
    }

    /** Returns all of this student's attendance records. */
    public WeeklyRecord<String> getAttendanceRecords() {
        return attendanceRecords;
    }

    /** Returns this student's participation record for {@code week}, if one has been entered. */
    public Optional<Integer> getParticipationRecord(int week) {
        return participationRecords.getRecord(week);
    }

    /** Returns all of this student's participation records. */
    public WeeklyRecord<Integer> getParticipationRecords() {
        return participationRecords;
    }

    /** Returns a copy with the attendance value for {@code week} added or replaced. */
    public Student withAttendanceRecord(int week, String status) {
        return new Student(name, studentId, attendanceRecords.withRecord(week, status), participationRecords);
    }

    /** Returns a copy with the participation value for {@code week} added or replaced. */
    public Student withParticipationRecord(int week, int score) {
        return new Student(name, studentId, attendanceRecords,
                participationRecords.withRecord(week, score));
    }

    /**
     * Returns true if both students have the same student ID.
     * This defines a weaker notion of equality between two students.
     */
    public boolean isSameStudent(Student otherStudent) {
        if (otherStudent == this) {
            return true;
        }

        return otherStudent != null
                && otherStudent.getStudentId().equals(getStudentId());
    }

    /**
     * Returns true if both students have the same name, student ID, attendance records,
     * and participation records.
     * This defines a stronger notion of equality between two students.
     */
    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof Student otherStudent)) {
            return false;
        }

        return name.equals(otherStudent.name)
                && studentId.equals(otherStudent.studentId)
                && attendanceRecords.equals(otherStudent.attendanceRecords)
                && participationRecords.equals(otherStudent.participationRecords);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, studentId, attendanceRecords, participationRecords);
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .add("name", name)
                .add("studentId", studentId)
                .add("attendanceRecords", attendanceRecords)
                .add("participationRecords", participationRecords)
                .toString();
    }

}
