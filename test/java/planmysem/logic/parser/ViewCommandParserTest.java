package planmysem.logic.parser;

import static planmysem.common.Messages.MESSAGE_ILLEGAL_WEEK_VALUE;
import static planmysem.common.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static planmysem.common.Messages.MESSAGE_INVALID_DATE;

import static planmysem.logic.parser.CommandParserTestUtil.assertParseFailure;
import static planmysem.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.Test;

import planmysem.logic.commands.ViewCommand;

public class ViewCommandParserTest {
    private ViewCommandParser parser = new ViewCommandParser();

    @Test
    public void parse_validMonthArgs_success() {
        assertParseSuccess(parser,
                "month",
                new ViewCommand(new String[]{"month"}));
    }

    @Test
    public void parse_validWeekArgs_success() {
        assertParseSuccess(parser,
                "week",
                new ViewCommand(new String[]{"week"}));

        assertParseSuccess(parser,
                "week 1",
                new ViewCommand(new String[]{"week", "1"}));

        assertParseSuccess(parser,
                "week 13",
                new ViewCommand(new String[]{"week", "13"}));

        assertParseSuccess(parser,
                "week exam",
                new ViewCommand(new String[]{"week", "Examination"}));

        assertParseSuccess(parser,
                "week recess",
                new ViewCommand(new String[]{"week", "Recess"}));

        assertParseSuccess(parser,
                "week reading",
                new ViewCommand(new String[]{"week", "Reading"}));

        assertParseSuccess(parser,
                "week orientation",
                new ViewCommand(new String[]{"week", "Orientation"}));

        assertParseSuccess(parser,
                "week details",
                new ViewCommand(new String[]{"week", "Details"}));

        assertParseSuccess(parser,
                "week exam details",
                new ViewCommand(new String[]{"week", "Examination", "Details"}));

        assertParseSuccess(parser,
                "week details exam",
                new ViewCommand(new String[]{"week", "Examination", "Details"}));

        assertParseSuccess(parser,
                "week recess details",
                new ViewCommand(new String[]{"week", "Recess", "Details"}));

        assertParseSuccess(parser,
                "week reading details",
                new ViewCommand(new String[]{"week", "Reading", "Details"}));

        assertParseSuccess(parser,
                "week orientation details",
                new ViewCommand(new String[]{"week", "Orientation", "Details"}));
    }

    @Test
    public void parse_validDayArgs_success() {
        assertParseSuccess(parser,
                "day",
                new ViewCommand(new String[]{"day"}));

        assertParseSuccess(parser,
                "day 1",
                new ViewCommand(new String[]{"day", "1"}));

        assertParseSuccess(parser,
                "day 7",
                new ViewCommand(new String[]{"day", "7"}));

        assertParseSuccess(parser,
                "day mon",
                new ViewCommand(new String[]{"day", "mon"}));

        assertParseSuccess(parser,
                "day sun",
                new ViewCommand(new String[]{"day", "sun"}));

        assertParseSuccess(parser,
                "day Monday",
                new ViewCommand(new String[]{"day", "Monday"}));

        assertParseSuccess(parser,
                "day Sunday",
                new ViewCommand(new String[]{"day", "Sunday"}));
    }

    @Test
    public void parse_invalidArgs_failure() {
        assertParseFailure(parser,
                "",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewCommand.MESSAGE_USAGE));

        assertParseFailure(parser,
                "month 1",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewCommand.MESSAGE_USAGE));

        assertParseFailure(parser,
                "test test test test",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, ViewCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidWeekArgs_failure() {
        assertParseFailure(parser,
                "week -1",
                String.format(MESSAGE_ILLEGAL_WEEK_VALUE, ViewCommand.MESSAGE_USAGE));

        assertParseFailure(parser,
                "week 14",
                String.format(MESSAGE_ILLEGAL_WEEK_VALUE, ViewCommand.MESSAGE_USAGE));

        assertParseFailure(parser,
                "week something",
                String.format(MESSAGE_ILLEGAL_WEEK_VALUE, ViewCommand.MESSAGE_USAGE));

        assertParseFailure(parser,
                "week detail detail",
                String.format(MESSAGE_ILLEGAL_WEEK_VALUE, ViewCommand.MESSAGE_USAGE));
    }

    @Test
    public void parse_invalidDayArgs_failure() {
        assertParseFailure(parser,
                "day 0",
                String.format(MESSAGE_INVALID_DATE, ViewCommand.MESSAGE_USAGE));

        assertParseFailure(parser,
                "day 32",
                String.format(MESSAGE_INVALID_DATE, ViewCommand.MESSAGE_USAGE));

        assertParseFailure(parser,
                "day test",
                String.format(MESSAGE_INVALID_DATE, ViewCommand.MESSAGE_USAGE));
    }

}
