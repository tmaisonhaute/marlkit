package communicationimplementation;

import java.io.Serializable;

import agent.MLKAgent;
import environment.observation.ObservationPositionsValues;
import util.Pair;

public record ObservationPositionsValuesMessage(MLKAgent sender, Pair<Double, Double> senderPosition, ObservationPositionsValues observation) 
implements Serializable {}
