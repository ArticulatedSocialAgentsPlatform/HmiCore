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
package hmi.graphics.collada.scenegraph;

import hmi.graphics.collada.Collada;
import hmi.graphics.collada.InstanceController;
import hmi.graphics.collada.InstanceGeometry;
import hmi.graphics.collada.Node;
import hmi.graphics.collada.Scene;
import hmi.graphics.collada.TransformNode;
import hmi.graphics.collada.VisualScene;
import hmi.graphics.scenegraph.GNode;
import hmi.graphics.scenegraph.GScene;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.regex.Pattern;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Translates a Collada scene graph into a GScene/GNode based scene graph.
 * 
 * @author Job Zwiers
 */
public final class ColladaTranslator
{
    private static Logger logger = LoggerFactory.getLogger(ColladaTranslator.class.getName());

    /***/
    private ColladaTranslator()
    {
    }

    // /**
    // * Translates a complete Collada document into a GScene. It contains the scene graph defined by the Collada scene element, possibly with multiple
    // * roots.
    // */
    // public static GScene colladaToGScene(Collada collada, float scale)
    // {
    // return colladaToGScene(collada, null, scale, true);
    // }
    //
    // public static GScene colladaToGScene(Collada collada, String renamingList, float scale)
    // {
    // return colladaToGScene(collada, renamingList, scale, true);
    // }

    public static GScene colladaToGSkinnedMeshScene(Collada collada)
    {
        return colladaToGSkinnedMeshScene(collada, true);
    }
    
    /**
     * Translates a complete Collada document into a GScene, supposed to define a GSkinnedMesh.
     * It contains the scene graph defined by the Collada scene element, possibly with multiple roots.
     * The renamingList String, if not null, specifies a joint renaming in the form of a number of String pairs: a regular jointname pattern,
     * followed by the replacement, to be used for both joint name and joint sid.
     */
    public static GScene colladaToGSkinnedMeshScene(Collada collada, boolean adjustBindPoses)
    {
        Scene scene = collada.getScene();
        if (scene == null)
        {
            throw new RuntimeException("ColladaTranslator.colladaToGScene: Collada document without scene.");
        }
        GScene gscene = colladaSceneToGScene(collada, scene);
        // gscene.analyzeMeshes();

        gscene.normalizeMeshes(); // ensure triangle meshes with unified indices
        gscene.collectSkinnedMeshes();

        gscene.resolveSkinnedMeshJoints();
        Map<String, String> renaming = new HashMap<String, String>();

        // gscene.adjustBindPoses();

        String upAxis = collada.getAsset().getUpAxis();
        if (upAxis.equals("X_UP"))
        {
            gscene.rotate(0f, 0f, 1f, (float) (Math.PI / 2.0)); // from X-up to Y-up
        }
        else if (upAxis.equals("Y_UP") || upAxis.equals(""))
        { // ok, do nothing
            // gscene.rotate(1f, 0f, 0f, (float)(-Math.PI/2.0)); // testing...
        }
        else if (upAxis.equals("Z_UP"))
        {
            gscene.rotate(1f, 0f, 0f, (float) (-Math.PI / 2.0)); // from Z-up to Y-up
        }
        else
        { // unknown up axis.
            logger.error("ColladaTranslator: Collada Asset with unknown or incorrect UP axis: " + upAxis);
        }

        float scale = collada.getAsset().getUnitMeter();
        if (scale != 1.0f) gscene.scale(scale);

        if(adjustBindPoses)
        {
            gscene.adjustBindPoses();
        }

        String renamingList = collada.getRenamingList();
        if (renamingList != null)
        {
            renaming = getColladaRenaming(renamingList, gscene.getRootNodes());
            gscene.renameJoints(renaming);
        }
        return gscene;
    }

    // public void writeBindMatrixRotations(Collada collada, String renamingList) {
    // Scene scene = collada.getScene();
    // if (scene == null) {
    // throw new RuntimeException("ColladaTranslator.writeBindMatrixRotations: Collada document without scene.");
    // }
    // GScene gscene = colladaSceneToGScene(collada, scene);
    // gscene.writeBindMatrixRotations(renamingList);
    //
    // }

    /**
     * Translates a Collada Scene into a GScene. The Collada argument refers to the getCollada() context for this scene.
     */
    public static GScene colladaSceneToGScene(Collada collada, Scene scene)
    {
        if (scene.getInstanceVisualScene() == null || scene.getInstanceVisualScene().getURL() == null)
        {
            throw new RuntimeException("Translator.colladaSceneToGNode: Collada document with null visual_scene.");
        }
        String url = scene.getInstanceVisualScene().getURL();

        VisualScene vscene = collada.getLibItem(collada.getLibrariesVisualScenes(), url);
        if (vscene == null)
        {
            throw new RuntimeException("ColladaTranslator.colladaSceneToGNode: visual_scene " + url + " could not be found.");
        }
        GScene gscene = new GScene(url);
        List<Node> rootNodes = vscene.getNodes(); // The top-level nodes from the visual_scene

        for (Node rootNode : rootNodes)
        {
            GNode gRootNode = colladaNodeToGNode(collada, rootNode);
            gscene.addRootNode(gRootNode);
        }
        return gscene;
    }

    /**
     * Translates a Collada Node tree recursively into an GNode tree.
     */
    private static GNode colladaNodeToGNode(Collada collada, Node node)
    {
        if (node == null) return null;
        GNode gnode = new GNode();
        String id = node.getId();
        String sid = node.getSid();
        String name = node.getName();
        if (id == null) id = name;
        gnode.setId(id);
        gnode.setSid(sid);
        gnode.setName(name);
        String type = node.getType();
        if (type != null) gnode.setType(type);

        List<TransformNode> transformList = node.getTransforms();
        TransformTranslator.setTransform(transformList, gnode); // also handles null or empty list

        List<InstanceGeometry> instanceGeometries = node.getInstanceGeometries();
        InstanceGeometryTranslator.addGShapes(collada, instanceGeometries, gnode);

        // deal with instance controllers...
        List<InstanceController> instanceControllers = node.getInstanceControllers();
        if (!instanceControllers.isEmpty())
        {
            // First check for instances of skin controllers. (These might contain instances of morph controllers *inside* the skin controller)
            List<InstanceController> instanceSkinControllers = new ArrayList<InstanceController>(instanceControllers.size());
            for (InstanceController ic : instanceControllers)
            {
                if (ic.getController().getSkin() != null) instanceSkinControllers.add(ic);
            }
            InstanceControllerTranslator.addInstanceSkinControllerGShapes(collada, instanceSkinControllers, gnode);

            // Next, check for instances of morph controllers that are not inside a skin controller.
            List<InstanceController> instanceMorphControllers = new ArrayList<InstanceController>(instanceControllers.size());
            for (InstanceController ic : instanceControllers)
            {
                if (ic.getController().getMorph() != null)
                {
                    instanceMorphControllers.add(ic);
                }
            }
            InstanceControllerTranslator.addInstanceMorphControllerGShapes(collada, instanceMorphControllers, gnode);
        }
        // finally, process child nodes recursively
        for (Node childNode : node.getNodes())
        {
            GNode gchild = colladaNodeToGNode(collada, childNode);
            gnode.addChildNode(gchild);
        }
        return gnode;
    }

    /**
     * Constructs a mapping that defines a joint renaming function for Collada skeleton structures. The specified renamingList String is assumed to be
     * an alternating sequence of old-name new-name Strings, separated by blank space characters. Here, &quot;old-name&quot; can be a fixed String
     * like &quot;Bip01_Bassin&quot;, but in fact it can be a Java-style regular pattern like &quot;.*L.*Clavicle$&quot; The latter would match names
     * like &quot;CWom0023-L_Clavicle&quot; The pattern is used to search for a scenegraph node with a matching name; The sid attribute of the found
     * node (or its name attribute if its sid is null) is the mapped to the &quot;new-name&quot; value, and as such, inserted as a pair into the
     * mapping. Some informal explanation/rationale: a Collada scenegraph node can have an id and/or sid and/or a name. For skeleton nodes, sid'd
     * should be used, but actually, Collada exporters like the 3DMax exporter use the name attribute for the more logical names like
     * &quot;Bip01_Bassin&quot; and use sid's like Bone21. We assume here that our renamingList uses the more logical names for patterns, rather than
     * sid's. Since other parts of a Collada scene specify skeleton joints by means of sids exclusively, we return a renaming mapping where we map
     * sid's, not names, to replacement strings.
     */
    public static Map<String, String> getColladaRenaming(String renamingList, List<GNode> rootNodes)
    {
        StringTokenizer tok = new StringTokenizer(renamingList);
        int numTokens = tok.countTokens();
        if (numTokens % 2 != 0)
        {
            throw new IllegalArgumentException("Collada translator: joint renaming list with odd number of Strings");
        }
        int numPairs = numTokens / 2;
        Pattern[] patterns = new Pattern[numPairs];
        String[] replacements = new String[numPairs];

        for (int i = 0; i < numPairs; i++)
        {
            patterns[i] = Pattern.compile(tok.nextToken());
            replacements[i] = tok.nextToken();
        }
        Map<String, String> renaming = new HashMap<String, String>();
        for (int i = 0; i < patterns.length; i++)
        {
            for (GNode rnode : rootNodes)
            {
                GNode nod = rnode.getPartByNamePattern(patterns[i]);
                if (nod != null)
                {
                    String sid = nod.getSid();
                    if (sid == null) sid = nod.getName(); // happens for Armandia for _PIVOT nodes.
                    if (sid != null)
                    {
                        // System.out.println("Skeletons.getRenaming: " + sid + " ==> " + replacements[i]);
                        renaming.put(sid, replacements[i]);
                    }
                    break;
                }
            }
        }
        return renaming;
    }

}
