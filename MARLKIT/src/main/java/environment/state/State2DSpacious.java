package environment.state;

import java.util.HashMap;
import java.util.Map;

import agent.MLKAgent;
import util.Pair;

public abstract class State2DSpacious implements State2D {

    protected double width;
    protected double height;
    protected double agentViewRange;
    protected boolean observeSelfPosition;
    protected boolean observeAgentsPositions;
    protected boolean toroidal = false;
    protected Map<MLKAgent, Pair<Double, Double>> agentsPosition;

    protected State2DSpacious(double width, double height) {
        this(width, height, Double.POSITIVE_INFINITY);
    }

    protected State2DSpacious(double width, double height, double agentViewRange) {
        this(width, height, agentViewRange, false, false, false);
    }

    protected State2DSpacious(double width, double height, double agentViewRange, boolean observeSelfPosition, boolean observeAgentsPositions, boolean toroidal) {
        if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("The space dimensions must be strictly positive.");
        }

        if (agentViewRange < 0) {
            throw new IllegalArgumentException("The agent view range must be positive or zero.");
        }

        this.width = width;
        this.height = height;
        this.agentViewRange = agentViewRange;
        this.observeSelfPosition = observeSelfPosition;
        this.observeAgentsPositions = observeAgentsPositions;
        this.toroidal = toroidal;
        this.agentsPosition = new HashMap<>();
    }


    public void setAgentPosition(MLKAgent agent, double x, double y) {
        checkPositionInsideBounds(x, y);
        agentsPosition.put(agent, new Pair<>(x, y));
    }

    public void removeAgent(MLKAgent agent) {
        agentsPosition.remove(agent);
    }

    public Map<MLKAgent, Pair<Double, Double>> getAgentsPositions() {
        Map<MLKAgent, Pair<Double, Double>> positions = new HashMap<>();

        for (Map.Entry<MLKAgent, Pair<Double, Double>> entry : agentsPosition.entrySet()) {
            positions.put(entry.getKey(), entry.getValue().clone());
        }

        return positions;
    }

    public Pair<Double, Double> getAgentPosition(MLKAgent agent) {
        Pair<Double, Double> position = agentsPosition.get(agent);
        return position == null ? null : position.clone();
    }

    public double getWidth() {
        return width;
    }

    public double getHeight() {
        return height;
    }

    public double getAgentViewRange() {
        return agentViewRange;
    }

    public boolean getIsToroidal() {
        return toroidal;
    }

    public boolean observesAgentsPositions() {
        return observeAgentsPositions;
    }

    public boolean observesSelfPosition() {
        return observeSelfPosition;
    }

    public void setObserveSelfPosition(boolean observeSelfPosition) {
        this.observeSelfPosition = observeSelfPosition;
    }

    public void setObserveAgentsPositions(boolean observeAgentsPositions) {
        this.observeAgentsPositions = observeAgentsPositions;
    }

    public void setToroidal(boolean toroidal) {
        this.toroidal = toroidal;
    }
}