package seedu.address.model.group;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.model.group.exceptions.DuplicateGroupException;
import seedu.address.model.group.exceptions.GroupNotFoundException;
import seedu.address.testutil.TypicalGroups;

public class UniqueGroupListTest {

    private final Group t01 = TypicalGroups.getT01();
    private final Group t02 = TypicalGroups.getT02();

    private final UniqueGroupList uniqueGroupList = new UniqueGroupList();

    @Test
    public void contains_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueGroupList.contains((Group) null));
        assertThrows(NullPointerException.class, () -> uniqueGroupList.contains((GroupName) null));
    }

    @Test
    public void contains_groupNotInList_returnsFalse() {
        assertFalse(uniqueGroupList.contains(t01));
        assertFalse(uniqueGroupList.contains(t01.getName()));
    }

    @Test
    public void contains_groupWithSameNameInDifferentCase_returnsTrue() {
        uniqueGroupList.add(t01);
        assertTrue(uniqueGroupList.contains(new Group(new GroupName("t01"))));
        assertTrue(uniqueGroupList.contains(new GroupName("t01")));
    }

    @Test
    public void add_sameNameInDifferentCase_throwsDuplicateGroupException() {
        uniqueGroupList.add(t01);
        Group sameName = new Group(new GroupName("t01"));
        assertThrows(DuplicateGroupException.class, () -> uniqueGroupList.add(sameName));
    }

    @Test
    public void find_existingAndMissingName_correctResult() {
        uniqueGroupList.add(t01);
        assertEquals(Optional.of(t01), uniqueGroupList.find(new GroupName("t01")));
        assertEquals(Optional.empty(), uniqueGroupList.find(t02.getName()));
    }

    @Test
    public void remove_groupDoesNotExist_throwsGroupNotFoundException() {
        assertThrows(GroupNotFoundException.class, () -> uniqueGroupList.remove(t01));
    }

    @Test
    public void remove_existingGroup_removesGroup() {
        uniqueGroupList.add(t01);
        uniqueGroupList.remove(t01);
        assertTrue(uniqueGroupList.asUnmodifiableObservableList().isEmpty());
    }

    @Test
    public void setGroups_listWithDuplicateNames_throwsDuplicateGroupException() {
        List<Group> groupsWithSameName = List.of(t01, new Group(new GroupName("t01")));
        assertThrows(DuplicateGroupException.class, () -> uniqueGroupList.setGroups(groupsWithSameName));
    }

    @Test
    public void setGroups_validList_replacesContents() {
        uniqueGroupList.add(t01);
        uniqueGroupList.setGroups(List.of(t02));
        assertEquals(List.of(t02), uniqueGroupList.asUnmodifiableObservableList());
    }

    @Test
    public void equals() {
        uniqueGroupList.add(t01);
        UniqueGroupList sameList = new UniqueGroupList();
        sameList.add(t01);

        assertTrue(uniqueGroupList.equals(sameList));
        assertEquals(uniqueGroupList.hashCode(), sameList.hashCode());
        assertFalse(uniqueGroupList.equals(new UniqueGroupList()));
    }
}
