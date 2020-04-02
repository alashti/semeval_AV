package tools.processors;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map.Entry;


import tools.util.collection.HashSert;
import tools.util.file.Write.HashMapWriteType;
import tools.util.collection.HashSertInteger;
import tools.util.sort.Collection.SortType;

public class Distribution {

    public static void main(String[] args) throws IOException {
        // String
        // fileAddress="/home/saeed/Data/bijankhan/sentencesWithCompletePOS.utf8";
        // tools.util.file.Write.mapToTextFileSortedByKey(Distribution.getFileCharDistribution(fileAddress),
        // fileAddress+".charDistrib", SortType.DECREMENTAL,
        // HashMapWriteType.KEYVALUE);
        // String fileAddress="/home/saeed/Downloads/KAW-ICASSP07.pdf.txt";
        // printFileCharDistribution(fileAddress);
        // String baseAddress="/home/saeed/Desktop/text pdf/univercity pdf/";
        // getPathCharDistribution(baseAddress, 0);
        // tools.util.file.Write.mapToTextFileSortedByValue(getPathCharDistribution(baseAddress,
        // 0), baseAddress+"allTokens.txt", SortType.DECREMENTAL,
        // HashMapWriteType.KEYVALUE);
        HashMap<String, Long> fileTokenDistribution = getFileTokenDistribution("/home/rahmani/Data/wiki-articles", "\\n|\\s| |\\u200c|\\u200e");
        tools.util.file.Write.mapToTextFileSortedByValue(fileTokenDistribution, "/home/rahmani/Data/wiki-articles.tokens", SortType.DECREMENTAL, HashMapWriteType.KEYVALUE);
        System.out.println("operation complete.");
//		test1("/media/saeed/Temp/pdf data set/sentenceIII/currect/");
    }


    static public HashSet<Character> getFileCharSet(String fileAddress)
            throws IOException {
        HashSet<Character> charSet = new HashSet<Character>(100000);
        int lineCounter = 0;
        BufferedReader fileBufferReader = tools.util.file.Reader
                .getFileBufferReader(fileAddress);
        String newLine;
        while ((newLine = fileBufferReader.readLine()) != null) {
            lineCounter++;
            for (int i = 0; i < newLine.length(); i++) {
                charSet.add(newLine.charAt(i));
            }
            if (lineCounter % 100000 == 0)
                System.out.println(lineCounter + " line handled.");
        }
        fileBufferReader.close();
        System.out.println("getCharSet operation complete with "
                + charSet.size() + " for " + lineCounter + " handled line.");
        return charSet;
    }

    static public HashSet<Character> getPathCharSet(String filesPath,
                                                    int directorySearchDeepLevel) throws IOException {
        HashSet<Character> charSet = new HashSet<Character>(100000);
        int lineCounter = 0;
        int fileCounter = 0;
        for (File file : tools.util.directory.Search.getFilesForPath(filesPath,
                false, new HashSet<String>(), directorySearchDeepLevel)) {
            fileCounter++;
            BufferedReader fileBufferReader = tools.util.file.Reader
                    .getFileBufferReader(file);
            String newLine;
            while ((newLine = fileBufferReader.readLine()) != null) {
                lineCounter++;
                for (int i = 0; i < newLine.length(); i++) {
                    charSet.add(newLine.charAt(i));
                }
                if (lineCounter % 100000 == 0)
                    System.out.println(lineCounter + " lines for "
                            + fileCounter + " file.");
            }
            fileBufferReader.close();
        }
        System.out.println("getCharSet operation complete with "
                + charSet.size() + " for " + fileCounter
                + " handled file of with " + lineCounter + " lines.");
        return charSet;
    }

    static public HashMap<Character, Long> getFileCharDistribution(
            String fileAddress) throws IOException {
        HashSert<Character> charDistribution = new HashSert<Character>();
        int lineCounter = 0;
        BufferedReader fileBufferReader = tools.util.file.Reader
                .getFileBufferReader(fileAddress);
        String newLine;
        while ((newLine = fileBufferReader.readLine()) != null) {
            lineCounter++;
            for (int i = 0; i < newLine.length(); i++) {
                charDistribution.put(newLine.charAt(i));
            }
            if (lineCounter % 100000 == 0)
                System.out.println(lineCounter + " line handled.");
        }
        fileBufferReader.close();
        System.out.println("getCharSet operation complete with "
                + charDistribution.size() + " for " + lineCounter
                + " handled line.");
        return charDistribution.getHashMap();
    }

    static public void printFileCharDistribution(String fileAddress)
            throws IOException {
        System.out.println("The " + tools.util.File.getName(fileAddress)
                + " character distribution:");
        int i = 0;
        for (Entry<Character, Long> entry : tools.util.sort.Collection
                .mapSortedByValuesDecremental(getFileCharDistribution(fileAddress))) {
            System.out.println(++i + ") " + entry.getKey() + "\t\t"
                    + entry.getValue());
        }
    }

    static public HashMap<Character, Long> getPathCharDistribution(
            String filesPath, int directorySearchDeepLevel) throws IOException {
        HashSert<Character> charDistribution = new HashSert<Character>();
        int lineCounter = 0;
        int fileCounter = 0;
        // String misToken =
        // "/home/saeed/Desktop/text pdf/univercity pdf/mis.allTokens.txt";
        // Set<Character> misTokens =
        // tools.util.file.Reader.getKeyValueCharacterIntegerFromTextFile(misToken,
        // 0, false, "\t").keySet();
        for (File file : tools.util.directory.Search.getFilesForPath(filesPath,
                false, new HashSet<String>(), directorySearchDeepLevel)) {
            fileCounter++;
            BufferedReader fileBufferReader = tools.util.file.Reader
                    .getFileBufferReader(file);
            String newLine;
            while ((newLine = fileBufferReader.readLine()) != null) {
                lineCounter++;
                for (int i = 0; i < newLine.length(); i++) {
                    // if(misTokens.size()==0)
                    // return null;
                    // if(misTokens.contains(newLine.charAt(i))){
                    // System.out.println(file.getAbsolutePath()+"\n"+newLine);
                    // misTokens.remove(newLine.charAt(i));
                    // }
                    charDistribution.add(newLine.charAt(i));
                }
                if (lineCounter % 100000 == 0)
                    System.out.println(lineCounter + " lines for "
                            + fileCounter + " file.");
            }
            fileBufferReader.close();
        }
        System.out.println("getCharSet operation complete with "
                + charDistribution.size() + " for " + fileCounter
                + " handled file of with " + lineCounter + " lines.");
        return charDistribution.getHashMap();
    }

    static public HashSet<String> getFileTokenSet(String fileAddress,
                                                  String splitRegularExpression) throws IOException {
        HashSet<String> tokenSet = new HashSet<String>(100000);
        int lineCounter = 0;
        BufferedReader fileBufferReader = tools.util.file.Reader
                .getFileBufferReader(fileAddress);
        String newLine;
        while ((newLine = fileBufferReader.readLine()) != null) {
            lineCounter++;
            for (String token : newLine.split(splitRegularExpression)) {
                tokenSet.add(token.trim());
            }
            if (lineCounter % 100000 == 0)
                System.out.println(lineCounter + " line handled.");
        }
        fileBufferReader.close();
        System.out.println("getCharSet operation complete with "
                + tokenSet.size() + " for " + lineCounter + " handled line.");
        return tokenSet;
    }

    static public HashSet<String> getPathTokenSet(String filesPath,
                                                  int directorySearchDeepLevel, String splitRegularExpression)
            throws IOException {
        HashSet<String> tokenSet = new HashSet<String>(100000);
        int lineCounter = 0;
        int fileCounter = 0;
        for (File file : tools.util.directory.Search.getFilesForPath(filesPath,
                false, new HashSet<String>(), directorySearchDeepLevel)) {
            fileCounter++;
            BufferedReader fileBufferReader = tools.util.file.Reader
                    .getFileBufferReader(file);
            String newLine;
            while ((newLine = fileBufferReader.readLine()) != null) {
                lineCounter++;
                for (String token : newLine.split(splitRegularExpression)) {
                    tokenSet.add(token.trim());
                }
                if (lineCounter % 100000 == 0)
                    System.out.println(lineCounter + " lines for "
                            + fileCounter + " file.");
            }
            fileBufferReader.close();
        }
        System.out.println("getCharSet operation complete with "
                + tokenSet.size() + " for " + fileCounter
                + " handled file of with " + lineCounter + " lines.");
        return tokenSet;
    }

    static public HashMap<String, Long> getFileTokenDistribution(
            String fileAddress, String splitRegularExpression)
            throws IOException {
        HashSert<String> tokenDistribution = new HashSert<String>();
        int lineCounter = 0;
        BufferedReader fileBufferReader = tools.util.file.Reader
                .getFileBufferReader(fileAddress);
        String newLine;
        while ((newLine = fileBufferReader.readLine()) != null) {
            lineCounter++;
            for (String token : newLine.split(splitRegularExpression)) {
                tokenDistribution.add(token.trim());
            }
            if (lineCounter % 100000 == 0)
                System.out.println(lineCounter + " line handled.");
        }
        fileBufferReader.close();
        System.out.println("getTermDistribution operation complete with "
                + tokenDistribution.size() + " for " + lineCounter
                + " handled line.");
        return tokenDistribution.getHashMap();
    }


    static public HashMap<String, Long> getFileTokenTokenDistribution(String fileAddress, String splitRegularExpression)
            throws IOException {
        tools.util.Time.setStartTimeForNow();
        HashSert<String> tokenDistribution = new HashSert<String>();
        int lineCounter = 0;
        BufferedReader fileBufferReader = tools.util.file.Reader
                .getFileBufferReader(fileAddress);
        String newLine;
        while ((newLine = fileBufferReader.readLine()) != null) {
            lineCounter++;
            String[] split = newLine.split(splitRegularExpression);
            for (int i = 0; i < split.length - 1; i++) {
                tokenDistribution.add(split[i] + " " + split[i + 1]);
            }
            if (lineCounter % 100000 == 0)
                System.out.println(lineCounter + " line handled.");
        }
        fileBufferReader.close();
        System.out.println("getTokenTonen operation complete with "
                + tokenDistribution.size() + " for " + lineCounter
                + " handled line in " + tools.util.Time.getTimeLengthForNow()
                + " ms.");
        return tokenDistribution.getHashMap();
    }

    static public HashMap<String, Long> getFileTrigramDistribution(String fileAddress, String splitRegularExpression)
            throws IOException {
        tools.util.Time.setStartTimeForNow();
        HashSert<String> tokenDistribution = new HashSert<String>();
        int lineCounter = 0;
        BufferedReader fileBufferReader = tools.util.file.Reader
                .getFileBufferReader(fileAddress);
        String newLine;
        while ((newLine = fileBufferReader.readLine()) != null) {
            lineCounter++;
            String[] split = newLine.split(splitRegularExpression);
            for (int i = 0; i < split.length - 2; i++) {
                tokenDistribution.add(split[i] + " " + split[i + 1] + " " + split[i + 2]);
            }
            if (lineCounter % 100000 == 0)
                System.out.println(lineCounter + " line handled.");
        }
        fileBufferReader.close();
        System.out.println("getTokenTonen operation complete with "
                + tokenDistribution.size() + " for " + lineCounter
                + " handled line in " + tools.util.Time.getTimeLengthForNow()
                + " ms.");
        return tokenDistribution.getHashMap();
    }

    static public HashMap<String, Integer> getPathTokenDistribution(
            String filesPath, int directorySearchDeepLevel,
            String splitRegularExpression) throws IOException {
        HashSertInteger<String> tokenDistribution = new HashSertInteger<String>();
        int lineCounter = 0;
        int fileCounter = 0;
        for (File file : tools.util.directory.Search.getFilesForPath(filesPath,
                false, new HashSet<String>(), directorySearchDeepLevel)) {
            fileCounter++;
            BufferedReader fileBufferReader = tools.util.file.Reader
                    .getFileBufferReader(file);
            String newLine;
            while ((newLine = fileBufferReader.readLine()) != null) {
                lineCounter++;
                for (String token : newLine.split(splitRegularExpression)) {
                    tokenDistribution.add(token.trim());
                }
                if (lineCounter % 100000 == 0)
                    System.out.println(lineCounter + " lines for "
                            + fileCounter + " file.");
            }
            fileBufferReader.close();
        }
        System.out.println("getCharSet operation complete with "
                + tokenDistribution.size() + " for " + fileCounter
                + " handled file of with " + lineCounter + " lines.");
        return tokenDistribution.getHashMap();
    }

}
