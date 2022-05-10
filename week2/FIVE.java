import java.io.*;
import java.util.*;
//Enter "cd week2" 
//then "java FIVE ../pride-and-prejudice.txt"
public class FIVE {
    private static HashMap<String,Integer> counts = new HashMap<>();
    private static ArrayList<String> stop_words = new ArrayList<>();
    private static Queue<String> heap = new PriorityQueue<>((n1, n2) -> counts.get(n1) - counts.get(n2));
    public static void main(String[] args) throws FileNotFoundException {
        stop_words();
        countWords(counts,args[0],stop_words);
        topKFrequent(counts,25);
        printResult(heap, counts);
    }
    public static ArrayList<String> stop_words() throws FileNotFoundException {

        File stop_words_file = new File("../stop_words.txt");
        Scanner scanner = new Scanner(stop_words_file);
        String list = "";
        while (scanner.hasNext()) {
            list = scanner.next();
        }
        String[] split_stop_words = list.split(",");
        // add each word to ArrayList
        for (int i = 0; i < split_stop_words.length; i++) {
            stop_words.add(split_stop_words[i]);
        }
        scanner.close();
        return stop_words;
    }
    private static void countWords(HashMap<String,Integer> frequencies,String filepath, ArrayList<String> stop_words) {
//        HashMap<String, Integer> frequencies = new HashMap<String, Integer>();
        try {
            FileInputStream inputStream = new FileInputStream(filepath);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
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
//        return frequencies;
    }
    private static void topKFrequent(HashMap<String, Integer> map, int k){
//        Queue<String> heap = new PriorityQueue<>((n1, n2) -> map.get(n1) - map.get(n2));
        String[] res = new String[25];
        for(String s : map.keySet()){
            heap.add(s);
            if(heap.size() > k)
                heap.poll();
        }
    }
    private static void printResult(Queue<String> heap, HashMap<String,Integer> counts){
        String[] res = new String[25];
        for(int i = 24; i >= 0; --i)
        {
            String word = heap.poll();
            res[i] = word;
        }
        for(int i = 0; i < 25; i++)
            System.out.println(res[i] + " - " + counts.get(res[i]));
    }

}
