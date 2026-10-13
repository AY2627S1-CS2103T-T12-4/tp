package seedu.address.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalGroups.NAME_T01;
import static seedu.address.testutil.TypicalGroups.NAME_T02;
import static seedu.address.testutil.TypicalGroups.NAME_T03;
import static seedu.address.testutil.TypicalGroups.NAME_T04;
import static seedu.address.testutil.TypicalStudents.FIONA;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.commons.core.GuiSettings;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;
import seedu.address.model.group.exceptions.DuplicateGroupException;
import seedu.address.model.group.exceptions.GroupNotFoundException;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentId;
import seedu.address.testutil.TAssistBuilder;
import seedu.address.testutil.TypicalGroups;

public class ModelManagerTest {

    private ModelManager modelManager = new ModelManager();

    @Test
    public void constructor() {
        assertEquals(new UserPrefs(), modelManager.getUserPrefs());
        assertEquals(new GuiSettings(), modelManager.getGuiSettings());
        assertEquals(new TAssist(), modelManager.getTAssist());
    }

    @Test
    public void constructor_noTAssistGiven_startsWithNoGroups() {
        assertEquals(new TAssist(), modelManager.getTAssist());
    }

    @Test
    public void constructor_noTAssistGiven_showsNoActiveGroup() {
        assertActiveGroupShown(modelManager, Optional.empty());
    }

    @Test
    public void constructor_nullTAssist_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new ModelManager(null, new UserPrefs()));
    }

    @Test
    public void constructor_tAssistWithActiveGroup_showsActiveGroupAndItsStudents() {
        modelManager = modelWithActiveGroup(NAME_T01);
        assertEquals(TypicalGroups.getTypicalTAssist().getGroupList(), modelManager.getTAssist().getGroupList());
        assertActiveGroupShown(modelManager, Optional.of(TypicalGroups.getT01()));
    }

    @Test
    public void constructor_validTAssist_copiesTAssist() {
        TAssist tAssist = typicalTAssistWithActiveGroup(NAME_T01);
        modelManager = new ModelManager(tAssist, new UserPrefs());

        // Modifying tAssist should not modify modelManager's TAssist data
        tAssist.setActiveGroup(NAME_T02);
        tAssist.addStudent(NAME_T01, FIONA);
        assertEquals(typicalTAssistWithActiveGroup(NAME_T01), modelManager.getTAssist());
    }

    @Test
    public void constructor_validUserPrefs_copiesUserPrefs() {
        UserPrefs userPrefs = new UserPrefs();
        userPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        modelManager = new ModelManager(new TAssist(), userPrefs);
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
    public void equals() {
        TAssist tAssist = typicalTAssistWithActiveGroup(NAME_T01);
        TAssist differentTAssist = new TAssist();
        UserPrefs userPrefs = new UserPrefs();

        // same values -> returns true
        modelManager = new ModelManager(tAssist, userPrefs);
        ModelManager modelManagerCopy = new ModelManager(tAssist, userPrefs);
        assertTrue(modelManager.equals(modelManagerCopy));

        // same object -> returns true
        assertTrue(modelManager.equals(modelManager));

        // null -> returns false
        assertFalse(modelManager.equals(null));

        // different types -> returns false
        assertFalse(modelManager.equals(5));

        // different TAssist -> returns false
        assertFalse(modelManager.equals(new ModelManager(differentTAssist, userPrefs)));

        // different userPrefs -> returns false
        UserPrefs differentUserPrefs = new UserPrefs();
        differentUserPrefs.setGuiSettings(new GuiSettings(1, 2, 3, 4));
        assertFalse(modelManager.equals(new ModelManager(tAssist, differentUserPrefs)));
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
    public void setTAssist_otherActiveGroup_showsNewActiveGroup() {
        modelManager = modelWithActiveGroup(NAME_T01);
        modelManager.setTAssist(typicalTAssistWithActiveGroup(NAME_T02));
        assertActiveGroupShown(modelManager, Optional.of(TypicalGroups.getT02()));
    }

    @Test
    public void setTAssist_noActiveGroup_clearsActiveGroupViews() {
        modelManager = modelWithActiveGroup(NAME_T01);
        modelManager.setTAssist(new TAssist());
        assertActiveGroupShown(modelManager, Optional.empty());
    }

    @Test
    public void setTAssist_sameActiveGroup_followsNewCopyOfGroup() {
        modelManager = modelWithActiveGroup(NAME_T01);
        modelManager.setTAssist(typicalTAssistWithActiveGroup(NAME_T01));
        modelManager.addStudent(NAME_T01, FIONA);
        assertTrue(modelManager.getActiveGroupStudentList().contains(FIONA));
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
    public void setActiveGroup_existingGroup_showsNewActiveGroupAndItsStudents() {
        modelManager = modelWithActiveGroup(NAME_T01);
        modelManager.setActiveGroup(NAME_T02);
        assertActiveGroupShown(modelManager, Optional.of(TypicalGroups.getT02()));
    }

    @Test
    public void setActiveGroup_existingGroup_notifiesActiveGroupListeners() {
        modelManager = modelWithActiveGroup(NAME_T01);
        List<Optional<Group>> notifiedGroups = new ArrayList<>();
        modelManager.activeGroupProperty().addListener((observable, oldGroup, newGroup) ->
                notifiedGroups.add(newGroup));

        modelManager.setActiveGroup(NAME_T03);

        assertEquals(List.of(Optional.of(TypicalGroups.getT03())), notifiedGroups);
    }

    @Test
    public void setActiveGroup_alreadyActiveGroup_keepsViewsAndDoesNotNotify() {
        modelManager = modelWithActiveGroup(NAME_T01);
        List<Optional<Group>> notifiedGroups = new ArrayList<>();
        modelManager.activeGroupProperty().addListener((observable, oldGroup, newGroup) ->
                notifiedGroups.add(newGroup));

        modelManager.setActiveGroup(new GroupName("t01"));

        assertTrue(notifiedGroups.isEmpty());
        assertActiveGroupShown(modelManager, Optional.of(TypicalGroups.getT01()));
    }

    @Test
    public void setTAssist_equalActiveGroup_doesNotNotifyButStudentListFollows() {
        modelManager = modelWithActiveGroup(NAME_T01);
        List<Optional<Group>> notifiedGroups = new ArrayList<>();
        modelManager.activeGroupProperty().addListener((observable, oldGroup, newGroup) ->
                notifiedGroups.add(newGroup));

        modelManager.setTAssist(typicalTAssistWithActiveGroup(NAME_T01));
        modelManager.addStudent(NAME_T01, FIONA);

        assertTrue(notifiedGroups.isEmpty());
        assertTrue(modelManager.getActiveGroupStudentList().contains(FIONA));
    }

    @Test
    public void setActiveGroup_afterSwitching_oldGroupChangesDoNotReachStudentList() {
        modelManager = modelWithActiveGroup(NAME_T01);
        modelManager.setActiveGroup(NAME_T02);
        modelManager.setActiveGroup(NAME_T01);

        modelManager.addStudent(NAME_T02, FIONA);

        assertActiveGroupShown(modelManager, Optional.of(TypicalGroups.getT01()));
    }

    @Test
    public void setActiveGroup_missingGroup_throwsAndKeepsActiveGroup() {
        modelManager = modelWithActiveGroup(NAME_T01);
        assertThrows(GroupNotFoundException.class, () -> modelManager.setActiveGroup(NAME_T04));
        assertEquals(Optional.of(TypicalGroups.getT01()), modelManager.getActiveGroup());
    }

    @Test
    public void setActiveGroup_missingGroup_keepsActiveGroupViews() {
        modelManager = modelWithActiveGroup(NAME_T01);
        assertThrows(GroupNotFoundException.class, () -> modelManager.setActiveGroup(NAME_T04));
        assertActiveGroupShown(modelManager, Optional.of(TypicalGroups.getT01()));
    }

    @Test
    public void addStudent_nullArguments_throwsNullPointerException() {
        modelManager = modelWithActiveGroup(NAME_T01);
        assertThrows(NullPointerException.class, () -> modelManager.addStudent(null, FIONA));
        assertThrows(NullPointerException.class, () -> modelManager.addStudent(NAME_T01, null));
    }

    @Test
    public void removeStudent_nullArguments_throwsNullPointerException() {
        modelManager = modelWithActiveGroup(NAME_T01);
        StudentId studentId = FIONA.getStudentId();
        assertThrows(NullPointerException.class, () -> modelManager.removeStudent(null, studentId));
        assertThrows(NullPointerException.class, () -> modelManager.removeStudent(NAME_T01, null));
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
    public void addStudent_toActiveGroup_studentListShowsStudent() {
        modelManager = modelWithActiveGroup(NAME_T01);
        modelManager.addStudent(NAME_T01, FIONA);
        assertTrue(modelManager.getActiveGroupStudentList().contains(FIONA));
        assertEquals(modelManager.getGroup(NAME_T01).getStudentList(), modelManager.getActiveGroupStudentList());
    }

    @Test
    public void addStudent_toInactiveGroup_studentListUnchanged() {
        modelManager = modelWithActiveGroup(NAME_T01);
        modelManager.addStudent(NAME_T02, FIONA);
        assertActiveGroupShown(modelManager, Optional.of(TypicalGroups.getT01()));
    }

    @Test
    public void addStudent_toPreviouslyActiveGroup_studentListUnchanged() {
        modelManager = modelWithActiveGroup(NAME_T01);
        modelManager.setActiveGroup(NAME_T02);
        modelManager.addStudent(NAME_T01, FIONA);
        assertEquals(TypicalGroups.getT02().getStudentList(), modelManager.getActiveGroupStudentList());
    }

    @Test
    public void removeStudent_fromActiveGroup_studentListNoLongerShowsStudent() {
        modelManager = modelWithActiveGroup(NAME_T01);
        Student student = TypicalGroups.getT01().getStudentList().getFirst();

        Student removedStudent = modelManager.removeStudent(NAME_T01, student.getStudentId());

        assertEquals(student, removedStudent);
        assertFalse(modelManager.getActiveGroupStudentList().contains(student));
    }

    @Test
    public void getActiveGroupStudentList_modifyList_throwsUnsupportedOperationException() {
        modelManager = modelWithActiveGroup(NAME_T01);
        assertThrows(UnsupportedOperationException.class, () -> modelManager.getActiveGroupStudentList().remove(0));
    }

    @Test
    public void equals_differentActiveGroup_returnsFalse() {
        UserPrefs userPrefs = new UserPrefs();
        modelManager = new ModelManager(typicalTAssistWithActiveGroup(NAME_T01), userPrefs);

        assertTrue(modelManager.equals(new ModelManager(typicalTAssistWithActiveGroup(NAME_T01), userPrefs)));
        assertFalse(modelManager.equals(new ModelManager(typicalTAssistWithActiveGroup(NAME_T02), userPrefs)));
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
        return new ModelManager(typicalTAssistWithActiveGroup(activeGroupName), new UserPrefs());
    }

    /**
     * Asserts that {@code model} shows {@code expectedGroup} as the active group, along with its students.
     */
    private static void assertActiveGroupShown(Model model, Optional<Group> expectedGroup) {
        assertEquals(expectedGroup, model.activeGroupProperty().get());
        List<Student> expectedStudents = expectedGroup.map(Group::getStudentList).map(List::copyOf)
                .orElse(List.of());
        assertEquals(expectedStudents, model.getActiveGroupStudentList());
    }
}
