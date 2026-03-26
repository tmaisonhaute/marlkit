# Team Battle Experiment Roadmap

## Scope
- Add a new large-scale team battle experiment in `MLK-experiments`.
- Keep MARLKit experiment structure consistent with existing packages.

## Steps
- [x] Define reward events matching requested values.
- [x] Add custom action type to support move or attack decisions.
- [x] Implement environment with HP, regen, attack, team logic, and no-overlap movement.
- [x] Add terminal criterion for team elimination.
- [x] Add scheduler, agent, launcher, and viewer.
- [x] Export new package in module descriptor.
- [ ] Run full Gradle build verification.

## Defaults Implemented
- Map size: `20x20`
- Observation range: `3` cells (Von Neumann)
- HP: `10.0`
- Attack damage: `2.0`
- Regen per turn: `0.1`
