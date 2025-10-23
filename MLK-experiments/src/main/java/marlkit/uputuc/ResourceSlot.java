package marlkit.uputuc;

public class ResourceSlot extends ResourceQuantify {

    private int maxStorage;

    public ResourceSlot(Resource type, int quantity, int maxStorage){
        super(type,quantity);
        this.maxStorage = maxStorage;
    }

    public ResourceSlot(Resource type, int quantity){
        this(type, quantity, 100);
    }

    public int getMaxStorage(){return this.maxStorage;}

    public void add(int nb){
        this.value += nb;
    }

}
