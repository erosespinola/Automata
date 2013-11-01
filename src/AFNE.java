import java.util.ArrayList;
import java.util.Stack;


public class AFNE {
	private static final int NSYMBOLS = 60;
	
	private ArrayList<ArrayList<Integer>[]> G;
	private ArrayList<Boolean> finals;
	
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
		previous = current = addState();
		
		for (int i = 0; i < word.length(); i++) {
			current = addState();
			c = word.charAt(i);
			
			G.get(previous)[convert(c)].add(current);
			previous = current;
		}
		
		finals.set(current, true);
		
		System.out.println(this);
	}
	
	@Override
	public String toString() {
		String output = "  ";
		int i = 0;
		
		for (char current = 'A'; current <= 'z'; current++) {
			output += current + " ";
		}
		output += "~ .\n";
		
		for (ArrayList<Integer>[] row : G) {
			output += i++ + " ";
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
	
	public AFNE concatenate(AFNE operand) {
		return this;
	}
	
	public void union(AFNE operand) {
		
	}
	
	public void kleenClausure() {
		
	}
	
	public void positiveClausure() {
		
	}
	
	private void concatenateString(String s) {
		
	}
	
	private int addState() {
		ArrayList<Integer>[] row = new ArrayList[NSYMBOLS];
		finals.add(false);
		
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
					op1 = operands.pop();
					op2 = operands.pop();
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
					throw new Exception("aaa");
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
	}
	
	private int convert(char c) {
		if (c == '.') {
			return 51;
		}
		else if (c == '~') {
			return 50;
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
