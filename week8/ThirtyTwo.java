import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class ThirtyTwo {
    public static void main(String[] args) {
        List<ArrayList<Object[]>> splits = partition(args[0]).stream().map(new splitWords()).collect(Collectors.toList());
        HashMap<String,ArrayList<Object[]>> splitsPerWord = regroup(splits);
        List<Map.Entry<String,Integer>> list = splitsPerWord.entrySet().stream().map(new countWords()).collect(Collectors.toList());
        list.sort((o1, o2) -> Integer.compare(o2.getValue(), o1.getValue()));
        int i = 0;
        for (Map.Entry<String, Integer> entry : list) {
            if(i == 25) break;
            System.out.println(entry.getKey() + " - " + entry.getValue());
            i++;
        }
    }
    static class countWords implements Function<Map.Entry<String,ArrayList<Object[]>>,Map.Entry<String,Integer>>{

        @Override
        public Map.Entry<String, Integer> apply(Map.Entry<String, ArrayList<Object[]>> mapping) {
            HashMap<String,Integer> result = new HashMap<>();
            Object[] reduceResult = mapping.getValue().stream().reduce(new Object[]{"",0},(a,b) -> new Object[]{"",(int) a[1] + (int) b[1]});
            result.put(mapping.getKey(),(int) reduceResult[1]);
            Map.Entry<String,Integer> entry = result.entrySet().iterator().next();
            return entry;
        }
    }
    public static HashMap<String,ArrayList<Object[]>> regroup(List<ArrayList<Object[]>> split){
        HashMap<String,ArrayList<Object[]>> result = new HashMap<>();
        for (ArrayList<Object[]> pairs: split){
            for (Object[] p: pairs){
                if (result.containsKey(p[0])){
                    result.get(p[0]).add(p);
                }
                else{
                    result.put((String) p[0], new ArrayList<Object[]>(Arrays.<Object[]>asList(p)) );
                }
            }
        }
        return result;
    }
    public static ArrayList<String> partition(String filepath){
        int nlines = 200;
        ArrayList<String> lines = new ArrayList<>();
        StringBuilder currLine = new StringBuilder();
        int i = 0;
        try {
            FileInputStream inputStream = new FileInputStream(filepath);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String str = null;
            while((str = bufferedReader.readLine())!=null) {
                currLine.append(str).append("\n");
                if (++i % nlines == 0){
                    lines.add(currLine.toString());
                    currLine = new StringBuilder();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }
    static class splitWords implements Function<String,ArrayList<Object[]>> {

        @Override
        public ArrayList<Object[]> apply(String s) {
            ArrayList<Object[]> result = new ArrayList<>();
            ArrayList<String> words = null;
            try {
                words = nonStopWords(scan(s));
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (String word: words){
                result.add(new Object[]{word,1});
            }
            return result;
        }
        public ArrayList<String> scan(String str) {
            ArrayList<String> words = new ArrayList<>();
                String[] strings = str.split("[^a-zA-Z0-9]+");
                for (int i = 0; i < strings.length; i++) {
                    if(strings[i].length()>=2)
                        words.add(strings[i].toLowerCase());
                }
            return words;
        }

        public ArrayList<String> nonStopWords(ArrayList<String> words) throws IOException {
            ArrayList<String> stop_words = new ArrayList<>();
            asList(Files.lines(Paths.get("stop_words.txt")).map(String::valueOf).collect(Collectors.joining("")).split(",")).forEach(s->{
                stop_words.add(s);
            });
            ArrayList<String> newWords = new ArrayList<>();
            for (String word: words) {
                if (!stop_words.contains(word)){
                    newWords.add(word);
                }
            }
            return newWords;
        }
    }
}
