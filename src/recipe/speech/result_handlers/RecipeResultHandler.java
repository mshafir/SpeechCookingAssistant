/*   This file is part of SpeechCookingAssistant.
 *
 *   SpeechCookingAssistant is free software: you can redistribute it and/or modify
 *   it under the terms of the GNU General Public License as published by
 *   the Free Software Foundation, version 3
 *
 *   SpeechCookingAssistant is distributed in the hope that it will be useful,
 *   but WITHOUT ANY WARRANTY; without even the implied warranty of
 *   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *   GNU General Public License for more details.
 *
 *   You should have received a copy of the GNU General Public License
 *   along with SpeechCookingAssistant.  If not, see <http://www.gnu.org/licenses/>.  
 * 
 *   Copyright 2011 Michael Shafir
 *   Michael.Shafir@gmail.com
 */
package recipe.speech.result_handlers;

import recipe.core.Ingredient;
import recipe.core.Recipe;
import recipe.speech.commands.TTSCommand;
import recipe.speech.commands.TTSStateCommand;

/**
 * 
 * @author Michael
 */
public class RecipeResultHandler extends AbstractResultHandler {

	Recipe recipe;

	public RecipeResultHandler(Recipe r) {
		super();
		this.recipe = r;
	}

	@Override
	public boolean loadCommands() {
		try {
			// load the action commands
			super.registerCommand("speak", new TTSCommand()); // standard text
																// speaker
			super.registerCommand("stateSetSpeak", new TTSStateCommand(recipe)); // recipe
																					// state-aware
																					// text
																					// speaker

			// ;)
			super.addGrammarRule("sue chef unleash hell", "speak",
					"Gah, you killed my father prepare to die.");
			// load the ingredient answering rules
			int cur = 0;
			for (Ingredient i : recipe.Ingredients) {
				String iRule = "<ingredientQuestion> ( ";
				for (int c = 0; c < i.IngredientNames.size(); c++) {
					iRule += i.IngredientNames.get(c);
					if (c != i.IngredientNames.size() - 1) {
						iRule += " | ";
					}
				}
				iRule += " )";
				super.addGrammarRule(iRule, "speak", i.getSpeechString());
				cur++;
			}

			// load the instruction answering rules
			super.addGrammarRule(
					"<stepQuestion> (first | (at the beginning) )",
					"stateSetSpeak", "start");
			super.addGrammarRule("<stepQuestion>", "stateSetSpeak", "");
			super.addGrammarRule("<stepQuestion> (next | now | (after that)) ",
					"stateSetSpeak", "next");
			super.addGrammarRule("<stepQuestion> before that ",
					"stateSetSpeak", "prev");
			return true;
		} catch (Exception ex) {
			System.out.println(ex.getMessage());
		}
		return false;
	}

	public String getWelcome() {
		return "Hello. I'm here to help you prepare " + this.recipe.Title
				+ ". For more help just say Sous-chef " + "more help."; // Start
																		// all
																		// commands
																		// with
																		// Sous-chef.
	}

	public String getHelp() {
		return "Start all commands with Sous-chef. You can ask me what happens next, what happens before that, or how "
				+ "much of any ingredient you need.";
	}

}
