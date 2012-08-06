/*******************************************************************************
 * Copyright (C) 2009 Human Media Interaction, University of Twente, the Netherlands
 * 
 * This file is part of the Elckerlyc BML realizer.
 * 
 * Elckerlyc is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Elckerlyc is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with Elckerlyc.  If not, see http://www.gnu.org/licenses/.
 ******************************************************************************/

package hmi.graphics.collada.scenegraph;

import hmi.graphics.collada.BindMaterial;
import hmi.graphics.collada.Collada;
import hmi.graphics.collada.Controller;
import hmi.graphics.collada.Geometry;
import hmi.graphics.collada.Input;
import hmi.graphics.collada.InstanceGeometry;
import hmi.graphics.collada.Mesh;
import hmi.graphics.collada.Morph;
import hmi.graphics.collada.PolyList;
import hmi.graphics.collada.Polygons;
import hmi.graphics.collada.PrimitiveMeshElement;
import hmi.graphics.collada.Skin;
import hmi.graphics.collada.Source;
import hmi.graphics.scenegraph.GMaterial;
import hmi.graphics.scenegraph.GMesh;
import hmi.graphics.scenegraph.GNode;
import hmi.graphics.scenegraph.GShape;
import hmi.graphics.scenegraph.GSkinnedMesh;
import hmi.graphics.scenegraph.VertexWeights;
import hmi.math.Mat4f;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * translates (lists of Collada Instance_) Geometry into GShape lists
 * 
 * @author Job Zwiers
 */
public final class InstanceGeometryTranslator
{

    private static Logger logger = LoggerFactory.getLogger(InstanceGeometryTranslator.class.getName());

    /***/
    private InstanceGeometryTranslator()
    {
    }

    // ORDINARY MESHES (i.e. not skinned)
    /**
     * Adds GShapes to the specified gnode, extracted from the specified instance_geometries. The specified getCollada() argument determines the
     * context, defining these geometries.
     */
    public static void addGShapes(Collada collada, List<InstanceGeometry> instanceGeometries, GNode gnode)
    {
        if (instanceGeometries != null)
        {
            for (InstanceGeometry igeom : instanceGeometries)
            {
                Geometry geom = igeom.getGeometry();
                if (geom == null)
                {
                    collada.warning("Collada InstanceGeometryTranslator: instance geometry: " + igeom.getURL() + " could not be found");
                }
                else
                {
                    BindMaterial bindMaterial = igeom.getBindMaterial();
                    // String geomName = geom.getIdOrName();
                    // Mesh mesh = geom.getMesh();
                    // TODO: take into account mesh sharing among instance_geometries
                    List<GShape> gshapeList = getGShapeList(collada, geom, bindMaterial);
                    gnode.addGShapes(gshapeList);
                }
            }
        } // else : add nothing to gnode
    }

    /* concatenate the Strings in the List, separated by - chars. */
    /*
     * private static String listToString(List<String> lst) { if (lst == null) return ""; StringBuilder buf = new StringBuilder(); for (String str:
     * lst) { buf.append('-'); buf.append(str); } return buf.toString(); }
     */

    private static final List<GShape> EMPTY_GSHAPE_LIST = new ArrayList<GShape>(0);

    /**
     * Returns a list of GShape objects, each containing a GMesh plus GMaterial, extracted from the specified Collada mesh and bind_material. The
     * getCollada() argument determines the context. This method is used for geometry not affected by controllers.
     */
    public static List<GShape> getGShapeList(Collada collada, Geometry geom, BindMaterial bindMaterial)
    {
        ArrayList<GShape> result = new ArrayList<GShape>();
        String geomName = geom.getIdOrName();
        Mesh mesh = geom.getMesh();
        List<PrimitiveMeshElement> primList = mesh.getPrimitiveMeshElements();

        ArrayList<GMesh> gmeshList = new ArrayList<GMesh>(primList.size());
        ArrayList<Input> verticesInputs = mesh.getVertices().getInputs();

        int counter = 0;
        for (PrimitiveMeshElement prim : primList)
        {

            MaterialTranslator.GMaterialPlusChannelBindings gmatBinding = MaterialTranslator.primitiveToGMaterial(collada, prim,
                    bindMaterial);
            GMaterial gmaterial = gmatBinding.getGMaterial();
            Map<String, String> texCoordBindings = gmatBinding.getBindings();

            GMesh gmesh = primitiveToGMesh(collada, prim, verticesInputs, null, null, geomName, texCoordBindings);
            gmeshList.add(gmesh);

            String shapeName = (counter == 0 || (primList.size() <= 1)) ? geomName : geomName + "-" + counter;
            counter++;
            gmesh.setId(shapeName);

            GShape gs = new GShape(gmesh, gmaterial, shapeName);
            result.add(gs);
        }
        return result;
    }

    // MORPHED MESHES
    /**
     * Returns a list of GShape objects, each containing a GMesh plus GMaterial, extracted from the specified Collada Morph. The getCollada()
     * argument determines the context.
     */
    public static List<GShape> getMorphedGShapeList(Collada collada, Morph morph, BindMaterial bindMaterial)
    {
        ArrayList<GShape> result = new ArrayList<GShape>();
        Geometry geom = morph.getBaseGeometry();
        String[] targetIds = morph.getMorphTargetIds();
        Geometry[] targetGeoms = morph.getTargetGeometries();
        String geomName = geom.getIdOrName();
        Mesh mesh = geom.getMesh();
        ArrayList<Input> verticesInputs = mesh.getVertices().getInputs(); // the vertices of the base mesh
        // the primitives like triangles, which are shared between base mesh and morph targets
        List<PrimitiveMeshElement> primList = mesh.getPrimitiveMeshElements();

        ArrayList<ArrayList<Input>> targetVerticesInputs = null; // optional List of vertices of the morph targets
        if (targetGeoms != null)
        {
            targetVerticesInputs = new ArrayList<ArrayList<Input>>(targetGeoms.length);
            for (int i = 0; i < targetGeoms.length; i++)
            {
                Mesh tgMesh = targetGeoms[i].getMesh();
                ArrayList<Input> tgVerticesInputs = tgMesh.getVertices().getInputs();
                targetVerticesInputs.add(tgVerticesInputs);
            }
        }

        // We allocate one GMesh for every primitive. Each GMesh will include its
        // morph data, if necessary.
        ArrayList<GMesh> gmeshList = new ArrayList<GMesh>(primList.size());
        int counter = 0;
        for (PrimitiveMeshElement prim : primList)
        {
            MaterialTranslator.GMaterialPlusChannelBindings gmatBinding = MaterialTranslator.primitiveToGMaterial(collada, prim,
                    bindMaterial);
            GMaterial gmaterial = gmatBinding.getGMaterial();
            // texCoordBindings: map Collada Input Strings like TEXCOORD1 to Strings like texCoord1.
            // Here TEXCOORD1 coorsponds to "semantic="TEXCOORD" set="1"
            // The texCoord1 String is the attribute name used in our scenegraph and OpenGL shaders
            Map<String, String> texCoordBindings = gmatBinding.getBindings();

            GMesh gmesh = primitiveToGMesh(collada, prim, verticesInputs, targetVerticesInputs, targetIds, geomName, texCoordBindings);

            gmeshList.add(gmesh);
            // skinnedMeshes.add(gmesh);

            // String shapeName = (primList.size() <= 1) ? geomName : geomName + "-" + counter;
            String shapeName = (counter == 0 || (primList.size() <= 1)) ? geomName : geomName + "-" + counter;
            counter++;
            gmesh.setId(shapeName);
            GShape gs = new GShape(gmesh, gmaterial, shapeName);
            result.add(gs);
        }
        return result;
    }

    // SKINNED MESHES
    /**
     * Returns a list of GShape objects, each containing a GSkinnedMesh plus GMaterial, extracted from the specified Collada Skin. The getCollada()
     * argument determines the context.
     */
    public static List<GShape> getSkinnedGShapeList(Collada collada, Skin skin, String[] skeletonIds, BindMaterial bindMaterial)
    {
        ArrayList<GShape> result = new ArrayList<GShape>();
        Geometry geom = skin.getGeometry(); // the geometry if it is non-null, else it will be set to the base geometry for morphed meshes below.
        Geometry[] targetGeoms = null; // only used for morphed meshes
        String[] targetIds = null;
        if (geom == null)
        { // then it should be a morphed mesh
            Controller morphController = skin.getController();
            if (morphController == null)
            {
                collada.warning("Collada Translator: skin geometry or morph controller: " + skin.getSource() + " could not be found");
                return EMPTY_GSHAPE_LIST;
            }
            Morph morph = morphController.getMorph();
            if (morph == null)
            {
                collada.warning("Collada Translator: Morph controller expected within Skin");
                return EMPTY_GSHAPE_LIST;
            }
            geom = morph.getBaseGeometry();
            targetIds = morph.getMorphTargetIds();
            targetGeoms = morph.getTargetGeometries();
        }
        // By now we have at least a Geometry geom, and optionally a (non-null) Geometry array targetGeoms.
        String geomName = geom.getIdOrName();
        Mesh mesh = geom.getMesh();
        ArrayList<Input> verticesInputs = mesh.getVertices().getInputs(); // the vertices of the mesh or, for morphed meshes, vertices of the base
                                                                          // mesh
        // the primitives like triangles, which are shared between base mesh
        // and morph targets
        List<PrimitiveMeshElement> primList = mesh.getPrimitiveMeshElements();

        ArrayList<ArrayList<Input>> targetVerticesInputs = null; // optional List of vertices of the morph targets
        if (targetGeoms != null)
        {
            // Mesh[] targetMeshes = new Mesh[targetGeoms.length];
            targetVerticesInputs = new ArrayList<ArrayList<Input>>(targetGeoms.length);
            for (int i = 0; i < targetGeoms.length; i++)
            {
                Mesh tgMesh = targetGeoms[i].getMesh();
                ArrayList<Input> tgVerticesInputs = tgMesh.getVertices().getInputs();
                targetVerticesInputs.add(tgVerticesInputs);
            }
        }

        // We allocate one GMesh for every primitive. Each GMesh will include its
        // morph data, if necessary.
        ArrayList<GMesh> gmeshList = new ArrayList<GMesh>(primList.size());

        int counter = 0;
        for (PrimitiveMeshElement prim : primList)
        {
            MaterialTranslator.GMaterialPlusChannelBindings gmatBinding = MaterialTranslator.primitiveToGMaterial(collada, prim,
                    bindMaterial);
            GMaterial gmaterial = gmatBinding.getGMaterial();
            // texCoordBindings: map Collada Input Strings like TEXCOORD1 to Strings like texCoord1.
            // Here TEXCOORD1 coorsponds to "semantic="TEXCOORD" set="1"
            // The texCoord1 String is the attribute name used in our scenegraph and OpenGL shaders
            Map<String, String> texCoordBindings = gmatBinding.getBindings();

            GSkinnedMesh gmesh = primitiveToGSkinnedMesh(collada, prim, verticesInputs, targetVerticesInputs, targetIds, skin, skeletonIds,
                    geomName, texCoordBindings);
            gmeshList.add(gmesh);

            // String shapeName = (primList.size() <= 1) ? geomName : geomName + "-" + counter;
            String shapeName = (counter == 0 || (primList.size() <= 1)) ? geomName : geomName + "-" + counter;
            counter++;
            gmesh.setId(shapeName);
            GShape gs = new GShape(gmesh, gmaterial, shapeName);
            result.add(gs);
        }
        return result;
    }

    /*
     * Converts a Collada Mesh type, like Triangles or Polylist, into a similar GMesg type
     */
    private static GMesh.MeshType convertType(Mesh.MeshType colladaType)
    {
        switch (colladaType)
        {
        case Triangles:
            return GMesh.MeshType.Triangles;
        case Trifans:
            return GMesh.MeshType.Trifans;
        case Tristrips:
            return GMesh.MeshType.Tristrips;
        case Polygons:
            return GMesh.MeshType.Polygons;
        case Polylist:
            return GMesh.MeshType.Polylist;
        case Lines:
            return GMesh.MeshType.Undefined;
        case Linestrips:
            return GMesh.MeshType.Undefined;
        default:
            return GMesh.MeshType.Undefined;
        }
    }

    /**
     * Returns a GMesh object, containing the data arrays and index arrays for a given Collada Primitive element, taking into account inputs like the
     * Position data from a Vertices element. These inputs are passed in by means of the vertexInputs parameter.
     */
    private static GMesh primitiveToGMesh(Collada collada, PrimitiveMeshElement prim, ArrayList<Input> verticesInputs,
            ArrayList<ArrayList<Input>> targetVerticesInputs, String[] targetIds, String geomName, Map<String, String> texCoordBindings)
    {
        // todo skeletonURLs translation
        prim.createIndexArrays(); // create separate indices, for each Collada "offset"
        GMesh gmesh = new GMesh();
        gmesh.setId(geomName); // temporary name, for debugging
        gmesh.setMeshType(convertType(prim.getMeshType()));
        if (targetIds != null) gmesh.setMorphTargets(targetIds); // announce morph target attributes, and set their names.
        ArrayList<Input> primInputs = prim.getInputs();
        for (Input inp : primInputs)
        {
            String semantic = inp.getSemantic();
            int offset = inp.getOffset();
            int[] indices = prim.getIndices(offset);
            if (semantic.equals("VERTEX"))
            {
                // ignore source attribute; simply assume that it refers to the vertexInputs parameter;
                for (Input vertexInput : verticesInputs)
                {
                    String vertexSemantic = vertexInput.getSemantic();
                    // int set = vertexInput.getSet();
                    String attributeName = translateAttributeName(vertexSemantic, -1, null);
                    if (attributeName == null)
                    {
                        collada.warning("InstanceGeometryTranslator.primitiveToGMesh: cannot handle " + vertexSemantic + " attribute");
                    }
                    else if (attributeName.equals("NotUsed"))
                    {
                        // collada.warning("InstanceGeometryTranslator.primitiveToGMesh: attribute" + vertexSemantic + " not used");
                        // return;
                    }
                    else
                    {
                        addGMeshData(-1, gmesh, vertexInput, indices, attributeName);
                        if (targetVerticesInputs != null)
                        {
                            int tgIndex = 0;
                            for (ArrayList<Input> tgVerticesInputs : targetVerticesInputs)
                            {
                                for (Input tgVertexInput : tgVerticesInputs)
                                {
                                    String tgVertexSemantic = tgVertexInput.getSemantic();
                                    if (tgVertexSemantic.equals(vertexSemantic)) addGMeshData(tgIndex, gmesh, tgVertexInput, indices,
                                            attributeName);
                                }
                                tgIndex++;
                            }
                        }
                    }
                }
            }
            else
            {
                int set = inp.getSet();
                if (set < 0) set = 0; // assume default set == 0
                String attributeName = translateAttributeName(semantic, set, texCoordBindings);
                if (attributeName == null)
                {
                    collada.warning("InstanceGeometryTranslator.primitiveToGMesh: cannot handle " + semantic + " attribute");
                }
                else if (attributeName.equals("NotUsed"))
                {
                    // collada.warning("InstanceGeometryTranslator.primitiveToGMesh: attribute" + semantic + " set:" + set + " not used");
                    // return;
                }
                else
                {
                    addGMeshData(-1, gmesh, inp, indices, attributeName);
                    // temporarily add morph target attributes for NON_VERTEX data as well:
                    if (targetVerticesInputs != null)
                    {
                        int tgIndex = 0;
                        for (ArrayList<Input> tgVerticesInputs : targetVerticesInputs)
                        {
                            for (Input tgVertexInput : tgVerticesInputs)
                            {
                                String tgVertexSemantic = tgVertexInput.getSemantic();
                                if (tgVertexSemantic.equals(semantic)) addGMeshData(tgIndex, gmesh, tgVertexInput, indices, attributeName);
                            }
                            tgIndex++;
                        }
                    }

                }
            }
        }
        if (prim instanceof PolyList)
        {
            gmesh.setVCountData(((PolyList) prim).getVCounts());
        }

        if (prim instanceof Polygons)
        {
            gmesh.setVCountData(((Polygons) prim).getVCounts());
        }
        return gmesh;
    }

    private static final float EPSMIN = 0.1f;
    private static final float EPSPLUS = 0.05f;

    /**
     * Returns a GMesh object, containing the data arrays and index arrays for a given Collada Primitive element, taking into account inputs like the
     * Position data from a Vertices element. These inputs are passed in by means of the vertexInputs parameter.
     */
    private static GSkinnedMesh primitiveToGSkinnedMesh(Collada collada, PrimitiveMeshElement prim, ArrayList<Input> verticesInputs,
            ArrayList<ArrayList<Input>> targetVerticesInputs, String[] targetIds, Skin skin, String[] skeletonIds, String geomName,
            Map<String, String> texCoordBindings)
    {
        GMesh gm = primitiveToGMesh(collada, prim, verticesInputs, targetVerticesInputs, targetIds, geomName, texCoordBindings);
        GSkinnedMesh gsm = new GSkinnedMesh(gm);
        float[] bindShapeMatrix = skin.getBindShapeMatrix();
        if (bindShapeMatrix != null)
        {
            gsm.affineTransform(bindShapeMatrix);
        }

        VertexWeights vertexWeights = createVertexWeights(skin); // createVertexWeights copies data, to avoid aliasing problems.
        vertexWeights.checkAccumulatedWeights(EPSMIN, EPSPLUS, true);
        gsm.setVertexWeights(vertexWeights);

        gsm.setSkeletonIds(skeletonIds);
        gsm.setJointSIDs(skin.getJointSIDs());
        // gsm.setJointNames(skin.getJointNames());

        float[] packedMatrices = skin.getInvBindMatrices();
        if (packedMatrices != null)
        {
            int nrOfMatrices = packedMatrices.length / Mat4f.MAT4F_SIZE;
            float[][] invBindMatrices = new float[nrOfMatrices][];
            for (int m = 0; m < nrOfMatrices; m++)
            {
                invBindMatrices[m] = Mat4f.getMat4f();
                for (int i = 0; i < Mat4f.MAT4F_SIZE; i++)
                {
                    invBindMatrices[m][i] = packedMatrices[Mat4f.MAT4F_SIZE * m + i];
                }
            }
            gsm.setInvBindMatrices(invBindMatrices);
        }
        // gsm.printJointInfo();
        return gsm;
    }

    /*
     * Creates a new VertexWeight by copying jointIndices, jointWeights, and count data
     */
    private static VertexWeights createVertexWeights(Skin skin)
    {
        int[] vcount = skin.getVCount();
        int[] vc = Arrays.copyOf(vcount, vcount.length);
        int[] jointIndices = skin.getJointIndices();
        int[] ji = Arrays.copyOf(jointIndices, jointIndices.length);
        float[] jointWeights = skin.getJointWeights();
        float[] jw = Arrays.copyOf(jointWeights, jointWeights.length);
        return new VertexWeights(vc, ji, jw);
    }

    private static Map<String, String> attributeNameTranslation = new HashMap<String, String>();
    static
    {
        attributeNameTranslation.put("POSITION", "mcPosition");
        attributeNameTranslation.put("NORMAL", "mcNormal");
        attributeNameTranslation.put("COLOR", "color");
        // attributeNameTranslation.put("TEXCOORD", "texCoord"); // texture channel will be added

    }

    private static String translateAttributeName(String semantic, int set, Map<String, String> texCoordBindings)
    {
        if (semantic.equals("TEXCOORD"))
        {
            String fullTexCoord = semantic + set;
            String texCoord = texCoordBindings.get(fullTexCoord);
            if (texCoord == null)
            {
                return "NotUsed";
            }
            else
            {
                return texCoord;
            }
        }
        else
        {
            return attributeNameTranslation.get(semantic);
        }
    }

    /*
     * adds data and index data to a GMesh object, based upon a given Input and specified index-offset
     */
    private static void addGMeshData(int morphTarget, GMesh gm, Input inp, int[] indices, String attributeName)
    {
        String sourceURL = inp.getSource();
        if (sourceURL == null)
        {
            inp.getCollada().warning("InstanceGeometryTranslator.addMeshData: null sourceURL for Input");
            return;
        }

        Source source = inp.getCollada().getSource(inp.urlToId(sourceURL));
        if (source == null)
        {
            inp.getCollada().warning("InstanceGeometryTranslator.addMeshData: null source for Source URL " + sourceURL);
            return;
        }

        if (attributeName.startsWith("texCoord"))
        {

            String[] texCoordFieldNames = new String[] { "s", "t" };
            gm.setIndexedVertexData(morphTarget, attributeName, texCoordFieldNames.length,
                    source.getHomogeneousFloatData(texCoordFieldNames), indices);
        }
        else
        {
            gm.setIndexedVertexData(morphTarget, attributeName, source.getNrOfNamedParams(), source.getHomogeneousFloatData(), indices);

        }
    }

}
