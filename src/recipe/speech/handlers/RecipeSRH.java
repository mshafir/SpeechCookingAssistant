/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package recipe.speech.handlers;

import recipe.core.Ingredient;
import recipe.core.Recipe;
import recipe.speech.RASpeechRecognizer;
import recipe.speech.SpeechResultHandler;
import recipe.speech.TTSCommand;
import recipe.speech.TTSStateCommand;

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
            for (Ingredient i : recipe.Ingredients) {
                String iRule = "<ingredientQuestion> ( ";
                for (int c=0;c<i.IngredientNames.size();c++) {
                    iRule += i.IngredientNames.get(c);
                    if (c != i.IngredientNames.size()-1) {
                        iRule += " | ";
                    }
                }
                iRule += " )";
                super.loadCommandRule("r"+Integer.toString(cur), 
                       iRule, "speak", amountConversion(i.Amount)+" "+
                            unitMapping(i.Unit,i.Amount>1)+" of "+
                            i.IngredientNames.get(0),
                       new TTSCommand());
                cur++;
            }
            super.loadCommandRule("rSteps1","<stepQuestion> (first | (at the beginning) )",
                    "stateSetSpeak","start",
                    new TTSStateCommand(recipe));
            super.loadCommandRule("rSteps2","<stepQuestion>",
                    "stateSetSpeak","");
            super.loadCommandRule("rSteps3","<stepQuestion> (next | now | (after that)) ",
                    "stateSetSpeak","next");
            super.loadCommandRule("rSteps4","<stepQuestion> before that ",
                    "stateSetSpeak","prev");
        } catch (Exception ex) {
            System.out.println(ex.getMessage());
        }
    }
    
    public String amountConversion(float amount) {
        int num = (int)amount;
        String start = Integer.toString(num);
        int remainder = (int)((amount-num)*100);
        
        switch (remainder)
        {
            case 0:
                return start;
            case 25:
                return start + " and a quarter";
            case 33:
                return start + " and a third";
            case 50:
                return start + " and a half";
            case 66:
                return start + " and two thirds";
            case 75:
                return start + " and three quarters";
            default:
                return Float.toString(amount);
        }     
    }
    
    public String unitMapping(String unit,boolean plural) {
        if (unit.equals("c")) {
            return plural ? "cups" : "cup";
        }
        else if (unit.equals("ts")) {
            return plural ? "teaspoons" : "teaspoon";
        }
        else if (unit.equals("tb")) {
            return plural ? "tablespoons" : "tablespoon";
        }
        else {
            return "";
        }
    }
}
