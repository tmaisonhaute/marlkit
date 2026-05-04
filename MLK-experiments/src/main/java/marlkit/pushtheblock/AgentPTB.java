package marlkit.pushtheblock;

import agent.AgentStandard;
import agent.action.Action;
import agent.action.Move2D;

public abstract class AgentPTB extends AgentStandard {
	public static final Action goLeft = Move2D.left(); 
	public static final Action goRight = Move2D.right();
	public static final Action goUp = Move2D.up(); 
	public static final Action goDown = Move2D.down();
}
