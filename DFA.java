package automata;

import java.io.File;
import java.util.TreeSet;

public class DFA extends Automaton {

	/*
	 * Reads a file using GRAIL+ formatting to build an automaton
	 */
	public DFA(File inputFile) {
		super(inputFile);
		if(checkIfDFA(states))
			System.out.println("not a dfa");
	}

	public DFA(Set states, Set startStates, Set finalStates,
			TreeSet<String> alphabet) {
		super(states, startStates, finalStates, alphabet);
	}

	private boolean checkIfDFA(Set states) {
		for (State s : states) {
			for (Set set : s.getTransitions().values()) {
				if (set.size() > 1)
					return false;
			}
		}
		return true;
	}
}
