import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Count1 implements Count {
    public Count1(){};
    public ArrayList<Map.Entry<String, Integer>> CountWords(ArrayList<String> words) {
        Map<String,Integer> count = new HashMap<>();
        ArrayList<Map.Entry<String,Integer>> result = new ArrayList<>();
        for (String s: words){
            if (count.containsKey(s)){
                count.put(s, count.get(s)+1);
            }
            else{
                count.put(s,1);
            }
        }
        ArrayList<Map.Entry<String, Integer>> list = new ArrayList<>(count.entrySet());
        list.sort((o1, o2) -> Integer.compare(o2.getValue(),o1.getValue())) ;
        ArrayList<Map.Entry<String,Integer>> newResult = new ArrayList<>(list.subList(0,25));
        return newResult;
    }
}
