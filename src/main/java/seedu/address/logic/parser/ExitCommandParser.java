package seedu.address.logic.parser;

import java.util.List;

import seedu.address.logic.commands.ExitCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses an exit command, rejecting any parameters so that a mistyped command does not close the app.
 */
public class ExitCommandParser implements Parser<ExitCommand> {
    @Override
    public ExitCommand parse(String arguments) throws ParseException {
        new TAssistArgumentParser(List.of(), List.of()).parse(arguments);
        return new ExitCommand();
    }
}
