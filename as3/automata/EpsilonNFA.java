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
		if (tmpSet.size() > 0)
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
			tmpSet = (transitionFunction(word.substring(1), tmpSet));
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
	
	//TODO show subsets
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
			//currentSet = transitionDFA.get(new State(String.valueOf(index)));
			currentSet = transitionDFA.get(new State(name));
			for(String symbol: alphaDFA){
				Set tmpSet = new Set();
				for(State s: currentSet){
					tmpSet.addAll(s.transition(symbol));
				}
				tmpSet = eClose(tmpSet);
				//State tmpState = new State(String.valueOf(transitionDFA.size()));
				State tmpState = new State(getSubsetName(tmpSet));
				for(State s: tmpSet){
					if(s.isFinal())
						tmpState.setFinal(true);
					if(s.isStart())
						tmpState.setFinal(true);
				}
				if(!transitionDFA.containsValue(tmpSet))
					transitionDFA.put(tmpState, tmpSet);
				name = getSubsetName(tmpSet);
			}
			index++;
		}while(transitionDFA.size()>index);
		Set statesDFA = new Set();
		//add transitions to states
		//System.out.println(transitionDFA);
		for(State s: transitionDFA.keySet()){
			for(String symbol: alphaDFA){
				Set set = transitionDFA.get(s);
				Set tmp = new Set();
				for(State q: set){
					tmp.addAll(q.transition(symbol));
				}
				tmp=eClose(tmp);
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
		return name.substring(0, name.length()-2);
	}
}
