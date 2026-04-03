package environment.observation.wrapperobservationvector;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import environment.observation.ObservationPositionValue;
import environment.observation.ObservationPositionsValues;
import learning.policy.PolicyInput;
import util.Tuple;

public class WrapperVectorObservationPositionsValues implements WrapperPolicyInputVector{

	private boolean doWrapPositionValue;
	
	public WrapperVectorObservationPositionsValues(boolean doWrapPositionValue) {
		this.doWrapPositionValue = doWrapPositionValue;
	}
	
	@Override
	public double[] transform(PolicyInput input) {
		ObservationPositionsValues obs = (ObservationPositionsValues) input;
		
		//TODO not really satisfying yet. It prevent wrapping for position that is not with 2 values for the position (2D)
		if (obs.getPosition(0).getSize() != 2) {
			throw new IllegalArgumentException("ObservationPositionValue must have a position of size 2");
		}
		
		int numberValuesForObservation = doWrapPositionValue ? 3 : 2;
		double[] vector = new double[obs.getListObs().size() * numberValuesForObservation];
		
		for (ObservationPositionValue o : obs.getListObs()) {
			int i = obs.getListObs().indexOf(o);
			Tuple position = o.getPosition();
			vector[i * numberValuesForObservation] = position.getValue(0);
			vector[i * numberValuesForObservation + 1] = position.getValue(1);
			if (doWrapPositionValue) {
				vector[i * numberValuesForObservation + 2] = o.getValue();
			}
		}
		return vector;
	}

	@Override
	public ObservationPositionsValues transform(double[] vector) {
		ObservationPositionsValues obs = new ObservationPositionsValues();
		int numberValuesForObservation = doWrapPositionValue ? 3 : 2;
		for(int i = 0; i < vector.length; i += numberValuesForObservation) {
			List<Double> position = new ArrayList<>(Arrays.asList(vector[i], vector[i + 1]));
			Tuple positionTuple = new Tuple(position);
			if (doWrapPositionValue) {
                obs.addObservation(new ObservationPositionValue(positionTuple, vector[i + 2]));
			}
			else {
				obs.addObservation(new ObservationPositionValue(positionTuple, 1));
			}	
		}
		return obs;
	}

}
