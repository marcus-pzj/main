package planmysem.logic.parser;

import static planmysem.common.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static planmysem.logic.parser.CommandParserTestUtil.assertParseFailure;
import static planmysem.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.Before;
import org.junit.Test;

import planmysem.common.Clock;
import planmysem.logic.commands.ExportCommand;

public class ExportCommandParserTest {
    private ExportCommandParser parser = new ExportCommandParser();

    @Before
    public void setup() {
        Clock.set("2019-01-14T10:00:00Z");
    }

    @Test
        public void parse_success() {
        assertParseSuccess(parser,
                "fn/test",
                new ExportCommand("test"));

        //default is PlanMySem
        assertParseSuccess(parser, "", new ExportCommand("PlanMySem"));
    }

    @Test
    public void parse_wrongParameter_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ExportCommand.MESSAGE_USAGE);

        assertParseFailure(parser,
                "n/WrongPara",
                expectedMessage
        );
    }
}
