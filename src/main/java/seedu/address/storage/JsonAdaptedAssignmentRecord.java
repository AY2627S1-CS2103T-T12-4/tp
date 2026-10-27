package seedu.address.storage;

import java.util.Optional;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.assignment.AssignmentName;
import seedu.address.model.assignment.AssignmentRecord;
import seedu.address.model.student.StudentId;

/**
 * Represents one student's assignment record in a Jackson-friendly form.
 */
class JsonAdaptedAssignmentRecord {
    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Assignment record's %s field is missing.";

    private final String assignmentName;
    private final String studentId;
    @JsonProperty("submitted")
    private final Boolean isSubmitted;
    private final Number grade;

    /**
     * Creates an adapted record from JSON fields.
     */
    @JsonCreator
    public JsonAdaptedAssignmentRecord(@JsonProperty("assignmentName") String assignmentName,
            @JsonProperty("studentId") String studentId, @JsonProperty("submitted") Boolean isSubmitted,
            @JsonProperty("grade") Number grade) {
        this.assignmentName = assignmentName;
        this.studentId = studentId;
        this.isSubmitted = isSubmitted;
        this.grade = grade;
    }

    /**
     * Converts an assignment record into its JSON representation.
     */
    public JsonAdaptedAssignmentRecord(AssignmentName assignmentName, StudentId studentId,
            AssignmentRecord record) {
        this(assignmentName.fullName, studentId.value, record.isSubmitted(), record.getGrade().orElse(null));
    }

    /**
     * Returns the validated assignment name in this record.
     */
    public AssignmentName getAssignmentName() throws IllegalValueException {
        if (assignmentName == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "assignmentName"));
        }
        if (!AssignmentName.isValidAssignmentName(assignmentName)) {
            throw new IllegalValueException(AssignmentName.MESSAGE_CONSTRAINTS);
        }
        return new AssignmentName(assignmentName);
    }

    /**
     * Returns the validated student ID in this record.
     */
    public StudentId getStudentId() throws IllegalValueException {
        if (studentId == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "studentId"));
        }
        if (!StudentId.isValidStudentId(studentId)) {
            throw new IllegalValueException(StudentId.MESSAGE_CONSTRAINTS);
        }
        return new StudentId(studentId);
    }

    /**
     * Converts the status and grade fields into a validated model record.
     */
    public AssignmentRecord toModelType() throws IllegalValueException {
        if (isSubmitted == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "submitted"));
        }
        try {
            return new AssignmentRecord(isSubmitted, toModelGrade());
        } catch (IllegalArgumentException exception) {
            throw new IllegalValueException(exception.getMessage(), exception);
        }
    }

    /**
     * Returns the grade as a whole number, rejecting a fractional grade instead of truncating it.
     */
    private Optional<Integer> toModelGrade() throws IllegalValueException {
        if (grade == null) {
            return Optional.empty();
        }
        Optional<Integer> wholeGrade = JsonWholeNumber.toInt(grade);
        if (wholeGrade.isEmpty()) {
            throw new IllegalValueException(AssignmentRecord.MESSAGE_INVALID_GRADE);
        }
        return wholeGrade;
    }
}
