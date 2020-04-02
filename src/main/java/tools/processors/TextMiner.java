package tools.processors;

import datatypes.FVEstimator;
import datatypes.IDF;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.util.CoreMap;

import java.io.PrintStream;
import java.util.*;

/**
 * Created with IntelliJ IDEA. User: epcpu Date: 12/16/14 Time: 9:22 PM To
 * change this template use File | Settings | File Templates.
 */
public class TextMiner<T> {

    private static StanfordCoreNLP pipeline;
    private static PrintStream jout = System.out;
    private HashMap<String, T> df = new HashMap<>();
    private int maxTf;
    private ArrayList<String> stopWords = new ArrayList<>();

    public TextMiner(HashMap<String, T> df) {
        this.df = df;
    }

    public TextMiner(ArrayList<String> stopWords) {
        this.stopWords = stopWords;
    }

    public TextMiner() {

    }

    static {
        try {
            Properties props = new Properties();
//            props.setProperty("annotators", "tokenize, ssplit");//, pos, lemma, parse, sentiment");
            props.setProperty("annotators", "tokenize, ssplit,");// pos, lemma, ner");//, parse, sentiment");
            pipeline = new StanfordCoreNLP(props);
        } catch (Exception ex) {
            jout.println(ex);
        }
    }

    //    public static void main(String[] args) {
////        jout.println(TextMiner.getNumberofSentences("Hello! I'm here to appoint a new thread of living. And what are you doing here?"));
//        jout.println(TextMiner.lemmatize("Hello! I'm here to appoint a new thread of living. And what are you doing here?"));
//    }
//    private static void getSentiment() {
//        String text = "I am feeling so good";
//        Properties props = new Properties();
//        props.setProperty("annotators", "tokenize, ssplit, pos");//, parse, sentiment");
//        StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
//
//        Annotation annotation = pipeline.process(text);
//        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
//        for (CoreMap sentence : sentences) {
//            String sentiment = sentence.get(SentimentCoreAnnotations.ClassName.class);
//            System.out.println(sentiment + ": \t" + sentence);
//        }
//
//    }
    public static int getMaxSentenceLength(String comment) {

        ArrayList<String> sent = new ArrayList<>();
        int size = 0;
        String words = "";

        Annotation annotation = pipeline.process(comment);
        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                words += " " + token.get(CoreAnnotations.TextAnnotation.class);
            }
            if (words.length() > size) {
                size = words.length();
            }
            words = "";
        }

        return size;
    }

    public static ArrayList<String> getSentences(String comment) {
        ArrayList<String> sent = new ArrayList<>();
        int size = 0;
        String words = "";

        Annotation annotation = pipeline.process(comment);
        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                words += " " + token.get(CoreAnnotations.TextAnnotation.class);
            }
            sent.add(words);
            words = "";
        }

        return sent;
    }

    public static HashMap<String, String> getNER(String comment) {
        HashMap<String, String> nes = new HashMap<>();
        Annotation annotation = pipeline.process(comment);
        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                String ne = token.get(CoreAnnotations.NamedEntityTagAnnotation.class);
//                jout.println(token.value() + ": " + ne);
                if (!ne.equalsIgnoreCase("O")) {
                    nes.put(token.value(), ne);
                }
            }
        }
        return nes;
    }

    public static void main(String[] args) {
        String str = "I've done it once at the Sharq Village &amp; Spa ... It's great";
        TextMiner.getNER(str);
    }

    public static int getNumberofSentences(String comment) {

        Annotation annotation = pipeline.process(comment);
        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        return sentences.size();
    }

    public static int getSentiment(String text) {
        Annotation annotation = pipeline.process(text);
        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        int sense = 0;
        for (CoreMap sentence : sentences) {
            String sentiment = sentence.get(SentimentCoreAnnotations.SentimentClass.class);
//            sense += sentiment.sent;
            if (sentiment != null) {
                if (sentiment.equalsIgnoreCase("Very negative")) {
                    sense -= 2;
                } else if (sentiment.equalsIgnoreCase("Negative")) {
                    sense -= 1;
                } else if (sentiment.equalsIgnoreCase("Positive")) {
                    sense += 1;
                } else if (sentiment.equalsIgnoreCase("Very positive")) {
                    sense += 2;
                }
            }
//            System.out.println(sent + ": \t" + sentence);
        }
//        jout.println(sense);
        return sense;
    }

    public static List<String> lemmatize(String documentText) {
        List<String> lemmas = new LinkedList<>();
        Annotation annotation = pipeline.process(documentText);
        List<CoreMap> sentences = annotation.get(CoreAnnotations.SentencesAnnotation.class);
        for (CoreMap sentence : sentences) {
            for (CoreLabel token : sentence.get(CoreAnnotations.TokensAnnotation.class)) {
                lemmas.add(token.get(CoreAnnotations.LemmaAnnotation.class));
            }
        }
        return lemmas;
    }

    /////////

    /**
     * It estimates simpleCosineSimilarity between two input strings while
     * tokens weight is assumed to be 0/1.
     *
     * @param strFirst
     * @param strSecond
     * @return
     */
    public double simpleCosineSimilarity(String strFirst, String strSecond) {

        int count = 0;
        HashSet<String> firstStrTokens = new HashSet<>(Arrays.asList(strFirst.toLowerCase().split(" ")));
        HashSet<String> secondStrTokens = new HashSet<>(Arrays.asList(strSecond.toLowerCase().split(" ")));

        for (Iterator<String> iter = secondStrTokens.iterator(); iter.hasNext(); ) {
            String str = iter.next();
            if (firstStrTokens.contains(str)) {
                count++;
            }
        }
        return count / Math.sqrt(Math.pow(firstStrTokens.size(), 2) + Math.pow(secondStrTokens.size(), 2));
    }

    public double freqCosineSimilarity(String strFirst, String strSecond) {

        int soratKasr = 0;
        HashMap<String, Double> firstStrTokensFreq = this.wordCount(strFirst.toLowerCase().split(" "));
        HashMap<String, Double> secondStrTokensFreq = this.wordCount(strSecond.toLowerCase().split(" "));

        for (String str : firstStrTokensFreq.keySet()) {
            if (secondStrTokensFreq.containsKey(str)) {
                soratKasr += secondStrTokensFreq.get(str) * firstStrTokensFreq.get(str);
            }
        }
        return soratKasr / Math.sqrt(this.vectorLengthInPoweredOfTwo(firstStrTokensFreq) + this.vectorLengthInPoweredOfTwo(secondStrTokensFreq));
    }

    public double freqCosineSimilarity(String strFirst, String strSecond, HashSet<String> irreleventTerms) {

        int soratKasr = 0;
        HashMap<String, Double> firstStrTokensFreq = this.wordCount(strFirst.toLowerCase().split(" "));
        HashMap<String, Double> secondStrTokensFreq = this.wordCount(strSecond.toLowerCase().split(" "));

        for (String str : firstStrTokensFreq.keySet()) {
            if (secondStrTokensFreq.containsKey(str)) {
                if (!irreleventTerms.contains(str)) {
                    soratKasr += secondStrTokensFreq.get(str) * firstStrTokensFreq.get(str);
                } else {
                    soratKasr -= secondStrTokensFreq.get(str) * firstStrTokensFreq.get(str);
                }
            }
        }
        return soratKasr / Math.sqrt(this.vectorLengthInPoweredOfTwo(firstStrTokensFreq) + this.vectorLengthInPoweredOfTwo(secondStrTokensFreq));
    }

    public double tfIdfCosineSimilarity(String strFirst, String strSecond) {

        int soratKasr = 0;
        HashMap<String, Double> firstStrTokensFreq = new HashMap<>();
        HashMap<String, Double> secondStrTokensFreq = new HashMap<>();
        firstStrTokensFreq = this.wordCount(strFirst.toLowerCase().split(" "));
        secondStrTokensFreq = this.wordCount(strSecond.toLowerCase().split(" "));
        ////////////////////////used for simple tf df method
        firstStrTokensFreq = this.simpleTfIdfCalculation(firstStrTokensFreq);
        secondStrTokensFreq = this.simpleTfIdfCalculation(secondStrTokensFreq);
        ////////////////////////used for my new tf df method
//        firstStrTokensFreq = this.newTfIdfCalculation(firstStrTokensFreq);
//        secondStrTokensFreq = this.newTfIdfCalculation(secondStrTokensFreq);
        ////////////////////////
        for (String str : firstStrTokensFreq.keySet()) {
            if (secondStrTokensFreq.containsKey(str)) {
                soratKasr += secondStrTokensFreq.get(str) * firstStrTokensFreq.get(str);
            }
        }
        return soratKasr / Math.sqrt(this.vectorLengthInPoweredOfTwo(firstStrTokensFreq) + this.vectorLengthInPoweredOfTwo(secondStrTokensFreq));
    }

    private HashMap<String, Double> simpleTfIdfCalculation(HashMap<String, Double> inputTf) {

        for (String str : inputTf.keySet()) {
            if (df.containsKey(str)) {
                inputTf.put(str, (inputTf.get(str) / maxTf)
                        * (1 + Math.log(FVEstimator.getSampleNumbers() / (int) (Object) df.get(str))));
            }
        }
        return inputTf;
    }

    private HashMap<String, Double> newTfIdfCalculation(HashMap<String, Double> inputTf) {

        for (String str : inputTf.keySet()) {
            if (df.containsKey(str)) {
                IDF i = (IDF) df.get(str);
                inputTf.put(str, (inputTf.get(str) / maxTf) * (1 + Math.log(FVEstimator.getCategorySize() / i.getCategoricalIDF().size())));
            }
        }
        return inputTf;
    }

    public HashMap<String, Double> wordCount(String[] inputString) {
        HashMap<String, Double> strTokensFreq = new HashMap<>();
        for (String str : inputString) {
            if (!strTokensFreq.containsKey(str)) {
                strTokensFreq.put(str, 1.0);
            } else {
                strTokensFreq.put(str, strTokensFreq.get(str) + 1.0);
            }
        }
        return strTokensFreq;
    }

    public int wordCountForaWord(String[] baseString, String searchString) {
        int num = 0;
        LinkedList<String> terms = new LinkedList<String>(Arrays.asList(baseString));

        while (terms.contains(searchString)) {
            num++;
            terms.remove(terms.indexOf(searchString));
        }

        return num;
    }

    public static HashSet<String> removeDuplicates(String inputString) {

        return new HashSet(Arrays.asList(inputString.toLowerCase().split(" ")));
    }

    public HashSet<String> removeStopWords(HashSet<String> words) throws Exception {

        HashSet<String> wordsTemp = (HashSet<String>) words.clone();
        for (String term : words) {
            if (stopWords.contains(term.toLowerCase())) {
                wordsTemp.remove(term);
            }
        }
        return wordsTemp;
    }

    public String removeStopWords(String words) throws Exception {

        int index1, index2 = 0, index3 = 0;
        words = words.trim();
        String stopWordFreeString = "";
        for (String term : words.split(" ")) {
            if (!stopWords.contains(term.toLowerCase())) {
                stopWordFreeString += term + " ";
//                while((index1 = words.indexOf(" " + term + " ")) != -1 || (index2 = words.indexOf(term + " ")) != -1 || (index3 = words.indexOf(" " + term)) != -1) {
//                    if(index1 != -1) {
//                        words = words.substring(0, index1) + words.substring(index1 + term.length() + 1, words.length());
//                    } else if(index2 != -1) {
//                        words = words.substring(0, index2) + words.substring(index2 + term.length() + 1, words.length());
//                    } else if(index3 != -1) {
//                        words = words.substring(0, index3) + words.substring(index3 + term.length() + 1, words.length());
//                    }
//                    words = words.trim();
//                }
//                words = words.replaceAll("[" +term + "]", "");
            }
        }
        return stopWordFreeString.trim();
    }

    private double vectorLengthInPoweredOfTwo(HashMap vector) {
        double length = 0;
        for (Object obj : vector.keySet()) {
            length += Math.pow((double) vector.get(obj), 2);
        }
        return length;
    }

    public int getMaxTf() {
        return maxTf;
    }

    public void setMaxTf(int maxTf) {
        this.maxTf = maxTf;
    }

}
