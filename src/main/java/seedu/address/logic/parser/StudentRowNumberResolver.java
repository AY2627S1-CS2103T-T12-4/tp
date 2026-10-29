package seedu.address.logic.parser;

import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;
import static seedu.address.logic.parser.StudentCommandParserUtil.PREFIX_ID;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import seedu.address.commons.util.StringUtil;
import seedu.address.model.Model;
import seedu.address.model.group.Group;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentId;

/**
 * Replaces each row number given with {@code id/} by the ID of the student in that row of the Students table, so the
 * TA can name a student by the table's {@code #} column instead of typing the full student ID.
 */
public class StudentRowNumberResolver {

    private static final Pattern ROW_NUMBER =
            Pattern.compile("(?<!\\S)" + Pattern.quote(PREFIX_ID.getPrefix()) + "(\\d+)(?!\\S)");
    /** Matches commands whose {@code id/} gives a new student ID or part of one, not a student to act on. */
    private static final Pattern NEW_OR_PARTIAL_ID_COMMAND =
            Pattern.compile("student\\s+(add|find)(\\s.*)?", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    private StudentRowNumberResolver() {}

    /**
     * Returns {@code commandText} with each {@code id/ROW} replaced by the ID of the student in that row of the
     * students shown in {@code model}. A number that is a student's ID in the active group, or that is not a row
     * shown, is kept, so the command handles it as a student ID.
     */
    public static String replaceRowNumbers(String commandText, Model model) {
        requireAllNonNull(commandText, model);
        String input = TAssistParser.replaceUnicodeSpaces(commandText).trim();
        Optional<Group> activeGroup = model.getActiveGroup();
        if (activeGroup.isEmpty() || NEW_OR_PARTIAL_ID_COMMAND.matcher(input).matches()) {
            return commandText;
        }

        Matcher matcher = ROW_NUMBER.matcher(input);
        StringBuilder result = new StringBuilder();
        while (matcher.find()) {
            String studentId = toStudentId(matcher.group(1), activeGroup.get(), model.getDisplayedStudentList());
            matcher.appendReplacement(result, Matcher.quoteReplacement(PREFIX_ID.getPrefix() + studentId));
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private static String toStudentId(String value, Group group, List<Student> displayedStudents) {
        boolean isStudentId = StudentId.isValidStudentId(value)
                && group.findStudent(new StudentId(value)).isPresent();
        if (isStudentId || !StringUtil.isNonZeroUnsignedInteger(value)
                || Integer.parseInt(value) > displayedStudents.size()) {
            return value;
        }
        return displayedStudents.get(Integer.parseInt(value) - 1).getStudentId().toString();
    }

}
