import javax.swing.*;
import java.sql.*;

public class Driver {
    public static void main(String[] args) {
        try {

            Connection connection = DriverManager.getConnection("jdbc:mysql://dijkstra.ug.bcc.bilkent.edu.tr/esad_tok", "esad.tok", "yW3Iu8Z1");

            Statement statement = connection.createStatement();

            createTable(connection);
            insertValues(connection);
            ResultSet resultSet;

            System.out.println("The names of the students who applied 3 companies for internships:");
            resultSet = statement.executeQuery("select sname from (select sid from (select sid, count(*) as cnt from apply group by sid having count(*) = 3) as X) as tab1 natural join student;" );
            while(resultSet.next()) {
                System.out.println(resultSet.getString("sname"));
            }

            System.out.println("\nThe sum of the quotas of the companies which are applied by the student having the most applications:");
            resultSet = statement.executeQuery("select sname, sum(quota) as res from student natural join apply natural join company GROUP BY cid  having count(*)  = (select max(cnt) from (select cid, count(*) as cnt from apply group by cid) as tab1);");
            while(resultSet.next()) {
                System.out.println(resultSet.getString("res"));
            }

            System.out.println("\nThe average number of applications of students by each nationality:");
            resultSet = statement.executeQuery("select nationality, avg(cnt) as res from (select sid, count(*) as cnt from apply group by sid) as tab1 natural join student group by nationality");
            while(resultSet.next()) {
                System.out.println(resultSet.getString("nationality") + " - " + resultSet.getString("res"));
            }

            System.out.println("\nThe name of the companies which are applied by all students from the freshman year:");
            resultSet = statement.executeQuery("select distinct cname from (select sid from student where year = \"freshman\") as tab1 natural join apply natural join company;");
            while(resultSet.next()) {
                System.out.println(resultSet.getString("cname"));
            }

            System.out.println("\nThe average gpa of applied students for each company:");
            resultSet = statement.executeQuery("select cid, avg(gpa) as avg_gpa from student natural join apply natural join company group by cid;");
            while(resultSet.next()) {
                System.out.println(resultSet.getString("cid") + " - " + resultSet.getString("avg_gpa"));
            }


        } catch (Exception exc) {
            exc.printStackTrace();
        }
    }

    public static void createTable(Connection connection) throws SQLException {
        String table1 = "create table student(" +
                "sid char(12)," +
                "sname varchar(50)," +
                "bdate date," +
                "address varchar(50)," +
                "scity varchar(20)," +
                "year char(20)," +
                "gpa float," +
                "nationality varchar(20)," +
                "PRIMARY KEY (sid)) ENGINE = INNODB;";

        String table2 = "create table company(" +
                "cid char(8)," +
                "cname varchar(20)," +
                "quota int," +
                "gpathreshold float," +
                "primary key(cid)) ENGINE = INNODB;";

        String table3 = "create table apply(" +
                "sid char(12)," +
                "cid char(8)," +
                "FOREIGN KEY(sid) references student(sid)," +
                "FOREIGN KEY(cid) references company(cid)) ENGINE = INNODB;";

        Statement statement = connection.createStatement();

        statement.execute("DROP TABLE IF EXISTS `apply`;");
        statement.execute("DROP TABLE IF EXISTS `student`;");
        statement.execute("DROP TABLE IF EXISTS `company`;");

        statement.execute(table1);
        statement.execute(table2);
        statement.execute(table3);
    }

    public static void insertValues(Connection connection) throws SQLException {
        Statement statement = connection.createStatement();
        statement.executeUpdate("insert into student values('21000001', 'Marco', '1998-05-31', 'Strobelallee', 'Dortmund', 'senior', 2.64, 'DE');");
        statement.executeUpdate("insert into student values('21000002', 'Arif', '2001-11-17', 'Nisantasi', 'Istanbul', 'junior', 3.86, 'TC');");
        statement.executeUpdate("insert into student values('21000003', 'Veli', '2003-02-19', 'Cayyolu', 'Ankara', 'freshman', 2.21, 'TC');");
        statement.executeUpdate("insert into student values('21000004', 'Ayse', '2003-05-01', 'Tunali', 'Ankara', 'freshman', 2.52, 'TC');");

        statement.executeUpdate("insert into company values('C101', 'milsoft', 3, 2.50);");
        statement.executeUpdate("insert into company values('C102', 'merkez bankasi', 10, 2.45);");
        statement.executeUpdate("insert into company values('C103', 'tubitak', 2, 3.00);");
        statement.executeUpdate("insert into company values('C104', 'havelsan', 5, 2.00);");
        statement.executeUpdate("insert into company values('C105', 'aselsan', 4, 2.50);");
        statement.executeUpdate("insert into company values('C106', 'tai', 2, 2.20);");
        statement.executeUpdate("insert into company values('C107', 'amazon', 1, 3.85);");

        statement.executeUpdate("insert into apply values('21000001', 'C101');");
        statement.executeUpdate("insert into apply values('21000001', 'C102');");
        statement.executeUpdate("insert into apply values('21000001', 'C104');");
        statement.executeUpdate("insert into apply values('21000002', 'C107');");
        statement.executeUpdate("insert into apply values('21000003', 'C104');");
        statement.executeUpdate("insert into apply values('21000003', 'C106');");
        statement.executeUpdate("insert into apply values('21000004', 'C102');");
        statement.executeUpdate("insert into apply values('21000004', 'C106');");

    }
}
