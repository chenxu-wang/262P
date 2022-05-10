import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;
//Enter "cd week2" 
//then "java FOUR ../pride-and-prejudice.txt"
public class FOUR {
    private static final char[] lowerLetter = new char[]{'a','b','c','d','e','f','g','h','i','j','k','l','m','n','o','p','q','r','s','t','u','v','w','x','y','z'};
    private static final char[] upperLetter = new char[]{'A','B','C','D','E','F','G','H','I','J','K','L','M','N','O','P','Q','R','S','T','U','V','W','X','Y','Z'};
    private static final char[] number = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};
    private static ArrayList<Object[]> wordsCount = new ArrayList<>();
    public static void main(String[] args) throws Exception {
        ArrayList<String> stop_words = new ArrayList<String>();
        File stop_words_file = new File("../stop_words.txt");
        Scanner scanner = new Scanner(stop_words_file);
        String list = "";
        while (scanner.hasNext()) {
            list = scanner.next();
        }
        String[] split_stop_words = list.split(",");
        // add each word to ArrayList
        for (int i = 0; i < split_stop_words.length; i++) {
            stop_words.add(split_stop_words[i]);
        }
        scanner.close();
        System.out.println("----TOP 25 WORDS----");
        String filepath = args[0];
        FileInputStream inputStream = new FileInputStream(filepath);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String str = null;
        while((str = bufferedReader.readLine())!=null){
            boolean found = false;
            int start = -1;
            //Avoid skipping last word;
            str = str + " ";
            for(int i = 0; i < str.length(); i++){
                char c = str.charAt(i);
                boolean isAlnum = false;
                for(int k = 0; k < lowerLetter.length; k++) {
                    if (c == lowerLetter[k]||c == upperLetter[k])
                        isAlnum = true;
                }

                for(int k = 0; k < number.length; k++) {
                    if (c == number[k])
                        isAlnum = true;
                }
                if(!found){
                    if(isAlnum) {
                        found = true;
                        start = i;
                    }
                }
                else{
                    if(!isAlnum){
                        String res = "";
                        for(int k = start; k <= i-1; k++){
                            char cc = str.charAt(k);
                            for(int j = 0; j < upperLetter.length; j++){
                                if(cc == upperLetter[j]){
                                    cc = lowerLetter[j];
                                    break;
                                }
                            }
                            res = res + cc;
                        }
                        String word = res;
                        found = false;
                        boolean isStopWord = false;
                        for(int k = 0; k < stop_words.size();k++){
                            if(word.equals(stop_words.get(k)))
                                isStopWord = true;
                        }
                        if(!isStopWord && word.length() >= 2){
                            boolean nextFound = false;
                            for(int j = 0; j < wordsCount.size(); j++){
                                if(wordsCount.get(j)[0].equals(word)){
                                    wordsCount.get(j)[1] = (int)wordsCount.get(j)[1] + 1;
                                    nextFound = true;
                                    break;
                                }
                            }
                            if(!nextFound)
                                wordsCount.add(new Object[]{word, 1});

                        }
                    }
                }

            }
        }
        for (int i = 0; i < wordsCount.size() - 1; i++){
            for (int j = 0; j < wordsCount.size() - i - 1; j++){
                if ((int)wordsCount.get(j)[1] < (int)wordsCount.get(j+1)[1]){
                    Object[] temp = wordsCount.get(j);
                    wordsCount.set(j, wordsCount.get(j+1));
                    wordsCount.set(j+1,temp);
                }
            }
        }
        for(int i = 0; i < 25; i++){
            String string = (String)wordsCount.get(i)[0];
            int counts = (int)wordsCount.get(i)[1];
            System.out.println(string + " - " + counts);
        }

    }
}
