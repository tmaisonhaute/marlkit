package marlkit.preyhunter.policies;

import agent.MLKAgent;
import agent.action.Action;
import agent.action.Move2DDouble;
import environment.observation.Observation;
import environment.observation.ObservationPositionValue;
import environment.observation.ObservationPositionsValues;
import learning.Policy;
import marlkit.preyhunter.environment.StatePreyHunter2D;
import util.Pair;

/**
 * Heuristic policy for prey agents.
 *
 * If at least one hunter is visible in the observation, the prey moves away
 * from the closest hunter. Otherwise, it moves randomly.
 */
public class PreyEscapePolicy implements Policy {

    private MLKAgent agent;
    private final double speed;

    public PreyEscapePolicy(double speed) {
        this.speed = speed;
    }

    @Override
    public void init(MLKAgent agent) {
        this.agent = agent;
    }

    @Override
    public MLKAgent getAgent() {
        return agent;
    }

    @Override
    public Action selectAction(Observation input) {
        if (!(input instanceof ObservationPositionsValues observation)) {
            return randomMove();
        }

        Pair<Double, Double> closestHunterRelativePosition =
                getClosestHunterRelativePosition(observation);

        if (closestHunterRelativePosition == null) {
            return randomMove();
        }

        return escapeFrom(closestHunterRelativePosition);
    }

    private Pair<Double, Double> getClosestHunterRelativePosition(ObservationPositionsValues observation) {
        Pair<Double, Double> closestPosition = null;
        double closestDistance = Double.POSITIVE_INFINITY;

        for (ObservationPositionValue obs : observation.getListObs()) {
            if (obs.getValue() != StatePreyHunter2D.OBS_HUNTER) {
                continue;
            }

            Pair<Double, Double> relativePosition = obs.getPosition().toPair2D();
            double distance = norm(relativePosition);

            if (distance < closestDistance) {
                closestDistance = distance;
                closestPosition = relativePosition;
            }
        }

        return closestPosition;
    }

    private Move2DDouble escapeFrom(Pair<Double, Double> hunterRelativePosition) {
        double dx = hunterRelativePosition.getFirst();
        double dy = hunterRelativePosition.getSecond();

        return Move2DDouble.fromVector(dx, dy, -speed);
    }

    private Move2DDouble randomMove() {
        double angle = prng().nextDouble() * 2.0 * Math.PI;
        return Move2DDouble.fromAngle(angle, speed);
    }

    private double norm(Pair<Double, Double> vector) {
        double x = vector.getFirst();
        double y = vector.getSecond();

        return Math.sqrt(x * x + y * y);
    }
}