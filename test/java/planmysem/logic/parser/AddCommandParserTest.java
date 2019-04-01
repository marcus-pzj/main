package planmysem.logic.parser;

import static planmysem.common.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static planmysem.common.Messages.MESSAGE_INVALID_COMMAND_FORMAT_ADDITIONAL;
import static planmysem.common.Messages.MESSAGE_INVALID_DATE;
import static planmysem.common.Messages.MESSAGE_INVALID_TAG;
import static planmysem.common.Messages.MESSAGE_INVALID_TIME;
import static planmysem.logic.parser.CommandParserTestUtil.assertParseFailure;
import static planmysem.logic.parser.CommandParserTestUtil.assertParseSuccess;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.Before;
import org.junit.Test;
import planmysem.common.Clock;
import planmysem.logic.commands.AddCommand;
import planmysem.model.recurrence.Recurrence;
import planmysem.model.slot.Slot;

public class AddCommandParserTest {
    private AddCommandParser parser = new AddCommandParser();

    @Before
    public void setup() {
        Clock.set("2019-01-13T10:00:00Z");
    }

    @Test
    public void parse_minimalFields_success() {
        // Date represented in day format
        assertParseSuccess(parser,
                "n/CS2113T Tutorial d/mon st/08:00 et/09:00",
                new AddCommand(new Slot(
                        "CS2113T Tutorial",
                        null,
                        null,
                        LocalTime.of(8, 0),
                        LocalTime.of(9, 0),
                        null
                ), new Recurrence(
                        null,
                        1
                )));

        // Date represented in date format
        assertParseSuccess(parser,
                "n/CS2113T Tutorial d/14-01-2019 st/08:00 et/09:00",
                new AddCommand(new Slot(
                        "CS2113T Tutorial",
                        null,
                        null,
                        LocalTime.of(8, 0),
                        LocalTime.of(9, 0),
                        null
                )
                        , new Recurrence(
                        null,
                        LocalDate.of(2019, 1, 14)
                )));
    }

    @Test
    public void parse_optionalFieldsMissing_success() {
        assertParseSuccess(parser,
                "n/CS2113T Tutorial d/mon st/08:00 et/09:00 des/Topic: Sequence Diagram t/CS2113T t/Tutorial",
                new AddCommand(new Slot(
                        "CS2113T Tutorial",
                        null,
                        "Topic: Sequence Diagram",
                        LocalTime.of(8, 0),
                        LocalTime.of(9, 0),
                        new HashSet<>(Arrays.asList("CS2113T", "Tutorial"))
                )
                        , new Recurrence(
                        null,
                        1
                )));

        assertParseSuccess(parser,
                "n/CS2113T Tutorial d/mon st/08:00 et/09:00 des/Topic: Sequence Diagram",
                new AddCommand(new Slot(
                        "CS2113T Tutorial",
                        null,
                        "Topic: Sequence Diagram",
                        LocalTime.of(8, 0),
                        LocalTime.of(9, 0),
                        new HashSet<>()
                )
                        , new Recurrence(
                        null,
                        1
                )));

        assertParseSuccess(parser,
                "n/CS2113T Tutorial l/COM2 04-01 d/mon st/08:00 et/09:00 des/Topic: Sequence Diagram t/CS2113T t/Tutorial",
                new AddCommand(new Slot(
                        "CS2113T Tutorial",
                        "COM2 04-01",
                        "Topic: Sequence Diagram",
                        LocalTime.of(8, 0),
                        LocalTime.of(9, 0),
                        new HashSet<>(Arrays.asList("CS2113T", "Tutorial"))
                )
                        , new Recurrence(
                        null,
                        1
                )));

        assertParseSuccess(parser,
                "n/CS2113T Tutorial l/COM2 04-01 d/mon st/08:00 et/09:00 des/Topic: Sequence Diagram",
                new AddCommand(new Slot(
                        "CS2113T Tutorial",
                        "COM2 04-01",
                        "Topic: Sequence Diagram",
                        LocalTime.of(8, 0),
                        LocalTime.of(9, 0),
                        new HashSet<>()
                )
                        , new Recurrence(
                        null,
                        1
                )));

        assertParseSuccess(parser,
                "n/CS2113T Tutorial l/COM2 04-01 d/mon st/08:00 et/09:00 t/CS2113T t/Tutorial",
                new AddCommand(new Slot(
                        "CS2113T Tutorial",
                        "COM2 04-01",
                        null,
                        LocalTime.of(8, 0),
                        LocalTime.of(9, 0),
                        new HashSet<>(Arrays.asList("CS2113T", "Tutorial"))
                )
                        , new Recurrence(
                        null,
                        1
                )));

        assertParseSuccess(parser,
                "n/CS2113T Tutorial l/COM2 04-01 d/mon st/08:00 et/09:00",
                new AddCommand(new Slot(
                        "CS2113T Tutorial",
                        "COM2 04-01",
                        null,
                        LocalTime.of(8, 0),
                        LocalTime.of(9, 0),
                        new HashSet<>()
                )
                        , new Recurrence(
                        null,
                        1
                )));
    }

    @Test
    public void parse_AllFieldsMissing_success() {
        assertParseSuccess(parser,
                "n/CS2113T Tutorial d/mon st/08:00 et/09:00 des/Topic: Sequence Diagram t/CS2113T t/Tutorial r/normal",
                new AddCommand(new Slot(
                        "CS2113T Tutorial",
                        null,
                        "Topic: Sequence Diagram",
                        LocalTime.of(8, 0),
                        LocalTime.of(9, 0),
                        new HashSet<>(Arrays.asList("CS2113T", "Tutorial"))
                ),
                        new Recurrence(
                                new HashSet<>(Arrays.asList("normal")),
                                1
                        )));

        assertParseSuccess(parser,
                "n/CS2113T Tutorial d/mon st/08:00 et/09:00 des/Topic: Sequence Diagram r/exam",
                new AddCommand(new Slot(
                        "CS2113T Tutorial",
                        null,
                        "Topic: Sequence Diagram",
                        LocalTime.of(8, 0),
                        LocalTime.of(9, 0),
                        new HashSet<>()
                )
                        , new Recurrence(
                        new HashSet<>(Arrays.asList("exam")),
                        1
                )));

        assertParseSuccess(parser,
                "n/CS2113T Tutorial r/reading r/exam l/COM2 04-01 d/mon st/08:00 et/09:00 des/Topic: Sequence Diagram t/CS2113T t/Tutorial",
                new AddCommand(new Slot(
                        "CS2113T Tutorial",
                        "COM2 04-01",
                        "Topic: Sequence Diagram",
                        LocalTime.of(8, 0),
                        LocalTime.of(9, 0),
                        new HashSet<>(Arrays.asList("CS2113T", "Tutorial"))
                )
                        , new Recurrence(
                        new HashSet<>(Arrays.asList("reading", "exam")),
                        1
                )));

        assertParseSuccess(parser,
                "n/CS2113T Tutorial l/COM2 04-01 d/mon st/08:00 et/09:00 r/normal r/exam des/Topic: Sequence Diagram",
                new AddCommand(new Slot(
                        "CS2113T Tutorial",
                        "COM2 04-01",
                        "Topic: Sequence Diagram",
                        LocalTime.of(8, 0),
                        LocalTime.of(9, 0),
                        new HashSet<>()
                )
                        , new Recurrence(
                        new HashSet<>(Arrays.asList("normal", "exam")),
                        1
                )));

        assertParseSuccess(parser,
                "n/CS2113T Tutorial l/COM2 04-01 d/mon r/reading r/recess st/08:00 et/09:00 t/CS2113T t/Tutorial",
                new AddCommand(new Slot(
                        "CS2113T Tutorial",
                        "COM2 04-01",
                        null,
                        LocalTime.of(8, 0),
                        LocalTime.of(9, 0),
                        new HashSet<>(Arrays.asList("CS2113T", "Tutorial"))
                )
                        , new Recurrence(
                        new HashSet<>(Arrays.asList("reading", "recess")),
                        1
                )));

        assertParseSuccess(parser,
                "r/normal r/reading r/recess r/exam n/CS2113T Tutorial l/COM2 04-01 d/mon st/08:00 et/09:00",
                new AddCommand(new Slot(
                        "CS2113T Tutorial",
                        "COM2 04-01",
                        null,
                        LocalTime.of(8, 0),
                        LocalTime.of(9, 0),
                        new HashSet<>()
                )
                        , new Recurrence(
                        new HashSet<>(Arrays.asList("reading", "recess", "normal", "exam")),
                        1
                )));
    }

    @Test
    public void parse_compulsoryFieldMissing_failure() {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE);

        // missing name prefix
        assertParseFailure(parser,
                "add d/mon st/08:00 et/09:00",
                expectedMessage);

        // missing date prefix
        assertParseFailure(parser,
                "add n/CS2113T Tutorial st/08:00 et/09:00",
                expectedMessage);

        // missing start time prefix
        assertParseFailure(parser,
                "add n/CS2113T Tutorial d/mon et/09:00 ",
                expectedMessage);

        // missing end time prefix
        assertParseFailure(parser,
                "add n/CS2113T Tutorial d/mon st/08:00",
                expectedMessage);

        // all prefixes missing
        assertParseFailure(parser,
                "add",
                expectedMessage);
    }

    @Test
    public void parse_invalidDate_failure() {
        // invalid day
        assertParseFailure(parser,
                "add n/CS2113T Tutorial d/0 st/08:00 et/09:00 des/Topic: Sequence Diagram t/CS2113T t/Tutorial r/normal",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT_ADDITIONAL,
                        AddCommand.MESSAGE_USAGE, MESSAGE_INVALID_DATE));

        // invalid date
        assertParseFailure(parser,
                "add n/CS2113T Tutorial d/19999 st/08:00 et/09:00 des/Topic: Sequence Diagram t/CS2113T t/Tutorial r/normal",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT_ADDITIONAL,
                        AddCommand.MESSAGE_USAGE, MESSAGE_INVALID_DATE));
    }

    @Test
    public void parse_invalidTime_failure() {
        // invalid start time
        assertParseFailure(parser,
                "add n/CS2113T Tutorial d/mon st/25:00 et/09:00 des/Topic: Sequence Diagram t/CS2113T t/Tutorial r/normal",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT_ADDITIONAL,
                        AddCommand.MESSAGE_USAGE, MESSAGE_INVALID_TIME));

        // invalid end time
        assertParseFailure(parser,
                "add n/CS2113T Tutorial d/mon st/08:00 et/25:00 des/Topic: Sequence Diagram t/CS2113T t/Tutorial r/normal",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT_ADDITIONAL,
                        AddCommand.MESSAGE_USAGE, MESSAGE_INVALID_TIME));
        assertParseFailure(parser,
                "add n/CS2113T Tutorial d/mon st/08:00 et/13:00AM des/Topic: Sequence Diagram t/CS2113T t/Tutorial r/normal",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT_ADDITIONAL,
                        AddCommand.MESSAGE_USAGE, MESSAGE_INVALID_TIME));
    }

    @Test
    public void parse_invalidTag_failure() {
        // invalid start time
        assertParseFailure(parser,
                "add n/CS2113T Tutorial d/mon st/08:00 et/09:00 des/Topic: Sequence Diagram t/ t/Tutorial r/normal",
                String.format(MESSAGE_INVALID_COMMAND_FORMAT_ADDITIONAL,
                        AddCommand.MESSAGE_USAGE, MESSAGE_INVALID_TAG));
    }
}
