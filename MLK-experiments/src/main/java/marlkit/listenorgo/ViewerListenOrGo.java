package marlkit.listenorgo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agent.MLKAgent;
import static javafx.scene.paint.Color.BLACK;
import static javafx.scene.paint.Color.LIGHTBLUE;
import static javafx.scene.paint.Color.LIGHTGRAY;
import static javafx.scene.paint.Color.RED;
import madkit.kernel.Agent;
import madkit.simulation.PropertyProbe;
import static madkit.simulation.SimuOrganization.ENVIRONMENT_ROLE;
import simulation.MLKViewer;

/**
 * JavaFX-based viewer for the ListenOrGo simulation.
 * <p>
 * Renders the two doors (left and right) on a canvas, highlights the correct
 * door, and shows each undecided agent as a coloured circle together with
 * counters displaying how many agents have moved to each side.
 * </p>
 */
public class ViewerListenOrGo extends MLKViewer {
    
    private PropertyProbe<Choice> correctChoiceProbe;
    private PropertyProbe<Map<MLKAgent, Choice>> agentsChoiceProbe;
    
    private Map<MLKAgent, Integer> agentIds;
    private int nextAgentId = 0;
    
    private static final double CANVAS_WIDTH = 600;
    private static final double CANVAS_HEIGHT = 400;
    private static final double BOX_WIDTH = 150;
    private static final double BOX_HEIGHT = 200;
    private static final double BOX_Y = 150;
    private static final double LEFT_BOX_X = 100;
    private static final double RIGHT_BOX_X = 350;
    private static final double AGENT_SIZE = 15;
    private static final double AGENT_Y = 80;
    
    /**
     * Initialises the viewer by setting up property probes on the environment
     * and configuring the canvas dimensions.
     */
    @Override
    protected void onActivation() {
        super.onActivation();
        correctChoiceProbe = new PropertyProbe<>(getModelGroup(), ENVIRONMENT_ROLE, "correctChoice");
        agentsChoiceProbe = new PropertyProbe<>(getModelGroup(), ENVIRONMENT_ROLE, "agentsChoice");
        
        addProbe(correctChoiceProbe);
        addProbe(agentsChoiceProbe);
        
        agentIds = new HashMap<>();
        
        getGUI().getCanvas().setWidth(CANVAS_WIDTH);
        getGUI().getCanvas().setHeight(CANVAS_HEIGHT);
    }
    
    /**
     * Renders the current episode state on the canvas.
     * <p>
     * Draws the two door boxes (highlighting the correct one), positions
     * undecided agents as circles, and displays per-door commitment counters.
     * If the probes are not yet ready, the method returns immediately.
     * </p>
     */
    @Override
    public void render() {
        super.render();
        
        if (correctChoiceProbe.getAgents().isEmpty() || agentsChoiceProbe.getAgents().isEmpty()) {
            return;
        }
        
        Agent env = correctChoiceProbe.getAgents().get(0);
        Choice correctChoice = correctChoiceProbe.getPropertyValue(env);
        Map<MLKAgent, Choice> agentsChoice = agentsChoiceProbe.getPropertyValue(env);
        
        if (correctChoice == null || agentsChoice == null) {
            return;
        }
        
        for (MLKAgent agent : agentsChoice.keySet()) {
            if (!agentIds.containsKey(agent)) {
                agentIds.put(agent, nextAgentId++);
            }
        }
        
        int leftCount = 0;
        int rightCount = 0;
        for (Map.Entry<MLKAgent, Choice> entry : agentsChoice.entrySet()) {
            Choice choice = entry.getValue();
            if (choice == Choice.LEFT) {
                leftCount++;
            } else if (choice == Choice.RIGHT) {
                rightCount++;
            }
        }
        
        drawBoxes(correctChoice == Choice.RIGHT);
        
        drawUnassignedAgents(agentsChoice);
        
        drawCounters(leftCount, rightCount);
    }
    
    private void drawBoxes(boolean isRight) {
        if (!isRight) {
            getGraphics().setFill(LIGHTBLUE);
        } else {
            getGraphics().setFill(LIGHTGRAY);
        }
        getGraphics().fillRect(LEFT_BOX_X, BOX_Y, BOX_WIDTH, BOX_HEIGHT);
        getGraphics().setStroke(BLACK);
        getGraphics().strokeRect(LEFT_BOX_X, BOX_Y, BOX_WIDTH, BOX_HEIGHT);
        
        if (isRight) {
            getGraphics().setFill(LIGHTBLUE);
        } else {
            getGraphics().setFill(LIGHTGRAY);
        }
        getGraphics().fillRect(RIGHT_BOX_X, BOX_Y, BOX_WIDTH, BOX_HEIGHT);
        getGraphics().setStroke(BLACK);
        getGraphics().strokeRect(RIGHT_BOX_X, BOX_Y, BOX_WIDTH, BOX_HEIGHT);
        
        // Labels
        getGraphics().setFill(BLACK);
        getGraphics().fillText("LEFT", LEFT_BOX_X + BOX_WIDTH/2 - 15, BOX_Y - 10);
        getGraphics().fillText("RIGHT", RIGHT_BOX_X + BOX_WIDTH/2 - 20, BOX_Y - 10);
    }
    
    private void drawUnassignedAgents(Map<MLKAgent, Choice> agentsChoice) {
        List<MLKAgent> unassigned = agentsChoice.entrySet().stream()
            .filter(e -> e.getValue() == Choice.NONE)
            .map(Map.Entry::getKey)
            .toList();
        
        int leftUnassigned = 0;
        int rightUnassigned = 0;
        
        for (MLKAgent agent : unassigned) {
            int agentId = agentIds.get(agent);
            
            double x, y;
            if (agentId % 2 == 0) {
                x = LEFT_BOX_X + BOX_WIDTH/2 - AGENT_SIZE/2 + (leftUnassigned * 25) - 25;
                y = AGENT_Y;
                leftUnassigned++;
            } else {
                x = RIGHT_BOX_X + BOX_WIDTH/2 - AGENT_SIZE/2 + (rightUnassigned * 25) - 25;
                y = AGENT_Y;
                rightUnassigned++;
            }
            
            getGraphics().setFill(RED);
            getGraphics().fillOval(x, y, AGENT_SIZE, AGENT_SIZE);
        }
    }
    
    private void drawCounters(int leftCount, int rightCount) {
        getGraphics().setFill(BLACK);
        getGraphics().setFont(javafx.scene.text.Font.font(30));
        
        String leftText = String.valueOf(leftCount);
        getGraphics().fillText(leftText, LEFT_BOX_X + BOX_WIDTH/2 - 10, BOX_Y + BOX_HEIGHT/2 + 10);
        
        String rightText = String.valueOf(rightCount);
        getGraphics().fillText(rightText, RIGHT_BOX_X + BOX_WIDTH/2 - 10, BOX_Y + BOX_HEIGHT/2 + 10);
        
        getGraphics().setFont(javafx.scene.text.Font.getDefault());
    }
    
    /**
     * Returns the {@link EnvListenOrGo} environment associated with this viewer.
     *
     * @return the typed environment instance.
     */
    @Override
    public EnvListenOrGo getEnvironment() {
        return (EnvListenOrGo) super.getEnvironment();
    }
}
