/**
 * \file LipMuscle.java
 * \author N.A.Nijdam
 */
package hmi.animation;

import hmi.graphics.render.BoundingBox;
import hmi.graphics.render.GenericMesh;
import hmi.math.BSpline2f;
import java.util.ArrayList;

/**
 * \brief the lip muscle is a BSpline used to adjust surrounding vertices
 */
public class LipMuscle extends Muscle
{

    /*
     * Params: "LipControlPoint1X", "LipControlPoint1Y", "LipControlPoint2X", "LipControlPoint2Y",
     * 
     * count 4
     */
    final private BSpline2f bSplineBase;
    final private BSpline2f bSpline;
    private float basePosition[];
    private float intensity[];
    private boolean negative;
    private ArrayList<Integer> lipFilterList = new ArrayList<Integer>();
    private BoundingBox boundingBox = new BoundingBox();
    private MouthMuscle mouth;

    /**
     * \brief the default constructor \param avatar the avatar \param name the name
     */
    public LipMuscle(String name, GenericMesh genericMesh)
    {
        super(name, genericMesh);
        this.bSpline = new BSpline2f();
        this.bSplineBase = new BSpline2f();
        paramSize = 4;
    }

    /**
     * \brief calculate the vertex indices for a given mesh \param mesh the mesh to be used
     */
    @Override
    public void calculateVertexIndices()
    {
        indices.clear();
        // this.indicesFiltered.clear();
        this.lipFilterList.clear();
        indices.addAll(this.boundingBox.extractVertexIndices(vertices));
        if (!this.indicesFiltered.isEmpty())
        {
            indices.removeAll(this.indicesFiltered);
        }

        float offset = 0;
        int index = 0;
        float width = 1 / (mouth.getLeftX() - mouth.getRightX());
        ArrayList<Float> basePositionList = new ArrayList<Float>();
        ArrayList<Float> intensityList = new ArrayList<Float>();
        float coord[] = new float[2];

        if (!negative)
        {
            for (int i : this.indices)
            {
                index = i * 3;
                offset = (mouth.getLeftX() - vertices[index]) * width;
                if (offset > 0.0f && offset < 1.0f)
                {
                    bSplineBase.getSplineCoord(offset, coord);
                    if (coord[1] < vertices[index + 1])
                    {
                        basePositionList.add(coord[0]);
                        basePositionList.add(coord[1]);
                        intensityList.add((this.boundingBox.getMaxVertex()[1] - vertices[index + 1])
                                / (this.boundingBox.getMaxVertex()[1] - bSpline.getPoint1Y()));

                    }
                    else
                    {
                        lipFilterList.add(i);
                    }
                }
                else
                {
                    lipFilterList.add(i);
                }
            }
        }
        else
        {
            for (int i : this.indices)
            {
                index = i * 3;
                offset = (mouth.getLeftX() - vertices[index]) * width;
                if (offset > 0.0f && offset < 1.0f)
                {
                    bSplineBase.getSplineCoord(offset, coord);
                    if (coord[1] > vertices[index + 1])
                    {
                        basePositionList.add(coord[0]);
                        basePositionList.add(coord[1]);
                        intensityList.add((vertices[index + 1] - this.boundingBox.getMinVertex()[1])
                                / (bSpline.getPoint1Y() - this.boundingBox.getMinVertex()[1]));
                    }
                    else
                    {
                        lipFilterList.add(i);
                    }
                }
                else
                {
                    lipFilterList.add(i);
                }
            }
        }
        basePosition = new float[basePositionList.size()];
        this.intensity = new float[intensityList.size()];
        for (int i = 0; i < basePositionList.size(); i++)
        {
            basePosition[i] = basePositionList.get(i);
        }
        for (int i = 0; i < intensityList.size(); i++)
        {
            intensity[i] = intensityList.get(i);
        }

        if (!lipFilterList.isEmpty())
        {
            indices.removeAll(lipFilterList);
            for (int i : lipFilterList)
            {
                if (!indicesFiltered.contains(i))
                {
                    indicesFiltered.add(i);
                }
            }
        }
    }

    /**
     * \brief initialize the lip muscle Set the BSpline
     */
    @Override
    public void init()
    {
        if (mouth == null)
        {
            System.out.println("Unable init lipmuscle without mouth parent ");
            return;
        }
        if (muscleSetSettings == null)
        {
            System.out.println("Unable init lipmuscle without musclesettings ");
            return;
        }
        bindBSplines();
        bSpline.setControlPoint1X(bSplineBase.getControlPoint1X());
        bSpline.setControlPoint1Y(bSplineBase.getControlPoint1Y());
        bSpline.setControlPoint2X(bSplineBase.getControlPoint2X());
        bSpline.setControlPoint2Y(bSplineBase.getControlPoint2Y());
        bSpline.updateAbsolute();
        bSpline.updateBSpline();
        bSpline.calculateSpline();
        bSplineBase.updateBSpline();
        calculateVertexIndices();
        bSpline.calculateSpline();
    }

    private void bindBSplines()
    {
        bSplineBase.setPoints(muscleSetSettings);
        bSplineBase.setOffsetPoints(mouth.getParamOffset());
        if (bSplineBase.getControlPoints() == null)
        {
            bSplineBase.setControlPoints(new float[] { 0, 0, 0, 0 });
        }

        bSpline.setPoints(muscleSetSettings);
        bSpline.setOffsetPoints(mouth.getParamOffset());
        bSpline.setControlPoints(muscleSetSettings);
        bSpline.setOffsetControlPoints(paramOffset);
        bSpline.updateAbsolute();
        bSpline.updateBSpline();
        // bSpline.calculateSpline();
    }

    /**
     * \brief update the vertices displacement
     */
    @Override
    public void updateVertices(float dstArray[], float muscleSettings[])
    {

        float offset = 0;
        int index = 0;
        float coord1[] = new float[2];
        float coord2[] = new float[2];
        float width = 1 / (mouth.getLeftX() - mouth.getRightX());

        int cnt = 0, cnt2 = 0;
        for (int i : this.indices)
        {
            index = i * 3;
            offset = (mouth.getLeftX() - dstArray[index]) * width;
            bSpline.getSplineCoord(offset, coord1);
            bSplineBase.getSplineCoord(offset, coord2);
            dstArray[index] += (coord1[0] - coord2[0]) * intensity[cnt2];
            dstArray[index + 1] += (coord1[1] - coord2[1]) * intensity[cnt2++];
            cnt++;
        }
    }

    /**
     * \brief boolean is negative upper or lower lip
     */
    public boolean isNegative()
    {
        return negative;
    }

    /**
     * \brief set negative
     */
    public void setNegative(boolean negative)
    {
        this.negative = negative;
    }

    /**
     * \brief get the BSpline
     */
    public BSpline2f getBSpline()
    {
        return bSpline;
    }

    /**
     * \brief get the vertices
     */
    public float[] getVertices()
    {
        return vertices;
    }

    /**
     * \brief filter a vertex
     */
    @Override
    public void filterVertex(Integer vertexIndex)
    {
        super.filterVertex(vertexIndex);
        calculateVertexIndices();
    }

    /**
     * \brief restore a vertex
     */
    @Override
    public void restoreVertex(Integer vertexIndex)
    {
        super.restoreVertex(vertexIndex);
        calculateVertexIndices();
    }

    /**
     * \brief get the bspline base The bsline base is the "original" bspline setting
     */
    public BSpline2f getBSplineBase()
    {
        return bSplineBase;
    }

    /**
     * \brief get the base position
     */
    public float[] getBasePosition()
    {
        return basePosition;
    }

    /**
     * \brief get the intensity array
     */
    public float[] getIntensity()
    {
        return intensity;
    }

    public BoundingBox getBoundingBox()
    {
        return boundingBox;
    }

    /**
     * \brief get the lip filter list returns list of vertices filtered.
     * 
     * TODO: remove this currently there is an automatic filtering algorithm but it doesn't really work together with the manual vertex filtering
     * therefore two filter array's.
     * 
     * Probably the best is to only have the hand filtered list. And another GUI function button for automatic filter (which is a wrapper for the
     * manual filter)
     */
    public ArrayList<Integer> getLipFilterList()
    {
        return lipFilterList;
    }

    @Override
    public void setMuscleSettings(float[] muscleSettings)
    {
        super.setMuscleSettings(muscleSettings);
        bindBSplines();
    }

    public MouthMuscle getMouth()
    {
        return mouth;
    }

    public void setMouth(MouthMuscle mouth)
    {
        this.mouth = mouth;
        if (negative)
        {
            if (this.mouth.getLowerLip() != this)
            {
                this.mouth.setLowerLip(this);
            }
        }
        else if (this.mouth.getUpperLip() != this)
        {
            this.mouth.setUpperLip(this);
        }

    }
}
