package planmysem.logic.Commands;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import planmysem.logic.parser.ParserManager;
import planmysem.logic.parser.exceptions.ParseException;

public class HelpCommandTest {
    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void whiteSpaceInput_ParserManager_throwsParseException() throws ParseException{
        thrown.expect(ParseException.class);
        ParserManager parserManager = new ParserManager();
        parserManager.parseCommand(" ");
    }
}
