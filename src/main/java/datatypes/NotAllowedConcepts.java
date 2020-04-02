/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datatypes;

import java.util.ArrayList;
import java.util.HashSet;

/**
 *
 * @author epcpu
 */
public class NotAllowedConcepts {

    /**
     * list of relevant terms in parts of a sub-question of a questioner's
     * comment which must not be answered in other users' comments.
     */
    private ArrayList<String> notAllowed = new ArrayList<>();

    public ArrayList<String> getNotAllowed() {
        return notAllowed;
    }

    public void setNotAllowed(ArrayList<String> notAllowed) {
        this.notAllowed = notAllowed;
    }

    public void setNotAllowed(HashSet<String> allow) {
        notAllowed = new ArrayList<>(allow);
    }
}
