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

package hmi.graphics.collada.scenegraph;

import hmi.graphics.collada.BindMaterial;
import hmi.graphics.collada.BindVertexInput;
import hmi.graphics.collada.Collada;
import hmi.graphics.collada.ColladaImage;
import hmi.graphics.collada.CommonTexture;
import hmi.graphics.collada.Effect;
import hmi.graphics.collada.FixedFunctionShader;
import hmi.graphics.collada.InstanceMaterial;
import hmi.graphics.collada.Material;
import hmi.graphics.collada.MayaProfile;
import hmi.graphics.collada.Newparam;
import hmi.graphics.collada.ParamValue;
import hmi.graphics.collada.PrimitiveMeshElement;
import hmi.graphics.collada.ProfileCOMMON;
import hmi.graphics.collada.Sampler2D;
import hmi.graphics.collada.Setparam;
import hmi.graphics.collada.Surface;
import hmi.graphics.collada.TechniqueFX;
import hmi.graphics.scenegraph.GMaterial;
import hmi.graphics.scenegraph.GTexture;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Translates Collada materials and effects
 * 
 * @author Job Zwiers
 */
public final class MaterialTranslator
{
    private static Logger logger = LoggerFactory.getLogger(MaterialTranslator.class.getName());

    /***/
    private MaterialTranslator()
    {
    }

    public static class GMaterialPlusChannelBindings
    {
        public GMaterialPlusChannelBindings(GMaterial gmaterial, Map<String, String> binding)
        {
            this.gmaterial = gmaterial;
            this.binding = binding;
        }

        public GMaterial getGMaterial()
        {
            return gmaterial;
        }

        public Map<String, String> getBindings()
        {
            return binding;
        }

        private GMaterial gmaterial;
        // binding: mappings like "diffuse" to "CHANNEL1" "opacitymap" to "CHANNEL2" "bumpmap" to "CHANNEL3", derived from an material/effect
        private Map<String, String> binding;

    }

    /**
     * Returns a GMaterial object for the specified material id, combined with channel bindings
     */
    public static GMaterialPlusChannelBindings primitiveToGMaterial(Collada collada, PrimitiveMeshElement prim, BindMaterial bindMaterial)
    {
        String materialId = prim.getMaterialId();
        if (materialId == null)
        {
            collada.warning("Collada Translator: mesh without material");
            return null;
        }
        else
        {
            InstanceMaterial im = null;

            if (bindMaterial != null)
            {
                //hmi.util.Console.println("MaterialTranslator.GMaterialPlusChannelBindings, materialId=" + materialId);
                im = bindMaterial.getInstanceMaterial(materialId);
                //hmi.util.Console.println( im==null, "null bindMaterial", "bindMaterial OK");
                String remappedId = im.getTarget();
                if (remappedId != null) materialId = remappedId;
            }
            Material material = collada.getLibItem(collada.getLibrariesMaterials(), materialId);
            GMaterialPlusChannelBindings gmaterialplusbindings = MaterialTranslator.materialToGMaterial(collada, material, im);
            return gmaterialplusbindings;
        }
    }

    public static Map<String, ParamValue> getSetparamMap(List<Setparam> params)
    {
        if (params == null) return null;
        HashMap<String, ParamValue> map = new HashMap<String, ParamValue>();
        for (Setparam p : params)
        {
            String ref = p.getRef();
            ParamValue val = p.getParamValue();
            if (ref != null && val != null) map.put(ref, val);
        }
        return map;
    }

    public static Map<String, ParamValue> addNewparams(Map<String, ParamValue> map, List<Newparam> params)
    {
        if (params == null) return map;
        if (map == null) map = new HashMap<String, ParamValue>();
        for (Newparam p : params)
        {
            String sid = p.getSid();
            ParamValue val = p.getParamValue();
            if (sid != null && val != null) map.put(sid, val);
        }
        return map;
    }

    public static Map<String, ParamValue> applySetparams(Map<String, ParamValue> paramMap, List<Setparam> setParams)
    {
        if (setParams == null || paramMap == null) return paramMap;
        for (Setparam p : setParams)
        {
            String ref = p.getRef();
            ParamValue val = p.getParamValue();
            if (ref != null && val != null)
            { // check whether the setparam defines a ParamValue
                ParamValue initialVal = paramMap.get(ref);
                if (initialVal != null)
                { // check whether there actually is a corresponding newparam defined
                    if (initialVal.getType() == val.getType())
                    { // check for consistency of base type
                      // cast if sizes do not match ...?
                        if (val.getType() != ParamValue.Type.ValueType)
                        {
                            paramMap.put(ref, val);
                        }
                    }
                }
            }
        }
        return paramMap;
    }

    public static GMaterialPlusChannelBindings materialToGMaterial(Collada collada, Material material, InstanceMaterial im)
    {
        GMaterial gmaterial = new GMaterial();
        gmaterial.setName(material.getId());
        Effect effect = material.getEffect(); // really the effect from the instance_effect
        if (effect == null) logger.info("MaterialTranslator.materialToGMaterial for Material " + material.getId() + " : null effect");
        List<Setparam> setParams = material.getSetparamList(); // really the setparams from the instance_effect

        List<Newparam> effectNewparamList = effect.getNewparamList();
        Map<String, ParamValue> paramMap = new HashMap<String, ParamValue>(); // the cumulative map combining newparams and setparams
        addNewparams(paramMap, effectNewparamList);

        List<ProfileCOMMON> profileCommonList = effect.getProfileCOMMONList();

        Map<String, String> semanticsToChannelbindings = null;

        for (ProfileCOMMON profile : profileCommonList)
        {
            Map<String, ParamValue> profileParamMap = (paramMap == null) ? null : new HashMap<String, ParamValue>(paramMap);

            List<Newparam> profileNewparamList = profile.getNewparamList(); // could be null
            addNewparams(profileParamMap, profileNewparamList); // could still return null
            // profile.getImageList();
            TechniqueFX technique = profile.getTechniqueFX();
            Map<String, ParamValue> techniqueParamMap = (profileParamMap == null) ? null : new HashMap<String, ParamValue>(profileParamMap);
            List<Newparam> techniqueNewparamList = technique.getNewparamList();
            addNewparams(techniqueParamMap, techniqueNewparamList); // could still return null
            applySetparams(techniqueParamMap, setParams); // even this could return null
            FixedFunctionShader shader = technique.getShader(); // could be Blinn, Lambert, etc, but all of these are all FixedFunction shaders
            semanticsToChannelbindings = fixedFunctionShaderSetting(collada, shader, techniqueParamMap, im, gmaterial);
        }

        GMaterialPlusChannelBindings result = new GMaterialPlusChannelBindings(gmaterial, semanticsToChannelbindings);

        // ................ process other profiles etcetera
        return result;
    }

    private static final Map<String, String> WRAPTYPE_CONVERSION = new HashMap<String, String>();

    static
    { // conversion as suggested in the Collada specification (fx_sampler_wrap_common Type)
        WRAPTYPE_CONVERSION.put("WRAP", "REPEAT");
        WRAPTYPE_CONVERSION.put("MIRROR", "MIRRORED_REPEAT");
        WRAPTYPE_CONVERSION.put("WRAP", "REPEAT");
        WRAPTYPE_CONVERSION.put("CLAMP", "CLAMP_TO_EDGE");
        WRAPTYPE_CONVERSION.put("BORDER", "GL_CLAMP_TO_BORDER");
        WRAPTYPE_CONVERSION.put("NONE", "GL_CLAMP_TO_BORDER");
    }

    /**
     * Converts a Collada texture wrap type to an OpenGL style wrap type.
     */
    public static String colladaWrapTypeToGTextureWrapType(String colladaWrapType)
    {
        String result = WRAPTYPE_CONVERSION.get(colladaWrapType);
        return (result == null) ? "REPEAT" : result;
    }

    private static String translateChannelToTEXCOORD(String channel, InstanceMaterial im)
    {
        ArrayList<BindVertexInput> bindVertexInputList = im.getBindVertexInputList();
        for (BindVertexInput bvi : bindVertexInputList)
        {
            if (bvi.getSemantic().equals(channel))
            {
                String inpSem = bvi.getInputSemantic(); // will be "TEXCOORD"
                int inpSet = bvi.getInputSet();
                return inpSem + inpSet;
            }
        }
        // should not happen
        logger.error("translateChannel " + channel + ": could not find matching <bind_vertex_input> ");
        return "TEXCOORD0"; // fallback

    }

    /**
     * create gmaterial settings from the specified shader, using possible parameter setting. Store the result in gmaterial.
     */
    private static Map<String, String> fixedFunctionShaderSetting(Collada collada, FixedFunctionShader shader,
            Map<String, ParamValue> paramMap, InstanceMaterial im, GMaterial gmaterial)
    {
        String matName = gmaterial.getName(); // for debugging/error messages
        Map<String, String> bindings = new HashMap<String, String>(); // bindings from effect textures to CHANNELS
        // shader type is Phong, Lambert, Blinn or Constant
        FixedFunctionShader.ShaderType shaderType = shader.getShaderType();

        float[] emissionColor = shader.getEmissionColor(paramMap);
        if (emissionColor != null) gmaterial.setEmissionColor(emissionColor);

        float[] ambientColor = shader.getAmbientColor(paramMap);
        if (ambientColor != null) gmaterial.setAmbientColor(ambientColor);

        float[] diffuseColor = shader.getDiffuseColor(paramMap);
        if (diffuseColor != null) gmaterial.setDiffuseColor(diffuseColor);

        String[] texCoordChannels = new String[3]; // diffuse, transparency, bumpmap: Collada texCoord/channel name

        CommonTexture diffuseTexture = shader.getDiffuseTexture();
        if (diffuseTexture != null)
        {
            String diffuseChannel = diffuseTexture.getTexCoord();
            texCoordChannels[0] = diffuseChannel;
            GTexture gtex = getGTexture(collada, diffuseTexture, paramMap);
            gmaterial.setDiffuseTexture(gtex);
            // Now we must collect possible non-standard settins for repeatX/offsetX values, stored inside the GTextures, and extrcted from the Maya
            // profile
            // We will duplicate these settings in the GMaterial, guessing/checking that all active textures will agree on these settings
            String errmsg = gmaterial.transferRepeatSettings(gtex); // first texture, no verify
            if (errmsg != null) collada.warning(errmsg);

        }

        float[] specularColor = shader.getSpecularColor(paramMap);
        if (specularColor != null)
        {
            gmaterial.setSpecularColor(specularColor);
            gmaterial.setShininess(shader.getShininess());
        }

        gmaterial.setTransparencyEnabled(shader.isTransparencyEnabled(paramMap));
        if (gmaterial.isTransparencyEnabled())
        {
            CommonTexture transparentTexture = shader.getTransparentTexture();
            if (transparentTexture != null)
            {
                String transparentChannel = transparentTexture.getTexCoord();
                texCoordChannels[1] = transparentChannel;
                GTexture gtex = getGTexture(collada, transparentTexture, paramMap);
                gmaterial.setTransparentTexture(gtex);
                String errmsg = gmaterial.transferRepeatSettings(gtex); // first texture, no verify
                if (errmsg != null) collada.warning(errmsg);

            }

            float[] transCol = shader.getTransparentColor(paramMap);
            if (transCol != null)
            {
                gmaterial.setTransparentColor(transCol);
            }

            String opaqueMode = shader.getOpaqueMode();
            gmaterial.setOpaqueMode(opaqueMode);
        }

        gmaterial.setShader(convertShaderType(shaderType, gmaterial.isTransparencyEnabled(), texCoordChannels, bindings, im, matName));
        return bindings;
    }

    /* creates a new GTexture from a CommonTexture */
    private static GTexture getGTexture(Collada collada, CommonTexture texture, Map<String, ParamValue> paramMap)
    {
        GTexture gtex = new GTexture();

        String s2dref = texture.getSampler2D();
        ParamValue s2dval = paramMap.get(s2dref);
        if (s2dval == null)
        {
            collada.warning("Texture with undefines Sampler2D reference:" + s2dref);
        }
        else
        {
            if (s2dval.getType() != ParamValue.Type.Sampler2D)
            {
                collada.warning("Texture with incorrect type (Sampler2D expected) :" + s2dval.getType());
            }
            else
            {
                Sampler2D s2d = s2dval.getSampler2D();

                gtex.setWrapS(colladaWrapTypeToGTextureWrapType(s2d.getWrapS()));
                gtex.setWrapT(colladaWrapTypeToGTextureWrapType(s2d.getWrapT()));
                gtex.setWrapR(colladaWrapTypeToGTextureWrapType(s2d.getWrapP())); // NB Collada: P, OpenGL: R

                MayaProfile mayaProfile = texture.getMayaProfile();

                if (mayaProfile != null)
                {
                    // this will store settings for repeatX/offsetX into the GTexture( extracted from the Maya profile)
                    // above, we will store it also into the GMaterial, where we guess that all material textures will agree on these settings
                    setMayaProfileSettings(mayaProfile, gtex);
                }

                String surfaceSid = s2d.getSurfaceSid();
                ParamValue surfaceval = paramMap.get(surfaceSid);
                if (surfaceval == null)
                {
                    collada.warning("Texture with undefined surface:" + surfaceSid);
                }
                else
                {
                    if (surfaceval.getType() != ParamValue.Type.Surface)
                    {
                        collada.warning("Texture with incorrect sampler type (Surface expected):" + surfaceval.getType());
                    }
                    else
                    {
                        Surface surface = surfaceval.getSurface();
                        String imageId = surface.getInitFrom().getImageId();
                        if (imageId == null)
                        {
                            collada.warning("Texture with undefined image");
                        }
                        else
                        {
                            ColladaImage img = collada.getLibItem(collada.getLibrariesImages(), imageId);
                            if (img == null)
                            {
                                collada.warning("Texture with undefined image: " + imageId);
                            }
                            else
                            {
                                String imageFile = img.getInitFrom();
                                gtex.setImageFileName(imageFile);
                            }
                        }
                    }
                }
            }
        }
        return gtex;
    }

    /* extracts settins like repeatU from mayaProfile, and sets corresponding values for gtex */
    private static void setMayaProfileSettings(MayaProfile mayaProfile, GTexture gtex)
    {
        gtex.setRepeatS(mayaProfile.getRepeatU());
        gtex.setOffsetS(mayaProfile.getOffsetU());

        gtex.setRepeatT(mayaProfile.getRepeatV());
        gtex.setOffsetT(mayaProfile.getOffsetV());
    }

    /* translate Collada shaders/attribute usage to HmiGraphics shader types */
    private static String convertShaderType(FixedFunctionShader.ShaderType type, boolean transparencyEnabled, String[] texCoordChannels,
            Map<String, String> bindings, InstanceMaterial im, String matName)
    {
        int texCoordChannelCount = 0;
        String[] texCOORD = new String[texCoordChannels.length];
        for (int i = 0; i < texCoordChannels.length; i++)
        {
            if (texCoordChannels[i] != null)
            {
                // translate Collada effect "channels" to Collada Input attribute/set combinations
                // for instance, if texCoordChannel[0] == "CHANNEL1", then according to bind_vertex_inputs we might
                // have a corresponding geometry input like:
                // < input semantic="TEXCOORD" .... set="1"/>
                // We refer to this Input elemnt by TEXCOORD1, which would here be the value of texCOORD[0]
                texCOORD[i] = translateChannelToTEXCOORD(texCoordChannels[i], im);
                texCoordChannelCount++;
            }
        }
        String prefix = "blinn"; // fallback for lambert, phong etc
        if (type.equals(FixedFunctionShader.ShaderType.Blinn))
        {
            prefix = "blinn";
        } else if (type.equals(FixedFunctionShader.ShaderType.Eye)) {
            prefix = "blinnEye";
        }
        String postfix = "Basic";
        if (!transparencyEnabled)
        {
            if (texCoordChannelCount == 0)
            {
                postfix = "Basic";
            }
            else if (texCoordChannelCount == 1)
            {
                if (texCoordChannels[0] != null)
                {
                    postfix = "Textured1";
                    bindings.put(texCOORD[0], "texCoord1");
                }
                else
                {
                    logger.error("MaterialTranslator for " + matName + ", "
                            + "cannot convert shader (not transparent, one channel, texCoordChannels[0]==null) ");
                }
            }
            else
            { // texCoordChannelCount > 1, not supported
                logger.error("MaterialTranslator for " + matName + ", cannot convert shader (not transparent, more than one channel) ");
            }

        }
        else
        { // transparency enabled ..
            if (texCoordChannelCount == 0)
            {
                postfix = "DiffuseColorTransparencyColor";
            }
            else if (texCoordChannelCount == 1)
            {
                if (texCoordChannels[0] != null)
                { // a diffuse texture, transparency color (no transparency texture)
                    postfix = "DiffuseTextureTransparencyColor";
                    bindings.put(texCOORD[0], "texCoord1");
                }
                else if (texCoordChannels[1] != null)
                { // just a transparency texture, no diffuse texture
                    postfix = "Transparency1";
                    bindings.put(texCOORD[1], "texCoord1");
                }
                else
                {
                    logger.error("MaterialTranslator for " + matName
                            + ", cannot convert shader (transparent, one channel, texCoordChannels[1]==null) ");
                }
            }
            else if (texCoordChannelCount == 2)
            {
                if (texCoordChannels[0].equals(texCoordChannels[1]))
                { // diffuse/transparency shader, with only one set of TexCoords:
                    postfix = "DiffuseTransparency1";
                    bindings.put(texCOORD[0], "texCoord1");
                }
                else
                { // diffuse/transparency shader, with separate texCoords for diffuse and transpraency
                    postfix = "DiffuseTransparency2";
                    bindings.put(texCOORD[0], "texCoord1"); // diffuse
                    bindings.put(texCOORD[1], "texCoord2"); // transparency
                }
            }
            else
            { // texCoordChannelCount > 2, not supported
                logger.error("MaterialTranslator for " + matName + ", cannot convert shader (transparent, more than two channels) ");
            }

        }
        if (debugShader)
        {
            return "debug";
        }
        else
        {
            return prefix + postfix;
        }

    }

    /**
     * Whne set to true, all shaders are replaced by the "debug" shader
     */
    public static void setDebugShader(boolean debug)
    {
        debugShader = debug;
    }

    private static boolean debugShader = false;

}
