package automata;

import java.io.File;
import java.util.TreeSet;

public class DFA extends Automaton {

	/*
	 * Reads a file using GRAIL+ formatting to build an automaton
	 */
	public DFA(File inputFile) throws Exception {
		super(inputFile);
		if(!checkIfDFA(states)){
			throw new Exception("Not a DFA");
		}
	}

	public DFA(Set states, Set startStates, Set finalStates,
			TreeSet<String> alphabet) {
		super(states, startStates, finalStates, alphabet);
	}

	private boolean checkIfDFA(Set states) {
		for (State s : states) {
			for(String letter: alphabet){
				if(s.transition(letter).size()>1)
					return false;
			}
		}
		return true;
	}
}
