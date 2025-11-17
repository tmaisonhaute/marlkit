package marlkit.uputuc;

import environment.observation.Observation;

/**
 * Observation wrapping a Resource.
 */
public class ObservationRessource implements Observation {

    private Resource ressource;

    public ObservationRessource(Resource ressource){
        this.ressource = ressource;
    }

    @Override
    public Observation add(Observation other) {
        return null;
    }

    public Resource getRessource(){
        return this.ressource;
    }
}
