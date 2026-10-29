package seedu.address.logic.parser;

import java.util.List;

import seedu.address.logic.commands.ParticipationSetCommand;
import seedu.address.logic.parser.exceptions.ParseException;

/**
 * Parses a student ID, teaching week, and participation score into a set command.
 */
public class ParticipationSetCommandParser implements Parser<ParticipationSetCommand> {
    private static final Prefix PREFIX_ID = new Prefix("id/");
    private static final Prefix PREFIX_WEEK = new Prefix("w/");
    private static final Prefix PREFIX_SCORE = new Prefix("s/");

    @Override
    public ParticipationSetCommand parse(String arguments) throws ParseException {
        ArgumentMultimap values = new TAssistArgumentParser(List.of(PREFIX_ID, PREFIX_WEEK, PREFIX_SCORE),
                List.of(), List.of(PREFIX_ID, PREFIX_WEEK, PREFIX_SCORE))
                .parse(arguments, ParticipationSetCommand.MESSAGE_USAGE);
        return new ParticipationSetCommand(
                TAssistParserUtil.parseStudentId(values.getValue(PREFIX_ID).orElseThrow()),
                TAssistParserUtil.parseWeek(values.getValue(PREFIX_WEEK).orElseThrow()),
                TAssistParserUtil.parseParticipationScore(values.getValue(PREFIX_SCORE).orElseThrow()));
    }
}
