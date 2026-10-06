package seedu.address.model.student;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import seedu.address.model.group.UniqueGroupList;
import seedu.address.model.student.exceptions.DuplicateStudentException;
import seedu.address.model.student.exceptions.StudentNotFoundException;

public class UniqueStudentListTest {

    private static final Student ALICE = new Student(new StudentName("Alice Tan"), new StudentId("A0123456X"));
    private static final Student BEN = new Student(new StudentName("Ben Lim"), new StudentId("A0234567Y"));
    private static final Student ALICE_SAME_ID =
            new Student(new StudentName("Alicia Tan"), new StudentId("a0123456x"));

    private final UniqueStudentList uniqueStudentList = new UniqueStudentList();

    @Test
    public void contains_nullStudent_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueStudentList.contains((Student) null));
        assertThrows(NullPointerException.class, () -> uniqueStudentList.contains((StudentId) null));
    }

    @Test
    public void contains_studentNotInList_returnsFalse() {
        assertFalse(uniqueStudentList.contains(ALICE));
        assertFalse(uniqueStudentList.contains(ALICE.getStudentId()));
    }

    @Test
    public void contains_studentWithSameIdInList_returnsTrue() {
        uniqueStudentList.add(ALICE);
        assertTrue(uniqueStudentList.contains(ALICE_SAME_ID));
        assertTrue(uniqueStudentList.contains(new StudentId("a0123456x")));
    }

    @Test
    public void add_nullStudent_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueStudentList.add(null));
    }

    @Test
    public void add_sameStudentId_throwsDuplicateStudentException() {
        uniqueStudentList.add(ALICE);
        assertThrows(DuplicateStudentException.class, () -> uniqueStudentList.add(ALICE_SAME_ID));
    }

    @Test
    public void add_sameNameDifferentId_success() {
        Student otherAlice = new Student(ALICE.getName(), new StudentId("A0999999Z"));
        uniqueStudentList.add(ALICE);
        uniqueStudentList.add(otherAlice);
        assertEquals(List.of(ALICE, otherAlice), uniqueStudentList.asUnmodifiableObservableList());
    }

    @Test
    public void find_existingAndMissingStudentId_correctResult() {
        uniqueStudentList.add(ALICE);
        assertEquals(Optional.of(ALICE), uniqueStudentList.find(new StudentId("a0123456x")));
        assertEquals(Optional.empty(), uniqueStudentList.find(BEN.getStudentId()));
    }

    @Test
    public void remove_studentDoesNotExist_throwsStudentNotFoundException() {
        assertThrows(StudentNotFoundException.class, () -> uniqueStudentList.remove(ALICE));
        assertThrows(StudentNotFoundException.class, () -> uniqueStudentList.remove(ALICE.getStudentId()));
    }

    @Test
    public void remove_existingStudentId_removesAndReturnsStudent() {
        uniqueStudentList.add(ALICE);
        uniqueStudentList.add(BEN);
        assertEquals(ALICE, uniqueStudentList.remove(new StudentId("a0123456x")));
        assertEquals(List.of(BEN), uniqueStudentList.asUnmodifiableObservableList());
    }

    @Test
    public void setStudents_listWithDuplicateStudentIds_throwsDuplicateStudentException() {
        List<Student> studentsWithSameId = List.of(ALICE, ALICE_SAME_ID);
        assertThrows(DuplicateStudentException.class, () -> uniqueStudentList.setStudents(studentsWithSameId));
    }

    @Test
    public void setStudents_listWithNull_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> uniqueStudentList.setStudents(null));
        List<Student> studentsWithNull = Arrays.asList(ALICE, null);
        assertThrows(NullPointerException.class, () -> uniqueStudentList.setStudents(studentsWithNull));
    }

    @Test
    public void setStudents_validList_replacesContents() {
        uniqueStudentList.add(ALICE);
        uniqueStudentList.setStudents(List.of(BEN));
        assertEquals(List.of(BEN), uniqueStudentList.asUnmodifiableObservableList());
    }

    @Test
    public void asUnmodifiableObservableList_modifyList_throwsUnsupportedOperationException() {
        assertThrows(UnsupportedOperationException.class, ()
            -> uniqueStudentList.asUnmodifiableObservableList().remove(0));
    }

    @Test
    public void iterator_remove_throwsUnsupportedOperationException() {
        uniqueStudentList.add(ALICE);
        Iterator<Student> iterator = uniqueStudentList.iterator();
        iterator.next();
        assertThrows(UnsupportedOperationException.class, iterator::remove);
    }

    @Test
    public void equals() {
        uniqueStudentList.add(ALICE);
        UniqueStudentList sameList = new UniqueStudentList();
        sameList.add(ALICE);

        assertTrue(uniqueStudentList.equals(uniqueStudentList));
        assertTrue(uniqueStudentList.equals(sameList));
        assertEquals(uniqueStudentList.hashCode(), sameList.hashCode());
        assertFalse(uniqueStudentList.equals(null));
        assertFalse(uniqueStudentList.equals(new UniqueStudentList()));

        // a different kind of unique list -> returns false
        assertFalse(new UniqueStudentList().equals(new UniqueGroupList()));
    }

    @Test
    public void toStringMethod() {
        uniqueStudentList.add(ALICE);
        assertEquals(uniqueStudentList.asUnmodifiableObservableList().toString(), uniqueStudentList.toString());
    }
}
