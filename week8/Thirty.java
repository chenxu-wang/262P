import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class Thirty {
    public static BlockingQueue<String> word_space = new ArrayBlockingQueue<>(200000);
    public static BlockingQueue<HashMap<String,Integer>> freq_space = new ArrayBlockingQueue<>(400);
    public static ArrayList<String> stop_words = new ArrayList<>();
    public static void main(String[] args) throws IOException {
        asList(Files.lines(Paths.get("stop_words.txt")).map(String::valueOf).collect(Collectors.joining("")).split(",")).forEach(s->{
            stop_words.add(s);
        });
        get_words(args[0]);
        ArrayList<Process_words> process_words = new ArrayList<>();
        for(int i = 0; i < 5; i++){
            process_words.add(new Process_words());
        }
        for (Process_words worker: process_words){
            worker.start();
        }
        for (Process_words worker: process_words){
            try {
                worker.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        HashMap<String,Integer> freqs_sum = new HashMap<>();
        while (!freq_space.isEmpty()){
            HashMap<String,Integer> freqs = freq_space.poll();
            for (String key: freqs.keySet()){
                int count;
                count = freqs_sum.getOrDefault(key,0) + freqs.get(key);
                freqs_sum.put(key,count);
            }
        }
        ArrayList<Map.Entry<String, Integer>> list = new ArrayList<>(freqs_sum.entrySet());
        list.sort((o1, o2) -> Integer.compare(o2.getValue(), o1.getValue()));
        int i = 0;
        for (Map.Entry<String, Integer> entry : list) {
            if(i == 25) break;
            System.out.println(entry.getKey() + " - " + entry.getValue());
            i++;
        }

    }
    static class Process_words extends Thread{
        public void run(){
            HashMap<String,Integer> word_freqs = new HashMap<>();
            while (true){
                String word = null;
                try {
                    word = word_space.poll(1, TimeUnit.SECONDS);
                    if (word == null){
                        break;
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!stop_words.contains(word)){
                        word_freqs.put(word, word_freqs.getOrDefault(word,0) + 1);
                }
            }
            try {
                freq_space.put(word_freqs);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public static void get_words(String filepath){
        try {
            FileInputStream inputStream = new FileInputStream(filepath);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String str = null;
            while((str = bufferedReader.readLine())!=null) {
                String[] strings = str.split("[^a-zA-Z0-9]+");
                for (int i = 0; i < strings.length; i++) {
                    if(strings[i].length()>=2)
                        word_space.add(strings[i].toLowerCase());
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
