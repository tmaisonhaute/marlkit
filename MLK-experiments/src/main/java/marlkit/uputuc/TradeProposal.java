package marlkit.uputuc;

import static java.lang.Math.min;

import madkit.messages.ObjectMessage;
import marlkit.uputuc.unite.Unite;

public class TradeProposal implements Comparable<TradeProposal> {
	
    protected ResourceQuantify resourceQuantify;
    protected float priceUnite;
    protected Double distance;
    protected Unite agentSource;
    protected Unite agentTarget;
    

    public TradeProposal(Resource type, int value, float priceUnite, Double distance, Unite agentSource, Unite agentTarget){
		this.resourceQuantify = new ResourceQuantify(type, value);
		this.priceUnite = priceUnite;
		this.distance = distance;
		this.agentSource = agentSource;
		this.agentTarget = agentTarget;
    }
    
    public TradeProposal(Resource type, int value, float priceUnite, Unite agentSource) {
        this(type, value, priceUnite, null, agentSource,  null);
    }
    
	public void setPriceUnite(float priceUnite){
        this.priceUnite = priceUnite;
    }

    public float getPriceTrade(){

        return priceUnite * Math.abs(resourceQuantify.value);
    }


    public float deal(int nbSold){
        nbSold = min(nbSold, resourceQuantify.value);
        resourceQuantify.value -= nbSold;
        return nbSold * priceUnite;
    }

    public ObjectMessage<TradeProposal> createMessage(){
        return new ObjectMessage<TradeProposal>(this);
    }

    @Override
    public String toString() {
        return "Trade{" +
                "stock=" + resourceQuantify +
                ", priceUnite=" + priceUnite +
                ", distance=" + distance +
                '}';
    }

    public ResourceQuantify getResourceQuantify() {
        return resourceQuantify;
    }
    public Resource getType(){
        return resourceQuantify.getType();
    }
    public int getValue(){
        return resourceQuantify.getValue();
    }

    public float getPriceUnite(){return priceUnite;}

	public Double getDistance() {
		return distance;
	}
	
	public void setAgentSource(Unite agentSource) {
		this.agentSource = agentSource;
	}
	public Unite getAgentSource() {
		return agentSource;
	}
	
	public void setAgentTarget(Unite agentTarget) {
        this.agentTarget = agentTarget;
    }
	public Unite getAgentTarget() {
		return agentTarget;
	}
	
    @Override
    public int compareTo(TradeProposal o) {
        if (distance > o.distance){return 1;}
        if (distance.equals(o.distance)){return 0;}
        return -1;
    }
}
