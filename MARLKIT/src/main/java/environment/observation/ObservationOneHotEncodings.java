package environment.observation;

import java.util.ArrayList;
import java.util.List;

public class ObservationOneHotEncodings implements Observation {
    private List<ObservationOneHotEncoding> oneHotEncodings;

    public ObservationOneHotEncodings(List<ObservationOneHotEncoding> oneHotEncodings) {
        this.oneHotEncodings = oneHotEncodings;
    }
    public ObservationOneHotEncodings() {
        this.oneHotEncodings = new ArrayList<>();
    }
    public List<ObservationOneHotEncoding> getOneHotEncodings() {
        return oneHotEncodings;
    }
    public void setOneHotEncodings(List<ObservationOneHotEncoding> oneHotEncodings) {
        this.oneHotEncodings = oneHotEncodings;
    }

    public Observation add(Observation obs) {
        if (!(obs instanceof ObservationOneHotEncoding)) {
            throw new IllegalArgumentException("Impossible to add a OneHotEncoding element with an element which isn't.");
        }
        oneHotEncodings.add((ObservationOneHotEncoding) obs);
        return this;
    }

    public Observation getAverage(){
        ObservationOneHotEncoding avg = new ObservationOneHotEncoding();
        for (ObservationOneHotEncoding OHE : oneHotEncodings) {
            avg.add(OHE);
        }
        avg.divide(oneHotEncodings.size());
        return avg;
    }

}


