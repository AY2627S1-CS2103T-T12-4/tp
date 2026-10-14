package seedu.address.storage;

import java.util.Map;
import java.util.TreeMap;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentId;
import seedu.address.model.student.StudentName;
import seedu.address.model.student.WeeklyRecord;

/**
 * Jackson-friendly version of {@link Student}.
 */
class JsonAdaptedStudent {

    public static final String MISSING_FIELD_MESSAGE_FORMAT = "Student's %s field is missing.";

    private final String name;
    private final String studentId;
    private final Map<Integer, String> attendance;
    private final Map<Integer, Integer> participation;

    /**
     * Constructs a {@code JsonAdaptedStudent} with the given student details.
     */
    @JsonCreator
    public JsonAdaptedStudent(@JsonProperty("name") String name, @JsonProperty("studentId") String studentId,
            @JsonProperty("attendance") Map<Integer, String> attendance,
            @JsonProperty("participation") Map<Integer, Integer> participation) {
        this.name = name;
        this.studentId = studentId;
        this.attendance = attendance == null ? Map.of() : new TreeMap<>(attendance);
        this.participation = participation == null ? Map.of() : new TreeMap<>(participation);
    }

    /**
     * Constructs an adapted student without weekly records, for existing student-only data and tests.
     */
    public JsonAdaptedStudent(String name, String studentId) {
        this(name, studentId, Map.of(), Map.of());
    }

    /**
     * Converts a given {@code Student} into this class for Jackson use.
     */
    public JsonAdaptedStudent(Student source) {
        name = source.getName().fullName;
        studentId = source.getStudentId().value;
        attendance = new TreeMap<>(source.getAttendanceRecords().getRecords());
        participation = new TreeMap<>(source.getParticipationRecords().getRecords());
    }

    /**
     * Converts this Jackson-friendly adapted student object into the model's {@code Student} object.
     *
     * @throws IllegalValueException if there were any data constraints violated in the adapted student.
     */
    public Student toModelType() throws IllegalValueException {
        if (name == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "name"));
        }
        if (!StudentName.isValidStudentName(name)) {
            throw new IllegalValueException(StudentName.MESSAGE_CONSTRAINTS);
        }
        final StudentName modelName = new StudentName(name);

        if (studentId == null) {
            throw new IllegalValueException(String.format(MISSING_FIELD_MESSAGE_FORMAT, "studentId"));
        }
        if (!StudentId.isValidStudentId(studentId)) {
            throw new IllegalValueException(StudentId.MESSAGE_CONSTRAINTS);
        }
        final StudentId modelStudentId = new StudentId(studentId);

        try {
            return new Student(modelName, modelStudentId, new WeeklyRecord<>(attendance),
                    new WeeklyRecord<>(participation));
        } catch (IllegalArgumentException exception) {
            throw new IllegalValueException(exception.getMessage(), exception);
        }
    }

}
