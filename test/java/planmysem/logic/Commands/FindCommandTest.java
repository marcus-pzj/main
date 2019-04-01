package planmysem.logic.Commands;

import javafx.util.Pair;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import planmysem.common.Clock;
import planmysem.common.Messages;
import planmysem.common.Utils;
import planmysem.logic.CommandHistory;
import planmysem.logic.commands.CommandResult;
import planmysem.logic.commands.FindCommand;
import planmysem.logic.parser.FindCommandParser;
import planmysem.logic.parser.exceptions.ParseException;
import planmysem.model.Model;
import planmysem.model.Planner;
import planmysem.model.VersionedPlanner;
import planmysem.model.semester.Day;
import planmysem.model.semester.ReadOnlyDay;
import planmysem.model.semester.Semester;
import planmysem.model.semester.WeightedName;
import planmysem.model.slot.ReadOnlySlot;
import planmysem.model.slot.Slot;
import planmysem.testutil.SlotBuilder;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;

import static java.util.Objects.requireNonNull;
import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static planmysem.common.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static planmysem.common.Messages.MESSAGE_INVALID_MULTIPLE_PARAMS;
import static planmysem.logic.commands.ListCommand.MESSAGE_SUCCESS;
import static planmysem.logic.commands.ListCommand.MESSAGE_SUCCESS_NONE;

public class FindCommandTest {
    private static final CommandHistory EMPTY_COMMAND_HISTORY = new CommandHistory();

    private CommandHistory commandHistory = new CommandHistory();

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Before
    public void setup() {
        Clock.set("2019-01-14T10:00:00Z");
    }

    /**
     *  Parser Tests
     */

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
        ModelStubAcceptingSlotAdded modelStub = new ModelStubAcceptingSlotAdded();
        Slot validSlot = new SlotBuilder().slotOne();
        LocalDate date = LocalDate.of(2019, 2, 1);
        modelStub.addSlot(date, validSlot);

        CommandResult commandResult = new FindCommand(validSlot.getName(), null).execute(modelStub, commandHistory);

        Map<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> selectedSlots = new TreeMap<>();

        for (Map.Entry<LocalDate, Day> entry : modelStub.getDays().entrySet()) {
            for (Slot slot : entry.getValue().getSlots()) {
                if (slot.getName().equalsIgnoreCase(validSlot.getName())) {
                    selectedSlots.put(entry.getKey(), new Pair<>(entry.getValue(), slot));
                }
            }
        }

        assertEquals(String.format(MESSAGE_SUCCESS, selectedSlots.size(),
                Messages.craftListMessage(selectedSlots)), commandResult.getFeedbackToUser());
    }

    @Test
    public void execute_slotAcceptedByModel_FindExactTagSuccessful() {
        ModelStubAcceptingSlotAdded modelStub = new ModelStubAcceptingSlotAdded();
        Slot validSlot = new SlotBuilder().slotOne();
        LocalDate date = LocalDate.of(2019, 2, 1);
        modelStub.addSlot(date, validSlot);

        Set<String> tags = validSlot.getTags();
        String tagToTest = tags.iterator().next();

        CommandResult commandResult = new FindCommand(null, tagToTest).execute(modelStub, commandHistory);

        Map<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> selectedSlots = new TreeMap<>();

        for (Map.Entry<LocalDate, Day> entry : modelStub.getDays().entrySet()) {
            for (Slot slot : entry.getValue().getSlots()) {
                Set<String> tagSet = slot.getTags();
                for (String tag : tagSet) {
                    if (tag.equalsIgnoreCase(tagToTest)) {
                        selectedSlots.put(entry.getKey(), new Pair<>(entry.getValue(), slot));
                    }
                }
            }
        }

        assertEquals(String.format(MESSAGE_SUCCESS, selectedSlots.size(),
                Messages.craftListMessage(selectedSlots)), commandResult.getFeedbackToUser());
    }

    @Test
    public void execute_slotAcceptedByModel_FindNameNotExact() {
        ModelStubAcceptingSlotAdded modelStub = new ModelStubAcceptingSlotAdded();
        Slot validSlot = new SlotBuilder().slotOne();
        LocalDate date = LocalDate.of(2019, 4, 30);
        modelStub.addSlot(date, validSlot);
        String nameToTest = validSlot.getName().concat("NotTheSame");

        CommandResult commandResult = new FindCommand(nameToTest, null).execute(modelStub, commandHistory);

        List<WeightedName> selectedSlots = new ArrayList<>();
        int minDistance = Integer.MAX_VALUE;
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

        for (Map.Entry<LocalDate, Day> entry : modelStub.getDays().entrySet()) {
            for (Slot slot : entry.getValue().getSlots()) {
                int dist = Utils.getLevenshteinDistance(nameToTest, slot.getName());
                if (dist < minDistance) {
                    minDistance = dist;
                }
                WeightedName distNameTrie = new WeightedName(entry, slot, dist);
                weightedNames.add(distNameTrie);
            }
        }

        while (!weightedNames.isEmpty() && weightedNames.peek().getDist() < minDistance + 1) {
            selectedSlots.add(weightedNames.poll());
        }

        assertEquals(String.format(MESSAGE_SUCCESS, selectedSlots.size(),
                Messages.craftListMessage(selectedSlots)), commandResult.getFeedbackToUser());
    }

    @Test
    public void execute_slotAcceptedByModel_FindTagNotFound() {
        ModelStubAcceptingSlotAdded modelStub = new ModelStubAcceptingSlotAdded();
        Slot validSlot = new SlotBuilder().slotOne();
        LocalDate date = LocalDate.of(2019, 2, 1);
        modelStub.addSlot(date, validSlot);

        Set<String> tags = validSlot.getTags();
        String tagToTest = tags.iterator().next();
        tagToTest.concat("NotTheSame");

        CommandResult commandResult = new FindCommand(null, tagToTest).execute(modelStub, commandHistory);

        Map<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> selectedSlots = new TreeMap<>();

        for (Map.Entry<LocalDate, Day> entry : modelStub.getDays().entrySet()) {
            for (Slot slot : entry.getValue().getSlots()) {
                Set<String> tagSet = slot.getTags();
                for (String tag : tagSet) {
                    if (tag.equalsIgnoreCase(tagToTest)) {
                        selectedSlots.put(entry.getKey(), new Pair<>(entry.getValue(), slot));
                    }
                }
            }
        }
        assertEquals(MESSAGE_SUCCESS_NONE, commandResult.getFeedbackToUser());
    }

    /**
     * A default model stub that have all of the methods failing.
     */
    private class ModelStub implements Model {
        private final VersionedPlanner versionedPlanner = new VersionedPlanner(new Planner());

        @Override
        public List<Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>>> getLastShownList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void clearLastShownList() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void commit() {
        }

        @Override
        public void setLastShownList(List<Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>>> list) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void setLastShownList(Map<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> list) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> getLastShownItem(int index) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Day addSlot(LocalDate date, Slot slot) throws Semester.DateNotFoundException {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void removeSlot(LocalDate date, ReadOnlySlot slot) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void removeSlot(Pair<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> slot) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void editSlot(LocalDate targetDate, ReadOnlySlot targetSlot, LocalDate date,
                             LocalTime startTime, int duration, String name, String location,
                             String description, Set<String> tags) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void clearSlots() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Planner getPlanner() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public HashMap<LocalDate, Day> getDays() {
            return versionedPlanner.getDays();
        }

        @Override
        public Day getDay(LocalDate date) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public Map<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> getSlots(Set<String> tags) {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean canUndo() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean canRedo() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void undo() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public void redo() {
            throw new AssertionError("This method should not be called.");
        }

        @Override
        public boolean equals(Object obj) {
            throw new AssertionError("This method should not be called.");
        }
    }

    /**
     * A Model stub that contains a single slot.
     */
    private class ModelStubWithSlot extends ModelStub {
        private final Slot slot;

        ModelStubWithSlot(Slot slot) {
            requireNonNull(slot);
            this.slot = slot;
        }
    }

    /**
     * A Model stub that always accept the slot being added.
     */
    private class ModelStubAcceptingSlotAdded extends ModelStub {
        Map<LocalDate, Day> days = new TreeMap<>();

        @Override
        public Day addSlot(LocalDate date, Slot slot) {
            Day day = new Day(DayOfWeek.MONDAY, "type");
            day.addSlot(slot);

            days.put(date, day);

            return day;
        }

        @Override
        public Planner getPlanner() {
            return new Planner();
        }
    }

    private ModelStubAcceptingSlotAdded generateModelStubAddedSlot() {
        ModelStubAcceptingSlotAdded modelStub = new ModelStubAcceptingSlotAdded();
        Slot validSlot = new SlotBuilder().slotOne();
        LocalDate date = LocalDate.of(2019, 2, 1);
        modelStub.addSlot(date, validSlot);

        return modelStub;
    }
}
