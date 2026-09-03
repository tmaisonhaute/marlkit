package environment.observation.wrapperobservationvector;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import environment.observation.Observation;
import environment.observation.ObservationPositionValue;
import environment.observation.ObservationPositionsValues;
import util.Tuple;

public class WrapperVectorObservationPositionsValues implements WrapperObservationVector{

	private boolean doWrapPositionValue;
	
	public WrapperVectorObservationPositionsValues(boolean doWrapPositionValue) {
		this.doWrapPositionValue = doWrapPositionValue;
	}
	
	@Override
	public double[] transform(Observation observation) {
		ObservationPositionsValues obs = (ObservationPositionsValues) observation;
		
		if (obs.getListObs().isEmpty()) {
	        return new double[0];
	    }
		
		validatePositions(obs);
		
		int numberValuesForObservation = doWrapPositionValue ? 3 : 2;
		double[] vector = new double[obs.getListObs().size() * numberValuesForObservation];
		
		for (int i = 0; i < obs.getListObs().size(); i++) {
		    ObservationPositionValue o = obs.getListObs().get(i);
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
	

	/**
	 * Validates that all ObservationPositionValue objects in the given ObservationPositionsValues have positions of size 2.
	 * @param obs the ObservationPositionsValues to validate
	 */
	private void validatePositions(ObservationPositionsValues obs) {
	    for (ObservationPositionValue o : obs.getListObs()) {
	        if (o.getPosition().getSize() != 2) {
	            throw new IllegalArgumentException("ObservationPositionValue must have a position of size 2");
	        }
	    }
	}


}
