package hmi.facegraphics.deformers;

import hmi.faceanimation.model.MPEG4;
import hmi.xml.XMLFormatting;
import hmi.xml.XMLTokenizer;

import java.io.IOException;
import java.util.HashMap;

public class EaseDeformer extends Deformer implements EaseDeformerServer
{
    private float scalex, scaley, scalez;
    private int ease;
    private boolean useVertexMask, invertVertexMask;

    protected EaseDeformerClient client;

    public EaseDeformer()
    {
        ease = 0;
        scalex = 1.0f;
        scaley = 1.0f;
        scalez = 1.0f;
    }

    public EaseDeformer(SmoothDeformer smoothDeformer)
    {

    }

    public void updateEase(int ease)
    {
        this.ease = ease;
        head.calculateVertexWeights();
        deform();
    }

    private void setEase(int ease)
    {
        updateEase(ease);
        emitEase(ease);
    }

    public int getEase()
    {
        return ease;
    }

    public void updateScalex(float scale)
    {
        scalex = scale;
        head.calculateVertexWeights();
        deform();
    }

    private void setScalex(float scale)
    {
        updateScalex(scale);
        emitScalex(scale);
    }

    public float getScalex()
    {
        return scalex;
    }

    public void updateScaley(float scale)
    {
        scaley = scale;
        head.calculateVertexWeights();
        deform();
    }

    private void setScaley(float scale)
    {
        updateScaley(scale);
        emitScaley(scale);
    }

    public float getScaley()
    {
        return scaley;
    }

    public void updateScalez(float scale)
    {
        scalez = scale;
        head.calculateVertexWeights();
        deform();
    }

    private void setScalez(float scale)
    {
        updateScalez(scale);
        emitScalez(scale);
    }

    public float getScalez()
    {
        return scalez;
    }

    public void updateUseVM(boolean value)
    {
        this.useVertexMask = value;
        head.calculateVertexWeights();
        deform();
    }

    private void setUseVM(boolean value)
    {
        updateUseVM(value);
        emitUseVM(value);
    }

    public boolean getUseVM()
    {
        return useVertexMask;
    }

    public void updateInvertVM(boolean value)
    {
        this.invertVertexMask = value;
        head.calculateVertexWeights();
        deform();
    }

    private void setInvertVM(boolean value)
    {
        updateInvertVM(value);
        emitInvertVM(value);
    }

    public boolean getInvertVM()
    {
        return invertVertexMask;
    }

    @Override
    float getWeight(int index)
    {
        boolean inVM = head.getVertexMask(fap).contains(index);
        if (invertVertexMask)
            inVM = !inVM;
        if (useVertexMask && !inVM)
            return 0f;

        // Distance between neutral position of vertex and original feature point position.
        float[] npos = head.getNeutralVertexPos(index);
        float dist = getDistance(npos, fpNeutralPos, scalex, scaley, scalez);

        // Factor to which extend we should apply the displacement
        /*
         * float exp = 1.0f; if (ease > 0) exp = 1.0f + (ease / 20f); else if (ease < 0) exp = 1.0f - (ease / -100f); float alpha = 1.0f - (float)
         * Math.pow(dist / size, exp);
         */

        // Map ease (GUI-side) to ease (backend side): [-100:100] to [-1:1].
        float easeBack = ease / 100f;

        float alpha = EaseFalloff.getFalloff(easeBack, dist / size);

        return alpha;
    }

    public void copyFrom(Deformer sourceIn)
    {
        if (sourceIn.getClass() != EaseDeformer.class)
            return;
        EaseDeformer source = (EaseDeformer) sourceIn;
        setSize(source.size);
        setEase(source.ease);
        setScalex(source.scalex);
        setScaley(source.scaley);
        setScalez(source.scalez);
        setUseVM(source.useVertexMask);
        //setInvertVM(source.invertVertexMask);
    }

    private void emitEase(int ease)
    {
        if (client != null)
            client.updateEase(ease);
    }

    private void emitScalex(float scale)
    {
        if (client != null)
            client.updateScalex(scale);
    }

    private void emitScaley(float scale)
    {
        if (client != null)
            client.updateScaley(scale);
    }

    private void emitScalez(float scale)
    {
        if (client != null)
            client.updateScalez(scale);
    }

    private void emitUseVM(boolean value)
    {
        if (client != null)
            client.updateUseVM(value);
    }

    private void emitInvertVM(boolean value)
    {
        if (client != null)
            client.updateInvertVM(value);
    }

    /*
     * Persistence methods.
     */

    @Override
    public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt)
    {
        HashMap<String, String> attrs = new HashMap<String, String>();

        attrs.put("fap", Integer.toString(fap.getNumber()));
        attrs.put("size", Float.toString(size));
        attrs.put("x", Float.toString(fpNeutralPos[0]));
        attrs.put("y", Float.toString(fpNeutralPos[1]));
        attrs.put("z", Float.toString(fpNeutralPos[2]));
        attrs.put("scalex", Float.toString(scalex));
        attrs.put("scaley", Float.toString(scaley));
        attrs.put("scalez", Float.toString(scalez));
        attrs.put("ease", Integer.toString(ease));
        attrs.put("use-vertex-mask", Boolean.toString(useVertexMask));
        attrs.put("invert-vertex-mask", Boolean.toString(invertVertexMask));
        appendEmptyTag(buf, fmt, "ease-deformer", attrs);

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

        scalex = getRequiredFloatAttribute("scalex", attrMap, tokenizer);
        scaley = getRequiredFloatAttribute("scaley", attrMap, tokenizer);
        scalez = getRequiredFloatAttribute("scalez", attrMap, tokenizer);
        ease = getRequiredIntAttribute("ease", attrMap, tokenizer);
        useVertexMask = getRequiredBooleanAttribute("use-vertex-mask", attrMap, tokenizer);
        invertVertexMask = getRequiredBooleanAttribute("invert-vertex-mask", attrMap, tokenizer);

        tokenizer.takeSTag("ease-deformer");
        tokenizer.takeETag("ease-deformer");
    }
}
