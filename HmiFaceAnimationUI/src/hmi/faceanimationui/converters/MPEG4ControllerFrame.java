package hmi.faceanimationui.converters;

import hmi.faceanimation.FaceController;
import hmi.faceanimation.model.FAP;
import hmi.faceanimation.model.MPEG4;
import hmi.faceanimation.model.MPEG4Configuration;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.AbstractCellEditor;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellEditor;

public class MPEG4ControllerFrame extends JFrame implements ActionListener, MouseMotionListener, TableModelListener
{
    private static final long serialVersionUID = 2585351165270261988L;
    private FapTableModel tableModel;
    private JTable fapTable;
    private int fapValueEditRow = -1;

    HashMap<Integer, FAP> faps = MPEG4.getFAPs();
    Integer[] valuesOut = new Integer[faps.size()];

    private ArrayList<FAP> fapsEverUsed = new ArrayList<FAP>(); // Administration that enables setting FAPs to 0 only when needed (when they were in use).

    private FaceController faceController = null;

    public MPEG4ControllerFrame(FaceController fc)
    {
        faceController = fc;

        getContentPane().setLayout(new BorderLayout());

        // Create the table.
        tableModel = new FapTableModel(MPEG4.getFAPs());
        final TableCellEditor valueEditor = new ValueEditor();
        fapTable = new JTable(tableModel)
        {
            private static final long serialVersionUID = 3273984920824254762L;

            public TableCellEditor getCellEditor(int row, int col)
            {
                if (col == 2) return valueEditor;
                else return super.getCellEditor(row, col);
            }
        };
        fapTable.setRowHeight(35);
        fapTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        fapTable.getColumnModel().getColumn(0).setPreferredWidth(50);
        fapTable.getColumnModel().getColumn(1).setPreferredWidth(100);
        fapTable.getColumnModel().getColumn(2).setPreferredWidth(525);
        fapTable.getColumnModel().getColumn(3).setPreferredWidth(50);
        fapTable.getColumnModel().getColumn(4).setPreferredWidth(50);

        fapTable.addMouseMotionListener(this);
        tableModel.addTableModelListener(this);

        JScrollPane scrollPane = new JScrollPane(fapTable);
        getContentPane().add(scrollPane, BorderLayout.CENTER);

        // Create some buttons in a FlowLayout.
        JPanel buttonPanel = new JPanel(new FlowLayout());

        JButton dumpButton = new JButton("Dump ranges");
        dumpButton.setActionCommand("dump");
        dumpButton.addActionListener(this);
        buttonPanel.add(dumpButton);

        JButton closeButton = new JButton("Close");
        closeButton.setActionCommand("close");
        closeButton.addActionListener(this);
        buttonPanel.add(closeButton);

        JButton saveMpeg4Config = new JButton("Save this MPEG4 FAP configuration");
        saveMpeg4Config.addActionListener(new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                MPEG4Configuration configuration = new MPEG4Configuration();
                configuration.setValues(valuesOut);
                final JFileChooser fc = new JFileChooser();
                int retval = fc.showSaveDialog(null);
                if (retval == JFileChooser.APPROVE_OPTION)
                {
                    File file = fc.getSelectedFile();
                    try
                    {
                        configuration.saveToFAPFile(file);
                    }
                    catch (IOException e1)
                    {
                        e1.printStackTrace();
                    }
                }
            }
        });
        buttonPanel.add(saveMpeg4Config);

        getContentPane().add(buttonPanel, BorderLayout.SOUTH);

        // / Do some other stuff.
        setPreferredSize(new Dimension(900, 450));
        setLocation(50, 50);
        setTitle("MPEG4 controller");
        pack();

        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e)
    {
        String command = e.getActionCommand();
        if (command.equals("send"))
        {
            doSend();
        }
        else if (command.equals("dump"))
        {
            dumpRanges(tableModel);
        }
        else if (command.equals("close"))
        {
            dispose();
        }
    }

    private void doSend()
    {
        int[] values = tableModel.getValues();

        for (FAP fap : faps.values())
        {
            int fapIndex = fap.getIndex();
            int value = values[fapIndex];
            if (value != 0)
            {
                if (!fapsEverUsed.contains(fap)) fapsEverUsed.add(fap);
                valuesOut[fapIndex] = value;
            }
            else
            {
                // Reset the value if this FAP was set before.
                if (fapsEverUsed.contains(fap))
                {
                    valuesOut[fapIndex] = value = 0;
                    fapsEverUsed.remove(fap);
                }
                else valuesOut[fapIndex] = null;
            }
        }

        if (faceController != null)
        {
            MPEG4Configuration config = new MPEG4Configuration();
            config.setValues(valuesOut);
            faceController.setMPEG4Configuration(config);
        }
    }

    @Override
    public void mouseDragged(MouseEvent e)
    {
    }

    @Override
    public void mouseMoved(MouseEvent e)
    {
        boolean isEditing = fapTable.isEditing();

        int col = fapTable.columnAtPoint(e.getPoint());
        if (col != 2)
        {
            if (isEditing) fapTable.getCellEditor().stopCellEditing();
            fapValueEditRow = -1;
            return;
        }

        int row = fapTable.rowAtPoint(e.getPoint());
        if (fapValueEditRow != row)
        {
            if (isEditing) fapTable.getCellEditor().stopCellEditing();
            fapTable.editCellAt(row, col);
            fapValueEditRow = row;
        }
    }

    @Override
    public void tableChanged(TableModelEvent e)
    {
        if (e.getColumn() == 2) this.actionPerformed(new ActionEvent(tableModel, 1, "send"));
    }

    private void dumpRanges(FapTableModel tableModel)
    {
        int[] mins = tableModel.getMins();
        int[] maxs = tableModel.getMaxs();
        int numFaps = tableModel.getNumFaps();

        System.out.println("int[][] range = new int[" + numFaps + "][];");
        for (int i = 0; i < numFaps; i++)
        {
            if (mins[i] != 0 || maxs[i] != 0)
            {
                System.out.println("range[" + i + "] = new int[2];");
                System.out.println("range[" + i + "][0] = " + mins[i] + ";");
                System.out.println("range[" + i + "][1] = " + maxs[i] + ";");
            }
        }
    }

}

class FapTableModel extends AbstractTableModel
{
    private static final long serialVersionUID = -3457006952869085351L;
    private HashMap<Integer, FAP> faps;
    private int numFaps;
    private int[] values;
    private int[] mins;
    private int[] maxs;
    private String[] columnNames = { "#", "Name", "Value", "Min", "Max", "Directionality" };

    public FapTableModel(HashMap<Integer, FAP> faps)
    {
        this.faps = faps;
        numFaps = faps.size();

        values = new int[numFaps];
        mins = new int[numFaps];
        maxs = new int[numFaps];
    }

    public int getNumFaps()
    {
        return numFaps;
    }

    public int[] getValues()
    {
        return values;
    }

    public int[] getMins()
    {
        return mins;
    }

    public int[] getMaxs()
    {
        return maxs;
    }

    @Override
    public int getColumnCount()
    {
        return 6;
    }

    @Override
    public int getRowCount()
    {
        return numFaps;
    }

    @Override
    public Object getValueAt(int row, int col)
    {
        switch (col)
        {
        case 0:
            return faps.get(Integer.valueOf(row + 1)).getNumber();
        case 1:
            return faps.get(Integer.valueOf(row + 1)).getName();
        case 2:
            return values[row];
        case 3:
            return mins[row];
        case 4:
            return maxs[row];
        case 5:
            return faps.get(Integer.valueOf(row + 1)).getDirectionality().toString();
        default:
            return null;
        }
    }

    public String getColumnName(int col)
    {
        return columnNames[col];
    }

    public boolean isCellEditable(int row, int col)
    {
        return (col == 2 || col == 3 || col == 4);
    }

    public void setValueAt(Object value, int row, int col)
    {
        int in = Integer.parseInt(value.toString());
        int out = -1;

        switch (col)
        {
        case 2:
            out = values[row];
            break;
        case 3:
            out = mins[row];
            break;
        case 4:
            out = maxs[row];
            break;
        }

        if (in != out)
        {
            switch (col)
            {
            case 2:
                values[row] = in;
                break;
            case 3:
                mins[row] = in;
                break;
            case 4:
                maxs[row] = in;
                break;
            }
            fireTableCellUpdated(row, col);
        }
    }
}

class ValueEditor extends AbstractCellEditor implements TableCellEditor, ActionListener
{
    private static final long serialVersionUID = -2605852299597275812L;
    int intValue;
    JTable table;
    JLabel label;
    int row;

    @Override
    public Component getTableCellEditorComponent(final JTable table, Object value, boolean isSelected, final int row, final int col)
    {
        intValue = Integer.parseInt(value.toString());
        this.table = table;
        this.row = row;

        JPanel buttonPanel = new JPanel(new FlowLayout());
        JButton button;

        button = new JButton("-500");
        button.setActionCommand("-500");
        button.addActionListener(this);
        buttonPanel.add(button);

        button = new JButton("-100");
        button.setActionCommand("-100");
        button.addActionListener(this);
        buttonPanel.add(button);

        button = new JButton("-10");
        button.setActionCommand("-10");
        button.addActionListener(this);
        buttonPanel.add(button);

        button = new JButton("<");
        button.setActionCommand("<");
        button.addActionListener(this);
        buttonPanel.add(button);

        label = new JLabel(value.toString());
        label.setPreferredSize(new Dimension(30, 10));
        buttonPanel.add(label);

        button = new JButton(">");
        button.setActionCommand(">");
        button.addActionListener(this);
        buttonPanel.add(button);

        button = new JButton("10");
        button.setActionCommand("10");
        button.addActionListener(this);
        buttonPanel.add(button);

        button = new JButton("100");
        button.setActionCommand("100");
        button.addActionListener(this);
        buttonPanel.add(button);

        button = new JButton("500");
        button.setActionCommand("500");
        button.addActionListener(this);
        buttonPanel.add(button);

        button = new JButton("R");
        button.setActionCommand("reset");
        button.addActionListener(this);
        buttonPanel.add(button);

        return buttonPanel;
    }

    @Override
    public Object getCellEditorValue()
    {
        return intValue;
    }

    @Override
    public void actionPerformed(ActionEvent event)
    {
        String actionCommand = event.getActionCommand();
        if (actionCommand.equals("<"))
        {
            table.getModel().setValueAt(intValue, row, 3);
        }
        else if (actionCommand.equals(">"))
        {
            table.getModel().setValueAt(intValue, row, 4);
        }
        else if (actionCommand.equals("reset"))
        {
            table.getModel().setValueAt(0, row, 2);
        }
        else
        {
            // We're pretty sure actionCommand now is a integer and we can
            // use it to change the value.
            intValue += Integer.parseInt(actionCommand);
            label.setText(new Integer(intValue).toString());
            table.getModel().setValueAt(intValue, row, 2);
        }
    }
}
