package datatypes;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import tools.util.file.BufferedIterator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

/**
 * Created by Saeed on 12/22/14.
 */
public class SentenceSimilarity {
    private ArrayList<String> sentences;
    private Multiset<String> df;

    public SentenceSimilarity(String fileAddress) throws IOException {
        this.df = HashMultiset.create();
        this.sentences = new ArrayList<>();
        tools.util.file.BufferedIterator bufferedIterator = new BufferedIterator(tools.util.file.Reader.getFileBufferReader(fileAddress));
        String newSentence;
        while (bufferedIterator.hasNext()) {
            newSentence = bufferedIterator.next();
            addSentence(newSentence);
        }
        bufferedIterator.close();
        System.out.println(this.getClass().getName() + ": " + sentences.size() + " sentence load complete with " + this.df.size() + " distinct token.");
    }

    private void addSentence(String sentence) {
        if (sentence.trim().length() == 0)
            return;
        this.sentences.add(sentence);
        HashSet<String> tokens = new HashSet<String>();
        for (String token : sentence.split("\\s+")) {
            tokens.add(token);
        }
        for (String token : tokens) {
            df.add(token, 1);
        }
    }

    public int getDF(String token) {
        return this.df.count(token);
    }

    public HashMap<String, Double> getDocument(String inText) {
        HashMap<String, Double> result = new HashMap<String, Double>();
        HashMap<String, Integer> tempIntegerResult = new HashMap<String, Integer>();
        for (String token : inText.split("\\s+")) {
            if (result.containsKey(token)) {
                tempIntegerResult.put(token, tempIntegerResult.get(token) + 1);
            } else {
                tempIntegerResult.put(token, 1);
            }
        }
        double maxTF = 0;
        for (Map.Entry<String, Integer> entry : tempIntegerResult.entrySet()) {
            if (entry.getValue() > maxTF)
                maxTF = entry.getValue();
        }
        for (Map.Entry<String, Integer> entry : tempIntegerResult.entrySet()) {
            result.put(entry.getKey(), entry.getValue() / maxTF);
        }
        return result;
    }

    public double getTFIDFCosinSimilarity(String sentence1, String sentence2) {
        double similarityScore = 0.;
        HashMap<String, Double> sentence1Vector = getDocument(sentence1);
        HashMap<String, Double> sentence2Vector = getDocument(sentence2);
        HashSet<String> sentence2VectorOvarlappedWithSentence1Token = new HashSet<String>(sentence1Vector.size());
        for (Map.Entry<String, Double> entry : sentence2Vector.entrySet()) {
            if (sentence1Vector.containsKey(entry.getKey())) {
                sentence2VectorOvarlappedWithSentence1Token.add(entry.getKey());
            }
        }
        double sumScore = 0;
        double sumSentence1SquaredSize = 0;
        for (Map.Entry<String, Double> entry : sentence1Vector.entrySet()) {
            double temp = entry.getValue() * getIDF(entry.getKey());
            sumSentence1SquaredSize += temp * temp;
        }
        double sumSentence2SquaredSize = 0;
        for (Map.Entry<String, Double> entry : sentence2Vector.entrySet()) {
            double temp = entry.getValue() * getIDF(entry.getKey());
            sumSentence2SquaredSize += temp * temp;
        }
        for (String overlappedToken : sentence2VectorOvarlappedWithSentence1Token) {
            double sentence1Score = sentence1Vector.get(overlappedToken)
                    * getIDF(overlappedToken);
            double sentence2Score = sentence2Vector.get(overlappedToken)
                    * getIDF(overlappedToken);
            sumScore += sentence1Score * sentence2Score;
        }
        return sumScore / (Math.sqrt(sumSentence1SquaredSize) * Math.sqrt(sumSentence2SquaredSize));
    }

    private double getIDF(String documentFrequency) {
        int docFreq = df.count(documentFrequency);
        return (1 + (Math.log((double) this.sentences.size() / docFreq)));
    }
}
