import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class TwentyNine {
    public static void main(String[] args) throws InterruptedException {
        WordFrequencyManager word_freq_manager = new WordFrequencyManager();

        StopWordManager stop_word_manager = new StopWordManager();
        send(stop_word_manager, new Object[]{"init",word_freq_manager});

        DataStorageManager storage_manager = new DataStorageManager();
        send(storage_manager, new Object[]{"init", args[0], stop_word_manager});

        WordFrequencyController wfcontroller = new WordFrequencyController();
        send(wfcontroller, new Object[]{"run", storage_manager});

        word_freq_manager.join();
        stop_word_manager.join();
        storage_manager.join();
        wfcontroller.join();

    }

    public static void send(ActiveWFObject receiver, Object[] message){
        receiver.queue.add(message);
    }

    static class DataStorageManager extends ActiveWFObject {
        private ArrayList<String> data = new ArrayList<>();
        private StopWordManager stop_word_manager;

        private void init(Object[] message){
            stop_word_manager = (StopWordManager) message[2];
            try {
                FileInputStream inputStream = new FileInputStream((String) message[1]);
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                String str = null;
                while((str = bufferedReader.readLine())!=null) {
                    String[] strings = str.split("[^a-zA-Z0-9]+");
                    for (int i = 0; i < strings.length; i++) {
                        if(strings[i].length()>=2)
                        data.add(strings[i].toLowerCase());
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void process_words(Object message) throws InterruptedException {
            WordFrequencyController wordFrequencyController = (WordFrequencyController) message;
            send(stop_word_manager, new Object[]{"filter", new ArrayList<>(data)});
            send(stop_word_manager,new Object[]{"top25", wordFrequencyController});
        }
        @Override
        void dispatch(Object[] message) throws Exception {
            if (message[0].equals("init")){
                init(message);
            }
            else if (message[0].equals("send_word_freqs")){
                process_words(message[1]);
            }
            else{
                send(stop_word_manager, message);
            }
        }
    }
    private static class StopWordManager extends ActiveWFObject{
        ArrayList<String> stop_words = new ArrayList<>();
        private WordFrequencyManager word_freqs_manager;
        private void init(Object message) throws IOException {
            word_freqs_manager = (WordFrequencyManager) message;
            asList(Files.lines(Paths.get("stop_words.txt")).map(String::valueOf).collect(Collectors.joining("")).split(",")).forEach(s->{
                stop_words.add(s);
            });
        }
        private void filter(Object message) throws IOException {
            ArrayList<String> words = (ArrayList<String>) message;
            ArrayList<String>newWords = new ArrayList<>();
            for(String word : words){
                if (!stop_words.contains(word)){
                    newWords.add(word);
                }
            }
            send(word_freqs_manager,new Object[]{"word", new ArrayList<>(newWords)});
        }
        @Override
        void dispatch(Object[] message) throws Exception {
            if (message[0].equals("init")){
                init(message[1]);
            }
            else if (message[0].equals("filter")){
                filter(message[1]);
            }
            else{
                send(word_freqs_manager, message);
            }
        }
    }
    private static class WordFrequencyManager extends ActiveWFObject{
        private HashMap<String,Integer> word_freqs = new HashMap<>();
        private void increment_count(Object message){
            ArrayList<String> words = (ArrayList<String>) message;
            for(String word : words){
                word_freqs.put(word, word_freqs.getOrDefault(word,0)+1);
            }
        }
        private void top25(Object message) {
            WordFrequencyController recipient = (WordFrequencyController) message;
            ArrayList<Map.Entry<String, Integer>> list = new ArrayList<>(word_freqs.entrySet());
            list.sort((o1, o2) -> Integer.compare(o2.getValue(), o1.getValue()));
            send(recipient, new Object[]{"top25",list});
        }
        @Override
        void dispatch(Object[] message) throws Exception {
            if(message[0].equals("word")){
                increment_count(message[1]);
            }
            else if (message[0].equals("top25")){
                top25(message[1]);
            }
        }


    }
    private static class WordFrequencyController extends ActiveWFObject{
        private DataStorageManager storage_manager;
        private void run(Object message){
            storage_manager = (DataStorageManager) message;
            send(storage_manager, new Object[]{"send_word_freqs", this});
        }
        private void display(Object message){
            ArrayList<Map.Entry<String, Integer>> list = new ArrayList<>();
            list = (ArrayList<Map.Entry<String, Integer>>) message;
            int i = 0;
            for (Map.Entry<String, Integer> entry : list) {
                if(i == 25) break;
                System.out.println(entry.getKey() + " - " + entry.getValue());
                i++;
            }
            send(storage_manager,new Object[]{"die"});
            stopMe = true;

        }
        @Override
        void dispatch(Object[] message) throws Exception {
            if (message[0].equals("run")){
                run(message[1]);
            }
            else if (message[0].equals("top25")){
                display(message[1]);
            }

        }

    }
}


abstract class ActiveWFObject extends Thread{
    public Boolean stopMe = false;
    public BlockingQueue<Object[]> queue = new ArrayBlockingQueue<>(20);
    public ActiveWFObject(){
        start();
    }
    public void run(){
        while(!stopMe){
            try {
                Object[] message = queue.poll();
                if (message != null) {
                    dispatch(message);
                    if (message[0].equals("die")) {
                        stopMe = true;
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }
    abstract void dispatch(Object[] message) throws Exception;

}

