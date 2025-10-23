package marlkit.uputuc;

public class ResourceQuantify {
    protected Resource type;
    protected int value;

    public ResourceQuantify(Resource type, int val) {
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

    public Resource getType() {
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
}

