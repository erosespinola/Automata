import java.util.ArrayList;
import java.util.Stack;

public class Postfix {
	private ArrayList<String> postfix;
	
	public Postfix(String entry){
		this.postfix = postfix(tokenizer(entry));
	}
	
	public ArrayList<String> getPostfix(){
		return this.postfix;
	}
	
	public ArrayList<String> tokenizer(String entry){
		ArrayList<String> list= new ArrayList<String>();
		String token = "";
		for(int i=0; i<entry.length(); i++){
			char actualChar = entry.charAt(i);
			if(Character.isAlphabetic(actualChar) || Character.isDigit(actualChar) || actualChar == '\\'){
				token += String.valueOf(actualChar);
			}
			else {
				if(token.length()>0){
					if(token.length() > 2 && (actualChar == '*' || actualChar == '+')){
						String newToken = String.valueOf(token.charAt(token.length()-1));
						token = token.substring(0, token.length()-1);
						list.add(token);
						list.add("&");
						list.add(newToken);
						token = "";
					} 
					else {
						list.add(token);
						token = "";	
						
					}
					if(actualChar == '('){
						list.add("&");
					}
				}
				if(actualChar == '('){
					if(entry.charAt(i+1)=='.' && entry.charAt(i+2)==')'){
						list.add(".");
						i = i + 2;
						continue;
					}
				}
				
				list.add(String.valueOf(actualChar));
				
				if (actualChar == '(' && entry.charAt(i+1) == ',' ||
					actualChar == ',' && entry.charAt(i+1) == ',' ||
					actualChar == ',' && entry.charAt(i+1) == ')') {
					list.add(String.valueOf(AFNE.EPSILON));
				}
				
				if(actualChar == ')' || actualChar == '+' || actualChar == '*'){
					if(i<entry.length()-1 && entry.charAt(i+1)!=',' && entry.charAt(i+1)!='*' 
							&& entry.charAt(i+1)!='+' && entry.charAt(i+1)!=')'){
							list.add("&");
					}
				}
			}
		}
		
		if (token != "") {
			list.add(String.valueOf(token));
		}
		
		return list;
	}
	
	public ArrayList<String> postfix(ArrayList<String> list){
		ArrayList<String> output = new ArrayList<String>();
		Stack<String> operators = new Stack<String>();
		for(int i=0; i<list.size(); i++){
			String actualToken = list.get(i);
			if(!isOperator(actualToken)){
				output.add(actualToken);
			}
			else if(actualToken.equals("(")){
				operators.push(actualToken);
			}
			else if(actualToken.equals(")")){
				while (!operators.peek().equals("(")){
					output.add(operators.pop());
				}
				operators.pop();
			}
			else if( actualToken.equals("&")){
				while (!operators.isEmpty() && operators.peek().equals("&")){
					output.add(operators.pop());
				}
				operators.push(actualToken);
			}
			else if( actualToken.equals(",")){
				while (!operators.isEmpty() && (operators.peek().equals("&") || operators.peek().equals(","))){
					output.add(operators.pop());
				}
				operators.push(actualToken);
			}
		}
		while(!operators.isEmpty()){
			output.add(operators.pop());
		}
		
		return output;
	}
	
	private boolean isOperator(String operator){
		return operator.equals("(") 
			|| operator.equals(",")
			|| operator.equals("&")
			|| operator.equals(")");
	}
	
	@Override
	public String toString() {
		String s = "";
		
		for (String token : postfix) {
			s += "[" + token + "]";
		}
		
		return s;
	}
	
	public static void main(String[] args) throws Exception{
		Postfix p = new Postfix("5*Zxc5*,6*zXc6*");
		System.out.println(p);
		AFNE a = new AFNE(p);
		System.out.println(a);
		System.out.println(a.accepted("666zXc666k"));
//		System.out.println(a.convertToAFD());
//		System.out.println(a.convertToAFD().accepted("xD lol"));
	}
}
