package marlkit.ExperimentPTC;

import madkit.kernel.Agent;
import madkit.messages.ObjectMessage;

import static java.lang.Math.min;

public class TradeProposal implements Comparable<TradeProposal> {
    public RessourceQuantify ressourceQuantify;
    protected float priceUnite;
    public Double distance;

    public TradeProposal(Ressource type, int value, float priceUnite, Double distance) {
        this.ressourceQuantify = new RessourceQuantify(type, value);
        this.priceUnite = priceUnite;
        this.distance = distance;
    }

    public TradeProposal(Ressource type, int nb, float priceUnite) {
        this(type, nb, priceUnite, 0.0);
    }

    public float getPriceTrade(){

        return priceUnite * Math.abs(ressourceQuantify.value);
    }


    public float deal(int nbSold){
        nbSold = min(nbSold, ressourceQuantify.value);
        ressourceQuantify.value -= nbSold;
        return nbSold * priceUnite;
    }

    public ObjectMessage<TradeProposal> createMessage(){
        return new ObjectMessage<TradeProposal>(this);
    }

    @Override
    public String toString() {
        return "Trade{" +
                "stock=" + ressourceQuantify +
                ", priceUnite=" + priceUnite +
                ", distance=" + distance +
                '}';
    }

    public RessourceQuantify getRessourceQuantify() {
        return ressourceQuantify;
    }
    public Ressource getType(){
        return ressourceQuantify.getType();
    }
    public int getValue(){
        return ressourceQuantify.getValue();
    }

    public float getPriceUnite(){return priceUnite;}

    @Override
    public int compareTo(TradeProposal o) {
        if (distance > o.distance){return 1;}
        if (distance.equals(o.distance)){return 0;}
        return -1;
    }
}
