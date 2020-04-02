/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package datatypes;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;


/**
 *
 * @author epcpu
 */
@XmlRootElement(name = "root")
public class Comment {
    
    /**
     * attributes
     * CID: Comment ID
     * CUserId: Comment User ID
     * CGold: Our Goal(Good, Potential, Bad, Dialogue)
     * CGold_YN: if Yes/No Question => Our Goal(Yes, No, Unsure) otherwise
     *      Not Applicable
     */
    private String CID, CUSERID, CGOLD, CGOLD_YN;
    /**
     * context of each Answer
     * CSubject: Comment Title
     * CBody: Comment Body
     */
    private String CSUBJECT, CBODY;

    /**
     * list wise features added during filtering
     */
    private double relevancyCount, irrelevancyCount;
    private double degreeOfRelevancy, degreeOfIrrelevancy;
    private int presentConcepts;
    private double degreeOfPresentConcepts;
    /**
     * 
     */
    private int numImages, numLinks;
    private int hasQuestionMark, hasShockMark;

    private float WordPerSentence;
    private float CharPerWords;
    private float Length;
    private float LongestSentence;
    /**
     * Access Methods
     */

    public String getCID() {
        return CID;
    }

    @XmlAttribute
    public void setCID(String CID) {
        this.CID = CID;
    }

    public String getCUSERID() {
        return CUSERID;
    }
    
    @XmlAttribute
    public void setCUSERID(String CUSERID) {
        this.CUSERID = CUSERID;
    }

    public String getCGOLD() {
        return CGOLD;
    }

    @XmlAttribute
    public void setCGOLD(String CGOLD) {
        this.CGOLD = CGOLD;
    }

    public String getCGOLD_YN() {
        return CGOLD_YN;
    }

    @XmlAttribute
    public void setCGOLD_YN(String CGOLD_YN) {
        this.CGOLD_YN = CGOLD_YN;
    }

    public String getCSUBJECT() {
        return CSUBJECT;
    }

    @XmlElement
    public void setCSUBJECT(String CSUBJECT) {
        this.CSUBJECT = CSUBJECT;
    }

    public String getCBODY() {
        return CBODY;
    }

    @XmlElement
    public void setCBODY(String CBODY) {
        this.CBODY = CBODY;
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

    public int getHasQuestionMark() {
        return hasQuestionMark;
    }

    public void setHasQuestionMark(int hasQuestionMark) {
        this.hasQuestionMark = hasQuestionMark;
    }

    public int getHasShockMark() {
        return hasShockMark;
    }

    public void setHasShockMark(int hasShockMark) {
        this.hasShockMark = hasShockMark;
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

    public void setLongestSentence(int LongestSentence) {
        this.LongestSentence = LongestSentence;
    }
    
    @Override
    public String toString() {
        return "Comment{" + "CID=" + CID + ", CUSERID=" + CUSERID + ", CGOLD=" + CGOLD + ", CGOLD_YN=" + CGOLD_YN + ", CSUBJECT=" + CSUBJECT + ", CBODY=" + CBODY + '}';
    }

}
