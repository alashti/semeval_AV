package datatypes;

import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: epcpu
 * Date: 12/20/14
 * Time: 9:30 AM
 * To change this template use File | Settings | File Templates.
 */
public class UserReputation {
    private LinkedList<String> Reputations = new LinkedList<>();

    public LinkedList<String> getReputations() {
        return Reputations;
    }

    public void setReputations(LinkedList<String> reputations) {
        Reputations = reputations;
    }

}
