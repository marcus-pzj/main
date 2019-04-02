package planmysem.logic.parser;

import static planmysem.common.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static planmysem.logic.parser.CommandParserTestUtil.assertParseFailure;
import static planmysem.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;

import planmysem.common.Clock;
import planmysem.logic.commands.DeleteCommand;

public class DeleteCommandParserTest {
    private DeleteCommandParser parser = new DeleteCommandParser();

    @Before
    public void setup() {
        Clock.set("2019-01-14T10:00:00Z");
    }

    @Test
    public void parse_validTags_success() {
        // single tag
        assertParseSuccess(parser,
                "t/CS2113T",
                new DeleteCommand(
                        new HashSet<>(Collections.singletonList("CS2113T")
                        )
                )
        );

        // multiple tags
        assertParseSuccess(parser,
                "t/CS2113T t/Tutorial t/Hard",
                new DeleteCommand(
                        new HashSet<>(Arrays.asList("CS2113T", "Tutorial", "Hard")
                        )
                )
        );
    }


    @Test
    public void parse_validIndex_success() {
        assertParseSuccess(parser,
                "1",
                new DeleteCommand(1)
        );

        assertParseSuccess(parser,
                "100",
                new DeleteCommand(100)
        );
    }

    @Test
    public void parse_noIndexNoTag_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE);

        assertParseFailure(parser,
                "nt/CS2113T nt/Tutorial nt/Hard",
                expectedMessage
        );

        assertParseFailure(parser,
                "",
                expectedMessage
        );
    }

    @Test
    public void parse_invalidIndex_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE);

        assertParseFailure(parser,
                "0",
                expectedMessage
        );

        // multiple tags
        assertParseFailure(parser,
                "-1",
                expectedMessage
        );
    }

}
