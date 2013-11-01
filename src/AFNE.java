import java.util.ArrayList;
import java.util.Stack;


public class AFNE {
	private static final int NSYMBOLS = 60;

	private ArrayList<ArrayList<Integer>[]> G;
	private ArrayList<Boolean> finals;

	private int initialState;

	public AFNE() {
		finals = new ArrayList<Boolean>();
		G = new ArrayList<ArrayList<Integer>[]>();
	}

	public AFNE(Postfix expression) throws Exception {
		this();

		evaluatePostfix(expression);
	}

	public AFNE(String word) {
		this();

		int current, previous;
		char c;
		initialState = 0;
		previous = current = addState();
		finals.add(false);

		for (int i = 0; i < word.length(); i++) {
			current = addState();
			finals.add(false);			
			c = word.charAt(i);

			G.get(previous)[convert(c)].add(current);
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
				G.get(i)[convert('~')].add(operand.initialState);
				finals.set(i, false);
			}
		}
		finals.addAll(operand.finals);
		return this;
	}

	public AFNE union(AFNE operand) {		
		finals.addAll(operand.finals);

		mergeTables(operand);

		int newInitialState = addState();
		finals.add(false);

		G.get(newInitialState)[convert('~')].add(initialState);
		G.get(newInitialState)[convert('~')].add(operand.initialState);
		initialState = newInitialState;

		int newFinalState = addState();
		finals.add(true);

		for (int i = 0; i < finals.size() - 1; i++) {
			if (finals.get(i)) {
				G.get(i)[convert('~')].add(newFinalState);
				finals.set(i, false);
			}
		}
		return this;
	}

	public AFNE kleenClausure() {
		int newInitialState = addState();
		int newFinalState = addState();

		finals.add(false);
		finals.add(true);

		G.get(newInitialState)[convert('~')].add(newFinalState);
		G.get(newInitialState)[convert('~')].add(initialState);


		for (int i = 0; i < finals.size() - 1; i++) {
			if (finals.get(i)) {
				G.get(i)[convert('~')].add(initialState);
				G.get(i)[convert('~')].add(newFinalState);
				finals.set(i, false);
			}
		}

		initialState = newInitialState;
		return this;
	}

	public AFNE positiveClausure() {
		int newInitialState = addState();
		int newFinalState = addState();

		finals.add(false);
		finals.add(true);

		G.get(newInitialState)[convert('~')].add(initialState);


		for (int i = 0; i < finals.size() - 1; i++) {
			if (finals.get(i)) {
				G.get(i)[convert('~')].add(initialState);
				G.get(i)[convert('~')].add(newFinalState);
				finals.set(i, false);
			}
		}

		initialState = newInitialState;
		return this;
	}

	private void concatenateString(String s) {

	}

	private int addState() {
		ArrayList<Integer>[] row = new ArrayList[NSYMBOLS];

		for (int i = 0; i < NSYMBOLS; i++) {
			row[i] = new ArrayList<Integer>();
		}
		G.add(row);

		return G.size() - 1;
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
				case "+":
					op1 = operands.pop();
					op1.positiveClausure();
					break;
				case "*":
					op1 = operands.pop();
					op1.kleenClausure();
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

		for (int i = 0; i < operands.size() - 1; i++) {
			operands.push(operands.pop().concatenate(operands.pop()));
		}

		this.G = operands.peek().G;
		this.finals = operands.peek().finals;
		this.initialState = operands.peek().initialState;
	}

	private int convert(char c) {
		if (c == '.') {
			return NSYMBOLS - 1;
		}
		else if (c == '~') {
			return NSYMBOLS - 2;
		}
		else {
			return c - 'A';
		}
	}

	private char revert(int c) {
		if (c == 51) {
			return '.';
		}
		else if (c == 52) {
			return '~';
		}
		else {
			return (char)(c + 'A');
		}
	}

	private boolean isOperator(String operator){
		return operator.equals("+") 
				|| operator.equals("*")
				|| operator.equals(",");
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

	@Override
	public String toString() {
		String output = "   ";
		int i = 0;

		for (char current = 'A'; current <= 'z'; current++) {
			output += current + " ";
		}
		output += "~ .\n";

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

	//	public boolean accepted(String input, int current, int i) {
	//		boolean a = false;
	//
	//		if (i == input.length()) {
	//			return finals[current];
	//		} else {
	//			for (int child : G[current][symbols.get(input.charAt(i) + "")]) {
	//				a |= accepted(input, child, i + 1);
	//			}
	//			return a;
	//		}
	//	}
}
