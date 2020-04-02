/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import datatypes.Comment;
import datatypes.Question;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * @author epcpu
 */
public class test {

    static PrintStream jout = System.out;

    private static void datasetAnalysis(ArrayList<Question> ques) {

        HashMap<String, Integer> trend = new HashMap<>();
        for (Question q : ques) {
            jout.println("Question Id: " + q.getQID());
            for (Comment c : q.getComments()) {
                if (!c.getCUSERID().equalsIgnoreCase(q.getQUserId())) {
                    if (!trend.containsKey(c.getCGOLD())) {
                        trend.put(c.getCGOLD(), 1);
                    } else {
                        trend.put(c.getCGOLD(), trend.get(c.getCGOLD()) + 1);
                    }
                } else if (!trend.isEmpty()) {
                    for (String CGold : trend.keySet()) {
                        jout.println(CGold + "-> #" + trend.get(CGold));
                    }
                    trend.clear();
                }
            }
        }
    }
}
