import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.*;
import java.util.List;
import java.util.jar.*;

public class JarClasses {
    private static void findClassInfo(String jarFile) throws ClassNotFoundException, IOException {
        Map<String, Integer> pub = new HashMap<>();
        Map<String, Integer> pri = new HashMap<>();
        Map<String, Integer> sta= new HashMap<>();
        Map<String, Integer> pro = new HashMap<>();
        Map<String, Integer> fields = new HashMap<>();
        List<String> result = new ArrayList<>();
         JarFile jar = null;
        try {
            jar = new JarFile(jarFile);

        } catch (IOException e) {System.out.println(e);}
        Set<String> classNames = getClassNamesFromJarFile(jar);
        Set<Class> classes = new HashSet<>(classNames.size());
        try (URLClassLoader cl = URLClassLoader.newInstance(
                new URL[] { new URL("jar:file:" + jarFile + "!/") })) {
            for (String name : classNames) {
                Class clazz = cl.loadClass(name); // Load the class by its name
                classes.add(clazz);
            }
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
        for(Class e: classes) {

                result.add(e.getName());
                Field[] x = e.getDeclaredFields();
                Method[] y = e.getDeclaredMethods();
                fields.put(e.getName(), x.length);
                for (Method field : y) {
                    int f = field.getModifiers();
                    if (Modifier.isPublic(f)) {
                        if (pub.get(e.getName()) == null) {
                            pub.put(e.getName(), 1);
                        } else {
                            pub.put(e.getName(), pub.get(e.getName()) + 1);
                        }
                    }
                    if (Modifier.isPrivate(f)) {
                        if (pri.get(e.getName()) == null) {
                            pri.put(e.getName(), 1);
                        } else pri.put(e.getName(), pri.get(e.getName()) + 1);
                    }
                    if (Modifier.isProtected(f)) {
                        if (pro.get(e.getName()) == null) {
                            pro.put(e.getName(), 1);
                        } else pro.put(e.getName(), pro.get(e.getName()) + 1);
                    }
                    if (Modifier.isStatic(f)) {
                        if (sta.get(e.getName()) == null) {
                            sta.put(e.getName(), 1);
                        } else sta.put(e.getName(), sta.get(e.getName()) + 1);
                    }
                }
        }

        Collections.sort(result);
        for (String name : result) {
            System.out.println("----------" + name + "----------");
            System.out.println("Public methods: " + pub.getOrDefault(name,0));
            System.out.println("Private methods: " + pri.getOrDefault(name, 0));
            System.out.println("Protected methods: " + pro.getOrDefault(name, 0));
            System.out.println("Static methods: " + sta.getOrDefault(name, 0));
            System.out.println("Fields methods: " + fields.getOrDefault(name, 0));
        }
    }


    public static void main(String[] args) throws IOException, ClassNotFoundException {
        String jarFile = args[0];
        findClassInfo(jarFile);
    }
    public static Set<String> getClassNamesFromJarFile(JarFile givenFile) throws IOException {
        Set<String> classNames = new HashSet<>();
        try (JarFile jarFile = givenFile) {
            Enumeration<JarEntry> e = jarFile.entries();
            while (e.hasMoreElements()) {
                JarEntry jarEntry = e.nextElement();
                if (jarEntry.getName().endsWith(".class")) {
                    String className = jarEntry.getName()
                            .replace("/", ".")
                            .replace(".class", "");
                    classNames.add(className);
                }
            }
            return classNames;
        }
    }
}
