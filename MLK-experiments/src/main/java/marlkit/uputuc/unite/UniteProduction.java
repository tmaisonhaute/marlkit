package marlkit.uputuc.unite;

import javafx.scene.paint.Color;
import learning.policy.Policy;
import madkit.messages.ObjectMessage;
import marlkit.uputuc.Ressource;
import marlkit.uputuc.Storage;
import marlkit.uputuc.TradeProposal;

import java.util.*;
import java.util.random.RandomGenerator;

public class UniteProduction extends Unite {

    private final Color color = Color.color(0.2, 0.8, 0.2);

    HashMap<Ressource, Double> probaProductionRessource = new HashMap<>();

    private double prodValue = 10.0;
    private double prodStd = 2.0;

    HashMap<Ressource, Storage> stock;

    public UniteProduction(){
        super();
        probaProductionRessource = new HashMap<>();
        probaProductionRessource.put(Ressource.AZENE, 0.5);
        probaProductionRessource.put(Ressource.BOGD, 0.4);
        probaProductionRessource.put(Ressource.CARBOL, 0.1);
        initFillStock();
    }

    protected UniteProduction(HashMap<Ressource, Double> probaProdRessource){
        super();
        probaProductionRessource = new HashMap<>(probaProdRessource);
        initFillStock();
    }

    public UniteProduction(double x, double y, HashMap<Ressource, Double> probaProdRessource){
        super(x,y);
        probaProductionRessource = new HashMap<>(probaProdRessource);
        initFillStock();
    }

    private void initFillStock(){
        stock = new HashMap<>();
        for (Map.Entry<Ressource, Double> entry : probaProductionRessource.entrySet()){
            if (entry.getValue() > 0){
                stock.put(entry.getKey(), new Storage(entry.getKey(), (int) Math.round(20*entry.getValue())));
            }
        }
    }

    @Override
    protected void onActivation() {
        super.onActivation();
        requestRole(getCommunity(), getModelGroup(), "production", null);
    }

    public void productRessource(){
        Random rand = new Random();
        Ressource randomRessource = getRandomRessource(probaProductionRessource);
        int production = (int) Math.max(0, Math.round(prodValue + prodStd * rand.nextGaussian()));
        stock.get(randomRessource).add(production);
    }

    public void processRequests(){
        List<ObjectMessage<TradeProposal>> messagesTradesRequest = orderRequests();
        satisfyRequest(messagesTradesRequest);
    }

    private List<ObjectMessage<TradeProposal>> orderRequests(){
        List<ObjectMessage<TradeProposal>> messagesTradesRequest = new ArrayList<>();
        messagesTradesRequest = getMailbox().nextMatches(null);

        messagesTradesRequest.sort(Comparator.comparingDouble(mess ->
                distancesID.get(mess.getSender().getAgentNetworkID())));
        return messagesTradesRequest;
    }

    private void satisfyRequest(List<ObjectMessage<TradeProposal>> messagesTradesRequest){
        for (ObjectMessage<TradeProposal> messageTradeRequest : messagesTradesRequest){
            TradeProposal tradeProposalRequest = messageTradeRequest.getContent();
            Ressource requestType = tradeProposalRequest.getType();
            int nbRequested = Math.max(0, -tradeProposalRequest.getValue());
            int nbInStock = stock.containsKey(requestType) ? stock.get(requestType).getValue() : 0;
            int nbVentes = Math.min(nbRequested, nbInStock);
            if (stock.containsKey(requestType) && nbVentes > 0){
                TradeProposal tradeProposalSend = new TradeProposal(requestType, nbVentes, 0, tradeProposalRequest.distance);
                reply(tradeProposalSend.createMessage(), messageTradeRequest);
                stock.get(requestType).add(-nbVentes);
            }
        }
    }

    public void resetStock() {
        for(Ressource resource : stock.keySet()){
            stock.get(resource).add(-stock.get(resource).getValue());
        }
    }

    public void printInfos(){
        getLogger().info("[prod] mes ressources : " + stock.toString());
    }

    public void setProdValue(double prodValue) {
        this.prodValue = prodValue;
    }

    public void setProdStd(double prodStd) {
        this.prodStd = prodStd;
    }
}
