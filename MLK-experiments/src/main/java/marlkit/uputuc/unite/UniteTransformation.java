package marlkit.uputuc.unite;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javafx.scene.paint.Color;
import learning.algorithm.Algorithm;
import learning.policy.Policy;
import marlkit.uputuc.ActionBuy;
import marlkit.uputuc.ActionSell;
import marlkit.uputuc.ObservationBuy;
import marlkit.uputuc.ObservationSell;
import marlkit.uputuc.Resource;
import marlkit.uputuc.ResourcesStock;
import marlkit.uputuc.TradeProposal;

public class UniteTransformation extends Unite {
	static float PRICE_PER_UNIT_BUY = 0.0f;

    private final Color color = Color.color(0.2, 0.2, 0.8);

    List<Resource> resourcesEntry;
    private ResourcesStock stock;

    @Override
    protected void onActivation() {
        super.onActivation();
        requestRole(getCommunity(), getModelGroup(), "transformation", null);   
    }

    public UniteTransformation(Policy policy, Algorithm algorithm) {
        this(policy, algorithm, new ArrayList<>(Arrays.asList(Resource.AZENE, Resource.BOGD)));
    }

    public UniteTransformation(Policy policy, Algorithm algorithm, List<Resource> ressourcesEntry) {
        super(policy, algorithm);
        stock = new ResourcesStock();
        this.resourcesEntry = new ArrayList<>(ressourcesEntry);
    }

    public UniteTransformation(Policy policy, Algorithm algorithm, double x, double y, List<Resource> ressourcesEntry) {
        super(policy, algorithm, x, y);
        stock = new ResourcesStock();
        this.resourcesEntry = new ArrayList<>(ressourcesEntry);
    }
    
    public TradeProposal requestBuy(ObservationBuy obsBuy) {
    	ActionBuy action = (ActionBuy) takeAction(obsBuy);
    	return actionBuyToTradeProposal(action);
    }
    
    protected TradeProposal actionBuyToTradeProposal(ActionBuy action) {
    	Double distance = distanceTo(action.getUniteProd());
    	TradeProposal trade = action.getTradeProposal(PRICE_PER_UNIT_BUY, distance, this);
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
    		stock.add(type, trade.getValue());
//    		money += trade.getPriceTrade();
//    		money -= Math.round(trade.getDistance()) / 500.0f;
    	}
        return true;
    }

    public void printInfos() {
        getLogger().info(() -> "[transfo] mes ressources : " + stock.toString());
//        getLogger().info("[transfo] mon argent : " + String.valueOf(money));
    }

    public void resetStock() {
    	stock.reset();
    }
    
	public ResourcesStock getResourcesStock() {
		return stock;
	}

}
