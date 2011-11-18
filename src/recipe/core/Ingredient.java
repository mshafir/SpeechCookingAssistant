/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package recipe.core;

import java.sql.SQLException;
import java.util.ArrayList;

import recipe.db.RecipeDB;
import recipe.speech.SpeechHelper;

/**
 *
 * @author Michael
 */
public class Ingredient {
    public ArrayList<String> IngredientNames;
    public float Amount;
    public String Unit;
    public String Detail;
    
    public Ingredient(String name,String alias1,String alias2,
            String alias3,float amount,String unit,String detail) {
        IngredientNames = new ArrayList<String>();
        IngredientNames.add(name);
        if (alias1 != null)
            IngredientNames.add(alias1);
        if (alias2 != null)
            IngredientNames.add(alias2);
        if (alias3 != null)
            IngredientNames.add(alias3);
        this.Amount = amount;
        this.Unit = unit;
        this.Detail = detail;
    }
    
    public String getSpeechString() throws SQLException {
        String unit = unitMapping(Unit,Amount>1);
        String of = unit.equals("") ? "" : " of ";
        return SpeechHelper.convertFloat(Amount)+" "+
                    unit+of+
                    IngredientNames.get(0); //aka 3 cups of butter
    }
    
    private String unitMapping(String unit,boolean plural) throws SQLException {
        if (plural) {
            return RecipeDB.getInstance().getPluralUnit(unit);
        } else {
            return RecipeDB.getInstance().getSingularUnit(unit);
        }
    }
    
    @Override
    public String toString() {
        return Float.toString(Amount)+ " " + Unit + 
                " " + IngredientNames.get(0) + " " + Detail;
    }
}
