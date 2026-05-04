package marlkit.trade;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import environment.observation.Observation;

public class ObservationUnites implements Observation {
	private final List<UniteProduction> unites;

	public ObservationUnites(List<UniteProduction> unites) {
		this.unites = unites;
	}

	@Override
	public Observation add(Observation other) {
		if (other instanceof ObservationUnites o ){
			List<UniteProduction> newUnites = new ArrayList<>();
			newUnites.addAll(this.unites);
			newUnites.addAll(o.unites);
			return new ObservationUnites(newUnites);
		}else{
			throw new IllegalArgumentException("Impossible to add a ObservationUnites element with an element which isn't.");
		}
	}

	public ResourceType getResourceType(UniteProduction unite) {
		return unite.getResourceType();
	}


	@Override
	public int hashCode() {
		return Objects.hash(unites);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj instanceof ObservationUnites obsu) {
			return Objects.equals(this.unites, obsu.unites);
		}
		return false;
	}

	@Override
	public Observation copy() {
		return new ObservationUnites(new ArrayList<>(this.unites));
	}

}
