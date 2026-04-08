package centralizedtraining;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import agent.MLKAgent;
import agent.action.Action;
import agent.action.JointAction;
import learning.Experience;
import learning.algorithm.ActorCritic;
import learning.policy.PolicyInput;
import madkit.kernel.Activator;
import madkit.kernel.Agent;



/**
 * Activator that centralizes all agents' experiences into one common
 * state-action description, then redistributes a personalized experience
 * to each agent.
 *
 * <p>The redistributed experience is identical for all agents except for the reward,
 * which remains the original reward of each recipient agent.</p>
 *
 * <p>Merging rule:
 * <ul>
 *   <li>Input: all PolicyInput objects are merged with {@code add(...)}.</li>
 *   <li>Action: all individual actions are merged into one {@link JointAction}.</li>
 *   <li>Reward: each agent keeps its own original reward.</li>
 * </ul>
 * </p>
 */

public class CentralizedCriticActivator extends Activator {
	
	/**
	 * Creates an activator operating on the specified group and role.
	 *
	 * @param group the model group
	 * @param role the role of the agents to process
	 */
	public CentralizedCriticActivator(String group, String role) {
		super(group, role);
	}
	

    /**
     * Collects experience from each alive agent, merges them into a common
     * centralized input and joint action, then redistributes one personalized
     * experience per agent.
     *
     * <p>The only personalized part is the reward.</p>
     *
     * @param args not used
     */
    @Override
    public void execute(Object... args) {
        List<MLKAgent> aliveAgents = collectAliveAgents();
        if (aliveAgents.isEmpty()) {
            return;
        }

        Map<MLKAgent, Experience> experiencesByAgent = collectExperiencesByAgent(aliveAgents);
        if (experiencesByAgent.isEmpty()) {
            return;
        }

        PolicyInput mergedInput = mergeInputs(experiencesByAgent);
        JointAction jointAction = mergeActions(experiencesByAgent);

        redistributeMergedExperiences(experiencesByAgent, mergedInput, jointAction);
    }

    /**
	 * Collects alive agents matching the activator role.
	 *
	 * @return list of alive MLK agents
	 */
	protected List<MLKAgent> collectAliveAgents() {
		List<MLKAgent> aliveAgents = new ArrayList<>();
		for (Agent agent : getAgents()) {
			if (agent.isAlive() && agent instanceof MLKAgent mlkAgent) {
				aliveAgents.add(mlkAgent);
			}
		}
		return aliveAgents;
	}

    /**
     * Collects a map of one experience links to one agent.
     *
     * @param agents the alive agents
     * @return a map agent -> individual experience
     */
    protected Map<MLKAgent, Experience> collectExperiencesByAgent(List<MLKAgent> agents) {
        Map<MLKAgent, Experience> experiencesByAgent = new LinkedHashMap<>();
        for (MLKAgent agent : agents) {
            Experience experience = resolveEnvironmentExperience(agent);
            if (experience != null) {
                experiencesByAgent.put(agent, experience);
            }
        }
        return experiencesByAgent;
    }
    
    /**
	 * Get the current environment experience for an agent.
	 *
	 * @param agent the agent
	 * @return the environment experience, or null if unavailable
	 */
	protected Experience resolveEnvironmentExperience(MLKAgent agent) {
		Experience experience = invokeExperienceMethod(agent, "getEnvExperience");
		if (experience != null) {
			return experience;
		}
		return invokeExperienceMethod(agent, "getEnvExperiment");
	}
	
	/**
	 * Invokes an experience accessor by name on the given agent.
	 *
	 * @param agent the target agent
	 * @param methodName method to invoke
	 * @return returned experience or null if method does not exist or returns null
	 */
	protected Experience invokeExperienceMethod(MLKAgent agent, String methodName) {
		try {
			Method method = agent.getClass().getMethod(methodName);
			Object result = method.invoke(agent);
			if (result instanceof Experience experience) {
				return experience;
			}
			return null;
		} catch (NoSuchMethodException e) {
			return null;
		} catch (IllegalAccessException | InvocationTargetException e) {
			throw new IllegalStateException("Cannot invoke " + methodName + " on " + agent.getClass().getName(), e);
		}
	}

   /**
     * Merges all policy inputs into one common input.
     *
     * @param experiencesByAgent agent -> experience map
     * @return merged input
     */
    protected PolicyInput mergeInputs(Map<MLKAgent, Experience> experiencesByAgent) {
        PolicyInput mergedInput = null;

        for (Experience experience : experiencesByAgent.values()) {
            PolicyInput input = experience.getInput();
            if (mergedInput == null) {
                mergedInput = input;
            } else {
                mergedInput = mergedInput.add(input);
            }
        }

        return mergedInput;
    }
    

    /**
      * Merges all individual actions into one joint action.
      *
      * @param experiencesByAgent agent -> experience map
      * @return the joint action
      */
     protected JointAction mergeActions(Map<MLKAgent, Experience> experiencesByAgent) {
         JointAction jointAction = new JointAction();

         for (Map.Entry<MLKAgent, Experience> entry : experiencesByAgent.entrySet()) {
             MLKAgent agent = entry.getKey();
             Action action = entry.getValue().getAction();
             jointAction.addActions(action);
         }

         return jointAction;
     }


     /**
      * Sends each agent a centralized experience:
      * <ul>
      *   <li>same merged input for everyone</li>
      *   <li>same joint action for everyone</li>
      *   <li>individual original reward for each recipient</li>
      * </ul>
      *
      * @param experiencesByAgent agent -> original experience
      * @param mergedInput the centralized merged input
      * @param jointAction the centralized joint action
      */
     protected void redistributeMergedExperiences(Map<MLKAgent, Experience> experiencesByAgent, PolicyInput mergedInput, JointAction jointAction) {
    	 
         for (Map.Entry<MLKAgent, Experience> entry : experiencesByAgent.entrySet()) {
             MLKAgent agent = entry.getKey();
             
             Experience originalExperience = entry.getValue();
             Experience centralizedExperience = new Experience(mergedInput, jointAction, originalExperience.getReward());

             agent.feedbackExperience(originalExperience);
             
             if (agent.getAlgorithm() instanceof ActorCritic actorCritic) {
            	 actorCritic.getCritic().enrichExperience(originalExperience, centralizedExperience);
				} else {
					throw new IllegalStateException(
							"CentralizedCriticActivator requires agents with ActorCritic algorithms.");
				}
         }
     }



}
