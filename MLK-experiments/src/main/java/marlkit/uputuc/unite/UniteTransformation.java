package marlkit.uputuc.unite;

import javafx.scene.paint.Color;
import learning.policy.Policy;
import madkit.kernel.Agent;
import madkit.kernel.AgentAddress;
import madkit.messages.ObjectMessage;
import madkit.simulation.SimuAgent;
import marlkit.uputuc.Ressource;
import marlkit.uputuc.Storage;
import marlkit.uputuc.TradeProposal;

import java.util.*;

public class UniteTransformation extends Unite {

    private final Color color = Color.color(0.2, 0.2, 0.8);
    private int buyValue = 10;

    List<Ressource> ressourcesEntry;
    HashMap<Ressource, Storage> stock = new HashMap<>();

    private double money = 0.0;

    List<ObjectMessage<TradeProposal>> messagesRequestSent = new ArrayList<>();

    @Override
    protected void onActivation() {
        super.onActivation();
        requestRole(getCommunity(), getModelGroup(), "transformation", null);
    }

    public UniteTransformation() {
        super();
        ressourcesEntry = new ArrayList<>(Arrays.asList(Ressource.AZENE, Ressource.BOGD));
    }

    public UniteTransformation(List<Ressource> ressourcesEntry) {
        super();
        this.ressourcesEntry = new ArrayList<>(ressourcesEntry);
    }

    public UniteTransformation(double x, double y, List<Ressource> ressourcesEntry) {
        super(x, y);
        this.ressourcesEntry = new ArrayList<>(ressourcesEntry);
    }

    public AgentAddress requestBuy() {
        List<AgentAddress> productorsAdress = getAgentsWithRole(getCommunity(), getModelGroup(), "production");
        Random rand = new Random();
        AgentAddress productor = productorsAdress.get(rand.nextInt(productorsAdress.size()));
        Ressource randomRessource = ressourcesEntry.get(rand.nextInt(ressourcesEntry.size()));
        TradeProposal tradeProposal = new TradeProposal(randomRessource, -buyValue, 0, distancesID.get(productor.getAgentNetworkID()));
        ObjectMessage<TradeProposal> messageRequestBuy = tradeProposal.createMessage();
        send(messageRequestBuy, productor);
        messagesRequestSent.add(messageRequestBuy);
        return productor;
    }

    public void requestSell() {
        ObjectMessage<TradeProposal> messageTradeRequestBuy = nextMessage();
        while (messageTradeRequestBuy != null) {
            TradeProposal TradeProposalRequest = messageTradeRequestBuy.getContent();
            Ressource typeBuy = TradeProposalRequest.getType();
            int nbSell = stock.containsKey(typeBuy) ? Math.min(stock.get(typeBuy).getValue(), -TradeProposalRequest.getValue()) : 0;

            if (nbSell > 0) {
                TradeProposal tradeProposalSell = new TradeProposal(typeBuy, nbSell, -TradeProposalRequest.getPriceUnite(), distancesID.get(messageTradeRequestBuy.getSender().getAgentNetworkID()));
                ObjectMessage<TradeProposal> messageRequestSell = tradeProposalSell.createMessage();
                send(messageRequestSell, messageTradeRequestBuy.getSender());
                messagesRequestSent.add(messageRequestSell);
                getMailbox().purge();
            }

            messageTradeRequestBuy = getMailbox().next();
        }
    }

    public boolean lookRequestSent() {
        for (ObjectMessage<TradeProposal> messageRequest : messagesRequestSent) {
            ObjectMessage<TradeProposal> reply = getMailbox().getReply(messageRequest);
            if (reply != null) {
                TradeProposal tradeProposalReceive = reply.getContent();
                Ressource type = tradeProposalReceive.getType();
                if (!stock.containsKey(type)) {
                    stock.put(type, new Storage(type, 0));
                }
                stock.get(type).add(tradeProposalReceive.getValue());
                money += tradeProposalReceive.getPriceTrade();
                money -= Math.round(tradeProposalReceive.distance) / 500.0f;
                messagesRequestSent.clear();
                return true;
            }

        }
        messagesRequestSent.clear();
        return false;

    }

    public void printInfos() {
        getLogger().info(() -> "[transfo] mes ressources : " + stock.toString());
        getLogger().info("[transfo] mon argent : " + String.valueOf(money));
    }

    public void setBuyValue(int buyValue) {
        this.buyValue = buyValue;
    }

    public void resetStock() {
        for(Ressource resource : stock.keySet()){
            stock.get(resource).add(-stock.get(resource).getValue());
        }

    }

}
