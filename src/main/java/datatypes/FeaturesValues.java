/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datatypes;

import java.io.IOException;
import java.io.PrintWriter;

/**
 * @author epcpu
 */
public class FeaturesValues {

    //Linguistic
    private float questionLogLikelihood;
    private float answerLogLikelihood;
    private float WordPerSentence;
    private float CharPerWords;
    private float Length;//int changed to float for new filtering method checking
    private float LongestSentence;//int changed to float for new filtering method checking
    //Other
    private int questionerSense;
    private int commenterSense;
    private int AnswerCount;
    private int Age;
    private String CreationDate;
    private String CGOLD;
    //New
    private String askerID;
    private String CommenterID;
    private int isCommenterAsker;
    private int hasQuestionMark;
    private int hasShockMark;
    private String cat;
    private String type;
    private int whichUserGroup;
    private int WhichUserCategory;
    //for identification
    private String Cid;
    //abolfazl feature needs
    private String qsubj, qtext, csubj, ctext;
    //last features added
    private double commentCosineSimilarity;
    //trend-based features
    private int isAfterQuestioner;
    private int isBerforQuestioner;
    //listwise based features
    private double relevancyCount, irrelevancyCount;
    private double degreeOfRelevancy, degreeOfIrrelevancy;
    private int presentConcepts;
    private double degreeOfPresentConcepts;
    //Not English
    private int numberOfTwoCharWords;
    //html tags
    private int numImages, numLinks;

    private int numEntities, numTypesOfEntities;

    /**
     * methods
     */
    
    public int getNumEntities() {
        return numEntities;
    }

    public void setNumEntities(int numEntities) {
        this.numEntities = numEntities;
    }

    public int getNumTypesOfEntities() {
        return numTypesOfEntities;
    }

    public void setNumTypesOfEntities(int numTypesOfEntities) {
        this.numTypesOfEntities = numTypesOfEntities;
    }

    public int getNumberOfTwoCharWords() {
        return numberOfTwoCharWords;
    }

    public void setNumberOfTwoCharWords(int numberOfTwoCharWords) {
        this.numberOfTwoCharWords = numberOfTwoCharWords;
    }

    public double getRelevancyCount() {
        return relevancyCount;
    }

    public void setRelevancyCount(double relevancyCount) {
        this.relevancyCount = relevancyCount;
    }

    public double getIrrelevancyCount() {
        return irrelevancyCount;
    }

    public void setIrrelevancyCount(double irrelevancyCount) {
        this.irrelevancyCount = irrelevancyCount;
    }

    public double getDegreeOfRelevancy() {
        return degreeOfRelevancy;
    }

    public void setDegreeOfRelevancy(double degreeOfRelevancy) {
        this.degreeOfRelevancy = degreeOfRelevancy;
    }

    public double getDegreeOfIrrelevancy() {
        return degreeOfIrrelevancy;
    }

    public void setDegreeOfIrrelevancy(double degreeOfIrrelevancy) {
        this.degreeOfIrrelevancy = degreeOfIrrelevancy;
    }

    public int getIsAfterQuestioner() {
        return isAfterQuestioner;
    }

    public void setIsAfterQuestioner(int isAfterQuestioner) {
        this.isAfterQuestioner = isAfterQuestioner;
    }

    public int getIsBerforQuestioner() {
        return isBerforQuestioner;
    }

    public void setIsBerforQuestioner(int isBerforQuestioner) {
        this.isBerforQuestioner = isBerforQuestioner;
    }

    public double getCommentCosineSimilarity() {
        return commentCosineSimilarity;
    }

    public void setCommentCosineSimilarity(double commentCosineSimilarity) {
        this.commentCosineSimilarity = commentCosineSimilarity;
    }

    public int getQuestionerSense() {
        return questionerSense;
    }

    public void setQuestionerSense(int questionerSense) {
        this.questionerSense = questionerSense;
    }

    public int getCommenterSense() {
        return commenterSense;
    }

    public void setCommenterSense(int commenterSense) {
        this.commenterSense = commenterSense;
    }

    public float getAnswerLogLikelihood() {
        return answerLogLikelihood;
    }

    public void setAnswerLogLikelihood(float answerLogLikelihood) {
        this.answerLogLikelihood = answerLogLikelihood;
    }

    public float getQuestionLogLikelihood() {
        return questionLogLikelihood;
    }

    public void setQuestionLogLikelihood(float questionLogLikelihood) {
        this.questionLogLikelihood = questionLogLikelihood;
    }

    public String getQsubj() {
        return qsubj;
    }

    public void setQsubj(String qsubj) {
        this.qsubj = qsubj;
    }

    public String getQtext() {
        return qtext;
    }

    public void setQtext(String qtext) {
        this.qtext = qtext;
    }

    public String getCsubj() {
        return csubj;
    }

    public void setCsubj(String csubj) {
        this.csubj = csubj;
    }

    public String getCtext() {
        return ctext;
    }

    public void setCtext(String ctext) {
        this.ctext = ctext;
    }

    public int getWhichUserCategory() {
        return WhichUserCategory;
    }

    public void setWhichUserCategory(int whichUserCategory) {
        WhichUserCategory = whichUserCategory;
    }

    public int getWhichUserGroup() {
        return whichUserGroup;
    }

    public void setWhichUserGroup(int whichUserGroup) {
        this.whichUserGroup = whichUserGroup;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCid() {
        return Cid;
    }

    public void setCid(String cid) {
        Cid = cid;
    }

    public String getCat() {
        return cat;
    }

    public void setCat(String cat) {
        this.cat = cat;
    }

    public int getCommenterAsker() {
        return isCommenterAsker;
    }

    public void setCommenterAsker(int commenterAsker) {
        isCommenterAsker = commenterAsker;
    }

    public String getAskerID() {
        return askerID;
    }

    public void setAskerID(String askerID) {
        this.askerID = askerID;
    }

    public String getCommenterID() {
        return CommenterID;
    }

    public void setCommenterID(String commenterID) {
        CommenterID = commenterID;
    }

    public float getWordPerSentence() {
        return WordPerSentence;
    }

    public void setWordPerSentence(float WordPerSentence) {
        this.WordPerSentence = WordPerSentence;
    }

    public float getCharPerWords() {
        return CharPerWords;
    }

    public void setCharPerWords(float CharPerWords) {
        this.CharPerWords = CharPerWords;
    }

    public float getLength() {
        return Length;
    }

    public void setLength(float Length) {
        this.Length = Length;
    }

    public float getLongestSentence() {
        return LongestSentence;
    }

    public void setLongestSentence(float LongestSentence) {
        this.LongestSentence = LongestSentence;
    }

    public int getAnswerCount() {
        return AnswerCount;
    }

    public void setAnswerCount(int AnswerCount) {
        this.AnswerCount = AnswerCount;
    }

    public int getAge() {
        return Age;
    }

    public void setAge(int Age) {
        this.Age = Age;
    }

    public String getCreationDate() {
        return CreationDate;
    }

    public void setCreationDate(String CreationDate) {
        this.CreationDate = CreationDate;
    }

    public int getHasQuestionMark() {
        return hasQuestionMark;
    }

    public void setHasQuestionMark(int hasQuestionMark) {
        this.hasQuestionMark = hasQuestionMark;
    }

    public String getCGOLD() {
        return CGOLD;
    }

    public void setCGOLD(String CGOLD) {
        this.CGOLD = CGOLD;
    }

    public int getHasShockMark() {
        return hasShockMark;
    }

    public void setHasShockMark(int hasShockMark) {
        this.hasShockMark = hasShockMark;
    }

    public int getPresentConcepts() {
        return presentConcepts;
    }

    public void setPresentConcepts(int presentConcepts) {
        this.presentConcepts = presentConcepts;
    }

    public double getDegreeOfPresentConcepts() {
        return degreeOfPresentConcepts;
    }

    public void setDegreeOfPresentConcepts(double degreeOfPresentConcepts) {
        this.degreeOfPresentConcepts = degreeOfPresentConcepts;
    }

    public int getNumImages() {
        return numImages;
    }

    public void setNumImages(int numImages) {
        this.numImages = numImages;
    }

    public int getNumLinks() {
        return numLinks;
    }

    public void setNumLinks(int numLinks) {
        this.numLinks = numLinks;
    }

    /**
     * @return
     */
    public static PrintWriter convertEntryToCsvStarter(String wekaInputFile) throws IOException {
        PrintWriter out = tools.util.file.Write.getPrintWriter(wekaInputFile, false);
        return out;
    }

    /**
     * @throws IOException
     */
    public String convertEntry() {
        return this.getCid() + ","
                + this.getLongestSentence() + ","
                + this.getWordPerSentence() + ","
                + this.getCharPerWords() + "," + this.getLength() + ","
                + this.getCat() + "," + this.getAnswerCount() + ","
                + //                this.getAskerID() + "," + this.getCommenterID() + "," +
                this.getWhichUserGroup() + "," + this.getWhichUserCategory() + ","
                + this.getCommenterAsker() + "," + this.getHasQuestionMark() + ","
                + this.getHasShockMark() + "," + this.getCreationDate() + ","
                + //                this.getQuestionLogLikelihood() + "," + this.getAnswerLogLikelihood() + "," +
                this.getCommentCosineSimilarity() + ","
                + this.getRelevancyCount() + ","
                + this.getIrrelevancyCount() + ","
                + this.getDegreeOfRelevancy() + ","
                + this.getDegreeOfIrrelevancy() + ","
                + this.getPresentConcepts() + ","
                + this.getDegreeOfPresentConcepts() + ","
                + this.getIsBerforQuestioner() + ","
                + this.getNumberOfTwoCharWords() + ","
                + this.getNumImages() + ","
                + this.getNumLinks() + ","
                + this.getNumEntities()+ ","
                + this.getNumTypesOfEntities()+ ","
                + //                this.getIsAfterQuestioner() + "," +
                this.getType() + ","
                + //                this.getCommenterSense() + "," +
                this.getCGOLD();
    }

    public String bagOfWordsConvertEntry() {
        return this.getCid() + "," + this.getQsubj() + "," + this.getQtext() + "," + this.getCsubj() + ","
                + this.getCtext() + "," + this.getCat() + "," + this.getAskerID() + "," + this.getCommenterID() + ","
                + this.getType() + "," + this.getCGOLD();
    }

//    @Override
//    public int compare(Object o1, Object o2) {
//        try {
//            if (o1 != null && o2 != null) {
//                o1 = o1.getClass().getMethod(featureName, new Class[0]).invoke(o1, new Object[0]);
//                o2 = o2.getClass().getMethod(featureName, new Class[0]).invoke(o2, new Object[0]);
//            }
//        } catch (Exception ex) {
//            System.out.println(ex.getMessage());
//        }
//        return (o1 == null) ? -1 : ((o2 == null) ? 1 : ((Comparable<Object>) o1).compareTo(o2));
//    }
//    public static Comparator<FeaturesValues> compareByCharPerWord = new Comparator<FeaturesValues>() {
//        public int compare(FeaturesValues one, FeaturesValues other) {
//            return one.CharPerWords.compareTo(other.CharPerWords);
//        }
//    };
//
//    public static Comparator<FeaturesValues> compareByWordPerSentence = new Comparator<FeaturesValues>() {
//        public int compare(FeaturesValues one, FeaturesValues other) {
//            return one.WordPerSentence.compareTo(other.WordPerSentence);
//        }
//    };
}
