/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tools.processors;

import ac.biu.nlp.normalization.BiuNormalizer;
import com.google.common.base.CharMatcher;
import com.swabunga.spell.engine.SpellDictionaryHashMap;
import com.swabunga.spell.engine.Word;
import com.swabunga.spell.event.SpellChecker;
import edu.stanford.nlp.process.CoreLabelTokenFactory;
import edu.stanford.nlp.process.PTBTokenizer;
import edu.stanford.nlp.util.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.io.StringReader;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author epcpu
 */
public class Preprocess {

    private static PrintStream jout = System.out;
    private static BiuNormalizer biuNorm;

    static {
        try {
            String rules_dir = "resources/biu-normalizer/string_rules.txt";
            biuNorm = new BiuNormalizer(new File(rules_dir));
        } catch (IOException ex) {
            Logger.getLogger(Preprocess.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static List tokenizerStanfordPTBTokenize(File textFile) throws FileNotFoundException, IOException, Exception {

        PTBTokenizer ptbt = new PTBTokenizer(tools.util.file.Reader.getFileBufferReader(textFile), new CoreLabelTokenFactory(), "");
        return ptbt.tokenize();
    }

    public static String tokenizerStanfordPTBTokenize(String text) throws FileNotFoundException, IOException, Exception {

        PTBTokenizer ptbt = new PTBTokenizer(new StringReader(text), new CoreLabelTokenFactory(), "");
        return StringUtils.join(ptbt.tokenize());
    }

    public static String simpleNormalize(String inputString) throws Exception {
        inputString = inputString.trim();
        inputString = CharMatcher.ASCII.retainFrom(inputString);
        inputString = inputString.toLowerCase();
        inputString = inputString.replaceAll(" \\s+", " ");
        inputString = biuNorm.normalize(inputString);
//        String[] temp = inputString.split(" ");
//        for (String str : temp) {
//            if (isAbnormalWord(str)) {
//                String str_temp = jazzySpellCheckerTest(Semeval_AV.getJazzyDictionaryFileAddress(), str, 2);
//                if (str_temp != null) {
//                    str = str_temp;
//                }
//            }
//        }
        return inputString;
    }

    private static boolean isAbnormalWord(String str) {
        char[] dst = new char[str.length()];
        str.getChars(0, str.length(), dst, 0);
        for (char ch : dst) {
            if (str.indexOf(ch + "" + ch + "" + ch) < 0) {
                return true;
            }
        }
        return false;  //To change body of created methods use File | Settings | File Templates.
    }

    public static String jazzySpellCheckerTest(String jazzyDictionaryFileAddress, String word, int threshold) throws IOException {
        SpellDictionaryHashMap dictionary = null;
        SpellChecker spellChecker = null;

        dictionary = new SpellDictionaryHashMap(new File(jazzyDictionaryFileAddress));

        spellChecker = new SpellChecker(dictionary);
        List list = spellChecker.getSuggestions(word, threshold);
//        for (Iterator iter = list.iterator(); iter.hasNext(); ) {
//            Word term = (Word) iter.next();
//            System.out.println("Word: " + term.getWord() + " ,Cost: " + term.getCost());
//        }
        if (list.size() > 0) {
            Word term = (Word) list.get(0);
            return term.getWord();
        } else {
            return null;
        }
    }

    public static String remove_junks(String question) {

        question = question.trim();
        question = CharMatcher.ASCII.retainFrom(question);
        question = question.replaceAll(" \\s+", " ");
        question = question.replaceAll("[.]{2,}", ".");
        question = question.replaceAll("[!]{2,}", "!");
        question = question.replaceAll("[?]{2,}", "?");
        return question;
    }

    public static String remove_rept(String question) {

        String Q = "";
        String[] word_temp = question.split(" ");
        for (String word : word_temp) {
            Pattern junk1 = Pattern.compile(word.charAt(0) + "[" + word.charAt(0) + "]+");
            Matcher match = junk1.matcher(word);
            if (match.find()) {
                jout.println(word + ": has beed reduced.");
                word = question.charAt(0) + "";
            }
            Q = Q + " " + word;
        }
        return Q;
    }

    public static String Tokenize_eng(String[] L_Split) {
        char quote = '\'', dot = '.', DQ = '\"', And = ',', Semi_colon = ';', open_paranthesis = '(', close_paranthesis = ')', colon = ':', slash = '/', bslash = '\\';
        char dash = '-';
        int L_len = L_Split.length;
        for (int i = 0; i < L_len; i++) {
            String str = L_Split[i];
            int S_len = str.length();
            int index;
            if (str.indexOf(quote) == 0) {
                str = quote + " " + str.substring(1);
                S_len = str.length();
            }
            if ((S_len - 1) > -1 && str.lastIndexOf(quote) == S_len - 1) {
                try {
                    str = str.substring(0, S_len - 1) + " " + quote;
                } catch (Exception ex) {
                    System.out.println(str);
                }
                S_len = str.length();
            }
            if (str.indexOf(DQ) == 0) {
                str = DQ + " " + str.substring(1);
                S_len = str.length();
            }
            if ((S_len - 1) > -1 && str.lastIndexOf(DQ) == S_len - 1) {
                try {
                    str = str.substring(0, S_len - 1) + " " + DQ;
                } catch (Exception ex) {
                    System.out.println(str);
                }
                S_len = str.length();
            }
            if ((index = str.lastIndexOf(DQ)) < S_len - 1 && index > 0 && ((index - 1) > -1 && str.charAt(index - 1) != ' ')) {
                str = str.substring(0, index) + " " + DQ + str.substring(index + 1, S_len);
                S_len = str.length();
            }
            if ((S_len - 1) > -1 && str.lastIndexOf(dot) == S_len - 1 && i == L_len - 1) {
                str = str.substring(0, S_len - 1) + " " + dot;
                S_len = str.length();
            }
            if ((S_len - 1) > -1 && str.indexOf(And) == S_len - 1) {// && str.charAt(S_len - 2) != ' '){
                str = str.substring(0, S_len - 1) + " " + And;
                S_len = str.length();
            }
            if ((S_len - 1) > -1 && str.indexOf(Semi_colon) == S_len - 1) {// && str.charAt(S_len - 2) != ' '){//agar space ghabl az har kodam az in alaem vojod dasht dige nabayad
                //space ezafe konim
                str = str.substring(0, S_len - 1) + " " + Semi_colon;
                S_len = str.length();
            }
            if (str.indexOf(open_paranthesis) == 0) {
                str = open_paranthesis + " " + str.substring(1, S_len);
                S_len = str.length();
            }
            if ((S_len - 1) > -1 && str.indexOf(close_paranthesis) == S_len - 1) {
                str = str.substring(0, S_len - 1) + " " + close_paranthesis;
                S_len = str.length();
            }
            if ((S_len - 1) > -1 && str.indexOf(colon) == S_len - 1) {
                str = str.substring(0, S_len - 1) + " " + colon;
                S_len = str.length();
            }
            if (((index = str.indexOf(dot)) < S_len - 1)
                    && ((index + 1) < S_len - 1 && str.charAt(index + 1) == ' ') && ((index - 1) > -1 && str.charAt(index - 1) != ' ')) {
                str = str.substring(0, index) + " " + dot + str.substring(index + 1, S_len);
                S_len = str.length();
            }
            if ((index = str.indexOf(dash)) < S_len - 1 && (index - 1) > -1 && (index + 1) < S_len) {
//                str = str.replaceAll("[-]", " - ");
                str = str.replaceAll("[-]", " ");
                S_len = str.length();
            }
            if (((index = str.indexOf(slash)) < S_len - 1) && index != -1) {
//                str = str.replaceAll("[/]", " / ");
                str = str.replaceAll("[/]", "");
                S_len = str.length();
            }
            if ((index = str.indexOf(bslash)) < S_len - 1 && index != -1) {
//                str = str.replaceAll("[\\]", " \\ ");
                str = str.replaceAll("[\\]]", "");
                S_len = str.length();
            }
            L_Split[i] = str;//kalameye pardazesh shode ra jaygozine kalameye ghabli mikonim
        }
        return edu.stanford.nlp.util.StringUtils.join(L_Split);
    }

    public static void testTokenizer() throws Exception {
        System.out.println(tokenizerStanfordPTBTokenize("i'm working"));
    }

//    public static void main (String[] args) {
//        System.out.println(Tokenize_eng("are you ok?".split(" ")));
//    }
}
