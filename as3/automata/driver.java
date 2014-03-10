package automata;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class driver {
	public static void main(String[] agrs) {
		Scanner sc = new Scanner(System.in);
		System.out.print(startMenu());
		int input = sc.nextInt();
		File file = null;
		String fileName, transition;
		BufferedWriter writer;
		while(input!=0){
			switch(input){
			case 1:
				//enter nfa
				System.out.println("Please enter NFA transitions in GRAIL format.\n"+
						"One space between all separators (START, FINAL, |-, -|), transitions, and transitions symbols.\n"+
						"Use ?e as epsilon.\n");
				sc.nextLine();
				transition = sc.nextLine();
				writer = null;
				try {
					file = File.createTempFile("input", ".txt");
					file.deleteOnExit();
					file.setReadable(true);
					file.setWritable(true);
					writer = new BufferedWriter(new FileWriter(file, true));
					while(transition.trim().length()!=0){
						writer.write(transition+"\n");
						transition = sc.nextLine();
					}
					writer.close();
					nfaMenu(new EpsilonNFA(file));
				} catch (IOException e1) {
					System.out.println("Creation error");
				} catch (Exception e2) {
					System.out.println(e2.toString());
				}
				file.delete();
				break;
			case 2:
				//enter dfa
				System.out.println("Please enter DFA transitions in GRAIL format.\n"+
									"One space between all separators (START, FINAL, |-, -|), transitions, and transitions symbols.\n");
				sc.nextLine();
				transition = sc.nextLine();
				writer = null;
				try {
					file = File.createTempFile("input", ".txt");
					file.deleteOnExit();
					file.setWritable(true);
					file.setReadable(true);
					writer = new BufferedWriter(new FileWriter(file, true));
					while(!transition.isEmpty()){
						writer.write(transition+"\n");
						transition = sc.nextLine();
					}
					writer.close();
					dfaMenu(new DFA(file));
				} catch (IOException e1) {
					System.out.println("Creation error");
				} catch (Exception e1) {
					System.out.println(e1.toString());
				}
				file.delete();
				break;
			case 3:
				System.out.println("Please enter the file with the transitions\n"+
								"for a NFA in GRAIL formatting(NO SPACES):");
				System.out.println("Starting in directory: "+System.getProperty("user.dir"));
				fileName = sc.next();
				file = null;
				EpsilonNFA nfa = null;
				try {
					if(fileName.contains("/")||fileName.contains("\\"))
						file = new File(fileName);
					else
						file = new File(System.getProperty("user.dir")+
									System.getProperty("file.separator")+fileName);
					nfa = new EpsilonNFA(file);
				} catch (Exception e) {
					break;
				}
				System.out.println("nfa successfully scanned.");
				clearConsole();
				nfaMenu(nfa);
				break;
			case 4:
				System.out.println("Please enter the file with the transitions\n"+
						"for a DFA in GRAIL formatting(NO SPACES):");
				System.out.println("Starting in directory: "+System.getProperty("user.dir"));
				fileName = sc.next();
				file = null;
				DFA dfa = null;
				try {
					if(fileName.contains("/")||fileName.contains("\\"))
						file = new File(fileName);
					else
						file = new File(System.getProperty("user.dir")+
								System.getProperty("file.separator")+fileName);
					dfa = new DFA(file);
				} catch (Exception e) {
					break;
				}
				System.out.println("nfa successfully scanned.");
				clearConsole();
				dfaMenu(dfa);;
				break;
			default:
				break;
			}
			clearConsole();
			System.out.println(startMenu());
			input = sc.nextInt();
		}
		sc.close();		
	}

	private static String startMenu(){
		return	"1. Enter NFA\n"+
				"2. Enter DFA\n"+
				"3. Scan NFA from file\n"+
				"4. Scan DFA from file\n"+alwaysMenu();
	}
	
	private static void nfaMenu(EpsilonNFA nfa){
		String menu = 	nfa.toString()+"\n\n"+
				"1. Compute the Epsilon Closure\n"+
				"2. Enter words\n"+
				"3. Print out Alphabet\n"+
				"4. Convert to DFA\n"+
				"5. Back\n"+alwaysMenu();
		
		System.out.println(menu);
		Scanner sc = new Scanner(System.in);
		int input = sc.nextInt();
		String answer;;
		while(input!=0 && input != 5){
			switch(input){
			case 1:
				System.out.print("What state would you like to close?>");
				input = sc.nextInt();
				System.out.println();
				Set eClose = (nfa.eClose(nfa.getState(String.valueOf(input))));
				for(State s: eClose)
					System.out.print(s.getName()+",");
				System.out.println();
				System.out.println("Would you like to close another state?>");
				sc.nextLine();
				answer = sc.nextLine();
				if(answer.toLowerCase().charAt(0)=='y'){
					input = 1;
					continue;
				}
				else
					break;
			case 2:
				System.out.print("Please enter a word: ");
				sc.nextLine();
				String word = sc.nextLine();
				System.out.println();
				System.out.println(nfa.containsWord(word));
				System.out.println("Would you like to test another word?>");
				answer = sc.nextLine();
				if(answer.toLowerCase().charAt(0)=='y'){
					input = 1;
					continue;
				}
				else
					break;
			case 3:
				System.out.println(nfa.alphabet);
				break;
			case 4:
				DFA dfa = nfa.convertToDFA();
				dfaMenu(dfa);
				break;
			default:
				break;
			}
			System.out.println(menu);
			input = sc.nextInt();
		}
		sc.close();
	}
	
	private static void dfaMenu(DFA dfa){
		String menu =	dfa.toString()+"\n\n"+
				"1. Enter Words\n"+
				"2. Print out Alphabet\n"+
				"3. Back\n"+alwaysMenu();
		System.out.println(menu);
		Scanner sc = new Scanner(System.in);
		int input = sc.nextInt();
		String answer;;
		while(input!=0 && input != 3){
			switch(input){
			case 1:
				System.out.print("Please enter a word: ");
				sc.nextLine();
				String word = sc.nextLine();
				System.out.println();
				System.out.println(dfa.containsWord(word));
				System.out.println("Would you like to test another word?>");
				answer = sc.nextLine();
				if(answer.toLowerCase().charAt(0)=='y'){
					input = 1;
					continue;
				}
				else
					break;
			case 2:
				System.out.println(dfa.alphabet);
				break;
			default:
				break;
			}
			System.out.println(menu);
			input = sc.nextInt();
		}
		sc.close();
	}
	
	private static String alwaysMenu(){
		 return "0. Quit\n"+
			 	"What would you like to do? > ";
	}
	
	private static void clearConsole()
	{
	    try
	    {
	        String os = System.getProperty("os.name");

	        if (os.contains("Windows"))
	        {
	            Runtime.getRuntime().exec("cls");
	        }
	        else
	        {
	            Runtime.getRuntime().exec("clear");
	        }
	    }
	    catch (Exception exception)
	    {
	       // don't do or display anything, just give up hope
	    }
	}
}