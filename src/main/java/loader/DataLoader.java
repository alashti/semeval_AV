/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package loader;

import datatypes.Comment;
import datatypes.Question;
import datatypes.UserReputation;

import java.io.DataInputStream;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.LinkedList;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;
import tools.processors.Preprocess;

import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;

//import org.json.simple.parser.JSONParser;
/**
 * @author epcpu
 */
public class DataLoader {

    //    File train;
//    private final Document doc;
    private ArrayList<Question> Que = new ArrayList<>();
    private ArrayList<String> stopWords = new ArrayList<>();

    private DataInputStream dio;
    private static PrintStream jout = System.out;
    private static String rules_dir = "D:\\compilers\\java library\\BiuNormalizer\\string_rules.txt";
    private String stopWordsFilePath = "resources/SmartStoplist.txt";

    public ArrayList<Question> xmlLoader() throws IOException {

        return xmlLoader("datasets/CQA-QL-train.xml");
    }

    public ArrayList<Question> xmlLoader(String xmlDir) throws IOException {

//        String dir = "D:\\compilers\\programming\\java\\semeval_AV\\datasets\\CQA-QL-train.xml";//Xml file
        Gson gson = new Gson();
        try {

            //baraye estefade az sax bayad aval az saxparserfactory yek nemone begirim
            SAXParserFactory sax_factory = SAXParserFactory.newInstance();
            //baed yek saxparser az saxfactory misazim
            SAXParser sax_parser = sax_factory.newSAXParser();
            //baed az on tamame kar ha i ke bayad anjam beshe ro toye tavabe i az object defaulthandler override mikonim
            //ke dar inja az 5 function estefade shode ast

            DefaultHandler handle;
            handle = new DefaultHandler() {
                boolean Question = false,
                        Comment = false,
                        QSubject = false,
                        QBody = false,
                        CSubject = false,
                        CBody = false;
                String QID,
                        QCAT,
                        QDATE,
                        QUSERID,
                        QTYPE,
                        QGOLD_YN,
                        QSUB,
                        QBODY;
                String CID,
                        CUSERID,
                        CGOLD,
                        CGOLD_YN,
                        CSUB,
                        CBODY;//baraye trigger shodane har kodam az bakhsh haye morede niaze file xml mibashand
                String temp;
                //tamame bakhsh haye morede niaz ra be onvane object haye in class zakhire mikonim
                LinkedList<Comment> coms;
                Comment com;
                Question q;

                @Override
                public void endDocument() {//dar zamane shoroe file xml tavasote parser call mishavad
                    jout.println("Document has been ended.");
                }

                @Override
                public void startDocument() {//dar zamane payane file xml tavasote parser call mishavad
                    jout.println("Document has been started.");
                }

                @Override
                public void startElement(String uriString, String localName,
                        String qName, Attributes attributes) throws SAXException {
                    //vaghti ke be starte yek tag miresad in function tavasote parser call mishavad
                    if (qName.equalsIgnoreCase("Question")) {
                        Question = true;

                        q = new Question();
                        coms = new LinkedList<Comment>();
                        temp = attributes.getValue("QID");
                        q.setQID(temp);
                        temp = attributes.getValue("QCATEGORY");
                        q.setQCategory(temp);
                        temp = attributes.getValue("QDATE");
                        q.setQDate(temp);
                        temp = attributes.getValue("QUSERID");
                        q.setQUserId(temp);
                        temp = attributes.getValue("QTYPE");
                        q.setQType(temp);
                        temp = attributes.getValue("QGOLD_YN");
                        q.setQGold_YN(temp);
                    }
                    if (qName.equalsIgnoreCase("Comment")) {
                        Comment = true;

                        com = new Comment();

                        temp = attributes.getValue("CID");
                        com.setCID(temp);
                        temp = attributes.getValue("CUSERID");
                        com.setCUSERID(temp);
                        temp = attributes.getValue("CGOLD");
                        com.setCGOLD(temp);
                        temp = attributes.getValue("CGOLD_YN");
                        com.setCGOLD_YN(temp);
                    }
                    if (qName.equalsIgnoreCase("QSubject")) {
                        temp = "";
                        QSubject = true;
                    }
                    if (qName.equalsIgnoreCase("CSubject")) {
                        temp = "";
                        CSubject = true;
                    }
                    if (qName.equalsIgnoreCase("QBody")) {
                        temp = "";
                        QBody = true;
                    }
                    if (qName.equalsIgnoreCase("CBody")) {
                        temp = "";
                        CBody = true;
                    }
                }

                @Override
                public void endElement(String uriString, String localName,
                        String qName) throws SAXException {
                    //vaghti ke be payane yek tag miresad in function ravasote parser call mishavad
                    if (qName.equalsIgnoreCase("Question")) {
                        Question = false;
                        q.setComments(coms);
                        Que.add(q);
                    }
                    if (qName.equalsIgnoreCase("Comment")) {
                        Comment = false;
                    }
                    if (qName.equalsIgnoreCase("QSubject")) {
//                        try {
//                            temp = temp.toLowerCase();
//                            temp = Preprocess.remove_junks(temp);
//                            temp = Preprocess.tokenizerStanfordPTBTokenize(temp);
//                        } catch (FileNotFoundException ex) {
//                            Logger.getLogger(DataLoader.class.getName()).log(Level.SEVERE, null, ex);
//                        } catch (IOException ex) {
//                            Logger.getLogger(DataLoader.class.getName()).log(Level.SEVERE, null, ex);
//                        } catch (Exception ex) {
//                            Logger.getLogger(DataLoader.class.getName()).log(Level.SEVERE, null, ex);
//                        }
                        q.setQSubject(temp);
//                        jout.println(temp);
                        QSubject = false;
                    }
                    if (qName.equalsIgnoreCase("CSubject")) {
//                        try {
//                            temp = temp.toLowerCase();
//                            temp = Preprocess.remove_junks(temp);
//                            temp = Preprocess.tokenizerStanfordPTBTokenize(temp);
//                        } catch (FileNotFoundException ex) {
//                            Logger.getLogger(DataLoader.class.getName()).log(Level.SEVERE, null, ex);
//                        } catch (IOException ex) {
//                            Logger.getLogger(DataLoader.class.getName()).log(Level.SEVERE, null, ex);
//                        } catch (Exception ex) {
//                            Logger.getLogger(DataLoader.class.getName()).log(Level.SEVERE, null, ex);
//                        }
                        com.setCSUBJECT(temp);
//                        jout.println(temp);
                        CSubject = false;
                    }
                    if (qName.equalsIgnoreCase("QBody")) {
//                        try {
//                            temp = temp.toLowerCase();
//                            temp = Preprocess.remove_junks(temp);
//                            temp = Preprocess.tokenizerStanfordPTBTokenize(temp);
//                        } catch (FileNotFoundException ex) {
//                            Logger.getLogger(DataLoader.class.getName()).log(Level.SEVERE, null, ex);
//                        } catch (IOException ex) {
//                            Logger.getLogger(DataLoader.class.getName()).log(Level.SEVERE, null, ex);
//                        } catch (Exception ex) {
//                            Logger.getLogger(DataLoader.class.getName()).log(Level.SEVERE, null, ex);
//                        }
                        q.setQBody(temp);
//                        jout.println(temp);
                        QBody = false;
                    }
                    if (qName.equalsIgnoreCase("CBody")) {
//                        try {
////                            temp = temp.toLowerCase();
////                            temp = Preprocess.remove_junks(temp);
////                            temp = Preprocess.tokenizerStanfordPTBTokenize(temp);
//                        } catch (FileNotFoundException ex) {
//                            Logger.getLogger(DataLoader.class.getName()).log(Level.SEVERE, null, ex);
//                        } catch (IOException ex) {
//                            Logger.getLogger(DataLoader.class.getName()).log(Level.SEVERE, null, ex);
//                        } catch (Exception ex) {
//                            Logger.getLogger(DataLoader.class.getName()).log(Level.SEVERE, null, ex);
//                        }
                        com.setCBODY(temp);
//                        jout.println(temp);
                        CBody = false;
//                        jout.println(com.toString());
                        coms.add(com);
                    }
                }

                public void characters(char ch[], int start, int length) throws SAXException {
                    //ba residane be har value i ke dar beyne tag ha gharar darad in function run mishavad
                    //if haye zir baraye mohtaviate har kodom az tag ha check mishavand
                    //dar har kodom information e morede niaz ro toye yek object az class repository zakhire mikonim
                    if (QSubject) {
                        temp += new String(ch, start, length);
                    }
                    if (CSubject) {
                        temp += new String(ch, start, length);
                    }
                    if (QBody) {
                        temp += new String(ch, start, length);
                    }
                    if (CBody) {
                        temp += new String(ch, start, length);
                    }
                }
            };
            sax_parser.parse(new File(xmlDir), handle);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return Que;
    }

    public static ArrayList<Question> jsonLoader(String jsonFileAddress) throws Exception {
        ArrayList<String> jsons = tools.util.file.Reader.getStringFromTextFile(jsonFileAddress, true);
        ArrayList<Question> que = new ArrayList<Question>();
        for (String json : jsons) {
            que.add(Question.getFromJsonString(json));
        }
        return que;
    }

    public static void loadXmlToJson(String xmlDir, String resultJsonFileAddress) throws CloneNotSupportedException, IOException {

        ArrayList<Question> que = null;
        DataLoader load = new DataLoader();
        que = load.xmlLoader(xmlDir);
        int i = 0;
        PrintWriter writer = tools.util.file.Write.getPrintWriter(resultJsonFileAddress, false);
        tools.util.Time.setStartTimeForNow();
        for (Question q : que) {
            writer.println(q.toJsonString());
            i++;
            if (i % 500 == 0) {
                jout.println("i is: " + i + " time: " + tools.util.Time.getTimeLengthForNow() / 1000 + "s");
            }
        }
        writer.close();
    }

    public static void normalizeJson(String inputJsonFileAddress, String resultJsonFileAddress) throws CloneNotSupportedException, IOException, Exception {

        ArrayList<Question> que = DataLoader.jsonLoader(inputJsonFileAddress);
        int i = 0;
        PrintWriter writer = tools.util.file.Write.getPrintWriter(resultJsonFileAddress, false);
        tools.util.Time.setStartTimeForNow();
        for (Question q : que) {
            try {
                q.setQSubject(Preprocess.simpleNormalize(q.getQSubject()));
                q.setQBody(Preprocess.simpleNormalize(q.getQBody()));
                LinkedList<Comment> comments = new LinkedList<Comment>();
                for (Comment comment : q.getComments()) {
                    comment.setCSUBJECT(Preprocess.simpleNormalize(comment.getCSUBJECT()));
                    comment.setCBODY(Preprocess.simpleNormalize(comment.getCBODY()));
                    comments.add(comment);
                }
                q.setComments(comments);
            } catch (ExceptionInInitializerError ex) {
                jout.println(q.getQSubject() + "\t" + q.getQBody());
            }
            writer.println(q.toJsonString());
            i++;
            if (i % 500 == 0) {
                jout.println("i is: " + i + " time: " + tools.util.Time.getTimeLengthForNow() / 1000 + "s");
            }
        }
        writer.close();
    }

    public static void tokenizeJson(String inputJsonFileAddress, String resultJsonFileAddress) throws CloneNotSupportedException, IOException, Exception {
//        tools.util.Directory.create(resultJsonFileAddress);
        ArrayList<Question> que = DataLoader.jsonLoader(inputJsonFileAddress);
        int i = 0;
        PrintWriter writer = tools.util.file.Write.getPrintWriter(resultJsonFileAddress, false);
        tools.util.Time.setStartTimeForNow();
        for (Question q : que) {
            q.setQSubject(Preprocess.tokenizerStanfordPTBTokenize(q.getQSubject()));
            q.setQBody(Preprocess.tokenizerStanfordPTBTokenize(q.getQBody()));
            LinkedList<Comment> comments = new LinkedList<Comment>();
            for (Comment comment : q.getComments()) {
                comment.setCSUBJECT(Preprocess.tokenizerStanfordPTBTokenize(comment.getCSUBJECT()));
                comment.setCBODY(Preprocess.tokenizerStanfordPTBTokenize(comment.getCBODY()));
                comments.add(comment);
            }
            q.setComments(comments);
            writer.println(q.toJsonString());
            i++;
            if (i % 500 == 0) {
                jout.println("i is: " + i + " time: " + tools.util.Time.getTimeLengthForNow() / 1000 + "s");
            }
        }
        writer.close();
    }

    public static void jsonToTextFile(String inputJsonFileAddress, String resultTextFileAddress) throws Exception {
        PrintWriter writer = tools.util.file.Write.getPrintWriter(resultTextFileAddress, false);
        ArrayList<Question> que = DataLoader.jsonLoader(inputJsonFileAddress);
        for (Iterator<Question> it = que.iterator(); it.hasNext();) {
            Question question = it.next();
            writer.println(question.getQSubject());
            writer.println(question.getQBody());
            for (Iterator<Comment> it2 = question.getComments().iterator(); it2.hasNext();) {
                Comment comment = it2.next();
                writer.println(comment.getCSUBJECT());
                writer.println(comment.getCBODY());
            }
        }
    }

    public void loadStopWordsList() throws
            IllegalArgumentException, IllegalAccessException, InvocationTargetException,
            SecurityException, NoSuchMethodException {

        stopWords = tools.util.file.Reader.getStringFromTextFile(stopWordsFilePath, true);
    }
    
    public void loadTrainSetOfQuestions(String questionTrainSetFilePath) throws FileNotFoundException, IOException {
        BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(questionTrainSetFilePath)));
        String str;
        while((str = bufferedReader.readLine()) != null) {
            
        }
    }

    public static HashMap<String, UserReputation> loadLinesOfTextFileSeperatedByTab(String textFilePath) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, SecurityException, NoSuchMethodException {

        int index;
        HashMap<String, UserReputation> userCategories = new HashMap<>();
        ArrayList<String> stringFromTextFile = tools.util.file.Reader.getStringFromTextFile(textFilePath, true);
        for(String str : stringFromTextFile) {
            str = str.trim();
            UserReputation user = new UserReputation();
            userCategories.put(str.substring(0, index = str.indexOf("\t")), user);
            user.getReputations().addAll(Arrays.asList(str.substring(index + 1, str.length()).split("\t")));
            
        }
        if (stringFromTextFile.size() > 0) {
            return userCategories;
        } else {
            return new HashMap<>();
        }
    }

    public ArrayList<String> getStopWords() {
        return stopWords;
    }

    public void setStopWords(ArrayList<String> stopWords) {
        this.stopWords = stopWords;
    }

}
