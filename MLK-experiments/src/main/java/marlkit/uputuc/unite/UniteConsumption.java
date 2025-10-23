package marlkit.uputuc.unite;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import javafx.scene.paint.Color;
import learning.policy.Policy;
import marlkit.uputuc.Resource;
import marlkit.uputuc.TradeProposal;

public class UniteConsumption extends Unite {

    private final Color color = Color.color(0.8, 0.2, 0.2);
    private int needValue = 10;

    EnumMap<Resource, Double> probaConsumptionResource;

    private TradeProposal myLastTradeRequest = null;


    @Override
    protected void onActivation() {
        super.onActivation();
        requestRole(getCommunity(), getModelGroup(), "consommation", null);
    }

    public UniteConsumption(Policy policy) {
        super(policy);
        probaConsumptionResource = new EnumMap<>(Resource.class);
        probaConsumptionResource.put(Resource.AZENE, 0.5);
        probaConsumptionResource.put(Resource.CARBOL, 0.4);
        probaConsumptionResource.put(Resource.BOGD, 0.1);
    }

    public UniteConsumption(Policy policy, Map<Resource, Double> probaConsoResource) {
        super(policy);
        probaConsumptionResource = new EnumMap<>(probaConsoResource);
    }

    public UniteConsumption(Policy policy, double x, double y, Map<Resource, Double> probaConsoResource) {
        super(policy, x, y);
        probaConsumptionResource = new EnumMap<>(probaConsoResource);
    }

    public TradeProposal declareNeeds() {
        Resource ressourceNeeded = getRandomRessource(probaConsumptionResource);
        TradeProposal tradeProposal = new TradeProposal(ressourceNeeded, -needValue, 2, null);

        myLastTradeRequest = tradeProposal;
        return tradeProposal;
    }

    public void buy(List<TradeProposal> tradeProposals) {
    	//TODO
//        List<ObjectMessage<TradeProposal>> messagesTradesProposals = new ArrayList<>();
//        ObjectMessage<TradeProposal> messRequest = getMailbox().next();
//        while (messRequest != null) {
//            messagesTradesProposals.add(messRequest);
//            messRequest = getMailbox().next();
//        }
//        messagesTradesProposals.sort(Comparator.comparingDouble(mess ->
//                distancesID.get(mess.getSender().getAgentNetworkID())));
//
//        myLastTradeRequest = null;
    }

    public void setNeedValue(int needValue) {
        this.needValue = needValue;
    }
}