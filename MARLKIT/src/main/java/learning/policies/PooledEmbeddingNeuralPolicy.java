package learning.policies;

import java.util.Arrays;
import java.util.Objects;

import agent.MLKAgent;
import agent.action.Action;
import environment.observation.wrapperobservationvector.WrapperPolicyInputVector;
import learning.Policy;
import learning.nn.NeuralNetwork;
import util.VectorOperator;

public class PooledEmbeddingNeuralPolicy implements Policy {

    private MLKAgent agent;

    private final NeuralNetwork entityEncoder;
    private final NeuralNetwork policyHead;
    private final WrapperPolicyInputVector wrapper;

    private final Action[] actions;
    private final double temperature;
    private final boolean initializeOnInit;

    private final int entityInputSize;
    private final int embeddingSize;

    public PooledEmbeddingNeuralPolicy(
            NeuralNetwork entityEncoder,
            NeuralNetwork policyHead,
            WrapperPolicyInputVector wrapper,
            Action[] actions,
            double temperature,
            boolean initializeOnInit
    ) {
        this.entityEncoder = Objects.requireNonNull(entityEncoder);
        this.policyHead = Objects.requireNonNull(policyHead);
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

        this.entityInputSize = entityEncoder.layerSizes()[0];
        this.embeddingSize = entityEncoder.layerSizes()[entityEncoder.layerSizes().length - 1];

        int policyInputSize = policyHead.layerSizes()[0];
        int expected = 2 * embeddingSize;

        if (policyInputSize != expected) {
            throw new IllegalArgumentException("policyHead input must be 2 * embedding size.");
        }

        int policyOutputSize = policyHead.layerSizes()[policyHead.layerSizes().length - 1];
        if (policyOutputSize != actions.length) {
            throw new IllegalArgumentException("policyHead output must match number of actions.");
        }
    }

    @Override
    public void init(MLKAgent agent) {
        this.agent = Objects.requireNonNull(agent);
        if (initializeOnInit) {
            entityEncoder.initializeParameters(agent.prng());
            policyHead.initializeParameters(agent.prng());
        }
    }

    @Override
    public MLKAgent getAgent() {
        return agent;
    }

    @Override
    public Action selectAction(PolicyInput input) {
        double[] vector = wrapper.transform(input);
        double[][] entities = split(vector);
        double[] logits = computeLogits(entities);
        int index = sampleSoftmax(logits);
        return actions[index];
    }

    public double[] computeLogits(double[][] entities) {
        double[] pooled = encodeEntities(entities);
        return policyHead.forward(pooled);
    }

    /**
     * Encodes each entity and applies pooling.
     */
    private double[] encodeEntities(double[][] entities) {
        if (entities == null || entities.length == 0) {
            return emptyEmbedding();
        }

        return poolEmbeddings(entities);
    }

    /**
     * Applies mean and max pooling over entity embeddings.
     */
    private double[] poolEmbeddings(double[][] entities) {
        double[] mean = new double[embeddingSize];
        double[] max = initMax();

        int count = 0;

        for (double[] entity : entities) {
            if (entity == null) {
                continue;
            }

            double[] emb = encodeEntity(entity);
            accumulate(mean, max, emb);
            count++;
        }

        if (count == 0) {
            return emptyEmbedding();
        }

        normalize(mean, count);
        return VectorOperator.concatenate(mean, max);
    }

    /**
     * Encodes a single entity.
     */
    private double[] encodeEntity(double[] entity) {
        return entityEncoder.forward(entity);
    }

    /**
     * Initializes max pooling vector.
     */
    private double[] initMax() {
        double[] max = new double[embeddingSize];
        Arrays.fill(max, Double.NEGATIVE_INFINITY);
        return max;
    }

    /**
     * Updates mean and max accumulators.
     */
    private void accumulate(double[] mean, double[] max, double[] emb) {
        for (int i = 0; i < embeddingSize; i++) {
            mean[i] += emb[i];
            max[i] = Math.max(max[i], emb[i]);
        }
    }

    /**
     * Normalizes mean embedding.
     */
    private void normalize(double[] mean, int count) {
        for (int i = 0; i < mean.length; i++) {
            mean[i] /= count;
        }
    }

    /**
     * Returns embedding for empty observation.
     */
    private double[] emptyEmbedding() {
        double[] zero = new double[embeddingSize];
        return VectorOperator.concatenate(zero, zero);
    }

    /**
     * Splits vector into entities.
     */
    private double[][] split(double[] vector) {
        return VectorOperator.split(vector, entityInputSize);
    }

    /**
     * Samples an action using softmax.
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

    
}