package semevalav;

import datatypes.Comment;
import datatypes.FVEstimator;
import datatypes.Question;
import org.xml.sax.SAXException;
import tools.processors.Analyzer;
import tools.processors.Distribution;
import tools.processors.Preprocess;
import tools.util.file.Write;
import tools.util.sort.Collection;
import weka.classifiers.Classifier;
import weka.classifiers.meta.Bagging;
import weka.classifiers.trees.J48;
import weka.core.Instances;
import weka.core.converters.ArffLoader;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.ParserConfigurationException;
import java.io.*;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;

//import tools.util.file.Write;
//import tools.util.sort.Collection;

/**
 * @author epcpu
 */
public class Semeval_AV {

    /**
     * @param args the command line arguments
     */
    private static String dir = "resources/qa.json";
    private static String jazzyDictionaryFileAddress = "resources/english.0";
    private static PrintStream jout = System.out;
    private static ArrayList<Question> que;
    private static ArrayList<String> stopWords;
    private static String trainFileAddress = "result/tokenize/freqDepartionListWiseFeatureTableForTest3.csv";//freqDepartionListWiseFeatureTableForLastPerlTest.csv";//"result/submission/adding test/adding train.arff";
    private static String testFileAddress = "result/test/2freqDepartionListWise-test-FeatureTableForTest.csv";//"result/submission/adding test/adding test.arff";
    private static String resultFileAddress = "result/test/departionResult.txt";//"result/submission/adding test/Result.txt";
    private static String tempTestFileAddress = "result/test/2freqDepartionListWise-test-FeatureTableTemp.csv";//"result/submission/adding test/tempadding test.arff";

    public static void main(String[] args) throws IOException, JAXBException, SAXException, ParserConfigurationException, Exception {
//        String inputFileAddress = "D:/compilers/programming/java/semeval_AV/datasets/CQA-QL-train.xml";
//        test1();
//        test4(inputFileAddress, "quoteDistribution.txt");
//        test5();
//        test6("resources/xmlTextFile.txt");
//        getTokenDistributionFromFileToFile("resources/xmlTextFile.txt", "xmlFileTokenDistribution.txt");
//        getBigramTest("resources/xmlTextFile.txt", "xmlFileTokenTokenDistribution.txt");
        ///////////////////////
//        String str = "I'm";
//        String readFileAddress = "result/qa.json.txt";
//        String resultFileAddress = str+".prefix.MatchStringBigram.txt";
//        Analyzer.getPrefixTermOccurencesBigram(str, readFileAddress, " ", "result/testTokenToken/" + str.replace("'", "takquotation.") + ".prefix.MatchStringBigram.txt");
//        Analyzer.getPostfixTermOccurencesBigram(str, readFileAddress, " ", "result/testTokenToken/" + str.replace("'", "takquotation.") + ".postfix.MatchStringBigram.txt");
//        que = loader.DataLoader.jsonLoader(dir);
//        DataLoader.loadXmlToJson("datasets/CQA-QL-train.xml", "result/qa.json");//train
//        DataLoader.loadXmlToJson("datasets/test_task3/test_task3_English.xml", "result/test-qa.json");//test
//        DataLoader.loadXmlToJson("datasets/CQA-QL-devel-input.xml", "result/devel-qa.json");//development


//        DataLoader.tokenizeJson("result/test/labeledTest.json", "result/test/test-qa-tokenize.json");
////////////////////////for normalizing
//        String resultAddress = "result/tokenize-normalize/test-qa-tokenize-normalize.json";
//        DataLoader.normalizeJson("result/tokenize/test-qa-tokenize.json", resultAddress);
//        DataLoader.jsonToTextFile(resultAddress, resultAddress + ".txt");
//        Distribution.getFileTokenDistribution(resultAddress + ".txt", resultAddress + ".TokenDistribution");
//        getTokenDistributionFromFileToFile(resultAddress + ".txt", resultAddress + ".TokenDistribution");
//        Analyzer.getCharDistribution(resultAddress + ".txt", resultAddress + ".Distribution");
//        getBigramTest(resultAddress + ".txt", resultAddress + ".BigramDistribution");
///////////////////////////testing tokenizer
//        Preprocess.testTokenizer();
///////////////////////////
//        getTrigramTest("result/tokenize/qa-tokenize.json.txt", "result/tokenize/qa-tokenizeTrigram.json.txt");
//        Preprocess.jazzySpellCheckerTest(jazzyDictionaryFileAddress, "'s", 2);
        //////////////////////////spell checker performance test
//        spellCheckerPerformanceTest(jazzyDictionaryFileAddress, 1);
        ////////for train
        ////
//        simpleWekaInputCsvCreator("result/qa.json", "result/newTfIdfFeatureTable.csv");//train
//        listWiseWekaInputCsvCreator("result/qa.json", "result/tfIdfListWiseFeatureTable.csv");//train
//        simpleWekaInputCsvCreator("result/tokenize/qa-tokenize.json", "result/tokenize/freqFeatureTable.csv");//train
//        listWiseWekaInputCsvCreator("result/tokenize/qa-tokenize.json", "result/tokenize/tfIdfListWiseFeatureTable.csv");//train
        departionListWiseWekaInputCsvCreator("result/tokenize/qa-tokenize.json", "result/tokenize/final/freqDepartionListWiseFeatureTableFinalCompleteImpl2.csv");//train
//        simpleWekaInputCsvCreator("result/tokenize-normalize/qa-tokenize-normalize3.json", "result/tokenize-normalize/FeatureTable.csv");//train
//        listWiseWekaInputCsvCreator("result/tokenize-normalize/qa-tokenize-normalize3.json", "result/tokenize-normalize/simpleTfIdfListWiseFeatureTable.csv");//train
        ////
//        simpleWekaInputCsvCreator("result/test/labeledTest.json", "result/test/simple-test-FeatureTable.csv");//test
//        listWiseWekaInputCsvCreator("result/test/labeledTest.json", "result/test/tfIdfListWise-test-FeatureTable.csv");//test
//        departionListWiseWekaInputCsvCreator("result/test/labeledTest.json", "result/test/final/freqDepartionListWise-test-FeatureTableFinalCompleteImpl.csv");//test
//        DiscreteWekaInputCsvCreator("result/qa.json", "result/Discrete-FeatureTable.csv");//it's not so good for current systems. the simple one is more perfect
        ////////
        //for test
//        String train = "result/FeatureTable.arff";
//        String testjson = "result/test-qa.json";
//        String test = "result/test-FeatureTable.arff";
//        String testOutput = "result/testResult.txt";
//        simpleWekaInputCsvCreator(testjson, testCSV);
//        wekaTest(trainFileAddress, testFileAddress, resultFileAddress, tempTestFileAddress);
//        aminWekaTest(train, train, testOutput);
//        DiscreteWekaInputCsvCreator("result/qa.json", "result/Discrete-FeatureTable.csv");
//        wekaTest("result/test classifier code/segment-challenge.arff", "result/test classifier code/segment-test.arff", "result/test classifier code/result.txt", "result/test classifier code/segment-testTemp.arff");
        //language model files normalizing
//        String questionBody = "datasets/dump_QL_all_question_body.txt/dump_QL_all_question_body.txt";
//        String commentBody = "datasets/dump_QL_all_comment_body.txt/dump_QL_all_comment_body.txt";
//        DataInputStream questionFIS = new DataInputStream(new FileInputStream(questionBody));
//        DataInputStream commentFIS = new DataInputStream(new FileInputStream(commentBody));
//        DataOutputStream questionFOS = new DataOutputStream(new FileOutputStream(questionBody + ".normalized"));
//        DataOutputStream commentFOS = new DataOutputStream(new FileOutputStream(commentBody + ".normalized"));
//
//        String str = "";
//        while((str = questionFIS.readLine()) != null) {
//            str = CharMatcher.ASCII.retainFrom(str);
//            str = Preprocess.tokenizerStanfordPTBTokenize(str);
//            str = Preprocess.simpleNormalize(str);
//            questionFOS.write((str + "\n").getBytes());
//            questionFOS.flush();
//        }
//        while((str = commentFIS.readLine()) != null) {
//            str = CharMatcher.ASCII.retainFrom(str);
//            str = Preprocess.tokenizerStanfordPTBTokenize(str);
//            str = Preprocess.simpleNormalize(str);
//            commentFOS.write((str + "\n").getBytes());
//            commentFOS.flush();
//        }
        //////////////////////
//        simpleBagOfWords("result/qa.json","result/qa-BagOfWordsType.csv");
        //////////////////////comments trends analyzer
//        Semeval_AV.datasetAnalysis("result/qa.json");
        //////////////////////
//        Semeval_AV.assignTestClassLabels("result/test/test-qa.json", "result/test/CQA-QL-test-gold.txt", "result/test/labeledTest.json");
    }

    private static void wekaTest(String trainFileAddress, String testFileAddress, String resultFileAddress, String tempTestFileAddress) throws IOException, Exception {

        BufferedWriter out = Files.newBufferedWriter(Paths.get(resultFileAddress));
        ArffLoader loader = new ArffLoader();
//        CSVLoader loader = new CSVLoader();
        File trainFile = new File(trainFileAddress);
        loader.setFile(trainFile);
        Instances trainData = loader.getDataSet();
        trainData.delete(0);
        trainData.setClassIndex(trainData.numAttributes() - 1);

        File testFile = new File(testFileAddress);
//        CSVLoader loader2 = new CSVLoader();
        ArffLoader loader2 = new ArffLoader();
        loader2.setFile(testFile);
        Instances testData = loader2.getDataSet();
        testData.setClassIndex(testData.numAttributes() - 1);

        File tempTestFile = new File(tempTestFileAddress);
//        CSVLoader loader3 = new CSVLoader();
        ArffLoader loader3 = new ArffLoader();
        loader3.setFile(tempTestFile);
        Instances tempTestData = loader3.getDataSet();

        J48 baseClassifier = new J48();
//        MyAdaBoostM1V3 classifier=new MyAdaBoostM1V3();
//        RandomForest classifier=new RandomForest();
        Bagging classifier = new Bagging();
        classifier.setClassifier(baseClassifier);

        classifier.buildClassifier(trainData);

        int trueSample = 0;
//        EvaluationUtils evalUtil = new EvaluationUtils();
//        evalUtil.getCVPredictions(new J48(), trainData, testData);
        for (int i = 0; i < testData.numInstances(); i++) {
            weka.core.Instance curInstance = testData.instance(i);
            double classInd = classifier.classifyInstance(curInstance);
//            System.out.println(curInstance.stringValue(0) + "\t" + classInd + "\t"
//                    + trainData.attribute(trainData.classIndex()).value((int) classInd));
            weka.core.Instance tempCurInstance = tempTestData.instance(i);
//            String expectedValue = curInstance.attribute(curInstance.classIndex()).value((int) curInstance.classValue());
            String predictedValue = trainData.attribute(trainData.classIndex()).value((int) classInd);
//            String output = tempCurInstance.stringValue(0) + "\t" + predictedValue;
//            System.out.println(output);
//            out.println(output);
            if (curInstance.attribute(curInstance.classIndex()).value((int) curInstance.classValue()).equalsIgnoreCase(predictedValue)) {
                trueSample++;
            }
        }
        System.out.println("accuracy of the system is: " + ((double) trueSample / (double) testData.numInstances()));
    }

    public static void aminWekaTest(String trainSetFileAddress, String testSetFileAddress, String resultFileAddress) throws Exception {
        BufferedReader trainSetBFR = Files.newBufferedReader(Paths.get(trainSetFileAddress));
        BufferedReader testSetBFR = Files.newBufferedReader(Paths.get(testSetFileAddress));

        // Create an empty training set
//        Instances trainingSet = new Instances("Rel", fvWekaAttributes, 10);

        Instances trainingSet = new Instances(trainSetBFR);
        Instances testSet = new Instances(testSetBFR);
        trainingSet.setClassIndex(trainingSet.numAttributes() - 1);
        testSet.setClassIndex(testSet.numAttributes() - 1);

        Classifier j48Model = new J48();
        j48Model.buildClassifier(trainingSet);

        // label instances
        for (int i = 0; i < testSet.numInstances(); i++) {
            double clsLabel = j48Model.classifyInstance(testSet.instance(i));
            testSet.instance(i).setClassValue(clsLabel);
        }
        // save labeled data
        BufferedWriter writer = Files.newBufferedWriter(Paths.get(resultFileAddress));
        writer.write(testSet.toString());
        writer.flush();
        writer.close();
        trainSetBFR.close();
        testSetBFR.close();
    }

//    public static void csvToArff(String csvFileAddress, String arffFileAddress) throws Exception {
//        CSVLoader loader = new CSVLoader();
//        loader.setSource(new File(csvFileAddress));
//        Instances csvDataSet = loader.getDataSet();
//
//        ArffSaver saver = new ArffSaver();
//        saver.setInstances(csvDataSet);
//        saver.setFile(new File(arffFileAddress));
//        saver.writeBatch();
//    }

    private static void test2(String inputFileAddress, String resultFileAddress) throws IOException {
        Analyzer.getCharOccurencesTerm('\'', inputFileAddress, "(\\s|\\t)", resultFileAddress);
    }

    private static void test3(String inputFileAddress, String resultFileAddress) throws IOException {
        Analyzer.getStringOccurencesTerm("\'s", inputFileAddress, "(\\s|\\t)", resultFileAddress);
    }

    private static void test4(String inputFileAddress, String resultFileAddress) throws IOException {
        Analyzer.getEndStringOccurencesTermReplacedWithString("\'d", "would", inputFileAddress, "(\\s|\\t)", resultFileAddress);
    }

//    private static void test5() throws IOException {
//        DataLoader load = new DataLoader();
//        load.xmlLoader();
//        FVEstimator featureTableCreator = new FVEstimator(que);
//        featureTableCreator.listWiseEstimator();
//    }

    private static void getTokenDistributionFromFileToFile(String inputFileAddress, String resultFileAddress) throws IOException {
        HashMap<String, Long> tokens = Distribution.getFileTokenDistribution(inputFileAddress, " ");
        Write.mapToTextFileSortedByValue(tokens, resultFileAddress, Collection.SortType.DECREMENTAL, Write.HashMapWriteType.KEYVALUE);
    }

    private static void getBigramTest(String inputFileAddress, String resultFileAddress) throws IOException {
        HashMap<String, Long> tokens = Distribution.getFileTokenTokenDistribution(inputFileAddress, " ");
        Write.mapToTextFileSortedByValue(tokens, resultFileAddress, Collection.SortType.DECREMENTAL, Write.HashMapWriteType.KEYVALUE);
    }

    private static void getTrigramTest(String inputFileAddress, String resultFileAddress) throws IOException {
        HashMap<String, Long> tokens = Distribution.getFileTrigramDistribution(inputFileAddress, " ");
        Write.mapToTextFileSortedByValue(tokens, resultFileAddress, Collection.SortType.DECREMENTAL, Write.HashMapWriteType.KEYVALUE);
    }

    private static void getMatchStringBigramTest(String matchString, String inputFileAddress, String resultFileAddress) throws IOException {
        Analyzer.getStringOccurencesBigram(matchString, inputFileAddress, " ", resultFileAddress);
    }

    private static void spellCheckerPerformanceTest(String fileAddress, int threshold) throws IOException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        int i = 0;
        tools.util.Time.setStartTimeForNow();
        for (String term : tools.util.file.Reader.getStringFromTextFile(fileAddress, false)) {
            i++;
            Preprocess.jazzySpellCheckerTest(jazzyDictionaryFileAddress, term, threshold);
            if (i % 20 == 0) {
                System.out.println(i + " spent time: " + tools.util.Time.getTimeLengthForNow() + "ms");
            }
        }
        System.out.println(i + " spent time: " + tools.util.Time.getTimeLengthForNow() / 1000 + "s");
    }

    private static void simpleWekaInputCsvCreator(String dataSourceFileAddress, String featureTableFileAddress) throws Exception {
        que = loader.DataLoader.jsonLoader(dataSourceFileAddress);
        FVEstimator fvEstimator = new FVEstimator(que);
        fvEstimator.simpleEstimator(featureTableFileAddress);
        jout.println("Operation Complete.");
    }

    private static void DiscreteWekaInputCsvCreator(String dataSourceFileAddress, String featureTableFileAddress) throws Exception {
        que = loader.DataLoader.jsonLoader(dataSourceFileAddress);
        FVEstimator fvEstimator = new FVEstimator(que);
        fvEstimator.DescritisedEstimator(featureTableFileAddress);
        jout.println("Operation Complete.");
    }

    private static void simpleBagOfWords(String dataSourceFileAddress, String featureTableFileAddress) throws Exception {
        que = loader.DataLoader.jsonLoader(dataSourceFileAddress);
        FVEstimator fvEstimator = new FVEstimator(que);
        fvEstimator.bagOfWordsOutput(featureTableFileAddress);
        jout.println("Operation Complete.");
    }

    private static void listWiseWekaInputCsvCreator(String dataSourceFileAddress, String featureTableFileAddress) throws Exception {
        que = loader.DataLoader.jsonLoader(dataSourceFileAddress);
        FVEstimator fvEstimator = new FVEstimator(que);
        fvEstimator.listWiseEstimator(featureTableFileAddress);
        jout.println("Operation Complete.");
    }

    private static void departionListWiseWekaInputCsvCreator(String dataSourceFileAddress, String featureTableFileAddress) throws Exception {
        que = loader.DataLoader.jsonLoader(dataSourceFileAddress);
        FVEstimator fvEstimator = new FVEstimator(que);
        fvEstimator.departionFilteringListWiseEstimator(featureTableFileAddress);
        jout.println("Operation Complete.");
    }

    private static void datasetAnalysis(String dataSourceFileAddress) throws Exception {

        que = loader.DataLoader.jsonLoader(dataSourceFileAddress);
        HashMap<String, Integer> trend = new HashMap<>();
        for (Question q : que) {
            jout.println("Question Id: " + q.getQID());
            for (Comment c : q.getComments()) {
                if (!c.getCUSERID().equalsIgnoreCase(q.getQUserId())) {
                    if (!trend.containsKey(c.getCGOLD())) {
                        trend.put(c.getCGOLD(), 1);
                    } else {
                        trend.put(c.getCGOLD(), trend.get(c.getCGOLD()) + 1);
                    }
                } else if (!trend.isEmpty()) {
                    for (String CGold : trend.keySet()) {
                        jout.println(CGold + "-> #" + trend.get(CGold));
                    }
                    trend.clear();
                    jout.println("+++++++++++");
                }
            }
            trend.clear();
            jout.println("******************************");
        }
    }

    public static void assignTestClassLabels(String testFilePath, String testGoldFilePath, String outPutFilePath) throws Exception {
        que = loader.DataLoader.jsonLoader(testFilePath);
        BufferedReader fis = new BufferedReader(new FileReader(testGoldFilePath));
        PrintWriter out = Write.getPrintWriter(outPutFilePath, false);
        String str;
        HashMap<String, String> goldLabels = new HashMap<>();
        while ((str = fis.readLine()) != null) {
            String[] strTemp = str.split("\t");
            goldLabels.put(strTemp[0], strTemp[1]);
        }

        for (Question q : que) {
            for (Comment c : q.getComments()) {
                c.setCGOLD(goldLabels.get(c.getCID()));
            }
            out.println(q.toJsonString());
        }
    }

    public static ArrayList<Question> getQue() {
        return que;
    }

    public static void setQue(ArrayList<Question> Que) {
        Semeval_AV.que = Que;
    }

    public static String getJazzyDictionaryFileAddress() {
        return jazzyDictionaryFileAddress;
    }

    public static void setJazzyDictionaryFileAddress(String jazzyDictionaryFileAddress) {
        Semeval_AV.jazzyDictionaryFileAddress = jazzyDictionaryFileAddress;
    }

}
