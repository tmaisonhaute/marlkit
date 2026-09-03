package centralizedtraining;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import agent.MLKAgent;
import experience.Experience;
import learning.algorithms.ActorCritic;
import madkit.kernel.Activator;
import madkit.kernel.Agent;



/**
 * Activator that collects the agents' experiences and delegates the creation
 * of centralized experiences to their concrete experience implementations.
 *
 * <p>The experiences are stored in an ordered map so that centralized
 * observations and actions can preserve a stable and consistent agent
 * ordering.</p>
 */
public class CentralizedCriticCollectExperienceActivator extends Activator {
	
	/**
	 * Creates an activator operating on the specified group and role.
	 *
	 * @param group the model group
	 * @param role the role of the agents to process
	 */
	public CentralizedCriticCollectExperienceActivator(String group, String role) {
		super(group, role);
	}
	

    /**
     * Collects experience from each alive agent, merges them into a common
     * centralized observation and joint action, then redistributes one personalized
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

        redistributeCentralizedExperiences(experiencesByAgent);
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
	 * Records each agent's original experience and associates it with a
	 * centralized experience created by its concrete experience implementation.
	 *
	 * @param experiencesByAgent the ordered map associating each agent with its
	 *                           original experience
	 */
     protected void redistributeCentralizedExperiences(Map<MLKAgent, Experience> experiencesByAgent) {
    	 
         for (Map.Entry<MLKAgent, Experience> entry : experiencesByAgent.entrySet()) {
        	 MLKAgent agent = entry.getKey();
             
             Experience originalExperience = entry.getValue();
             Experience centralizedExperience = originalExperience.createCentralizedExperience(experiencesByAgent, originalExperience.getReward());

             agent.feedbackExperience(originalExperience);
             
             if (agent.getAlgorithm() instanceof ActorCritic actorCritic) {
            	 actorCritic.getCritic().enrichExperience(originalExperience, centralizedExperience);
             } else {
				throw new IllegalStateException("CentralizedCriticActivator requires agents with ActorCritic algorithms.");
             }
         }
     }

}
