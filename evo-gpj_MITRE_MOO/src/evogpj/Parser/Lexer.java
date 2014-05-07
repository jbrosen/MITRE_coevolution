package evogpj.Parser;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
/***
 * 
 * @author Badar
 *
 */
public class Lexer {

	/***
	 * Grammar file to read:
	 * <actions>::= <action><action><action>
	 * <action>::= Action(<entity>,<entity>,<Asset>)
	 * <Entity>::= X|Y
	 * <Asset>::= Cash|PC
	 * 
	 * Grammar to read grammar file.
	 * Non-Terminal::= [Production]+
	 * Production::= [Token]+
	 * 
	 * 
	 */
	private ArrayList<Production> productionList = new ArrayList<Production>();
	private String line;
	public Lexer(String line){
		this.line = line;
	}
	/***
	 * Production: list of terminals and non-terminals.
	 * 
	 * @return list of Productions
	 */
//	Split right side of production string by | and return
	public ArrayList<Production> getProductions(){
		String right = getRightExpression();
		String[] s = right.split("\\|");
		for(int i=0;i<s.length;i++){
			Production p = new Production(createTokens(s[i]));
			productionList.add(p);
		}
		return productionList;
	}
	/***
	 * 
	 * @param s line from a file that needs
	 * to be borken into a set of terminals.
	 * @return list of tokens
	 */
	
	public ArrayList<Symbol> createTokens(String s){
		String tokenRegex = "([a-zA-Z]*\\()|(<[a-zA-Z]+>)|(\\,)|([a-zA-Z]+)|(\\))|\\d*";
		Pattern tokenPattern = Pattern.compile(tokenRegex);
		ArrayList<Symbol> tokens = new ArrayList<Symbol>();
		Matcher m = tokenPattern.matcher(s);
		while(m.find()) {
			//System.out.println("token :" + m.group());
			if(m.group().startsWith("<")){
				Symbol t = new Symbol(Type.NON_TERMINAL,m.group());
				tokens.add(t);
			}else{
				Symbol t = new Symbol(Type.TERMINAL,m.group());
				tokens.add(t);
			}	
		}
		return tokens;
	}
	
	public String getRightExpression(){
		int index = this.line.indexOf("=");
		String sub = this.line.substring(index+1);
		return sub;
	}
	public Symbol getLeftToken(){
		int index = this.line.indexOf("=");
		String sub = this.line.substring(0, index-2);
		Symbol t = new Symbol(Type.NON_TERMINAL,sub);
		return t;
	}

}
