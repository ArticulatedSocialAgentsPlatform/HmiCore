package hmi.faceanimation.model;

/**
 * A FACS AU.
 * 
 * @author PaulRC
 */
public class ActionUnit
{
    protected int index; // Sequence number.
    protected int number;

    public enum Symmetry
    {
        SYMMETRIC, ASYMMETRIC
    }

    protected Symmetry symmetry;
    protected String name;
    protected String muscle;

    /**
     * Constructor
     */
    public ActionUnit()
    {
    }

    /**
     * Constructor
     * 
     * @param index the index to set
     * @param number the number to set
     * @param symmetry the symmetry to set
     * @param name the name to set
     * @param muscle the muscle to set
     */
    public ActionUnit(int index, int number, Symmetry symmetry, String name, String musle)
    {
        this();

        this.index = index;
        this.number = number;
        this.symmetry = symmetry;
        this.name = name;
        this.muscle = musle;
    }

    /**
     * Constructor
     * 
     * @param index the index to set
     * @param number the number to set
     * @param symmetry the symmetry to set
     * @param name the name to set
     */
    public ActionUnit(int index, int number, Symmetry symmetry, String name)
    {
        this();

        this.index = index;
        this.number = number;
        this.symmetry = symmetry;
        this.name = name;
    }

    /**
     * @return the index
     */
    public int getIndex()
    {
        return index;
    }

    /**
     * @return the number
     */
    public int getNumber()
    {
        return number;
    }

    /**
     * @return the symmetry
     */
    public Symmetry getSymmetry()
    {
        return symmetry;
    }

    /**
     * @return the name
     */
    public String getName()
    {
        return name;
    }

    /**
     * @return the muscle
     */
    public String getMuscle()
    {
        return muscle;
    }

    @Override
    public String toString()
    {
        StringBuffer retval = new StringBuffer();
        retval.append("[ActionUnit, index: " + index);
        retval.append(", number: " + number);
        retval.append(", symmetry: " + symmetry.toString());
        retval.append(", name: " + name);
        retval.append(", muscle: " + muscle);
        retval.append(']');
        return retval.toString();
    }
}