package hmi.faceanimationui.converters;

import hmi.faceanimation.converters.FACSConverter;
import hmi.faceanimation.model.ActionUnit;
import hmi.faceanimation.model.ActionUnit.Symmetry;
import hmi.faceanimation.model.FACS;
import hmi.faceanimation.model.FACS.Side;
import hmi.faceanimation.model.FACSConfiguration;

import java.awt.Color;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.filechooser.FileFilter;

//TODO: if config changed, e.g. by loading from file, then reset sliders to new positions!
public class FACSPanel extends JPanel implements ChangeListener
{
    private static final long serialVersionUID = 3118834458010380447L;
    FACSConfiguration config;

    private ArrayList<ConfigListener> configListeners = new ArrayList<ConfigListener>();

    private AUSlider[] sliders;
    private int numAus;

    public FACSPanel(FACSConfiguration config, FACSConverter facsConverter)
    {
        this.config = config;

        numAus = FACS.getActionUnits().size();
        sliders = new AUSlider[numAus * 2];

        Color color = new Color(0, 0, 0);
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        setLayout(layout);

        constraints.weightx = 1.0;

        // Add header row.
        addLabel("", layout, constraints, color);
        addLabel("Right", layout, constraints, color);
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        addLabel("Left", layout, constraints, color);

        // Add slider controls.
        constraints.fill = GridBagConstraints.BOTH;
        HashMap<Integer, ActionUnit> aus = FACS.getActionUnits();
        for (ActionUnit au : aus.values())
        {
            if (facsConverter.getNumberOfInfluences(au) == 0) color = new Color(127, 127, 127);
            else color = new Color(0, 0, 0);

            constraints.gridwidth = 1;
            addLabel(au.getNumber() + ": " + au.getName(), layout, constraints, color);
            if (au.getSymmetry() == Symmetry.ASYMMETRIC)
            {
                addSlider(au, Side.RIGHT, layout, constraints, color);
                constraints.gridwidth = GridBagConstraints.REMAINDER;
                addSlider(au, Side.LEFT, layout, constraints, color);
            }
            else
            {
                constraints.gridwidth = GridBagConstraints.REMAINDER;
                addSlider(au, Side.NONE, layout, constraints, color);
            }
        }

        // add save button
        JButton saveButton = new JButton("SAVE");
        saveButton.addActionListener(new SaveListener());
        add(saveButton);
        // add load button
        JButton loadButton = new JButton("LOAD");
        loadButton.addActionListener(new LoadListener());
        add(loadButton);
    }

    protected void addSlider(ActionUnit au, Side side, GridBagLayout layout, GridBagConstraints constraints, Color color)
    {
        AUSlider slider = new AUSlider(au, side);
        slider.setForeground(color);
        layout.setConstraints(slider, constraints);
        slider.addChangeListener(this);
        add(slider);
        int index = au.getIndex();
        if (side == Side.RIGHT)
        {
            index += numAus;
        }
        sliders[index] = slider;
    }

    protected void setConfig(FACSConfiguration fc)
    {
        config = fc;
        for (int i = 0; i < 2 * numAus - 1; i++)
        {
            Float val = fc.getValues()[i];
            if (val != null)
            {
                if (sliders[i] != null) sliders[i].setValue((int) (val.floatValue() * 100));
            }
            else
            {
                if (sliders[i] != null) sliders[i].setValue(0);
            }
        }
    }

    protected void addLabel(String text, GridBagLayout layout, GridBagConstraints constraints, Color color)
    {
        JLabel label = new JLabel(text);
        label.setForeground(color);
        layout.setConstraints(label, constraints);
        add(label);
    }

    @Override
    public void stateChanged(ChangeEvent event)
    {
        AUSlider source = (AUSlider) event.getSource();
        ActionUnit au = source.getActionUnit();
        config.setValue(source.getSide(), au.getIndex(), source.getAUValue());
        fireConfigChanged();
    }

    public void addConfigListener(ConfigListener cl)
    {
        configListeners.add(cl);
    }

    public void removeConfigListener(ConfigListener cl)
    {
        configListeners.remove(cl);
    }

    public void fireConfigChanged()
    {
        for (ConfigListener cl : configListeners)
            cl.configChanged(config);
    }

    /** load facs config from file */
    class LoadListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            JFileChooser chooser = new JFileChooser(".");
            chooser.setFileFilter(new FileFilter()
            {
                @Override
                public boolean accept(File f)
                {
                    return f.isDirectory() || f.getName().endsWith(".xml");
                }

                @Override
                public String getDescription()
                {
                    return "FACS configs (.xml)";
                }
            });
            int returnVal = chooser.showOpenDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                File f = chooser.getSelectedFile();
                if (f != null)
                {
                    FileReader r;
                    try
                    {
                        r = new FileReader(f);
                    }
                    catch (FileNotFoundException e1)
                    {
                        System.out.println("File not found; see stack trace for more info.");
                        e1.printStackTrace();
                        return;
                    }

                    try
                    {
                        config.readXML(r);
                        setConfig(config);
                    }
                    catch (IOException ex)
                    {
                        System.out.println("Error reading file; see stack trace for more info.");
                        ex.printStackTrace();
                    }
                    try
                    {
                        r.close();
                    }
                    catch (IOException e1)
                    {
                        System.out.println("Error closing file; see stack trace for more info.");
                        e1.printStackTrace();
                    }
                }
            }
        }
    }

    /** save facs config to file */
    class SaveListener implements ActionListener
    {
        @Override
        public void actionPerformed(ActionEvent e)
        {
            JFileChooser chooser = new JFileChooser(".");
            chooser.setFileFilter(new FileFilter()
            {
                @Override
                public boolean accept(File f)
                {
                    return f.isDirectory() || f.getName().endsWith(".xml");
                }

                @Override
                public String getDescription()
                {
                    return "FACS configs (.xml)";
                }
            });
            int returnVal = chooser.showSaveDialog(null);
            if (returnVal == JFileChooser.APPROVE_OPTION)
            {
                File f = chooser.getSelectedFile();
                if (f != null)
                {
                    try
                    {
                        PrintWriter pw = new PrintWriter(f);
                        config.writeXML(pw);
                        pw.close();
                    }
                    catch (IOException ex)
                    {
                        System.out.println("Error writing file; see stack trace for more info.");
                        ex.printStackTrace();
                    }
                }
            }
        }
    }

}

class AUSlider extends JSlider
{
    private static final long serialVersionUID = 4179739217850318037L;
    private Side side;
    private ActionUnit au;

    public AUSlider(ActionUnit au, Side side)
    {
        super(0, 100, 0);
        this.au = au;
        this.side = side;
    }

    public ActionUnit getActionUnit()
    {
        return au;
    }

    public float getAUValue()
    {
        return (float) getValue() / 100;
    }

    public Side getSide()
    {
        return side;
    }
}

interface ConfigListener
{
    void configChanged(FACSConfiguration config);
}
