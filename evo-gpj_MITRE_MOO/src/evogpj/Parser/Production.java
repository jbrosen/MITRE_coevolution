package evogpj.Parser;

import java.util.ArrayList;

/***
 * list of tokens.
 * @author Badar
 *
 */
public class Production{

	private ArrayList<Symbol> tokens;
	public Production(ArrayList<Symbol> tokens){
		this.tokens = tokens;
	}
	
	public ArrayList<Symbol> getTokens(){
		return tokens;
	}


	
}
