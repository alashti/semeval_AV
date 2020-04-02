package tools.processors;

import datatypes.AllowedConcepts;
import datatypes.Comment;
import datatypes.NotAllowedConcepts;
import datatypes.Question;
import edu.stanford.nlp.util.StringUtils;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map.Entry;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 *
 * @author epcpu
 */
public class Filtering {

    /**
     * this is an arraylist of some features of relevant qSentences which have
     * been extracted from question and relevant parts of qSentences of the
     * comments. (features: qSentences, qSentences' keywords, ...)
     */
    private ArrayList<AllowedConcepts> allowed = new ArrayList<>();
    /**
     * this is an arraylist of some features of irrelevant parts of comments a
     * question which are emerged by other users or even some parts of the
     * questioner's comments. e.g. there may be some new questions of course
     * some which are not related to the main concepts exists in the question
     * and those are not relevant. (then must not use some tools like wordnet or
     * wikipedia.)
     */
    private ArrayList<NotAllowedConcepts> notAllowed = new ArrayList<>();

    private Question q;
    private ArrayList<String> qSentences = new ArrayList<>();
    private ArrayList<String> cSentences = new ArrayList<>();
    private ArrayList<Integer> relevant = new ArrayList<>();//It keeps index of relevant sentences of a comment
    private ArrayList<Integer> irrelevant = new ArrayList<>();//It keeps index of relevant sentences of a comment
    private final double disSimilarityThreshold;
    private final double similarityThreshold;
    private ArrayList<String> stopWords;
//    boolean hasQMark = true;
    private HashSet<String> irrelevantTerms = new HashSet<>();

    public Filtering(Question que, ArrayList<String> stopWords) {
        this.disSimilarityThreshold = 0.1;
        this.similarityThreshold = 0.6;
        this.q = que;
        this.questionInitialize(this.q);

        this.stopWords = stopWords;
    }

    public void questionInitialize(Question q) {

        String questionContent;
        if (q.getQBody().contains(q.getQSubject())) {
            questionContent = q.getQBody();
        } else {
            questionContent = q.getQSubject() + ". " + q.getQBody();
        }
        questionContent = Preprocess.remove_junks(questionContent);
        qSentences.addAll(TextMiner.getSentences(questionContent));
//        if (!questionContent.contains("?")) {
//            hasQMark = false;
//        }
//        qSentences.addAll(TextMiner.getSentences(q.getQBody()));
    }

    public void commentInitialize(Comment com) {
        String commentContent = com.getCBODY();
//        if (!com.getCBODY().contains(com.getCSUBJECT()) && !q.getQSubject().contains(com.getCSUBJECT())) {
//            commentContent = com.getCSUBJECT() + " . " + com.getCBODY();
//        } else {
//            commentContent = com.getCBODY();
//        }
        commentContent = Preprocess.remove_junks(commentContent);
        Document doc = Jsoup.parse(commentContent);
        com.setNumImages(doc.getElementsByTag("img src=\"http:").size() + doc.getElementsByTag("img").size());
        com.setNumLinks(doc.getElementsByTag("a href=\"http:").size() + doc.getElementsByTag("a").size());
//        cSentences.addAll(TextMiner.getSentences(commentContent));
        cSentences.addAll(TextMiner.getSentences(doc.text()));
    }

    public void relevancyDetectorOfQuestions() throws Exception {

        TextMiner miner = new TextMiner(stopWords);
        for (String question : qSentences) {
            if (this.isQuestion(question)) {
                question = Preprocess.remove_junks(question);
//                question = StringUtils.join(TextMiner.lemmatize(question));
                String removeStopWords = miner.removeStopWords(question);
                removeStopWords = removeStopWords.replaceAll("\\s+", " ");
                AllowedConcepts concept = new AllowedConcepts();
                concept.setAllowed(new ArrayList(Arrays.asList(removeStopWords.split(" "))));
                allowed.add(concept);
            }
        }
    }

    public void relevancyDetectorOfComments(Comment com) throws Exception {

        TextMiner miner = new TextMiner(stopWords);
        int relevantNum = 0, irrelevantNum = 0;
        double degreeRelevants = 0, degreeIrrelevant = 0;

        if (!com.getCID().equals(q.getQID())) { //other users' comments
            double similarity;
            for (String comment : cSentences) {
                if (!this.isQuestion(comment)) {
                    similarity = this.findMostSimilarAllowedConceptSimilarity(comment);
                    if (similarity >= similarityThreshold) {
                        this.appendToMostSimilarAllowedQuestionConcept(comment);
                        relevantNum++;
                        degreeRelevants += similarity;
                        relevant.add(cSentences.indexOf(comment));
                    } else {
                        irrelevantNum++;
                        degreeIrrelevant += (1 - similarity);
                    }
                } else {
//                    similarity = this.findMostSimilarConceptSimilarity(comment);
//                    irrelevant++;
//                    degreeIrrelevant += (1 - similarity);
                }
            }
        } else { //questioner's comment
            double similarity = 0;
            for (String comment : cSentences) {
                if (this.isQuestion(comment)) {
                    similarity = this.findMostSimilarAllowedConceptSimilarity(comment);
                    if (similarity < similarityThreshold) {
                        NotAllowedConcepts notAllow = new NotAllowedConcepts();
                        notAllow.setNotAllowed(miner.removeStopWords(miner.removeDuplicates(comment)));
                        irrelevantNum++;
                        degreeIrrelevant += (1 - similarity);
                    } else {
                        this.appendToMostSimilarNotAllowedQuestionConcept(comment);
                        relevantNum++;
                        degreeRelevants += similarity;
//                        relevant.add(cSentences.indexOf(comment));
                    }
                } else {

                }
            }
        }
        com.setRelevancyCount((double) relevantNum / (double) cSentences.size());//deviding the relevants to the whole number of sentences can cause a decrease in accuracy
        com.setIrrelevancyCount((double) irrelevantNum / (double) cSentences.size());//deviding the relevants to the whole number of sentences can cause a decrease in accuracy
        com.setDegreeOfRelevancy(degreeRelevants);
        com.setDegreeOfIrrelevancy(degreeIrrelevant);
        String cBody = "";
        for (int sentIndex : relevant) {
            cBody += cSentences.get(sentIndex);
        }
        com.setCBODY(cBody);
    }

    public void relevancyDetectorOfCommentsToQuestion(Comment com) throws Exception {

        TextMiner miner = new TextMiner(stopWords);
        int relevants = 0, irrelevants = 0;
        double degreeRelevants = 0, degreeIrrelevant = 0;
        LinkedList presentConcepts = new LinkedList();

        double similarity = 0;
        for (String comment : cSentences) {
            if (!this.isQuestion(comment)) {
                comment = Preprocess.remove_junks(comment);
                String removeStopWords = miner.removeStopWords(comment);
                comment = comment.replaceAll("\\s\\s+", " ");
                Entry<AllowedConcepts, Double> alcon = this.find(comment);
                similarity = alcon.getValue();
                if (similarity >= similarityThreshold) {
                    relevants++;
                    degreeRelevants += similarity;
                    relevant.add(cSentences.indexOf(comment));
                    if (!presentConcepts.contains(alcon.getKey())) {
                        presentConcepts.add(alcon.getKey());
                    }
                } else if (similarity >= disSimilarityThreshold) {

                } else {
                    irrelevants++;
                    degreeIrrelevant += (1 - similarity);
                }
            } else {
                comment = Preprocess.remove_junks(comment);
                String removeStopWords = miner.removeStopWords(comment);
                removeStopWords = removeStopWords.replaceAll("\\s+", " ");
                NotAllowedConcepts concept = new NotAllowedConcepts();
                concept.setNotAllowed(new ArrayList(Arrays.asList(removeStopWords.split(" "))));
                notAllowed.add(concept);
            }
        }
        int sentNum = cSentences.size();
        if (sentNum == 0) {
            sentNum++;
        }
        com.setRelevancyCount((double) relevants / sentNum);
        com.setIrrelevancyCount((double) irrelevants / sentNum);
        com.setDegreeOfRelevancy(degreeRelevants);
        com.setDegreeOfIrrelevancy(degreeIrrelevant);
        String cBody = "";
        for (int sentIndex : relevant) {
            cBody += cSentences.get(sentIndex);
        }
        com.setCBODY(cBody);

        int contNum = allowed.size();
        if (contNum == 0) {
            contNum++;
        }
        com.setPresentConcepts(presentConcepts.size());
        com.setDegreeOfPresentConcepts(presentConcepts.size() / sentNum);

        cSentences.clear();
        relevant.clear();
    }

    public HashSet<String> commentsSentencesClassification(Comment com) throws Exception {
        int relevantNum = 0, irrelevantsNum = 0;
        double degreeRelevants = 0, degreeIrrelevant = 0;
        LinkedList presentConcepts = new LinkedList();
        TextMiner miner = new TextMiner(stopWords);

        double similarity;
        for (String comment : cSentences) {
            if (!this.isQuestion(comment)) {
                comment = Preprocess.remove_junks(comment);
//                String removeStopWords = miner.removeStopWords(comment);
                String removeStopWords = comment.replaceAll("\\s\\s+", " ");
                Entry<AllowedConcepts, Double> alcon = this.find(removeStopWords);
                similarity = alcon.getValue();
                if (similarity >= similarityThreshold) {
                    relevantNum++;
                    degreeRelevants += similarity;
                    relevant.add(cSentences.indexOf(removeStopWords));
                    if (!presentConcepts.contains(alcon.getKey())) {
                        presentConcepts.add(alcon.getKey());
                    }
                    alcon.getKey().addNewElementsAndRemoveDuplicates(TextMiner.removeDuplicates(removeStopWords));
                } else {
                    irrelevant.add(cSentences.indexOf(removeStopWords));
                    irrelevantTerms.addAll(TextMiner.removeDuplicates(removeStopWords));
                    irrelevantsNum++;
                    degreeIrrelevant += (1 - similarity);
                }
            }
        }
        int sentNum = cSentences.size();
        if (sentNum == 0) {
            sentNum++;
        }
        com.setRelevancyCount(((double) relevantNum / (double) sentNum));
        com.setIrrelevancyCount(((double) irrelevantsNum / (double) sentNum));
        com.setDegreeOfRelevancy(degreeRelevants);
        com.setDegreeOfIrrelevancy(degreeIrrelevant);

        String relevantConcats = this.concateLinkedListOfSentences(relevant), irrelevantConcats = this.concateLinkedListOfSentences(irrelevant);
        com.setLength(relevantConcats.length() / (1 + com.getCBODY().length()));//(1 + irrelevantConcats.length()));
        com.setWordPerSentence(relevantConcats.split(" ").length / (1 + TextMiner.getNumberofSentences(com.getCBODY())));

        int relMeanLen = 0;
        for (String token : relevantConcats.split(" ")) {
            relMeanLen += token.length();
        }
        int meanLen = 0;
        for (String token : com.getCBODY().split(" ")) {
            meanLen += token.length();
        }
        if (meanLen == 0) {
            meanLen = 1;
        }
        com.setCharPerWords(relMeanLen / meanLen);

        com.setLongestSentence(TextMiner.getMaxSentenceLength(relevantConcats) / (1 + TextMiner.getMaxSentenceLength(irrelevantConcats)));
//        String cBody = "";
//        for (int sentIndex : relevant) {
//            cBody += cSentences.get(sentIndex);
//        }
//        com.setCBODY(cBody);
        int contNum = allowed.size();
        if (contNum == 0) {
            contNum = 1;
        }
        com.setPresentConcepts(presentConcepts.size());
        com.setDegreeOfPresentConcepts(presentConcepts.size() / sentNum);

        cSentences.clear();
        return irrelevantTerms;
    }

    public boolean isQuestion(String sent) {
        return sent.contains("?");
    }

    private String concateLinkedListOfSentences(ArrayList commentSentences) {
        StringBuilder concate = new StringBuilder();
        for (Iterator<Integer> it = commentSentences.iterator(); it.hasNext();) {
            try {
            String sentence = cSentences.get(it.next());
            concate = concate.append(" ").append(sentence);
            } catch(Exception ex) {
                System.out.println(ex.getMessage());
            }
        }
        return concate.toString();
    }

    private void appendToMostSimilarAllowedQuestionConcept(String question) throws Exception {

        TextMiner miner = new TextMiner(stopWords);
        AllowedConcepts allow;
        allow = this.findMostSimilarAllowedConcept(question);
        if (allow != null) {
            allow.getAllowed().addAll(miner.removeStopWords(miner.removeDuplicates(question)));
            allow.setAllowed(new ArrayList<>(new HashSet<>(allow.getAllowed())));
        }
    }

    private void appendToMostSimilarAllowedQuestionConceptWithThreshold(String question) throws Exception {

        TextMiner miner = new TextMiner(stopWords);
        AllowedConcepts allow = null;
        allow = this.findMostSimilarAllowedConcept(question);
        if (allow != null) {
            allow.getAllowed().addAll(miner.removeStopWords(miner.removeDuplicates(question)));
            allow.setAllowed(new ArrayList<>(new HashSet<>(allow.getAllowed())));
        }
    }

    private void appendToMostSimilarNotAllowedQuestionConcept(String comment) throws Exception {

        TextMiner miner = new TextMiner(stopWords);
        NotAllowedConcepts notAllow = null;
        notAllow = this.findMostSimilarNotAllowedConcept(comment);
        if (notAllow != null) {
            notAllow.getNotAllowed().addAll(miner.removeStopWords(miner.removeDuplicates(comment)));
            notAllow.setNotAllowed(new ArrayList<>(new HashSet<>(notAllow.getNotAllowed())));
        }
    }

    private AllowedConcepts findMostSimilarAllowedConcept(String question) throws Exception {

        TextMiner miner = new TextMiner(stopWords);
        double similarity = 0;
        question = StringUtils.join(miner.removeStopWords(miner.removeDuplicates(question)));
        AllowedConcepts allow = null;
        for (AllowedConcepts al : allowed) {
            double tempSimilarity;
            tempSimilarity = miner.freqCosineSimilarity(StringUtils.join(al.getAllowed()), question);
            if (similarity < tempSimilarity) {
                similarity = tempSimilarity;
                allow = al;
            }
        }
        return allow;
    }

    private Entry<AllowedConcepts, Double> find(String question) throws Exception {

        TextMiner miner = new TextMiner(stopWords);
        double similarity = 0;
        question = StringUtils.join(miner.removeStopWords(miner.removeDuplicates(question)));
        AllowedConcepts allow = null;
        for (AllowedConcepts al : allowed) {
            double tempSimilarity;
            tempSimilarity = miner.freqCosineSimilarity(StringUtils.join(al.getAllowed()), question);
            if (similarity < tempSimilarity) {
                similarity = tempSimilarity;
                allow = al;
            }
        }
        Entry<AllowedConcepts, Double> mostSimilar = new EntryImpl(allow, similarity);
        return mostSimilar;
    }

    private NotAllowedConcepts findMostSimilarNotAllowedConcept(String question) throws Exception {

        TextMiner miner = new TextMiner(stopWords);
        double similarity = 0;
        question = StringUtils.join(miner.removeStopWords(miner.removeDuplicates(question)));
        NotAllowedConcepts notAllow = null;
        for (NotAllowedConcepts nal : notAllowed) {
            double tempSimilarity;
            tempSimilarity = miner.freqCosineSimilarity(StringUtils.join(nal.getNotAllowed()), question);
            if (similarity < tempSimilarity) {
                similarity = tempSimilarity;
                notAllow = nal;
            }
        }
        return notAllow;
    }

    private double findMostSimilarAllowedConceptSimilarity(String question) throws Exception {

        TextMiner miner = new TextMiner(stopWords);
        double similarity = 0;
        question = miner.removeStopWords(question);
        for (AllowedConcepts al : allowed) {
            double tempSimilarity;
            tempSimilarity = miner.freqCosineSimilarity(StringUtils.join(al.getAllowed()), question);
            if (similarity < tempSimilarity) {
                similarity = tempSimilarity;
            }
        }
        return similarity;
    }

    private double findMostSimilarNotAllowedConceptSimilarity(String question) throws Exception {

        TextMiner miner = new TextMiner(stopWords);
        double similarity = 0;
        question = StringUtils.join(miner.removeStopWords(miner.removeDuplicates(question)));
        for (NotAllowedConcepts nal : notAllowed) {
            double tempSimilarity = miner.freqCosineSimilarity(StringUtils.join(nal.getNotAllowed()), question);
            if (similarity < tempSimilarity) {
                similarity = tempSimilarity;
            }
        }
        return similarity;
    }

    public ArrayList<Integer> getIrrelevant() {
        return irrelevant;
    }

    public void setIrrelevant(ArrayList<Integer> irrelevant) {
        this.irrelevant = irrelevant;
    }

    public ArrayList<Integer> getRelevant() {
        return relevant;
    }

    public void setRelevant(ArrayList<Integer> relevant) {
        this.relevant = relevant;
    }

    private static class EntryImpl implements Entry<AllowedConcepts, Double> {

        private final AllowedConcepts key;
        private double value;

        private EntryImpl(AllowedConcepts allow, double similarity) {
            this.key = allow;
            this.value = similarity;
        }

        @Override
        public AllowedConcepts getKey() {
            return key;
        }

        @Override
        public Double getValue() {
            return value;
        }

        @Override
        public Double setValue(Double value) {
            return this.value = value;
        }
    }
}
