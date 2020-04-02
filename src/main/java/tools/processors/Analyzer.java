/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools.processors;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.Map;

import tools.util.file.Write;
import tools.util.sort.Collection;

/**
 * @author epcpu
 */
public class Analyzer {

    public static void getCharOccurencesTerm(char ch, String inputFileAddress, String termSpliterRegex, String resultFileAddress) throws IOException {
        PrintWriter pw = tools.util.file.Write.getPrintWriter(resultFileAddress, false);
        for (Map.Entry<String, Long> termTTF : tools.util.sort.Collection.mapSortedByValuesDecremental(Distribution.getFileTokenDistribution(inputFileAddress, termSpliterRegex))) {
            if (termTTF.getKey().indexOf(ch) >= 0) {
                pw.println(termTTF.getKey() + "\t" + termTTF.getValue());
            }
        }
        pw.close();
        System.out.println("Operation Complete.");
    }

    public static void getCharDistribution(String inputFileAddress, String outputFileAddress) throws IOException {
        tools.util.file.Write.mapToTextFileSortedByValue(Distribution.getFileCharDistribution(inputFileAddress), outputFileAddress, Collection.SortType.DECREMENTAL, Write.HashMapWriteType.KEYVALUE);
    }

    public static void getStringOccurencesTerm(String str, String inputFileAddress, String termSpliterRegex, String resultFileAddress) throws IOException {
        PrintWriter pw = tools.util.file.Write.getPrintWriter(resultFileAddress, false);
        for (Map.Entry<String, Long> termTTF : tools.util.sort.Collection.mapSortedByValuesDecremental(Distribution.getFileTokenDistribution(inputFileAddress, termSpliterRegex))) {
            if (termTTF.getKey().indexOf(str) >= 0) {
                pw.println(termTTF.getKey() + "\t" + termTTF.getValue());
            }
        }
        pw.close();
        System.out.println("Operation Complete.");
    }

    public static void getStringOccurencesBigram(String str, String inputFileAddress, String termSpliterRegex, String resultFileAddress) throws IOException {
        PrintWriter pw = tools.util.file.Write.getPrintWriter(resultFileAddress, false);
        for (Map.Entry<String, Long> termTTF : tools.util.sort.Collection.mapSortedByValuesDecremental(Distribution.getFileTokenTokenDistribution(inputFileAddress, termSpliterRegex))) {
            if (termTTF.getKey().indexOf(str) >= 0) {
                pw.println(termTTF.getKey() + "\t" + termTTF.getValue());
            }
        }
        pw.close();
        System.out.println("Operation Complete.");
    }

    public static void getPrefixTermOccurencesBigram(String prefixTerm, String inputFileAddress, String termSpliterRegex, String resultFileAddress) throws IOException {
        PrintWriter pw = tools.util.file.Write.getPrintWriter(resultFileAddress, false);
        for (Map.Entry<String, Long> termTTF : tools.util.sort.Collection.mapSortedByValuesDecremental(Distribution.getFileTokenTokenDistribution(inputFileAddress, termSpliterRegex))) {
            try {
                String term1 = termTTF.getKey().split(" ")[0];
                if (prefixTerm.equals(term1)) {
                    pw.println(termTTF.getKey() + "\t" + termTTF.getValue());
                }
            } finally {
                System.out.println(termTTF.getKey() + " " + termTTF.getValue());
            }
//            System.out.println(term1);

        }
        pw.close();
        System.out.println("Operation Complete.");
    }

    public static void getPostfixTermOccurencesBigram(String postfixTerm, String inputFileAddress, String termSpliterRegex, String resultFileAddress) throws IOException {
        PrintWriter pw = tools.util.file.Write.getPrintWriter(resultFileAddress, false);
        for (Map.Entry<String, Long> termTTF : tools.util.sort.Collection.mapSortedByValuesDecremental(Distribution.getFileTokenTokenDistribution(inputFileAddress, termSpliterRegex))) {
            String term2 = termTTF.getKey().split(" ")[1];
            if (postfixTerm.contains(term2) && term2.contains(postfixTerm)) {
                pw.println(termTTF.getKey() + "\t" + termTTF.getValue());
            }
        }
        pw.close();
        System.out.println("Operation Complete.");
    }

    public static void getEndStringOccurencesTermReplacedWithString(String str, String replacedStr, String inputFileAddress, String termSpliterRegex, String resultFileAddress) throws IOException {
        PrintWriter pw = tools.util.file.Write.getPrintWriter(resultFileAddress, false);
        for (Map.Entry<String, Long> termTTF : tools.util.sort.Collection.mapSortedByValuesDecremental(Distribution.getFileTokenDistribution(inputFileAddress, termSpliterRegex))) {
            int index = termTTF.getKey().indexOf(str);
            if (index >= 0)
                if (index == (termTTF.getKey().length() - str.length())) {
                    pw.println(termTTF.getKey() + "\t" + termTTF.getKey().substring(0, index) + " " + replacedStr);
                }
        }
        pw.close();
        System.out.println("Operation Complete.");
    }

}
