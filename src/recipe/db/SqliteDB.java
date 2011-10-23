/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package recipe.db;
import java.sql.*;

/**
 *
 * @author Michael
 */
public class SqliteDB {
    Connection con;
    
    public SqliteDB() throws ClassNotFoundException, SQLException {
       Class.forName("org.sqlite.JDBC"); 
       //con = DriverManager.getConnection("jdbc:sqlite:recipe.db");
       String path = System.getProperty("user.dir");
       System.out.println(path);
       con = DriverManager.getConnection("jdbc:sqlite:"+path+"/scripts/recipe.db");
    }
    
    protected ResultSet executeQuery(String query) throws SQLException {
        Statement s = con.createStatement();
        return s.executeQuery(query);
    }
    
    public void disconnect() throws SQLException {
        if (con!=null)
            con.close();    
    }
}
