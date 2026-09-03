# CollectingResource experiment

## Intro

CollectingResource is an experiment where agents request resources from production units and receive rewards from collection and distance-based penalties. The experiment is defined by its environment, agents, scenarios, reward models, and launchers.

## Simulation loop (scheduler)

SchedulerCollectingResource coordinates the simulation by calling environment and agent methods at the right time, using the base flow provided by MLKScheduler. It is responsible for triggering observation, action, environment reaction, experience collection, model updates, and policy updates.

Simulation stop conditions and timing rules are configured in SchedulerCollectingResource via its internal criteria class (SchedulerTrade2DCriteria).

## Reward Models

Four different reward models are evaluated. Let $\bar{r}_i$ denote the raw reward of agent $i$, $r_i$ its final reward, and $N$ the number of agents.

### Fully Cooperative Reward

`FullyCooperativeReward` gives all agents the average raw reward:

$$
r_i = \frac{1}{N}\sum_{j=1}^{N}\bar{r}_j.
$$

### Mixed Reward

`MixedReward` rewards each agent according to its individual outcome:

$$
r_i = \bar{r}_i.
$$

### Fair Mixed Reward

`FairMixedReward` combines each agent's raw reward with the lowest raw reward received by any agent:

$$
r_i = (1-\delta)\bar{r}_i + \delta \min_j \bar{r}_j,
$$

where $\delta \in [0,1]$ is the equity parameter. A larger $\delta$ gives more weight to the lowest individual reward. In the following experiments, $\delta$ is set to $0.5$. The influence of this parameter is discussed at the end.

### Logistical Reward

`LogisticalRewardModel` reduces an agent's raw reward when the requested production unit does not have enough stock to satisfy all requests:

$$
r_i = \bar{r}_i \min(1,\tau_i),
$$

where $\tau_i$ is the ratio between the available stock of the production unit requested by agent $i$ and the total quantity requested from that unit.


## Launchers and how to run

All launchers extend LauncherCollectingResource, which defines the environment parameters and starts the simulation. Four concrete launchers are defined, respectively:

- LauncherMixedReward uses MixedReward
- LauncherFullyCooperativeReward uses FullyCooperativeReward
- LauncherFairMixedReward uses FairMixedReward
- LauncherLogisticalReward uses LogisticalRewardModel

The used scenario is defined by each launcher through getScenario().

Each concrete launcher provides a main method, so you can run the class directly in your IDE.

## Results

This experiment illustrates how different reward models affect agent behavior in the same environment and scenario. All results were obtained with independent Q-learning agents without communication. The parameter for the simulation were : episode last 30 timesteps, and the simulation ends after 5000 episodes. More parameters can be seen in the SchedulerCollectingResource for the simulation parameters, and in CollectingResourceAgent for the learning parameters. 

### Final Policies

#### Mixed / Fair Mixed Reward

![Mixed / Fair Mixed final policy](figures/mixed_final_policy.png)

Mixed and Fair Mixed rewards lead to strongly competitive behavior, with all agents targeting the highest-value production unit.

#### Fully Cooperative Reward

![Fully Cooperative final policy](figures/fullycoop_final_policy.png)

The Fully Cooperative reward encourages agents to distribute themselves across the most valuable production units, reducing competition.

#### Logistical Reward

![Logistical final policy](figures/logistical_final_policy.png)

The Logistical reward produces an intermediate behavior by penalizing requests to overcrowded production units. 2 units compete for 1 resource, while the last one takes another resource.

### Performance Metrics

The following figures compare the four reward models on ScenarioSpatial5.

#### Total Reward

![Total reward](figures/trade2D_sc5_total.png)

The Fully Cooperative reward achieves the highest final total reward, followed by the Logistical reward. Mixed and Fair Mixed rewards lead to stronger competition and lower overall performance.


#### Lowest Individual Reward

![Lowest individual reward](figures/trade2D_sc5_lowest.png)

The Fully Cooperative reward produces the lowest outcome for the worst-performing agent because it focuses on collective rather than individual performance.

#### Total Resources Collected

![Total resources collected](figures/trade2D_sc5_total_resource.png)

The Fully Cooperative reward leads to the largest number of collected resources by reducing competition. Mixed and Fair Mixed rewards result in stronger competition for high-value resources, while the Logistical reward partially mitigates this effect.


## Discussion of the Equity Parameter of the FairMixedReward

In this experiment, `FairMixedReward` produces results similar to those obtained with `MixedReward`. The difference between the two models is that `FairMixedReward` penalizes an agent proportionally to the difference between its raw reward and the lowest raw reward among all agents.

However, the behavior emerging with `MixedReward` already tends to balance the agents' rewards and maximize the lowest individual reward. Consequently, adding the equity term does not substantially change the learned behavior.

Increasing $\delta$ nevertheless makes learning more difficult because the final reward becomes less directly correlated with the individual agent's actions. As $\delta$ increases, a larger part of each agent's reward depends on the outcome of the least-rewarded agent.
