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
package hmi.faceanimation.converters;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import hmi.faceanimation.model.ActionUnit;
import hmi.faceanimation.model.ActionUnit.Symmetry;
import hmi.faceanimation.model.FACS;
import hmi.faceanimation.model.FACSConfiguration;
import hmi.faceanimation.model.FACS.Side;
import hmi.xml.XMLTokenizer;
import lombok.extern.slf4j.Slf4j;

/**
 * Converts FACS to morph target names given a mapping
 * @author Jan Kolkmeier
 *
 */

@Slf4j
public class FACS2MorphConverter {
	
	public int mappings =0;
	
	private String[] mappedMorphNames;
	private Side[] mappedSide;
	private Integer[] mappedAU;
	private Double[] mappedIntensity;
	
	
	public FACS2MorphConverter() {
		
	}

	public Map<String, Double> convert(FACSConfiguration facsConfig) {
		Map<String, Double> res = new HashMap<String, Double>();
		Float[] values = facsConfig.getValues();
		if (mappedMorphNames == null || mappedMorphNames.length == 0) {
			return res;
		}

		for (int m=0; m < mappedMorphNames.length; m++) {
			// test if assymetric:
			ActionUnit au = FACS.getActionUnit(mappedAU[m]); //FACS.getActionUnitsByIndex().get();
			if (au == null) continue;
			int index = au.getIndex();
			if (au.getSymmetry() == Symmetry.ASYMMETRIC && mappedSide[m] == Side.RIGHT)
				index = index + FACS.getActionUnits().size();
			
			if (values[index] != null) {
				res.put(mappedMorphNames[m], values[index].doubleValue() * mappedIntensity[m].doubleValue());
			}
		}
		return res;
	}

	public void readXML(BufferedReader reader)  {
		List<String> _mappedMorphNames = new ArrayList<String>();
		List<Side> _mappedSide = new ArrayList<Side>();
		List<Integer> _mappedAU = new ArrayList<Integer>();
		List<Double> _mappedIntensity = new ArrayList<Double>();
		
		XMLTokenizer tokenizer = new XMLTokenizer(reader);
		try {
			while (!tokenizer.atETag("FACS2MorphMapping")) {
		        if (tokenizer.getTagName().equals("FACS2MorphMapping")) {
					tokenizer.takeSTag();
		        } else if (tokenizer.atSTag("Map")) {
			    	 HashMap<String, String> attrMap = tokenizer.getAttributes();
					 double intensity = 1.0f;
					 if (attrMap.containsKey("intensity")) {
						 intensity = Double.parseDouble(attrMap.get("intensity"));
					 }
					 Side side = Side.NONE;
					 if (attrMap.containsKey("side")) {
						 side = Side.valueOf(attrMap.get("side"));
					 }
					 int au = Integer.parseInt(attrMap.get("AU"));
					 String morph = attrMap.get("morph");
					 
					 _mappedMorphNames.add(morph);
					 _mappedSide.add(side);
					 _mappedAU.add(au);
					 _mappedIntensity.add(intensity);
					 
					 tokenizer.takeSTag();
					 tokenizer.takeETag();
		    	}
			}
			
			mappedMorphNames = _mappedMorphNames.toArray(new String[0]);
			mappedSide = _mappedSide.toArray(new Side[0]);
			mappedAU = _mappedAU.toArray(new Integer[0]);
			mappedIntensity = _mappedIntensity.toArray(new Double[0]);
			
			for (int m = 0; m < mappedMorphNames.length; m++) {
				log.info("  Mapped AU "+mappedAU[m]+" to "+mappedMorphNames[m]+" ("+mappedSide[m]+")");
			}
			mappings = mappedMorphNames.length;
		} catch (IOException ioe) {
			log.info("Error parsing FACS2Morph Mapping ", ioe);
		}
	}
}
