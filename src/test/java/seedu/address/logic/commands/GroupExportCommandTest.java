package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandFailure;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;
import static seedu.address.testutil.Assert.assertThrows;
import static seedu.address.testutil.TypicalGroups.NAME_T01;
import static seedu.address.testutil.TypicalGroups.NAME_T02;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import seedu.address.logic.Messages;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.model.UserPrefs;
import seedu.address.model.group.GroupName;
import seedu.address.storage.GroupRecordsCsvExporter;
import seedu.address.testutil.TAssistBuilder;
import seedu.address.testutil.TypicalGroups;

public class GroupExportCommandTest {
    @TempDir
    public Path temporaryFolder;

    @Test
    public void execute_activeGroup_writesCsvAndReportsWhereWithoutChangingData() {
        GroupRecordsCsvExporter exporter = new GroupRecordsCsvExporter(temporaryFolder);
        Model model = modelWithActiveGroup(NAME_T01);
        Path exportFile = exporter.getExportFile(NAME_T01);

        assertCommandSuccess(new GroupExportCommand(exporter), model,
                String.format(GroupExportCommand.MESSAGE_SUCCESS, "3 students", "T01", exportFile),
                modelWithActiveGroup(NAME_T01));
        assertTrue(Files.exists(exportFile));
    }

    @Test
    public void execute_groupWithOneStudent_usesSingular() {
        GroupRecordsCsvExporter exporter = new GroupRecordsCsvExporter(temporaryFolder);
        Model model = modelWithActiveGroup(NAME_T02);
        model.removeStudent(NAME_T02, TypicalGroups.getT02().getStudentList().getFirst().getStudentId());
        Model expectedModel = new ModelManager(model.getTAssist(), new UserPrefs());

        assertCommandSuccess(new GroupExportCommand(exporter), model, String.format(GroupExportCommand.MESSAGE_SUCCESS,
                "1 student", "T02", exporter.getExportFile(NAME_T02)), expectedModel);
    }

    @Test
    public void execute_noActiveGroup_throwsCommandExceptionAndWritesNothing() {
        Model model = new ModelManager(TypicalGroups.getTypicalTAssist(), new UserPrefs());

        assertCommandFailure(new GroupExportCommand(new GroupRecordsCsvExporter(temporaryFolder)), model,
                Messages.MESSAGE_NO_ACTIVE_GROUP);
        assertFalse(Files.exists(temporaryFolder.resolve("T01-records.csv")));
    }

    @Test
    public void execute_folderCannotBeWritten_throwsCommandExceptionNamingFile() throws IOException {
        Path notAFolder = Files.writeString(temporaryFolder.resolve("exports"), "not a folder");
        GroupRecordsCsvExporter exporter = new GroupRecordsCsvExporter(notAFolder);

        assertCommandFailure(new GroupExportCommand(exporter), modelWithActiveGroup(NAME_T01),
                String.format(GroupExportCommand.MESSAGE_EXPORT_FAILED, "T01", exporter.getExportFile(NAME_T01)));
    }

    @Test
    public void equals() {
        GroupExportCommand command = new GroupExportCommand(new GroupRecordsCsvExporter(temporaryFolder));

        assertTrue(command.equals(command));
        assertTrue(command.equals(new GroupExportCommand(new GroupRecordsCsvExporter(temporaryFolder))));
        assertEquals(command.hashCode(),
                new GroupExportCommand(new GroupRecordsCsvExporter(temporaryFolder)).hashCode());
        assertFalse(command.equals(null));
        assertFalse(command.equals(new GroupListCommand()));
        assertFalse(command.equals(new GroupExportCommand(
                new GroupRecordsCsvExporter(temporaryFolder.resolve("other")))));
    }

    @Test
    public void constructor_nullExporter_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new GroupExportCommand(null));
    }

    private static Model modelWithActiveGroup(GroupName activeGroup) {
        return new ModelManager(new TAssistBuilder(TypicalGroups.getTypicalTAssist()).withActiveGroup(activeGroup)
                .build(), new UserPrefs());
    }
}
