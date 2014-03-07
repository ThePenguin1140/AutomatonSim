package automata;

import java.io.File;
import java.util.Scanner;
import java.util.TreeSet;

public class driver {
	public static void main(String[] agrs) {
		File file = new File(System.getProperty("user.dir")
			+ "/as3/as3eNFAtranstbl.txt");
		EpsilonNFA nfa = new EpsilonNFA(file);
		System.out.println(nfa);
		DFA dfa = nfa.convertToNFA();
		System.out.println(dfa);
		System.out.println(dfa.grailFormat());
		TreeSet<Set> tmp = new TreeSet<Set>();
		tmp = nfa.eCloseAutomaton();
		for(Set a: tmp){
			for(State s: a){
				System.out.print(s.getName()+" ");
			}
			System.out.println();
		}
		Scanner sc = new Scanner(System.in);
		String word = sc.next();
		while(!word.equalsIgnoreCase("quit")){
			System.out.println(nfa.containsWord(word)+" | "+dfa.containsWord(word));
			word = sc.next();
		}
		sc.close();		
	}
}
