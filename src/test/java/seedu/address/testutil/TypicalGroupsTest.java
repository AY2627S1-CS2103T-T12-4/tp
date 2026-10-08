package seedu.address.testutil;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.TypicalGroups.NAME_T01;
import static seedu.address.testutil.TypicalGroups.NAME_T02;
import static seedu.address.testutil.TypicalGroups.NAME_T03;
import static seedu.address.testutil.TypicalGroups.NAME_T04;
import static seedu.address.testutil.TypicalStudents.ALICE;
import static seedu.address.testutil.TypicalStudents.BEN;
import static seedu.address.testutil.TypicalStudents.CARL;
import static seedu.address.testutil.TypicalStudents.DANIEL;
import static seedu.address.testutil.TypicalStudents.ELLE;
import static seedu.address.testutil.TypicalStudents.FIONA;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.model.TAssist;
import seedu.address.model.group.Group;

public class TypicalGroupsTest {

    @Test
    public void getTypicalGroups_containsGroupsWithTypicalStudents() {
        List<Group> groups = TypicalGroups.getTypicalGroups();
        assertEquals(List.of(NAME_T01, NAME_T02, NAME_T03), groups.stream().map(Group::getName).toList());
        assertEquals(List.of(ALICE, BEN, CARL), groups.get(0).getStudentList());
        assertEquals(List.of(DANIEL, ELLE), groups.get(1).getStudentList());
        assertTrue(groups.get(2).getStudentList().isEmpty());
    }

    @Test
    public void getT01_changeGroup_nextGroupUnchanged() {
        Group group = TypicalGroups.getT01();
        group.addStudent(FIONA);

        Group other = TypicalGroups.getT01();
        assertNotSame(group, other);
        assertFalse(other.hasStudent(FIONA.getStudentId()));
    }

    @Test
    public void getTypicalTAssist_changeData_nextTAssistUnchanged() {
        TAssist tAssist = TypicalGroups.getTypicalTAssist();
        assertEquals(TypicalGroups.getTypicalGroups(), tAssist.getGroupList());
        assertEquals(Optional.empty(), tAssist.getActiveGroup());

        tAssist.addStudent(NAME_T03, FIONA);
        assertEquals(TypicalGroups.getTypicalTAssist().getGroup(NAME_T03), TypicalGroups.getT03());
    }

    @Test
    public void getTypicalTAssist_doesNotContainManuallyAddedFixtures() {
        TAssist tAssist = TypicalGroups.getTypicalTAssist();
        assertFalse(tAssist.hasGroup(NAME_T04));
        assertTrue(tAssist.getGroupList().stream().noneMatch(group -> group.hasStudent(FIONA.getStudentId())));
    }
}
