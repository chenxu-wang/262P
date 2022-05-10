import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Map;
import java.util.Properties;

public class Framework {
    public static void main(String[] args) {
        Class countCls = null;
        Class extractCls = null;
        URL classUrl1 = null;
        URL classUrl2 = null;
        String pathToJar1 = "";
        String pathToJar2 = "";
        String nameOfCount = "";
        String nameOfExtract = "";

        try {
            Properties prop = new Properties();
            String propFileName = "config.properties";
            FileInputStream ip= new FileInputStream(propFileName);
            if (ip != null) {
                prop.load(ip);
            } else {
                throw new FileNotFoundException("File not found");
            }
            pathToJar1 = prop.getProperty("pathToJar1");
            pathToJar2 = prop.getProperty("pathToJar2");
            nameOfCount = prop.getProperty("nameOfCount");
            nameOfExtract = prop.getProperty("nameOfExtract");
            ip.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try{
            // find classes in jar file in config
            classUrl1 = new File(pathToJar1).toURI().toURL();
        } catch (Exception e) {
            e.printStackTrace();
        }
        URL[] classUrls = {classUrl1};
        URLClassLoader cloader = new URLClassLoader(classUrls);
        try {
            extractCls = cloader.loadClass(nameOfExtract);
        } catch (Exception e) {
            e.printStackTrace();
        }
        try{
            // find classes in jar file in config
            classUrl2 = new File(pathToJar2).toURI().toURL();
        } catch (Exception e) {
            e.printStackTrace();
        }
        URL[] classUrls2 = {classUrl2};
        cloader = new URLClassLoader(classUrls2);
        try {
            countCls = cloader.loadClass(nameOfCount);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (countCls != null && extractCls != null) {
            try {
                Count count = (Count) countCls.getDeclaredConstructor().newInstance();
                Extract extract = (Extract) extractCls.getDeclaredConstructor().newInstance();
                ArrayList<String> words = extract.extract_words(args[0]);
                ArrayList<Map.Entry<String, Integer>> wordsCount = count.CountWords(words);
                // int i = 0;
                for (Map.Entry<String, Integer> entry : wordsCount) {
                  // if(i == 25) break;
                    System.out.println(entry.getKey() + " - " + entry.getValue());
                    // i++;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
          System.out.println("fail");
        }
    }
    }
