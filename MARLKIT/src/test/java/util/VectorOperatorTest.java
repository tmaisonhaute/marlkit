package util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.assertj.core.data.Offset;
import org.testng.annotations.Test;

public class VectorOperatorTest {

    private static final double EPSILON = 1e-12;

    @Test
    public void givenVectorAndObjectSize_whenSplit_thenExpectedMatrixReturned() {
        double[] vector = new double[] {1.0, 2.0, 3.0, 4.0, 5.0, 6.0};

        double[][] result = VectorOperator.split(vector, 3);

        assertThat(result.length).isEqualTo(2);
        assertThat(result[0]).containsExactly(1.0, 2.0, 3.0);
        assertThat(result[1]).containsExactly(4.0, 5.0, 6.0);
    }

    @Test
    public void givenEmptyVector_whenSplit_thenEmptyMatrixReturned() {
        double[] vector = new double[0];

        double[][] result = VectorOperator.split(vector, 3);

        assertThat(result.length).isEqualTo(0);
    }

    @Test
    public void givenObjectSizeOne_whenSplit_thenEachValueBecomesOneRow() {
        double[] vector = new double[] {1.0, 2.0, 3.0};

        double[][] result = VectorOperator.split(vector, 1);

        assertThat(result.length).isEqualTo(3);
        assertThat(result[0]).containsExactly(1.0);
        assertThat(result[1]).containsExactly(2.0);
        assertThat(result[2]).containsExactly(3.0);
    }

    @Test
    public void givenNonDivisibleVector_whenSplit_thenRemainingValuesAreIgnored() {
        double[] vector = new double[] {1.0, 2.0, 3.0, 4.0};

        double[][] result = VectorOperator.split(vector, 3);

        assertThat(result.length).isEqualTo(1);
        assertThat(result[0]).containsExactly(1.0, 2.0, 3.0);
    }

    @Test
    public void givenZeroObjectSize_whenSplit_thenThrows() {
        double[] vector = new double[] {1.0, 2.0};

        assertThatThrownBy(() -> VectorOperator.split(vector, 0))
            .isInstanceOf(ArithmeticException.class);
    }

    @Test
    public void givenSimpleLogits_whenSoftmax_thenProbabilitiesSumToOne() {
        double[] logits = new double[] {1.0, 2.0, 3.0};

        double[] result = VectorOperator.softmax(logits, 1.0);

        assertThat(result).hasSize(3);
        assertThat(sum(result)).isCloseTo(1.0, Offset.offset(EPSILON));
    }

    @Test
    public void givenSimpleLogits_whenSoftmax_thenHighestLogitHasHighestProbability() {
        double[] logits = new double[] {1.0, 2.0, 3.0};

        double[] result = VectorOperator.softmax(logits, 1.0);

        assertThat(result[2]).isGreaterThan(result[1]);
        assertThat(result[1]).isGreaterThan(result[0]);
    }

    @Test
    public void givenEqualLogits_whenSoftmax_thenUniformDistributionReturned() {
        double[] logits = new double[] {5.0, 5.0, 5.0, 5.0};

        double[] result = VectorOperator.softmax(logits, 1.0);

        assertThat(result[0]).isCloseTo(0.25, Offset.offset(EPSILON));
        assertThat(result[1]).isCloseTo(0.25, Offset.offset(EPSILON));
        assertThat(result[2]).isCloseTo(0.25, Offset.offset(EPSILON));
        assertThat(result[3]).isCloseTo(0.25, Offset.offset(EPSILON));
    }

    @Test
    public void givenLargePositiveLogits_whenSoftmax_thenProbabilitiesAreFinite() {
        double[] logits = new double[] {1000.0, 1001.0, 1002.0};

        double[] result = VectorOperator.softmax(logits, 1.0);

        assertThat(sum(result)).isCloseTo(1.0, Offset.offset(EPSILON));
        for (double value : result) {
            assertThat(value).isFinite();
        }
    }

    @Test
    public void givenLargeNegativeLogits_whenSoftmax_thenProbabilitiesAreFinite() {
        double[] logits = new double[] {-1000.0, -1001.0, -1002.0};

        double[] result = VectorOperator.softmax(logits, 1.0);

        assertThat(sum(result)).isCloseTo(1.0, Offset.offset(EPSILON));
        for (double value : result) {
            assertThat(value).isFinite();
        }
    }

    @Test
    public void givenLowTemperature_whenSoftmax_thenDistributionIsSharper() {
        double[] logits = new double[] {1.0, 2.0, 3.0};

        double[] normal = VectorOperator.softmax(logits, 1.0);
        double[] sharp = VectorOperator.softmax(logits, 0.1);

        assertThat(sharp[2]).isGreaterThan(normal[2]);
        assertThat(sharp[0]).isLessThan(normal[0]);
    }

    @Test
    public void givenHighTemperature_whenSoftmax_thenDistributionIsFlatter() {
        double[] logits = new double[] {1.0, 2.0, 3.0};

        double[] normal = VectorOperator.softmax(logits, 1.0);
        double[] flat = VectorOperator.softmax(logits, 10.0);

        assertThat(flat[2]).isLessThan(normal[2]);
        assertThat(flat[0]).isGreaterThan(normal[0]);
    }

    @Test
    public void givenSingleLogit_whenSoftmax_thenProbabilityIsOne() {
        double[] logits = new double[] {42.0};

        double[] result = VectorOperator.softmax(logits, 1.0);

        assertThat(result).containsExactly(1.0);
    }

    @Test
    public void givenEmptyLogits_whenSoftmax_thenEmptyArrayReturned() {
        double[] logits = new double[0];

        double[] result = VectorOperator.softmax(logits, 1.0);

        assertThat(result).isEmpty();
    }

    @Test
    public void givenZeroTemperature_whenSoftmax_thenProducesInvalidValues() {
        double[] logits = new double[] {1.0, 2.0};

        double[] result = VectorOperator.softmax(logits, 0.0);

        assertThat(result.length).isEqualTo(2);
        assertThat(Double.isNaN(result[0]) || Double.isNaN(result[1]) || Double.isInfinite(result[0]) || Double.isInfinite(result[1])).isTrue();
    }

    @Test
    public void givenBatchLogits_whenSoftmax_thenEachRowIsProbabilityDistribution() {
        double[][] logits = new double[][] {
            {1.0, 2.0, 3.0},
            {-1.0, 0.0, 1.0}
        };

        double[][] result = VectorOperator.softmax(logits, 1.0);

        assertThat(result.length).isEqualTo(2);
        assertThat(result[0]).hasSize(3);
        assertThat(result[1]).hasSize(3);
        assertThat(sum(result[0])).isCloseTo(1.0, Offset.offset(EPSILON));
        assertThat(sum(result[1])).isCloseTo(1.0, Offset.offset(EPSILON));
    }

    @Test
    public void givenEmptyBatch_whenSoftmax_thenEmptyBatchReturned() {
        double[][] logits = new double[0][];

        double[][] result = VectorOperator.softmax(logits, 1.0);

        assertThat(result.length).isEqualTo(0);
    }

    @Test
    public void givenTwoVectors_whenConcatenate_thenExpectedVectorReturned() {
        double[] first = new double[] {1.0, 2.0};
        double[] second = new double[] {3.0, 4.0, 5.0};

        double[] result = VectorOperator.concatenate(first, second);

        assertThat(result).containsExactly(1.0, 2.0, 3.0, 4.0, 5.0);
    }

    @Test
    public void givenFirstVectorEmpty_whenConcatenate_thenSecondVectorReturned() {
        double[] first = new double[0];
        double[] second = new double[] {1.0, 2.0};

        double[] result = VectorOperator.concatenate(first, second);

        assertThat(result).containsExactly(1.0, 2.0);
    }

    @Test
    public void givenSecondVectorEmpty_whenConcatenate_thenFirstVectorReturned() {
        double[] first = new double[] {1.0, 2.0};
        double[] second = new double[0];

        double[] result = VectorOperator.concatenate(first, second);

        assertThat(result).containsExactly(1.0, 2.0);
    }

    private double sum(double[] values) {
        double sum = 0.0;

        for (double value : values) {
            sum += value;
        }

        return sum;
    }
}
