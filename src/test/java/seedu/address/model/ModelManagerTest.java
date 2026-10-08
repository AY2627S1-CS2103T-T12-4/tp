package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.model.Model.PREDICATE_SHOW_ALL_PERSONS;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalGroups.NAME_T01;
import static seedu.address.testutil.TypicalGroups.NAME_T02;
import static seedu.address.testutil.TypicalGroups.NAME_T04;
import static seedu.address.testutil.TypicalPersons.ALICE;
import static seedu.address.testutil.TypicalPersons.BENSON;
import static seedu.address.testutil.TypicalStudents.FIONA;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.GuiSettings;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;
import seedu.address.model.group.exceptions.DuplicateGroupException;
import seedu.address.model.group.exceptions.GroupNotFoundException;
import seedu.address.model.person.NameContainsKeywordsPredicate;
import seedu.address.model.student.Student;
import seedu.address.testutil.AddressBookBuilder;
import seedu.address.testutil.TAssistBuilder;
import seedu.address.testutil.TypicalGroups;

public class ModelManagerTest {

    private ModelManager modelManager = new ModelManager();

    @Test
    public void constructor() {
        assertEquals(new UserPrefs(), modelManager.getUserPrefs());
        assertEquals(new GuiSettings(), modelManager.getGuiSettings());
        assertEquals(new AddressBook(), new AddressBook(modelManager.getAddressBook()));
    }

    @Test
    public void constructor_noTAssistGiven_startsWithNoGroups() {
        assertEquals(new TAssist(), modelManager.getTAssist());
    }

    @Test
    public void constructor_nullTAssist_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ModelManager(new AddressBook(), null, new UserPrefs()));
    }

    @Test
    public void constructor_validTAssist_copiesTAssist() {
        TAssist tAssist = typicalTAssistWithActiveGroup(NAME_T01);
        modelManager = new ModelManager(new AddressBook(), tAssist, new UserPrefs());

        // Modifying tAssist should not modify modelManager's TAssist data
        tAssist.setActiveGroup(NAME_T02);
        tAssist.addStudent(NAME_T01, FIONA);
        assertEquals(typicalTAssistWithActiveGroup(NAME_T01), modelManager.getTAssist());
    }

    @Test
    public void constructor_validUserPrefs_copiesUserPrefs() {
        UserPrefs userPrefs = new UserPrefs();
        userPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        modelManager = new ModelManager(new AddressBook(), userPrefs);
        assertEquals(userPrefs, modelManager.getUserPrefs());

        // Modifying userPrefs should not modify modelManager's userPrefs
        UserPrefs oldUserPrefs = new UserPrefs(userPrefs);
        userPrefs.setGuiSettings(new GuiSettings(5, 6, 7, 8));
        assertEquals(oldUserPrefs, modelManager.getUserPrefs());
    }

    @Test
    public void setGuiSettings_nullGuiSettings_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setGuiSettings(null));
    }

    @Test
    public void setGuiSettings_validGuiSettings_setsGuiSettings() {
        GuiSettings guiSettings = new GuiSettings(1, 2, 3, 4);
        modelManager.setGuiSettings(guiSettings);
        assertEquals(guiSettings, modelManager.getGuiSettings());
    }

    @Test
    public void hasPerson_nullPerson_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.hasPerson(null));
    }

    @Test
    public void hasPerson_personNotInAddressBook_returnsFalse() {
        assertFalse(modelManager.hasPerson(ALICE));
    }

    @Test
    public void hasPerson_personInAddressBook_returnsTrue() {
        modelManager.addPerson(ALICE);
        assertTrue(modelManager.hasPerson(ALICE));
    }

    @Test
    public void getFilteredPersonList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, () -> modelManager.getFilteredPersonList().remove(0));
    }

    @Test
    public void equals() {
        AddressBook addressBook = new AddressBookBuilder().withPerson(ALICE).withPerson(BENSON).build();
        AddressBook differentAddressBook = new AddressBook();
        UserPrefs userPrefs = new UserPrefs();

        // same values -> returns true
        modelManager = new ModelManager(addressBook, userPrefs);
        ModelManager modelManagerCopy = new ModelManager(addressBook, userPrefs);
        assertTrue(modelManager.equals(modelManagerCopy));

        // same object -> returns true
        assertTrue(modelManager.equals(modelManager));

        // null -> returns false
        assertFalse(modelManager.equals(null));

        // different types -> returns false
        assertFalse(modelManager.equals(5));

        // different addressBook -> returns false
        assertFalse(modelManager.equals(new ModelManager(differentAddressBook, userPrefs)));

        // different filteredList -> returns false
        String[] keywords = ALICE.getName().fullName.split("\\s+");
        modelManager.updateFilteredPersonList(new NameContainsKeywordsPredicate(List.of(keywords)));
        assertFalse(modelManager.equals(new ModelManager(addressBook, userPrefs)));

        // resets modelManager to initial state for upcoming tests
        modelManager.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);

        // different userPrefs -> returns false
        UserPrefs differentUserPrefs = new UserPrefs();
        differentUserPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        assertFalse(modelManager.equals(new ModelManager(addressBook, differentUserPrefs)));
    }

    @Test
    public void setTAssist_nullTAssist_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setTAssist(null));
    }

    @Test
    public void setTAssist_newData_replacesData() {
        modelManager = modelWithActiveGroup(NAME_T01);
        modelManager.setTAssist(typicalTAssistWithActiveGroup(NAME_T02));
        assertEquals(typicalTAssistWithActiveGroup(NAME_T02), modelManager.getTAssist());
    }

    @Test
    public void hasGroup_nullName_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.hasGroup(null));
    }

    @Test
    public void hasGroup_groupInTAssist_returnsTrueIgnoringCase() {
        modelManager = modelWithActiveGroup(NAME_T01);
        assertTrue(modelManager.hasGroup(new GroupName("t02")));
        assertFalse(modelManager.hasGroup(NAME_T04));
    }

    @Test
    public void addGroup_newGroup_addsGroupWithoutMakingItActive() {
        modelManager = modelWithActiveGroup(NAME_T01);
        modelManager.addGroup(new Group(NAME_T04));
        assertTrue(modelManager.hasGroup(NAME_T04));
        assertEquals(Optional.of(TypicalGroups.getT01()), modelManager.getActiveGroup());
    }

    @Test
    public void addGroup_duplicateName_throwsDuplicateGroupException() {
        modelManager = modelWithActiveGroup(NAME_T01);
        assertThrows(DuplicateGroupException.class, () -> modelManager.addGroup(new Group(new GroupName("t03"))));
    }

    @Test
    public void getGroup_existingGroup_returnsGroup() {
        modelManager = modelWithActiveGroup(NAME_T01);
        assertEquals(TypicalGroups.getT02(), modelManager.getGroup(new GroupName("t02")));
    }

    @Test
    public void getGroup_missingGroup_throwsGroupNotFoundException() {
        assertThrows(GroupNotFoundException.class, () -> modelManager.getGroup(NAME_T04));
    }

    @Test
    public void setActiveGroup_nullName_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> modelManager.setActiveGroup(null));
    }

    @Test
    public void setActiveGroup_existingGroupInDifferentCase_makesGroupActive() {
        modelManager = modelWithActiveGroup(NAME_T01);
        modelManager.setActiveGroup(new GroupName("t02"));
        assertEquals(Optional.of(TypicalGroups.getT02()), modelManager.getActiveGroup());
    }

    @Test
    public void setActiveGroup_missingGroup_throwsAndKeepsActiveGroup() {
        modelManager = modelWithActiveGroup(NAME_T01);
        assertThrows(GroupNotFoundException.class, () -> modelManager.setActiveGroup(NAME_T04));
        assertEquals(Optional.of(TypicalGroups.getT01()), modelManager.getActiveGroup());
    }

    @Test
    public void addStudent_toGroup_addsStudentToThatGroup() {
        modelManager = modelWithActiveGroup(NAME_T01);
        modelManager.addStudent(new GroupName("t02"), FIONA);
        assertTrue(modelManager.getGroup(NAME_T02).hasStudent(FIONA.getStudentId()));
        assertFalse(modelManager.getGroup(NAME_T01).hasStudent(FIONA.getStudentId()));
    }

    @Test
    public void removeStudent_studentInGroup_removesAndReturnsStudent() {
        modelManager = modelWithActiveGroup(NAME_T01);
        Student student = TypicalGroups.getT02().getStudentList().getFirst();

        Student removedStudent = modelManager.removeStudent(NAME_T02, student.getStudentId());

        assertEquals(student, removedStudent);
        assertFalse(modelManager.getGroup(NAME_T02).hasStudent(student.getStudentId()));
    }

    @Test
    public void equals_differentTAssist_returnsFalse() {
        AddressBook addressBook = new AddressBook();
        UserPrefs userPrefs = new UserPrefs();
        modelManager = new ModelManager(addressBook, typicalTAssistWithActiveGroup(NAME_T01), userPrefs);

        assertTrue(modelManager.equals(
                new ModelManager(addressBook, typicalTAssistWithActiveGroup(NAME_T01), userPrefs)));
        assertFalse(modelManager.equals(
                new ModelManager(addressBook, typicalTAssistWithActiveGroup(NAME_T02), userPrefs)));
        assertFalse(modelManager.equals(new ModelManager(addressBook, userPrefs)));
    }

    /**
     * Returns the typical TAssist data with the group named {@code activeGroupName} active.
     */
    private static TAssist typicalTAssistWithActiveGroup(GroupName activeGroupName) {
        return new TAssistBuilder(TypicalGroups.getTypicalTAssist()).withActiveGroup(activeGroupName).build();
    }

    /**
     * Returns a model holding the typical TAssist data with the group named {@code activeGroupName} active.
     */
    private static ModelManager modelWithActiveGroup(GroupName activeGroupName) {
        return new ModelManager(new AddressBook(), typicalTAssistWithActiveGroup(activeGroupName), new UserPrefs());
    }
}
