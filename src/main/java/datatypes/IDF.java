/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package datatypes;

import java.util.HashMap;

/**
 * @author epcpu
 */
public class IDF {
    private String term;
    private HashMap<String, Integer> categoricalIDF = new HashMap<>();
    private int maxFreqCategory;

    public int getMaxFreqCategory() {
        return maxFreqCategory;
    }

    public void setMaxFreqCategory(int maxFreqCategory) {
        this.maxFreqCategory = maxFreqCategory;
    }

    public void setMaxFreqCategory() {

        int max = 0;
        for (String cat : this.categoricalIDF.keySet()) {
            if (this.categoricalIDF.get(cat) > max) {
                max = this.categoricalIDF.get(cat);
            }
        }
        this.setMaxFreqCategory(max);
    }

    public String getTerm() {
        return term;
    }

    public void setTerm(String term) {
        this.term = term;
    }

    public HashMap<String, Integer> getCategoricalIDF() {
        return categoricalIDF;
    }

    public void setCategoricalIDF(HashMap<String, Integer> categoricalIDF) {
        this.categoricalIDF = categoricalIDF;
    }

}
