package planmysem.logic.parser;

import static junit.framework.TestCase.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Arrays;
import java.util.HashSet;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import planmysem.logic.commands.AddCommand;
import planmysem.logic.commands.ClearCommand;
import planmysem.logic.commands.DeleteCommand;
import planmysem.logic.commands.EditCommand;
import planmysem.logic.commands.ExitCommand;
import planmysem.logic.commands.ExportCommand;
import planmysem.logic.commands.HelpCommand;
import planmysem.logic.commands.ImportCommand;
import planmysem.logic.commands.ListCommand;
import planmysem.model.recurrence.Recurrence;
import planmysem.model.slot.Slot;

public class ParserManagerTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    private final ParserManager parser = new ParserManager();

    @Test
    public void parseCommand_add_via_day() throws Exception {
        AddCommand command = (AddCommand) parser.parseCommand(
                AddCommand.COMMAND_WORD + " " + "n/CS2113T Tutorial d/mon st/08:00 et/09:00");
        assertEquals(new AddCommand(new Slot(
                "CS2113T Tutorial",
                null,
                null,
                LocalTime.of(8, 0),
                LocalTime.of(9, 0),
                null
        )
                , new Recurrence(
                null,
                1
        )), command);

        AddCommand commandShort = (AddCommand) parser.parseCommand(
                AddCommand.COMMAND_WORD_SHORT + " " + "n/CS2113T Tutorial d/mon st/08:00 et/09:00");
        assertEquals(new AddCommand(new Slot(
                "CS2113T Tutorial",
                null,
                null,
                LocalTime.of(8, 0),
                LocalTime.of(9, 0),
                null
        )
                , new Recurrence(
                null,
                1
        )), commandShort);
    }

    @Test
    public void parseCommand_add_via_date() throws Exception {
        AddCommand command = (AddCommand) parser.parseCommand(
                AddCommand.COMMAND_WORD + " " + "n/CS2113T Tutorial d/21-01-2019 st/08:00 et/09:00");
        assertEquals(new AddCommand(new Slot(
                "CS2113T Tutorial",
                null,
                null,
                LocalTime.of(8, 0),
                LocalTime.of(9, 0),
                null
        )
                , new Recurrence(
                null,
                LocalDate.of(2019, 1, 21)
        )), command);
    }

    @Test
    public void parseCommand_delete_via_index() throws Exception {
        DeleteCommand command = (DeleteCommand) parser.parseCommand(
                DeleteCommand.COMMAND_WORD + " 1");
        assertEquals(new DeleteCommand(1), command);

        DeleteCommand commandAlt = (DeleteCommand) parser.parseCommand(
                DeleteCommand.COMMAND_WORD_ALT + " 1");
        assertEquals(new DeleteCommand(1), commandAlt);

        DeleteCommand commandShort = (DeleteCommand) parser.parseCommand(
                DeleteCommand.COMMAND_WORD_SHORT + " 1");
        assertEquals(new DeleteCommand(1), commandShort);


    }

    @Test
    public void parseCommand_delete_via_tags() throws Exception {
        DeleteCommand command = (DeleteCommand) parser.parseCommand(
                DeleteCommand.COMMAND_WORD + " t/CS2113T t/Tutorial");
        assertEquals(new DeleteCommand(new HashSet<>(Arrays.asList("CS2113T", "Tutorial"))), command);
    }

    @Test
    public void parseCommand_edit_via_index() throws Exception {
        EditCommand command = (EditCommand) parser.parseCommand(
                EditCommand.COMMAND_WORD + " " + "1 nl/COM2 04-01");
        assertEquals(new EditCommand(
                1,
                null,
                null,
                null,
                -1,
                "COM2 04-01",
                null,
                new HashSet<>()
        ), command);

        EditCommand commandShort = (EditCommand) parser.parseCommand(
                EditCommand.COMMAND_WORD_SHORT + " " + "1 nl/COM2 04-01");
        assertEquals(new EditCommand(
                1,
                null,
                null,
                null,
                -1,
                "COM2 04-01",
                null,
                new HashSet<>()
        ), commandShort);
    }

    @Test
    public void parseCommand_edit_via_tags() throws Exception {
        EditCommand command = (EditCommand) parser.parseCommand(
                EditCommand.COMMAND_WORD + " " + "t/CS2113T nl/COM2 04-01");
        assertEquals(new EditCommand(
                null,
                null,
                -1,
                "COM2 04-01",
                null,
                new HashSet<>(Arrays.asList("CS2113T")),
                new HashSet<>()
        ), command);
    }


    @Test
    public void parseCommand_help() throws Exception {
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD) instanceof HelpCommand);
        assertTrue(parser.parseCommand(HelpCommand.COMMAND_WORD + " 3") instanceof HelpCommand);
    }

    @Test
    public void parseCommand_clear() throws Exception {
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD) instanceof ClearCommand);
        assertTrue(parser.parseCommand(ClearCommand.COMMAND_WORD + " 3") instanceof ClearCommand);
    }

    @Test
    public void parseCommand_exit() throws Exception {
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD) instanceof ExitCommand);
        assertTrue(parser.parseCommand(ExitCommand.COMMAND_WORD + " 3") instanceof ExitCommand);
    }

    @Test
    public void parseCommand_export() throws Exception {
        assertTrue(parser.parseCommand(ExportCommand.COMMAND_WORD) instanceof ExportCommand);
        assertTrue(parser.parseCommand(ExportCommand.COMMAND_WORD + " 3") instanceof ExportCommand);
    }

    @Test
    public void parseCommand_import() throws Exception {
        assertTrue(parser.parseCommand(ImportCommand.COMMAND_WORD + " /test/data/ImportExportTest/importTest.ics\"") instanceof ImportCommand);
    }
//
//    @Test
//    public void parseCommand_find() throws Exception {
//        List<String> keywords = Arrays.asList("foo", "bar", "baz");
//        FindCommand command = (FindCommand) parser.parseCommand(
//                FindCommand.COMMAND_WORD + " " + keywords.stream().collect(Collectors.joining(" ")));
//        assertEquals(new FindCommand(new NameContainsKeywordsPredicate(keywords)), command);
//    }
//

//
//    @Test
//    public void parseCommand_history() throws Exception {
//        assertTrue(parser.parseCommand(HistoryCommand.COMMAND_WORD) instanceof HistoryCommand);
//        assertTrue(parser.parseCommand(HistoryCommand.COMMAND_WORD + " 3") instanceof HistoryCommand);
//
//        try {
//            parser.parseCommand("histories");
//            throw new AssertionError("The expected ParseException was not thrown.");
//        } catch (ParseException pe) {
//            assertEquals(MESSAGE_UNKNOWN_COMMAND, pe.getMessage());
//        }
//    }
//
//    @Test
//    public void parseCommand_list() throws Exception {
//        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD) instanceof ListCommand);
//        assertTrue(parser.parseCommand(ListCommand.COMMAND_WORD + " 3") instanceof ListCommand);
//    }
//
//    @Test
//    public void parseCommand_select() throws Exception {
//        SelectCommand command = (SelectCommand) parser.parseCommand(
//                SelectCommand.COMMAND_WORD + " " + INDEX_FIRST_PERSON.getOneBased());
//        assertEquals(new SelectCommand(INDEX_FIRST_PERSON), command);
//    }
//
//    @Test
//    public void parseCommand_redoCommandWord_returnsRedoCommand() throws Exception {
//        assertTrue(parser.parseCommand(RedoCommand.COMMAND_WORD) instanceof RedoCommand);
//        assertTrue(parser.parseCommand("redo 1") instanceof RedoCommand);
//    }
//
//    @Test
//    public void parseCommand_undoCommandWord_returnsUndoCommand() throws Exception {
//        assertTrue(parser.parseCommand(UndoCommand.COMMAND_WORD) instanceof UndoCommand);
//        assertTrue(parser.parseCommand("undo 3") instanceof UndoCommand);
//    }
//
//    @Test
//    public void parseCommand_unrecognisedInput_throwsParseException() throws Exception {
//        thrown.expect(ParseException.class);
//        thrown.expectMessage(String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
//        parser.parseCommand("");
//    }
//
//    @Test
//    public void parseCommand_unknownCommand_throwsParseException() throws Exception {
//        thrown.expect(ParseException.class);
//        thrown.expectMessage(MESSAGE_UNKNOWN_COMMAND);
//        parser.parseCommand("unknownCommand");
//    }
}
