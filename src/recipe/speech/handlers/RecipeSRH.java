/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package recipe.speech.handlers;

import recipe.core.Ingredient;
import recipe.core.Recipe;
import recipe.speech.RASpeechRecognizer;
import recipe.speech.SpeechResultHandler;
import recipe.speech.TTSCommandHandler;

/**
 *
 * @author Michael
 */
public class RecipeSRH extends SpeechResultHandler {
    
    Recipe recipe;
    
    public RecipeSRH(Recipe r, RASpeechRecognizer rec) {
        super(rec);
        this.recipe = r;
    }
    
    @Override
    public void loadCommands() {
        try {
            int cur=0;
            for (Ingredient i : recipe.ingredients) {
                super.loadCommandRule("r"+Integer.toString(cur), 
                        "<ingredientQuestion> "+i.IngredientName, "speak",
                        Float.toString(i.Amount)+" "+i.Unit+" of "+i.IngredientName);
                cur++;
            }
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
    
}
