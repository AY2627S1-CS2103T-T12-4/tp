package seedu.address.testutil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalGroups.NAME_T01;
import static seedu.address.testutil.TypicalGroups.NAME_T02;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.model.TAssist;
import seedu.address.model.group.Group;
import seedu.address.model.group.exceptions.GroupNotFoundException;

public class TAssistBuilderTest {

    @Test
    public void build_noGroupAdded_emptyTAssistWithNoActiveGroup() {
        TAssist tAssist = new TAssistBuilder().build();
        assertEquals(new TAssist(), tAssist);
    }

    @Test
    public void build_activeGroupNamedBeforeGroupAdded_activeGroupSet() {
        TAssist tAssist = new TAssistBuilder()
                .withActiveGroup(NAME_T02)
                .withGroup(TypicalGroups.getT01())
                .withGroup(TypicalGroups.getT02())
                .build();
        assertEquals(Optional.of(TypicalGroups.getT02()), tAssist.getActiveGroup());
        assertEquals(List.of(TypicalGroups.getT01(), TypicalGroups.getT02()), tAssist.getGroupList());
    }

    @Test
    public void build_activeGroupNotAdded_throwsGroupNotFoundException() {
        TAssistBuilder builder = new TAssistBuilder().withActiveGroup(NAME_T01);
        assertThrows(GroupNotFoundException.class, builder::build);
    }

    @Test
    public void constructor_copyOfTAssist_changeBuilderResultOriginalUnchanged() {
        TAssist original = TypicalGroups.getTypicalTAssist();
        TAssist changed = new TAssistBuilder(original).withGroup(new Group(TypicalGroups.NAME_T04)).build();

        assertEquals(TypicalGroups.getTypicalTAssist(), original);
        assertEquals(original.getGroupList().size() + 1, changed.getGroupList().size());
    }
}
