package planmysem.logic.Commands;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Test;
import planmysem.logic.commands.CommandResult;

public class CommandResultTest {
    @Test
    public void equals() {
        CommandResult commandResult = new CommandResult("test");
        CommandResult CommandResultClone = new CommandResult("test");

        // equals same object
        assertEquals(commandResult, commandResult);
        assertEquals(commandResult.hashCode(), commandResult.hashCode());

        // equals null
        assertNotEquals(commandResult, null);

        // equals different object same values
        assertEquals(commandResult, CommandResultClone);
        assertEquals(commandResult.hashCode(), CommandResultClone.hashCode());
    }
}
