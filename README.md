# MARLKIT

MARLKIT is an open-source Java library for designing, implementing, and
comparing multi-agent reinforcement learning (MARL) experiments. It is built
on top of [MaDKit](https://madkit.org/) and provides reusable abstractions for
environments, agents, learning algorithms, rewards, communication, training,
simulation control, and evaluation.

The central design principle is **modular experiment composition**: an
experiment is assembled from independent components instead of being written
as one monolithic simulation. This makes it possible to replace an algorithm,
reward model, observation, or communication mechanism while keeping the rest
of the experiment unchanged.

## Why modular MARL experiments?

MARL experiments often combine several concerns that are difficult to compare
when they are tightly coupled:

- the environment defines state and transition dynamics;
- agents select actions from their observations;
- learning algorithms update policies or value functions;
- reward models interpret events and outcomes;
- communication defines what agents can share;
- schedulers control episodes, evaluation, and termination; and
- launchers assemble and start a complete experiment.

MARLKIT gives each concern a separate extension point. A researcher can keep
the same environment and compare Q-learning, policy-gradient, or actor-critic
agents, or keep the same agents and compare individual, cooperative, or
event-based reward models. The resulting implementations are easier to reuse,
test, and compare.

## Experiment composition

A typical experiment can be understood as the following dependency chain:

```text
				 observations             actions
					┌───────┐           ┌─────────────┐
					│ Agents│──────────▶│ Environment │
					└───┬───┘           └──────┬──────┘
						│                      │
				 policies and             state transitions
				 learning updates          and reaction events
						│                      │
						└──────────┬───────────┘
								   ▼
							Reward model
								   │
								   ▼
					  rewards and learning signals

			  Scheduler ── controls episodes and evaluation
			  Launcher  ── assembles and starts the experiment
			  Viewer    ── displays state and learning results
```

The main responsibilities are:

| Component | Responsibility |
| --- | --- |
| **Environment** | Maintains the world state, applies joint actions, and emits reaction events. |
| **State** | Represents the environment state, such as a grid, positions, values, or continuous variables. |
| **Observation** | Defines the information available to each agent. |
| **Action** | Defines the actions an agent can select. |
| **Agent** | Selects actions, receives feedback, and participates in the simulation lifecycle. |
| **Policy** | Maps observations or learned values to actions, including exploration. |
| **Learning algorithm** | Updates values, policies, models, or neural-network parameters. |
| **Reward model** | Converts environment events and outcomes into rewards for agents. |
| **Communication** | Controls messages, broadcasts, filters, or shared observations between agents. |
| **Scheduler** | Controls episode duration, display, evaluation, pauses, and stopping criteria. |
| **Launcher** | Creates the environment and agents and starts one complete experiment configuration. |
| **Viewer and evaluator** | Displays the simulation and records or computes experiment results. |

### One simulation step

The components cooperate during each step of an episode:

1. The environment provides an observation to each agent.
2. Each agent selects an action using its policy and current learned state.
3. The environment applies the joint action and updates its state.
4. The environment emits reaction events describing what happened.
5. The reward model translates those events into agent rewards.
6. Agents receive the new observations and rewards.
7. Learning components update their values, policies, or models.
8. The scheduler checks episode, evaluation, and termination criteria.

This separation is useful for controlled comparisons. For example, the same
environment dynamics can be paired with several reward models, or the same
learning algorithm can be evaluated with different observation and
communication designs.

## Repository structure

The repository is a Gradle multi-project build with three main subprojects:

```text
MARLKIT/
├── MARLKIT/
│   └── Core framework abstractions and reusable implementations
├── MARL-Methods/
│   └── Learning methods, agent modules, communication, prediction,
│       centralized training, and reward-model implementations
├── MLK-experiments/
│   └── Concrete environments, agents, launchers, schedulers, viewers,
│       evaluations, documentation, and result figures
└── settings.gradle
```

The dependency direction is:

```text
MARLKIT  ◀──  MARL-Methods  ◀──  MLK-experiments
```

### `MARLKIT`: the base framework

The `marlkit.base` module contains the contracts and general-purpose
implementations used by experiments. Important packages include:

- `environment` and `environment.state` for environment and state abstractions;
- `environment.observation` for observations and observation wrappers;
- `agent` and `agent.action` for agent and action abstractions;
- `learning`, `learning.algorithms`, and `learning.policies` for reusable
  learning and decision-making components;
- `reward` for reward models and reaction-event processing;
- `communication` and `agent.communication` for communication support;
- `simulation` and `trainingexecutionstrategy` for simulation control;
- `evaluation`, `experience`, and `util` for evaluation, experience storage,
  criteria, and shared utilities.

This layer should remain independent of a particular scenario. A grid-world
environment, for example, belongs in an experiment module rather than in the
base framework unless it is intentionally reusable as a general component.

### `MARL-Methods`: reusable MARL methods

The `marlkit.methods` module depends on `marlkit.base` and groups methods that
can be reused across several environments:

- `agentmodule` for configurable learning-agent modules;
- `algorithm` for algorithm implementations;
- `learningstructure` for reusable learning structures;
- `modelofotheragents` for individual and group prediction models;
- `centralizedtraining` for shared experience and centralized critics;
- `communicationimplementation` for concrete communication and broadcast
  mechanisms; and
- `rewardmodelimplementation` for common reward-model implementations.

Keeping these methods separate from scenarios allows one algorithm or reward
model to be tested in multiple environments.

### `MLK-experiments`: concrete scenarios

The `marlkit.xp` module depends on both core modules and contains complete
experiment applications. An experiment package typically contains some or all
of the following:

- an environment and its state representation;
- experiment-specific agents and configurations;
- reaction events and reward-model selection;
- a scheduler and evaluation criteria;
- a viewer and result-processing code;
- one or more launchers; and
- a README and result figures.

The generic launcher menu is configured as the application entry point:
`marlkit.launcher.ExperimentLauncherMenu`. It discovers documented experiment
groups and their launchers, displays the associated README, and starts the
selected configuration.

## Example: composing PushTheBlock

The PushTheBlock experiment demonstrates how a scenario is assembled from
separate components:

| Experiment concern | Implementation |
| --- | --- |
| Launcher | `marlkit.pushtheblock.LauncherPTB` |
| Environment | `marlkit.pushtheblock.EnvPushTheBlock` |
| Agent | `AgentPTBqLearning` |
| Reward model | `MixedReward` |
| Scheduler | `marlkit.pushtheblock.SchedulerPTB` |
| Viewer | `marlkit.pushtheblock.ViewerPTB` |

The launcher creates the environment with a reward model, launches the
experiment agent, and associates the scheduler and viewer with the simulation.
The environment owns the grid state and dynamics, while reaction events such
as moving or pushing a block are interpreted by the reward model. The scheduler
then controls episode length, display timing, evaluation, and the maximum
number of episodes.

This arrangement makes alternatives straightforward: a different agent can
be selected without rewriting the environment, and a different reward model
can be evaluated without changing the grid dynamics.

## Available functionality

### Core architecture

- Environment, state, agent, action, observation, and reward contracts
- Grid, position/value, wrapped, and other observation/state representations
- Reaction-event processing for environment-driven rewards
- Simulation, evaluation, experience, and training-execution utilities

### Learning and policies

- Q-learning, SARSA, Monte Carlo, REINFORCE, and Actor-Critic workflows
- Value-based and softmax policies
- Epsilon-greedy and other exploration strategies
- Neural-network components for actor-critic workflows

### Multi-agent methods

- Joint-action learning, including `QLearningJAL`
- Models of other agents and prediction managers
- Individual and group prediction models
- Shared experience and centralized-critic training utilities

### Communication

- Agent communication modules
- Broadcast and filtered communication implementations
- Shared-observation and other experiment-specific communication designs

### Experiment infrastructure

- Schedulers with episode and evaluation criteria
- Launchers, viewers, logging, and evaluation hooks
- Generic launcher discovery and documentation navigation

## Experiments

Experiment source code is located under
`MLK-experiments/src/main/java/marlkit`. The following packages currently have
dedicated documentation:

- [Collecting Resource](MLK-experiments/src/main/java/marlkit/collectingresource/README.md)
- [Foraging](MLK-experiments/src/main/java/marlkit/foraging/README.md)
- [Go or Yield](MLK-experiments/src/main/java/marlkit/gooryield/README.md)
- [PreyHunter](MLK-experiments/src/main/java/marlkit/preyhunter/README.md)

Other scenario packages include PushTheBlock, PushTheBlockTogether, Listen or
Go, Up or Turn, Maze, Cross Escape, Team Battle, Team Surround, PreyHunter
Grid, and continuous Foraging. Their launchers can be explored through the
generic launcher menu.

## Creating a new experiment

When adding a scenario, the following workflow keeps scenario code and reusable
methods separate:

1. Define the environment state, observations, and actions.
2. Implement the environment dynamics and reaction events.
3. Choose or implement a reward model.
4. Choose the agent modules, policies, and learning algorithms.
5. Add communication or centralized training if the experiment requires it.
6. Implement a scheduler with episode, evaluation, and termination criteria.
7. Add a viewer or evaluator when visualization or custom measurements are
   useful.
8. Create a launcher that assembles the environment, agents, scheduler, and
   viewer.
9. Add documentation, figures, and tests for the new launcher.

Put reusable algorithms, communication mechanisms, prediction models, and
reward models in `MARL-Methods` when they are not specific to one scenario.
Put scenario-specific state, dynamics, and configuration in
`MLK-experiments`.

## Build and run

Run these commands from the repository root:

```bash
# Compile all subprojects and run the test suites
./gradlew build

# Run the generic JavaFX experiment launcher
./gradlew :MLK-experiments:run

# Run only the experiment-module tests
./gradlew :MLK-experiments:test

# Create an installable distribution of the experiments
./gradlew :MLK-experiments:installDist
```

The experiment module uses JavaFX for controls and documentation rendering,
and Gradle is configured with `marlkit.launcher.ExperimentLauncherMenu` as its
application main class. Individual launcher classes can also be started from
an IDE when developing or debugging a specific scenario.

## Testing and reproducibility

Tests are organized with the modules they exercise. When adding an experiment,
tests should cover at least:

- launcher discovery and metadata;
- resource packaging for README files and figures;
- important environment or reward-model behavior; and
- command construction or configuration for the selected launcher.

For comparisons between methods, keep the environment, episode criteria,
randomness configuration, and evaluation metrics explicit. Change one major
experimental factor at a time where possible, and record the resulting logs
and figures with the experiment documentation.

## Contributing

Before adding a class, decide whether it is:

- a general abstraction or reusable implementation for `MARLKIT`;
- a reusable MARL method for `MARL-Methods`; or
- a scenario-specific component for `MLK-experiments`.

Prefer composition over copying an existing experiment. If a new experiment
shares an environment mechanism, reward model, communication strategy, or
learning component with an existing one, extract that reusable part into the
appropriate module and document the resulting configuration.