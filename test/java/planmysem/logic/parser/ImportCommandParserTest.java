package planmysem.logic.parser;

import static planmysem.common.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static planmysem.logic.parser.CommandParserTestUtil.assertParseFailure;
import static planmysem.logic.parser.CommandParserTestUtil.assertParseSuccess;

import org.junit.Before;
import org.junit.Test;

import planmysem.common.Clock;

import planmysem.logic.commands.ImportCommand;

public class ImportCommandParserTest {
    private ImportCommandParser parser = new ImportCommandParser();

    @Before
    public void setup() {
        Clock.set("2019-01-14T10:00:00Z");
    }

    @Test
    public void parse_success() {
        assertParseSuccess(parser,
                "fn/test",
                new ImportCommand("test"));
    }

    @Test
    public void parse_wrongParameter_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ImportCommand.MESSAGE_USAGE);

        assertParseFailure(parser,
                "n/WrongPara",
                expectedMessage
        );

        assertParseFailure(parser,
                "",
                expectedMessage
        );

        assertParseFailure(parser, "", expectedMessage);
    }
}
