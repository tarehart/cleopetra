package dk.aau.cs.ds306e18.tournament.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

public class CreateTables {

    public static void main(String[] args) {
        createTables();
    }

    static private void testReadFromTable() {
        Connection con = null;
        Statement stmt = null;

        try {
            con = DriverManager.getConnection("jdbc:h2:./database/db", "SA", "");
            stmt = con.createStatement();

            ResultSet rs = stmt.executeQuery("select * from bot_tb");

            while (rs.next()) {
                System.out.println(rs.getString("name"));
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    static private void createTables() {

        Connection con = null;
        Statement stmt = null;

        try {
            con = DriverManager.getConnection("jdbc:h2:./database/db", "SA", "");
            stmt = con.createStatement();

            stmt.executeUpdate("CREATE TABLE Bot (\n" +
                    "   Bot_id INT NOT NULL,\n" +
                    "   Name VARCHAR(20) NOT NULL,\n" +
                    "   Config_file VARCHAR(20) NOT NULL,\n" +
                    "   Description VARCHAR(200) NOT NULL,\n" +
                    "   PRIMARY KEY (Bot_id)\n" +
                    ");");



            stmt.executeUpdate("CREATE TABLE Team (\n" +
                    "   Team_id INT,\n" +
                    "   Name VARCHAR(20) NOT NULL,\n" +
                    "   Seed VARCHAR(30) NOT NULL,\n" +
                    "   Description VARCHAR(200) NOT NULL,\n" +
                    "   Bot_id INT NOT NULL,\n" +
                    "   PRIMARY KEY (Team_id),\n" +
                    "   FOREIGN KEY (Bot_id) REFERENCES Bot(Bot_id) \n" +
                    ");");

            stmt.executeUpdate("CREATE TABLE Stage (\n" +
                    "   Stage_id INT NOT NULL,\n" +
                    "   Name VARCHAR(20) NOT NULL,\n" +
                    "   Format VARCHAR(20) NOT NULL,\n" +
                    "   State VARCHAR(15) NOT NULL,\n" +
                    "   SubmissionDate DATE,\n" +
                    "   PRIMARY KEY (Stage_id) \n" +
                    ");");

            stmt.executeUpdate("CREATE TABLE Tournament (\n" +
                    "   Tournament_id INT NOT NULL,\n" +
                    "   Name VARCHAR(30) NOT NULL,\n" +
                    "   Tiebreaker_rule VARCHAR(30) NOT NULL,\n" +
                    "   State VARCHAR(15) NOT NULL,\n" +
                    "   Stage_id INT NOT NULL,\n" +
                    "   Team_id INT NOT NULL,\n" +
                    "   PRIMARY KEY (Tournament_id), \n" +
                    "   FOREIGN KEY (Stage_id) REFERENCES Stage(Stage_id), \n" +
                    "   FOREIGN KEY (Team_id) REFERENCES Team(Team_id) \n" +
                    ");");




            //stmt.executeUpdate("insert INTO bot_tb VALUES (2, 'Beast from the east','Nicolaj', 'fuckIfIKnow',NOW())");

            con.commit();

        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }
}
