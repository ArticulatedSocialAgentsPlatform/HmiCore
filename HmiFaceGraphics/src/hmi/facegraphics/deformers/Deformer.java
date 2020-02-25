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
package hmi.facegraphics.deformers;

import hmi.faceanimation.model.FAP;
import hmi.faceanimation.model.MPEG4;
import hmi.facegraphics.GLHead;
import hmi.math.Vec3f;
import hmi.xml.XMLFormatting;
import hmi.xml.XMLStructureAdapter;
import hmi.xml.XMLTokenizer;

import java.io.IOException;
import java.util.HashMap;

public abstract class Deformer extends XMLStructureAdapter implements DeformerServer
{
    protected float size;
    protected FAP fap;

    protected int value;
    protected GLHead head;
    protected float[] fpNeutralPos, fpPos;
    private float[] displacement;

    protected DeformerClient client;

    public Deformer()
    {
    }

    public void setHead(GLHead head)
    {
        this.head = head;
    }

    public void setFAP(FAP fap)
    {
        this.fap = fap;
        fpNeutralPos = head.getFPPosition(fap.getFeaturePoint());
        if (fpNeutralPos == null)
            fpNeutralPos = new float[] { 0.0f, 0.0f, 0.0f };
    }

    public FAP getFAP()
    {
        return fap;
    }

    public void updateSize(float size)
    {
        this.size = size;
        head.calculateVertexWeights();
        deform();
    }

    public void setSize(float size)
    {
        updateSize(size);
        emitSize(size);
    }

    public float getSize()
    {
        return size;
    }

    public void updateValue(int value)
    {
        this.value = value;
        deform();
        if (client != null)
            client.setDisplacement(fpPos);
    }

    public void setValue(int value)
    {
        updateValue(value);
        emitValue(value);
    }

    public int getValue()
    {
        return value;
    }

    public void setClient(DeformerClient client)
    {
        this.client = client;
    }

    protected void emitSize(float size)
    {
        if (client != null)
            client.updateSize(size);
    }

    protected void emitValue(int value)
    {
        if (client != null)
            client.updateValue(value);
    }

    public abstract void copyFrom(Deformer source);

    public void deform()
    {
        if (fap.getNumber() == 23)
        {
            if (head.getLeftEye()!=null)head.getLeftEye().setYawValue(value);
            return;
        }
        else if (fap.getNumber() == 24)
        {
            if (head.getRightEye()!=null)head.getRightEye().setYawValue(value);
            return;
        }
        else if (fap.getNumber() == 25)
        {
            if (head.getLeftEye()!=null)head.getLeftEye().setPitchValue(value);
            return;
        }
        else if (fap.getNumber() == 26)
        {
            if (head.getRightEye()!=null)head.getRightEye().setPitchValue(value);
            return;
        }
        else if (fap.getNumber() == 27)
        {
            if (head.getLeftEye()!=null)head.getLeftEye().setThrustValue(value);
            return;
        }
        else if (fap.getNumber() == 28)
        {
            if (head.getRightEye()!=null)head.getRightEye().setThrustValue(value);
            return;
        }
        else if (fap.getNumber() == 48)
        {
            if (head.getNeck()!=null)head.getNeck().setPitchValue(value);
            return;
        }
        else if (fap.getNumber() == 49)
        {
            if (head.getNeck()!=null)head.getNeck().setYawValue(value);
            return;
        }
        else if (fap.getNumber() == 50)
        {
            if (head.getNeck()!=null)head.getNeck().setRollValue(value);
            return;
        }

        float[] disp = getDisplacement();
        float[] opos = fpNeutralPos;
        float[] fpPos = { opos[0] + disp[0], opos[1] + disp[1], opos[2] + disp[2] }; // fpNeutralPos + delta's
        this.fpPos = fpPos;

        if (fap.getNumber() == 3)
            if (head.getLowerJaw()!=null)head.getLowerJaw().setOpen(disp);
        if (fap.getNumber() == 14)
            if (head.getLowerJaw()!=null)head.getLowerJaw().setThrust(disp);
        if (fap.getNumber() == 15)
            if (head.getLowerJaw()!=null)head.getLowerJaw().setShift(disp);

        head.scheduleDeform();
    }

    public float[] getDisplacement()
    {
        calculateDisplacement();
        return displacement;
    }

    public float[] getFullDisplacement()
    {
        int temp = value;
        value = 1024;
        calculateDisplacement();
        value = temp;
        float[] fullDisplacement = displacement.clone();
        calculateDisplacement();
        return fullDisplacement;
    }

    private void calculateDisplacement()
    {
        if (fap == null)
            return;

        String fapu = fap.getUnit().toString();
        float unit = head.getFAPU(fapu);
        if (unit == 0.0f && !fapu.equals("NA"))
        {
            System.err.println("FAPU " + fapu + " not available, continuing with dummy value");
            unit = 0.01f;
        }

        float dx = 0;
        float dy = 0;
        float dz = 0;
        switch (fap.getDirection())
        {
        case UP:
            dy = unit * value;
            break;
        case DOWN:
            dy = -unit * value;
            break;
        case LEFT:
            dx = unit * value;
            break;
        case RIGHT:
            dx = -unit * value;
            break;
        case FORWARD:
            dz = unit * value;
            break;
        }

        // We save the displacement.
        displacement = new float[] { dx, dy, dz };
    }

    float getDistance(float[] a, float[] b, float scalex, float scaley, float scalez)
    {
        float[] dst = new float[3];
        Vec3f.sub(dst, a, b);
        dst[0] = dst[0] / scalex;
        dst[1] = dst[1] / scaley;
        dst[2] = dst[2] / scalez;
        return Vec3f.length(dst);
    }

    float getWeight(int index)
    {
        return 0.0f;
    }

    public HashMap<Integer, Float> getVertexWeights()
    {
        HashMap<Integer, Float> weights = new HashMap<Integer, Float>();

        int numVertices = head.getNumVertices();
        for (int index = 0; index < numVertices; index++)
        {
            float alpha = getWeight(index);
            if (alpha > 0.0f)
                weights.put(index, alpha);
        }

        return weights;
    }

    /*
     * Persistence methods.
     */

    @Override
    public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt)
    {
        HashMap<String, Object> attrs = new HashMap<String, Object>();

        attrs.put("fap", fap.getNumber());
        attrs.put("size", size);
        attrs.put("x", fpNeutralPos[0]);
        attrs.put("y", fpNeutralPos[1]);
        attrs.put("z", fpNeutralPos[2]);
        appendEmptyTag(buf, fmt, "deformer", attrs);

        return buf;
    }

    @Override
    public void decodeContent(XMLTokenizer tokenizer) throws IOException
    {
        HashMap<String, String> attrMap = tokenizer.getAttributes();
        fap = MPEG4.getFAP(getRequiredIntAttribute("fap", attrMap, tokenizer));
        size = getRequiredFloatAttribute("size", attrMap, tokenizer);
        Float x = getRequiredFloatAttribute("x", attrMap, tokenizer);
        Float y = getRequiredFloatAttribute("y", attrMap, tokenizer);
        Float z = getRequiredFloatAttribute("z", attrMap, tokenizer);
        fpNeutralPos = new float[] { x, y, z };

        tokenizer.takeSTag("deformer");
        tokenizer.takeETag("deformer");
    }

    // @Deprecated
    // public void appendContent(StringBuilder buf)
    // {
    // buf.append(size);
    // buf.append('\t');
    // buf.append(fap.getNumber());
    // buf.append('\t');
    //
    // for (int i=0; i<fpNeutralPos.length; i++)
    // {
    // if (i > 0) buf.append(' ');
    // buf.append(fpNeutralPos[i]);
    // }
    // }
    //
    // @Deprecated
    // public void parseContent(String line)
    // {
    // String[] fields = line.split("\t");
    // size = Float.parseFloat(fields[8]);
    // fap = MPEG4.getFAP(Integer.parseInt(fields[9]));
    //
    // String[] subfields = fields[10].split(" ");
    // fpNeutralPos = new float[] {
    // Float.parseFloat(subfields[0]),
    // Float.parseFloat(subfields[1]),
    // Float.parseFloat(subfields[2]) };
    // }
}
