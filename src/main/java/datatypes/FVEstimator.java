package datatypes;

//import sentimenttest.SentimentTest;

import edu.stanford.nlp.util.StringUtils;
import loader.DataLoader;
import tools.processors.Filtering;
import tools.processors.TextMiner;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static ch.lambdaj.Lambda.*;
import static org.hamcrest.Matchers.equalTo;

//import edu.berkeley.nlp.lm.io.LmReaders;

/**
 * @author epcpu
 */
public class FVEstimator {

    private static ArrayList<Question> Que;
    private static int sampleNumbers = 0;
    private static ArrayList<String> categoryNumbers = new ArrayList<>();

    Writer out;
    //    String questionCorpusFileAddress = "G:\\epcpu\\projects\\semeval_AV\\datasets\\dump_QL_all_question_body.txt"
//            + "\\dump_QL_all_question_body.txt.normalized";
//    String commentCorpusFileAddress = "G:\\epcpu\\projects\\semeval_AV\\datasets\\dump_QL_all_comment_body.txt"
//            + "\\dump_QL_all_comment_body.txt.normalized";
    SentenceLanguageModelSimilarity questionLM, answerLM;
    SentenceSimilarity answerSm;//, questionSm;
//    Hashtable<String, Float> questionsLM, answersLM;

    public FVEstimator(ArrayList<Question> Que) throws FileNotFoundException, IOException, InstantiationException, IllegalAccessException {
        FVEstimator.Que = Que;

//        questionLM = new SentenceLanguageModelSimilarity(questionCorpusFileAddress);
//        answerLM = new SentenceLanguageModeeSimilarity(commentCorpusFileAddress);//used
//        questionSm = new SentenceSimilarity(questionCorpusFileAddress);
//        answerSm = new SentenceSimilarity(commentCorpusFileAddress);
    }

    /**
     * @param featureTableFileAddress
     * @throws IOException
     */
    public void listWiseEstimator(String featureTableFileAddress) throws IOException, Exception {

//        List samples = new LinkedList();
        int before = 0, after = 0;
        PrintWriter out = FeaturesValues.convertEntryToCsvStarter(featureTableFileAddress);
        String headers = "commentId,"
                + "longestSentence,"
                + "wordsPerSentence,"
                + "charPerSentence,Length,Category,answerCount,"
                //                + "AskerUserId,CommenterUserId,"
                + "whichUserGroup,whichUserCategory,isCommenterAsker,"
                + "hasQuestionMark,hasShockMark,creationDate,"
                //                + "questionLMValue,answerLMValue,"
                + "cosinesimilarity,"
                + "relevancyCount,"
                + "irrelevancyCount,"
                + "degreeOfrelevancy,"
                + "degreeOfIrrelevancy,"
                + "presentConcepts,"
                + "degreeOfPresentConcepts,"
                + "isBeforeQuestioner,"
                //                + "isAfterQuestioner,"
                + "numberOfTwoCharsWords,"
                + "numberOfImages,"
                + "numberOfLinks,"
                + "numberOfEntities,"
                + "numberOfTypesOfEntities,"
                + "type,"
                //                + "commenterSense,"
                + "CGOLD";
        out.println(headers);
        /////////////////Used for simple tf idf calculation method
        HashMap<String, Integer> idf = FVEstimator.estimateIDF();
        List<Map.Entry<String, Integer>> max = select(idf.entrySet(),
                having(on(Map.Entry.class).getValue(), equalTo(max(idf, on(Integer.class)))));
        /////////////////Used for my new tf idf calculation method
//        HashMap<String, IDF> idf = FVEstimator.estimateNewIDF();
//        int max = FVEstimator.estimateMaxFreqTerms(idf);
        /////////////////
//        samples.add(headers);
//        SentenceSimilarity sentenceSimilarity = new SentenceSimilarity("D:\\compilers\\programming\\java\\semeval_AV\\" +
//                "datasets\\dump_QL_all_question_body.txt\\dump_QL_all_question_body.txt.normalized");//dump_QL_all_comment_body.txt\\dump_QL_all_comment_body.txt.normalized");
//        System.out.println(sentenceSimilarity.getTFIDFCosinSimilarity("it is a book", "it is not a book"));

        HashMap<String, UserReputation> userCategory = new HashMap<>();//helps to give user posts in differenct categories a unique identifier
        HashMap<Comment, FeaturesValues> cosineSimilarity = new HashMap<>();//helps to apply list wise approach for cosine similarity feature

        DataLoader loader = new DataLoader();
        loader.loadStopWordsList();
        ArrayList<String> stopWords = loader.getStopWords();

        for (Iterator<Question> qIt = Que.iterator(); qIt.hasNext(); ) {

            LinkedList<String> map = new LinkedList<>();//helps to give each of commenters a unique identifier

            Question question = qIt.next();
            LinkedList<Comment> comment = question.getComments();

            Filtering qFilter = new Filtering(question, stopWords);
            qFilter.relevancyDetectorOfQuestions();
//            float qLMV = OutFeaturesEstimatorMethods.sentenceLMEstimator(question.getQBody(), questionsLM);
            for (Iterator<Comment> cIt = comment.iterator(); cIt.hasNext(); ) {
                Comment c = cIt.next();
                if (c.getCGOLD().equalsIgnoreCase("Not English") || c.getCGOLD().equalsIgnoreCase("Other")) {
                    continue;
                }
                FeaturesValues features = new FeaturesValues();
                qFilter.commentInitialize(c);
//                qFilter.relevancyDetectorOfComments(c);
                qFilter.relevancyDetectorOfCommentsToQuestion(c);
//
                features.setRelevancyCount(c.getRelevancyCount());
                features.setIrrelevancyCount(c.getIrrelevancyCount());
                features.setDegreeOfRelevancy(c.getDegreeOfRelevancy());
                features.setDegreeOfIrrelevancy(c.getDegreeOfIrrelevancy());
                features.setPresentConcepts(c.getPresentConcepts());
                features.setDegreeOfPresentConcepts(c.getDegreeOfPresentConcepts());
                ////////////////
                features.setNumImages(c.getNumImages());
                features.setNumLinks(c.getNumLinks());
                ////////////////
                if (c.getCUSERID().equalsIgnoreCase(question.getQUserId())) {
//                    after = -1;
                    before = 1;
                    features.setIsBerforQuestioner(-1);
                } else {
//                    before = 0;
                    features.setIsBerforQuestioner(before);
//                    after = -1;
                }
                ////////////////
                if (!map.contains(c.getCUSERID())) {
                    map.add(c.getCUSERID());
                }
                features.setWhichUserGroup(map.indexOf(c.getCUSERID()));
                ////////////////
                UserReputation reputation;
                if (userCategory.containsKey(c.getCUSERID())) {
                    reputation = userCategory.get(c.getCUSERID());
                    if (!reputation.getReputations().contains(question.getQCategory())) {
                        reputation.getReputations().add(question.getQCategory());
                    }
                } else {
                    reputation = new UserReputation();
                    reputation.getReputations().add(question.getQCategory());
                    userCategory.put(c.getCUSERID(), reputation);
                }
                features.setWhichUserCategory(reputation.getReputations().indexOf(question.getQCategory()));
                ////////////////
                TextMiner miner = new TextMiner(idf);
                miner.setMaxTf(max.get(0).getValue());
//                miner.setMaxTf(max);
                ////////////////
                String[] cBodySplit = c.getCBODY().split(" ");
                features.setAnswerCount(comment.size());
                features.setLength(c.getCBODY().length());
                features.setWordPerSentence(cBodySplit.length / (1 + TextMiner.getNumberofSentences(c.getCBODY())));
                features.setCommenterID(c.getCUSERID());
                features.setAskerID(question.getQUserId());
                features.setCid(c.getCID());
                features.setCat(question.getQCategory());
                features.setType(question.getQType());
                features.setCreationDate(question.getQDate().substring(0, 7));//exracts Year-Month
//                features.setHasQuestionMark(c.getHasQuestionMark());
//                features.setHasShockMark(c.getHasShockMark());

                HashMap<String, String> entities = new HashMap<>();//key: entity, value: type of entity
                features.setNumEntities(entities.size());
                features.setNumTypesOfEntities(new HashSet<>(entities.values()).size());

                if (c.getCBODY().contains("?")) {
                    features.setHasQuestionMark(1);
                } else {
                    features.setHasQuestionMark(0);
                }
                if (c.getCBODY().contains("!")) {
                    features.setHasShockMark(1);
                } else {
                    features.setHasShockMark(0);
                }

                if (c.getCGOLD().equalsIgnoreCase("Other") ||
                        c.getCGOLD().equalsIgnoreCase("Not English")) {// || c.getCGOLD().equalsIgnoreCase("Dialogue")) {
                    features.setCGOLD("Bad");
                } //                else if (c.getCGOLD().equalsIgnoreCase("Potential")) {
                //                    features.setCGOLD("Good");
                //                } 
                else {
                    features.setCGOLD(c.getCGOLD());
                }
                if (features.getCommenterID().equalsIgnoreCase(features.getAskerID())) {
                    features.setCommenterAsker(0);//0: commenter is asker
                } else {
                    features.setCommenterAsker(1);//1: commenter is not asker
                }
                ////////////////
                int mean_len = 0;
                for (String token : cBodySplit) {
                    mean_len += token.length();
                }
                features.setCharPerWords((float) mean_len / cBodySplit.length);
                ////////////////
//                float aLMV = OutFeaturesEstimatorMethods.sentenceLMEstimator(comment.getCBODY(), answersLM);
//                features.setQuestionLogLikelihood(qLMV);
//                features.setAnswerLogLikelihood(aLMV);
//                features.setCommenterSense(TextMiner.getSentiment(comment.getCBODY()));
//                features.setCommenterSense(SentimentTest.getSentiment(comment.getCBODY()));
                String questionContent, commentContent;
                if (question.getQBody().contains(question.getQSubject())) {
                    questionContent = question.getQBody();
                } else {
                    questionContent = question.getQSubject() + " " + question.getQBody();
                }
                if (!c.getCBODY().contains(c.getCSUBJECT()) && !question.getQSubject().contains(c.getCSUBJECT())) {
                    commentContent = c.getCSUBJECT() + " " + c.getCBODY();
                } else {
                    commentContent = c.getCBODY();
                }
                ///////////
                features.setLongestSentence(TextMiner.getMaxSentenceLength(commentContent));
                features.setNumberOfTwoCharWords(FVEstimator.getNumberOfTwoCharsWords(commentContent));
                if (features.getCommenterAsker() == 1) {
//                    features.setCommentCosineSimilarity(miner.simpleCosineSimilarity(questionContent, commentContent));
                    features.setCommentCosineSimilarity(miner.freqCosineSimilarity(questionContent, commentContent));
//                    features.setCommentCosineSimilarity(miner.tfIdfCosineSimilarity(questionContent, commentContent));
                    cosineSimilarity.put(c, features);
                } else if (features.getCommenterAsker() == 0) {
                    if (cosineSimilarity.size() > 0) {
                        for (Iterator<Comment> comIter = cosineSimilarity.keySet().iterator(); comIter.hasNext(); ) {
                            Comment com = comIter.next();
                            String comContent;

                            if (!com.getCBODY().contains(com.getCSUBJECT()) && !question.getQSubject().contains(com.getCSUBJECT())) {
                                comContent = com.getCSUBJECT() + " " + com.getCBODY();
                            } else {
                                comContent = com.getCBODY();
                            }
                            FeaturesValues feat = cosineSimilarity.get(com);
//                            feat.setCommentCosineSimilarity(miner.simpleCosineSimilarity(questionContent + " " + commentContent, comContent));
                            feat.setCommentCosineSimilarity(miner.freqCosineSimilarity(questionContent + " " + commentContent, comContent));
//                            feat.setCommentCosineSimilarity(miner.tfIdfCosineSimilarity(questionContent + " " + commentContent, comContent));
                            out.println(feat.convertEntry());
                        }
//                        features.setCommentCosineSimilarity(miner.simpleCosineSimilarity(questionContent, commentContent));
                        features.setCommentCosineSimilarity(miner.freqCosineSimilarity(questionContent, commentContent));
//                        features.setCommentCosineSimilarity(miner.tfIdfCosineSimilarity(questionContent, commentContent));
                        out.println(features.convertEntry());
                        cosineSimilarity.clear();
                    } else {
//                        features.setCommentCosineSimilarity(miner.simpleCosineSimilarity(questionContent, commentContent));
                        features.setCommentCosineSimilarity(miner.freqCosineSimilarity(questionContent, commentContent));
//                        features.setCommentCosineSimilarity(miner.tfIdfCosineSimilarity(questionContent, commentContent));
                        out.println(features.convertEntry());
                    }
                }
                qFilter.getRelevant().clear();
                qFilter.getIrrelevant().clear();
            }
            if (cosineSimilarity.size() > 0) {
                for (Iterator<Comment> comIter = cosineSimilarity.keySet().iterator(); comIter.hasNext(); ) {
                    Comment com = comIter.next();
                    out.println(cosineSimilarity.get(com).convertEntry());
                }
                cosineSimilarity.clear();
            }
            before = after = 0;
        }
        out.close();
    }

    /**
     * @param featureTableFileAddress
     * @throws IOException
     */
    public void departionFilteringListWiseEstimator(String featureTableFileAddress) throws IOException, Exception {

//        List samples = new LinkedList();
        int before = 0, after = 0;
        PrintWriter out = FeaturesValues.convertEntryToCsvStarter(featureTableFileAddress);
        String headers = "commentId,"
                + "longestSentence,"
                + "wordsPerSentence,"
                + "charPerSentence,Length,Category,answerCount,"
                + "whichUserGroup,whichUserCategory,isCommenterAsker,"
                + "hasQuestionMark,hasShockMark,creationDate,"
                + "cosinesimilarity,"
                + "relevancyCount,"
                + "irrelevancyCount,"
                + "degreeOfrelevancy,"
                + "degreeOfIrrelevancy,"
                + "presentConcepts,"
                + "degreeOfPresentConcepts,"
                + "isBeforeQuestioner,"
                //                + "isAfterQuestioner,"
                + "numberOfTwoCharsWords,"
                + "numberOfImages,"
                + "numberOfLinks,"
                + "numberOfEntities,"
                + "numberOfTypesOfEntities,"
                + "type,"
                //                + "commenterSense,"
                + "CGOLD";
        out.println(headers);
        /////////////////Used for simple tf idf calculation method
        HashMap<String, Integer> idf = FVEstimator.estimateIDF();
        List<Map.Entry<String, Integer>> max = select(idf.entrySet(),
                having(on(Map.Entry.class).getValue(), equalTo(max(idf, on(Integer.class)))));
        /////////////////Used for my new tf idf calculation method
//        HashMap<String, IDF> idf = FVEstimator.estimateNewIDF();
//        int max = FVEstimator.estimateMaxFreqTerms(idf);
        /////////////////
        /**
         * helps to give user posts in different categories a unique identifier
         */
        HashMap<String, UserReputation> userCategory = loader.DataLoader.loadLinesOfTextFileSeperatedByTab("resources/whichUserCategory.txt");
        HashMap<Comment, FeaturesValues> cosineSimilarity = new HashMap<>();//helps to apply list wise approach for cosine similarity feature

        DataLoader loader = new DataLoader();
        loader.loadStopWordsList();
        ArrayList<String> stopWords = loader.getStopWords();

        for (Iterator<Question> qIt = Que.iterator(); qIt.hasNext(); ) {

            LinkedList<String> map = new LinkedList<>();//helps to give each of commenters a unique identifier

            Question question = qIt.next();
            LinkedList<Comment> comment = question.getComments();

            Filtering qFilter = new Filtering(question, stopWords);
            qFilter.relevancyDetectorOfQuestions();

            for (Iterator<Comment> cIt = comment.iterator(); cIt.hasNext(); ) {
                Comment c = cIt.next();
//                if (c.getCGOLD().equalsIgnoreCase("Not English") || c.getCGOLD().equalsIgnoreCase("Other")) {
//                    continue;
//                }
                FeaturesValues features = new FeaturesValues();
                qFilter.commentInitialize(c);
                HashSet<String> irreleventTerms = qFilter.commentsSentencesClassification(c);
//
                features.setRelevancyCount(c.getRelevancyCount());
                features.setIrrelevancyCount(c.getIrrelevancyCount());
                features.setDegreeOfRelevancy(c.getDegreeOfRelevancy());
                features.setDegreeOfIrrelevancy(c.getDegreeOfIrrelevancy());
                features.setPresentConcepts(c.getPresentConcepts());
                features.setDegreeOfPresentConcepts(c.getDegreeOfPresentConcepts());
                ////////////////
                features.setNumImages(c.getNumImages());
                features.setNumLinks(c.getNumLinks());
                ////////////////
                if (c.getCUSERID().equalsIgnoreCase(question.getQUserId())) {
//                    after = -1;
                    before = 1;
                    features.setIsBerforQuestioner(-1);
                } else {
//                    before = 0;
                    features.setIsBerforQuestioner(before);
//                    after = -1;
                }
                ////////////////
                if (!map.contains(c.getCUSERID())) {
                    map.add(c.getCUSERID());
                }
                features.setWhichUserGroup(map.indexOf(c.getCUSERID()));
                ////////////////
                UserReputation reputation;
                if (userCategory.containsKey(c.getCUSERID())) {
                    reputation = userCategory.get(c.getCUSERID());
                    if (!reputation.getReputations().contains(question.getQCategory())) {
                        reputation.getReputations().add(question.getQCategory());
                    }
                } else {
                    reputation = new UserReputation();
                    reputation.getReputations().add(question.getQCategory());
                    userCategory.put(c.getCUSERID(), reputation);
                }
                features.setWhichUserCategory(reputation.getReputations().indexOf(question.getQCategory()));
                ////////////////
                TextMiner miner = new TextMiner(idf);
                miner.setMaxTf(max.get(0).getValue());
//                miner.setMaxTf(max);
                ////////////////
                String[] cBodySplit = c.getCBODY().split(" ");
                features.setAnswerCount(comment.size());

                features.setLength(c.getLength());
                features.setWordPerSentence(c.getWordPerSentence());

                features.setCommenterID(c.getCUSERID());
                features.setAskerID(question.getQUserId());
                features.setCid(c.getCID());
                features.setCat(question.getQCategory());
                features.setType(question.getQType());
                features.setCreationDate(question.getQDate().substring(0, 7));//exracts Year-Month
//                features.setHasQuestionMark(c.getHasQuestionMark());
//                features.setHasShockMark(c.getHasShockMark());


                if (c.getCBODY().contains("?")) {
                    features.setHasQuestionMark(1);
                } else {
                    features.setHasQuestionMark(0);
                }
                if (c.getCBODY().contains("!")) {
                    features.setHasShockMark(1);
                } else {
                    features.setHasShockMark(0);
                }

                if (c.getCGOLD().equalsIgnoreCase("Other") || c.getCGOLD().equalsIgnoreCase("Not English")) {// || c.getCGOLD().equalsIgnoreCase("Dialogue")) {
                    features.setCGOLD("Bad");
                } //                else if (c.getCGOLD().equalsIgnoreCase("Potential")) {
                //                    features.setCGOLD("Good");
                //                } 
                else {
                    features.setCGOLD(c.getCGOLD());
                }
                if (features.getCommenterID().equalsIgnoreCase(features.getAskerID())) {
                    features.setCommenterAsker(0);//0: commenter is asker
                } else {
                    features.setCommenterAsker(1);//1: commenter is not asker
                }
                ////////////////
//                int mean_len = 0;
//                for (String token : cBodySplit) {
//                    mean_len += token.length();
//                }
//                features.setCharPerWords((float) mean_len / cBodySplit.length);
                features.setCharPerWords(c.getCharPerWords());
                ////////////////
//                float aLMV = OutFeaturesEstimatorMethods.sentenceLMEstimator(comment.getCBODY(), answersLM);
//                features.setQuestionLogLikelihood(qLMV);
//                features.setAnswerLogLikelihood(aLMV);
//                features.setCommenterSense(TextMiner.getSentiment(comment.getCBODY()));
//                features.setCommenterSense(SentimentTest.getSentiment(comment.getCBODY()));
                String questionContent, commentContent;
                if (question.getQBody().contains(question.getQSubject())) {
                    questionContent = question.getQBody();
                } else {
                    questionContent = question.getQSubject() + " " + question.getQBody();
                }
                if (!c.getCBODY().contains(c.getCSUBJECT()) && !question.getQSubject().contains(c.getCSUBJECT())) {
                    commentContent = c.getCSUBJECT() + " " + c.getCBODY();
                } else {
                    commentContent = c.getCBODY();
                }

//                HashMap<String, String> entities = TextMiner.getNER(commentContent);//key: entity, value: type of entity
//                features.setNumEntities(entities.size());
//                features.setNumTypesOfEntities(new HashSet<>(entities.values()).size());
                ///////////
//                features.setLongestSentence(TextMiner.getMaxSentenceLength(commentContent));
                features.setLongestSentence(c.getLongestSentence());

                features.setNumberOfTwoCharWords(FVEstimator.getNumberOfTwoCharsWords(commentContent));
                if (features.getCommenterAsker() == 1) {
//                    features.setCommentCosineSimilarity(miner.simpleCosineSimilarity(questionContent, commentContent));
                    features.setCommentCosineSimilarity(miner.freqCosineSimilarity(questionContent, commentContent));//, irreleventTerms));
//                    features.setCommentCosineSimilarity(miner.tfIdfCosineSimilarity(questionContent, commentContent));
                    cosineSimilarity.put(c, features);
                } else if (features.getCommenterAsker() == 0) {
                    if (cosineSimilarity.size() > 0) {
                        for (Iterator<Comment> comIter = cosineSimilarity.keySet().iterator(); comIter.hasNext(); ) {
                            Comment com = comIter.next();
                            String comContent;

                            if (!com.getCBODY().contains(com.getCSUBJECT()) && !question.getQSubject().contains(com.getCSUBJECT())) {
                                comContent = com.getCSUBJECT() + " " + com.getCBODY();
                            } else {
                                comContent = com.getCBODY();
                            }
                            FeaturesValues feat = cosineSimilarity.get(com);
//                            feat.setCommentCosineSimilarity(miner.simpleCosineSimilarity(questionContent + " " + commentContent, comContent));
                            feat.setCommentCosineSimilarity(miner.freqCosineSimilarity(questionContent + " " + commentContent, comContent));//, irreleventTerms));
//                            feat.setCommentCosineSimilarity(miner.tfIdfCosineSimilarity(questionContent + " " + commentContent, comContent));
                            out.println(feat.convertEntry());
                        }
//                        features.setCommentCosineSimilarity(miner.simpleCosineSimilarity(questionContent, commentContent));
                        features.setCommentCosineSimilarity(miner.freqCosineSimilarity(questionContent, commentContent));//, irreleventTerms));
//                        features.setCommentCosineSimilarity(miner.tfIdfCosineSimilarity(questionContent, commentContent));
                        out.println(features.convertEntry());
                        cosineSimilarity.clear();
                    } else {
//                        features.setCommentCosineSimilarity(miner.simpleCosineSimilarity(questionContent, commentContent));
                        features.setCommentCosineSimilarity(miner.freqCosineSimilarity(questionContent, commentContent));//, irreleventTerms));
//                        features.setCommentCosineSimilarity(miner.tfIdfCosineSimilarity(questionContent, commentContent));
                        out.println(features.convertEntry());
                    }
                }
                qFilter.getRelevant().clear();
                qFilter.getIrrelevant().clear();
            }
            if (cosineSimilarity.size() > 0) {
                for (Iterator<Comment> comIter = cosineSimilarity.keySet().iterator(); comIter.hasNext(); ) {
                    Comment com = comIter.next();
                    out.println(cosineSimilarity.get(com).convertEntry());
                }
                cosineSimilarity.clear();
            }
            before = after = 0;
        }
        out.close();
        PrintWriter printer = tools.util.file.Write.getPrintWriter("resources/whichUserCategory.txt", false);
        for (String key : userCategory.keySet()) {
            String attendedCats = StringUtils.join((Iterable<?>) userCategory.get(key).getReputations(), "\t");
            printer.println(key + "\t" + attendedCats);
        }
    }

    /**
     * @param featureTableFileAddress
     * @throws IOException
     */
    public void simpleEstimator(String featureTableFileAddress) throws IOException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException {

//        List samples = new LinkedList();
        int before = 0;
        PrintWriter out = FeaturesValues.convertEntryToCsvStarter(featureTableFileAddress);
        String headers = "commentId,"
                + "longestSentence,"
                + "wordsPerSentence,"
                + "charPerSentence,Length,Category,answerCount,"
                + "whichUserGroup,whichUserCategory,isCommenterAsker,"
                + "hasQuestionMark,hasShockMark,creationDate,"
                + "cosinesimilarity,"
                + "relevancyCount,"
                + "irrelevancyCount,"
                + "degreeOfrelevancy,"
                + "degreeOfIrrelevancy,"
                + "presentConcepts,"
                + "degreeOfPresentConcepts,"
                + "isBeforeQuestioner,"
                //                + "isAfterQuestioner,"
                + "numberOfTwoCharsWords,"
                + "numberOfImages,"
                + "numberOfLinks,"
                + "numberOfEntities,"
                + "numberOfTypesOfEntities,"
                + "type,"
                //                + "commenterSense,"
                + "CGOLD";
        out.println(headers);
        /////////////////Used for simple tf idf calculation method
        HashMap<String, Integer> idf = FVEstimator.estimateIDF();
        List<Map.Entry<String, Integer>> max = select(idf.entrySet(),
                having(on(Map.Entry.class).getValue(), equalTo(max(idf, on(Integer.class)))));
        /////////////////Used for my new tf idf calculation method
//        HashMap<String, IDF> idf = FVEstimator.estimateNewIDF();
//        int max = FVEstimator.estimateMaxFreqTerms(idf);
        /////////////////
//        samples.add(headers);
//        SentenceSimilarity sentenceSimilarity = new SentenceSimilarity("D:\\compilers\\programming\\java\\semeval_AV\\"
//                + "datasets\\dump_QL_all_question_body.txt\\dump_QL_all_question_body.txt.normalized");//dump_QL_all_comment_body.txt\\dump_QL_all_comment_body.txt.normalized");
//        System.out.println(sentenceSimilarity.getTFIDFCosinSimilarity("it is a book", "it is not a book"));

        HashMap<String, UserReputation> userCategory = new HashMap<>();//helps to give user posts in differenct categories a unique identifier

        for (Iterator<Question> qIt = Que.iterator(); qIt.hasNext(); ) {

            LinkedList<String> map = new LinkedList<>();//helps to give each of commenters a unique identifier

            Question question = qIt.next();
            LinkedList<Comment> comment = question.getComments();
//            float qLMV = OutFeaturesEstimatorMethods.sentenceLMEstimator(question.getQBody(), questionsLM);
            for (Iterator<Comment> cIt = comment.iterator(); cIt.hasNext(); ) {
                FeaturesValues features = new FeaturesValues();
                Comment c = cIt.next();
//                if (c.getCGOLD().equalsIgnoreCase("Not English") || c.getCGOLD().equalsIgnoreCase("Other")) {
//                    continue;
//                }
                ////////////
                features.setNumImages(c.getNumImages());
                features.setNumLinks(c.getNumLinks());
                ////////////
                if (c.getCUSERID().equalsIgnoreCase(question.getQUserId())) {
//                    after = -1;
                    before = 1;
                    features.setIsBerforQuestioner(-1);
                } else {
//                    before = 0;
                    features.setIsBerforQuestioner(before);
//                    after = -1;
                }
                ////////////////
                if (!map.contains(c.getCUSERID())) {
                    map.add(c.getCUSERID());
                }
                features.setWhichUserGroup(map.indexOf(c.getCUSERID()));
                ////////////////
                UserReputation reputation;
                if (userCategory.containsKey(c.getCUSERID())) {
                    reputation = userCategory.get(c.getCUSERID());
                    if (!reputation.getReputations().contains(question.getQCategory())) {
                        reputation.getReputations().add(question.getQCategory());
                    }
                } else {
                    reputation = new UserReputation();
                    reputation.getReputations().add(question.getQCategory());
                    userCategory.put(c.getCUSERID(), reputation);
                }
                features.setWhichUserCategory(reputation.getReputations().indexOf(question.getQCategory()));

                TextMiner miner = new TextMiner(idf);
                miner.setMaxTf(max.get(0).getValue());
//                miner.setMaxTf(max);
                ////////////////
                String[] cBodySplit = c.getCBODY().split(" ");
                features.setAnswerCount(comment.size());
                features.setLength(c.getCBODY().length());
                features.setWordPerSentence(cBodySplit.length / (1 + TextMiner.getNumberofSentences(c.getCBODY())));
                features.setCommenterID(c.getCUSERID());
                features.setAskerID(question.getQUserId());
                features.setCid(c.getCID());
                features.setCat(question.getQCategory());
                features.setType(question.getQType());
                features.setCreationDate(question.getQDate().substring(0, 7));//exracts Year-Month
                features.setLongestSentence(TextMiner.getMaxSentenceLength(c.getCBODY()));

//                HashMap<String, String> entities = new HashMap<>();//key: entity, value: type of entity
//                features.setNumEntities(entities.size());
//                features.setNumTypesOfEntities(new HashSet<>(entities.values()).size());

                String questionContent, commentContent;
                if (question.getQBody().contains(question.getQSubject())) {
                    questionContent = question.getQBody();
                } else {
                    questionContent = question.getQSubject() + " " + question.getQBody();
                }
                if (!c.getCBODY().contains(c.getCSUBJECT()) && !question.getQSubject().contains(c.getCSUBJECT())) {
                    commentContent = c.getCSUBJECT() + " " + c.getCBODY();
                } else {
                    commentContent = c.getCBODY();
                }
                features.setNumberOfTwoCharWords(FVEstimator.getNumberOfTwoCharsWords(commentContent));
                ////////comment cosine similarity estimation
//                features.setCommentCosineSimilarity(miner.simpleCosineSimilarity(questionContent, commentContent));
                features.setCommentCosineSimilarity(miner.freqCosineSimilarity(questionContent, commentContent));
//                features.setCommentCosineSimilarity(miner.tfIdfCosineSimilarity(questionContent, commentContent));
                ////////
                features.setCGOLD(c.getCGOLD());
                if (c.getCBODY().contains("?")) {
                    features.setHasQuestionMark(1);
                } else {
                    features.setHasQuestionMark(0);
                }
                if (c.getCBODY().contains("!")) {
                    features.setHasShockMark(1);
                } else {
                    features.setHasShockMark(0);
                }

                if (c.getCGOLD().equalsIgnoreCase("Other") || c.getCGOLD().equalsIgnoreCase("Not English")) {//) || c.getCGOLD().equalsIgnoreCase("Dialogue")) {
                    features.setCGOLD("Bad");
                } //                else if (c.getCGOLD().equalsIgnoreCase("Potential")) {
                //                    features.setCGOLD("Good");
                //                } 
                else {
                    features.setCGOLD(c.getCGOLD());
                }

                if (features.getCommenterID().equalsIgnoreCase(features.getAskerID())) {
                    features.setCommenterAsker(0);//0: commenter is asker
                } else {
                    features.setCommenterAsker(1);//1: commenter is not asker
                }
                ////////////////
                int mean_len = 0;
                for (String token : cBodySplit) {
                    mean_len += token.length();
                }
                features.setCharPerWords((float) mean_len / cBodySplit.length);
                ////////////////
//                float aLMV = OutFeaturesEstimatorMethods.sentenceLMEstimator(comment.getCBODY(), answersLM);
//                features.setQuestionLogLikelihood(qLMV);
//                features.setAnswerLogLikelihood(aLMV);
//                features.setCommenterSense(TextMiner.getSentiment(comment.getCBODY()));
//                features.setCommenterSense(SentimentTest.getSentiment(comment.getCBODY()));         
                out.println(features.convertEntry());
            }
        }
        out.close();
    }

    /**
     * @param featureTableFileAddress
     * @throws IOException
     */
    public void DescritisedEstimator(String featureTableFileAddress) throws IOException, IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException {

        //        List samples = new LinkedList();
        PrintWriter out = FeaturesValues.convertEntryToCsvStarter(featureTableFileAddress);
        String headers = "commentId,longestSentence,wordsPerSentence,"
                + "charPerSentence,Length,Category,answerCount,AnswererUserId,"
                + "CommenterUserId,whichUserGroup,whichUserCategory,isCommenterAsker,"
                + "creationDate,questionLMValue,answerLMValue,type,commenterSense,CGOLD";
        out.println(headers);
//        samples.add(headers);
        HashMap<String, UserReputation> userCategory = new HashMap<>();
        for (Iterator<Question> it = Que.iterator(); it.hasNext(); ) {
            List<FeaturesValues> forDiscriting = new ArrayList<>();
            LinkedList<String> map = new LinkedList<>();
            FeaturesValues features;
            Question question = it.next();
            LinkedList<Comment> C = question.getComments();
//            float qLMV = OutFeaturesEstimatorMethods.sentenceLMEstimator(question.getQBody(), questionsLM);
            for (Iterator<Comment> it1 = C.iterator(); it1.hasNext(); ) {
                features = new FeaturesValues();
                Comment comment = it1.next();
                //////////////////////
                if (!map.contains(comment.getCUSERID())) {
                    map.add(comment.getCUSERID());
                }
                features.setWhichUserGroup(map.indexOf(comment.getCUSERID()));
                //////////////////////
                UserReputation reputation;
                if (userCategory.containsKey(comment.getCUSERID())) {
                    reputation = userCategory.get(comment.getCUSERID());
                    if (!reputation.getReputations().contains(question.getQCategory())) {
                        reputation.getReputations().add(question.getQCategory());
                    }
                } else {
                    reputation = new UserReputation();
                    reputation.getReputations().add(question.getQCategory());
                    userCategory.put(comment.getCUSERID(), reputation);
                }
                features.setWhichUserCategory(reputation.getReputations().indexOf(question.getQCategory()));
                //////////////////////
                String[] temp = comment.getCBODY().split(" ");
                features.setAnswerCount(C.size());
                features.setWordPerSentence(temp.length);
                features.setLength(comment.getCBODY().length());
                features.setCGOLD(comment.getCGOLD());
                features.setCommenterID(comment.getCUSERID());
                features.setCid(comment.getCID());
                features.setAskerID(question.getQUserId());
                features.setCat(question.getQCategory());
                features.setType(question.getQType());
                features.setCreationDate(question.getQDate().substring(0, 7));
//                features.setLongestSentence(TextMiner.getMaxSentenceLength(comment.getCBODY()));
                if (features.getCommenterID().equalsIgnoreCase(features.getAskerID())) {
                    features.setCommenterAsker(0);
                } else {
                    features.setCommenterAsker(1);
                }
                //////////////////////
                int mean_len = 0;
                for (String temp_sub : temp) {
                    mean_len += temp_sub.length();
                }
                features.setCharPerWords((float) mean_len / temp.length);
                //////////////////////
//                float aLMV = OutFeaturesEstimatorMethods.sentenceLMEstimator(comment.getCBODY(), answersLM);
//                features.setQuestionLogLikelihood(qLMV);
//                features.setAnswerLogLikelihood(aLMV);
//                out.println(features.convertEntry());
                forDiscriting.add(features);
//                System.out.println(question.getQID() + "\t" + forDiscriting.size());
            }
            forDiscriting = sort(forDiscriting, on(FeaturesValues.class).getCharPerWords());
            for (int i = 0; i < forDiscriting.size(); i++) {
                forDiscriting.get(i).setCharPerWords((float) (i + 1));
            }
            forDiscriting = sort(forDiscriting, on(FeaturesValues.class).getLength());
            for (int i = 0; i < forDiscriting.size(); i++) {
                forDiscriting.get(i).setLength((i + 1));
            }
            forDiscriting = sort(forDiscriting, on(FeaturesValues.class).getLongestSentence());
            for (int i = 0; i < forDiscriting.size(); i++) {
                forDiscriting.get(i).setLongestSentence((i + 1));
            }
            forDiscriting = sort(forDiscriting, on(FeaturesValues.class).getCreationDate());
            for (int i = 0; i < forDiscriting.size(); i++) {
                forDiscriting.get(i).setCreationDate((i + 1) + "");
            }
            forDiscriting = sort(forDiscriting, on(FeaturesValues.class).getWordPerSentence());
            for (int i = 0; i < forDiscriting.size(); i++) {
                forDiscriting.get(i).setWordPerSentence((i + 1));
                out.println(forDiscriting.get(i).convertEntry());
            }
        }
        out.close();
    }

    /**
     * @param featureTableFileAddress
     * @throws IOException
     */
    public void bagOfWordsOutput(String featureTableFileAddress) throws IOException {
        //        List samples = new LinkedList();
        PrintWriter out = FeaturesValues.convertEntryToCsvStarter(featureTableFileAddress);
        String headers = "commentId,qsubject,qtext,csubject,ctext,category,questionerID,CommenterID,type,CGOLD";
        out.println(headers);
        HashMap<String, UserReputation> userCategory = new HashMap<>();
        for (Iterator<Question> it = Que.iterator(); it.hasNext(); ) {
            Question question = it.next();
            LinkedList<Comment> C = question.getComments();
            for (Iterator<Comment> it1 = C.iterator(); it1.hasNext(); ) {
                FeaturesValues features = new FeaturesValues();
                Comment comment = it1.next();

                features.setCid(comment.getCID().substring(1));
                features.setQsubj("\"" + question.getQSubject().replaceAll("\n", " ").replaceAll("\t", " ") + "\"");
                features.setQtext("\"" + question.getQBody().replaceAll("\n", " ").replaceAll("\t", " ") + "\"");
                features.setCsubj("\"" + comment.getCSUBJECT().replaceAll("\n", " ").replaceAll("\t", " ") + "\"");
                features.setCtext("\"" + comment.getCBODY().replaceAll("\n", " ").replaceAll("\t", " ") + "\"");
                features.setCat(question.getQCategory());
                features.setAskerID(question.getQUserId().substring(1));
                features.setCommenterID(comment.getCUSERID().substring(1));
                features.setType(question.getQType());
                features.setCGOLD(comment.getCGOLD());

                out.println(features.bagOfWordsConvertEntry());
            }
        }
        out.close();
    }

    /**
     * @return
     */
    public static HashMap<String, Integer> estimateIDF() {

        TextMiner miner = new TextMiner();
        HashMap<String, Integer> idf = new HashMap<>();
        for (Question q : Que) {
            sampleNumbers++;
            for (String qBody : miner.removeDuplicates(q.getQBody())) {
                if (!idf.containsKey(qBody)) {
                    idf.put(qBody, 1);
                } else {
                    idf.put(qBody, idf.get(qBody) + 1);
                }
            }
            for (Comment com : q.getComments()) {
                sampleNumbers++;
                for (Iterator<String> iter = miner.removeDuplicates(com.getCBODY()).iterator(); iter.hasNext(); ) {
                    String cBody = iter.next();
                    if (!idf.containsKey(cBody)) {
                        idf.put(cBody, 1);
                    } else {
                        idf.put(cBody, idf.get(cBody) + 1);
                    }
                }
            }

        }
        return idf;
    }

    /**
     * It estimates IDF according to my categorical algorithm
     *
     * @return
     */
    public static HashMap<String, IDF> estimateNewIDF() {

        TextMiner miner = new TextMiner();
        HashMap<String, IDF> idf = new HashMap<>();
        for (Question q : Que) {
            String qCat = q.getQCategory();
            if (!categoryNumbers.contains(qCat)) {
                categoryNumbers.add(qCat);
            }
            for (Iterator<String> iter = miner.removeDuplicates(q.getQBody()).iterator(); iter.hasNext(); ) {
                String qBodyTerm = iter.next();
                IDF iTerm;
                if (!idf.containsKey(qBodyTerm)) {
                    iTerm = new IDF();

                    iTerm.setTerm(qBodyTerm);
                    iTerm.getCategoricalIDF().put(qCat, 1);

                    idf.put(qBodyTerm, iTerm);
                } else {
                    iTerm = idf.get(qBodyTerm);
                    if (!iTerm.getCategoricalIDF().containsKey(qCat)) {
                        iTerm.getCategoricalIDF().put(qCat, 1);
                    } else {
                        iTerm.getCategoricalIDF().put(qCat, iTerm.getCategoricalIDF().get(qCat) + 1);
                    }
                }
            }
            for (Comment com : q.getComments()) {
                for (Iterator<String> iter = miner.removeDuplicates(com.getCBODY()).iterator(); iter.hasNext(); ) {
                    String cBodyTerm = iter.next();
                    IDF iTerm;
                    if (!idf.containsKey(cBodyTerm)) {
                        iTerm = new IDF();
                        iTerm.getCategoricalIDF().put(qCat, 1);
                        idf.put(cBodyTerm, iTerm);
                    } else {
                        iTerm = idf.get(cBodyTerm);
                        if (!iTerm.getCategoricalIDF().containsKey(qCat)) {
                            iTerm.getCategoricalIDF().put(qCat, 1);
                        } else {
                            iTerm.getCategoricalIDF().put(qCat, iTerm.getCategoricalIDF().get(qCat) + 1);
                        }
                    }
                }
            }

        }
        return idf;
    }

    /**
     * IT first estimates maximum frequency of all the terms presented in the
     * idf HashMap and simultaneously finds out the maximum frequency of all the
     * terms exists.
     *
     * @param idf
     * @return
     */
    public static int estimateMaxFreqTerms(HashMap<String, IDF> idf) {

        int max = 0;
        for (String term : idf.keySet()) {
            idf.get(term).setMaxFreqCategory();
            int temp = idf.get(term).getMaxFreqCategory();
            if (max < temp) {
                max = temp;
            }
        }
        return max;
    }

    private static int getNumberOfTwoCharsWords(String commentContent) {
        int count = 0;
        for (String str : commentContent.split(" ")) {
            if (str.length() == 2) {
                count++;
            }
        }
        return count;
    }

    public static int getSampleNumbers() {
        return sampleNumbers;
    }

    public static void setSampleNumbers(int sampleNumbers) {
        FVEstimator.sampleNumbers = sampleNumbers;
    }

    public static ArrayList<String> getCategoryNumbers() {
        return categoryNumbers;
    }

    public static void setCategoryNumbers(ArrayList<String> categoryNumbers) {
        FVEstimator.categoryNumbers = categoryNumbers;
    }

    public static int getCategorySize() {
        return categoryNumbers.size();
    }
}
