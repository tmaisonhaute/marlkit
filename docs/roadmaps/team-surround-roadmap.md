# Team Surround Experiment Roadmap

## Scope
- Add a new experiment similar to team battle but movement-only.
- Implement death by local majority in 4-neighborhood.
- Support deterministic and stochastic death modes.
- Add team-level cooperative reward averaging by team.

## Steps
- [x] Create `AgentTeam`, `AgentTeam1`, `AgentTeam2` classes.
- [x] Create `GroupAgentsTeam1` and `GroupAgentsTeam2` classes.
- [x] Implement `RewardModelTeam` with per-team reward averaging.
- [x] Implement environment with movement, no-overlap, and surround death rule.
- [x] Add scheduler, terminal criterion, launcher, and viewer.
- [x] Export package in module descriptor.
- [ ] Run full Gradle build verification.

## Defaults
- Grid: `10x10`
- Team sizes: `10` and `10`
- Observation range: `3`
- Surround neighborhood: 4 direct neighbors
