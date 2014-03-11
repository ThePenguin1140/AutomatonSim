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
		String name = getSubsetName(eClose(startStates));
		State currentState = new State(name);
		Set currentSet = new Set();
		currentState.setStart(true);
		transitionDFA.put(currentState, eClose(startStates));
		TreeSet<String> alphaDFA = new TreeSet<String>(alphabet);
		alphaDFA.remove("?e");
		int index = 0;
		//construct subsets for each state
		do{
			currentSet = transitionDFA.get(new ArrayList<State>(transitionDFA.keySet()).get(index));
			for(String symbol: alphaDFA){
				Set tmpSet = new Set();
				for(State s: currentSet){
					tmpSet.addAll(s.transition(symbol));
				}
				// close the set
				tmpSet = eClose(tmpSet);
				State tmpState = new State(getSubsetName(tmpSet));
				// let the new state inherit the start and final 
				// flags of it's parents
				for(State s: tmpSet){
					if(s.isFinal())
						tmpState.setFinal(true);
					if(s.isStart())
						tmpState.setFinal(true);
				}
				// if the constructed subset does not exist in the transition table 
				// then add it
				if(!transitionDFA.containsValue(tmpSet))
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
			// if the states name is empty then don't bother adding it
			if(s.getName().isEmpty())
				continue;
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
				if(!transitionDFA.values().contains(tmp))
					transitionDFA.put(s, tmp);
				ArrayList<Set> values = new ArrayList<Set>(transitionDFA.values());
				int location = values.indexOf(tmp);
				ArrayList<State> keys = new ArrayList<State>(transitionDFA.keySet());
				State destination = keys.get(location);
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
