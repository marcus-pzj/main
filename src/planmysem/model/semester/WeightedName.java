//@@author marcus-pzj
package planmysem.model.semester;
/**
 * WeightedName of integer and string
 */

public class WeightedName {
    private int dist;
    private String name;

    public WeightedName(String name, int dist) {
        this.name = name;
        this.dist = dist;
    }

    public int getDist() {
        return this.dist;
    }

    public String getName() {
        return name;
    }
}
