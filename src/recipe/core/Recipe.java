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

import java.util.ArrayList;

/**
 * 
 * @author Michael
 */
public class Recipe {
	public ArrayList<Ingredient> Ingredients;
	public ArrayList<String> Steps;
	public int ID;
	public String Title;
	public float Yield;
	private int step;

	public Recipe(int id, String title, float yield) {
		this.ID = id;
		this.Title = title;
		this.Yield = yield;
		this.step = 0;
	}

	@Override
	public String toString() {
		return Title;
	}

	/**
	 * @return the step
	 */
	public int getStep() {
		return step;
	}

	/**
	 * @param step
	 *            the step to set
	 */
	public void setStep(int step) {
		if (step >= 0 && step < this.Steps.size())
			this.step = step;
	}

	public String currentStep() {
		return Steps.get(step);
	}

}
