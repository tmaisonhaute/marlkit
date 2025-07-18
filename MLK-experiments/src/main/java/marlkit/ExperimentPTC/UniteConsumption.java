package marlkit.ExperimentPTC;

import javafx.scene.paint.Color;
import madkit.messages.ObjectMessage;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

public class UniteConsumption extends Unite {

    private final Color color = Color.color(0.8, 0.2, 0.2);
    private int needValue = 10;

    HashMap<Ressource, Double> probaConsumptionRessource;

    private TradeProposal myLastTradeRequest = null;


    public UniteConsumption() {
        super();
        probaConsumptionRessource = new HashMap<>();
        probaConsumptionRessource.put(Ressource.AZENE, 0.5);
        probaConsumptionRessource.put(Ressource.CARBOL, 0.4);
        probaConsumptionRessource.put(Ressource.BOGD, 0.1);
    }

    protected UniteConsumption(HashMap<Ressource, Double> probaConsoRessource) {
        super();
        probaConsumptionRessource = new HashMap<>(probaConsoRessource);
    }

    public UniteConsumption(double x, double y, HashMap<Ressource, Double> probaConsoRessource) {
        super(x, y);
        probaConsumptionRessource = new HashMap<>(probaConsoRessource);
    }

    @Override
    protected void onActivation() {
        super.onActivation();
        requestRole(getCommunity(), getModelGroup(), "consommation", null);
    }


    public Ressource declareBuy() {
        Ressource ressourceNeeded = getRandomRessource(probaConsumptionRessource);
        TradeProposal tradeProposal = new TradeProposal(ressourceNeeded, -needValue, 2, null);
        ObjectMessage<TradeProposal> declaration = tradeProposal.createMessage();
        broadcast(declaration, getAgentsWithRole(getCommunity(), getModelGroup(), "transformation"));

        myLastTradeRequest = tradeProposal;
        return ressourceNeeded;
    }

    public void buy() {
        List<ObjectMessage<TradeProposal>> messagesTradesProposals = new ArrayList<>();
        ObjectMessage<TradeProposal> messRequest = getMailbox().next();
        while (messRequest != null) {
            messagesTradesProposals.add(messRequest);
            messRequest = getMailbox().next();
        }
        messagesTradesProposals.sort(Comparator.comparingDouble(mess ->
                distancesID.get(mess.getSender().getAgentNetworkID())));

        for (ObjectMessage<TradeProposal> messageTradeRequest : messagesTradesProposals) {
            TradeProposal tradeProposalRequest = messageTradeRequest.getContent();
            Ressource typeBuy = tradeProposalRequest.getType();
            if (myLastTradeRequest.getType() == typeBuy && -myLastTradeRequest.getValue() > 0) {
                int nbBuy = Math.min(-myLastTradeRequest.getValue(), tradeProposalRequest.getValue());
                if (nbBuy > 0) {
                    myLastTradeRequest.ressourceQuantify.value += nbBuy;
                    TradeProposal tradeProposalBuy = new TradeProposal(typeBuy, -nbBuy, -tradeProposalRequest.getPriceUnite(), tradeProposalRequest.distance);
                    reply(tradeProposalBuy.createMessage(), messageTradeRequest);
                }
            }
        }
        myLastTradeRequest = null;
    }

    public void setNeedValue(int needValue) {
        this.needValue = needValue;
    }
}