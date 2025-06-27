package environment.observation;

import java.util.ArrayList;
import java.util.List;

public class ObservationOneHotEncoding implements Observation{
    private List<Double> OHE;
    public ObservationOneHotEncoding() {
        this.OHE = null ;
    }
    public ObservationOneHotEncoding(List<Double> action) {
        this.OHE = action ;
    }
    public List<Double> getAction() {
        return OHE;
    }
    public void setAction(List<Double> action) {
        this.OHE = action ;
    }
    public Observation add(Observation obs) {
        if (!(obs instanceof ObservationOneHotEncoding)) {
            throw new IllegalArgumentException("Impossible to add a ObservationOneHotEncoding element with an element which isn't.");
        }
        List<Double> otherOHE =((ObservationOneHotEncoding) obs).OHE;
        if (this.OHE == null) {
            this.OHE = otherOHE ;
        }
        else if (!(otherOHE.size() == this.OHE.size())) {
            throw new IllegalArgumentException("Impossible to add a ObservationOneHotEncoding element to an ObservationOneHotEncoding with a different size ");
        }
        else {
            List<Double> newOHE = new ArrayList<>();
            for (int i = 0; i < OHE.size(); i++) {
                newOHE.add(OHE.get(i) + otherOHE.get(i));
            }
            this.OHE = newOHE ;
        }
        return this;
    }

    public Observation divide(int nb) {
        List<Double> newOHE = new ArrayList<>();
        for (int i = 0; i < OHE.size(); i++) {
            newOHE.add(this.OHE.get(i) /nb);
        }
        this.OHE = newOHE ;
        return this;
    }

    public Observation clone() {
        List<Double> newOHE = new ArrayList<>();
        for (int i = 0; i < OHE.size(); i++) {
            newOHE.add(this.OHE.get(i));
        }
        return new ObservationOneHotEncoding(newOHE) ;
    }
}
