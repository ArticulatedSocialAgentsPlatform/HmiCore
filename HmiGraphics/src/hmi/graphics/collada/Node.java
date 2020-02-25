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
package hmi.graphics.collada;

import hmi.xml.XMLFormatting;
import hmi.xml.XMLTokenizer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Node in a hierarchical tree of Collada scene graph elements.
 * 
 * @author Job Zwiers
 */
public class Node extends ColladaElement
{

    // attributes: (id, sid, name inherited from ColladaElement)
    private String type; // "NODE" or "JOINT" (optional)
    private String layers; // optional list of layer names

    // child elements:
    private Asset asset;
    // possible transform elements: lookat, matrix, rotate, scale, skew, translate
    private List<TransformNode> transforms = new ArrayList<TransformNode>(LISTSIZE);
    private List<InstanceGeometry> instanceGeometries = null;
    private List<InstanceCamera> instanceCameras = null;
    private List<InstanceController> instanceControllers = null;
    private List<InstanceLight> instanceLights = null;
    private List<InstanceNode> instanceNodes = null;

    private List<Node> nodes = new ArrayList<Node>();
    private List<Extra> extras = new ArrayList<Extra>();

    public Node()
    {
        super();
    }

    public Node(Collada collada, XMLTokenizer tokenizer) throws IOException
    {
        super(collada);
        readXML(tokenizer);
    }

    public String getType()
    {
        return type;
    }

    public String getLayers()
    {
        return layers;
    }

    public Asset getAsset()
    {
        return asset;
    }

    public List<TransformNode> getTransforms()
    {
        return transforms;
    }

    public List<InstanceGeometry> getInstanceGeometries()
    {
        return ((instanceGeometries == null) ? emptyInstanceGeometries : instanceGeometries);
    }

    public List<InstanceCamera> getInstanceCameras()
    {
        return ((instanceCameras == null) ? emptyInstanceCameras : instanceCameras);
    }

    public List<InstanceController> getInstanceControllers()
    {
        return ((instanceControllers == null) ? emptyInstanceControllers : instanceControllers);
    }

    public List<InstanceLight> getInstanceLights()
    {
        return ((instanceLights == null) ? emptyInstanceLights : instanceLights);
    }

    public List<InstanceNode> getInstanceNodes()
    {
        return ((instanceNodes == null) ? emptyInstanceNodes : instanceNodes);
    }

    public List<Node> getNodes()
    {
        return nodes;
    }

    public List<Extra> getExtras()
    {
        return extras;
    }

    private static List<InstanceGeometry> emptyInstanceGeometries = Collections.emptyList();
    private static List<InstanceCamera> emptyInstanceCameras = Collections.emptyList();
    private static List<InstanceController> emptyInstanceControllers = Collections.emptyList();
    private static List<InstanceLight> emptyInstanceLights = Collections.emptyList();
    private static List<InstanceNode> emptyInstanceNodes = Collections.emptyList();

    /**
     * appends a String of attributes to buf.
     */
    @Override
    public StringBuilder appendAttributes(StringBuilder buf)
    {
        super.appendAttributes(buf);
        appendAttribute(buf, "type", type);
        appendAttribute(buf, "layers", layers);
        return buf;
    }

    /**
     * decodes the XML attributes
     */
    @Override
    public void decodeAttributes(HashMap<String, String> attrMap, XMLTokenizer tokenizer)
    {
        type = getOptionalAttribute("type", attrMap);
        layers = getOptionalAttribute("layer", attrMap);
        super.decodeAttributes(attrMap, tokenizer);
    }

    @Override
    public StringBuilder appendContent(StringBuilder buf, XMLFormatting fmt)
    {
        appendXMLStructure(buf, fmt, asset);
        appendXMLStructureList(buf, fmt, transforms);
        appendXMLStructureList(buf, fmt, instanceCameras);
        appendXMLStructureList(buf, fmt, instanceControllers);
        appendXMLStructureList(buf, fmt, instanceGeometries);
        appendXMLStructureList(buf, fmt, instanceLights);
        appendXMLStructureList(buf, fmt, instanceNodes);
        appendXMLStructureList(buf, fmt, nodes);
        appendXMLStructureList(buf, fmt, extras);
        return buf;
    }

    private static final int LISTSIZE = 4;

    @Override
    public void decodeContent(XMLTokenizer tokenizer) throws IOException
    {
        while (tokenizer.atSTag())
        {
            String tag = tokenizer.getTagName();
            if (tag.equals(Asset.xmlTag()))
            {
                asset = new Asset(getCollada(), tokenizer);
            }
            else if (tag.equals(Translate.xmlTag()))
            {
                transforms.add(new Translate(getCollada(), tokenizer));
            }
            else if (tag.equals(Rotate.xmlTag()))
            {
                transforms.add(new Rotate(getCollada(), tokenizer));
            }
            else if (tag.equals(Scale.xmlTag()))
            {
                transforms.add(new Scale(getCollada(), tokenizer));
            }
            else if (tag.equals(Skew.xmlTag()))
            {
                transforms.add(new Skew(getCollada(), tokenizer));
            }
            else if (tag.equals(Matrix.xmlTag()))
            {
                transforms.add(new Matrix(getCollada(), tokenizer));
            }
            else if (tag.equals(LookAt.xmlTag()))
            {
                transforms.add(new LookAt(getCollada(), tokenizer));
            }
            else if (tag.equals(InstanceCamera.xmlTag()))
            {
                if (instanceCameras == null)
                    instanceCameras = new ArrayList<InstanceCamera>(LISTSIZE);
                instanceCameras.add(new InstanceCamera(getCollada(), tokenizer));
            }
            else if (tag.equals(InstanceController.xmlTag()))
            {
                if (instanceControllers == null)
                    instanceControllers = new ArrayList<InstanceController>(LISTSIZE);
                instanceControllers.add(new InstanceController(getCollada(), tokenizer));
            }
            else if (tag.equals(InstanceGeometry.xmlTag()))
            {
                if (instanceGeometries == null)
                    instanceGeometries = new ArrayList<InstanceGeometry>(LISTSIZE);
                instanceGeometries.add(new InstanceGeometry(getCollada(), tokenizer));
            }
            else if (tag.equals(InstanceLight.xmlTag()))
            {
                if (instanceLights == null)
                    instanceLights = new ArrayList<InstanceLight>(LISTSIZE);
                instanceLights.add(new InstanceLight(getCollada(), tokenizer));
            }
            else if (tag.equals(InstanceNode.xmlTag()))
            {
                if (instanceNodes == null)
                    instanceNodes = new ArrayList<InstanceNode>(LISTSIZE);
                instanceNodes.add(new InstanceNode(getCollada(), tokenizer));
            }
            else if (tag.equals(Node.xmlTag()))
            {
                nodes.add(new Node(getCollada(), tokenizer));
            }
            else if (tag.equals(Extra.xmlTag()))
            {
                extras.add(new Extra(getCollada(), tokenizer));
            }
            else
            {
                getCollada().warning(tokenizer.getErrorMessage("Node: skip : " + tokenizer.getTagName()));
                tokenizer.skipTag();
            }
        }
        addColladaNode(asset);
        addColladaNodes(transforms);
        addColladaNodes(instanceCameras);
        addColladaNodes(instanceControllers);
        addColladaNodes(instanceGeometries);
        addColladaNodes(instanceLights);
        addColladaNodes(instanceNodes);
        addColladaNodes(nodes);
        addColladaNodes(extras);
    }

    /*
     * The XML Stag for XML encoding
     */
    private static final String XMLTAG = "node";

    /**
     * The XML Stag for XML encoding
     */
    public static String xmlTag()
    {
        return XMLTAG;
    }

    /**
     * returns the XML Stag for XML encoding
     */
    @Override
    public String getXMLTag()
    {
        return XMLTAG;
    }

}
