package planmysem.model;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;
import planmysem.common.Clock;

public class PlannerTest {

    @Before
    public void setup() {
        Clock.set("2019-01-14T10:00:00Z");
    }

    @Test
    public void initTest() {
        Planner planner = new Planner();
        Planner expectedPlanner = new Planner(planner.getSemester());
        assertEquals(expectedPlanner, planner);
    }

    @Test
    public void equals() {
        Planner planner = new Planner();
        Planner expectedPlanner = new Planner(planner);
        assertEquals(expectedPlanner, planner);
        assertEquals(expectedPlanner.hashCode(), planner.hashCode());
    }
}
