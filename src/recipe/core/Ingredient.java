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

	public Ingredient(String name, String alias1, String alias2, String alias3,
			float amount, String unit, String detail) {
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
		String unit = unitMapping(Unit, Amount > 1);
		String of = unit.equals("") ? "" : " of ";
		return SpeechHelper.convertFloat(Amount, true) + " " + unit + of
				+ IngredientNames.get(0); // aka 3 cups of butter
	}

	private String unitMapping(String unit, boolean plural) throws SQLException {
		if (plural) {
			return RecipeDB.getInstance().getPluralUnit(unit);
		} else {
			return RecipeDB.getInstance().getSingularUnit(unit);
		}
	}

	@Override
	public String toString() {
		return SpeechHelper.convertFloat(Amount, false) + " " + Unit + " "
				+ IngredientNames.get(0) + " " + Detail;
	}
}
