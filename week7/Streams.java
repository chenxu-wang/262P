import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class Streams {
    private static ArrayList<String> stop_words = new ArrayList<>();
    public static void main(String[] args) throws IOException {
        asList(Files.lines(Paths.get("stop_words.txt")).map(String::valueOf).collect(Collectors.joining("")).split(",")).forEach(s->{
            stop_words.add(s);
        });
        ConcurrentMap<String, Integer> freqMap =
                asList(Files.lines(Paths.get("pride-and-prejudice.txt")).map(String::valueOf).collect(Collectors.joining(" ")).split("[^a-zA-Z0-9]+"))
                        .parallelStream()
                        .filter(s -> (!stop_words.contains(s.toLowerCase())&&!s.isEmpty()&&s.length()>=2))
                        .collect(Collectors.toConcurrentMap(w -> w.toLowerCase(), w -> 1, Integer::sum));
        PriorityQueue<String> pq = new PriorityQueue<>((n1, n2) -> freqMap.get(n2) - freqMap.get(n1));
        pq.addAll(freqMap.keySet());
        for(int i = 0; i < 25; i++){
            String word = pq.poll();
            System.out.println( word+ " - " + freqMap.get(word));
        }

    }
}