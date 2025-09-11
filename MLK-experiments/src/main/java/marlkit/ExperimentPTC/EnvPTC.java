package marlkit.ExperimentPTC;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import agent.MLKAgent;
import environment.MLKEnvironment;
import environment.reward.Reward;
import environment.reward.RewardStandard;
import environment.state.State2DGridInt;
import learning.Experience;


public class EnvPTC implements MLKEnvironment {

    private final static double REWARDTRADECOMPLETED = 1;
    private List<UniteProduction> productionUnits;
    private List<UniteTransformation> transformationUnits;
    private List<UniteConsumption> consumptionUnits;

    protected State2DGridInt state;

    public EnvPTC(){
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

    public void receiveAgentInfo(MLKAgent agent) {
        if (agent instanceof UniteProduction) {
            productionUnits.add((UniteProduction) agent);
        } else if (agent instanceof UniteTransformation) {
            transformationUnits.add((UniteTransformation) agent);
        } else if (agent instanceof UniteConsumption) {
            consumptionUnits.add((UniteConsumption) agent);
        }
    }

    public void setupAgent(MLKAgent agent) {
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
        for(UniteProduction productionUnit : productionUnits) {
            productionUnit.productRessource();
        }
        for(UniteConsumption consumptionUnit : consumptionUnits) {
            observations.add(new ObservationRessource(consumptionUnit.declareBuy()));
        }
        for(UniteTransformation transformationUnit : transformationUnits) {
            actions.add(new ActionAgentAdress(transformationUnit.requestBuy()));
        }
        for(UniteProduction productionUnit : productionUnits) {
            productionUnit. processRequests();
        }
        for(UniteTransformation transformationUnit : transformationUnits) {
            transformationUnit.lookRequestSent();
        }
        for(UniteTransformation transformationUnit : transformationUnits) {
            transformationUnit.requestSell();
        }
        for(UniteConsumption consumptionUnit : consumptionUnits) {
            consumptionUnit.buy();
        }
        for(UniteTransformation transformationUnit : transformationUnits) {
            boolean isRequestAccepted = transformationUnit.lookRequestSent();
            if(isRequestAccepted){
                rewards.add(new RewardStandard(REWARDTRADECOMPLETED));
            }
            else{
                rewards.add(new RewardStandard(-REWARDTRADECOMPLETED));
            }
        }
        for(UniteProduction productionUnit : productionUnits) {
            productionUnit.printInfos();
        }
        for(UniteTransformation transformationUnit : transformationUnits) {
            transformationUnit.printInfos();
        }

        for(int i = 0;i<transformationUnits.size();i++ ) {
            experiences.put(transformationUnits.get(i), new Experience(observations.get(i),actions.get(i),rewards.get(i)));
        }



        return experiences;
    }
}
