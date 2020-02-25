/*******************************************************************************
 * Copyright (C) 2009-2020 Human Media Interaction, University of Twente, the Netherlands
 *
 * This file is part of the Articulated Social Agents Platform BML realizer (ASAPRealizer).
 *
 * ASAPRealizer is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License (LGPL) as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * ASAPRealizer is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with ASAPRealizer.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/
package hmi.tts.fluency8;

import java.util.HashMap;

public class PhonemeNameToNumber {

	static HashMap<String,Integer> n2n = null;
	
	static
	{
		n2n = new HashMap<String,Integer>();
		n2n.put("_",0);
		
		n2n.put("h", 1);
		n2n.put("H", 2);
		n2n.put("?",3);
		
		n2n.put("i",4);
		n2n.put("J",5);
		
		n2n.put("p", 6);
		n2n.put("b", 7);
		n2n.put("m", 8);
		n2n.put("w", 9);
		
		n2n.put("t", 10);
		n2n.put("d", 11);
		n2n.put("D", 12);
		n2n.put("n", 13);
		n2n.put("nj", 14);
		n2n.put("n0", 15);
		
		n2n.put("k", 16);
		n2n.put("g", 17);
		n2n.put("N", 18);
		
		n2n.put("f", 19);
		n2n.put("v", 20);
		
		n2n.put("s", 21);
		n2n.put("z", 22);
		
		n2n.put("S", 23);
		n2n.put("Z", 24);
		n2n.put("tj", 25);
		n2n.put("dj", 26);
		n2n.put("nj", 27);
		
		n2n.put("x", 28);
		n2n.put("G", 29);
		
		n2n.put("l", 30);
		n2n.put("L", 31);
		n2n.put("r", 32);
		n2n.put("R", 33);
		n2n.put("j", 34);
		
		n2n.put("a", 35);
		n2n.put("Au", 36);
		
		n2n.put("y", 37);
		n2n.put("u", 38);
		n2n.put("2", 39);
		n2n.put("W", 40);
		
		n2n.put("e", 41);
		
		n2n.put("o", 42);
		
		n2n.put("I", 43);
		n2n.put("I:", 44);
		
		n2n.put("E", 45);
		n2n.put("Ei", 46);
		
		n2n.put("Y", 47);
		n2n.put("Y:", 48);
		n2n.put("Y~", 49);
		
		n2n.put("O", 50);
		n2n.put("O:", 51);
		n2n.put("O~", 52);
		
		n2n.put("A", 53);
		n2n.put("Q", 54);
		n2n.put("A~", 55);
		n2n.put("9y", 56);
		
		n2n.put("@", 57);
		
	}
	/** gives silence (0) for every unknown phoneme */
	public static int getPhonemeNumber(String code)
	{
        if (code == null) return 0;
        if (!n2n.containsKey(code)) return 0;
		return n2n.get(code);
	}
}
