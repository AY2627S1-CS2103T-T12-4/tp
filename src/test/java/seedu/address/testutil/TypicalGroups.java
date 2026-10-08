package seedu.address.testutil;

import static seedu.address.testutil.TypicalStudents.ALICE;
import static seedu.address.testutil.TypicalStudents.BEN;
import static seedu.address.testutil.TypicalStudents.CARL;
import static seedu.address.testutil.TypicalStudents.DANIEL;
import static seedu.address.testutil.TypicalStudents.ELLE;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import seedu.address.model.TAssist;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;

/**
 * A utility class containing typical {@code Group} objects to be used in tests.
 * A {@code Group} is mutable, so only the names are constants. Each {@code getT0X()} method returns a new
 * group, so that one test can never change the group seen by another test.
 */
public class TypicalGroups {

    public static final GroupName NAME_T01 = new GroupName("T01");
    public static final GroupName NAME_T02 = new GroupName("T02");
    public static final GroupName NAME_T03 = new GroupName("T03");

    // Manually added, so that it is not in any typical group
    public static final GroupName NAME_T04 = new GroupName("T04");

    private TypicalGroups() {} // prevents instantiation

    /**
     * Returns a new group T01 with the students ALICE, BEN and CARL.
     */
    public static Group getT01() {
        return new GroupBuilder().withName(NAME_T01).withStudents(ALICE, BEN, CARL).build();
    }

    /**
     * Returns a new group T02 with the students DANIEL and ELLE.
     */
    public static Group getT02() {
        return new GroupBuilder().withName(NAME_T02).withStudents(DANIEL, ELLE).build();
    }

    /**
     * Returns a new group T03 with no students.
     */
    public static Group getT03() {
        return new GroupBuilder().withName(NAME_T03).build();
    }

    /**
     * Returns a new list of new typical groups T01, T02 and T03.
     */
    public static List<Group> getTypicalGroups() {
        return new ArrayList<>(Arrays.asList(getT01(), getT02(), getT03()));
    }

    /**
     * Returns a new {@code TAssist} with the typical groups T01, T02 and T03, and no active group.
     */
    public static TAssist getTypicalTAssist() {
        TAssistBuilder builder = new TAssistBuilder();
        getTypicalGroups().forEach(builder::withGroup);
        return builder.build();
    }
}
