package marlkit.uputuc.unite;

import java.util.EnumMap;
import java.util.HashMap;

import agent.MultiDimensionalAgent;
import javafx.scene.paint.Color;
import learning.policy.deprecated.Policy;
import madkit.messages.ObjectMessage;
import madkit.simulation.environment.Environment2D;
import marlkit.uputuc.Resource;
import util.MapSelector;
import util.Position;


public abstract class Unite extends MultiDimensionalAgent { //extends Watcher {
	protected static final String UNITE = "unite";
    protected Position position = new Position(0,0);
    private final Color color = Color.color(0.1, 0.1, 0.1);
    protected HashMap<String, Double> distancesID = new HashMap<>();
    
    protected Unite(Policy policy){
    	super(policy);
    	randomTeleport();
    }
    
    protected Unite(Policy policy, double x, double y){
    	super(policy);
    	position.x = x;
    	position.y = y;
    }

    @Override
    protected void onActivation() {
    	requestRole(getCommunity(), getModelGroup(), UNITE);
    }

    public void sendPosition(){
        ObjectMessage<Position> messagePosition = new ObjectMessage<>(position.copy());
        broadcast(messagePosition, getAgentsWithRole(getCommunity(), getModelGroup(), UNITE));
    }

    public void receivePosition(){
        ObjectMessage<Position> messagePosition = getMailbox().next();
        while (messagePosition != null){
            Position pos = messagePosition.getContent();
            double dist = position.distancePoint(pos);
            distancesID.put(messagePosition.getSender().getAgentNetworkID(), dist);
            messagePosition = getMailbox().next();
        }
    }


    @Override
    public Environment2D getEnvironment() {
        return (Environment2D) super.getEnvironment();
    }


    private void randomTeleport() {
        double recouvreCoef = 0.9;
        position.x = getEnvironment().getWidth() * (Math.random()*recouvreCoef + (1-recouvreCoef)/2);
        position.y = getEnvironment().getHeight() * (Math.random()*recouvreCoef + (1-recouvreCoef)/2);
    }

    public double distanceTo(Unite unite){
        return position.distancePoint(unite.position);
    }

    protected Resource getRandomRessource(EnumMap<Resource, Double> probaRessource) {
    	
    	try {
    		return MapSelector.getRandomItem(probaRessource, prng());
    	} catch (IllegalArgumentException e) {
            throw new IllegalStateException("Aucune ressource n'a été sélectionnée. Vérifiez vos probabilités.");
    	}
    	
    }

    public double getX() {
        return position.x;
    }

    public void setX(double x) {
        position.x = x;
    }

    public double getY() {
        return position.y;
    }

    public void setY(double y) {
        position.y = y;
    }

    public Color getColor() {
        return color;
    }

}
