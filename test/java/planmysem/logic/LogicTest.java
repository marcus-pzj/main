package planmysem.logic;


import static junit.framework.TestCase.assertEquals;
import static planmysem.common.Messages.MESSAGE_INVALID_COMMAND_FORMAT;
import static planmysem.common.Messages.MESSAGE_INVALID_COMMAND_FORMAT_ADDITIONAL;
import static planmysem.common.Messages.MESSAGE_INVALID_DATE;
import static planmysem.common.Messages.MESSAGE_INVALID_MULTIPLE_PARAMS;
import static planmysem.common.Messages.MESSAGE_INVALID_SLOT_DISPLAYED_INDEX;
import static planmysem.common.Messages.MESSAGE_INVALID_TIME;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringJoiner;
import java.util.TreeMap;

import javafx.util.Pair;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import planmysem.commands.AddCommand;
import planmysem.commands.ClearCommand;
import planmysem.commands.CommandResult;
import planmysem.commands.DeleteCommand;
import planmysem.commands.EditCommand;
import planmysem.commands.ExitCommand;
import planmysem.commands.FindCommand;
import planmysem.commands.HelpCommand;
import planmysem.commands.ListCommand;
import planmysem.common.Messages;
import planmysem.common.Utils;
import planmysem.data.Planner;
import planmysem.data.recurrence.Recurrence;
import planmysem.data.semester.Day;
import planmysem.data.semester.ReadOnlyDay;
import planmysem.data.slot.Description;
import planmysem.data.slot.Location;
import planmysem.data.slot.Name;
import planmysem.data.slot.ReadOnlySlot;
import planmysem.data.slot.Slot;
import planmysem.data.tag.Tag;
import planmysem.storage.StorageFile;

public class LogicTest {

    /**
     * See https://github.com/junit-team/junit4/wiki/rules#temporaryfolder-rule
     */
    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private StorageFile storageFile;
    private Planner planner;
    private Logic logic;

    @Before
    public void setup() throws Exception {
        storageFile = new StorageFile(temporaryFolder.newFile("testSaveFile.txt").getPath());
        planner = createPlanner();
        storageFile.save(planner);
        logic = new Logic(storageFile, planner);
        Instant.now(Clock.fixed(
                Instant.parse("2019-02-02T10:00:00Z"),
                ZoneOffset.UTC));
    }

    private Planner createPlanner() {
        return new Planner(Planner.generateSemester(LocalDate.of(2019, 1, 14)));
    }

    @Test
    public void constructor() {
        //Constructor is called in the setup() method which executes before every test, no need to call it here again.

        //Confirm the last shown list is empty
        assertEquals(null, logic.getLastShownSlots());
    }

    @Test
    public void execute_invalid() throws Exception {
        String invalidCommand = "       ";
        assertCommandBehavior(invalidCommand,
                String.format(MESSAGE_INVALID_COMMAND_FORMAT, HelpCommand.MESSAGE_USAGE));
    }

    @Test
    public void execute_unknownCommandWord() throws Exception {
        String unknownCommand = "uicfhmowqewca";
        assertCommandBehavior(unknownCommand, HelpCommand.MESSAGE_ALL_USAGES);
    }

    @Test
    public void execute_help() throws Exception {
        assertCommandBehavior("help", HelpCommand.MESSAGE_ALL_USAGES);
    }

    @Test
    public void execute_exit() throws Exception {
        assertCommandBehavior("exit", ExitCommand.MESSAGE_EXIT_ACKNOWEDGEMENT);
    }

    @Test
    public void execute_clear() throws Exception {
        Planner expectedPlanner = createPlanner();
        TestDataHelper helper = new TestDataHelper();
        planner.addSlot(LocalDate.of(2019, 2, 1), helper.generateSlot(1));
        planner.addSlot(LocalDate.of(2019, 2, 1), helper.generateSlot(2));
        planner.addSlot(LocalDate.of(2019, 2, 1), helper.generateSlot(3));

        assertCommandBehavior("clear",
                ClearCommand.MESSAGE_SUCCESS,
                expectedPlanner,
                false,
                null);
    }

    /**
     * Test add command
     */

    @Test
    public void execute_add_invalidArgsFormat() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, AddCommand.MESSAGE_USAGE);
        assertCommandBehavior(
                "add wrong args wrong args", expectedMessage);
        assertCommandBehavior(
                "add d/mon st/08:00 et/09:00", expectedMessage);
        assertCommandBehavior(
                "add n/CS2113T Tutorial st/08:00 et/09:00", expectedMessage);
        assertCommandBehavior(
                "add n/CS2113T Tutorial d/mon et/09:00", expectedMessage);
    }

    @Test
    public void execute_add_invalidSlotData() throws Exception {
        assertCommandBehavior("add n/CS2113T Tutorial d/mon st/08:00 et/25:00", String.format(MESSAGE_INVALID_COMMAND_FORMAT_ADDITIONAL,
                        AddCommand.MESSAGE_USAGE, MESSAGE_INVALID_TIME));
        assertCommandBehavior("add n/CS2113T Tutorial d/mon st/08:00 et/13:00am", String.format(MESSAGE_INVALID_COMMAND_FORMAT_ADDITIONAL,
                AddCommand.MESSAGE_USAGE, MESSAGE_INVALID_TIME));
        assertCommandBehavior("add n/CS2113T Tutorial d/Superday st/08:00 et/11:00", String.format(MESSAGE_INVALID_COMMAND_FORMAT_ADDITIONAL,
                AddCommand.MESSAGE_USAGE, MESSAGE_INVALID_DATE));
        assertCommandBehavior("add n/CS2113T Tutorial d/01-13-2019 st/08:00 et/11:00", String.format(MESSAGE_INVALID_COMMAND_FORMAT_ADDITIONAL,
                AddCommand.MESSAGE_USAGE, MESSAGE_INVALID_DATE));
    }

    @Test
    public void execute_add_by_date_successful() throws Exception {
        // item to be added
        TestDataHelper helper = new TestDataHelper();
        Slot slotToBeAdded = helper.slotOne();
        LocalDate dateToBeAdded = LocalDate.of(2019, 2, 2);

        // expectation
        Planner expectedPlanner = createPlanner();
        expectedPlanner.addSlot(dateToBeAdded, slotToBeAdded);
        Map<LocalDate, Day> days = new TreeMap<>();
        days.put(dateToBeAdded, expectedPlanner.getDay(dateToBeAdded));

        // execute command and verify result
        assertCommandBehavior(helper.generateAddCommand(slotToBeAdded, dateToBeAdded, ""),
                String.format(AddCommand.MESSAGE_SUCCESS,
                        1,
                        AddCommand.craftSuccessMessage(days, slotToBeAdded)),
                expectedPlanner,
                false,
                null);
    }

    @Test
    public void execute_add_by_day_successful() throws Exception {
        // because adding by day takes the nearest day, the planner has to be changed to be in respects to the current system date.
        // item to be added
        TestDataHelper helper = new TestDataHelper();
        Slot slotToBeAdded = helper.slotOne();
        int dayToBeAdded = LocalDate.now().getDayOfWeek().getValue();
        LocalDate dateToBeAdded = Utils.getNearestDayOfWeek(LocalDate.now(), dayToBeAdded);

        // expectation
        Planner expectedPlanner = createPlanner();
        expectedPlanner.addSlot(dateToBeAdded, slotToBeAdded);
        Map<LocalDate, Day> days = new TreeMap<>();
        days.put(dateToBeAdded, expectedPlanner.getDay(dateToBeAdded));

        // execute command and verify result
        assertCommandBehavior(helper.generateAddCommand(slotToBeAdded, dayToBeAdded, ""),
                String.format(AddCommand.MESSAGE_SUCCESS,
                        1,
                        AddCommand.craftSuccessMessage(days, slotToBeAdded)),
                expectedPlanner,
                false,
                null);
    }

    @Test
    public void execute_add_by_date_multiple_successful() throws Exception {
        // item to be added
        TestDataHelper helper = new TestDataHelper();
        Slot slotToBeAdded = helper.slotOne();
        LocalDate dateToBeAdded = LocalDate.of(2019, 2, 2);

        // expectation
        Planner expectedPlanner = createPlanner();
        Recurrence recurrence = new Recurrence(new HashSet<>(Arrays.asList("normal")), dateToBeAdded);
        Map<LocalDate, Day> days = new TreeMap<>();
        for (LocalDate date : recurrence.generateDates(expectedPlanner.getSemester())) {
            expectedPlanner.addSlot(date, slotToBeAdded);
            days.put(date, expectedPlanner.getDay(date));
        }


        // execute command and verify result
        assertCommandBehavior(helper.generateAddCommand(slotToBeAdded, dateToBeAdded, "r/normal"),
                String.format(AddCommand.MESSAGE_SUCCESS,
                        days.size(),
                        AddCommand.craftSuccessMessage(days, slotToBeAdded)),
                expectedPlanner,
                false,
                null);
    }

    @Test
    public void execute_add_by_day_multiple_successful() throws Exception {
        // item to be added
        TestDataHelper helper = new TestDataHelper();
        Slot slotToBeAdded = helper.slotOne();
        int dayToBeAdded = LocalDate.of(2019, 2, 2).getDayOfWeek().getValue();
        LocalDate dateToBeAdded = Utils.getNearestDayOfWeek(LocalDate.now(), dayToBeAdded);

        // expectation
        Planner expectedPlanner = createPlanner();
        Recurrence recurrence = new Recurrence(new HashSet<>(Arrays.asList("normal")), dayToBeAdded);
        Map<LocalDate, Day> days = new TreeMap<>();
        for (LocalDate date : recurrence.generateDates(expectedPlanner.getSemester())) {
            expectedPlanner.addSlot(date, slotToBeAdded);
            days.put(date, expectedPlanner.getDay(date));
        }


        // execute command and verify result
        assertCommandBehavior(helper.generateAddCommand(slotToBeAdded, dayToBeAdded, "r/normal"),
                String.format(AddCommand.MESSAGE_SUCCESS,
                        days.size(),
                        AddCommand.craftSuccessMessage(days, slotToBeAdded)),
                expectedPlanner,
                false,
                null);
    }

    @Test
    public void execute_add_unsuccessful() throws Exception {
        // item to be added
        TestDataHelper helper = new TestDataHelper();
        Slot slotToBeAdded = helper.slotOne();
        LocalDate dateToBeAdded = LocalDate.of(1999, 1, 1);

        // execute command and verify result
        assertCommandBehavior(helper.generateAddCommand(slotToBeAdded, dateToBeAdded, ""),
                AddCommand.MESSAGE_FAIL_OUT_OF_BOUNDS);
    }

    /**
     * Test edit command
     */

    @Test
    public void execute_edit_invalidArgsFormat() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, EditCommand.MESSAGE_USAGE);
        assertCommandBehavior(
                "edit wrong arguments", expectedMessage);
        assertCommandBehavior(
                "edit nl/COM2 04-01", expectedMessage);
        assertCommandBehavior(
                "edit -1", expectedMessage);
        assertCommandBehavior(
                "e nl/COM2 04-01", expectedMessage);
    }

    @Test
    public void execute_edit_successful() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        Slot slotToBeAdded = helper.slotOne();
        LocalDate dateToBeAdded = LocalDate.of(2019, 2, 2);
        planner.addSlot(dateToBeAdded, new Slot(slotToBeAdded));
        Map<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> selectedSlots = new TreeMap<>();
        selectedSlots.put(dateToBeAdded,
                new Pair(planner.getDay(dateToBeAdded), new Slot(slotToBeAdded)));

        // setup expectations
        Planner expectedPlanner = createPlanner();
        expectedPlanner.addSlot(dateToBeAdded, slotToBeAdded);

        // create tags
        Set<String> rawTags = new HashSet<>();
        rawTags.add("CS2113T");
        Set<Tag> tags = Utils.parseTags(rawTags);

        Set<String> rawNewTags = new HashSet<>();
        rawNewTags.add("CS2101");
        Set<Tag> newTags = Utils.parseTags(rawNewTags);

        expectedPlanner.editSlot(dateToBeAdded, slotToBeAdded, null, LocalTime.of(4, 0), 60,
                "test", "testlo", "testdes", newTags);

        // Just to generate the crafted message in this case.
        EditCommand ec = new EditCommand("test", LocalTime.of(4, 0), 60,
                "testlo", "testdes", rawTags, rawNewTags);

        // execute command and verify result
        assertCommandBehavior("edit t/CS2113T nt/CS2101 nn/test nl/testlo ndes/testdes nst/04:00 net/60",
                String.format(EditCommand.MESSAGE_SUCCESS, selectedSlots.size(),
                        Messages.craftSelectedMessage(tags), ec.craftSuccessMessage(selectedSlots)),
                expectedPlanner,
                false,
                null);
    }

    @Test
    public void execute_edit_no_change_successful() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        Set<String> rawTags = new HashSet<>();
        rawTags.add("someTagThatDoesNotExist");
        Set<Tag> tags = Utils.parseTags(rawTags);

        assertCommandBehavior("edit t/someTagThatDoesNotExist n/test",
                String.format(EditCommand.MESSAGE_SUCCESS_NO_CHANGE,
                        Messages.craftSelectedMessage(tags)));
    }

    @Test
    public void execute_edit_invalid_slot_displayed_unsuccessful() throws Exception {
        assertCommandBehavior("edit 100", MESSAGE_INVALID_SLOT_DISPLAYED_INDEX);
    }

//    @Test
//    public void execute_edit_out_of_bound_unsuccessful() throws Exception {
//        assertCommandBehavior("edit 100", MESSAGE_INVALID_SLOT_DISPLAYED_INDEX);
//    }

    /**
     * Test delete command
     */

    @Test
    public void execute_delete_invalidArgsFormat() throws Exception {
        String expectedMessage = String.format(MESSAGE_INVALID_COMMAND_FORMAT, DeleteCommand.MESSAGE_USAGE);
        assertCommandBehavior(
                "delete wrong args wrong args", expectedMessage);
        assertCommandBehavior(
                "delete t", expectedMessage);
        assertCommandBehavior(
                "del wrong", expectedMessage);
        assertCommandBehavior(
                "d wrong", expectedMessage);
    }

    @Test
    public void execute_delete_successful() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        Slot slotToBeAdded = helper.slotOne();
        LocalDate dateToBeAdded = LocalDate.of(2019, 2, 2);
        planner.addSlot(dateToBeAdded, slotToBeAdded);
        Map<LocalDate, Pair<ReadOnlyDay, ReadOnlySlot>> selectedSlots = new TreeMap<>();
        selectedSlots.put(dateToBeAdded,
                new Pair(planner.getDay(dateToBeAdded), slotToBeAdded));

        // setup expectations
        Planner expectedPlanner = createPlanner();
        expectedPlanner.addSlot(dateToBeAdded, slotToBeAdded);
        expectedPlanner.getSemester().removeSlot(dateToBeAdded, slotToBeAdded);

        // execute command and verify result
        assertCommandBehavior(helper.generateDeleteCommand(slotToBeAdded),
                String.format(DeleteCommand.MESSAGE_SUCCESS,
                        1,
                        Messages.craftSelectedMessage(slotToBeAdded.getTags()),
                        Messages.craftSelectedMessage("Deleted Slots:", selectedSlots)),                expectedPlanner,
                false,
                null);
    }

    @Test
    public void execute_delete_no_change_successful() throws Exception {
        TestDataHelper helper = new TestDataHelper();
        Set<String> rawTags = new HashSet<>();
        rawTags.add("someTagThatDoesNotExist");
        Set<Tag> tags = Utils.parseTags(rawTags);

        assertCommandBehavior(helper.generateDeleteCommand(tags),
                String.format(DeleteCommand.MESSAGE_SUCCESS_NO_CHANGE,
                        Messages.craftSelectedMessage(tags)));
    }

    @Test
    public void execute_delete_invalid_slot_displayed_unsuccessful() throws Exception {
        assertCommandBehavior("delete 100", MESSAGE_INVALID_SLOT_DISPLAYED_INDEX);
    }

    /**
     * Test find command
     */

    @Test
    public void execute_find_invalidArgsFormat() throws Exception {
        String expectedMessageSingle = String.format(MESSAGE_INVALID_COMMAND_FORMAT, FindCommand.MESSAGE_USAGE);
        String expectedMessageMultipleParams = String.format(MESSAGE_INVALID_MULTIPLE_PARAMS, FindCommand.MESSAGE_USAGE);
        assertCommandBehavior(
                "find wrong args wrong args", expectedMessageSingle);
        assertCommandBehavior(
                "find n/CS2113T t/Tutorial", expectedMessageMultipleParams);
    }

    @Test
    public void execute_list_invalidArgsFormat() throws Exception {
        String expectedMessageSingle = String.format(MESSAGE_INVALID_COMMAND_FORMAT, ListCommand.MESSAGE_USAGE);
        String expectedMessageMultipleParams = String.format(MESSAGE_INVALID_MULTIPLE_PARAMS, ListCommand.MESSAGE_USAGE);
        assertCommandBehavior(
                "list wrong args wrong args", expectedMessageSingle);
        assertCommandBehavior(
                "list n/CS2113T t/Tutorial", expectedMessageMultipleParams);
    }

    /**
     * Executes the command and confirms that the result message is correct.
     * Both the 'planner' and the 'last shown list' are expected to be empty.
     * @see #assertCommandBehavior(String, String, Planner, boolean, List)
     */
    private void assertCommandBehavior(String inputCommand, String expectedMessage) throws Exception {
        assertCommandBehavior(inputCommand, expectedMessage, planner,false, null);
    }

    /**
     * Executes the command and confirms that the result message is correct and
     * also confirms that the following three parts of the Logic object's state are as expected:<br>
     *      - the internal planner data are same as those in the {@code expectedPlanner} <br>
     *      - the internal 'last shown slots' matches the {@code expectedLastList} <br>
     *      - the storage file content matches data in {@code expectedPlanner} <br>
     */
    private void assertCommandBehavior(String inputCommand,
                                       String expectedMessage,
                                       Planner expectedPlanner,
                                       boolean isRelevantSlotsExpected,
                                       List<? extends ReadOnlySlot> lastShownSlots) throws Exception {

        //Execute the command
        CommandResult r = logic.execute(inputCommand);

        //Confirm the result contains the right data
        assertEquals(expectedMessage, r.feedbackToUser);
        assertEquals(r.getRelevantSlots().isPresent(), isRelevantSlotsExpected);
        if(isRelevantSlotsExpected){
            assertEquals(lastShownSlots, r.getRelevantSlots().get());
        }

        //Confirm the state of data is as expected
        assertEquals(expectedPlanner, planner);
        assertEquals(lastShownSlots, logic.getLastShownSlots());
        assertEquals(planner, storageFile.load());
    }


    /**
     * A utility class to generate test data.
     */
    class TestDataHelper{

        Slot slotOne() throws Exception {
            Name name = new Name("CS2113T Tutorial");
            Location location = new Location("COM2 04-11");
            Description description = new Description("Topic: Sequence Diagram");
            LocalTime startTime = LocalTime.parse("08:00");
            LocalTime endTime = LocalTime.parse("09:00");
            Tag tag1 = new Tag("CS2113T");
            Tag tag2 = new Tag("Tutorial");
            Set<Tag> tags = new HashSet<>(Arrays.asList(tag1, tag2));
            return new Slot(name, location, description, startTime, endTime, tags);
        }

        /**
         * Generates a valid slot using the given seed.
         * Running this function with the same parameter values guarantees the returned slot will have the same state.
         * Each unique seed will generate a unique slot object.
         *
         * @param seed used to generate the person data field values
         */
        Slot generateSlot(int seed) throws Exception {
            return new Slot(
                    new Name("slot " + seed),
                    new Location("location " + Math.abs(seed)),
                    new Description("description " + Math.abs(seed)),
                    LocalTime.parse("00:00"),
                    LocalTime.parse("00:00"),
                    new HashSet<>(Arrays.asList(new Tag("tag" + Math.abs(seed)), new Tag("tag" + Math.abs(seed + 1))))
            );
        }

        /** Generates the correct add command based on the person given */
        String generateAddCommand(Slot s, LocalDate date, String recurrence) {
            StringJoiner cmd = new StringJoiner(" ");

            cmd.add("add");

            cmd.add("n/" + s.getName());
            cmd.add("d/" + Utils.parseDate(date));
            cmd.add("st/" + s.getStartTime());
            cmd.add("et/" + s.getDuration());
            if (s.getLocation() != null) {
                cmd.add("l/" + s.getLocation());
            }
            if (s.getDescription() != null) {
                cmd.add("des/" + s.getDescription());
            }

            Set<Tag> tags = s.getTags();
            if (tags != null) {
                for(Tag tag : tags){
                    cmd.add("t/" + tag);
                }
            }

            cmd.add(recurrence);

            return cmd.toString();
        }

        /** Generates the correct add command based on the person given */
        String generateAddCommand(Slot s, int day, String recurrence) {
            StringJoiner cmd = new StringJoiner(" ");

            cmd.add("add");

            cmd.add("n/" + s.getName());
            cmd.add("d/" + day);
            cmd.add("st/" + s.getStartTime());
            cmd.add("et/" + s.getDuration());
            if (s.getLocation() != null) {
                cmd.add("l/" + s.getLocation());
            }
            if (s.getDescription() != null) {
                cmd.add("des/" + s.getDescription());
            }

            Set<Tag> tags = s.getTags();
            if (tags != null) {
                for(Tag tag : tags){
                    cmd.add("t/" + tag);
                }
            }

            cmd.add(recurrence);

            return cmd.toString();
        }

        /** Generates the correct delete command based on tags */
        String generateDeleteCommand(Set<Tag> tags) {
            StringJoiner cmd = new StringJoiner(" ");

            cmd.add("delete");

            if (tags != null) {
                for(Tag tag : tags){
                    cmd.add("t/" + tag);
                }
            }

            return cmd.toString();
        }

        /** Generates the correct delete command based on the slot. */
        String generateDeleteCommand(Slot slot) {
            StringJoiner cmd = new StringJoiner(" ");

            cmd.add("delete");

            Set<Tag> tags = slot.getTags();
            if (tags != null) {
                for(Tag tag : tags){
                    cmd.add("t/" + tag);
                }
            }

            return cmd.toString();
        }

        //        public String recurrenceToString(Recurrence recurrence) {
        //            StringJoiner cmd = new StringJoiner(" ");
        //            if (recurrence.recess) {
        //                cmd.add("r/" + "recess");
        //            }
        //            if (reading) {
        //                cmd.add("r/" + "reading");
        //            }
        //            if (normal) {
        //                cmd.add("r/" + "normal");
        //            }
        //            if (exam) {
        //                cmd.add("r/" + "exam");
        //            }
        //            if (past) {
        //                cmd.add("r/" + "past");
        //            }
        //            return cmd.toString();
        //        }
        /**
         * Generates an AddressBook with auto-generated persons.
         * @param isPrivateStatuses flags to indicate if all contact details of respective persons should be set to
         *                          private.
         */
        //        AddressBook generateAddressBook(Boolean... isPrivateStatuses) throws Exception{
        //            AddressBook addressBook = new AddressBook();
        //            addToAddressBook(addressBook, isPrivateStatuses);
        //            return addressBook;
        //        }

        /**
         * Generates an AddressBook based on the list of Persons given.
         */
        //        AddressBook generateAddressBook(List<Person> persons) throws Exception{
        //            AddressBook addressBook = new AddressBook();
        //            addToAddressBook(addressBook, persons);
        //            return addressBook;
        //        }

        /**
         * Adds auto-generated Person objects to the given AddressBook
         * @param planner The AddressBook to which the Persons will be added
         */
        //        void addToPlanner(Planner planner) throws Exception{
        //            addToPlanner(planner, generatePersonList(isPrivateStatuses));
        //        }

        /**
         * Adds the given list of slots to the given Planner
         */
        void addToPlanner(Planner planner, List<Pair<LocalDate, Slot>> slotsToAdd) throws Exception{
            for(Pair<LocalDate, Slot> p: slotsToAdd){
                planner.addSlot(p.getKey(), p.getValue());
            }
        }

        /**
         * Creates a list of Persons based on the give Person objects.
         */
        //        List<Person> generatePersonList(Person... persons) throws Exception{
        //            List<Person> personList = new ArrayList<>();
        //            for(Person p: persons){
        //                personList.add(p);
        //            }
        //            return personList;
        //        }

        /**
         * Generates a list of Persons based on the flags.
         * @param isPrivateStatuses flags to indicate if all contact details of respective persons should be set to
         *                          private.
         */
        //        List<Person> generatePersonList(Boolean... isPrivateStatuses) throws Exception{
        //            List<Person> persons = new ArrayList<>();
        //            int i = 1;
        //            for(Boolean p: isPrivateStatuses){
        //                persons.add(generatePerson(i++, p));
        //            }
        //            return persons;
        //        }

        /**
         * Generates a Person object with given name. Other fields will have some dummy values.
         */
        //        Person generatePersonWithName(String name) throws Exception {
        //            return new Person(
        //                    new Name(name),
        //                    new Phone("1", false),
        //                    new Email("1@email", false),
        //                    new Address("House of 1", false),
        //                    Collections.singleton(new Tag("tag"))
        //            );
        //        }
    }

}