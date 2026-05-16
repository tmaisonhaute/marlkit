package evaluation;

import util.grafana.Extra;

/**
 * Represents a system-level metric computed during evaluation.
 */
public class Measure implements Extra {
	protected String name;
	protected Double value;

	public Measure(String name, Double value) {
		this.name = name;
		this.value = value;
	}

	public Measure(String name) {
		this(name, null);
	}

	public Measure() {
		this(null, null);
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public void setValue(Double value) {
		this.value = value;
	}
	
	public void addValue(double v) {
		if (this.value == null) {
			this.value = v;
		} else {
			this.value += v;
		}
	}
	
	@Override
	public String toString() {
		return name;
	}

	@Override
	public double toDouble() {
		return value != null ? value : 0.0;
	}
	
	
}
