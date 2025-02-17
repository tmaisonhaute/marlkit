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
open module marlkit.xp {
	requires marlkit.base;
	requires madkit.base;
	exports marlkit.hello;
	exports marlkit.pushtheblock;
	
}
