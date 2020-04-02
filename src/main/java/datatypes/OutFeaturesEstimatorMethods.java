/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datatypes;

import java.util.Hashtable;
import java.util.StringTokenizer;

/**
 *
 * @author Amir Hossein
 */
public class OutFeaturesEstimatorMethods {

    public static float sentenceLMEstimator(String sentence, Hashtable<String, Float> lm) {

        float lmValue = 0;
        String[] temp = sentence.split(" ");
        String left = "", right = "";
        for (StringTokenizer stringTokenized = new StringTokenizer(sentence); stringTokenized.hasMoreTokens();) {

            left = stringTokenized.nextToken();
            if(!stringTokenized.hasMoreTokens()) {
                break;
            }
            right = stringTokenized.nextToken();
            float prob = 0;
            if(lm.containsKey(left + " " + right)) {
                prob = lm.get(left + " " + right);
            } else {
                prob = 1;
            }
            lmValue += Math.log10(prob);
        }
        return lmValue;
    }
}
