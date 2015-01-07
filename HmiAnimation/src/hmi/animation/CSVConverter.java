/*******************************************************************************
 * The MIT License (MIT)
 * Copyright (c) 2015 University of Twente
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 *******************************************************************************/
/* 
 */

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
