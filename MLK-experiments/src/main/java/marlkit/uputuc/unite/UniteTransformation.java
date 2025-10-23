package marlkit.uputuc.unite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;

import javafx.scene.paint.Color;
import learning.policy.Policy;
import marlkit.uputuc.ActionBuy;
import marlkit.uputuc.ActionSell;
import marlkit.uputuc.ObservationBuy;
import marlkit.uputuc.ObservationSell;
import marlkit.uputuc.Resource;
import marlkit.uputuc.ResourceSlot;
import marlkit.uputuc.TradeProposal;

public class UniteTransformation extends Unite {
	static float PRICE_PER_UNIT_BUY = 0.0f;

    private final Color color = Color.color(0.2, 0.2, 0.8);
//    private int buyValue = 10;

    List<Resource> resourcesEntry;
    EnumMap<Resource, ResourceSlot> stock;

//    private double money = 0.0;

//    List<ObjectMessage<TradeProposal>> messagesRequestSent = new ArrayList<>();

    @Override
    protected void onActivation() {
        super.onActivation();
        requestRole(getCommunity(), getModelGroup(), "transformation", null);   
    }

    public UniteTransformation(Policy policy) {
        this(policy, new ArrayList<>(Arrays.asList(Resource.AZENE, Resource.BOGD)));
    }

    public UniteTransformation(Policy policy, List<Resource> ressourcesEntry) {
        super(policy);
        stock = new EnumMap<>(Resource.class);
        this.resourcesEntry = new ArrayList<>(ressourcesEntry);
    }

    public UniteTransformation(Policy policy, double x, double y, List<Resource> ressourcesEntry) {
        super(policy, x, y);
        stock = new EnumMap<>(Resource.class);
        this.resourcesEntry = new ArrayList<>(ressourcesEntry);
    }
    
    public TradeProposal requestBuy(ObservationBuy obsBuy) {
    	ActionBuy action = (ActionBuy) takeAction(obsBuy);
    	return actionBuyToTradeProposal(action);
    }
    
    protected TradeProposal actionBuyToTradeProposal(ActionBuy action) {
    	TradeProposal trade = action.getTradeProposal();
    	trade.setPriceUnite(PRICE_PER_UNIT_BUY);
    	trade.setAgentSource(this);
    	return trade;
    }

    public TradeProposal requestSell(ObservationSell obsSell) {
    	ActionSell action = (ActionSell) takeAction(obsSell);
    	return actionSellToTradeProposal(action); 
    }
    
    protected TradeProposal actionSellToTradeProposal(ActionSell action) {
    	TradeProposal trade = action.getTradeProposal();
    	return trade;
    }

    public boolean lookRequestSent(List<TradeProposal> trades) {
		if (trades.isEmpty()) {
			return false;
		}
    	for (TradeProposal trade : trades) {
    		Resource type = trade.getType();
    		if (!stock.containsKey(type)) {
    			stock.put(type, new ResourceSlot(type, 0));
    		}
    		stock.get(type).add(trade.getValue());
//    		money += trade.getPriceTrade();
//    		money -= Math.round(trade.getDistance()) / 500.0f;
    	}
        return true;
    }

    public void printInfos() {
        getLogger().info(() -> "[transfo] mes ressources : " + stock.toString());
//        getLogger().info("[transfo] mon argent : " + String.valueOf(money));
    }

//    public void setBuyValue(int buyValue) {
//        this.buyValue = buyValue;
//    }

    public void resetStock() {
        for(Resource resource : stock.keySet()){
            stock.get(resource).add(-stock.get(resource).getValue());
        }

    }

}
