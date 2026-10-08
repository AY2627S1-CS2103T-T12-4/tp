package seedu.address.logic.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.commands.CommandTestUtil.assertCommandSuccess;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.help.HelpCatalog;
import seedu.address.logic.help.HelpEntry;
import seedu.address.model.Model;
import seedu.address.model.ModelManager;
import seedu.address.testutil.HelpEntryBuilder;

public class HelpCommandTest {
    private final HelpEntry studentList = new HelpEntryBuilder().withCommandFormat("student list").build();
    private final HelpEntry groupAdd = new HelpEntryBuilder().withTopic("group").withCommandFormat("group add")
            .build();
    private final List<HelpEntry> entries = HelpCatalog.getEntries(List.of(groupAdd, studentList));

    private Model model = new ModelManager();
    private Model expectedModel = new ModelManager();

    @Test
    public void execute_noTopic_showsEveryEntryInResultAndOpensHelp() {
        CommandResult expectedCommandResult = new CommandResult(HelpCatalog.format(entries), true, false);
        assertCommandSuccess(new HelpCommand(entries), model, expectedCommandResult, expectedModel);

        String feedback = new HelpCommand(entries).execute(model).getFeedbackToUser();
        assertTrue(feedback.contains("Current commands"));
        assertTrue(feedback.contains("Group commands\ngroup add"));
        assertTrue(feedback.contains("Student commands\nstudent list"));
    }

    @Test
    public void execute_topic_showsOnlyThatTopic() {
        CommandResult expectedCommandResult = new CommandResult(HelpCatalog.format(List.of(studentList)),
                true, false);
        assertCommandSuccess(new HelpCommand(entries, "student"), model, expectedCommandResult, expectedModel);

        String feedback = new HelpCommand(entries, "student").execute(model).getFeedbackToUser();
        assertFalse(feedback.contains("Current commands"));
        assertFalse(feedback.contains("group add"));
    }

    @Test
    public void execute_topicWithoutRegisteredCommands_explainsNothingIsAvailable() {
        CommandResult expectedCommandResult = new CommandResult("No attendance commands are available yet.",
                true, false);
        assertCommandSuccess(new HelpCommand(entries, "attendance"), model, expectedCommandResult, expectedModel);
    }

    @Test
    public void constructor_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new HelpCommand(null));
        assertThrows(NullPointerException.class, () -> new HelpCommand(null, "student"));
        assertThrows(NullPointerException.class, () -> new HelpCommand(entries, null));
    }

    @Test
    public void equals() {
        HelpCommand command = new HelpCommand(entries, "student");

        assertTrue(command.equals(command));
        assertTrue(command.equals(new HelpCommand(entries, "student")));
        assertEquals(command.hashCode(), new HelpCommand(entries, "student").hashCode());
        assertEquals(new HelpCommand(entries), new HelpCommand(entries, ""));
        assertFalse(command.equals(null));
        assertFalse(command.equals(new ClearCommand()));
        assertFalse(command.equals(new HelpCommand(entries, "group")));
        assertFalse(command.equals(new HelpCommand(List.of(studentList), "student")));
    }

    @Test
    public void toString_containsTopic() {
        assertTrue(new HelpCommand(entries, "student").toString().contains("topic=student"));
    }

    @Test
    public void messages_listEveryTopic() {
        assertEquals("Unknown help topic. Available topics: group, student, attendance, participation, "
                + "assignment.", HelpCommand.MESSAGE_UNKNOWN_TOPIC);
        for (String topic : HelpCatalog.TOPICS) {
            assertTrue(HelpCommand.MESSAGE_USAGE.contains(topic));
        }
    }
}
