package marlkit.ExperimentPTC;

public class RessourceQuantify {
    protected Ressource type;
    public int value;

    public RessourceQuantify(Ressource type, int val) {
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

    public Ressource getType() {
        return type;
    }

    public int getValue() {
        return value;
    }

    public RessourceQuantify copy() {
        return new RessourceQuantify(type, value);
    }
}

