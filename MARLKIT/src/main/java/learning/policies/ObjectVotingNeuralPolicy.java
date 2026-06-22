package learning.policies;

import java.util.Arrays;
import java.util.Objects;

import agent.MLKAgent;
import agent.action.Action;
import environment.observation.wrapperobservationvector.WrapperPolicyInputVector;
import learning.nn.NeuralNetwork;
import util.VectorOperator;

/**
 * A policy that uses a neural network to compute action logits based on input entities, and selects actions using a softmax distribution.
 * 
 * This policy is designed for environments where the observation can be represented as a vector of entities, and each entity contributes 
 * to the overall action logits. The logits are averaged across all entities before applying the softmax function to select an action.
 */
public class ObjectVotingNeuralPolicy implements PolicyGradientPolicy {


    private MLKAgent agent;

    private final NeuralNetwork objectToActionNetwork;
    private final WrapperPolicyInputVector wrapper;
    private final Action[] actions;
    private final double temperature;
    private final boolean initializeOnInit;
    private final int inputSize;

    public ObjectVotingNeuralPolicy(
            NeuralNetwork objectToActionNetwork,
            WrapperPolicyInputVector wrapper,
            Action[] actions,
            double temperature,
            boolean initializeOnInit
    ) {
        this.objectToActionNetwork = Objects.requireNonNull(objectToActionNetwork);
        this.wrapper = Objects.requireNonNull(wrapper);	
        this.actions = Arrays.copyOf(actions, actions.length);
        this.temperature = temperature;
        this.initializeOnInit = initializeOnInit;

        if (actions.length == 0) {
            throw new IllegalArgumentException("actions must not be empty.");
        }
        if (temperature <= 0.0) {
            throw new IllegalArgumentException("temperature must be > 0.");
        }

        this.inputSize = objectToActionNetwork.layerSizes()[0];
        int outputSize = objectToActionNetwork.layerSizes()[objectToActionNetwork.layerSizes().length - 1];
        if (outputSize != actions.length) {
            throw new IllegalArgumentException("Network output size must match number of actions.");
        }
    }

    @Override
    public void init(MLKAgent agent) {
        this.agent = Objects.requireNonNull(agent);
        if (initializeOnInit) {
            objectToActionNetwork.initializeParameters(agent.prng());
        }
    }

    @Override
    public MLKAgent getAgent() {
        return agent;
    }

    @Override
    public Action selectAction(PolicyInput input) {
        double[] vector = wrapper.transform(input);
        
        double[] logits;
        if(vector.length == 0) {
        	logits = defaultLogits();
        } else {
        	double[][] entities = VectorOperator.split(vector, this.inputSize);
        	logits = computeLogits(entities);
        }
        int actionIndex = sampleSoftmax(logits);
        return actions[actionIndex];
    }

    /**
     * Computes the logits for each action given the entities.
     * @param entities the input entities, each represented as a vector
     * @return the logits for each action
     */
    public double[] computeLogits(double[][] entities) {
        double[] logits = new double[actions.length];

        if (entities == null || entities.length == 0) {
            return logits;
        }

        int count = 0;

        for (double[] entity : entities) {
            if (entity == null) {
                continue;
            }

            double[] objectLogits = objectToActionNetwork.forward(entity);

            for (int a = 0; a < logits.length; a++) {
                logits[a] += objectLogits[a];
            }

            count++;
        }

        if (count > 0) {
            for (int a = 0; a < logits.length; a++) {
                logits[a] /= count;
            }
        }

        return logits;
    }

    /**
     * Samples an action index from the softmax distribution of logits.
     * @param logits the raw logits for each action
     * @return the index of the selected action
     */
    private int sampleSoftmax(double[] logits) {
        double[] probabilities = VectorOperator.softmax(logits, temperature);
        double r = prng().nextDouble();

        double cumulative = 0.0;
        for (int i = 0; i < probabilities.length; i++) {
            cumulative += probabilities[i];
            if (r <= cumulative) {
                return i;
            }
        }

        return probabilities.length - 1;
    }
    
	
	@Override
	public double[][] forwardLogits(PolicyInput[] inputs) {
	    double[][] logits = new double[inputs.length][];
	
	    for (int i = 0; i < inputs.length; i++) {
	        double[] vector = wrapper.transform(inputs[i]);
	        double[][] entities = VectorOperator.split(vector, inputSize);
	        logits[i] = computeLogits(entities);
	    }
	
	    return logits;
	}


	@Override
	public void updateFromLogitsGradient(PolicyInput[] inputs, double[][] dLossDLogits, double learningRate) {
	    for (int i = 0; i < inputs.length; i++) {
	        updateOneInput(inputs[i], dLossDLogits[i], learningRate);
	    }
	}
	

	@Override
	public int actionIndex(Action action) {
	    for (int i = 0; i < actions.length; i++) {
	        if (actions[i].equals(action)) {
	            return i;
	        }
	    }
	
	    throw new IllegalArgumentException("Unknown action.");
	}
	

	/**
	 * Updates the policy network for a single input using the provided gradient w.r.t logits.
	 * @param input the policy input
	 * @param dLossDLogits 	the gradient of the loss w.r.t the logits for this input
	 * @param learningRate the learning rate for the update
	 */
	private void updateOneInput(PolicyInput input, double[] dLossDLogits, double learningRate) {
	    double[] vector = wrapper.transform(input);
	    double[][] entities = VectorOperator.split(vector, inputSize);
	
	    if (entities.length == 0) {
	        return;
	    }
	
	    double[] entityGradient = scaledGradient(dLossDLogits, 1.0 / entities.length);
	
	    for (double[] entity : entities) {
	        objectToActionNetwork.applyOutputGradient(entity, entityGradient, learningRate);
	    }
	}
	
	/**
	 * Scales the given gradient by the specified factor.
	 * @param gradient the gradient to scale
	 * @param factor the scaling factor
	 * @return the scaled gradient
	 */
	private double[] scaledGradient(double[] gradient, double factor) {
	    double[] scaled = new double[gradient.length];
	
	    for (int i = 0; i < gradient.length; i++) {
	        scaled[i] = gradient[i] * factor;
	    }
	
	    return scaled;
	}

	@Override
	public double getSoftmaxTemperature() {
		return temperature;
	}

	/**
	 * Returns the default logits for the policy, which are uniform across all actions.
	 * @return an array of default logits, each set to 1.0 / number of actions
	 */
	protected double[] defaultLogits() {
		int nbActions = actions.length;
		
		double[] logits = new double[nbActions];
		for (int i = 0; i < nbActions; i++) {
			logits[i] = 1.0 / nbActions;
		}
		
		return logits;
	}

    
    
    
}