package marlkit.pushtheblock.agent;

import agent.AgentStandard;
import agent.action.Action;
import agent.action.Move2DInt;

public abstract class AgentPTB extends AgentStandard {
	public static final Action goLeft = Move2DInt.left(); 
	public static final Action goRight = Move2DInt.right();
	public static final Action goUp = Move2DInt.up(); 
	public static final Action goDown = Move2DInt.down();
}
