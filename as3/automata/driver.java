package automata;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class driver {
	public static void main(String[] agrs) {
		Scanner sc = new Scanner(System.in);
		File file = null;
		String fileName, transition;
		BufferedWriter writer;
		int input = 0;
		do{
			System.out.print(startMenu());
			input = sc.nextInt();
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
					System.out.println(e.toString());
					break;
				}
				System.out.println("nfa successfully scanned.");
				clearConsole();
				if(nfaMenu(nfa)==0)
					input=0;
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
					System.out.println(e.toString());
					break;
				}
				System.out.println("nfa successfully scanned.");
				clearConsole();
				if(dfaMenu(dfa)==0)
					input = 0;
				break;
			default:
				break;
			}
			clearConsole();
		}while(input!=0);
		sc.close();		
	}

	private static String startMenu(){
		return	"1. Enter NFA\n"+
				"2. Enter DFA\n"+
				"3. Scan NFA from file\n"+
				"4. Scan DFA from file\n"+alwaysMenu();
	}
	
	private static int nfaMenu(EpsilonNFA nfa){
		String menu = 	nfa.toString()+"\n\n"+
				"1. Compute the Epsilon Closure\n"+
				"2. Enter words\n"+
				"3. Print out Alphabet\n"+
				"4. Convert to DFA\n"+
				"5. Print GRAIL format transitions\n"+
				"6. Back\n"+alwaysMenu();
		Scanner nfaScan = new Scanner(System.in);
		int input;
		String answer;
		do{
			System.out.println(menu);
			input = nfaScan.nextInt();
			switch(input){
			case 1:
				System.out.print("What state would you like to close?>");
				input = nfaScan.nextInt();
				System.out.println();
				Set eClose = (nfa.eClose(nfa.getState(String.valueOf(input))));
				for(State s: eClose)
					System.out.print(s.getName()+",");
				System.out.println();
				System.out.println("Would you like to close another state?>");
				nfaScan.nextLine();
				answer = nfaScan.nextLine();
				if(answer.toLowerCase().charAt(0)=='y'){
					input = 1;
					continue;
				}
				else
					break;
			case 2:
				System.out.print("Please enter a word: ");
				nfaScan.nextLine();
				String word = nfaScan.nextLine();
				System.out.println();
				System.out.println(nfa.containsWord(word));
				break;
			case 3:
				System.out.println(nfa.alphabet);
				break;
			case 4:
				DFA dfa = nfa.convertToDFA();
				if(dfaMenu(dfa)==0)
					input = 0;
				break;
			case 5:
				System.out.println(nfa.grailFormat());
				break;
			default:
				break;
			}
		}while(input!=0 && input != 6);
		//can't close? makes sc in main throw an error
		//nfaScan.close();
		return input;
	}
	
	private static int dfaMenu(DFA dfa){
		String menu =	dfa.toString()+"\n\n"+
				"1. Enter Words\n"+
				"2. Print out Alphabet\n"+
				"3. Print GRAIL transition table\n"+
				"4. Back\n"+alwaysMenu();
		Scanner dfaScan = new Scanner(System.in);
		int input;
		String answer;
		do{
			System.out.println(menu);
			input = dfaScan.nextInt();
			switch(input){
			case 1:
				System.out.print("Please enter a word: ");
				dfaScan.nextLine();
				String word = dfaScan.nextLine();
				System.out.println();
				System.out.println(dfa.containsWord(word));
				break;
			case 2:
				System.out.println(dfa.alphabet);
				break;
			case 3:
				System.out.println(dfa.grailFormat());
				break;
			default:
				break;
			}
		}while(input!=0 && input != 4);
		//dfaScan.close();
		return input;
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
