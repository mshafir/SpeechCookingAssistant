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
package recipe.speech;

/**
 * A collection of useful helper functions for speech
 * 
 * @author Michael
 * 
 */
public class SpeechHelper {
	public static int[] breakdowns = new int[] { 500, 250, 333, 125 };

	/**
	 * Converts a float to speakable fraction representation
	 * 
	 * @return
	 */
	public static String convertFloat(float amount, boolean speech) {
		int num = (int) amount;
		String start = Integer.toString(num);
		int remainder = (int) ((amount - num) * 1000);
		if (start.equals("0")) {
			start = "";
		} else if (remainder > 0) {
			if (speech) {
				start += " and ";
			} else {
				start += " ";
			}
		}

		if (remainder > 0) {
			int denominator = 0;
			int numerator = 0;
			for (int i : breakdowns) {
				if (remainder % i == 0) {
					denominator = 1000 / i;
					numerator = remainder / i;
					break;
				}
			}

			if (denominator > 0 && numerator > 0) {
				if (speech)
					return start + Integer.toString(numerator) + " "
							+ fraction(denominator);
				else
					return start + Integer.toString(numerator) + "/"
							+ Integer.toString(denominator);
			} else {
				return Float.toString(amount);
			}
		}
		return start;
	}

	public static String fraction(int denominator) {
		switch (denominator) {
		case 2:
			return "half";
		case 3:
			return "third";
		case 4:
			return "quarter";
		case 8:
			return "eighth";
		default:
			return "";
		}
	}
}
