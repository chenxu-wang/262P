package twenty_six;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static java.util.Arrays.asList;

public class TwentySix {
    public static void main(String[] args)
    {
        Connection connection = null;
        try
        {
            // create a database connection
            connection = DriverManager.getConnection("jdbc:sqlite:exercise.db");
            create_db_schema(connection);
            load_file_into_database(args[0], connection);
            Statement statement = connection.createStatement();
            statement.setQueryTimeout(30);  // set timeout to 30 sec.
            ResultSet rs = statement.executeQuery("SELECT value, COUNT(*) as C FROM words GROUP BY value ORDER BY C DESC");
            int i = 0;
            while (rs.next()) {
                if (i == 25) break;
                System.out.println(rs.getString("value") + " - " + rs.getInt("C"));
                i++;
            }
            ResultSet rswithZ = statement.executeQuery("SELECT COUNT(DISTINCT value) as C FROM words WHERE value LIKE '%z%'");
            while (rs.next()){
                System.out.println("Unique words with 'z': " + rswithZ.getInt("C"));
            }


        }
        catch(SQLException | IOException e)
        {
            // if the error message is "out of memory",
            // it probably means no database file is found
            System.err.println(e.getMessage());
        }
        finally
        {
            try
            {
                if(connection != null)
                    connection.close();
            }
            catch(SQLException e)
            {
                // connection close failed.
                System.err.println(e.getMessage());
            }
        }
    }
    public static void create_db_schema(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        statement.setQueryTimeout(30);  // set timeout to 30 sec.
        statement.executeUpdate("drop table if exists words");
        statement.executeUpdate("CREATE TABLE words (id INTEGER PRIMARY KEY AUTOINCREMENT, value)");
        statement.executeUpdate("drop table if exists characters");
        statement.executeUpdate("CREATE TABLE characters (id, word_id, value)");
        statement.close();
    }
    private static void load_file_into_database(String filepath, Connection connection) throws IOException, SQLException {
        ArrayList<String> words_filtered = extract_words(filepath);
        Statement statement = connection.createStatement();
        int row = 0;
        ResultSet rs = statement.executeQuery("SELECT MAX(id) FROM words");
        while(rs.next()){
            row = rs.getInt("MAX(id)");
        }
        PreparedStatement pstmt = connection.prepareStatement("INSERT INTO words VALUES (?, ?)");
        for (String wf: words_filtered){
            pstmt.setInt(1,row);
            pstmt.setString(2,wf);
            pstmt.executeUpdate();
            row++;
        }
//        connection.commit();
        statement.close();
    }
    private static ArrayList<String> extract_words(String filepath) throws IOException {
        ArrayList<String> stop_words = new ArrayList<>();
        ArrayList<String> words_filtered = new ArrayList<>();
        asList(Files.lines(Paths.get("stop_words.txt")).map(String::valueOf).collect(Collectors.joining("")).split(",")).forEach(s->{
            stop_words.add(s);
        });
        List<String> strings = asList(Files.lines(Paths.get(filepath)).map(String::valueOf).collect(Collectors.joining(" ")).split("[^a-zA-Z0-9]+"));
        for(String s : strings){
            if(!stop_words.contains(s.toLowerCase())&&s.length()>=2) words_filtered.add(s.toLowerCase());
        }
        return words_filtered;
    }
}
