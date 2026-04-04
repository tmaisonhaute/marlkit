package centralizedtraining;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import agent.MLKAgent;
import learning.Experience;
import madkit.kernel.Activator;
import madkit.kernel.Agent;

/**
 * Activator that centralizes environment experiences and redistributes them to all targeted agents.
 */
public class CollectiveExperienceActivator extends Activator {

	/**
	 * Creates an activator operating on the specified group and role.
	 *
	 * @param group the model group
	 * @param role the role of the agents to process
	 */
	public CollectiveExperienceActivator(String group, String role) {
		super(group, role);
	}

	/**
	 * Pulls each alive agent's environment experience and broadcasts non-null experiences to all alive agents.
	 *
	 * @param args not used
	 */
	@Override
	public void execute(Object... args) {
		List<MLKAgent> aliveAgents = collectAliveAgents();
		if (aliveAgents.isEmpty()) {
			return;
		}
		List<Experience> collectiveExperiences = collectExperiences(aliveAgents);
		redistributeExperiences(aliveAgents, collectiveExperiences);
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
	 * Collects non-null environment experiences from the provided agents.
	 *
	 * @param agents agents to query
	 * @return all non-null experiences
	 */
	protected List<Experience> collectExperiences(List<MLKAgent> agents) {
		List<Experience> experiences = new ArrayList<>();
		for (MLKAgent agent : agents) {
			Experience experience = resolveEnvironmentExperience(agent);
			if (experience != null) {
				experiences.add(experience);
			}
		}
		return experiences;
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
	 * Sends each collected experience to every agent in the target list.
	 *
	 * @param agents recipients
	 * @param experiences experiences to distribute
	 */
	protected void redistributeExperiences(List<MLKAgent> agents, List<Experience> experiences) {
		for (Experience experience : experiences) {
			for (MLKAgent agent : agents) {
				agent.feedbackExperience(experience);
			}
		}
	}

}
