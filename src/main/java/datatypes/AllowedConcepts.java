/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package datatypes;

import edu.stanford.nlp.util.StringUtils;
import tools.processors.TextMiner;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 *
 * @author epcpu
 */
public class AllowedConcepts {
    
    /**
     * list of relevant terms in parts of a relevant question
     */
    private ArrayList<String> allowed;

    public List<String> getAllowed() {
        return allowed;
    }

    public void setAllowed(ArrayList<String> allowed) {
        this.allowed = allowed;
    }
    
    public void setAllowed(HashSet<String> allow) {
        
        allowed = new ArrayList<>(allow);
    }
    
    public void addNewElementsAndRemoveDuplicates(HashSet<String> appendList) {
        this.allowed.addAll(appendList);
        this.allowed = new ArrayList<>(TextMiner.removeDuplicates(StringUtils.join(this.allowed)));
    }
    
}
