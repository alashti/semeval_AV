/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package datatypes;

import com.google.gson.Gson;
import java.util.LinkedList;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 *
 * @author epcpu
 */
@XmlRootElement(name = "root")
public class Question {

    
    /**
     * attributes QID: Question Id QCategory: Question Category QDate: Question
     * Asked at Date QUserId: User who Asked the Question QType: It's General or
     * Yes/No Question QGold_YN: OverAll Answer of a Yes/No Question(Yes, No,
     * Not Sure) or Not Applicable otherwise
     */
    private String QID, QCATEGORY, QDATE, QUSERID, QTYPE, QGOLD_YN;
    /**
     * context of each Question QSubject: Title of each of the Questions QBody:
     * Body of each of the Questions
     */
    private String QSUBJECT, QBODY;

    private LinkedList<Comment> comments;

    /**
     * access methods
     */
    public LinkedList<Comment> getComments() {
        return comments;
    }

    public void setComments(LinkedList<Comment> comments) {
        this.comments = comments;
    }

    public String getQID() {
        return QID;
    }

    @XmlAttribute
    public void setQID(String QID) {
        this.QID = QID;
    }

    public String getQCategory() {
        return QCATEGORY;
    }

    @XmlAttribute
    public void setQCategory(String QCategory) {
        this.QCATEGORY = QCategory;
    }

    public String getQDate() {
        return QDATE;
    }

    @XmlAttribute
    public void setQDate(String QDate) {
        this.QDATE = QDate;
    }

    public String getQUserId() {
        return QUSERID;
    }

    @XmlAttribute
    public void setQUserId(String QUserId) {
        this.QUSERID = QUserId;
    }

    public String getQType() {
        return QTYPE;
    }

    @XmlAttribute
    public void setQType(String QType) {
        this.QTYPE = QType;
    }

    public String getQGold_YN() {
        return QGOLD_YN;
    }

    @XmlAttribute
    public void setQGold_YN(String QGold_YN) {
        this.QGOLD_YN = QGold_YN;
    }

    public String getQSubject() {
        return QSUBJECT;
    }

    @XmlElement
    public void setQSubject(String QSubject) {
        this.QSUBJECT = QSubject;
    }

    public String getQBody() {
        return QBODY;
    }

    @XmlElement
    public void setQBody(String QBody) {
        this.QBODY = QBody;
    }

    @Override
    public String toString() {
        return "Question{" + "QID=" + QID + ", QCATEGORY=" + QCATEGORY + ", QDATE=" + QDATE + ", QUSERID=" + QUSERID + ", QTYPE=" + QTYPE + ", QGOLD_YN=" + QGOLD_YN + ", QSUBJECT=" + QSUBJECT + ", QBODY=" + QBODY + '}';
    }

    public String toJsonString() throws CloneNotSupportedException {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    static public Question getFromJsonString(String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, Question.class);
    }
}
