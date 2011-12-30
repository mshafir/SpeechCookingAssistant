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

import java.awt.Component;

/**
 * 
 * @author Michael
 */
public class Category extends Component {
	public int ID;
	public String Name;

	public Category(int id, String name) {
		this.ID = id;
		this.Name = name;
	}

	@Override
	public String toString() {
		return Name;
	}
}
