import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class Eight {
    private static ArrayList<String> wordsCount = new ArrayList<>();
    private static ArrayList<String> stop_words = new ArrayList<String>();
    public static void main(String[] args) throws IOException {
        asList(Files.lines(Paths.get("../stop_words.txt")).map(String::valueOf).collect(Collectors.joining("")).split(",")).forEach(s->{
            stop_words.add(s);
        });
        FileInputStream inputStream = new FileInputStream(args[0]);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String strLine = null;
        while((strLine = bufferedReader.readLine())!=null){
            ArrayList<String> emptyList = new ArrayList<>();
            StringReader stringReader = new StringReader(strLine);
            ArrayList<String> words = parse(stringReader, emptyList, stop_words);
            wordsCount.addAll(words);
            stringReader.close();
        }
        Map<String, Integer> result = new HashMap<>();
        wordsCount.forEach(s -> {
            result.compute(s, (s1, count) -> count == null ? 1 : count + 1);
        });
        // Sort
        ArrayList<Map.Entry<String, Integer>> list = new ArrayList<>(result.entrySet());
        list.sort((o1, o2) -> Integer.compare(o2.getValue(),o1.getValue())) ;
        //print
        for(int i = 0; i < 25; i++){
            System.out.println(list.get(i).getKey() + " - " + list.get(i).getValue());
        }

    }
    //parse function
    private static ArrayList<String> parse(StringReader reader, ArrayList<String> words, ArrayList<String> stop_words) throws IOException {
        int start = 0;
        if((start=reader.read()) == -1) return words;
        else {
            StringBuilder stringBuilder = new StringBuilder();
            char c = (char) start;
            while (Character.isLetterOrDigit(c)) {
                stringBuilder.append(Character.toLowerCase(c));
                c = (char) reader.read();
            }
            String result = stringBuilder.toString();
            //add word to the list
            if (!stop_words.contains(result) && (result.length() >= 2))
                words.add(result);
            return parse(reader, words, stop_words);
        }
    }


}
