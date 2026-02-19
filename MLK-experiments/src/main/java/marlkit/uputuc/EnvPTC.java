package marlkit.uputuc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agent.MLKAgent;
import agent.action.Action;
import environment.EnvironmentStandard;
import environment.reward.Reward;
import environment.state.State;
import environment.state.State2DGridInt;
import learning.Experience;
import marlkit.uputuc.unite.UniteConsumption;
import marlkit.uputuc.unite.UniteProduction;
import marlkit.uputuc.unite.UniteTransformation;
import rewardmodeling.ReactionEvent;
import rewardmodels.MixedReward;
import util.Pair;


public class EnvPTC extends EnvironmentStandard {

//    private final static double REWARDTRADECOMPLETED = 1;
//    private final static double REWARDTRADENOTCOMPLETED = 0;
    private List<UniteProduction> productionUnits;
    private List<UniteTransformation> transformationUnits;
    private List<UniteConsumption> consumptionUnits;

    protected State2DGridInt state;

    public EnvPTC(){
    	super(30, 30, new MixedReward());
        state = new State2DGridInt(30,30);
    }


    public void reset(){
        state.reset();
        for(UniteProduction unite : productionUnits){
            unite.resetStock();
        }
        for (UniteTransformation unite : transformationUnits){
            unite.resetStock();
        }
    }

    @Override
    public void addAgent(MLKAgent agent) {
        if (agent instanceof UniteProduction) {
            productionUnits.add((UniteProduction) agent);
        } else if (agent instanceof UniteTransformation) {
            transformationUnits.add((UniteTransformation) agent);
        } else if (agent instanceof UniteConsumption) {
            consumptionUnits.add((UniteConsumption) agent);
        }
    }

    @Override
    public Map<MLKAgent, Experience> step() {
        Map<MLKAgent,Experience> experiences = new HashMap<>();
        List<ObservationRessource> observations = new ArrayList<>();
        List<ActionAgentAdress> actions = new ArrayList<>();
        List<Reward> rewards = new ArrayList<>();
        
//        List<TradeProposal> consumptionNeeds = new ArrayList<>();
//        for(UniteConsumption consumptionUnit : consumptionUnits) {
//        	TradeProposal newNeed = consumptionUnit.declareNeeds();
//        	consumptionNeeds.add(newNeed);
//        }
//        
//        for(UniteProduction productionUnit : productionUnits) {
//            productionUnit.productRessource();
//        }
//        
////        for(UniteConsumption consumptionUnit : consumptionUnits) {
////            observations.add(new ObservationRessource(consumptionUnit.declareBuy()));
////        }
//        
//        List<TradeProposal> buyRequests = new ArrayList<>();
//        for(UniteTransformation transformationUnit : transformationUnits) {
//            //actions.add(new ActionAgentAdress(transformationUnit.requestBuy()));
//        	ObservationBuy obsAchat = null;//TODO
//        	buyRequests.add(transformationUnit.requestBuy(obsAchat));
//        }
//        for(UniteProduction productionUnit : productionUnits) {
//            productionUnit.processRequests(buyRequests);
//        }
//        
//        for(UniteTransformation transformationUnit : transformationUnits) {
//            transformationUnit.lookRequestSent(buyRequests);
//        }
//        
//        List<TradeProposal> sellRequests = new ArrayList<>();
//        for(UniteTransformation transformationUnit : transformationUnits) {
//        	ObservationSell obsVente = null; //TODO
//        	sellRequests.add(transformationUnit.requestSell(obsVente));
//        }
//        for(UniteConsumption consumptionUnit : consumptionUnits) {
//            consumptionUnit.buy(sellRequests);
//        }
//        for(UniteTransformation transformationUnit : transformationUnits) {
//            boolean isRequestAccepted = transformationUnit.lookRequestSent(sellRequests);
//            if(isRequestAccepted){
//                rewards.add(new RewardStandard(REWARDTRADECOMPLETED));
//            }
//            else{
//                rewards.add(new RewardStandard(REWARDTRADENOTCOMPLETED));
//            }
//        }
//        for(UniteProduction productionUnit : productionUnits) {
//            productionUnit.printInfos();
//        }
//        for(UniteTransformation transformationUnit : transformationUnits) {
//            transformationUnit.printInfos();
//        }
//
//        for(int i = 0;i<transformationUnits.size();i++ ) {
//            experiences.put(transformationUnits.get(i), new Experience(observations.get(i),actions.get(i),rewards.get(i)));
//        }
        return experiences;
    }



	@Override
	protected void setupState() {
		// TODO Auto-generated method stub	
	}
	
	@Override 
	protected void setupAgents() { 
		// TODO Auto-generated method stub
	}



	@Override
	public Map<MLKAgent, Pair<Action, List<ReactionEvent>>> dynamics(Map<MLKAgent, Action> actions) {
		// TODO Auto-generated method stub
		return null;
	}



	@Override
	public State getState() {
		// TODO Auto-generated method stub
		return null;
	}
}
