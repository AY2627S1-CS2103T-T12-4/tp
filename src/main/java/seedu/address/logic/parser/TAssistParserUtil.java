package seedu.address.logic.parser;

import static java.util.Objects.requireNonNull;

import seedu.address.commons.util.StringUtil;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.group.GroupName;
import seedu.address.model.student.StudentId;
import seedu.address.model.student.StudentName;

/**
 * Validates and normalizes values shared by TAssist feature parsers.
 */
public class TAssistParserUtil {
    public static final int NAME_MAX_LENGTH = 60;
    public static final String MESSAGE_ASSIGNMENT_NAME = "Assignment names must contain 1-60 characters.";
    public static final String MESSAGE_WEEK = "Week must be a whole number from 1 to 13.";
    public static final String MESSAGE_PARTICIPATION_SCORE = "Participation score must be a whole number from 0 to 5.";
    public static final String MESSAGE_GRADE = "Grade must be a whole number from 0 to 100.";

    private TAssistParserUtil() {}

    /**
     * Returns whether a command keyword contains only lowercase letters.
     */
    public static boolean isValidCommandKeyword(String keyword) {
        requireNonNull(keyword);
        return keyword.matches("[a-z]+");
    }

    /**
     * Returns whether a prefix consists of a lowercase keyword followed by a slash.
     */
    public static boolean isValidPrefix(Prefix prefix) {
        requireNonNull(prefix);
        requireNonNull(prefix.getPrefix());
        return prefix.getPrefix().matches("[a-z]+/");
    }

    /**
     * Parses a student name using the domain's character, length, and whitespace rules.
     *
     * @throws ParseException if the name is invalid.
     */
    public static StudentName parseStudentName(String name) throws ParseException {
        requireNonNull(name);
        if (!StudentName.isValidStudentName(name)) {
            throw new ParseException(StudentName.MESSAGE_CONSTRAINTS);
        }
        return new StudentName(name);
    }

    /**
     * Parses a student ID using the domain's validation and uppercase normalization.
     *
     * @throws ParseException if the ID is invalid.
     */
    public static StudentId parseStudentId(String studentId) throws ParseException {
        requireNonNull(studentId);
        if (!StudentId.isValidStudentId(studentId)) {
            throw new ParseException(StudentId.MESSAGE_CONSTRAINTS);
        }
        return new StudentId(studentId);
    }

    /**
     * Parses a group name using the domain's character, length, and whitespace rules.
     *
     * @throws ParseException if the name is invalid.
     */
    public static GroupName parseGroupName(String name) throws ParseException {
        requireNonNull(name);
        if (!GroupName.isValidGroupName(name)) {
            throw new ParseException(GroupName.MESSAGE_CONSTRAINTS);
        }
        return new GroupName(name);
    }

    /**
     * Parses an assignment name, collapsing internal whitespace and preserving capitalization.
     *
     * @throws ParseException if the normalized name is empty or exceeds 60 characters.
     */
    public static String parseAssignmentName(String name) throws ParseException {
        requireNonNull(name);
        String normalizedName = StringUtil.normalizeSpaces(name);
        if (normalizedName.isEmpty() || normalizedName.length() > NAME_MAX_LENGTH) {
            throw new ParseException(MESSAGE_ASSIGNMENT_NAME);
        }
        return normalizedName;
    }

    /**
     * Parses a whole-number week from 1 to 13, ignoring surrounding whitespace.
     *
     * @throws ParseException if the week is invalid.
     */
    public static int parseWeek(String week) throws ParseException {
        return parseWholeNumber(week, 1, 13, MESSAGE_WEEK);
    }

    /**
     * Parses a whole-number participation score from 0 to 5, ignoring surrounding whitespace.
     *
     * @throws ParseException if the score is invalid.
     */
    public static int parseParticipationScore(String score) throws ParseException {
        return parseWholeNumber(score, 0, 5, MESSAGE_PARTICIPATION_SCORE);
    }

    /**
     * Parses a whole-number grade from 0 to 100, ignoring surrounding whitespace.
     *
     * @throws ParseException if the grade is invalid.
     */
    public static int parseGrade(String grade) throws ParseException {
        return parseWholeNumber(grade, 0, 100, MESSAGE_GRADE);
    }

    private static int parseWholeNumber(String value, int minimum, int maximum, String message) throws ParseException {
        requireNonNull(value);
        String trimmedValue = value.trim();
        if (!trimmedValue.matches("[0-9]+")) {
            throw new ParseException(message);
        }

        try {
            int number = Integer.parseInt(trimmedValue);
            if (number < minimum || number > maximum) {
                throw new ParseException(message);
            }
            return number;
        } catch (NumberFormatException e) {
            throw new ParseException(message, e);
        }
    }
}
