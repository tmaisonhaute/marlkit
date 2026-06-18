package marlkit.gooryield.environment.events;


import agent.action.Action;
import marlkit.gooryield.agent.action.ActionGo;
import marlkit.gooryield.agent.action.ActionYield;
import reward.ReactionEvent;
import reward.Reward;
import reward.RewardStandard;

public class MatrixRewardEvent extends ReactionEvent {

    private final Action selfAction;
    private final Action otherAction;
    private final double rewardValue;

    public MatrixRewardEvent(Action selfAction, Action otherAction, double rewardValue) {
        if (selfAction == null || otherAction == null) {
            throw new IllegalArgumentException("selfAction and otherAction must not be null.");
        }
        this.selfAction = selfAction.copy();
        this.otherAction = otherAction.copy();
        this.rewardValue = rewardValue;
    }

    public Action getSelfAction() {
        return selfAction.copy();
    }

    public Action getOtherAction() {
        return otherAction.copy();
    }

    public double getRewardValue() {
        return rewardValue;
    }

    public boolean isSelfGo() {
        return selfAction instanceof ActionGo;
    }

    public boolean isSelfYield() {
        return selfAction instanceof ActionYield;
    }

    public boolean isOtherGo() {
        return otherAction instanceof ActionGo;
    }

    public boolean isOtherYield() {
        return otherAction instanceof ActionYield;
    }

    public boolean isGoGo() {
        return isSelfGo() && isOtherGo();
    }

    public boolean isYieldYield() {
        return isSelfYield() && isOtherYield();
    }

    public boolean isGoYield() {
        return (isSelfGo() && isOtherYield()) || (isSelfYield() && isOtherGo());
    }

    @Override
    public Reward toReward() {
        return new RewardStandard(rewardValue);
    }

    @Override
    public String toString() {
        return "MatrixRewardEvent[selfAction=" + selfAction
                + ", otherAction=" + otherAction
                + ", rewardValue=" + rewardValue + "]";
    }
}