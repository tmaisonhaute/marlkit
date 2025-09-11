package marlkit.uputuc;

public class Storage extends RessourceQuantify {

    private int maxStorage;

    public Storage(Ressource type, int quantity, int maxStorage){
        super(type,quantity);
        this.maxStorage = maxStorage;
    }

    public Storage(Ressource type, int quantity){
        this(type, quantity, 100);
    }

    public int getMaxStorage(){return this.maxStorage;}

    public void add(int nb){
        this.value += nb;
    }

}
