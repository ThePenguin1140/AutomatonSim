package automata;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

public class EpsilonNFA extends Automaton {

	public EpsilonNFA(File inputFile) throws Exception {
		super(inputFile);
	}

	@Override
	public boolean containsWord(String word) {
		Set tmpSet = new Set(startStates);
		Iterator<State> iterator = tmpSet.iterator();
		while (iterator.hasNext()) {
			tmpSet.addAll(eClose(iterator.next()));
		}
		// System.out.println(tmpSet);
		tmpSet = transitionFunction(word, tmpSet);
		tmpSet.retainAll(finalStates);
		if (tmpSet.size()>1)
			return true;
		else
			return false;
	}

	private Set transitionFunction(String word, Set currentStates) {
		Set tmpSet = new Set(currentStates);
		if (word.length() == 1) {
			for (State state : currentStates) {
				tmpSet.addAll(state.transition(word));
				tmpSet.remove(state);
			}
			Iterator<State> it = tmpSet.iterator();
			while (it.hasNext())
				tmpSet.addAll(eClose(it.next()));
			return tmpSet;
		} else {
			for (State state : currentStates) {
				tmpSet.addAll(state.transition(String.valueOf(word.charAt(0))));
				tmpSet.remove(state);
			}
			Iterator<State> it = tmpSet.iterator();
			while (it.hasNext())
				tmpSet.addAll(eClose(it.next()));
			word = word.substring(1);
			tmpSet = (transitionFunction(word, tmpSet));
			return tmpSet;
		}
	}

	public Set eClose(State state) {
		Set closure = state.transition("?e");
		if (closure != null) {
			closure = eClose(state, closure);
			return closure;
		} else
			return new Set();
	}

	private Set eClose(Set set) {
		Set tmp = new Set();
		for (State s : set) {
			tmp.addAll(eClose(s));
		}
		return tmp;
	}

	private Set eClose(State state, Set closure) {
		if (closure.contains(state))
			return closure;
		else {
			for (State eState : closure) {
				Set newPath = eClose(eState, closure);
				for (State newState : newPath) {
					if (!closure.contains(newState))
						closure.add(newState);
				}
			}
			closure.add(state);
			return closure;
		}
	}

	public TreeSet<Set>	eCloseAutomaton(){
		TreeSet<Set> tmp = new TreeSet<Set>(treeComparator);
		for(State s: states){
			tmp.add(eClose(s));
		}
		return tmp;
	}

	public DFA convertToDFA(){
		Map<State, Set> transitionDFA = new HashMap<State, Set>();
		//String name = getSubsetName(eClose(startStates));
		String name = "0";
		State currentState = new State(name);
		Set currentSet = new Set();
		currentState.setStart(true);
		transitionDFA.put(currentState, eClose(startStates));
		TreeSet<String> alphaDFA = new TreeSet<String>(alphabet);
		alphaDFA.remove("?e");
		int index = 0;
		//construct subsets for each state
		do{
			// get the next subset from the construction
			currentSet = transitionDFA.get(new ArrayList<State>(transitionDFA.keySet()).get(index));
			// for letter in the alphabet
			for(String letter: alphaDFA){
				Set tmpSet = new Set();
				// explore the transition for every state in the subset 
				// and collect the destination in tmpSet
				for(State s: currentSet)
					tmpSet.addAll(s.transition(letter));
				// close the set
				tmpSet = eClose(tmpSet);
				//State tmpState = new State(getSubsetName(tmpSet));
				State tmpState = new State(String.valueOf(transitionDFA.size()));
				// let the new state inherit the start and final 
				// flags of it's parents
				for(State q: tmpSet){
					if(q.isFinal())
						tmpState.setFinal(true);
					if(q.isStart())
						tmpState.setFinal(true);
				}
				// if the constructed subset does not exist in the transition table
				// and it is not an empty subset then add it to the table
				if(!transitionDFA.containsValue(tmpSet) && !getSubsetName(tmpSet).isEmpty())
					transitionDFA.put(tmpState, tmpSet);
			}
			// increment the index since another subset has now been explored
			index++;
		}while(transitionDFA.size()>index);
		Set statesDFA = new Set();
		//add transitions to states
		// for every state in the dfa
		// add the transitions of it's parents to the state
		for(State s: transitionDFA.keySet()){
			// for every symbol in the dfa's alphabet
			for(String symbol: alphaDFA){
				// get the parents
				Set set = transitionDFA.get(s);
				Set tmp = new Set();
				// for every parent
				for(State q: set){
					// add the destination of the parent to a set
					tmp.addAll(q.transition(symbol));
				}
				// close the set
				tmp=eClose(tmp);
				// WORK AROUND
				// if the subset doesn't exist in the transition table
				// (which it really shouldn't) then add it.
				//	if(!transitionDFA.values().contains(tmp))
				//	transitionDFA.put(s, tmp);
				
				// get the subsets from the table
				ArrayList<Set> values = new ArrayList<Set>(transitionDFA.values());
				// and find the index of the destination subset of the current transition
				int location = values.indexOf(tmp);
				if(location==-1)
					continue;
				// then get all the states from the table
				ArrayList<State> keys = new ArrayList<State>(transitionDFA.keySet());
				// and find the one that represents the subset
				State destination = keys.get(location);
				// then link the letter from the alphabet to the destination subset
				s.setTransition(symbol, destination);
			}
			statesDFA.add(s);
		}
		//System.out.println(statesDFA);
		Set startStatesDFA = new Set();
		//add start states to start states set
		for(State s: statesDFA){
			if(s.isStart())
				startStatesDFA.add(s);
		}
		Set finalStatesDFA = new Set();
		//add final states to final states set
		for(State s: statesDFA){
			if(s.isFinal())
				finalStatesDFA.add(s);
		}
		return new DFA(statesDFA, startStatesDFA, finalStatesDFA, alphaDFA);
	}

	private String getSubsetName(Set set){
		String name = "";
		for(State s: set)
			name += s.getName()+",";
		if(name.length()>1)
			return name.substring(0, name.length()-1);
		else
			return name;
	}
}
