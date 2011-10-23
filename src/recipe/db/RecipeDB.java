/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package recipe.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import recipe.core.Category;
import recipe.core.Recipe;

/**
 *
 * @author Michael
 */
public class RecipeDB extends SqliteDB {

    public RecipeDB() throws ClassNotFoundException, SQLException {
        super();
    }
    
    public ArrayList<Category> getCategories() throws SQLException {
        ArrayList<Category> ret = new ArrayList<Category>();
        ResultSet rs = super.executeQuery("SELECT ID,Name FROM Categories ORDER BY ID");
        while (rs.next()) {
            ret.add(new Category(rs.getInt("ID"),rs.getString("Name")));
        }
        rs.close();
        return ret;
    }
    
    public ArrayList<Recipe> getRecipes(int categoryID) throws SQLException {
        ArrayList<Recipe> ret = new ArrayList<Recipe>();
        ResultSet rs = super.executeQuery("SELECT ID,Title,Yield FROM Recipes "
                + "INNER JOIN RecipeCategories ON ID=RecipeID "
                + "WHERE CategoryID="+Integer.toString(categoryID)
                + " ORDER BY ID");
        while (rs.next()) {
            ret.add(new Recipe(rs.getInt("ID"),
                    rs.getString("Title"),rs.getInt("Yield")));
        }
        rs.close();
        return ret;
    }
}
