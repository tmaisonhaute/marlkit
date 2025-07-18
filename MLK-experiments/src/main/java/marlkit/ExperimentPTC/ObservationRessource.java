package marlkit.ExperimentPTC;

import environment.observation.Observation;

public class ObservationRessource implements Observation {

    private Ressource ressource;

    public ObservationRessource(Ressource ressource){
        this.ressource = ressource;
    }

    @Override
    public Observation add(Observation other) {
        return null;
    }

    public Ressource getRessource(){
        return this.ressource;
    }
}
