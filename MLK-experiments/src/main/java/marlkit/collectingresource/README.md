# CollectingResource experiment

## Intro

CollectingResource is an experiment where agents request resources from production units and receive rewards from collection and distance-based penalties. The experiment is defined by its environment, agents, scenarios, reward models, and launchers.

## Simulation loop (scheduler)

SchedulerCollectingResource coordinates the simulation by calling environment and agent methods at the right time, using the base flow provided by MLKScheduler. It is responsible for triggering observation, action, environment reaction, experience collection, model updates, and policy updates.

Simulation stop conditions and timing rules are configured in SchedulerCollectingResource via its internal criteria class (SchedulerTrade2DCriteria).

## Launchers and how to run

All launchers extend LauncherCollectingResource, which defines the environment parameters and starts the simulation. Four concrete launchers are defined, respectively:

- LauncherMixedReward uses MixedReward
- LauncherFullyCooperativeReward uses FullyCooperativeReward
- LauncherFairMixedReward uses FairMixedReward
- LauncherLogisticalReward uses LogisticalRewardModel

The used scenario is defined by each launcher through getScenario().

Each concrete launcher provides a main method, so you can run the class directly in your IDE.
