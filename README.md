MARL Kit - MaDKit - version 0.9
================================================

**MARLKIT is an open source MARL library written in Java.**

It is designed as a lightweight Java library for testing MARL approaches.
MARLKIT is a framework designed around core modules. Each core module represents a crucial part of MARL. By making core modules easier to isolate, reuse, and compare, MARLKIT contributes to more reproducible and controlled experimentation in MARL.

## Features

### Core architecture
* Modular agent and environment contracts with standard implementations
* Action, observation, and state abstractions (including 2D grid and position/value observations)
* Reward and reaction-event pipeline for environment-driven rewards

### Learning and policies
* Classic RL algorithms: QLearning, Sarsa, MonteCarlo, Reinforce, ActorCritic
* Policies and exploration strategies: QValueBasedPolicy, SoftmaxQPolicy, epsilon-greedy decay
* Neural network components for actor-critic workflows

### Multi-agent modeling and coordination
* Models of other agents (per-agent and group predictors, prediction managers)
* Joint-action learning (QLearningJAL) and deterministic prediction models
* Centralized training utilities (shared experience and centralized critic schedulers)

### Communication
* Communication modules and broadcast/filter implementations for agent messaging

### Simulation tooling
* Schedulers, launchers, and viewers with criteria-based episode control
* Logging and evaluation hooks for experiments

### Experiments
* A set of ready-to-run experiments and scenarios in MLK-experiments (collectingresource, maze, teamsurround, and more)


