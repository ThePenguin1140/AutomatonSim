package automata;

import java.util.HashMap;
import java.util.Map;

public class State implements Comparable<State> {
	private Map<String, Set> transitions = new HashMap<String, Set>();
	// private String epsilon = "?e";
	private boolean isFinal = false;
	private boolean isStart = false;
	private String name = null;

	public State() {
	} // blank constructor

	public State(String name) {
		this.name = name;
	}

	public State(State parent, String name) {
		this.isFinal = parent.isFinal();
		this.isStart = parent.isStart();
		this.name = name;
	}

	public State(boolean isFinal, boolean isStart, String name) {
		super();
		this.isFinal = isFinal;
		this.name = name.trim();
		this.isStart = isStart;
	}

	public boolean isFinal() {
		return isFinal;
	}

	public void setFinal(boolean isFinal) {
		this.isFinal = isFinal;
	}

	public boolean isStart() {
		return isStart;
	}

	public void setStart(boolean isStart) {
		this.isStart = isStart;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean setTransition(String transitionSymbol, State destination) {
		Set values = new Set();
		if (transitions.containsKey(transitionSymbol))
			values = transitions.get(transitionSymbol);
		values.add(destination);
		if (transitions.put(transitionSymbol, values) != null)
			return true;
		else
			return false;
	}

	public boolean setTransition(String transitionSymbol, Set desitination) {
		if (transitions.put(transitionSymbol, desitination) != null)
			return true;
		else
			return false;
	}

	public Map<String, Set> getTransitions() {
		return transitions;
	}

	public void clearTransitions() {
		transitions.clear();
	}

	public Set transition(String input) {
		// System.out.println(transitions.get(input));
		if (transitions.get(input) == null)
			return new Set();
		else
			return transitions.get(input);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		State other = (State) obj;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		return true;
	}

	@Override
	public String toString() {
		String output = "";
		if (isStart)
			output += "(START)\t";
		else if (isFinal)
			output += "(FINAL)\t";
		else
			output += "\t";
		for (String key : transitions.keySet()) {
			output += key + "\t";
		}
		output += "\n" + name + "\t";
		for (String key : transitions.keySet()) {
			output += "(";
			for (State state : transitions.get(key)) {
				output += state.name + ",";
			}
			output = output.substring(0, output.length() - 1); // trims trailing
																// comma
			output += ")\t";
		}
		return output + "\n";
	}

	@Override
	public int compareTo(State arg0) {
		return name.compareTo(arg0.getName());
	}
}
