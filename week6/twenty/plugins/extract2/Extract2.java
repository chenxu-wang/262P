import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class Extract2 implements Extract {
    public Extract2(){};
    public ArrayList<String> extract_words(String filepath) throws IOException {
        ArrayList<String> stop_words = new ArrayList<>();
        ArrayList<String> result = new ArrayList<>();
        asList(Files.lines(Paths.get("stop_words.txt")).map(String::valueOf).collect(Collectors.joining("")).split(",")).forEach(s->{
            stop_words.add(s);
        });
        List<String> strings = asList(Files.lines(Paths.get(filepath)).map(String::valueOf).collect(Collectors.joining(" ")).split("[^a-zA-Z0-9]+"));
        for(String s : strings){
            if(!stop_words.contains(s.toLowerCase()) && s.length()>=2 && s.toLowerCase().indexOf('z')>=0) result.add(s.toLowerCase());
        }
        return result;
    }
}
