package environment.state;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agent.MLKAgent;
import util.Pair;

public interface State2D extends State {

    Pair<Double, Double> getAgentPosition(MLKAgent agent);

    Map<MLKAgent, Pair<Double, Double>> getAgentsPositions();

    void setAgentPosition(MLKAgent agent, double x, double y);

    void removeAgent(MLKAgent agent);

    double getWidth();

    double getHeight();

    double getAgentViewRange();

    boolean getIsToroidal();

    default void addAgent(MLKAgent agent, double x, double y) {
        checkPositionInsideBounds(x, y);
        setAgentPosition(agent, x, y);
    }

    default void addAgent(MLKAgent agent, Pair<Double, Double> position) {
        addAgent(agent, position.getFirst(), position.getSecond());
    }

    default void setAgentPosition(MLKAgent agent, Pair<Double, Double> position) {
        setAgentPosition(agent, position.getFirst(), position.getSecond());
    }

    default void moveAgent(MLKAgent agent, Pair<Double, Double> move) {
        Pair<Double, Double> position = getAgentPosition(agent);

        if (position == null) {
            throw new IllegalArgumentException("The agent is not registered in the state.");
        }

        double newX = position.getFirst() + move.getFirst();
        double newY = position.getSecond() + move.getSecond();

        if (getIsToroidal()) {
            newX = ((newX % getWidth()) + getWidth()) % getWidth();
            newY = ((newY % getHeight()) + getHeight()) % getHeight();
        } else {
            newX = Math.clamp(newX, 0.0, getWidth());
            newY = Math.clamp(newY, 0.0, getHeight());
        }

        setAgentPosition(agent, newX, newY);
    }

    default void moveAgent(MLKAgent agent, double dx, double dy) {
        moveAgent(agent, new Pair<>(dx, dy));
    }

    default Pair<Double, Double> relativePosition(Pair<Double, Double> reference, Pair<Double, Double> target) {
        double dx = target.getFirst() - reference.getFirst();
        double dy = target.getSecond() - reference.getSecond();

        if (getIsToroidal()) {
            dx = Math.min(Math.abs(dx), getWidth() - Math.abs(dx));
            dy = Math.min(Math.abs(dy), getHeight() - Math.abs(dy));
        }

        return new Pair<>(dx, dy);
    }

    default Pair<Double, Double> relativePosition(MLKAgent observer, MLKAgent observed) {
        Pair<Double, Double> observerPosition = getAgentPosition(observer);
        Pair<Double, Double> observedPosition = getAgentPosition(observed);

        if (observerPosition == null || observedPosition == null) {
            throw new IllegalArgumentException("Both agents must be registered in the state.");
        }

        return relativePosition(observerPosition, observedPosition);
    }

    default double distance(Pair<Double, Double> p1, Pair<Double, Double> p2) {
        Pair<Double, Double> relative = relativePosition(p1, p2);
        double dx = relative.getFirst();
        double dy = relative.getSecond();
        return Math.sqrt(dx * dx + dy * dy);
    }

    default double distance(MLKAgent a1, MLKAgent a2) {
        Pair<Double, Double> p1 = getAgentPosition(a1);
        Pair<Double, Double> p2 = getAgentPosition(a2);

        if (p1 == null || p2 == null) {
            throw new IllegalArgumentException("Both agents must be registered in the state.");
        }

        return distance(p1, p2);
    }

    default boolean isInViewRange(MLKAgent agent, Pair<Double, Double> position, double viewRange) {
        Pair<Double, Double> agentPosition = getAgentPosition(agent);

        if (agentPosition == null) {
            throw new IllegalArgumentException("The agent is not registered in the state.");
        }

        return distance(agentPosition, position) <= viewRange;
    }

    default boolean isInViewRange(MLKAgent agent, Pair<Double, Double> position) {
        return isInViewRange(agent, position, getAgentViewRange());
    }

    default boolean isAgentInViewRange(MLKAgent observer, MLKAgent observed, double viewRange) {
        if (observer.equals(observed)) {
            return true;
        }

        Pair<Double, Double> observedPosition = getAgentPosition(observed);

        if (observedPosition == null) {
            throw new IllegalArgumentException("The observed agent is not registered in the state.");
        }

        return isInViewRange(observer, observedPosition, viewRange);
    }

    default boolean isAgentInViewRange(MLKAgent observer, MLKAgent observed) {
        return isAgentInViewRange(observer, observed, getAgentViewRange());
    }

    default List<Pair<Double, Double>> getPointsInViewRange(MLKAgent agent, List<Pair<Double, Double>> points, double viewRange) {
        List<Pair<Double, Double>> visiblePoints = new ArrayList<>();

        for (Pair<Double, Double> point : points) {
            if (isInViewRange(agent, point, viewRange)) {
                visiblePoints.add(point.clone());
            }
        }

        return visiblePoints;
    }

    default List<Pair<Double, Double>> getPointsInViewRange(MLKAgent agent, List<Pair<Double, Double>> points) {
        return getPointsInViewRange(agent, points, getAgentViewRange());
    }

    default Map<MLKAgent, Pair<Double, Double>> getAgentsInViewRange(MLKAgent observer, double viewRange) {
        Map<MLKAgent, Pair<Double, Double>> visibleAgents = new HashMap<>();

        for (Map.Entry<MLKAgent, Pair<Double, Double>> entry : getAgentsPositions().entrySet()) {
            MLKAgent otherAgent = entry.getKey();

            if (otherAgent.equals(observer)) {
                continue;
            }

            if (isAgentInViewRange(observer, otherAgent, viewRange)) {
                visibleAgents.put(otherAgent, entry.getValue().clone());
            }
        }

        return visibleAgents;
    }

    default Map<MLKAgent, Pair<Double, Double>> getAgentsInViewRange(MLKAgent observer) {
        return getAgentsInViewRange(observer, getAgentViewRange());
    }

    default boolean isInsideBounds(double x, double y) {
        return x >= 0.0 && x <= getWidth() && y >= 0.0 && y <= getHeight();
    }

    default boolean isInsideBounds(Pair<Double, Double> position) {
        return isInsideBounds(position.getFirst(), position.getSecond());
    }

    default void checkPositionInsideBounds(double x, double y) {
        if (!isInsideBounds(x, y)) {
            throw new IllegalArgumentException("Position outside bounds: (" + x + "; " + y + ")");
        }
    }

    default void checkPositionInsideBounds(Pair<Double, Double> position) {
        checkPositionInsideBounds(position.getFirst(), position.getSecond());
    }
}