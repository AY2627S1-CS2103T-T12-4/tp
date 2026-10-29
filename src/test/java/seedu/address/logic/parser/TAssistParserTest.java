package seedu.address.logic.parser;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static seedu.address.logic.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static seedu.address.logic.Messages.MESSAGE_UNKNOWN_COMMAND;
import static seedu.address.testutil.Assert.assertThrows;

import java.util.List;
import java.util.Locale;

import org.junit.jupiter.api.Test;

import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.commands.HelpCommand;
import seedu.address.logic.commands.RedoCommand;
import seedu.address.logic.commands.UndoCommand;
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
        assertThrows(ParseException.class, TAssistArgumentParser.MESSAGE_NO_PARAMETERS, () ->
                parser.parseCommand(ExitCommand.COMMAND_WORD + " unexpected"));
    }

    @Test
    public void replaceUnicodeSpaces_spaceCharacters_becomeNormalSpaces() {
        assertEquals("group add g/T04 ", TAssistParser.replaceUnicodeSpaces("group\u00A0add\u2003g/T04\u3000"));
        assertEquals("n/Ann Lee\tid/A1\n", TAssistParser.replaceUnicodeSpaces("n/Ann\u202FLee\tid/A1\n"));
    }

    @Test
    public void parseCommand_unicodeSpaces_separateWordsAndAreTrimmed() throws Exception {
        StubCommand groupAdd = new StubCommand();
        CommandRegistry registry = new CommandRegistry();
        registry.register("group", "add", args -> {
            assertEquals("g/T04", args.trim());
            return groupAdd;
        });
        TAssistParser featureParser = new TAssistParser(registry);

        assertSame(groupAdd, featureParser.parseCommand("\u00A0group\u00A0add\u00A0g/T04\u00A0"));
        assertEquals(new UndoCommand(), parser.parseCommand("undo\u2003"));
    }

    @Test
    public void parseCommand_undoAndRedo() throws Exception {
        assertEquals(new UndoCommand(), parser.parseCommand("  " + UndoCommand.COMMAND_WORD + "  "));
        assertEquals(new RedoCommand(), parser.parseCommand(RedoCommand.COMMAND_WORD));
        for (String input : List.of("undo 3", "redo all", "undo id/A0123456X", "UNDO 3", "ReDo all", "EXIT now")) {
            assertThrows(ParseException.class, TAssistArgumentParser.MESSAGE_NO_PARAMETERS, () ->
                    parser.parseCommand(input));
        }
        assertEquals(new UndoCommand(), parser.parseCommand("UNDO"));
        assertEquals(new RedoCommand(), parser.parseCommand("ReDo"));
    }

    @Test
    public void parseCommand_uppercaseBuiltIns_returnsSameCommand() throws Exception {
        assertTrue(parser.parseCommand("ExIt") instanceof ExitCommand);
        for (String input : List.of("help", "help student", "view students", "undo", "redo")) {
            assertEquals(parser.parseCommand(input).execute(new ModelManager()),
                    parser.parseCommand(input.toUpperCase(Locale.ROOT)).execute(new ModelManager()));
        }
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
    public void parseCommand_registeredFeatures_dispatchesWithoutChangingBuiltInCommands() throws Exception {
        StubCommand groupList = new StubCommand();
        StubCommand studentAdd = new StubCommand();
        CommandRegistry registry = new CommandRegistry();
        registry.register("group", "list", args -> groupList);
        registry.register("student", "add", args -> studentAdd);
        TAssistParser featureParser = new TAssistParser(registry);

        assertSame(groupList, featureParser.parseCommand("  group\t list  "));
        assertSame(studentAdd, featureParser.parseCommand("student\n add Alice Tan"));
        assertSame(groupList, featureParser.parseCommand("  GROUP\t LiSt  "));
        assertSame(studentAdd, featureParser.parseCommand("StuDent\n ADD Alice Tan"));
        assertTrue(featureParser.parseCommand("exit") instanceof ExitCommand);
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
    public void constructor_builtInKeywordConflict_throwsIllegalArgumentException() {
        for (String builtInKeyword : List.of("view", "help", "undo", "redo", "exit")) {
            CommandRegistry registry = new CommandRegistry();
            registry.register(builtInKeyword, "student", args -> new StubCommand());
            assertThrows(IllegalArgumentException.class, () -> new TAssistParser(registry));
        }
    }

    @Test
    public void nullInputs_throwsNullPointerException() {
        assertThrows(NullPointerException.class, () -> new TAssistParser(null));
        assertThrows(NullPointerException.class, () -> parser.parseCommand(null));
    }
}
