package automata;

import java.util.TreeSet;

@SuppressWarnings("serial")
public class Set extends TreeSet<State> {

	public Set(Set set) {
		super(set);
	}

	public Set() {
		super();
	}

	public State get(State s) {
		return this.tailSet(s).first();
	}
}
