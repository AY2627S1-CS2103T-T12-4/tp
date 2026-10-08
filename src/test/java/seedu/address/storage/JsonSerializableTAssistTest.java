package seedu.address.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static seedu.address.testutil.Assert.assertThrows;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.IllegalValueException;
import seedu.address.commons.util.JsonUtil;
import seedu.address.model.TAssist;
import seedu.address.model.student.StudentId;
import seedu.address.testutil.GroupBuilder;
import seedu.address.testutil.TAssistBuilder;
import seedu.address.testutil.TypicalGroups;

public class JsonSerializableTAssistTest {

    private static final Path TEST_DATA_FOLDER = Paths.get("src", "test", "data", "JsonSerializableTAssistTest");
    private static final Path TYPICAL_TASSIST_FILE = TEST_DATA_FOLDER.resolve("typicalTAssist.json");
    private static final Path INVALID_STUDENT_FILE = TEST_DATA_FOLDER.resolve("invalidStudentTAssist.json");
    private static final Path DUPLICATE_GROUP_FILE = TEST_DATA_FOLDER.resolve("duplicateGroupTAssist.json");

    private static final List<JsonAdaptedGroup> TYPICAL_GROUPS = TypicalGroups.getTypicalGroups().stream()
            .map(JsonAdaptedGroup::new)
            .toList();

    @Test
    public void toModelType_typicalTAssistFile_success() throws Exception {
        JsonSerializableTAssist dataFromFile = readFile(TYPICAL_TASSIST_FILE);
        TAssist expected = new TAssistBuilder(TypicalGroups.getTypicalTAssist())
                .withActiveGroup(TypicalGroups.NAME_T02).build();
        assertEquals(expected, dataFromFile.toModelType());
    }

    @Test
    public void toModelType_invalidStudentFile_throwsIllegalValueException() throws Exception {
        JsonSerializableTAssist dataFromFile = readFile(INVALID_STUDENT_FILE);
        assertThrows(IllegalValueException.class, StudentId.MESSAGE_CONSTRAINTS, dataFromFile::toModelType);
    }

    @Test
    public void toModelType_duplicateGroupsIgnoringCase_throwsIllegalValueException() throws Exception {
        JsonSerializableTAssist dataFromFile = readFile(DUPLICATE_GROUP_FILE);
        assertThrows(IllegalValueException.class,
                String.format(JsonSerializableTAssist.MESSAGE_DUPLICATE_GROUP, "t01"), dataFromFile::toModelType);
    }

    @Test
    public void toModelType_savedTAssist_returnsSameTAssist() throws Exception {
        TAssist original = new TAssistBuilder(TypicalGroups.getTypicalTAssist())
                .withActiveGroup(TypicalGroups.NAME_T01).build();
        assertEquals(original, new JsonSerializableTAssist(original).toModelType());
    }

    @Test
    public void toModelType_nullGroupsAndActiveGroup_returnsEmptyTAssist() throws Exception {
        assertEquals(new TAssist(), new JsonSerializableTAssist(null, null).toModelType());
    }

    @Test
    public void toModelType_activeGroupInDifferentCaseWithSpaces_setsActiveGroup() throws Exception {
        TAssist expected = new TAssistBuilder(TypicalGroups.getTypicalTAssist())
                .withActiveGroup(TypicalGroups.NAME_T03).build();
        assertEquals(expected, new JsonSerializableTAssist(" t03 ", TYPICAL_GROUPS).toModelType());
    }

    @Test
    public void toModelType_unknownActiveGroup_throwsIllegalValueException() {
        JsonSerializableTAssist data = new JsonSerializableTAssist("T04", TYPICAL_GROUPS);
        assertThrows(IllegalValueException.class,
                String.format(JsonSerializableTAssist.MESSAGE_UNKNOWN_ACTIVE_GROUP, "T04"), data::toModelType);
    }

    @Test
    public void toModelType_invalidActiveGroupName_throwsIllegalValueException() {
        JsonSerializableTAssist data = new JsonSerializableTAssist("", TYPICAL_GROUPS);
        assertThrows(IllegalValueException.class,
                String.format(JsonSerializableTAssist.MESSAGE_UNKNOWN_ACTIVE_GROUP, ""), data::toModelType);
    }

    @Test
    public void toModelType_nullGroup_throwsIllegalValueException() {
        List<JsonAdaptedGroup> groups = new ArrayList<>(TYPICAL_GROUPS);
        groups.add(null);
        JsonSerializableTAssist data = new JsonSerializableTAssist(null, groups);
        assertThrows(IllegalValueException.class, JsonSerializableTAssist.MESSAGE_EMPTY_GROUP, data::toModelType);
    }

    @Test
    public void toModelType_unknownFieldsInFile_ignored() throws Exception {
        String json = "{ \"version\" : 2, \"groups\" : [ { \"name\" : \"T01\", \"note\" : \"Mondays\" } ] }";
        JsonSerializableTAssist data = JsonUtil.fromJsonString(json, JsonSerializableTAssist.class);
        TAssist expected = new TAssistBuilder().withGroup(new GroupBuilder().withName("T01").build()).build();
        assertEquals(expected, data.toModelType());
    }

    private static JsonSerializableTAssist readFile(Path file) throws Exception {
        return JsonUtil.readJsonFile(file, JsonSerializableTAssist.class).orElseThrow();
    }
}
