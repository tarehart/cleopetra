package dk.aau.cs.ds306e18.tournament.database;

import java.sql.*;

public class CreateTables {

    public static void main(String[] args) {

        Connection con = null;
        Statement stmt = null;

        /*try {
        con = DriverManager.getConnection("jdbc:h2:~/test", "SA", "");
        stmt = con.createStatement();

        ResultSet rs = stmt.executeQuery("select * from bot_tbl");

            while (rs.next()) {
                System.out.println(rs.getString("name"));
            }
        }  catch (Exception e) {
            e.printStackTrace(System.out);
        }*/


        try {
            con = DriverManager.getConnection("jdbc:h2:~/$PROJECT_DIR$/database", "SA", "");
            stmt = con.createStatement();



            stmt.executeUpdate("CREATE TABLE bot_tb (\n" +
                    "   id INT NOT NULL,\n" +
                    "   name VARCHAR(50) NOT NULL,\n" +
                    "   developer VARCHAR(50) NOT NULL,\n" +
                    "   configPath VARCHAR(50) NOT NULL,\n" +
                    "   submissionDate DATE,\n" +
                    "   PRIMARY KEY (id) \n" +
                    ");");

            stmt.executeUpdate("insert INTO bot_tb VALUES (2, 'Beast from the east','Nicolaj', 'fuckIfIKnow',NOW())");

            con.commit();

        }  catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }
}
