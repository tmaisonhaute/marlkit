package marlkit.uputuc.unite;

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.random.RandomGenerator;

import javafx.scene.paint.Color;
import learning.algorithm.Algorithm;
import learning.policy.Policy;
import marlkit.uputuc.Resource;
import marlkit.uputuc.ResourceSlot;
import marlkit.uputuc.TradeProposal;

public class UniteProduction extends Unite {

    private final Color color = Color.color(0.2, 0.8, 0.2);

    EnumMap<Resource, Double> probaProductionResource;

    private double prodValue = 10.0;
    private double prodStd = 2.0;

    HashMap<Resource, ResourceSlot> stock;

    @Override
    protected void onActivation() {
        super.onActivation();
        requestRole(getCommunity(), getModelGroup(), "production", null);
    }
    
    public UniteProduction(){
    	this(null, null);
    }
    
    public UniteProduction(Policy policy, Algorithm algorithm){
        super(policy, algorithm);
        probaProductionResource = new EnumMap<>(Resource.class);
        probaProductionResource.put(Resource.AZENE, 0.5);
        probaProductionResource.put(Resource.BOGD, 0.4);
        probaProductionResource.put(Resource.CARBOL, 0.1);
        initFillStock();
    }
    
    public UniteProduction(Policy policy, Algorithm algorithm, double x, double y){
        super(policy, algorithm, x,y);
        probaProductionResource = new EnumMap<>(Resource.class);
        probaProductionResource.put(Resource.AZENE, 0.5);
        probaProductionResource.put(Resource.BOGD, 0.4);
        probaProductionResource.put(Resource.CARBOL, 0.1);
        initFillStock();
    }

    public UniteProduction(Policy policy, Algorithm algorithm, Map<Resource, Double> probaProdResource){
        super(policy, algorithm);
        probaProductionResource = new EnumMap<>(probaProdResource);
        initFillStock();
    }

    public UniteProduction(Policy policy, Algorithm algorithm, double x, double y, Map<Resource, Double> probaProdResource){
        super(policy, algorithm, x,y);
        probaProductionResource = new EnumMap<>(probaProdResource);
        initFillStock();
    }

    private void initFillStock(){
        stock = new HashMap<>();
        for (Map.Entry<Resource, Double> entry : probaProductionResource.entrySet()){
            if (entry.getValue() > 0){
                stock.put(entry.getKey(), new ResourceSlot(entry.getKey(), (int) Math.round(20*entry.getValue())));
            }
        }
    }

    public void productRessource(){
        RandomGenerator rand = prng();
        Resource randomRessource = getRandomRessource(probaProductionResource);
        int production = (int) Math.max(0, Math.round(prodValue + prodStd * rand.nextGaussian()));
        stock.get(randomRessource).add(production);
    }

    public void processRequests(List<TradeProposal> tradesRequests){
        orderRequests(tradesRequests);
        satisfyRequests(tradesRequests);
    }

    protected void orderRequests(List<TradeProposal> tradesRequests) {
//      List<TradeProposal> sortedRequests = new ArrayList<>(tradesRequests);
//      sortedRequests.sort(null);
//    	return sortedRequests;
    	tradesRequests.sort(null);
    }

    protected void satisfyRequests(List<TradeProposal> tradesRequests){
    	
    	Iterator<TradeProposal> iterator = tradesRequests.iterator();
        while (iterator.hasNext()) {
        	TradeProposal tradeRequest = iterator.next();
        	
        	Resource requestType = tradeRequest.getType();
            int nbRequested = Math.max(0, -tradeRequest.getValue());
            int nbInStock = stock.containsKey(requestType) ? stock.get(requestType).getValue() : 0;
            int nbVentes = Math.min(nbRequested, nbInStock);
            
            if (stock.containsKey(requestType) && nbVentes > 0){
            	stock.get(requestType).add(-nbVentes);
            }else {
            	iterator.remove();
            }
            
        }
    }

    public void resetStock() {
        for(Resource resource : stock.keySet()){
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
