import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class Nine {
    public static void main(String[] args) throws IOException {
        readFile(args[0],new filterAndRemove());
    }
    private interface Function{
        void call(Object arg, Function func) throws IOException;
    }
    private static void readFile(String filepath, Function func) throws IOException {
        FileInputStream inputStream = new FileInputStream(filepath);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        func.call(bufferedReader, new sort());
    }
    private static class filterAndRemove implements Function{

        @Override
        public void call(Object arg, Function func) throws IOException {
            BufferedReader bufferedReader = (BufferedReader) arg;
            HashMap<String, Integer> frequencies = new HashMap<String, Integer>();
            // load stop words
            ArrayList<String> stop_words = new ArrayList<>();
            asList(Files.lines(Paths.get("../stop_words.txt")).map(String::valueOf).collect(Collectors.joining("")).split(",")).forEach(s->{
                stop_words.add(s);
            });
            try {
                String str = null;
                while((str = bufferedReader.readLine())!=null){
                    String[] strings = str.split("[^a-zA-Z0-9]+");

                    for(int i=0;i<strings.length;i++){
                        if(!stop_words.contains(strings[i].toLowerCase())){
                            if (frequencies.containsKey(strings[i].toLowerCase())&&strings[i].length()>=2) {
                                frequencies.put(strings[i].toLowerCase(), frequencies.get(strings[i].toLowerCase()) + 1);
                            } else {
                                frequencies.put(strings[i].toLowerCase(), 1);
                            }
                        }
                    }
                }
            } catch (FileNotFoundException e) {
                System.out.println("Error.");
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            func.call(frequencies,new print_text());
        }
    }
    private static class sort implements Function{

        @Override
        public void call(Object arg, Function func) throws IOException {
            HashMap<String, Integer> frequencies = (HashMap<String, Integer>) arg;
            ArrayList<Map.Entry<String, Integer>> list = new ArrayList<>(frequencies.entrySet());
            list.sort((o1, o2) -> Integer.compare(o2.getValue(),o1.getValue())) ;
            func.call(list, null);
        }
    }

    private static class print_text implements Function{

        @Override
        public void call(Object arg, Function func) throws IOException {
            ArrayList<Map.Entry<String, Integer>> result = (ArrayList<Map.Entry<String, Integer>>) arg;
            for(int i = 0; i < 25; i++){
                System.out.println(result.get(i).getKey() + " - " + result.get(i).getValue());
            }

        }
    }

}
