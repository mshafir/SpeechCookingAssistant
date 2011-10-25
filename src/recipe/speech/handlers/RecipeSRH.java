/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package recipe.speech.handlers;

import recipe.core.Ingredient;
import recipe.core.Recipe;
import recipe.speech.SpeechResultHandler;
import recipe.speech.TTSCommand;
import recipe.speech.TTSStateCommand;

/**
 *
 * @author Michael
 */
public class RecipeSRH extends SpeechResultHandler {
    
    Recipe recipe;
    
    public RecipeSRH(Recipe r) {
        super();
        this.recipe = r;
    }
    
    @Override
    public void loadCommands() {
        try {
            //load the action commands
            super.addAction("speak", new TTSCommand()); //standard text speaker
            super.addAction("stateSetSpeak", new TTSStateCommand(recipe)); //recipe state-aware text speaker
            
            //load the ingredient answering rules
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
                String unit = unitMapping(i.Unit,i.Amount>1);
                String of = unit.equals("") ? "" : " of ";
                String answer = amountConversion(i.Amount)+" "+
                            unit+of+
                            i.IngredientNames.get(0); //aka 3 cups of butter
                
                super.loadCommandRule("r"+Integer.toString(cur), 
                       iRule, "speak", answer);
                cur++;
            }
            
            //load the instruction answering rules
            super.loadCommandRule("rSteps1","<stepQuestion> (first | (at the beginning) )",
                    "stateSetSpeak","start");
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
        if (start.equals("0")) {
            start = "";
        } else if (remainder > 0) {
            start += " and ";
        }
        
        switch (remainder)
        {
            case 0:
                return start;
            case 12:
                return start + "an eighth";
            case 25:
                return start + "a quarter";
            case 33:
                return start + "a third";
            case 50:
                return start + "a half";
            case 66:
                return start + "two thirds";
            case 75:
                return start + "three quarters";
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
