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
    public void add(Observation other) {
    }

    public Resource getRessource(){
        return this.ressource;
    }

	@Override
	public Observation copy() {
		return new ObservationRessource(this.ressource);
	}
}
