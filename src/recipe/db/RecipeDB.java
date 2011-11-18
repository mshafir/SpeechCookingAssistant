/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package recipe.db;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import recipe.core.Category;
import recipe.core.Ingredient;
import recipe.core.Recipe;

/**
 *
 * @author Michael
 */
public class RecipeDB extends SqliteDB {

    //singleton again - we only want one of these over the whole program (at least for now)
    private static RecipeDB instance;
    public static void initialize() {
        try {
            instance = new RecipeDB();
        } catch (Exception ex) {
            System.err.println(ex);
        }
    }
    public static RecipeDB getInstance() {
        if (instance == null) {
            initialize();
        }
        return instance;
    }
    
    
    public RecipeDB() throws ClassNotFoundException, SQLException {
        super("scripts/recipe.db");
    }
    
    public ArrayList<Category> getCategories() throws SQLException {
        ArrayList<Category> ret = new ArrayList<Category>();
        ResultSet rs = super.executeQuery("SELECT ID,Name FROM Categories "
                + "WHERE Display=1 ORDER BY ID");
        while (rs.next()) {
            ret.add(new Category(rs.getInt("ID"),rs.getString("Name")));
        }
        rs.close();
        return ret;
    }
    
    public ArrayList<Recipe> getRecipes(int categoryID) throws SQLException {
        ArrayList<Recipe> ret = new ArrayList<Recipe>();
        ResultSet rs = super.executeQuery("SELECT DISTINCT ID,Title,Yield FROM Recipes "
                + "INNER JOIN RecipeCategories ON ID=RecipeID "
                + "WHERE CategoryID="+Integer.toString(categoryID)
                + " ORDER BY ID");
        while (rs.next()) {
            ret.add(new Recipe(rs.getInt("ID"),
                    rs.getString("Title"),rs.getFloat("Yield")));
        }
        rs.close();
        return ret;
    }
    
    public ArrayList<Ingredient> getIngredients(int recipeID) throws SQLException {
        ArrayList<Ingredient> ret = new ArrayList<Ingredient>();
        ResultSet rs = super.executeQuery("SELECT DISTINCT Name,Alias1,Alias2,Alias3,"
                + "Amount,Unit,IngredientDetail FROM RecipeIngredients "
                + "INNER JOIN Ingredients ON ID=IngredientID "
                + "WHERE RecipeID="+Integer.toString(recipeID)
                + " ORDER BY Name");
        while (rs.next()) {
            ret.add(new Ingredient(rs.getString("Name"),
                    rs.getString("Alias1"),rs.getString("Alias2"),
                    rs.getString("Alias3"),rs.getFloat("Amount"),
                    rs.getString("Unit"),rs.getString("IngredientDetail")));
        }
        rs.close();
        return ret;
    }
    
    public ArrayList<String> getSteps(int recipeID) throws SQLException {
        ArrayList<String> ret = new ArrayList<String>();
        ResultSet rs = super.executeQuery("SELECT Instruction "
                + "FROM Instructions "
                + "WHERE RecipeID="+Integer.toString(recipeID)
                + " ORDER BY Step");
        while (rs.next()) {
            ret.add(rs.getString("Instruction"));
        }
        rs.close();
        return ret;
    }
    
    public String getSingularUnit(String unit) throws SQLException {
        ResultSet rs = super.executeQuery("SELECT UnitSingular FROM Units WHERE"
                + " UnitShortName='"+unit+"'");
        String text = "";
        if (rs.next())
        	text = rs.getString("UnitSingular");
        rs.close();
        return text;
    }
    
     public String getPluralUnit(String unit) throws SQLException {
        ResultSet rs = super.executeQuery("SELECT UnitPlural FROM Units WHERE"
                + " UnitShortName='"+unit+"'");
        String text = "";
        if (rs.next())
        	text = rs.getString("UnitPlural");
        rs.close();
        return text;
    }
}
