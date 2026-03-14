package marlkit.pushtheblock;

import agent.AgentStandard;
import agent.action.Action;
import agent.action.Action2DMove;

public abstract class AgentPTB extends AgentStandard {
	public static final Action goLeft = Action2DMove.left(); 
	public static final Action goRight = Action2DMove.right();
	public static final Action goUp = Action2DMove.up(); 
	public static final Action goDown = Action2DMove.down();
}
