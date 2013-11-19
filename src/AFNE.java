import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;

public class AFNE {
	private static int NSYMBOLS = 0;
	public static final char EPSILON = '~';
	private static boolean HASLOADEDDICT = false;
	
	private static HashMap<String, Integer> symbols;
	private static HashMap<Integer, String> inverseIndex;
	
	private ArrayList<ArrayList<Integer>[]> G;
	private ArrayList<Boolean> finals;
	private int initialState;

	//AFNE functions
	public AFNE() throws FileNotFoundException {
		if (!HASLOADEDDICT) {
			HASLOADEDDICT = true;
			Scanner dictFile = new Scanner(new FileReader("symbols.txt"));
			symbols = new HashMap<String, Integer>();
			inverseIndex = new HashMap<Integer, String>();
			
			for (String symbol : dictFile.nextLine().split(",")) {
				symbols.put(symbol, NSYMBOLS);
				inverseIndex.put(NSYMBOLS, symbol);
				NSYMBOLS++;
			}
		}
		
		finals = new ArrayList<Boolean>();
		G = new ArrayList<ArrayList<Integer>[]>();
	}

	public AFNE(Postfix expression) throws Exception {
		this();
		evaluatePostfix(expression);
	}

	public AFNE(String word) throws FileNotFoundException {
		this();

		int current, previous;
		char c;
		initialState = 0;
		previous = current = addState(false);

		for (int i = 0; i < word.length(); i++) {
			current = addState(false);		
			c = word.charAt(i);

			addTransition(previous, current, c);
			previous = current;
		}

		finals.set(current, true);
	}

	public int getStateCount() {
		return G.size();
	}

	public AFNE concatenate(AFNE operand) {
		mergeTables(operand);
		for (int i = 0; i < finals.size(); i++) {
			if (finals.get(i)) {
				addTransition(i, operand.initialState, EPSILON);
				finals.set(i, false);
			}
		}
		finals.addAll(operand.finals);
		return this;
	}

	public AFNE union(AFNE operand) {		
		finals.addAll(operand.finals);

		mergeTables(operand);

		int newInitialState = addState(false);

		addTransition(newInitialState, initialState, EPSILON);
		addTransition(newInitialState, operand.initialState, EPSILON);
		
		initialState = newInitialState;

		int newFinalState = addState(true);

		for (int i = 0; i < finals.size() - 1; i++) {
			if (finals.get(i)) {
				addTransition(i, newFinalState, EPSILON);
				finals.set(i, false);
			}
		}
		return this;
	}

	public AFNE kleenClosure() {
		int newInitialState = addState(false);
		int newFinalState = addState(true);

		addTransition(newInitialState, newFinalState, EPSILON);
		addTransition(newInitialState, initialState, EPSILON);

		for (int i = 0; i < finals.size() - 1; i++) {
			if (finals.get(i)) {
				addTransition(i, initialState, EPSILON);
				addTransition(i, newFinalState, EPSILON);
				finals.set(i, false);
			}
		}

		initialState = newInitialState;
		return this;
	}

	public AFNE positiveClosure() {
		int newInitialState = addState(false);
		int newFinalState = addState(true);

		addTransition(newInitialState, initialState, EPSILON);

		for (int i = 0; i < finals.size() - 1; i++) {
			if (finals.get(i)) {
				addTransition(i, initialState, EPSILON);
				addTransition(i, newFinalState, EPSILON);
				finals.set(i, false);
			}
		}

		initialState = newInitialState;
		return this;
	}

	private int addState(boolean finalState) {
		ArrayList<Integer>[] row = new ArrayList[NSYMBOLS];
		finals.add(finalState);

		for (int i = 0; i < NSYMBOLS; i++) {
			row[i] = new ArrayList<Integer>();
		}
		G.add(row);

		return G.size() - 1;
	}

	private void removeLastState() {
		G.remove(G.size() - 1);
	}
	
	private void evaluatePostfix(Postfix expression) throws Exception {
		Stack<AFNE> operands = new Stack<AFNE>();
		ArrayList<String> exp = expression.getPostfix();
		AFNE op1 = null, op2;

		for (String s : exp) {
			if (isOperator(s)) {
				switch (s) {
				case ",":
					op2 = operands.pop();
					op1 = operands.pop();
					op1.union(op2);
					break;
				case "&":
					op2 = operands.pop();
					op1 = operands.pop();
					op1.concatenate(op2);
					break;
				case "+":
					op1 = operands.pop();
					op1.positiveClosure();
					break;
				case "*":
					op1 = operands.pop();
					op1.kleenClosure();
					break;
				default:
					throw new Exception("Something went wrong");
				}

				operands.push(op1);
			}
			else {
				operands.push(new AFNE(s));
			}
		}

		this.G = op1.G;
		this.finals = op1.finals;
		this.initialState = op1.initialState;
	}

	private int convert(String c) {
		return symbols.get(c);
	}
	
	private int convert(char c) {
		return symbols.get(String.valueOf(c));
	}

	private void addTransition(int source, int destination, char symbol) {
		if (symbol == '.') {
			for (String current : symbols.keySet()) {
				if (!current.equals(String.valueOf(EPSILON))) {
					G.get(source)[convert(current)].add(destination);
				}
			}
		}
		else {
			G.get(source)[convert(symbol)].add(destination);
		}
	}
	
	private boolean isOperator(String operator){
		return operator.equals("+") 
				|| operator.equals("*")
				|| operator.equals(",")
				|| operator.equals("&");
	}

	private void mergeTables(AFNE afne) {
		addOffset(afne, getStateCount());

		for (ArrayList<Integer>[] row : afne.G) {
			G.add(row);
		}
	}

	private void addOffset(AFNE afne, int offset) {
		ArrayList<ArrayList<Integer>[]> table = afne.G;
		afne.initialState += offset;

		for (ArrayList<Integer>[] row : table) {
			for (ArrayList<Integer> cell : row) {
				for (int i = 0; i < cell.size(); i++) {
					cell.set(i, cell.get(i) + offset);
				}
			}
		}
	}
	
	private ArrayList<Integer> getTransitions(int state, char symbol) {
		return G.get(state)[convert(symbol)];
	}
	
	private ArrayList<Integer> getTransitions(int state, int symbolIndex) {
		return G.get(state)[symbolIndex];
	}
	
	//Convert to AFD
	private HashSet<Integer> epsilonClosure(int state) {
		HashSet<Integer> states = new HashSet<Integer>();
		epsilonClosure(state, states);
		return states;
	}
	
	private void epsilonClosure(int state, HashSet<Integer> states) {
		states.add(state);
		
		for (int child : getTransitions(state, EPSILON)) {
			if (!states.contains(child)) {
				epsilonClosure(child, states);
			}
		}
	}
	
	public AFNE convertToAFD() throws FileNotFoundException {
		HashSet<Integer> current = epsilonClosure(initialState);
		
		AFNE result = new AFNE();
		
		HashMap<HashSet<Integer>, Integer> reverseIndex = new HashMap<>();
		ArrayList<HashSet<Integer>> states = new ArrayList<>();
		ArrayList<HashSet<Integer>[]> table = new ArrayList<>();
		ArrayList<Boolean> afdFinals = new ArrayList<>();
		
		states.add(current);
		reverseIndex.put(current, 0);
		afdFinals.add(false);
		for (int finalStates : current) {
			if (finals.get(finalStates)) {
				afdFinals.set(0, true);
				break;
			}
		}
		
		for (int i = 0; i < states.size(); i++) {
			table.add(new HashSet[NSYMBOLS]);
			
			for (int j = 0; j < NSYMBOLS - 1; j++) {
				table.get(i)[j] = new HashSet<>();
				for (int state : states.get(i)) {
					current = new HashSet<Integer>();
					
					for (int transition : getTransitions(state, j)) {
						current.addAll(epsilonClosure(transition));
					}
					
					table.get(i)[j].addAll(current);
				}
				current = table.get(i)[j];
				
				if (!reverseIndex.containsKey(current)) {
					reverseIndex.put(current, states.size());
					states.add(current);
					afdFinals.add(false);
					for (int finalStates : current) {
						if (finals.get(finalStates)) {
							afdFinals.set(states.size() - 1, true);
							break;
						}
					}
				}
			}
		}
		
		result.finals = afdFinals;
		result.initialState = 0;
		result.G = new ArrayList<ArrayList<Integer>[]>();
		
		for (int i = 0; i < states.size(); i++) {
			result.G.add(new ArrayList[NSYMBOLS]);
			for (int j = 0; j < NSYMBOLS; j++) {
				result.G.get(i)[j] = new ArrayList<>();
				result.G.get(i)[j].add(reverseIndex.get(table.get(i)[j]));
			}
			
			result.G.get(i)[NSYMBOLS - 1] = new ArrayList<>();
		}
		
		return result;
	}

	@Override
	public String toString() {
		String output = "   ";
		int i = 0;

		for (int j = 0; j < NSYMBOLS; j++) {
			output += inverseIndex.get(j) + " ";
		}
		output += "\n";

		for (ArrayList<Integer>[] row : G) {
			if(finals.get(i)){
				output += "*" + i++ + " ";
			}
			else {
				output += " "+ i++ + " ";
			}
			for (ArrayList<Integer> cell : row) {
				for (Integer state : cell) {
					output += state;
				}
				if (cell.size() == 0) {
					output += ' ';
				}

				output += ' ';
			}
			output += "\n";
		}

		return output;
	}
	
//	public boolean accepted(String input) {
//		boolean result = false;
//		
//		for (int i = 0; i < input.length(); i++) {
//			result |= accepted(input.substring(i), initialState, 0);
//		}
//		
//		return result;
//	}
//
//	private boolean accepted(String input, int current, int i) {
//		boolean a = false;
//
//		for (int state : epsilonClosure(current)) {
//			if (finals.get(state)) {
//				return true;
//			}
//		}
//		
//		for (int state : epsilonClosure(current)) {
//			for (int child : getTransitions(state, input.charAt(i))) {
//				a |= accepted(input, child, i + 1);
//			}
//		}
//		
//		return a;
//	}
	
	public HashSet<String> accepted(String input) {
		HashSet<String> result = new HashSet<String>();
		
		for (int i = 0; i < input.length(); i++) {
			accepted(input.substring(i), initialState, 0, result);
		}
		
		return result;
	}
	
	private void accepted(String input, int current, int i, HashSet<String> result) {
		for (int state : epsilonClosure(current)) {
			if (finals.get(state)) {
				result.add(input.substring(0, i));
				return;
			}
		}
		
		for (int state : epsilonClosure(current)) {
			for (int child : getTransitions(state, input.charAt(i))) {
				accepted(input, child, i + 1, result);
			}
		}
	}
}
