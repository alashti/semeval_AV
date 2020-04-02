/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools.processors;

import edu.stanford.nlp.util.ConfusionMatrix;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import weka.classifiers.Classifier;
import weka.classifiers.Evaluation;
import weka.classifiers.meta.Bagging;
import weka.classifiers.trees.J48;
import weka.core.Attribute;
import weka.core.Debug.Random;
import weka.core.FastVector;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.converters.ArffLoader;
import weka.core.converters.CSVLoader;

/**
 *
 * @author epcpu
 */
public class MLAlgorithms {

    private static Classifier getClassifier() {
        J48 baseClassifier = new J48();
        Bagging bagging = new Bagging();
        bagging.setClassifier(baseClassifier);
        return bagging;
    }

    private static Classifier getJ48Classifier() {
        return new J48();
    }

    private static Instances getInstancesOfFeatureTable(String featureTableFilePath) throws IOException {
        ArffLoader loader = new ArffLoader();
        File file = new File(featureTableFilePath);
        loader.setFile(file);
        Instances instances = loader.getDataSet();
        return instances;
    }

    private void baggingJ48(String trainFileAddress, String testFileAddress, String tempTestFileAddress, String resultOutputPath) throws IOException, Exception {
        PrintWriter pw = tools.util.file.Write.getPrintWriter(resultOutputPath, true);

        Instances instancesOfTrain = MLAlgorithms.getInstancesOfFeatureTable(trainFileAddress);
        instancesOfTrain.setClassIndex(instancesOfTrain.numAttributes() - 1);

        Instances instancesOfTest = MLAlgorithms.getInstancesOfFeatureTable(testFileAddress);
        instancesOfTest.setClassIndex(instancesOfTest.numAttributes() - 1);

        Instances tempTestData = MLAlgorithms.getInstancesOfFeatureTable(tempTestFileAddress);

        Classifier classifier = MLAlgorithms.getClassifier();

        classifier.buildClassifier(instancesOfTrain);

        for (int i = 0; i < instancesOfTest.numInstances(); i++) {
            weka.core.Instance curInstance = instancesOfTest.instance(i);
            double classInd = classifier.classifyInstance(curInstance);
            weka.core.Instance tempCurInstance = tempTestData.instance(i);
            pw.println(tempCurInstance.stringValue(0) + "\t" + instancesOfTrain.attribute(instancesOfTrain.classIndex()).value((int) classInd));
        }
    }

    public double[][] nFoldCrossValidationByFeatureTable(String featuresTableFilePath) throws IOException, Exception {
        Instances inst = MLAlgorithms.getInstancesOfFeatureTable(featuresTableFilePath);
        return this.nFoldCrossValidationByInstances(inst);
    }

    public double[][] nFoldCrossValidationByInstances(Instances inst) throws IOException, Exception {
        inst.deleteAttributeAt(0);
        inst.setClassIndex(inst.numAttributes() - 1);
        Random r = new Random(1);

        Classifier classifier = MLAlgorithms.getClassifier();

        Evaluation eval = new Evaluation(inst);
        eval.crossValidateModel(classifier, inst, 2, r);
        double[][] confusionMatrix = eval.confusionMatrix();
        System.out.println(eval.correct() / inst.numInstances());
        return confusionMatrix;
    }

    public void balancerBasedOnMergeClassSamples(String featuresTableFilePath, String secondLayerfeaturesTableFilePath) throws IOException, Exception {
        File featuresTable = new File(featuresTableFilePath);
        CSVLoader loader1 = new CSVLoader();
        loader1.setFile(featuresTable);
        Instances inst = loader1.getDataSet();
        inst.setClassIndex(inst.numAttributes() - 1);

        Instances goodTrainLevel1 = new Instances(inst), badTrainLevel1 = new Instances(inst);
        goodTrainLevel1.delete();
        badTrainLevel1.delete();
        HashMap<Instance, String> goodIds = new HashMap<>();
        HashMap<Instance, String> badIds = new HashMap<>();

        Enumeration enumerateInstances = inst.enumerateInstances();
        while (enumerateInstances.hasMoreElements()) {
            Instance instance = (Instance) enumerateInstances.nextElement();
            if (inst.classAttribute().value((int) instance.classValue()).equalsIgnoreCase("Good")
                    || inst.classAttribute().value((int) instance.classValue()).equalsIgnoreCase("Potential")) {
                goodTrainLevel1.add(instance);
            } else if (inst.classAttribute().value((int) instance.classValue()).equalsIgnoreCase("Bad")
                    || inst.classAttribute().value((int) instance.classValue()).equalsIgnoreCase("Dialogue")) {
                badTrainLevel1.add(instance);
            }
        }

        Attribute classAttribute = inst.classAttribute();
        int dialogue = inst.classAttribute().indexOfValue("Dialogue");
        int potential = inst.classAttribute().indexOfValue("Potential");
        inst.renameAttributeValue(classAttribute, "Dialogue", "Bad");
        inst.renameAttributeValue(classAttribute, "Potential", "Good");
        inst.deleteAttributeAt(0);

        Classifier classifierLevel1 = MLAlgorithms.getJ48Classifier();
        classifierLevel1.buildClassifier(inst);

        goodTrainLevel1.deleteAttributeAt(0);
        Classifier classifierLevel2Good = MLAlgorithms.getJ48Classifier();
        classifierLevel2Good.buildClassifier(goodTrainLevel1);

        badTrainLevel1.deleteAttributeAt(0);
        Classifier classifierLevel2Bad = MLAlgorithms.getJ48Classifier();
        classifierLevel2Good.buildClassifier(badTrainLevel1);

        File secondLayerFeatureTable = new File(secondLayerfeaturesTableFilePath);
        CSVLoader loader2 = new CSVLoader();
        loader2.setFile(secondLayerFeatureTable);
        Instances instCloneForSecondLayer = loader2.getDataSet();
        instCloneForSecondLayer.setClassIndex(instCloneForSecondLayer.numAttributes() - 1);

        for (int i = 0; i < inst.numInstances(); i++) {
            weka.core.Instance curInstance = inst.instance(i);
            double classInd = classifierLevel1.classifyInstance(curInstance);
            String predictedClassLabel = inst.attribute(inst.classIndex()).value((int) classInd);

            if (predictedClassLabel.equalsIgnoreCase("Good")) {
                String predictedSampleId = instCloneForSecondLayer.attribute(0).value(
                        (int) instCloneForSecondLayer.instance(i).value(0));
//                Instance good = instCloneForSecondLayer.instance(i);
                goodTrainLevel1.add(instCloneForSecondLayer.instance(i));
                goodIds.put(curInstance, predictedSampleId);
            } else if (predictedClassLabel.equalsIgnoreCase("Bad")) {
                String predictedSampleId = instCloneForSecondLayer.attribute(0).value(
                        (int) instCloneForSecondLayer.instance(i).value(0));
//                Instance bad = instCloneForSecondLayer.instance(i);
                badTrainLevel1.add(instCloneForSecondLayer.instance(i));
                badIds.put(curInstance, predictedSampleId);
            }
        }
        goodTrainLevel1.deleteWithMissing(0);
        badTrainLevel1.deleteWithMissing(0);
        int correct = 0, total = 0;
        Classifier classifier2 = MLAlgorithms.getJ48Classifier();
        classifier2.buildClassifier(goodTrainLevel1);
        for (int i = 0; i < goodTrainLevel1.numInstances(); i++) {
            weka.core.Instance curInstance = goodTrainLevel1.instance(i);
            double classInd = classifier2.classifyInstance(curInstance);
            String predictedClassLabel = goodTrainLevel1.attribute(goodTrainLevel1.classIndex()).value((int) classInd);
            if (predictedClassLabel.equalsIgnoreCase(goodTrainLevel1.attribute(goodTrainLevel1.classIndex()).value(i))) {
                correct++;
            }
            total++;
        }
        Classifier classifier3 = MLAlgorithms.getJ48Classifier();
        classifier3.buildClassifier(badTrainLevel1);
        for (int i = 0; i < badTrainLevel1.numInstances(); i++) {
            weka.core.Instance curInstance = badTrainLevel1.instance(i);
            double classInd = classifier3.classifyInstance(curInstance);
            String predictedClassLabel = badTrainLevel1.attribute(badTrainLevel1.classIndex()).value((int) classInd);
            if (predictedClassLabel.equalsIgnoreCase(badTrainLevel1.attribute(badTrainLevel1.classIndex()).value(i))) {
                correct++;
            }
            total++;
        }
        System.out.println("Accuracy of two level system: " + (correct / total));
    }

    public void twoLayerClassifier222(String featuresTableFilePath, String testFeatureTable, String cgoldFilePath) throws IOException, Exception {

        File featuresTable = new File(featuresTableFilePath);
        CSVLoader loader1 = new CSVLoader();
        loader1.setFile(featuresTable);
        Instances inst = loader1.getDataSet();
        inst.setClassIndex(inst.numAttributes() - 1);

        Instances goodTrainLevel2 = new Instances(inst), badTrainLevel2 = new Instances(inst);
        goodTrainLevel2.setClassIndex(goodTrainLevel2.numAttributes() - 1);
        badTrainLevel2.setClassIndex(badTrainLevel2.numAttributes() - 1);
        goodTrainLevel2.delete();
        badTrainLevel2.delete();

        FastVector cgold = new FastVector();
        cgold.addElement("Bad");
        cgold.addElement("Good");
        Attribute attrib = new Attribute("CGOLDLvl1", cgold);
        inst.insertAttributeAt(attrib, inst.classIndex() + 1);
//        for(int i = 23; i < 46; i++) {
//            inst.deleteAttributeAt(i);
//        }

        Enumeration enumerateInstances = inst.enumerateInstances();
        while (enumerateInstances.hasMoreElements()) {
            Instance instance = (Instance) enumerateInstances.nextElement();
            if (inst.classAttribute().value((int) instance.classValue()).equalsIgnoreCase("Good")
                    || inst.classAttribute().value((int) instance.classValue()).equalsIgnoreCase("Potential")) {
                goodTrainLevel2.add(instance);
                instance.setValue(inst.classIndex() + 1, 1);
            } else if (inst.classAttribute().value((int) instance.classValue()).equalsIgnoreCase("Bad")
                    || inst.classAttribute().value((int) instance.classValue()).equalsIgnoreCase("Dialogue")) {
                badTrainLevel2.add(instance);
                instance.setValue(inst.classIndex() + 1, 0);
            }
        }
        goodTrainLevel2.deleteWithMissing(0);
        badTrainLevel2.deleteWithMissing(0);
        /////////////build 3 classifiers for two layers
        inst.setClassIndex(inst.classIndex() + 1);
        inst.deleteAttributeAt(inst.classIndex() - 1);
        Classifier classifierLevel1 = MLAlgorithms.getJ48Classifier();
        classifierLevel1.buildClassifier(inst);

        cgold.removeAllElements();
        cgold.addElement("Good");
        cgold.addElement("Potential");
        attrib = new Attribute("CGOLDLvl2Good", cgold);
        goodTrainLevel2.insertAttributeAt(attrib, goodTrainLevel2.classIndex() + 1);
        goodTrainLevel2.setClassIndex(goodTrainLevel2.classIndex() + 1);
        goodTrainLevel2.deleteAttributeAt(goodTrainLevel2.classIndex() - 1);
        Classifier classifierLevel2Good = MLAlgorithms.getJ48Classifier();
        classifierLevel2Good.buildClassifier(goodTrainLevel2);

        cgold.removeAllElements();
        cgold.addElement("Bad");
        cgold.addElement("Dialogue");
        attrib = new Attribute("CGOLDLvl2Bad", cgold);
        badTrainLevel2.insertAttributeAt(attrib, badTrainLevel2.classIndex() + 1);
        badTrainLevel2.setClassIndex(badTrainLevel2.classIndex() + 1);
        badTrainLevel2.deleteAttributeAt(badTrainLevel2.classIndex() - 1);
        Classifier classifierLevel2Bad = MLAlgorithms.getJ48Classifier();
        classifierLevel2Bad.buildClassifier(badTrainLevel2);
        /////////////
        File testTable = new File(testFeatureTable);
        CSVLoader loaderTest = new CSVLoader();
        loaderTest.setFile(testTable);
        Instances testInstances = loaderTest.getDataSet();
        testInstances.setClassIndex(testInstances.numAttributes() - 1);

        HashMap<String, String> qIdLabel = tools.util.file.Reader.getKeyValueFromTextFile(cgoldFilePath, Boolean.TRUE, "\t");
//        HashMap instanceLabel = new HashMap();
//        for(int i = 0; i < testInstances.numInstances(); i++) {
//            Instance instance = testInstances.instance(i);
//            double classInd = instance.classValue();
//            instanceLabel.put(i, System.out)
//        }
        Instances testTemplate = new Instances(testInstances);
        testInstances.deleteAttributeAt(0);

        Instances goodTestLevel2 = new Instances(goodTrainLevel2), badTestLevel2 = new Instances(badTrainLevel2);
        goodTestLevel2.delete();
        badTestLevel2.delete();

        int goodCorrect = 0, badCorrect = 0, total = 0;

        System.out.println("Level 1: ");
        if (!inst.equalHeaders(testInstances)) {
            System.out.println("Test Set and Inst do not have equal headers for level 1 of classifier.");
        }
        for (int i = 0; i < testInstances.numInstances(); i++) {
            Instance curInstance = testInstances.instance(i);
            double classInd = classifierLevel1.classifyInstance(curInstance);
//            System.out.println("predicted: " + classInd + ", expected: " + (int) curInstance.classValue());
            String expectedLabel = (String) qIdLabel.get(testTemplate.instance(i).stringValue(0));
            String predictedClassLabel = inst.attribute(inst.classIndex()).value((int) classInd);
//            String expectedLabel = testInstances.attribute(testInstances.classIndex()).value((int) curInstance.classValue());
            if (expectedLabel.equalsIgnoreCase("Dialogue")) {
                expectedLabel = "Bad";
            } else if (expectedLabel.equalsIgnoreCase("Potential")) {
                expectedLabel = "Good";
            }

            if (predictedClassLabel.equalsIgnoreCase("Good")) {
                goodTestLevel2.add(testInstances.instance(i));
                if (predictedClassLabel.equalsIgnoreCase(expectedLabel)) {
                    goodCorrect++;
                }
            } else if (predictedClassLabel.equalsIgnoreCase("Bad")) {
                badTestLevel2.add(testInstances.instance(i));
                if (predictedClassLabel.equalsIgnoreCase(expectedLabel)) {
                    badCorrect++;
                }
            }
            total++;
        }
        goodTestLevel2.deleteWithMissing(0);
        badTestLevel2.deleteWithMissing(0);
        System.out.println((double) goodCorrect / (double) goodTestLevel2.numInstances());
        System.out.println((double) badCorrect / (double) badTestLevel2.numInstances());
        ///////////////////////////
        int correct = 0;
        total = 0;
        if (!goodTrainLevel2.equalHeaders(goodTestLevel2)) {
            System.out.println("goodTrain set and goodTest set do not have compatible headers for good classifier of second layer of whole classifier.");
        }
        for (int i = 0; i < goodTestLevel2.numInstances(); i++) {
            Instance curInstance = goodTestLevel2.instance(i);
            double classInd = classifierLevel2Good.classifyInstance(curInstance);
            String predictedClassLabel = goodTrainLevel2.attribute(goodTrainLevel2.classIndex()).value((int) classInd);
            String expectedValue = (String) qIdLabel.get(testTemplate.instance(i).attribute(0).value(0));
            if (predictedClassLabel.equalsIgnoreCase(expectedValue)) {
                correct++;
            }
            total++;
        }
        if (!badTrainLevel2.equalHeaders(badTestLevel2)) {
            System.out.println("badTrain set and badTest set do not have compatible headers for bad classifier of second layer of whole classifier.");
        }
        for (int i = 0; i < badTestLevel2.numInstances(); i++) {
            Instance curInstance = badTestLevel2.instance(i);
            double classInd = classifierLevel2Bad.classifyInstance(curInstance);
            String predictedClassLabel = badTrainLevel2.attribute(badTrainLevel2.classIndex()).value((int) classInd);
            String expectedValue = (String) qIdLabel.get(testTemplate.instance(i).attribute(0).value(0));
            if (predictedClassLabel.equalsIgnoreCase(expectedValue)) {
                correct++;
            }
            total++;
        }
        System.out.println("Accuracy of two level system: " + ((double) correct / total));
        ///////////////////////////
//        double[][] nFoldCrossValidationByInstancesGood = nFoldCrossValidationByInstances(goodTestLevel2);
//        double[][] nFoldCrossValidationByInstancesBad = nFoldCrossValidationByInstances(badTestLevel2);
//        MLAlgorithms.printMatrix(nFoldCrossValidationByInstancesGood, "Good Confusion Matrix:\n ");
//        MLAlgorithms.printMatrix(nFoldCrossValidationByInstancesBad, "Bad Confusion Matrix:\n ");
        ///////////////////////////
    }

    private static void printMatrix(double[][] matrix, String prompt) {
        System.out.println(prompt);
        for (double[] matrixRow : matrix) {
            for (double matrixCell : matrixRow) {
                System.out.print(matrixCell + "\t");
            }
            System.out.print("\n");
        }
    }

    public void twoLayerClassifier320(String featuresTableFilePath, String testFeatureTable) throws IOException, Exception {
        File featuresTable = new File(featuresTableFilePath);
        CSVLoader loader1 = new CSVLoader();
        loader1.setFile(featuresTable);
        Instances inst = loader1.getDataSet();
        inst.setClassIndex(inst.numAttributes() - 1);

        Instances badTrainLevel2 = new Instances(inst), tempAttributes = new Instances(inst);
        badTrainLevel2.delete();

        Enumeration enumerateInstances = inst.enumerateInstances();
        while (enumerateInstances.hasMoreElements()) {
            Instance instance = (Instance) enumerateInstances.nextElement();
            if (inst.classAttribute().value((int) instance.classValue()).equalsIgnoreCase("Bad")
                    || inst.classAttribute().value((int) instance.classValue()).equalsIgnoreCase("Dialogue")) {
                badTrainLevel2.add(instance);
            }
        }
        badTrainLevel2.deleteWithMissing(0);
        /////////////build 3 classifiers for two layers
        Attribute classAttribute = inst.classAttribute();
        inst.renameAttributeValue(classAttribute, "Dialogue", "Bad");
//        inst.deleteAttributeAt(0);
        Classifier classifierLevel1 = MLAlgorithms.getJ48Classifier();
        classifierLevel1.buildClassifier(inst);

//        badTrainLevel2.deleteAttributeAt(0);
//        badTrainLevel2.renameAttributeValue(classAttribute.index(), 1, "Bad");
//        badTrainLevel2.renameAttributeValue(classAttribute.index(), 3, "Dialogue");
        Classifier classifierLevel2Bad = MLAlgorithms.getJ48Classifier();
        classifierLevel2Bad.buildClassifier(badTrainLevel2);
        /////////////
        File testTable = new File(testFeatureTable);
        CSVLoader loaderTest = new CSVLoader();
        loaderTest.setFile(testTable);
        Instances testInstances = loaderTest.getDataSet();
        testInstances.setClassIndex(testInstances.numAttributes() - 1);

        Instances badTestLevel2 = new Instances(tempAttributes);
        badTrainLevel2.delete();

        int correct = 0, total = 0;

        for (int i = 0; i < testInstances.numInstances(); i++) {
            Instance curInstance = testInstances.instance(i);
            double classInd = classifierLevel1.classifyInstance(curInstance);
            String predictedClassLabel = inst.attribute(inst.classIndex()).value((int) classInd);
            String expectedLabel = testInstances.attribute(testInstances.classIndex()).value((int) curInstance.classValue());
            if (expectedLabel.equalsIgnoreCase("Dialogue")) {
                expectedLabel = "Bad";
            }
            if (expectedLabel.equalsIgnoreCase("Good") || expectedLabel.equalsIgnoreCase("Potential")) {
                if (predictedClassLabel.equalsIgnoreCase(expectedLabel)) {
                    correct++;
                }
            }
            if (predictedClassLabel.equalsIgnoreCase("Bad")) {
                badTestLevel2.add(testInstances.instance(i));
            }
            total++;
        }
        badTestLevel2.deleteWithMissing(0);
        for (int i = 0; i < badTestLevel2.numInstances(); i++) {
            Instance curInstance = badTestLevel2.instance(i);
            double classInd = classifierLevel2Bad.classifyInstance(curInstance);
            String predicted = badTestLevel2.attribute(badTestLevel2.classIndex()).value((int) classInd);
            if (predicted.equalsIgnoreCase(testInstances.attribute(testInstances.classIndex()).value((int) curInstance.classValue()))) {
                correct++;
            }
            total++;
        }
        System.out.println("Accuracy of two level system: " + ((double) correct / total));
    }

    public void balancerBasedOnSampling(String featuresTableFilePath) {

    }

    public void officialMeasureEvaluation(String resultFilePath, String cGoldFilePath, String outPutValues) {
        try {
            Runtime rt = Runtime.getRuntime();
            Process p = rt.exec("perl SemEval2015-task3-scorer-subtaskA.pl "
                    + cGoldFilePath + " " + resultFilePath + " " + outPutValues);
        } catch (IOException ex) {
            Logger.getLogger(MLAlgorithms.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void twoLayerClassifier230(String layer1TSFilePath, String layer2TSFilePath, String testSetFilePath, String testSetFilePathTemp, String goldLabelsFilePath) {
        ConfusionMatrix confMat = new ConfusionMatrix();
        try {
            ////////////////////
            CSVLoader layer1TSLoader = new CSVLoader();
            layer1TSLoader.setFile(new File(layer1TSFilePath));
            Instances layer1TS = layer1TSLoader.getDataSet();
            layer1TS.setClassIndex(layer1TS.numAttributes() - 1);
            System.out.println("First Layer Train Set has been loaded.");

            Classifier layer1Classifier = MLAlgorithms.getClassifier();
            layer1Classifier.buildClassifier(layer1TS);
            System.out.println("First Layer Classifier has been trained.");
            ////////////////////
            CSVLoader layer2TSLoader = new CSVLoader();
            layer2TSLoader.setFile(new File(layer2TSFilePath));
            Instances layer2TS = layer2TSLoader.getDataSet();
            layer2TS.setClassIndex(layer2TS.numAttributes() - 1);
            System.out.println("Second Layer Train Set has been loaded.");

            Classifier layer2Classifier = MLAlgorithms.getClassifier();
            layer2Classifier.buildClassifier(layer2TS);
            System.out.println("Second Layer Classifier has been trained.");
            ////////////////////
            HashMap<Instance, String> testSamples = new HashMap<>();
//            CSVLoader testSetLoader = new CSVLoader();
            ArffLoader testSetLoader = new ArffLoader();
            testSetLoader.setFile(new File(testSetFilePath));
            Instances testSet = testSetLoader.getDataSet();
            testSet.setClassIndex(testSet.numAttributes() - 1);

            ArffLoader testSetLoaderTemp = new ArffLoader();
            testSetLoaderTemp.setFile(new File(testSetFilePathTemp));
            Instances testSetTemp = testSetLoaderTemp.getDataSet();
            testSetTemp.setClassIndex(testSetTemp.numAttributes() - 1);
            testSetTemp.deleteAttributeAt(0);
            for (int i = 0; i < testSet.numInstances(); i++) {
                String instanceId = testSet.instance(i).stringValue(0);
                testSamples.put(testSetTemp.instance(i), instanceId);
            }
            System.out.println("Test Set has been loaded.");
            HashMap<String, String> goldLabels = new HashMap<>();
            try (BufferedReader bfr = new BufferedReader(new FileReader(goldLabelsFilePath))) {
                String str;
                int index;
                while ((str = bfr.readLine()) != null) {
                    goldLabels.put(str.substring(0, index = str.indexOf("\t")), str.substring(index + 1));
                }
            }
            System.out.println("Gold Labels has been loaded.");
            double accuracySorat = 0;
            int pote = 0, dia = 0, bad = 0, good = 0;
            System.out.println("Test Process is going to be started.");
            for (int i = 0; i < testSet.numInstances(); i++) {
                Instance testSample = testSetTemp.instance(i);
                double predictedLabelAtFirstLayer = layer1Classifier.classifyInstance(testSample);
                String firstPredictedClass = layer1TS.attribute(layer1TS.classIndex()).value((int) predictedLabelAtFirstLayer);
                String expectedClass = goldLabels.get(testSamples.get(testSample));
                if (firstPredictedClass.equalsIgnoreCase("Potential")) {
                    if (expectedClass.equalsIgnoreCase(firstPredictedClass)) {
                        accuracySorat++;
                        pote++;
                    }
                    confMat.add(firstPredictedClass, expectedClass);
                } else {// if (firstPredictedClass.equalsIgnoreCase("Other")) {
                    double predictedLabelAtSecondLayer = layer2Classifier.classifyInstance(testSample);
                    String secondPredictedClass = layer2TS.attribute(layer2TS.classIndex()).value((int) predictedLabelAtSecondLayer);
                    if (expectedClass.equalsIgnoreCase(secondPredictedClass)) {
                        accuracySorat++;
                        if (secondPredictedClass.equalsIgnoreCase("Good")) {
                            good++;
                        } else if (secondPredictedClass.equalsIgnoreCase("Dialogue")) {
                            dia++;
                        } else if (secondPredictedClass.equalsIgnoreCase("Bad")) {
                            bad++;
                        }
                    }
                    confMat.add(secondPredictedClass, expectedClass);
                }
            }
            System.out.println("accuracy is: " + accuracySorat / testSet.numInstances());
            System.out.println("Bad: " + bad + ", Dialogue: " + dia + ", Good: " + good + ", Potential: " + pote);
            System.out.println("ConfusionMatrix: \n" + confMat.printTable());
        } catch (IOException ex) {
            Logger.getLogger(MLAlgorithms.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(MLAlgorithms.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void oneLayerClassifier(String trainSetFilePath, String testSetFilePath, String goldLabelsFilePath) {
        try {
            ////////////////////
            CSVLoader trainSetLoader = new CSVLoader();
            trainSetLoader.setFile(new File(trainSetFilePath));
            Instances trainSet = trainSetLoader.getDataSet();
            trainSet.setClassIndex(trainSet.numAttributes() - 1);
            System.out.println("Train Set has been loaded.");

//            Classifier trainSetClassifier = MLAlgorithms.getClassifier();
            Classifier trainSetClassifier = MLAlgorithms.getJ48Classifier();
            trainSetClassifier.buildClassifier(trainSet);
            System.out.println("Classifier has been trained.");
            ////////////////////
            HashMap<Instance, String> testSamples = new HashMap<>();
            CSVLoader testSetLoader = new CSVLoader();
            testSetLoader.setFile(new File(testSetFilePath));
            Instances testSet = testSetLoader.getDataSet();
            Instances testSetTemp = new Instances(testSet);
            testSet.setClassIndex(testSet.numAttributes() - 1);
            testSetTemp.setClassIndex(testSetTemp.numAttributes() - 1);
            testSetTemp.deleteAttributeAt(0);
            for (int i = 0; i < testSet.numInstances(); i++) {
                String instanceId = testSet.instance(i).stringValue(0);
                testSamples.put(testSetTemp.instance(i), instanceId);
            }
            System.out.println("Test Set has been loaded.");
            HashMap<String, String> goldLabels = new HashMap<>();
            try (BufferedReader bfr = new BufferedReader(new FileReader(goldLabelsFilePath))) {
                String str;
                int index;
                while ((str = bfr.readLine()) != null) {
                    goldLabels.put(str.substring(0, index = str.indexOf("\t")), str.substring(index + 1));
                }
            }
            System.out.println("Gold Labels has been loaded.");
            double accuracySorat = 0;
            System.out.println("Test Process is going to be started.");
            for (int i = 0; i < testSet.numInstances(); i++) {
                Instance testSample = testSetTemp.instance(i);
                double predictedLabelIndex = trainSetClassifier.classifyInstance(testSample);
                String predictedClass = trainSet.attribute(trainSet.classIndex()).value((int) predictedLabelIndex);
                String expectedClass = goldLabels.get(testSamples.get(testSample));
                if (predictedClass.equalsIgnoreCase(expectedClass)) {
                    accuracySorat++;
                }
            }
            System.out.println("accuracy is: " + accuracySorat / testSet.numInstances());
        } catch (IOException ex) {
            Logger.getLogger(MLAlgorithms.class.getName()).log(Level.SEVERE, null, ex);
        } catch (Exception ex) {
            Logger.getLogger(MLAlgorithms.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) throws Exception {

        String firstLayerFeatureTablePath = "result\\tokenize\\freqDepartionListWiseFeatureTableNewBalanced.csv";//freqDepartionListWiseFeatureTableBalanced.csv";
        String secondLayerfeatureTablePath = "result\\tokenize\\freqDepartionListWiseFeatureTableBalancedLayer2.csv";
        String testFeatureTableFilePath = "result\\test\\freqDepartionListWise-test-FeatureTable - Copy.arff";
        String testFeatureTableFilePathTemp = "result\\test\\freqDepartionListWise-test-FeatureTable - CopyTemp.arff";
        String cgoldFilePath = "result\\test\\CQA-QL-test-gold.txt";
        MLAlgorithms ml = new MLAlgorithms();
//        double[][] confutionMatrix = ml.nFoldCrossValidationByFeatureTable(featureTablePath);
//        for (double[] confuse : confutionMatrix) {
//            for (double d : confuse) {
//                System.out.print(d + "\t");
//            }
//            System.out.print("\n");
//        }
//        ml.balancerBasedOnMergeClassSamples(featureTablePath, secondLayerfeatureTablePath);
//        ml.twoLayerClassifier222(featureTablePath, testFeatureTableFilePath, cgoldFilePath);
//        ml.twoLayerClassifier320(featureTablePath, testFeatureTableFilePath);
//        ml.baggingJ48(cgoldFilePath, cgoldFilePath, cgoldFilePath, cgoldFilePath);
        ml.twoLayerClassifier230(firstLayerFeatureTablePath, secondLayerfeatureTablePath, testFeatureTableFilePath, testFeatureTableFilePathTemp, cgoldFilePath);
//        ml.oneLayerClassifier(secondLayerfeatureTablePath, testFeatureTableFilePath, cgoldFilePath);
    }
}
