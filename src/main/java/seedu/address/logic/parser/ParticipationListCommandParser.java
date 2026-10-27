package seedu.address.logic.parser;

import java.util.List;

import seedu.address.logic.commands.ParticipationListCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses a teaching week into a participation listing command.
 */
public class ParticipationListCommandParser implements Parser<ParticipationListCommand> {
    private static final Prefix PREFIX_WEEK = new Prefix("w/");

    @Override
    public ParticipationListCommand parse(String arguments) throws ParseException {
        ArgumentMultimap values = new TAssistArgumentParser(List.of(PREFIX_WEEK), List.of(), List.of(PREFIX_WEEK))
                .parse(arguments);
        return new ParticipationListCommand(TAssistParserUtil.parseWeek(values.getValue(PREFIX_WEEK).orElseThrow()));
    }
}
