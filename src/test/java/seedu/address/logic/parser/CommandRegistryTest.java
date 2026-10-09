package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.testutil.Assert.assertThrows;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.Command;
import seedu.address.logic.help.HelpEntry;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.testutil.HelpEntryBuilder;
import seedu.address.testutil.StubCommand;

public class CommandRegistryTest {
    private final CommandRegistry registry = new CommandRegistry();

    @Test
    public void register_independentFeatures_dispatchesToOwnParsers() throws Exception {
        Command groupCommand = new StubCommand();
        Command studentCommand = new StubCommand();
        registry.register("group", "list", args -> groupCommand);
        registry.register("student", "list", args -> studentCommand);

        assertSame(groupCommand, registry.parseCommand("group", "list"));
        assertSame(studentCommand, registry.parseCommand("student", "list"));
        assertTrue(registry.hasFeature("group"));
        assertFalse(registry.hasFeature("attendance"));
    }

    @Test
    public void register_duplicate_throwsAndKeepsFirstParser() throws Exception {
        Command originalCommand = new StubCommand();
        registry.register("group", "list", args -> originalCommand);
        assertThrows(IllegalArgumentException.class, "Command already registered: group list", () ->
                registry.register("group", "list", args -> new StubCommand()));
        assertSame(originalCommand, registry.parseCommand("group", "list"));
    }

    @Test
    public void register_invalidKeywords_throwsWithoutRegisteringFeature() {
        for (String keyword : new String[] {"", "Group", "group add", " group", "group/", "group1"}) {
            assertThrows(IllegalArgumentException.class, () -> registry.register(keyword, "list", args -> null));
            assertThrows(IllegalArgumentException.class, () -> registry.register("group", keyword, args -> null));
        }
        assertFalse(registry.hasFeature("group"));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void parseCommand_featureWithoutSubcommands_throwsAssertionError() throws Exception {
        Field featureParsers = CommandRegistry.class.getDeclaredField("featureParsers");
        featureParsers.setAccessible(true);
        Map<String, Object> registeredFeatures = (Map<String, Object>) featureParsers.get(registry);
        registeredFeatures.put("group", new TreeMap<String, Object>());

        assertThrows(AssertionError.class, () -> registry.parseCommand("group", "list"));
    }

    @Test
    public void register_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> registry.register(null, "list", args -> null));
        assertThrows(NullPointerException.class, () -> registry.register("group", null, args -> null));
        assertThrows(NullPointerException.class, () -> registry.register("group", "list", null));
        assertThrows(NullPointerException.class, () -> registry.register("group", "list", args -> null, null));
    }

    @Test
    public void register_helpEntries_areOrderedAndImmutable() {
        HelpEntry studentList = helpEntry("student", "student list");
        HelpEntry studentAdd = helpEntry("student", "student add");
        HelpEntry groupList = helpEntry("group", "group list");
        registry.register("student", "list", args -> new StubCommand(), studentList);
        registry.register("student", "add", args -> new StubCommand(), studentAdd);
        registry.register("group", "list", args -> new StubCommand(), groupList);
        registry.register("group", "remove", args -> new StubCommand());

        assertEquals(List.of(groupList, studentAdd, studentList), registry.getHelpEntries());
        assertThrows(UnsupportedOperationException.class, () -> registry.getHelpEntries().clear());
    }

    @Test
    public void register_helpTopicDoesNotMatchFeature_throws() {
        HelpEntry entry = helpEntry("student", "student list");

        assertThrows(IllegalArgumentException.class, "Help topic must match the feature keyword.", () ->
                registry.register("group", "list", args -> new StubCommand(), entry));
        assertFalse(registry.hasFeature("group"));
        assertTrue(registry.getHelpEntries().isEmpty());
    }

    @Test
    public void copyConstructor_copiesParsersAndHelpEntriesIndependently() throws Exception {
        HelpEntry originalEntry = helpEntry("student", "student list");
        registry.register("student", "list", args -> new StubCommand(), originalEntry);

        CommandRegistry copy = new CommandRegistry(registry);
        registry.register("student", "add", args -> new StubCommand(), helpEntry("student", "student add"));

        assertEquals(List.of(originalEntry), copy.getHelpEntries());
        assertEquals(2, registry.getHelpEntries().size());
        assertTrue(copy.parseCommand("student", "list") instanceof StubCommand);
    }

    @Test
    public void parseCommand_whitespace_preservesArgumentValues() throws Exception {
        registry.register("student", "add", args -> {
            assertEquals("n/Alice   Tan\t id/a123", args);
            return new StubCommand();
        });
        registry.parseCommand("student", " \tadd \n n/Alice   Tan\t id/a123  ");
    }

    @Test
    public void parseCommand_noArguments_passesEmptyString() throws Exception {
        registry.register("group", "list", args -> {
            assertEquals("", args);
            return new StubCommand();
        });
        registry.parseCommand("group", "list");
    }

    @Test
    public void parseCommand_unknownFeature_throwsSpecificError() {
        assertThrows(ParseException.class, String.format(MESSAGE_UNKNOWN_COMMAND, "unknown"), () ->
                registry.parseCommand("unknown", "list"));
    }

    @Test
    public void parseCommand_missingOrUnknownSubcommand_listsValidSubcommands() {
        registry.register("group", "list", args -> new StubCommand());
        registry.register("group", "add", args -> new StubCommand());
        String missingMessage = String.format(CommandRegistry.MESSAGE_MISSING_SUBCOMMAND,
                "group", "add, list", "group");
        assertThrows(ParseException.class, missingMessage, () -> registry.parseCommand("group", " \t "));
        for (String subcommand : new String[] {"unknown", "LIST", "n/T01"}) {
            String unknownMessage = String.format(CommandRegistry.MESSAGE_UNKNOWN_SUBCOMMAND,
                    subcommand, "group", "add, list");
            assertThrows(ParseException.class, unknownMessage, () -> registry.parseCommand("group", subcommand));
        }
    }

    @Test
    public void parseCommand_invalidArguments_propagatesFeatureError() {
        registry.register("group", "add", args -> {
            throw new ParseException("Invalid group name.");
        });
        assertThrows(ParseException.class, "Invalid group name.", () -> registry.parseCommand("group", "add n/"));
    }

    @Test
    public void parseCommand_null_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> registry.parseCommand(null, "list"));
        assertThrows(NullPointerException.class, () -> registry.parseCommand("group", null));
        assertThrows(NullPointerException.class, () -> registry.hasFeature(null));
    }

    private HelpEntry helpEntry(String topic, String commandFormat) {
        return new HelpEntryBuilder().withTopic(topic).withCommandFormat(commandFormat).build();
    }
}
