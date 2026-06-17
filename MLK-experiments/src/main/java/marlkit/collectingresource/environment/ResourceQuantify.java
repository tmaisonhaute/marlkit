package marlkit.collectingresource.environment;

import java.util.Objects;

public class ResourceQuantify {
	protected ResourceType type;
    protected int value;

    public ResourceQuantify(ResourceType type, int val) {
        this.type = type;
        this.value = val;
    }

    @Override
    public String toString() {
        return "Stock{" +
                "type=" + type +
                ", nb=" + value +
                '}';
    }

    public ResourceType getType() {
        return type;
    }

    public int getValue() {
        return value;
    }
    
	public void addValue(int val) {
		this.value += val;
	}
	
	public void setValue(int val) {
		this.value = val;
	}

    public ResourceQuantify copy() {
        return new ResourceQuantify(type, value);
    }
    
    public int[] toOneHotEncoding() {
        int[] encoding = new int[ResourceType.values().length];
        encoding[type.ordinal()] = value;
        return encoding;
    }

    @Override
	public int hashCode() {
		return Objects.hash(type, value);
	}

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj instanceof ResourceQuantify rq) {
            return this.type == rq.type && this.value == rq.value;
        }
        return false;
    }
}