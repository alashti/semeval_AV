/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools.processors;

import ch.lambdaj.Lambda;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.String;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

/**
 *
 * @author epcpu
 */
public class rake {

    public static boolean isNumber(String s) {

        try {
            if (s.contains(".")) {
                Float.parseFloat(s);
                return true;
            } else {
                Integer.parseInt(s);
                return true;
            }
        } catch (Exception ex) {
            return false;
        }
    }

    public static float convertNumber(String str) {

        if (str.contains(".")) {
            return Float.parseFloat(str);
        } else {
            return Integer.parseInt(str);
        }
    }

    public static ArrayList<String> loadStopWords(String stopWordsFileAddress) throws
            FileNotFoundException, IllegalArgumentException, IllegalAccessException,
            InvocationTargetException, SecurityException, NoSuchMethodException, IOException {
        ArrayList<String> stopWords = new ArrayList<>();
        BufferedReader bf = tools.util.file.Reader.getFileBufferReader(stopWordsFileAddress);
        String str;
        while ((str = bf.readLine()) != null) {
            stopWords.add(str);
        }
        return stopWords;
    }

    public static ArrayList<String> separateWords(String text, int minWordReturnSize) {
        Pattern splitter = Pattern.compile("[^a-zA-Z0-9_\\+\\-/]");
        ArrayList<String> words = new ArrayList<>();
        String curWord = "";
        for (String str : splitter.split(text)) {

            if (curWord.length() > minWordReturnSize && !curWord.equals("")
                    && !isNumber(curWord)) {
                words.add(curWord);
            }
        }
        return words;
    }

    public static ArrayList<String> splitSentences(String text) {

        Pattern sentenceSplitter = Pattern.compile("u[.!?,;:\\t\\\\-\\\\\"\\\\(\\\\)\\\\\\'\\u2019\\u2013]");
        String[] senteces = sentenceSplitter.split(text);
        return new ArrayList<>(Arrays.asList(senteces));
    }

    public static String buildStopWordRegex(String stopWordFilePath) throws Exception {

        ArrayList<String> loadStopWords = loadStopWords(stopWordFilePath);
        ArrayList<String> regex = new ArrayList<>();
        for (String word : loadStopWords) {
            String wordRegex = "\\b" + word + "\\b";
            regex.add(wordRegex);
        }
        Pattern stopWordPattern = Pattern.compile("|".concat(stopWordFilePath),
                Pattern.CASE_INSENSITIVE);
        return stopWordPattern.pattern();
    }

    public static ArrayList<String> generateCandidateKeywords(ArrayList<String> senteList, String stopWordPattern) {

        ArrayList<String> phraseList = new ArrayList<>();
        for (String sent : senteList) {
            sent = sent.replaceAll(stopWordPattern, "|");
            String[] phrases = sent.split("|");
            for (String phrase : phrases) {
                phrase = Lambda.join(phrase.toLowerCase().split(" "), " ");
                if (!phrase.equals("")) {
                    phraseList.add(phrase);
                }
            }
        }
        return phraseList;
    }

    public static HashMap<String, Integer> calculateWordScores(ArrayList<String> phraseList) {

        HashMap<String, Integer> wordFreq = new HashMap<>();
        HashMap<String, Integer> wordDegree = new HashMap<>();
        ArrayList<String> separateWords = new ArrayList<>();
        for (String phrase : phraseList) {
            separateWords = separateWords(phrase, 0);
            int len = separateWords.size();
            int wordListDegree = len - 1;
            for (String word : separateWords) {
                try {
                    wordFreq.put(word, wordFreq.get(word) + 1);
                } catch (Exception ex) {
                    wordFreq.put(word, 1);
                }
                try {
                    wordDegree.put(word, wordDegree.get(word) + wordListDegree);
                } catch (Exception ex) {
                    wordDegree.put(word, wordListDegree);
                }
            }
        }
        for (String word : wordFreq.keySet()) {
            wordDegree.put(word, wordDegree.get(word) + wordFreq.get(word));
        }
        HashMap<String, Integer> wordScore = new HashMap<>();
        for (String word : wordFreq.keySet()) {
            try {
                wordScore.put(word, wordDegree.get(word) / (wordFreq.get(word)));
            } catch (Exception ex) {
                wordScore.put(word, 0);
            }
        }
        return wordScore;
    }

    public static HashMap<String, Integer> generateCandidateKeywordScores(ArrayList<String> phraseList,
            HashMap<String, Integer> wordScore) {

        HashMap<String, Integer> keywordCandidates = new HashMap<>();
        for (String phrase : phraseList) {
            keywordCandidates.put(phrase, 0);
            ArrayList<String> wordList = separateWords(phrase, 0);
            int candidateScore = 0;
            for (String word : wordList) {
                candidateScore += wordScore.get(word);
            }
            keywordCandidates.put(phrase, candidateScore);
        }
        return keywordCandidates;
    }
}

class Runner {

    private final String StopWordsPath;
    private final String stopWordsPattern;

    public Runner(String StopWordsPath) throws Exception {
        this.StopWordsPath = StopWordsPath;
        this.stopWordsPattern = rake.buildStopWordRegex(StopWordsPath);
    }

    private Comparator<Map.Entry<String, Integer>> byMapValues = new Comparator<Map.Entry<String, Integer>>() {

        @Override
        public int compare(Map.Entry<String, Integer> o1, Map.Entry<String, Integer> o2) {
            return o1.getValue().compareTo(o2.getValue());
        }
    };

    public List<Map.Entry<String, Integer>> run(String text) {
        ArrayList<String> senteceList = rake.splitSentences(text);

        ArrayList<String> phraseList = rake.generateCandidateKeywords(senteceList,
                this.stopWordsPattern);

        HashMap<String, Integer> wordScore = rake.calculateWordScores(phraseList);

        HashMap<String, Integer> keywordCandidates = rake.generateCandidateKeywordScores(phraseList, wordScore);

        List<Map.Entry<String, Integer>> keywordCandids = new ArrayList<Map.Entry<String, Integer>>();
        keywordCandids.addAll(keywordCandidates.entrySet());
        Collections.sort(keywordCandids, byMapValues);
        return keywordCandids;
    }
    
    public static void main(String[] args) throws Exception {
        
        String text = "Compatibility of systems of linear constraints over the set of natural numbers. Criteria of compatibility of a system of linear Diophantine equations, strict inequations, and nonstrict inequations are considered. Upper bounds for components of a minimal set of solutions and algorithms of construction of minimal generating sets of solutions for all types of systems are given. These criteria and the corresponding algorithms for constructing a minimal supporting set of solutions can be used in solving all the considered types of systems and systems of mixed types.";
        ArrayList<String> sentenceList = rake.splitSentences(text);
        String stopPath = "resources/SmartStoplist.txt";
        String stopWordRegex = rake.buildStopWordRegex(stopPath);
        ArrayList<String> phraseList = rake.generateCandidateKeywords(sentenceList, stopPath);
        HashMap<String, Integer> wordScores = rake.calculateWordScores(phraseList);
        HashMap<String, Integer> keywordCandidateScores = rake.generateCandidateKeywordScores(phraseList, wordScores);
        System.out.println(keywordCandidateScores.size());
        System.out.println("[");
        for(String str : keywordCandidateScores.keySet()) {
            System.out.println("(" + str + ", " + keywordCandidateScores.get(str) + "), ");
        }
        System.out.println("]");
    }

}
