package marlkit.uputuc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import agent.AgentStandard;
import javafx.scene.paint.Color;
import learning.policy.PolicyRandom;
import madkit.messages.ObjectMessage;
import madkit.simulation.environment.Environment2D;

public abstract class Unite extends AgentStandard { //extends Watcher {
    protected Position position = new Position(0,0);
    private final Color color = Color.color(0.1, 0.1, 0.1);
    protected HashMap<String, Double> distancesID = new HashMap<>();


    public void sendPosition(){
        ObjectMessage<Position> messagePosition = new ObjectMessage<>(position.copy());
        broadcast(messagePosition, getAgentsWithRole(getCommunity(), getModelGroup(), "unite"));
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

    public Unite(){
        super(new PolicyRandom(new ArrayList<>()));
        randomTeleport();
    }

    public Unite(double x, double y){
        super(new PolicyRandom(new ArrayList<>()));
        position.x = x;
        position.y = y;
    }

    @Override
    protected void onActivation() {
        requestRole(getCommunity(), getModelGroup(), "unite");
    }

    @Override
    public Environment2D getEnvironment() {
        return super.getEnvironment();
    }


    private void randomTeleport() {
        double recouvreCoef = 0.9;
        position.x = getEnvironment().getWidth() * (Math.random()*recouvreCoef + (1-recouvreCoef)/2);
        position.y = getEnvironment().getHeight() * (Math.random()*recouvreCoef + (1-recouvreCoef)/2);
    }

    public double distanceTo(Unite unite){
        return position.distancePoint(unite.position);
    }

    protected void sendLocation(){
        ObjectMessage<Position> messagePos = new ObjectMessage<Position>(position.copy());
        sendWithRole(messagePos, getCommunity(), getModelGroup(), "unite", "unite");
    }

    protected void receiveLocation(){
        ObjectMessage<Position> messagePos = new ObjectMessage<Position>(position.copy());
        sendWithRole(messagePos, getCommunity(), getModelGroup(), "unite", "unite");
    }

    protected Ressource getRandomRessource(HashMap<Ressource, Double> probaRessource) {
        Random rand = new Random();
        double totalProba = probaRessource.values().stream().mapToDouble(Double::doubleValue).sum();
        double randomValue = rand.nextDouble();
        double cumulativeProba = 0.0;

        for (Map.Entry<Ressource, Double> entry : probaRessource.entrySet()) {
            cumulativeProba += entry.getValue() / totalProba;
            if (randomValue <= cumulativeProba) {
                return entry.getKey();
            }
        }
        throw new IllegalStateException("Aucune ressource n'a été sélectionnée. Vérifiez vos probabilités.");
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
