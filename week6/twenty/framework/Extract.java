import java.io.IOException;
import java.util.ArrayList;

public interface Extract {
    ArrayList<String> extract_words(String filepath) throws IOException;
}
