package datatypes;

import java.util.LinkedList;

/**
 *
 * @author epcpu
 */
public class QuestionTrainSet {
    private String rawInput;
    private String type;
    private String question;
    private LinkedList<String> bagOfPOS;

    public QuestionTrainSet() {
        this.bagOfPOS = new LinkedList<>();
    }

    public String getRawInput() {
        return rawInput;
    }

    public void setRawInput(String rawInput) {
        this.rawInput = rawInput;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public LinkedList<String> getBagOfPOS() {
        return bagOfPOS;
    }

    public void setBagOfPOS(LinkedList<String> bagOfPOS) {
        this.bagOfPOS = bagOfPOS;
    }
    
    
    
}
