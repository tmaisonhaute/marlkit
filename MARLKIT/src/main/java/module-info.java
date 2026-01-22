/**
 * 
 * The madkit.base module provides the core functionalities of the MaDKit
 * platform. MaDKit (Multi-Agent Development Kit) is a lightweight framework for
 * building multi-agent systems. It allows developers to create, manage, and
 * simulate agents within an artificial organization.
 *
 * Features provided by MaDKit include:
 * <ul>
 * 
 * <li>Agent Management: Create, launch, and manage the lifecycle of
 * agents.</li>
 * <li>Communication: Facilitate communication between agents using messages and
 * roles.</li>
 * <li>Simulation: Support for simulating agent behaviors and interactions.</li>
 * <li>Logging: Integrated logging system for monitoring and debugging.</li>
 * <li>Configuration: Flexible configuration options for customizing the
 * platform.</li>
 * <li>Extensibility: Easily extendable to add new functionalities and integrate
 * with other systems.</li>
 * </ul>
 *
 * @version 1.0
 * @since 6.0
 */
open module marlkit.base {
	requires transitive madkit.base;
	requires java.logging;
    requires org.apache.commons.logging;
    requires com.fasterxml.jackson.databind;
    requires org.apache.commons.lang3;
    

    exports marlkit.test;
	exports environment;
	exports environment.observation;
	exports environment.observation.wrapperobservationvector;
	exports environment.reward;
	exports environment.state;
	exports agent;
	exports agent.action;
	exports agent.action.wrapperactionvector;
	exports agent.interaction;
	exports learning;
	exports learning.policy;
	exports learning.algorithm;
	exports learning.policy.explorationsettings;
	exports util;
	exports util.criteria;
	exports simulation;
    exports util.grafana;
    exports environment.observation.wrapperactionobservation;
    exports rewardstructure;
}
