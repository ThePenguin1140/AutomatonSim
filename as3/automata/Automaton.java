package automata;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Comparator;
import java.util.Iterator;
import java.util.Scanner;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Automaton {
	Set states = new Set();
	Set startStates = new Set();
	Set finalStates = new Set();
	TreeSet<String> alphabet = new TreeSet<String>();

	// comparator used to compare two sets containing sets of states
	Comparator<Set> treeComparator = new Comparator<Set>() {
		@Override
		public int compare(Set lhs, Set rhs) {
			Iterator<State> li = lhs.iterator();
			Iterator<State> ri = rhs.iterator();
			while (li.hasNext() && ri.hasNext()) {
				int res = li.next().compareTo(ri.next());
				if (res != 0)
					return res;
			}
			if (li.hasNext())
				return 1;
			if (ri.hasNext())
				return -1;
			return 0;
		}
	};

	/*
	 * Reads a file using GRAIL+ formatting to build an automaton
	 */
	public Automaton(File inputFile) {
		// scan file here
		Scanner fileSc = null;
		// try to open file
		try {
			fileSc = new Scanner(inputFile);
		} catch (FileNotFoundException except) {
			System.out.println("FILE NOT FOUND!!");
		}
		// while there are still lines in the file
		while (fileSc.hasNextLine()) {
			String line = fileSc.nextLine();
			Scanner lineSc = new Scanner(line);
			// regex patterns used to identify special parts of the file
			Pattern start = Pattern.compile("\\((START)\\)");
			Pattern end = Pattern.compile("\\((FINAL)\\)");
			Pattern seperator = Pattern.compile("(-\\||\\|-)");
			// regex matcher that identify start and end states
			Matcher endMatcher = end.matcher(line);
			Matcher startMatcher = start.matcher(line);
			// if the line describes a start state
			if (startMatcher.find()) {
				// skip over the start tag and the separator
				lineSc.next(start);
				lineSc.next(seperator);
				// read the state number
				String inputState = lineSc.next();
				// make a new state
				State startState = new State(inputState);
				// if the state exists
				if (states.contains(startState))
					// get it and make that state a start state
					states.tailSet(startState).first().setStart(true);
				else {
					// otherwise make a new start state and add it
					startState.setStart(true);
					states.add(startState);
				}
				// System.out.println(startState);
			}
			// IF there is a (FINAL) tag on the line
			else if (endMatcher.find()) {
				// read the state
				String inputState = lineSc.next();
				// skip the separators
				lineSc.next(seperator);
				lineSc.next(end);
				// make a new state
				State endState = new State(inputState);
				// IF the state exists then find it and make that state FINAL
				if (states.contains(endState))
					states.tailSet(endState).first().setFinal(true);
				// ELSE add a new FINAL state
				else {
					endState.setFinal(true);
					states.add(endState);
				}
				// System.out.println(endState);
				/*
				 * ELSE, if there is not tags on the line, it describes a
				 * transition in the form a & b where a,b are states and & is a
				 * letter found in the alphabet
				 */
			} else {
				State startState = new State(lineSc.next());
				String transitionSymbol = lineSc.next();
				// if the current alphabet does not contain
				// the letter than add it to the alphabet
				if (!alphabet.contains(transitionSymbol))
					alphabet.add(transitionSymbol);
				State endState = new State(lineSc.next());
				// if the end or start state does not exists
				// then add it
				if (!states.contains(endState))
					states.add(endState);
				if (!states.contains(startState))
					states.add(startState);
				// link the end state to the start state with the letter
				// set.tailSet(state).first() gets the state from the tree
				// by returning the sub tree beginning with the state
				// and then the first item of the subtree
				states.tailSet(startState)
						.first()
						.setTransition(transitionSymbol,
								states.tailSet(endState).first());
			}
			lineSc.close();
		}
		fileSc.close();

		/*
		 * for every state in the automaton add the start states to the start
		 * set and the final states to the final set NOTE: finalStates and
		 * startStates are subsets of states
		 */
		for (State state : states) {
			if (state.isStart() && !startStates.contains(state))
				startStates.add(state);
			if (state.isFinal() && !finalStates.contains(state))
				finalStates.add(state);
		}
	}

	public Automaton(Set states, Set startStates, Set finalStates,
			TreeSet<String> alphabet) {
		this.states = states;
		this.startStates = startStates;
		this.finalStates = finalStates;
		this.alphabet = alphabet;
	}

	public boolean containsWord(String word) {
		State tmpState = transitionFunction(word, startStates.first());
		if (finalStates.contains(tmpState))
			return true;
		else
			return false;
	}

	private State transitionFunction(String word, State currentState) {
		if (word.length() == 1) {
			return currentState.transition(word).first();
		} else {
			currentState = currentState.transition(String.valueOf(word.charAt(0))).first();
			return transitionFunction(word.substring(1), currentState);
		}
	}

	public State getState(String name) {
		State requestState = new State(name);
		return states.tailSet(requestState).first();
	}

	public String grailFormat(){
		String output = "Grail Transitions:";
		for(State s: states){
			for(String symbol: alphabet){
				output += s.getName() + " " + symbol + " ";
				for(State q: s.transition(symbol))
					output += q.getName();
				output += "\n";
			}
		}
		return output;
	}

	@Override
	public String toString() {
		String output = "\t\t";
		for (String letter : alphabet)
			output += letter + "\t";
		output += "\n";
		for (State state : states) {
			if (state.isStart())
				output += "->\t" + state.getName() + "\t";
			else if (state.isFinal())
				output += "*\t" + state.getName() + "\t";
			else
				output += "\t" + state.getName() + "\t";
			for (String letter : alphabet) {
				Set transStates = state.transition(letter);
				if (transStates == null)
					output += "";
				else
					for (State state2 : transStates)
						output += state2.getName() + " ";
				output += "\t";
			}
			output += "\n";
		}
		return output;
	}
}
