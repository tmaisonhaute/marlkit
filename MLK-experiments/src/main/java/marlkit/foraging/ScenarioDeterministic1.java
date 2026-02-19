package marlkit.foraging;

import util.Pair;

public class ScenarioDeterministic1 extends ScenarioDeterministic {
	@SuppressWarnings("unchecked")
	private static final Pair<Integer, Integer>[] resources = new Pair[] {
			new Pair<>(4, 0),
			new Pair<>(1, 1),
			new Pair<>(4, 1)
	};
	@SuppressWarnings("unchecked")
	private static final Pair<Integer, Integer>[] agents = new Pair[] {
			new Pair<>(3, 1),
			new Pair<>(0, 4)
	};

	public ScenarioDeterministic1() {
		super(resources, agents);
	}

}
