package seedu.address.commons.util;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.commons.exceptions.DataLoadingException;
import seedu.address.testutil.SerializableTestClass;
import seedu.address.testutil.TestUtil;

/**
 * Tests JSON Read and Write
 */
public class JsonUtilTest {

    private static final Path SERIALIZATION_FILE = TestUtil.getFilePathInSandboxFolder("serialize.json");

    @Test
    public void serializeObjectToJsonFile_noExceptionThrown() throws IOException {
        SerializableTestClass serializableTestClass = new SerializableTestClass();
        serializableTestClass.setTestValues();

        JsonUtil.serializeObjectToJsonFile(SERIALIZATION_FILE, serializableTestClass);

        assertEquals(FileUtil.readFromFile(SERIALIZATION_FILE), SerializableTestClass.JSON_STRING_REPRESENTATION);
    }

    @Test
    public void deserializeObjectFromJsonFile_noExceptionThrown() throws IOException {
        FileUtil.writeToFile(SERIALIZATION_FILE, SerializableTestClass.JSON_STRING_REPRESENTATION);

        SerializableTestClass serializableTestClass = JsonUtil
                .deserializeObjectFromJsonFile(SERIALIZATION_FILE, SerializableTestClass.class);

        assertEquals(serializableTestClass.getName(), SerializableTestClass.getNameTestValue());
        assertEquals(serializableTestClass.getListOfLocalDateTimes(), SerializableTestClass.getListTestValues());
        assertEquals(serializableTestClass.getMapOfIntegerToString(), SerializableTestClass.getHashMapTestValues());
    }

    @Test
    public void readJsonFile_nullContent_throwsDataLoadingException() throws IOException {
        FileUtil.writeToFile(SERIALIZATION_FILE, "null");

        DataLoadingException e = assertThrows(DataLoadingException.class, () ->
                JsonUtil.readJsonFile(SERIALIZATION_FILE, SerializableTestClass.class));
        assertEquals(JsonUtil.MESSAGE_NULL_CONTENT, e.getCause().getMessage());
    }

    @Test
    public void fromJsonString_trailingContent_throwsIoException() {
        for (String trailingContent : List.of("THIS IS INVALID JSON", "{}", "[]", "null", "42", "true",
                "\"extra\"", "{", "/* comment */")) {
            String json = SerializableTestClass.JSON_STRING_REPRESENTATION + "\n" + trailingContent;

            assertThrows(IOException.class, () -> JsonUtil.fromJsonString(json, SerializableTestClass.class),
                    trailingContent);
        }
    }

    @Test
    public void fromJsonString_trailingWhitespace_returnsObject() throws IOException {
        String json = SerializableTestClass.JSON_STRING_REPRESENTATION + " \t\r\n";

        SerializableTestClass result = JsonUtil.fromJsonString(json, SerializableTestClass.class);

        assertEquals(SerializableTestClass.getNameTestValue(), result.getName());
        assertEquals(SerializableTestClass.getListTestValues(), result.getListOfLocalDateTimes());
        assertEquals(SerializableTestClass.getHashMapTestValues(), result.getMapOfIntegerToString());
    }

    @Test
    public void readJsonFile_trailingContent_throwsDataLoadingException() throws IOException {
        for (String trailingContent : List.of("THIS IS INVALID JSON", "{}", "null")) {
            String json = SerializableTestClass.JSON_STRING_REPRESENTATION + "\n" + trailingContent;
            FileUtil.writeToFile(SERIALIZATION_FILE, json);

            assertThrows(DataLoadingException.class, () ->
                    JsonUtil.readJsonFile(SERIALIZATION_FILE, SerializableTestClass.class), trailingContent);
            assertEquals(json, FileUtil.readFromFile(SERIALIZATION_FILE));
        }
    }

    //TODO: @Test jsonUtil_readJsonStringToObjectInstance_correctObject()

    //TODO: @Test jsonUtil_writeThenReadObjectToJson_correctObject()
}
