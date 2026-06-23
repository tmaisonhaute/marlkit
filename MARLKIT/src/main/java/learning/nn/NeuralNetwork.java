package learning.nn;
import java.util.Arrays;
import java.util.Objects;
import java.util.random.RandomGenerator;

/**
 * A fully-connected feed-forward neural network (MLP) supporting backpropagation training.
 * <p>
 * The network is defined by layer sizes. For example, [2, 8, 1] creates an input layer of size 2,
 * one hidden layer of size 8, and an output layer of size 1.
 * </p>
 * <p>
 * This implementation focuses on clarity and good structure: each method does one thing.
 * </p>
 */
public class NeuralNetwork {

    /**
     * Activation function abstraction.
     * <p>
     * The derivative is expressed with respect to the activated output (not the pre-activation),
     * which makes backpropagation simpler and avoids storing extra values for many activations.
     * </p>
     */
    public interface Activation {
        /**
         * Applies the activation function to a scalar.
         *
         * @param x the pre-activation value
         * @return the activated value
         */
        double apply(double x);

        /**
         * Computes the derivative of the activation function using the activated value.
         *
         * @param activated the activated value
         * @return the derivative at that point
         */
        double derivativeFromActivated(double activated);
    }

    /**
     * Loss function abstraction.
     */
    public interface Loss {
        /**
         * Computes the loss value for one example.
         *
         * @param target the expected output
         * @param prediction the predicted output
         * @return the scalar loss
         */
        double value(double[] target, double[] prediction);

        /**
         * Computes the gradient of the loss with respect to the prediction (dL/da_out).
         *
         * @param target the expected output
         * @param prediction the predicted output
         * @return dL/da_out
         */
        double[] gradient(double[] target, double[] prediction);
    }

    /**
     * A training example (input, target).
     *
     * @param input input vector
     * @param target target vector
     */
    public record TrainingSample(double[] input, double[] target) { }

    /**
     * A forward pass cache used for backpropagation.
     *
     * @param activations layer activations (including input at index 0)
     * @param preActivations layer pre-activations (index 0 unused)
     */
    private record ForwardPass(double[][] activations, double[][] preActivations) { }

    /**
     * Trainable parameter gradients.
     *
     * @param dWeights weight gradients for each layer
     * @param dBiases bias gradients for each layer
     */
    private record Gradients(double[][][] dWeights, double[][] dBiases) { }

//    private final RandomGenerator prng;
    private final int[] layerSizes;
    private final double[][][] weights;
    private final double[][] biases;
    private final Activation hiddenActivation;
    private final Activation outputActivation;

    /**
     * Creates a neural network with the given architecture and activations.
     *
     * @param layerSizes layer sizes including input and output (must be length >= 2)
     * @param hiddenActivation activation for hidden layers
     * @param outputActivation activation for output layer
     * @param prng randomness source used for initialization and shuffling
     */
    public NeuralNetwork(
            int[] layerSizes,
            Activation hiddenActivation,
            Activation outputActivation
    ) {
//        this.prng = Objects.requireNonNull(prng, "prng");
        this.layerSizes = validateAndCopyLayerSizes(layerSizes);
        this.hiddenActivation = Objects.requireNonNull(hiddenActivation, "hiddenActivation");
        this.outputActivation = Objects.requireNonNull(outputActivation, "outputActivation");
        this.weights = allocateWeights(this.layerSizes);
        this.biases = allocateBiases(this.layerSizes);
//        initializeParameters();
    }

    /**
     * Creates a neural network with ReLU hidden layers and Identity output.
     *
     * @param layerSizes layer sizes including input and output
     * @param prng randomness source used for initialization and shuffling
     * @return a new neural network
     */
    public static NeuralNetwork reluIdentity(int[] layerSizes) {
        return new NeuralNetwork(layerSizes, Activations.relu(), Activations.identity());
    }

    /**
     * Computes the network prediction for a single input.
     *
     * @param input input vector
     * @return output vector
     */
    public double[] predict(double[] input) {
        return forward(input);
    }

    /**
     * Runs a forward pass and returns the output only.
     *
     * @param input input vector
     * @return output vector
     */
    public double[] forward(double[] input) {
        ForwardPass pass = forwardWithCache(input);
        return copyVector(lastLayerActivations(pass));
    }

    /**
     * Trains the network for a given number of epochs using mini-batch SGD.
     *
     * @param samples training data
     * @param epochs number of epochs
     * @param batchSize mini-batch size (must be >= 1)
     * @param learningRate learning rate (must be > 0)
     * @param loss loss function
     */
    public void fit(TrainingSample[] samples, int epochs, int batchSize, double learningRate, Loss loss, RandomGenerator prng) {
        validateFitArguments(samples, epochs, batchSize, learningRate, loss);
        int[] indices = createIndices(samples.length);

        for (int epoch = 0; epoch < epochs; epoch++) {
            shuffle(indices, prng);
            trainOneEpoch(samples, indices, batchSize, learningRate, loss);
        }
    }

    /**
     * Computes the average loss over a dataset.
     *
     * @param samples dataset
     * @param loss loss function
     * @return mean loss value
     */
    public double evaluate(TrainingSample[] samples, Loss loss) {
        validateEvaluateArguments(samples, loss);
        return computeMeanLoss(samples, loss);
    }

    /**
     * Returns a defensive copy of the architecture.
     *
     * @return layer sizes
     */
    public int[] layerSizes() {
        return Arrays.copyOf(layerSizes, layerSizes.length);
    }

    private void trainOneEpoch(TrainingSample[] samples, int[] indices, int batchSize, double learningRate, Loss loss) {
        int start = 0;
        while (start < indices.length) {
            int end = Math.min(start + batchSize, indices.length);
            trainOneBatch(samples, indices, start, end, learningRate, loss);
            start = end;
        }
    }

    private void trainOneBatch(TrainingSample[] samples, int[] indices, int start, int end, double learningRate, Loss loss) {
        Gradients sum = zeroGradients();
        accumulateBatchGradients(samples, indices, start, end, loss, sum);
        Gradients avg = scaleGradients(sum, 1.0 / (end - start));
        applyGradients(avg, learningRate);
    }

    private void accumulateBatchGradients(
            TrainingSample[] samples,
            int[] indices,
            int start,
            int end,
            Loss loss,
            Gradients accumulator
    ) {
        for (int i = start; i < end; i++) {
            TrainingSample sample = samples[indices[i]];
            Gradients g = computeGradients(sample.input(), sample.target(), loss);
            addInPlace(accumulator, g);
        }
    }

    private Gradients computeGradients(double[] input, double[] target, Loss loss) {
        validateInputSize(input);
        validateTargetSize(target);
        ForwardPass pass = forwardWithCache(input);
        double[] dLossDa = loss.gradient(target, lastLayerActivations(pass));
        double[][] deltas = backpropagateDeltas(pass, dLossDa);
        return computeParameterGradients(pass, deltas);
    }

    private ForwardPass forwardWithCache(double[] input) {
        double[][] activations = allocateActivations();
        double[][] preActivations = allocatePreActivations();
        setInputActivations(activations, input);
        computeForwardLayers(activations, preActivations);
        return new ForwardPass(activations, preActivations);
    }

    private void computeForwardLayers(double[][] activations, double[][] preActivations) {
        for (int layer = 1; layer < layerSizes.length; layer++) {
            computeLayerPreActivations(layer, activations, preActivations);
            computeLayerActivations(layer, activations, preActivations);
        }
    }

    private void computeLayerPreActivations(int layer, double[][] activations, double[][] preActivations) {
        double[] prev = activations[layer - 1];
        double[] z = preActivations[layer];
        double[][] w = weights[layer - 1];
        double[] b = biases[layer - 1];
        fillPreActivations(prev, w, b, z);
    }

    private void computeLayerActivations(int layer, double[][] activations, double[][] preActivations) {
        Activation act = activationForLayer(layer);
        applyActivation(preActivations[layer], activations[layer], act);
    }

    private double[][] backpropagateDeltas(ForwardPass pass, double[] dLossDaOut) {
        double[][] activations = pass.activations();
        double[][] deltas = allocateDeltas();

        int lastLayer = layerSizes.length - 1;
        computeOutputDelta(activations[lastLayer], dLossDaOut, deltas[lastLayer], outputActivation);

        for (int layer = lastLayer - 1; layer >= 1; layer--) {
            Activation act = activationForLayer(layer);
            computeHiddenDelta(layer, activations, deltas, act);
        }
        return deltas;
    }

    private void computeOutputDelta(double[] aOut, double[] dLossDaOut, double[] deltaOut, Activation act) {
        fillOutputDelta(aOut, dLossDaOut, deltaOut, act);
    }

    private void computeHiddenDelta(int layer, double[][] activations, double[][] deltas, Activation act) {
        double[] a = activations[layer];
        double[] nextDelta = deltas[layer + 1];
        double[][] nextWeights = weights[layer];
        double[] delta = deltas[layer];
        fillHiddenDelta(a, nextWeights, nextDelta, delta, act);
    }

    private Gradients computeParameterGradients(ForwardPass pass, double[][] deltas) {
        double[][] activations = pass.activations();
        double[][][] dW = allocateWeights(layerSizes);
        double[][] dB = allocateBiases(layerSizes);

        for (int layer = 1; layer < layerSizes.length; layer++) {
            computeLayerGradients(layer, activations, deltas, dW[layer - 1], dB[layer - 1]);
        }
        return new Gradients(dW, dB);
    }

    private void computeLayerGradients(int layer, double[][] activations, double[][] deltas, double[][] dW, double[] dB) {
        double[] prevA = activations[layer - 1];
        double[] delta = deltas[layer];
        fillWeightGradients(prevA, delta, dW);
        fillBiasGradients(delta, dB);
    }

    private void applyGradients(Gradients gradients, double learningRate) {
        applyWeightUpdates(gradients.dWeights(), learningRate);
        applyBiasUpdates(gradients.dBiases(), learningRate);
    }

    private void applyWeightUpdates(double[][][] dW, double learningRate) {
        for (int layer = 0; layer < weights.length; layer++) {
            updateMatrix(weights[layer], dW[layer], learningRate);
        }
    }

    private void applyBiasUpdates(double[][] dB, double learningRate) {
        for (int layer = 0; layer < biases.length; layer++) {
            updateVector(biases[layer], dB[layer], learningRate);
        }
    }

    public void initializeParameters(RandomGenerator prng) {
        for (int layer = 0; layer < weights.length; layer++) {
            initializeLayerWeights(layer, prng);
            initializeLayerBiases(layer);
        }
    }

    private void initializeLayerWeights(int layer, RandomGenerator prng) {
        int fanIn = layerSizes[layer];
        double std = heStdDev(fanIn);
        fillGaussian(weights[layer], std, prng);
    }

    private void initializeLayerBiases(int layer) {
        fillZeros(biases[layer]);
    }

    private void shuffle(int[] indices, RandomGenerator prng) {
        for (int i = indices.length - 1; i > 0; i--) {
            int j = prng.nextInt(i + 1);
            swap(indices, i, j);
        }
    }

    private double computeMeanLoss(TrainingSample[] samples, Loss loss) {
        double sum = 0.0;
        for (TrainingSample s : samples) {
            double[] pred = forward(s.input());
            sum += loss.value(s.target(), pred);
        }
        return sum / samples.length;
    }

    private Activation activationForLayer(int layer) {
        return (layer == layerSizes.length - 1) ? outputActivation : hiddenActivation;
    }

    private double[] lastLayerActivations(ForwardPass pass) {
        double[][] a = pass.activations();
        return a[a.length - 1];
    }

    private int[] validateAndCopyLayerSizes(int[] sizes) {
        Objects.requireNonNull(sizes, "layerSizes");
        if (sizes.length < 2) {
            throw new IllegalArgumentException("layerSizes must contain at least input and output layers.");
        }
        for (int v : sizes) {
            if (v <= 0) {
                throw new IllegalArgumentException("All layer sizes must be positive.");
            }
        }
        return Arrays.copyOf(sizes, sizes.length);
    }

    private void validateFitArguments(TrainingSample[] samples, int epochs, int batchSize, double learningRate, Loss loss) {
        Objects.requireNonNull(samples, "samples");
        Objects.requireNonNull(loss, "loss");
        if (samples.length == 0) {
            throw new IllegalArgumentException("samples must not be empty.");
        }
        if (epochs <= 0) {
            throw new IllegalArgumentException("epochs must be > 0.");
        }
        if (batchSize <= 0) {
            throw new IllegalArgumentException("batchSize must be >= 1.");
        }
        if (!(learningRate > 0.0)) {
            throw new IllegalArgumentException("learningRate must be > 0.");
        }
        validateSamplesDimensions(samples);
    }

    private void validateEvaluateArguments(TrainingSample[] samples, Loss loss) {
        Objects.requireNonNull(samples, "samples");
        Objects.requireNonNull(loss, "loss");
        if (samples.length == 0) {
            throw new IllegalArgumentException("samples must not be empty.");
        }
        validateSamplesDimensions(samples);
    }

    private void validateSamplesDimensions(TrainingSample[] samples) {
        for (TrainingSample s : samples) {
            validateInputSize(s.input());
            validateTargetSize(s.target());
        }
    }

    private void validateInputSize(double[] input) {
        Objects.requireNonNull(input, "input");
        if (input.length != layerSizes[0]) {
            throw new IllegalArgumentException("Input size mismatch: expected " + layerSizes[0] + " but got " + input.length);
        }
    }

    private void validateTargetSize(double[] target) {
        Objects.requireNonNull(target, "target");
        int out = layerSizes[layerSizes.length - 1];
        if (target.length != out) {
            throw new IllegalArgumentException("Target size mismatch: expected " + out + " but got " + target.length);
        }
    }

    private int[] createIndices(int n) {
        int[] idx = new int[n];
        for (int i = 0; i < n; i++) {
            idx[i] = i;
        }
        return idx;
    }

    private void swap(int[] a, int i, int j) {
        int t = a[i];
        a[i] = a[j];
        a[j] = t;
    }

    private double[][][] allocateWeights(int[] sizes) {
        int layers = sizes.length - 1;
        double[][][] w = new double[layers][][];
        for (int i = 0; i < layers; i++) {
            w[i] = new double[sizes[i + 1]][sizes[i]];
        }
        return w;
    }

    private double[][] allocateBiases(int[] sizes) {
        int layers = sizes.length - 1;
        double[][] b = new double[layers][];
        for (int i = 0; i < layers; i++) {
            b[i] = new double[sizes[i + 1]];
        }
        return b;
    }

    private double[][] allocateActivations() {
        double[][] a = new double[layerSizes.length][];
        for (int i = 0; i < layerSizes.length; i++) {
            a[i] = new double[layerSizes[i]];
        }
        return a;
    }

    private double[][] allocatePreActivations() {
        double[][] z = new double[layerSizes.length][];
        z[0] = new double[layerSizes[0]];
        for (int i = 1; i < layerSizes.length; i++) {
            z[i] = new double[layerSizes[i]];
        }
        return z;
    }

    private double[][] allocateDeltas() {
        double[][] d = new double[layerSizes.length][];
        d[0] = new double[layerSizes[0]];
        for (int i = 1; i < layerSizes.length; i++) {
            d[i] = new double[layerSizes[i]];
        }
        return d;
    }

    private void setInputActivations(double[][] activations, double[] input) {
        copyInto(input, activations[0]);
    }

    private void copyInto(double[] src, double[] dst) {
        System.arraycopy(src, 0, dst, 0, src.length);
    }

    private void fillPreActivations(double[] prev, double[][] w, double[] b, double[] out) {
        Arrays.fill(out, 0.0);
        addMatrixVectorProduct(w, prev, out);
        addInPlace(out, b);
    }

    private void addMatrixVectorProduct(double[][] w, double[] x, double[] out) {
        for (int i = 0; i < w.length; i++) {
            out[i] += dot(w[i], x);
        }
    }

    private double dot(double[] a, double[] b) {
        double s = 0.0;
        for (int i = 0; i < a.length; i++) {
            s += a[i] * b[i];
        }
        return s;
    }

    private void addInPlace(double[] a, double[] b) {
        for (int i = 0; i < a.length; i++) {
            a[i] += b[i];
        }
    }

    private void applyActivation(double[] z, double[] a, Activation activation) {
        for (int i = 0; i < z.length; i++) {
            a[i] = activation.apply(z[i]);
        }
    }

    private void fillOutputDelta(double[] aOut, double[] dLossDaOut, double[] deltaOut, Activation activation) {
        for (int i = 0; i < aOut.length; i++) {
            deltaOut[i] = dLossDaOut[i] * activation.derivativeFromActivated(aOut[i]);
        }
    }

    private void fillHiddenDelta(double[] a, double[][] nextW, double[] nextDelta, double[] delta, Activation activation) {
        for (int i = 0; i < delta.length; i++) {
            double propagated = weightedDeltaSum(nextW, nextDelta, i);
            delta[i] = propagated * activation.derivativeFromActivated(a[i]);
        }
    }

    private double weightedDeltaSum(double[][] nextW, double[] nextDelta, int neuronIndex) {
        double s = 0.0;
        for (int j = 0; j < nextW.length; j++) {
            s += nextW[j][neuronIndex] * nextDelta[j];
        }
        return s;
    }

    private void fillWeightGradients(double[] prevA, double[] delta, double[][] dW) {
        for (int i = 0; i < dW.length; i++) {
            outerProductRow(delta[i], prevA, dW[i]);
        }
    }

    private void outerProductRow(double scalar, double[] vector, double[] outRow) {
        for (int j = 0; j < vector.length; j++) {
            outRow[j] = scalar * vector[j];
        }
    }

    private void fillBiasGradients(double[] delta, double[] dB) {
        copyInto(delta, dB);
    }

    private Gradients zeroGradients() {
        return new Gradients(allocateWeights(layerSizes), allocateBiases(layerSizes));
    }

    private void addInPlace(Gradients acc, Gradients g) {
        addWeightsInPlace(acc.dWeights(), g.dWeights());
        addBiasesInPlace(acc.dBiases(), g.dBiases());
    }

    private void addWeightsInPlace(double[][][] a, double[][][] b) {
        for (int layer = 0; layer < a.length; layer++) {
            addMatrixInPlace(a[layer], b[layer]);
        }
    }

    private void addMatrixInPlace(double[][] a, double[][] b) {
        for (int i = 0; i < a.length; i++) {
            addVectorInPlace(a[i], b[i]);
        }
    }

    private void addBiasesInPlace(double[][] a, double[][] b) {
        for (int layer = 0; layer < a.length; layer++) {
            addVectorInPlace(a[layer], b[layer]);
        }
    }

    private void addVectorInPlace(double[] a, double[] b) {
        for (int i = 0; i < a.length; i++) {
            a[i] += b[i];
        }
    }

    private Gradients scaleGradients(Gradients g, double factor) {
        double[][][] w = allocateWeights(layerSizes);
        double[][] b = allocateBiases(layerSizes);
        scaleWeights(g.dWeights(), w, factor);
        scaleBiases(g.dBiases(), b, factor);
        return new Gradients(w, b);
    }

    private void scaleWeights(double[][][] src, double[][][] dst, double factor) {
        for (int layer = 0; layer < src.length; layer++) {
            scaleMatrix(src[layer], dst[layer], factor);
        }
    }

    private void scaleMatrix(double[][] src, double[][] dst, double factor) {
        for (int i = 0; i < src.length; i++) {
            scaleVector(src[i], dst[i], factor);
        }
    }

    private void scaleBiases(double[][] src, double[][] dst, double factor) {
        for (int layer = 0; layer < src.length; layer++) {
            scaleVector(src[layer], dst[layer], factor);
        }
    }

    private void scaleVector(double[] src, double[] dst, double factor) {
        for (int i = 0; i < src.length; i++) {
            dst[i] = src[i] * factor;
        }
    }

    private void updateMatrix(double[][] params, double[][] grads, double learningRate) {
        for (int i = 0; i < params.length; i++) {
            updateVector(params[i], grads[i], learningRate);
        }
    }

    private void updateVector(double[] params, double[] grads, double learningRate) {
        for (int i = 0; i < params.length; i++) {
            params[i] -= learningRate * grads[i];
        }
    }

    private void fillGaussian(double[][] matrix, double stdDev, RandomGenerator prng) {
        for (double[] row : matrix) {
            fillGaussian(row, stdDev, prng);
        }
    }

    private void fillGaussian(double[] vector, double stdDev, RandomGenerator prng) {
        for (int i = 0; i < vector.length; i++) {
            vector[i] = prng.nextGaussian() * stdDev;
        }
    }

    private void fillZeros(double[] vector) {
        Arrays.fill(vector, 0.0);
    }

    private double heStdDev(int fanIn) {
        return Math.sqrt(2.0 / fanIn);
    }

    private double[] copyVector(double[] v) {
        return Arrays.copyOf(v, v.length);
    }
    

    /* Applies a single-sample gradient step from a user-provided output gradient.
        *
        * @param input the input vector
        * @param dLossDOutput the gradient with respect to the output activations (dL/dA_out)
        * @param learningRate the learning rate (must be > 0)
        */
   public void applyOutputGradient(double[] input, double[] dLossDOutput, double learningRate) {
       validateOutputGradientArguments(input, dLossDOutput, learningRate);
       Gradients gradients = gradientsFromOutputGradient(input, dLossDOutput);
       applyGradients(gradients, learningRate);
   }

   /**
    * Applies a mini-batch gradient step from user-provided output gradients.
    *
    * @param inputs the batch inputs
    * @param dLossDOutputs the batch output gradients (dL/dA_out)
    * @param learningRate the learning rate (must be > 0)
    */
   public void applyOutputGradientBatch(double[][] inputs, double[][] dLossDOutputs, double learningRate) {
       validateOutputGradientBatchArguments(inputs, dLossDOutputs, learningRate);
       Gradients sum = accumulateOutputGradientBatch(inputs, dLossDOutputs);
       Gradients avg = scaleGradients(sum, 1.0 / inputs.length);
       applyGradients(avg, learningRate);
   }

   private void validateOutputGradientArguments(double[] input, double[] dLossDOutput, double learningRate) {
       Objects.requireNonNull(dLossDOutput, "dLossDOutput");
       validateInputSize(input);
       validateOutputGradientSize(dLossDOutput);
       validateLearningRate(learningRate);
   }

   private void validateOutputGradientBatchArguments(double[][] inputs, double[][] dLossDOutputs, double learningRate) {
       Objects.requireNonNull(inputs, "inputs");
       Objects.requireNonNull(dLossDOutputs, "dLossDOutputs");
       if (inputs.length == 0) {
           throw new IllegalArgumentException("inputs must not be empty.");
       }
       if (inputs.length != dLossDOutputs.length) {
           throw new IllegalArgumentException("Batch size mismatch between inputs and output gradients.");
       }
       for (int i = 0; i < inputs.length; i++) {
           validateOutputGradientArguments(inputs[i], dLossDOutputs[i], learningRate);
       }
   }

   private void validateOutputGradientSize(double[] dLossDOutput) {
       int out = layerSizes[layerSizes.length - 1];
       if (dLossDOutput.length != out) {
           throw new IllegalArgumentException("Output gradient size mismatch: expected " + out + " but got " + dLossDOutput.length);
       }
   }

   private void validateLearningRate(double learningRate) {
       if (!(learningRate > 0.0)) {
           throw new IllegalArgumentException("learningRate must be > 0.");
       }
   }

   private Gradients gradientsFromOutputGradient(double[] input, double[] dLossDOutput) {
       ForwardPass pass = forwardWithCache(input);
       double[][] deltas = backpropagateDeltas(pass, dLossDOutput);
       return computeParameterGradients(pass, deltas);
   }

   private Gradients accumulateOutputGradientBatch(double[][] inputs, double[][] dLossDOutputs) {
       Gradients sum = zeroGradients();
       for (int i = 0; i < inputs.length; i++) {
           Gradients g = gradientsFromOutputGradient(inputs[i], dLossDOutputs[i]);
           addInPlace(sum, g);
       }
       return sum;
   }


    /**
     * Common activation functions.
     */
    public static final class Activations {
        private Activations() { }

        /**
         * Identity activation.
         *
         * @return identity activation
         */
        public static Activation identity() {
            return new Activation() {
                @Override
                public double apply(double x) {
                    return x;
                }

                @Override
                public double derivativeFromActivated(double activated) {
                    return 1.0;
                }
            };
        }

        /**
         * ReLU activation.
         *
         * @return ReLU activation
         */
        public static Activation relu() {
            return new Activation() {
                @Override
                public double apply(double x) {
                    return Math.max(0.0, x);
                }

                @Override
                public double derivativeFromActivated(double activated) {
                    return activated > 0.0 ? 1.0 : 0.0;
                }
            };
        }

        /**
         * Sigmoid activation.
         *
         * @return sigmoid activation
         */
        public static Activation sigmoid() {
            return new Activation() {
                @Override
                public double apply(double x) {
                    return 1.0 / (1.0 + Math.exp(-x));
                }

                @Override
                public double derivativeFromActivated(double activated) {
                    return activated * (1.0 - activated);
                }
            };
        }

        /**
         * Hyperbolic tangent activation.
         *
         * @return tanh activation
         */
        public static Activation tanh() {
            return new Activation() {
                @Override
                public double apply(double x) {
                    return Math.tanh(x);
                }

                @Override
                public double derivativeFromActivated(double activated) {
                    return 1.0 - activated * activated;
                }
            };
        }
    }

    /**
     * Common loss functions.
     */
    public static final class Losses {
        private Losses() { }

        /**
         * Mean Squared Error (MSE) for a single example.
         *
         * @return MSE loss
         */
        public static Loss mse() {
            return new Loss() {
                @Override
                public double value(double[] target, double[] prediction) {
                    validateSameLength(target, prediction);
                    double s = 0.0;
                    for (int i = 0; i < target.length; i++) {
                        double d = prediction[i] - target[i];
                        s += d * d;
                    }
                    return s / target.length;
                }

                @Override
                public double[] gradient(double[] target, double[] prediction) {
                    validateSameLength(target, prediction);
                    double[] g = new double[target.length];
                    double scale = 2.0 / target.length;
                    for (int i = 0; i < target.length; i++) {
                        g[i] = scale * (prediction[i] - target[i]);
                    }
                    return g;
                }

                private void validateSameLength(double[] a, double[] b) {
                    Objects.requireNonNull(a, "target");
                    Objects.requireNonNull(b, "prediction");
                    if (a.length != b.length) {
                        throw new IllegalArgumentException("Vector length mismatch.");
                    }
                }
            };
        }
    }
    

    /**
     * Returns the total number of trainable parameters in the network (weights + biases).
     * @return total parameter count 
     */
	public int parametersCount() {
	    int count = 0;
	    for (int layer = 0; layer < weights.length; layer++) {
	        count += weights[layer].length * weights[layer][0].length;
	        count += biases[layer].length;
	    }
	    return count;
	}
	
	/**
	 * Returns a flat array of all trainable parameters (weights and biases) in the network.
	 * @return array of parameters 
	 */
	public double[] getParameters() {
	    double[] parameters = new double[parametersCount()];
	    int index = 0;
	
	    for (int layer = 0; layer < weights.length; layer++) {
	        for (int i = 0; i < weights[layer].length; i++) {
	            for (int j = 0; j < weights[layer][i].length; j++) {
	                parameters[index++] = weights[layer][i][j];
	            }
	        }
	
	        for (int i = 0; i < biases[layer].length; i++) {
	            parameters[index++] = biases[layer][i];
	        }
	    }
	
	    return parameters;
	}
	
	/**
	 * Sets the network parameters from a flat array. The array must match the total parameter count.
	 * @param parameters flat array of parameters (weights and biases)
	 */
	public void setParameters(double[] parameters) {
	    Objects.requireNonNull(parameters, "parameters");
	
	    if (parameters.length != parametersCount()) {
	        throw new IllegalArgumentException("Parameter count mismatch: expected " + parametersCount() + " but got " + parameters.length);
	    }
	
	    int index = 0;
	
	    for (int layer = 0; layer < weights.length; layer++) {
	        for (int i = 0; i < weights[layer].length; i++) {
	            for (int j = 0; j < weights[layer][i].length; j++) {
	                weights[layer][i][j] = parameters[index++];
	            }
	        }
	
	        for (int i = 0; i < biases[layer].length; i++) {
	            biases[layer][i] = parameters[index++];
	        }
	    }
}

}

