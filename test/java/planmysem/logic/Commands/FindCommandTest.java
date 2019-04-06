package planmysem.logic.Commands;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static planmysem.common.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static planmysem.common.Messages.MESSAGE_INVALID_MULTIPLE_PARAMS;
import static planmysem.logic.commands.ListCommand.MESSAGE_SUCCESS;
import static planmysem.logic.commands.ListCommand.MESSAGE_SUCCESS_NONE;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;
import java.util.TreeMap;

import javafx.util.Pair;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import planmysem.common.Clock;
import planmysem.common.Messages;
import planmysem.common.Utils;
import planmysem.logic.CommandHistory;
import planmysem.logic.commands.Command;
import planmysem.logic.commands.CommandResult;
import planmysem.logic.commands.FindCommand;
import planmysem.logic.parser.FindCommandParser;
import planmysem.logic.parser.ParserManager;
import planmysem.logic.parser.exceptions.ParseException;
import planmysem.model.Model;
import planmysem.model.ModelManager;
import planmysem.model.semester.Day;
import planmysem.model.semester.ReadOnlyDay;
import planmysem.model.semester.WeightedName;
import planmysem.model.slot.ReadOnlySlot;
import planmysem.model.slot.Slot;
import planmysem.testutil.SlotBuilder;

public class FindCommandTest {
    private Model model;
    private Model expectedModel;
    private Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> pair1;
    private Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> pair2;
    private Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> pair3;
    private Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> pair4;
    private CommandHistory commandHistory = new CommandHistory();

    private SlotBuilder slotBuilder = new SlotBuilder();

    private static final String LONG_STRING = "Lorem ipsum dolor sit amet, consectetur adipiscing elit, " +
            "sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. " +
            "Ut enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip " +
            "ex ea commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore " +
            "eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa qui " +
            "officia deserunt mollit anim id est laborum";

    private static final CommandHistory EMPTY_COMMAND_HISTORY = new CommandHistory();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setup() throws Exception {
        Clock.set("2019-01-14T10:00:00Z");
        // Create typical planner
        model = new ModelManager();
        pair1 = new Pair<>(
                LocalDate.of(2019, 02, 01),
                new Pair<>(
                        new Day(
                                DayOfWeek.FRIDAY,
                                "Week 3"
                        ),
                        slotBuilder.generateSlot(1)
                )
        );
        pair2 = new Pair<>(
                LocalDate.of(2019, 02, 02),
                new Pair<>(
                        new Day(
                                DayOfWeek.SATURDAY,
                                "Week 3"
                        ),
                        slotBuilder.generateSlot(2)
                )
        );
        pair3 = new Pair<>(
                LocalDate.of(2019, 02, 03),
                new Pair<>(
                        new Day(
                                DayOfWeek.SUNDAY,
                                "Week 3"
                        ),
                        slotBuilder.generateSlot(3)
                )
        );
        pair4 = new Pair<>(
                LocalDate.of(2019, 02, 04),
                new Pair<>(
                        new Day(
                                DayOfWeek.MONDAY,
                                "Week 4"
                        ),
                        slotBuilder.generateSlot(3)
                )
        );
        model.addSlot(LocalDate.of(2019, 02, 01), slotBuilder.generateSlot(1));
        model.addSlot(LocalDate.of(2019, 02, 02), slotBuilder.generateSlot(2));
        model.addSlot(LocalDate.of(2019, 02, 03), slotBuilder.generateSlot(3));
        model.addSlot(LocalDate.of(2019, 02, 04), slotBuilder.generateSlot(3));

        Map<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> list = new TreeMap<>();
        list.put(pair4.getKey(), pair4.getValue());
        list.put(pair3.getKey(), pair3.getValue());
        list.put(pair2.getKey(), pair2.getValue());
        list.put(pair1.getKey(), pair1.getValue());
        model.setLastShownList(list);

        expectedModel = new ModelManager();
        expectedModel.addSlot(LocalDate.of(2019, 02, 01), slotBuilder.generateSlot(1));
        expectedModel.addSlot(LocalDate.of(2019, 02, 02), slotBuilder.generateSlot(2));
        expectedModel.addSlot(LocalDate.of(2019, 02, 03), slotBuilder.generateSlot(3));
        expectedModel.addSlot(LocalDate.of(2019, 02, 04), slotBuilder.generateSlot(3));
        expectedModel.setLastShownList(model.getLastShownList());
    }

    /**
     *  Parser Tests
     */

    @Test
    public void execute_Valid_FindCommand_ParserManager() throws Exception {
        ParserManager parserManager = new ParserManager();
        Command actualFindCommand = parserManager.parseCommand("find n/CS2113T");
        CommandResult expectedFindCommandOutput = new FindCommandParser().parse("n/CS2113T").execute(model, EMPTY_COMMAND_HISTORY );
        assertEquals(expectedFindCommandOutput, actualFindCommand.execute(model, EMPTY_COMMAND_HISTORY));
    }
    @Test
    public void execute_Invalid_IncorrectPrefix_throwsParserException() throws Exception {
        FindCommandParser findCommandParser = new FindCommandParser();

        thrown.expect(ParseException.class);
        thrown.expectMessage(String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE));
        findCommandParser.parse("st/08:00");
    }

    @Test
    public void execute_Invalid_MultipleParams_throwsParserException() throws Exception {
        FindCommandParser findCommandParser = new FindCommandParser();

        thrown.expect(ParseException.class);
        thrown.expectMessage(String.format(MESSAGE_INVALID_MULTIPLE_PARAMS, FindCommand.MESSAGE_USAGE));
        findCommandParser.parse("n/CS2101 t/Tutorial");
    }

    @Test
    public void execute_ValidPrefixNameOnly_SameKeyword() throws Exception {
        FindCommandParser findCommandParser = new FindCommandParser();
        FindCommand expectedFindCommand = new FindCommand("CS2113", null);

        FindCommand actualFindCommand = findCommandParser.parse("n/CS2113");

        assertEquals(actualFindCommand.getKeyword(), expectedFindCommand.getKeyword());
    }

    @Test
    public void execute_ValidPrefixTagOnly_SameKeyword() throws Exception {
        FindCommandParser findCommandParser = new FindCommandParser();
        FindCommand expectedFindCommand = new FindCommand(null, "Tutorial");

        FindCommand actualFindCommand = findCommandParser.parse("t/Tutorial");

        assertEquals(actualFindCommand.getKeyword(), expectedFindCommand.getKeyword());
    }

    /**
     * Constructor Tests
     */

    @Test
    public void constructor_ValidName_NullTag() {
        FindCommand actualFindCommand = new FindCommand("CS2101", null);
        String expectedKeyword = "CS2101";

        assertEquals(expectedKeyword, actualFindCommand.getKeyword());
    }

    @Test
    public void constructor_NullName_ValidTag() {
        FindCommand actualFindCommand = new FindCommand(null, "Tutorial");
        String expectedKeyword = "Tutorial";

        assertEquals(expectedKeyword, actualFindCommand.getKeyword());
    }

    /**
     * Logic Tests
     */

    @Test
    public void isFindByName_ValidName_NullTag() {
        FindCommand actualFindCommand = new FindCommand("Name", null);

        assertTrue(actualFindCommand.getIsFindByName());
    }

    @Test
    public void isFindByName_NullName_ValidTag() {
        FindCommand actualFindCommand = new FindCommand(null, "Tag");

        assertFalse(actualFindCommand.getIsFindByName());
    }

    @Test
    public void execute_slotAcceptedByModel_FindExactNameSuccessful() {
        CommandResult commandResult = new FindCommand(slotBuilder.generateSlot(1).getName(), null).execute(model, commandHistory);

        List<WeightedName> selectedSlots = new ArrayList<>();
        Queue<WeightedName> weightedNames = new PriorityQueue<>(new Comparator<>() {
            @Override
            public int compare(WeightedName p1, WeightedName p2) {
                String n1 = p1.getName();
                String n2 = p2.getName();
                int d1 = p1.getDist();
                int d2 = p2.getDist();

                if (d1 != d2) {
                    return d1 - d2;
                } else {
                    return n1.compareTo(n2);
                }
            }
        });

        for (Map.Entry<LocalDate, Day> entry : model.getDays().entrySet()) {
            for (Slot slot : entry.getValue().getSlots()) {
                if (slot.getName().length() + 3 < slotBuilder.generateSlot(1).getName().length()) {
                    return;
                }
                int dist = Utils.getLevenshteinDistance(slotBuilder.generateSlot(1).getName(), slot.getName());
                WeightedName distNameTrie = new WeightedName(entry, slot, dist);
                weightedNames.add(distNameTrie);
            }
        }

        while (!weightedNames.isEmpty() && weightedNames.peek().getDist() < 10 ) {
            selectedSlots.add(weightedNames.poll());
        }
        assertEquals(String.format(MESSAGE_SUCCESS, selectedSlots.size(),
                Messages.craftListMessageWeighted(selectedSlots)), commandResult.getFeedbackToUser());
    }

    @Test
    public void execute_slotAcceptedByModel_FindExactTagSuccessful() {
        Set<String> tags = slotBuilder.generateSlot(1).getTags();
        String tagToTest = tags.iterator().next();

        CommandResult commandResult = new FindCommand(null, tagToTest).execute(model, commandHistory);

        List<WeightedName> selectedSlots = new ArrayList<>();
        Queue<WeightedName> weightedNames = new PriorityQueue<>(new Comparator<>() {
            @Override
            public int compare(WeightedName p1, WeightedName p2) {
                String n1 = p1.getName();
                String n2 = p2.getName();
                int d1 = p1.getDist();
                int d2 = p2.getDist();

                if (d1 != d2) {
                    return d1 - d2;
                } else {
                    return n1.compareTo(n2);
                }
            }
        });

        for (Map.Entry<LocalDate, Day> entry : model.getDays().entrySet()) {
            for (Slot slot : entry.getValue().getSlots()) {
                Set<String> tagSet = slot.getTags();
                for (String tag : tagSet) {
                    if (tag.length() + 3 < tagToTest.length()) {
                        return;
                    }
                    int dist = Utils.getLevenshteinDistance(tagToTest, tag);
                    WeightedName distNameTrie = new WeightedName(entry, slot, dist);
                    weightedNames.add(distNameTrie);
                }
            }
        }

        while (!weightedNames.isEmpty() && weightedNames.peek().getDist() < 10) {
            selectedSlots.add(weightedNames.poll());
        }
            assertEquals(String.format(MESSAGE_SUCCESS, selectedSlots.size(),
                    Messages.craftListMessageWeighted(selectedSlots)), commandResult.getFeedbackToUser());
    }

    @Test
    public void execute_slotAcceptedByModel_FindNameNotExact() throws Exception {
        String nameToTest = slotBuilder.generateSlot(1).getName().concat("NotTheSame");

        CommandResult commandResult = new FindCommand(nameToTest, null).execute(model, commandHistory);

        List<WeightedName> selectedSlots = new ArrayList<>();
        Queue<WeightedName> weightedNames = new PriorityQueue<>(new Comparator<>() {
            @Override
            public int compare(WeightedName p1, WeightedName p2) {
                String n1 = p1.getName();
                String n2 = p2.getName();
                int d1 = p1.getDist();
                int d2 = p2.getDist();

                if (d1 != d2) {
                    return d1 - d2;
                } else {
                    return n1.compareTo(n2);
                }
            }
        });

        for (Map.Entry<LocalDate, Day> entry : model.getDays().entrySet()) {
            for (Slot slot : entry.getValue().getSlots()) {
                if (slot.getName().length() + 3 < nameToTest.length()) {
                    return;
                }
                int dist = Utils.getLevenshteinDistance(nameToTest, slot.getName());
                WeightedName distNameTrie = new WeightedName(entry, slot, dist);
                weightedNames.add(distNameTrie);
            }
        }

        while (!weightedNames.isEmpty() && weightedNames.peek().getDist() < 10) {
            selectedSlots.add(weightedNames.poll());
        }
        assertEquals(String.format(MESSAGE_SUCCESS, selectedSlots.size(),
                Messages.craftListMessageWeighted(selectedSlots)), commandResult.getFeedbackToUser());
    }

    @Test
    public void execute_slotAcceptedByModel_FindTagNotExact() throws Exception {
        Set<String> tags = slotBuilder.generateSlot(1).getTags();
        String tagToTest = tags.iterator().next();
        tagToTest = tagToTest.concat("NotTheSame");

        CommandResult commandResult = new FindCommand(null, tagToTest).execute(model, commandHistory);

        List<WeightedName> selectedSlots = new ArrayList<>();
        Queue<WeightedName> weightedNames = new PriorityQueue<>(new Comparator<>() {
            @Override
            public int compare(WeightedName p1, WeightedName p2) {
                String n1 = p1.getName();
                String n2 = p2.getName();
                int d1 = p1.getDist();
                int d2 = p2.getDist();

                if (d1 != d2) {
                    return d1 - d2;
                } else {
                    return n1.compareTo(n2);
                }
            }
        });

        for (Map.Entry<LocalDate, Day> entry : model.getDays().entrySet()) {
            for (Slot slot : entry.getValue().getSlots()) {
                Set<String> tagSet = slot.getTags();
                for (String tag : tagSet) {
                    if (tag.length() + 3 < tagToTest.length()) {
                        return;
                    }
                    int dist = Utils.getLevenshteinDistance(tagToTest, tag);
                    WeightedName distNameTrie = new WeightedName(entry, slot, dist);
                    weightedNames.add(distNameTrie);
                }
            }
        }

        while (!weightedNames.isEmpty() && weightedNames.peek().getDist() < 10) {
            selectedSlots.add(weightedNames.poll());
        }
        assertEquals(String.format(MESSAGE_SUCCESS, selectedSlots.size(),
                Messages.craftListMessageWeighted(selectedSlots)), commandResult.getFeedbackToUser());
    }

    @Test
    public void execute_slotAcceptedByModel_FindTagNotFound() throws Exception {
        String tagToTest = LONG_STRING;

        CommandResult commandResult = new FindCommand(null, tagToTest).execute(model, commandHistory);
        List<WeightedName> selectedSlots = new ArrayList<>();
        Queue<WeightedName> weightedNames = new PriorityQueue<>(new Comparator<>() {
            @Override
            public int compare(WeightedName p1, WeightedName p2) {
                String n1 = p1.getName();
                String n2 = p2.getName();
                int d1 = p1.getDist();
                int d2 = p2.getDist();

                if (d1 != d2) {
                    return d1 - d2;
                } else {
                    return n1.compareTo(n2);
                }
            }
        });

        for (Map.Entry<LocalDate, Day> entry : model.getDays().entrySet()) {
            for (Slot slot : entry.getValue().getSlots()) {
                Set<String> tagSet = slot.getTags();
                for (String tag : tagSet) {
                    if (tag.length() + 3 < tagToTest.length()) {
                        return;
                    }
                    int dist = Utils.getLevenshteinDistance(tagToTest, tag);
                    WeightedName distNameTrie = new WeightedName(entry, slot, dist);
                    weightedNames.add(distNameTrie);
                }
            }
        }

        while (!weightedNames.isEmpty() && weightedNames.peek().getDist() < 10) {
            selectedSlots.add(weightedNames.poll());
        }
        assertEquals(MESSAGE_SUCCESS_NONE, commandResult.getFeedbackToUser());
    }
}
