import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class Ten {
    public static void main(String[] args) throws IOException {
        TFTheOne one = new TFTheOne(args[0]);
        one.bind(new readFile()).bind(new filterAndRemove()).bind(new sort()).bind(new top25()).printme();
    }
    private interface One {
        Object call(Object arg) throws IOException;
    }
    private static class TFTheOne{
        private Object value;

        TFTheOne(Object v) {
            value = v;
        }

        public TFTheOne bind(One func) throws IOException {
            value = func.call(value);
            return this;
        }

        public void printme() {
            System.out.println(value);
        }
    }

    private static class readFile implements One {
        @Override
        public Object call(Object arg) throws FileNotFoundException {
            String filepath = (String) arg;
            FileInputStream inputStream = new FileInputStream(filepath);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            return bufferedReader;
        }
    }
    private static class filterAndRemove implements One {
        @Override
        public Object call(Object arg) throws IOException {
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
            return frequencies;
        }
    }

    private static class sort implements One {
        @Override
        public Object call(Object arg) {
            HashMap<String, Integer> frequencies = (HashMap<String, Integer>) arg;
            ArrayList<Map.Entry<String, Integer>> list = new ArrayList<>(frequencies.entrySet());
            list.sort((o1, o2) -> Integer.compare(o2.getValue(),o1.getValue())) ;
            return list;
        }
    }
    private static class top25 implements One {
        @Override
        public Object call(Object arg) {
            ArrayList<Map.Entry<String, Integer>> result = (ArrayList<Map.Entry<String, Integer>>) arg;
            StringBuilder top25_freqs = new StringBuilder();
            // adds 25 most frequent entries
            for(int i = 0; i < 25; i++){
                top25_freqs.append(result.get(i).getKey() + " - " + result.get(i).getValue());
                top25_freqs.append("\n");
            }
            return top25_freqs;
        }
    }

}
