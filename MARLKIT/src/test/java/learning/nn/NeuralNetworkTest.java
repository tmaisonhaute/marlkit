package learning.nn;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Random;

import org.testng.annotations.Test;

public class NeuralNetworkTest {

    @Test
    public void givenSimpleInput_whenForward_thenOutputIsComputed() {
        // Given
    	Random random = new Random(12345);
        NeuralNetwork1 network = new NeuralNetwork1(2, 4, 1, random, 0.1);
        double[] input = {0.5, 0.8};
        
        // When
        double[] output = network.forward(input);
        
        // Then
        assertThat(output).hasSize(1);
        assertThat(output[0]).isNotNull();
    }
    
    @Test
    public void givenInputAndTarget_whenUpdate_thenErrorDecreases() {
        // Given
    	Random random = new Random(12345);
        NeuralNetwork1 network = new NeuralNetwork1(2, 8, 1, random, 0.1);
        double[] input = {0.5, 0.8};
        double[] target = {0.7};
        
        // When
        double[] initialOutput = network.forward(input);
        double initialError = Math.abs(target[0] - initialOutput[0]);
        
        // Train the network for several iterations
        for (int i = 0; i < 1000; i++) {
            network.update(input, target);
        }
        
        double[] finalOutput = network.forward(input);
        double finalError = Math.abs(target[0] - finalOutput[0]);
        
        // Then
        assertThat(finalError).isLessThan(initialError);
    }
    
//    @Test
//    public void givenMultipleExamples_whenTrained_thenNetworkLearnsMapping() {
//        // Given
//    	Random random = new Random(12345);
//        NeuralNetwork network = new NeuralNetwork(2, 8, 1, random, 0.1);
//        double[][] inputs = {
//            {0.0, 0.0},
//            {0.0, 1.0},
//            {1.0, 0.0},
//            {1.0, 1.0}
//        };
//        double[][] targets = {
//            {0.0}, // 0 XOR 0 = 0
//            {1.0}, // 0 XOR 1 = 1
//            {1.0}, // 1 XOR 0 = 1
//            {0.0}  // 1 XOR 1 = 0
//        };
//        
//        // When: train the network for many iterations
//        for (int epoch = 0; epoch < 500; epoch++) {
//            for (int i = 0; i < inputs.length; i++) {
//                network.update(inputs[i], targets[i]);
//            }
//        }
//        
//        // Then: network should have learned the XOR function
//        for (int i = 0; i < inputs.length; i++) {
//            double[] output = network.forward(inputs[i]);
//            double prediction = output[0] > 0.5 ? 1.0 : 0.0;
//            assertThat(prediction).isEqualTo(targets[i][0]);
//        }
//    }
    
    @Test
    public void givenValue_whenReluApplied_thenCorrectOutputReturned() {
        // Given
    	Random random = new Random(12345);
        NeuralNetwork1 network = new NeuralNetwork1(1, 1, 1, random, 0.1);
        
        // Define a method to access the private relu method
        java.lang.reflect.Method reluMethod;
        try {
            reluMethod = NeuralNetwork1.class.getDeclaredMethod("relu", double.class);
            reluMethod.setAccessible(true);
            
            // When/Then
            assertThat((double)reluMethod.invoke(network, 5.0)).isEqualTo(5.0);
            assertThat((double)reluMethod.invoke(network, -3.0)).isEqualTo(0.0);
            assertThat((double)reluMethod.invoke(network, 0.0)).isEqualTo(0.0);
            
        } catch (Exception e) {
            assertThat(true).isFalse(); // Force test to fail if exception occurs
        }
    }
    
    @Test
    public void givenValue_whenReluDerivativeApplied_thenCorrectOutputReturned() {
        // Given
    	Random random = new Random(12345);
        NeuralNetwork1 network = new NeuralNetwork1(1, 1, 1, random, 0.1);
        
        // Define a method to access the private reluDerivative method
        java.lang.reflect.Method reluDerivativeMethod;
        try {
            reluDerivativeMethod = NeuralNetwork1.class.getDeclaredMethod("reluDerivative", double.class);
            reluDerivativeMethod.setAccessible(true);
            
            // When/Then
            assertThat((double)reluDerivativeMethod.invoke(network, 5.0)).isEqualTo(1.0);
            assertThat((double)reluDerivativeMethod.invoke(network, -3.0)).isEqualTo(0.0);
            assertThat((double)reluDerivativeMethod.invoke(network, 0.0)).isEqualTo(0.0);
            
        } catch (Exception e) {
            assertThat(true).isFalse(); // Force test to fail if exception occurs
        }
    }
    
    @Test
    public void givenNetwork_whenLearningRateChanged_thenLearningRateIsUpdated() {
        // Given
    	Random random = new Random(12345);
        NeuralNetwork1 network = new NeuralNetwork1(2, 4, 1, random, 0.1);
        
        // When
        network.setLearningRate(0.05);
        
        // Then - we can only verify indirectly by checking learning behavior
        double[] input = {0.5, 0.8};
        double[] target = {0.7};
        double[] initialOutput = network.forward(input);
        
        for (int i = 0; i < 10; i++) {
            network.update(input, target);
        }
        
        double[] finalOutput = network.forward(input);
        double change = Math.abs(finalOutput[0] - initialOutput[0]);
        
        // With a smaller learning rate, the change should be detectable but not too large
        assertThat(change).isGreaterThan(0.0);
    }
}
