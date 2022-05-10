import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Count2 implements Count {
    public Count2(){};
    public ArrayList<Map.Entry<String, Integer>> CountWords(ArrayList<String> words) {
      String[] letter = {"a","b","c","d","e","f","g","h","i","j","k","l","m","n","o","p","q","r","s","t","u","v","w","x","y","z"};
        Map<String,Integer> count = new HashMap<>();
        ArrayList<Map.Entry<String,Integer>> result = new ArrayList<>();
        for (String l:letter){
          count.put(l,0);
        }
        for (String s: words){
            if (count.containsKey(s.charAt(0)+"") && Character.isLetter(s.charAt(0))){
                count.put((s.charAt(0)+""), count.get(s.charAt(0)+"")+1);
            }
            else{
              if(Character.isLetter(s.charAt(0)))
                count.put((s.charAt(0)+""),1);
            }
        }
        ArrayList<Map.Entry<String, Integer>> list = new ArrayList<>(count.entrySet());
        list.sort((o1, o2) -> (o1.getKey().compareTo(o2.getKey()))) ;
        ArrayList<Map.Entry<String,Integer>> newResult = new ArrayList<>(list.subList(0,26));
        return newResult;
    }
}
