import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

class Iterators {
    public static void main(String[] args) throws IOException {
        File file_path = new File(args[0]);
        FileInputStream inputStream = new FileInputStream(file_path);
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        Get_lines get_lines = new Get_lines(bufferedReader);
        All_words all_words = new All_words(get_lines);
        Non_stop_words non_stop_words = new Non_stop_words(all_words);
        Count_and_sort count_and_sort = new Count_and_sort(non_stop_words);
        int time = 1;
        while(count_and_sort.hasNext()){
            System.out.println("------ iteration: " + time + " ------" );
            ArrayList<Map.Entry<String,Integer>> frequencies = count_and_sort.next();
            int i = 0;
            for (Map.Entry<String, Integer> entry : frequencies) {
                if(i == 25) break;
                System.out.println(entry.getKey() + " - " + entry.getValue());
                i++;
            }
            time++;
        }
    }
    static class Get_lines implements Iterator<String>{
        private BufferedReader bufferedReader;
        public Get_lines(BufferedReader bufferedReader){
            this.bufferedReader = bufferedReader;
        }
        @Override
        public boolean hasNext() {
            boolean result = true;
            try {
                result = bufferedReader.ready();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }

        @Override
        public String next() {
            String result = "";
            try {
                result = bufferedReader.readLine().toLowerCase();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }
    }
    static class All_words implements Iterator<String>{
        private Iterator<String> previous;
        String[] strings = new String[]{};
        private int position = 0;
        public All_words(Iterator<String> previous){
            this.previous = previous;
            strings = previous.next().split("[^a-zA-Z0-9]+");
        }
        @Override
        public boolean hasNext() {
            if (previous.hasNext()||(position<strings.length-1&& strings.length>0)){
                return true;
            }
            else{
                return false;
            }
        }

        @Override
        public String next() {
            if (position<strings.length-1){
                position++;
                if(strings[position-1].length()<2) return this.next();
                return strings[position-1];
            }
            else {
                String[] newResult = strings;
                strings = previous.next().split("[^a-zA-Z0-9]+");
                position = 0;
                if(newResult.length == 0 ) return this.next();
                if(newResult[newResult.length-1].length()<2) return this.next();
                return newResult[newResult.length-1];
            }
        }
    }
    static class Non_stop_words implements Iterator<String>{
        private Iterator<String> previous;
        private ArrayList<String> stop_words = new ArrayList<>();

        public Non_stop_words(Iterator<String> previous) throws IOException {
            this.previous = previous;
            asList(Files.lines(Paths.get("stop_words.txt")).map(String::valueOf).collect(Collectors.joining("")).split(",")).forEach(s->{
                stop_words.add(s);
            });
        }
        @Override
        public boolean hasNext() {
            return previous.hasNext();
        }

        @Override
        public String next() {
            String word;
            do {
                word = previous.next();
            } while (stop_words.contains(word) && previous.hasNext());
            return word;
        }
    }
    static class Count_and_sort implements Iterator<ArrayList<Map.Entry<String,Integer>>>{
        private Iterator<String> previous;
        private int count =1;
        private HashMap<String,Integer> freqs = new HashMap<>();
        public Count_and_sort(Iterator<String> previous){
            this.previous = previous;
        }
        @Override
        public boolean hasNext() {
            if (previous.hasNext()) return true;
            else return false;
        }

        @Override
        public ArrayList<Map.Entry<String, Integer>> next() {
            while (count % 5000 != 0 && previous.hasNext()){
                String word = previous.next();
                if (freqs.containsKey(word)){
                    freqs.put(word, freqs.get(word) + 1);
                }
                else{
                    freqs.put(word,1);
                }
                count++;
            }
            ArrayList<Map.Entry<String, Integer>> list = new ArrayList<>(freqs.entrySet());
            list.sort((o1, o2) -> Integer.compare(o2.getValue(),o1.getValue())) ;
            count = 1;
            return list;
        }
    }
}
