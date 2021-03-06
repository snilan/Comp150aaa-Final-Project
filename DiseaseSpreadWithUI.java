import sim.portrayal.*;
import sim.portrayal.continuous.*;
import sim.portrayal.simple.*;
import sim.engine.*;
import sim.display.*;
import javax.swing.*;
import java.awt.Color;

/**
 * Encapsulates the GUI for the disease-spreading simulation.
 */
public class DiseaseSpreadWithUI extends GUIState
{
    // GUI data:
    private Display2D display;
    private JFrame displayFrame;
    private ContinuousPortrayal2D envPortrayal = new ContinuousPortrayal2D();

    /**
     * Initializes GUI with a new simulation. Takes optional cmdline arguments;
     * see DiseaseSpread.SimMaker.
     */
    public DiseaseSpreadWithUI(String[] args)
    {
        super((new DiseaseSpread.SimMaker()).newInstance(System.currentTimeMillis(), args));
    }

    /** Initializes GUI with an existing simulation. */
    public DiseaseSpreadWithUI(SimState state)
    {
        super(state);
    }

    /** Simulation name to display in the GUI. */
    public static String getName()
    {
        return "DiseaseSpread";
    }

    /** Returns simulation object for GUI inspector. */
    public Object getSimulationInspectedObject()
    {
        return state;
    }

    /** Returns inspector that updates at every time step (slow). */
    public Inspector getInspector()
    {
        Inspector i = super.getInspector();
        i.setVolatile(true);  // update at every step
        return i;
    }

    /** Starts the simulation ("play" button). */
    public void start()
    {
        super.start();
        setupPortrayals();
    }

    /** Loads the simulation from a checkpoint. */
    public void load(SimState state)
    {
        super.load(state);
        setupPortrayals();
    }

    /** Sets up the visualization. */
    public void setupPortrayals()
    {
        DiseaseSpread sim = (DiseaseSpread)state;

        // tell the portrayals what to portray and how to portray them
        envPortrayal.setField(sim.environment);
        envPortrayal.setPortrayalForClass(Agent.class, new AgentPortrayal());
        envPortrayal.setPortrayalForClass(Food.class, new FoodPortrayal());

        // reschedule the displayer
        display.reset();
        display.setBackdrop(Color.white);

        // redraw the display
        display.repaint();
    }

    /** Creates the GUI. */
    public void init(Controller c)
    {
        super.init(c);
        display = new Display2D(800, 600, this);
        display.setClipping(false);
        displayFrame = display.createFrame();
        displayFrame.setTitle("Disease Spread Display");
        c.registerFrame(displayFrame);
        displayFrame.setVisible(true);
        display.attach(envPortrayal, "environment");
    }

    /** Destroys the GUI. Doc says this may be called more than once. */
    public void quit()
    {
        super.quit();
        if (displayFrame != null) {
            displayFrame.dispose();
        }
        displayFrame = null;
        display = null;
    }

    /** Runs the simulation GUI. */
    public static void main(String[] args)
    {
        new DiseaseSpreadWithUI(args).createController();
    }
}
