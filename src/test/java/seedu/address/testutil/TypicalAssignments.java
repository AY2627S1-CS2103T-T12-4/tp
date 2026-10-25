package seedu.address.testutil;

import seedu.address.model.assignment.Assignment;
import seedu.address.model.assignment.AssignmentName;

/**
 * A utility class containing typical {@code Assignment} objects to be used in tests.
 */
public class TypicalAssignments {

    public static final AssignmentName NAME_PS1 = new AssignmentName("PS1");
    public static final AssignmentName NAME_PS2 = new AssignmentName("PS2");

    public static final Assignment PS1 = new Assignment(NAME_PS1);
    public static final Assignment PS2 = new Assignment(NAME_PS2);

    // Manually added, so that it is not in any typical group
    public static final Assignment PS3 = new Assignment(new AssignmentName("PS3"));

    private TypicalAssignments() {} // prevents instantiation
}
