package evogpj.Parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Random;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/***
 * Read a Grammar file to generate 
 * string of actions.
 * @author Badar
 *
 */
public class Parser {

	private HashMap<Symbol,ArrayList<Production>> map = new HashMap<Symbol,ArrayList<Production>>();
	private ArrayList<String> actions = new ArrayList<String>();
	private ArrayList<String> transactions = new ArrayList<String>();
	private double annuityThreshold = 0;
	private ArrayList<String> clauses = new ArrayList<String>();

    private Symbol startToken = null;
    private Stack<Symbol> stack = new Stack<Symbol>();
    
    private String filename = "TaxGrammar3";
    private String codeFilename = "TaxCodeGrammar";
    private String phenotype = "";
	
//    INPUT DOESN'T SEEM TO MATTER HERE, SHOULDNT BE LIKE THIS
	public ArrayList<String> getAction(ArrayList<Integer> genotypeList) throws IOException{
		createMap(false);
		
		generateActions(genotypeList);
		return this.transactions;
	}
	
	
	
	/***
	 * 
	 * Reads in a grammar file and creates a map
	 * of non-terminals and productions.
	 * @throws IOException
	 */
	public void createMap(boolean taxCode) throws IOException{
		
		String line;
		
		BufferedReader br = null;
		
//		InputStream is = getClass().getResourceAsStream(AlgorithmBase.grammarFile);
		InputStream is = getClass().getResourceAsStream(filename);
		if (taxCode) {
			is = getClass().getResourceAsStream(codeFilename);
		}
		
//		InputStream is = getClass().getResourceAsStream(filename);
		
		InputStreamReader isr = new InputStreamReader(is); 
		br = new BufferedReader(isr);

		
		int i = 0;
		while ((line = br.readLine()) != null) {
//			System.out.println(line);
			Lexer lex = new Lexer(line);
			ArrayList<Production> p = lex.getProductions();
			Symbol t = lex.getLeftToken();
			if(i == 0){
				this.startToken = t;
			}
//			creates a map of tokens (left side) to productions (right side) which essentially makes the grammar
			if(!(this.map.containsKey(t))){
				this.map.put(t, p);
			}
			i+=1;
		}
		br.close();	
		is.close();
		isr.close();
	}
	
	/***
	 * Runs Depth-First search on a graph of nodes 
	 * which are stored in the hashmap. Each non-terminal
	 * has children that are mapped as productions.
	 * Production is a list of Non-terminals and terminals.
	 * 
	 * @param randList list genotype passed in.
	 * @return Array of actions.
	 */
//	MIGHT NEED TO CHANGE WHETHER IT READS FROM THE RIGHT OR THE LEFT
	public void generateActions(ArrayList<Integer> randList){
		String s ="";
		int count = 0;
		int time_out=0;
		this.stack.push(this.startToken);
		while(!stack.isEmpty() && time_out<10000){
			Symbol token = (Symbol) stack.pop();
				if(token.getType().equals(Type.TERMINAL)){
					s+=token.getValue();
					//System.out.println("concatenate terminal: " + s);
				}
				else{
					ArrayList<Production> pList = this.map.get(token);
//					if there is more than one production, then use the integer mod rule to figure out which one to use
//					if there is only one production, just use that
					if(pList.size()>1){
						if(count >=0 && count<randList.size()){
							int index = randList.get(count) % (pList.size());
							ArrayList<Symbol> tokens = pList.get(index).getTokens();
							loadStack(tokens);
							count+=1;
							if(count>=randList.size()){
								count=0;
							}
						}
						
					}else{
						ArrayList<Symbol> tokens = pList.get(0).getTokens();
						loadStack(tokens);
					}
				}
				time_out+=1;
			}
		//System.out.println("Phenotype created: " + s);
		this.phenotype = s;
		createActions(s);
	}
	
	public void createActions(String s){
		String tokenRegex = "(Transaction\\([a-zA-Z]+,[a-zA-Z]+,[a-zA-Z]+\\(\\d+(,([a-zA-Z]+|\\d+))*\\),[a-zA-Z]+\\(\\d+(,([a-zA-Z]+|\\d+))*\\)\\))";
		Pattern tokenPattern = Pattern.compile(tokenRegex);
		Matcher m = tokenPattern.matcher(s);
		//ArrayList<String> transactions = new ArrayList<String>();
		while(m.find()) {
			//System.out.println("now tokens: " + m.group());
			this.transactions.add(m.group());
			//createActionsFromTransactions(transactions);
			//this.actions.add(m.group());
		}
	}
	
	

	public String getPhenotype(){
		return phenotype;
	}
	
	/***
	 * push new child nodes on stack
	 * after each iteration.
	 * @param tokens to push on the stack
	 * 
	 */
	public void loadStack(ArrayList<Symbol> tokens){
		int size = tokens.size();
		for(int j=size-1;j>=0;j--){
			this.stack.push(tokens.get(j));
		}
	}
	/***
	 * 
	 * @param s String of actions
	 * separate each action and 
	 * store them in a list.
	 */
	/*public void createActions(String s){
		String tokenRegex = "(Action\\([a-zA-Z]+,[a-zA-Z]+,[a-zA-Z]+\\(\\d+(,([a-zA-Z]+|\\d+))*\\)\\))";
		Pattern tokenPattern = Pattern.compile(tokenRegex);
		Matcher m = tokenPattern.matcher(s);
		while(m.find()) {
			System.out.println("now tokens: " + m.group());
			this.actions.add(m.group());
		}
	}*/
	
	
	public void createActionsFromTransactions(ArrayList<String> transactions){
		for(String transaction:transactions){		
		String s  = transaction.substring(12,transaction.length()-1);
		String tokenRegex = "(([a-zA-Z]+\\(\\d+(,([a-zA-Z]+|\\d+))*\\))|[a-zA-Z]+)";
		Pattern tokenPattern = Pattern.compile(tokenRegex);
		Matcher m = tokenPattern.matcher(s);
		ArrayList<String> token = new ArrayList<String>();
		while(m.find()) {
			//System.out.println("TOKEN FOR TRANSACTION: " + m.group());
			token.add(m.group());
		}
		String action1 = "Action(" + token.get(0) + "," + token.get(1)+ "," + token.get(2) + ")";
		String action2 = "Action(" + token.get(1) + ","+token.get(0) + ","+token.get(3) + ")";
		//System.out.println("ACTION1 CREATED FOR TRANSACTION: " + action1);
		//System.out.println("ACTION2 CREATED FOR TRANSACTION: " + action2);
		
		this.actions.add(action1);
		this.actions.add(action2);
		}
	}
    public String printMap() {
        String ret = "";
        Iterator it = map.entrySet().iterator();
        while (it.hasNext()) {
                Map.Entry pairs = (Map.Entry)it.next();
                String s = "";
                Symbol symb = (Symbol)pairs.getKey();
                s += symb.getValue();
                s += " ==> ";
                ArrayList<Production> plist = (ArrayList<Production>)pairs.getValue();
                for (int i=0;i<plist.size();++i) {
//                        s += "\n";
                        ArrayList<Symbol> symbList = plist.get(i).getTokens();
                        for (int j=0;j<symbList.size();++j) {
                                if (j>0) {
                                        s += " | ";
                                }
                                s += symbList.get(j).getValue();
                        }
                }
                ret += s;
                ret += "\n";
        }
        return ret;
}
	/*
	 * Annuity threshold stuffs
	 */
	
	public ArrayList<String> getClauses(ArrayList<Integer> genotypeList) throws IOException{
		createMap(true);
//		System.out.println(printMap());
		
//		generateActions(genotypeList);
		generateClauses(genotypeList);
		return this.clauses;
	}
	
	public void generateClauses(ArrayList<Integer> randList){
		String s ="";
		int count = 0;
		int time_out=0;
		this.stack.push(this.startToken);
		while(!stack.isEmpty() && time_out<10000){
			Symbol token = (Symbol) stack.pop();
				if(token.getType().equals(Type.TERMINAL)){
					s+=token.getValue();
					//System.out.println("concatenate terminal: " + s);
				}
//				MAKE THIS INTO A FUNCTION
				else if (token.getValue().equals("<auditScores>")) {
					ArrayList<Production> pList = this.map.get(token);
					int numAuditScores = Integer.parseInt(pList.get(0).getTokens().get(0).getValue());
					String str = "AuditScores(";
					for (int i = 0 ; i < numAuditScores ; ++i) {
						if (count >= 0 && count < randList.size()) {
							str += randList.get(count).toString();
							count += 1;
						}
						else {
							count = 0;
							str += randList.get(count).toString();
							count += 1;
						}
						if (i < (numAuditScores - 1)) {
							str += ",";
						}
					}
					s += str+")";
				}
				
				else{
//					System.out.println(token.getValue());
					ArrayList<Production> pList = this.map.get(token);
//					if there is more than one production, then use the integer mod rule to figure out which one to use
//					if there is only one production, just use that
					if(pList.size()>1){
						if(count >=0 && count<randList.size()){
							int index = randList.get(count) % (pList.size());
							ArrayList<Symbol> tokens = pList.get(index).getTokens();
							loadStack(tokens);
							count+=1;
							if(count>=randList.size()){
								count=0;
							}
						}
						
					}
					else{
						ArrayList<Symbol> tokens = pList.get(0).getTokens();
						loadStack(tokens);
					}
				}
				time_out+=1;
			}
//		System.out.println(s);
		this.phenotype = s;
//		System.out.println("Phenotype created: " + s);
		createClauses(s);
		//return this.actions;
		
		//return this.transactions;
	}
	public void createClauses(String s) {
		
		
		ArrayList<String> tmpClauses = new ArrayList<String>();
//		String tokenRegex = "(([a-zA-Z]+\\(\\d+(,([a-zA-Z]+|\\d+))*\\))|[a-zA-Z]+)";

//		String tokenRegex = "(AnnuityThreshold\\([a-zA-Z]+,[a-zA-Z]+,[a-zA-Z]+\\(\\d+(,([a-zA-Z]+|\\d+))*\\),[a-zA-Z]+\\(\\d+(,([a-zA-Z]+|\\d+))*\\)\\))";
		String tokenRegex = "([a-zA-Z0-9]+\\([0-9a-z,]+\\))";
		Pattern tokenPattern = Pattern.compile(tokenRegex);
		Matcher m = tokenPattern.matcher(s);
		//ArrayList<String> transactions = new ArrayList<String>();
		while(m.find()) {
			//System.out.println("now tokens: " + m.group());
			tmpClauses.add(m.group());
//			System.out.println("HI: "+m.group());
		}

		this.clauses = tmpClauses;
	}
	
	
	/***
	 * read in a grammar file
	 * @return file buffer reader.
	 * 
	 */
	public BufferedReader readFile(String filename) throws IOException{
		BufferedReader br = null;
		InputStream is = getClass().getResourceAsStream("filename");
		//System.out.println("CLASS IS: "+ getClass().getResourceAsStream(filename));
		InputStreamReader isr = new InputStreamReader(is); 
		br = new BufferedReader(isr); 
		return br;	
	}
	
	/***
	 * Random numbers are used to pick productions.
	 * @param maxCount
	 * @return list of random number.
	 */
	public ArrayList<Integer> generateRandomList(int maxCount){
		ArrayList<Integer> num = new ArrayList<Integer>();
		Random r = new Random();
		for(int i=0;i<maxCount;i++){
			int n = r.nextInt(maxCount);			
			num.add(n);
		}
		return num;
	}
	
}
