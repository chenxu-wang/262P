import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class Sixteen {
    static private EventManager event_manager;
    public static void main(String[] args) {
        EventManager eventManager = new EventManager();
        new DataStorage(eventManager);
        new StopWordFilter(eventManager);
        new WordFrequencyCounter(eventManager);
        new WordFrequencyApplication(eventManager);
        new CountZ(eventManager);

        eventManager.publish(new String[]{"run", args[0]});
    }

    /**
     * The event management substrate
     */
    private static class EventManager{
        static HashMap<String, ArrayList<Consumer<String[]>>> subscriptions = new HashMap<>();

        private static void subscribe(String event_type, Consumer<String[]> handler){
            if (subscriptions.containsKey(event_type)){
                subscriptions.get(event_type).add(handler);
            }
            else{
                subscriptions.put(event_type,new ArrayList<>(Arrays.asList(handler)));
            }
        }

        private static void publish(String[] event){
            String event_type = event[0];
            if (subscriptions.containsKey(event_type)){
                for (Consumer<String[]> h: subscriptions.get(event_type)){
                    h.accept(event);
                }
            }
        }

    }

    /**
     * The application entities
     */
    private static class DataStorage{
        //Models the contents of the file
        static private ArrayList<String> data = new ArrayList<>();
        DataStorage(EventManager eventManager){
            event_manager = eventManager;
            event_manager.subscribe("load", (String[] event)-> {
                try {
                    load(event);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            event_manager.subscribe("start", (String[] event)->produce_words(event));
        }

        private  static void load(String[] event) throws IOException {
            String path_to_file = event[1];
            asList(Files.lines(Paths.get(path_to_file)).map(String::valueOf).collect(Collectors.joining(" ")).split("[^a-zA-Z0-9]+")).forEach(w->{
                if(w.length()>=2) {
                    data.add(w.toLowerCase());
                }
            });
        }
        private static void produce_words(String[] event){
            for (String w : data){
                event_manager.publish(new String[]{"word", w});
            }
            event_manager.publish(new String[]{"eof"});
        }

    }
    private static class StopWordFilter{
        //Models the stop word filter
        static private ArrayList<String> stop_words = new ArrayList<>();
        StopWordFilter(EventManager eventManager){
            event_manager = eventManager;
            event_manager.subscribe("load", (String[] event)->load(event));
            event_manager.subscribe("word", (String[] event)->is_stop_word(event));
        }

        private void load(String[] event) {
            try {
                asList(Files.lines(Paths.get("../stop_words.txt")).map(String::valueOf).collect(Collectors.joining("")).split(",")).forEach(s->{
                    stop_words.add(s);
                });
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        private static void is_stop_word(String[] event){
            String word = event[1];
            if (!stop_words.contains(word)){
                event_manager.publish(new String[]{"valid_word", word});
            }
        }

    }
    private static class WordFrequencyCounter{
        //Keeps the word frequency data

        HashMap<String, Integer> frequencies = new HashMap<>();
        WordFrequencyCounter(EventManager eventManager){
            event_manager = eventManager;
            event_manager.subscribe("valid_word", (String[] event)->increment_count(event));
            event_manager.subscribe("print", (String[] event)->print_freqs(event));
        }

        private void print_freqs(String[] event) {
            ArrayList<Map.Entry<String, Integer>> list = new ArrayList<>(frequencies.entrySet());
            list.sort((o1, o2) -> Integer.compare(o2.getValue(),o1.getValue())) ;
            for(int i = 0; i < 25; i++){
                System.out.println(list.get(i).getKey() + " - " + list.get(i).getValue());
            }
        }

        private void increment_count(String[] event) {
            String word = event[1];
            if (frequencies.containsKey(word)){
                frequencies.put(word, frequencies.get(word) + 1);
            }
            else{
                frequencies.put(word,1);
            }
        }


    }
    private static class WordFrequencyApplication{
        WordFrequencyApplication(EventManager eventManager){
            event_manager = eventManager;
            event_manager.subscribe("run", (String[] event)->run(event));
            event_manager.subscribe("eof", (String[] event)->stop(event));
        }

        private void stop(String[] event) {
            event_manager.publish(new String[]{"print", null});
        }

        private void run(String[] event) {
            String path_to_file = event[1];
            event_manager.publish(new String[]{"load", path_to_file});
            event_manager.publish(new String[]{"start", null});
        }

    }
    private static class CountZ{
        static int num;

        CountZ(EventManager eventManager){
            event_manager = eventManager;
            event_manager.subscribe("valid_word", (String[] event)->count(event));
            event_manager.subscribe("print", (String[] event)->printNum());
        }

        private static void count(String[] event){
            String word = event[1];
            if (word.indexOf('z') >= 0){
                num++;
            }
        }

        private static void printNum(){
            System.out.println("Non-stop words with z: " + num);
        }
      }

}
