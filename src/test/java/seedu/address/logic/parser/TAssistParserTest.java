package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.help.HelpCatalog;
import seedu.address.logic.help.HelpEntry;
import seedu.address.logic.parser.exceptions.ParseException;
import seedu.address.model.ModelManager;
import seedu.address.testutil.HelpEntryBuilder;
import seedu.address.testutil.StubCommand;

public class TAssistParserTest {

    private final TAssistParser parser = new TAssistParser();

    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD + " 3") instanceof ExitCommand);
    }

    @Test
    public void parseCommand_help() throws Exception {
        List<HelpEntry> entries = parser.getHelpEntries();
        assertEquals(new HelpCommand(entries), parser.parseCommand(HelpCommand.COMMAND_WORD));
        assertEquals(new HelpCommand(entries, "student"), parser.parseCommand(HelpCommand.COMMAND_WORD + " student"));
        assertThrows(ParseException.class, HelpCommand.MESSAGE_UNKNOWN_TOPIC, () ->
                parser.parseCommand(HelpCommand.COMMAND_WORD + " 3"));
    }

    @Test
    public void getHelpEntries_includesRegisteredFeatureHelp() {
        HelpEntry groupAdd = new HelpEntryBuilder().withTopic("group").withCommandFormat("group add").build();
        CommandRegistry registry = new CommandRegistry();
        registry.register("group", "add", args -> new StubCommand(), groupAdd);

        assertEquals(HelpCatalog.getEntries(List.of(groupAdd)), new TAssistParser(registry).getHelpEntries());
        assertEquals(HelpCatalog.getEntries(List.of()), parser.getHelpEntries());
    }

    @Test
    public void parseCommand_unrecognisedInput_throwsParseException() {
        assertThrows(ParseException.class, String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE), ()
            -> parser.parseCommand(""));
    }

    @Test
    public void parseCommand_unknownCommand_throwsParseException() {
        assertThrows(ParseException.class, String.format(MESSAGE_UNKNOWN_COMMAND, "unknownCommand"), () ->
                parser.parseCommand("unknownCommand"));
    }

    @Test
    public void parseCommand_registeredFeatures_dispatchesWithoutChangingLegacyCommands() throws Exception {
        StubCommand groupList = new StubCommand();
        StubCommand studentAdd = new StubCommand();
        CommandRegistry registry = new CommandRegistry();
        registry.register("group", "list", args -> groupList);
        registry.register("student", "add", args -> studentAdd);
        TAssistParser featureParser = new TAssistParser(registry);

        assertSame(groupList, featureParser.parseCommand("  group\t list  "));
        assertSame(studentAdd, featureParser.parseCommand("student\n add Alice Tan"));
        assertTrue(featureParser.parseCommand("exit extra") instanceof ExitCommand);
        assertEquals(parser.parseCommand("view attendance").execute(new ModelManager()),
                featureParser.parseCommand("view attendance").execute(new ModelManager()));
    }

    @Test
    public void constructor_snapshotOfRegistrations_canBeReusedAndDoesNotChange() throws Exception {
        StubCommand groupList = new StubCommand();
        StubCommand groupAdd = new StubCommand();
        CommandRegistry registry = new CommandRegistry();
        registry.register("group", "list", args -> groupList);
        TAssistParser firstParser = new TAssistParser(registry);
        TAssistParser secondParser = new TAssistParser(registry);
        registry.register("group", "add", args -> groupAdd);

        assertSame(groupList, firstParser.parseCommand("group list"));
        assertSame(groupList, secondParser.parseCommand("group list"));
        assertThrows(ParseException.class, () -> firstParser.parseCommand("group add"));
        assertSame(groupAdd, new TAssistParser(registry).parseCommand("group add"));
    }

    @Test
    public void constructor_legacyKeywordConflict_throwsIllegalArgumentException() {
        for (String legacyKeyword : List.of("view", "help", "exit")) {
            CommandRegistry registry = new CommandRegistry();
            registry.register(legacyKeyword, "student", args -> new StubCommand());
            assertThrows(IllegalArgumentException.class, () -> new TAssistParser(registry));
        }
    }

    @Test
    public void nullInputs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new TAssistParser(null));
        assertThrows(NullPointerException.class, () -> parser.parseCommand(null));
    }
}
