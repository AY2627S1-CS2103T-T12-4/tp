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

public class UniqueGroupListTest {

    private static final Group T01 = new Group(new GroupName("T01"));
    private static final Group T02 = new Group(new GroupName("T02"));

    private final UniqueGroupList uniqueGroupList = new UniqueGroupList();

    @Test
    public void contains_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueGroupList.contains((Group) null));
        assertThrows(NullPointerException.class, () -> uniqueGroupList.contains((GroupName) null));
    }

    @Test
    public void contains_groupNotInList_returnsFalse() {
        assertFalse(uniqueGroupList.contains(T01));
        assertFalse(uniqueGroupList.contains(T01.getName()));
    }

    @Test
    public void contains_groupWithSameNameInDifferentCase_returnsTrue() {
        uniqueGroupList.add(T01);
        assertTrue(uniqueGroupList.contains(new Group(new GroupName("t01"))));
        assertTrue(uniqueGroupList.contains(new GroupName("t01")));
    }

    @Test
    public void add_sameNameInDifferentCase_throwsDuplicateGroupException() {
        uniqueGroupList.add(T01);
        Group sameName = new Group(new GroupName("t01"));
        assertThrows(DuplicateGroupException.class, () -> uniqueGroupList.add(sameName));
    }

    @Test
    public void find_existingAndMissingName_correctResult() {
        uniqueGroupList.add(T01);
        assertEquals(Optional.of(T01), uniqueGroupList.find(new GroupName("t01")));
        assertEquals(Optional.empty(), uniqueGroupList.find(T02.getName()));
    }

    @Test
    public void remove_groupDoesNotExist_throwsGroupNotFoundException() {
        assertThrows(GroupNotFoundException.class, () -> uniqueGroupList.remove(T01));
    }

    @Test
    public void remove_existingGroup_removesGroup() {
        uniqueGroupList.add(T01);
        uniqueGroupList.remove(T01);
        assertTrue(uniqueGroupList.asUnmodifiableObservableList().isEmpty());
    }

    @Test
    public void setGroups_listWithDuplicateNames_throwsDuplicateGroupException() {
        List<Group> groupsWithSameName = List.of(T01, new Group(new GroupName("t01")));
        assertThrows(DuplicateGroupException.class, () -> uniqueGroupList.setGroups(groupsWithSameName));
    }

    @Test
    public void setGroups_validList_replacesContents() {
        uniqueGroupList.add(T01);
        uniqueGroupList.setGroups(List.of(T02));
        assertEquals(List.of(T02), uniqueGroupList.asUnmodifiableObservableList());
    }

    @Test
    public void equals() {
        uniqueGroupList.add(T01);
        UniqueGroupList sameList = new UniqueGroupList();
        sameList.add(T01);

        assertTrue(uniqueGroupList.equals(sameList));
        assertEquals(uniqueGroupList.hashCode(), sameList.hashCode());
        assertFalse(uniqueGroupList.equals(new UniqueGroupList()));
    }
}
