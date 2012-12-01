package hmi.faceanimationui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JPanel;

import lombok.Getter;

/**
 * A user interface to set and update morph target deformations
 * @author hvanwelbergen
 */
public class MorphView
{
    @Getter
    private final JPanel panel = new JPanel();
    private final MorphController morphController;
    private List<MorphPanel> morphPanels = new ArrayList<>();

    public MorphView(MorphController mc, Collection<String>morphs)
    {
        this.morphController = mc;
        panel.setLayout(new BoxLayout(panel,BoxLayout.PAGE_AXIS));
        for(String morph:morphs)
        {
            MorphPanel mp = new MorphPanel(morph, this);
            morphPanels.add(mp);
            panel.add(mp.getPanel());
        }
    }

    public void update()
    {
        List<MorphConfiguration> mc = new ArrayList<>();
        for (MorphPanel mp : morphPanels)
        {
            mc.add(mp.getMorphConfiguration());
        }
        morphController.update(mc);
    }
}
