package datatypes;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Multiset;
import tools.util.file.BufferedIterator;

import java.io.IOException;
import java.util.*;

/**
 * Created by Saeed on 12/22/14.
 */
public class SentenceLanguageModelSimilarity {

    private final double DELTA = 0.00100;
    private ArrayList<String> sentences;
    private Multiset<String> tokenDf;
    private Multiset<String> tokenTokenDf;
    private Multiset<String> tokenTokenTokenDf;

    public SentenceLanguageModelSimilarity(String fileAddress) throws IOException {
        this.tokenDf = HashMultiset.create();
        this.tokenTokenDf = HashMultiset.create();
        this.tokenTokenTokenDf = HashMultiset.create();
        this.sentences = new ArrayList<String>();
        BufferedIterator bufferedIterator = new BufferedIterator(tools.util.file.Reader.getFileBufferReader(fileAddress));
        String newSentence;
        int lineCounter = 0;
        while (bufferedIterator.hasNext()) {
            lineCounter++;
            newSentence = bufferedIterator.next();
            if (newSentence.trim().length() == 0) {
                continue;
            }
            this.sentences.add(newSentence);
            String[] tokens = newSentence.split("\\s+");
            addTokenSentence(tokens);
            addTokenTokenSentence(tokens);
            addTokenTokenTokenSentence(tokens);
            if (lineCounter % 10000 == 0) {
                System.out.println("loading ... " + lineCounter + " line load complete.");
            }
        }
        bufferedIterator.close();
        System.out.println(this.getClass().getName() + ": " + sentences.size() + " sentence load complete with " + this.tokenDf.size() + " distinct token.");
    }

    private void addTokenSentence(String[] tokens) {
        HashSet<String> distinctClauses = new HashSet<>(Arrays.asList(tokens));
        this.tokenDf.addAll(distinctClauses);
    }

    private void addTokenTokenSentence(String[] tokens) {
        HashSet<String> distinctClauses = new HashSet<>();
        for (int i = 0; i < tokens.length - 1; i++) {
            distinctClauses.add(tokens[i] + " " + tokens[i + 1]);
        }
        this.tokenTokenDf.addAll(distinctClauses);
    }

    private void addTokenTokenTokenSentence(String[] tokens) {
        HashSet<String> distinctClauses = new HashSet<>();
        for (int i = 0; i < tokens.length - 2; i++) {
            distinctClauses.add(tokens[i] + " " + tokens[i + 1] + " " + tokens[i + 2]);
        }
        this.tokenTokenTokenDf.addAll(distinctClauses);
    }

    public int getTokenDF(String token) {
        return this.tokenDf.count(token);
    }

    public int getTokenTokenDF(String token) {
        return this.tokenTokenDf.count(token);
    }

    public int getTokenTokenTokenDF(String token) {
        return this.tokenTokenTokenDf.count(token);
    }

    public HashMap<String, Double> getTokenVector(String inText) {
        HashMap<String, Integer> tempIntegerresult = new HashMap<>();
        for (String token : inText.split("\\s+")) {
            if (tempIntegerresult.containsKey(token)) {
                tempIntegerresult.put(token, tempIntegerresult.get(token) + 1);
            } else {
                tempIntegerresult.put(token, 1);
            }
        }
        return getVector(tempIntegerresult);
    }

    public HashMap<String, Double> getTokenTokenVector(String inText) {
        HashMap<String, Integer> tempIntegerresult = new HashMap<>();
        String[] clauses = inText.split("\\s+");
        for (int i = 0; i < clauses.length - 1; i++) {
            String clause = clauses[i] + " " + clauses[i + 1];
            if (tempIntegerresult.containsKey(clause)) {
                tempIntegerresult.put(clause, tempIntegerresult.get(clause) + 1);
            } else {
                tempIntegerresult.put(clause, 1);
            }
        }
        return getVector(tempIntegerresult);
    }

    public HashMap<String, Double> getTokenTokenTokenVector(String inText) {
        HashMap<String, Integer> tempIntegerresult = new HashMap<>();
        String[] clauses = inText.split("\\s+");
        for (int i = 0; i < clauses.length - 2; i++) {
            String clause = clauses[i] + " " + clauses[i + 1] + " " + clauses[i + 2];
            if (tempIntegerresult.containsKey(clause)) {
                tempIntegerresult.put(clause, tempIntegerresult.get(clause) + 1);
            } else {
                tempIntegerresult.put(clause, 1);
            }
        }
        return getVector(tempIntegerresult);
    }

    private HashMap<String, Double> getVector(HashMap<String, Integer> tempIntegerresult) {
        HashMap<String, Double> result = new HashMap<>();
        double maxTF = 0;
        for (Map.Entry<String, Integer> entry : tempIntegerresult.entrySet()) {
            if (entry.getValue() > maxTF) {
                maxTF = entry.getValue();
            }
        }
        for (Map.Entry<String, Integer> entry : tempIntegerresult.entrySet()) {
            result.put(entry.getKey(), entry.getValue() / maxTF);
        }
        return result;
    }

    public double getTokenTFIDFCosinSimilarity(String sentence1, String sentence2) {
        HashMap<String, Double> sentence1Vector = getTokenVector(sentence1);
        HashMap<String, Double> sentence2Vector = getTokenVector(sentence2);
        HashSet<String> sentence2VectorOvarlappedWithSentence1Token = new HashSet<>(sentence1Vector.size());
        for (Map.Entry<String, Double> entry : sentence2Vector.entrySet()) {
            if (sentence1Vector.containsKey(entry.getKey())) {
                sentence2VectorOvarlappedWithSentence1Token.add(entry.getKey());
            }
        }
        double sumScore = 0;
        double sumSentence1SquaredSize = 0;
        for (Map.Entry<String, Double> entry : sentence1Vector.entrySet()) {
            double temp = entry.getValue() * getTokenIDF(entry.getKey());
            sumSentence1SquaredSize += temp * temp;
        }
        double sumSentence2SquaredSize = 0;
        for (Map.Entry<String, Double> entry : sentence2Vector.entrySet()) {
            double temp = entry.getValue() * getTokenIDF(entry.getKey());
            sumSentence2SquaredSize += temp * temp;
        }
        for (String overlappedToken : sentence2VectorOvarlappedWithSentence1Token) {
            double sentence1Score = sentence1Vector.get(overlappedToken)
                    * getTokenIDF(overlappedToken);
            double sentence2Score = sentence2Vector.get(overlappedToken)
                    * getTokenIDF(overlappedToken);
            sumScore += sentence1Score * sentence2Score;
        }
        return sumScore / (Math.sqrt(sumSentence1SquaredSize) * Math.sqrt(sumSentence2SquaredSize));
    }

    public double getLanguageModelScore(String sentence, double k1, double k2, double k3) {
        return k1 * getUnigramScore(sentence) + k2 * getBigramScore(sentence) + k3 * getTrigramScore(sentence);
    }

    public double getUnigramScore(String sentence) {

        HashMap<String, Double> sentenceTokenVector = getTokenVector(sentence);

        double sumScore = 0;

        final double DOCUMENTVOCABSIZE = this.tokenDf.size();
        for (Map.Entry<String, Double> entry : sentenceTokenVector.entrySet()) {
            sumScore += (entry.getValue() + DELTA) / (DOCUMENTVOCABSIZE + 1);
        }

        return sumScore;
    }

    public double getBigramScore(String sentence) {

        HashMap<String, Double> sentenceTokenVector = getTokenVector(sentence);
        HashMap<String, Double> sentenceTokenTokenVector = getTokenTokenVector(sentence);

        double sumScore = 0;

        for (Map.Entry<String, Double> entry : sentenceTokenTokenVector.entrySet()) {
            sumScore += (entry.getValue() + DELTA) / (sentenceTokenVector.get(entry.getKey().split(" ")[0]) + 1);
        }

        return sumScore;
    }

    public double getTrigramScore(String sentence) {

        HashMap<String, Double> sentenceTokenTokenVector = getTokenTokenVector(sentence);
        HashMap<String, Double> sentenceTokenTokenTokenVector = getTokenTokenTokenVector(sentence);

        double sumScore = 0;

        for (Map.Entry<String, Double> entry : sentenceTokenTokenTokenVector.entrySet()) {
            String[] splits = entry.getKey().split(" ");
            sumScore += (entry.getValue() + DELTA) / (sentenceTokenTokenVector.get(splits[0] + " " + splits[1]) + 1);
        }

        return sumScore;
    }

    private double getTokenIDF(String documentFrequency) {
        int docFreq = tokenDf.count(documentFrequency) + 1;
        double result = (1 + Math.log((double) this.sentences.size() / docFreq));
        return result;
    }

    public double getTokenTokenTFIDFCosinSimilarity(String sentence1, String sentence2) {
        double similarityScore = 0.;
        HashMap<String, Double> sentence1Vector = getTokenTokenVector(sentence1);
        HashMap<String, Double> sentence2Vector = getTokenTokenVector(sentence2);
        HashSet<String> sentence2VectorOvarlappedWithSentence1Token = new HashSet<>(sentence1Vector.size());
        for (Map.Entry<String, Double> entry : sentence2Vector.entrySet()) {
            if (sentence1Vector.containsKey(entry.getKey())) {
                sentence2VectorOvarlappedWithSentence1Token.add(entry.getKey());
            }
        }
        double sumScore = 0;
        double sumSentence1SquaredSize = 0;
        for (Map.Entry<String, Double> entry : sentence1Vector.entrySet()) {
            double temp = entry.getValue() * getTokenTokenIDF(entry.getKey());
            sumSentence1SquaredSize += temp * temp;
        }
        double sumSentence2SquaredSize = 0;
        for (Map.Entry<String, Double> entry : sentence2Vector.entrySet()) {
            double temp = entry.getValue() * getTokenTokenIDF(entry.getKey());
            sumSentence2SquaredSize += temp * temp;
        }
        for (String overlappedToken : sentence2VectorOvarlappedWithSentence1Token) {
            double sentence1Score = sentence1Vector.get(overlappedToken)
                    * getTokenTokenIDF(overlappedToken);
            double sentence2Score = sentence2Vector.get(overlappedToken)
                    * getTokenTokenIDF(overlappedToken);
            sumScore += sentence1Score * sentence2Score;
        }
        return sumScore / (Math.sqrt(sumSentence1SquaredSize) * Math.sqrt(sumSentence2SquaredSize));
    }

    private double getTokenTokenIDF(String documentFrequency) {
        int docFreq = tokenTokenDf.count(documentFrequency) + 1;
        double result = (1 + Math.log((double) this.sentences.size() / docFreq));
        return result;
    }

    public double getTokenTokenTokenTFIDFCosinSimilarity(String sentence1, String sentence2) {
        double similarityScore = 0.;
        HashMap<String, Double> sentence1Vector = getTokenTokenTokenVector(sentence1);
        HashMap<String, Double> sentence2Vector = getTokenTokenTokenVector(sentence2);
        HashSet<String> sentence2VectorOvarlappedWithSentence1Token = new HashSet<>(sentence1Vector.size());
        for (Map.Entry<String, Double> entry : sentence2Vector.entrySet()) {
            if (sentence1Vector.containsKey(entry.getKey())) {
                sentence2VectorOvarlappedWithSentence1Token.add(entry.getKey());
            }
        }
        double sumScore = 0;
        double sumSentence1SquaredSize = 0;
        for (Map.Entry<String, Double> entry : sentence1Vector.entrySet()) {
            double temp = entry.getValue() * getTokenTokenIDF(entry.getKey());
            sumSentence1SquaredSize += temp * temp;
        }
        double sumSentence2SquaredSize = 0;
        for (Map.Entry<String, Double> entry : sentence2Vector.entrySet()) {
            double temp = entry.getValue() * getTokenTokenIDF(entry.getKey());
            sumSentence2SquaredSize += temp * temp;
        }
        for (String overlappedToken : sentence2VectorOvarlappedWithSentence1Token) {
            double sentence1Score = sentence1Vector.get(overlappedToken)
                    * getTokenTokenIDF(overlappedToken);
            double sentence2Score = sentence2Vector.get(overlappedToken)
                    * getTokenTokenIDF(overlappedToken);
            sumScore += sentence1Score * sentence2Score;
        }
        return sumScore / (Math.sqrt(sumSentence1SquaredSize) * Math.sqrt(sumSentence2SquaredSize));
    }

    private double getTokenTokenTokenIDF(String documentFrequency) {
        int docFreq = tokenTokenTokenDf.count(documentFrequency) + 1;
        double result = (1 + Math.log((double) this.sentences.size() / docFreq));
        return result;
    }

    public double getInterpolatedLMValueForSemanticSimilarityBetweenStrings(String first, String second) {
        double uni, bi, tri;

        uni = this.getTokenTFIDFCosinSimilarity(first, second);
        bi = this.getTokenTokenTFIDFCosinSimilarity(first, second);
        tri = this.getTokenTokenTokenTFIDFCosinSimilarity(first, second);

        return ((3.3 * uni) + (3.4 * bi) + (3.3 * tri));
    }

    public double getInterpolatedLMScoreForAString(String str) {
        double uni, bi, tri;

        uni = this.getUnigramScore(str);
        bi = this.getBigramScore(str);
        tri = this.getTrigramScore(str);

        return ((3.3 * uni) + (3.4 * bi) + (3.3 * tri));
    }

}
