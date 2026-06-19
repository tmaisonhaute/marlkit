package learning.policies;

import java.util.Arrays;
import java.util.Objects;

import agent.MLKAgent;
import agent.action.Action;
import environment.observation.wrapperobservationvector.WrapperPolicyInputVector;
import learning.Policy;
import learning.nn.NeuralNetwork;
import util.VectorOperator;

public class ObjectVotingNeuralPolicy implements Policy {


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
        double[][] entities = VectorOperator.split(vector, this.inputSize);
        double[] logits = computeLogits(entities);
        int actionIndex = sampleSoftmax(logits);
        return actions[actionIndex];
    }

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

    
    
    
}