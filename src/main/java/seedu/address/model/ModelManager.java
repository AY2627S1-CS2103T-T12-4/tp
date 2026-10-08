package seedu.address.model;

import static java.util.Objects.requireNonNull;
import static seedu.address.commons.util.CollectionUtil.requireAllNonNull;

import java.util.Optional;
import java.util.function.Predicate;
import java.util.logging.Logger;

import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import seedu.address.commons.core.GuiSettings;
import seedu.address.commons.core.LogsCenter;
import seedu.address.model.group.Group;
import seedu.address.model.group.GroupName;
import seedu.address.model.person.Person;
import seedu.address.model.student.Student;
import seedu.address.model.student.StudentId;

/**
 * Represents the in-memory model of the address book data and the TAssist data.
 */
public class ModelManager implements Model {
    private static final Logger logger = LogsCenter.getLogger(ModelManager.class);

    private final AddressBook addressBook;
    private final TAssist tAssist;
    private final UserPrefs userPrefs;
    private final FilteredList<Person> filteredPersons;
    private final ActiveGroupTracker activeGroupTracker = new ActiveGroupTracker();

    /**
     * Initializes a ModelManager with copies of the given addressBook, tAssist and userPrefs.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, ReadOnlyTAssist tAssist, ReadOnlyUserPrefs userPrefs) {
        requireAllNonNull(addressBook, tAssist, userPrefs);

        logger.fine("Initializing with address book: " + addressBook + ", TAssist data: " + tAssist
                + " and user prefs " + userPrefs);

        this.addressBook = new AddressBook(addressBook);
        this.tAssist = new TAssist(tAssist);
        this.userPrefs = new UserPrefs(userPrefs);
        filteredPersons = new FilteredList<>(this.addressBook.getPersonList());
        trackActiveGroup();
    }

    /**
     * Initializes a ModelManager with a copy of the given addressBook and userPrefs, and no TAssist data.
     */
    public ModelManager(ReadOnlyAddressBook addressBook, ReadOnlyUserPrefs userPrefs) {
        this(addressBook, new TAssist(), userPrefs);
    }

    public ModelManager() {
        this(new AddressBook(), new UserPrefs());
    }

    //=========== UserPrefs ==================================================================================

    @Override
    public ReadOnlyUserPrefs getUserPrefs() {
        return userPrefs;
    }

    @Override
    public GuiSettings getGuiSettings() {
        return userPrefs.getGuiSettings();
    }

    @Override
    public void setGuiSettings(GuiSettings guiSettings) {
        requireNonNull(guiSettings);
        userPrefs.setGuiSettings(guiSettings);
    }

    //=========== AddressBook ================================================================================

    @Override
    public void setAddressBook(ReadOnlyAddressBook addressBook) {
        this.addressBook.resetData(addressBook);
    }

    @Override
    public ReadOnlyAddressBook getAddressBook() {
        return addressBook;
    }

    @Override
    public boolean hasPerson(Person person) {
        requireNonNull(person);
        return addressBook.hasPerson(person);
    }

    @Override
    public void deletePerson(Person target) {
        addressBook.removePerson(target);
    }

    @Override
    public void addPerson(Person person) {
        addressBook.addPerson(person);
        updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS);
    }

    @Override
    public void setPerson(Person target, Person editedPerson) {
        requireAllNonNull(target, editedPerson);

        addressBook.setPerson(target, editedPerson);
    }

    //=========== Filtered Person List Accessors =============================================================

    /**
     * Returns an unmodifiable view of the list of {@code Person} backed by the internal list of
     * {@code addressBook}
     */
    @Override
    public ObservableList<Person> getFilteredPersonList() {
        return filteredPersons;
    }

    @Override
    public void updateFilteredPersonList(Predicate<Person> predicate) {
        requireNonNull(predicate);
        filteredPersons.setPredicate(predicate);
    }

    //=========== TAssist ===================================================================================

    @Override
    public void setTAssist(ReadOnlyTAssist tAssist) {
        requireNonNull(tAssist);
        this.tAssist.resetData(tAssist);
        trackActiveGroup();
    }

    @Override
    public ReadOnlyTAssist getTAssist() {
        return tAssist;
    }

    @Override
    public boolean hasGroup(GroupName name) {
        requireNonNull(name);
        return tAssist.hasGroup(name);
    }

    @Override
    public void addGroup(Group group) {
        requireNonNull(group);
        tAssist.addGroup(group);
    }

    @Override
    public Group getGroup(GroupName name) {
        requireNonNull(name);
        return tAssist.getGroup(name);
    }

    @Override
    public void setActiveGroup(GroupName name) {
        requireNonNull(name);
        tAssist.setActiveGroup(name);
        logger.info("Active group is now " + name);
        trackActiveGroup();
    }

    @Override
    public Optional<Group> getActiveGroup() {
        return tAssist.getActiveGroup();
    }

    @Override
    public void addStudent(GroupName groupName, Student student) {
        tAssist.addStudent(groupName, student);
    }

    @Override
    public Student removeStudent(GroupName groupName, StudentId studentId) {
        return tAssist.removeStudent(groupName, studentId);
    }

    //=========== Active Group Accessors =====================================================================

    @Override
    public ReadOnlyObjectProperty<Optional<Group>> activeGroupProperty() {
        return activeGroupTracker.activeGroupProperty();
    }

    @Override
    public ObservableList<Student> getActiveGroupStudentList() {
        return activeGroupTracker.getStudentList();
    }

    /**
     * Points the active group views at the group that is active in {@code tAssist}.
     */
    private void trackActiveGroup() {
        activeGroupTracker.track(tAssist.getActiveGroup());
        assert activeGroupTracker.activeGroupProperty().get().equals(tAssist.getActiveGroup())
                : "The active group views must show the active group";
    }

    @Override
    public boolean equals(Object other) {
        if (other == this) {
            return true;
        }

        // instanceof handles nulls
        if (!(other instanceof ModelManager otherModelManager)) {
            return false;
        }

        return addressBook.equals(otherModelManager.addressBook)
                && tAssist.equals(otherModelManager.tAssist)
                && userPrefs.equals(otherModelManager.userPrefs)
                && filteredPersons.equals(otherModelManager.filteredPersons);
    }

}
