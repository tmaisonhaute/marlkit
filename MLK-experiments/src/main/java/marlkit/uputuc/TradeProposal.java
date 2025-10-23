package marlkit.uputuc;

import static java.lang.Math.min;

import agent.MLKAgent;
import madkit.messages.ObjectMessage;

public class TradeProposal implements Comparable<TradeProposal> {
	
    protected ResourceQuantify resourceQuantify;
    protected float priceUnite;
    protected Double distance;
    protected MLKAgent agentSource;
    protected MLKAgent agentTarget;
    

    public TradeProposal(Resource type, int value, float priceUnite, Double distance, MLKAgent agentSource, MLKAgent agentTarget){
		this.resourceQuantify = new ResourceQuantify(type, value);
		this.priceUnite = priceUnite;
		this.distance = distance;
		this.agentSource = agentSource;
		this.agentTarget = agentTarget;
    }
    
    public TradeProposal(Resource type, int value, float priceUnite, MLKAgent agentSource) {
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
	
	public void setAgentSource(MLKAgent agentSource) {
		this.agentSource = agentSource;
	}
	public MLKAgent getAgentSource() {
		return agentSource;
	}
	
	public void setAgentTarget(MLKAgent agentTarget) {
        this.agentTarget = agentTarget;
    }
	public MLKAgent getAgentTarget() {
		return agentTarget;
	}
	
    @Override
    public int compareTo(TradeProposal o) {
        if (distance > o.distance){return 1;}
        if (distance.equals(o.distance)){return 0;}
        return -1;
    }
}
