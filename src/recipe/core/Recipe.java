/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package recipe.core;

import recipe.core.Ingredient;
import java.util.ArrayList;

/**
 *
 * @author Michael
 */
public class Recipe {
    public ArrayList<Ingredient> ingredients;
    public ArrayList<String> steps;
    public int ID;
    public String Title;
    public int Yield;
    
    public Recipe(int id, String title, int yield) {
        this.ID = id;
        this.Title = title;
        this.Yield = yield;
    }
    
    @Override
    public String toString() {
        return Title + "         yields: "+Integer.toString(ID) + " serving(s)";
    }
    
}
