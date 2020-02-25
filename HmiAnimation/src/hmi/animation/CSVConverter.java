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
package hmi.animation;

import hmi.xml.XMLStructureAdapter;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.StringTokenizer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Reads SkeletonInterpolator files in CSV format, and converts them to SkeletonInterpolator XML format.
 */
public class CSVConverter extends XMLStructureAdapter 
{
    private static Logger logger = LoggerFactory.getLogger(CSVConverter.class.getName());
    
    
    public CSVConverter(String resourceDir, String infile, String outfile)
    {
        System.out.println("CSVConverter " + resourceDir + ", " + infile + ", " + outfile);
        FileInputStream instream = null;
        BufferedReader in = null;
        PrintWriter out = null;
        
        try {
        
            instream = new FileInputStream(infile);
            in = new BufferedReader(new InputStreamReader(instream));
            out = new PrintWriter(outfile);
        } catch (FileNotFoundException e) {
            System.out.println("CSVConverter: Could not find input file \"" + infile + "\"");
            System.exit(1);
        }
        
        out.print("<SkeletonInterpolator encoding=\"T1R\" parts=\"");
        
        String line = "";
        try {
            String firstLine = in.readLine();
            System.out.println("Firstline:" + firstLine);
            StringTokenizer tokenizer = new StringTokenizer(firstLine, ", ");
            
            // skip first four tokens
            tokenizer.nextToken();
            tokenizer.nextToken();
            tokenizer.nextToken();
            tokenizer.nextToken();
            
            String token = tokenizer.nextToken(); // first joint
            out.print(token);
            // remainder should be joint names, separated by commas/spaces
            while (tokenizer.hasMoreTokens()) {                        
                token = tokenizer.nextToken();
                out.print(' ');    
                out.print(token);                    
            }
            out.println("\">");
            
            String x, y, z, w;        
            while (line != null) {           
                line = in.readLine();
                
                if (line != null) {
                    tokenizer = new StringTokenizer(line, ", ");
                    out.print("  ");
                    token = tokenizer.nextToken(); // time stamp
                    out.print(token);
                    
                    x = tokenizer.nextToken(); // Tx
                    y = tokenizer.nextToken(); // Ty
                    z = tokenizer.nextToken(); // Tz
                    out.print(' '); out.print(x);
                    out.print(' '); out.print(y);
                    out.print(' '); out.print(z);
                    
                    while (tokenizer.hasMoreTokens()) {   // then we expect four quat components, in XYZW order                     
                        x = tokenizer.nextToken();
                        y = tokenizer.nextToken();
                        z = tokenizer.nextToken();
                        w = tokenizer.nextToken();
                        out.print(' '); out.print(w);
                        out.print(' '); out.print(x);
                        out.print(' '); out.print(y);
                        out.print(' '); out.print(z);
                    } 
                    out.println();            
                }         
            }
            out.println("</SkeletonInterpolator>");
            in.close();
            out.close();
        } catch (IOException e) {
             System.out.println("CSVConverter: " + e);
             
        }
        
    }
    

//    private static final String[] empty_PartIds = new String[0];
//
//    private String[] partIds = empty_PartIds;
//
//    private ConfigList configs;
//
//    private String configType;
//
//    private int configSize; // length of a single config, in number of floats.
//
//    private int stride; // number of floats for a single joint, except for a possible
//                        // rootTranslation
//
//    private boolean hasRootTranslation, hasTranslation, hasRotation, hasScale, hasVelocity,
//            hasAngularVelocity;
//
//    private String rotationEncoding = "Quat"; // default is quaternions
//
//    private VObject[] targetParts;
//    
//    private VJoint target;
//
//    // cached values for the interpolation process:
//    private int lowerIndex, upperIndex;
//
//    private double lowerTime, upperTime, interval;
//
//    private float[] lowerConfig, upperConfig;
//
//    private float[] buf = new float[4]; // temp copy buffer for Vec3f and Quat4f elements

//    /**
//     * Creates a new, uninitialized, SkeletonInterpolator
//     */
//    public SkeletonInterpolator(XMLTokenizer tokenizer) throws IOException
//    {
//        super();
//        readXML(tokenizer);
//    }
//
//    /**
//     * Creates a new SkeletonInterpolator for a specified ConfigList, VParts, and Config type. The
//     * Config type should be a String like "T1R" or "R", or "TRSVW".
//     */
//    public SkeletonInterpolator(String[] partIds, ConfigList configs, String configType)
//    {
//    
//    }

    public static void main(String[] arg)
    {

        String humanoidResources = "Humanoids/armandia/dae";
        String colladafile = "armandia-toplevel.dae";
        String infile = "";
        String outfile = "";

        switch (arg.length)
        {
        case 1:
            infile = arg[0];
            int extPos = infile.lastIndexOf(".csv");
            String basefile = (extPos <0) ? infile : infile.substring(0, extPos);
            infile = basefile + ".csv";
            outfile = basefile + ".xml";
            break;
        case 2:
            infile = arg[0];
            outfile = arg[1];
            break;
        default:
            System.out.println("provide conversion arguments:  <SkeletonInterpolator file> [<output file>] ");
            System.exit(0);
        }

       

        CSVConverter converter = new CSVConverter(humanoidResources, infile, outfile);
        System.out.println("");
       // converter.convertSkeletonInterpolator(null, infile, outfile);
        System.out.println("Conversion finished");
    }
  
}
