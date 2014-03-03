package interpreter.misc;

import interpreter.assets.Annuity;
import interpreter.assets.Assets;
import interpreter.assets.Cash;
import interpreter.assets.Material;
import interpreter.assets.PartnershipAsset;
import interpreter.assets.Share;
import interpreter.entities.Entity;
import interpreter.entities.Partnership;
import interpreter.entities.TaxPayer;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import evogpj.algorithm.Parameters;
public class Graph {
	public ArrayList<Transaction> transactionList;
	public static ArrayList<Entity> nodesList; 
	private boolean verbose = Parameters.Defaults.VERBOSE;
	
	
	
	/*
	 * Test
	 */
	private double annThresh = 0;
	public double getAnnThresh() {
		return this.annThresh;
	}
	public void setAnnThresh(double a) {
		this.annThresh=a;
	}
	
	public Graph(){
	
		//Entities
		TaxPayer Jones = new TaxPayer("Jones");
		Partnership NewCo = new Partnership("NewCo");
		Partnership JonesCo = new Partnership("JonesCo");
		Partnership FamilyTrust = new Partnership("FamilyTrust");
		TaxPayer Brown = new TaxPayer("Brown");
		
		
		//Assets
//		JonesCo owns a 99% Partnership of NewCo
		PartnershipAsset P1 = new PartnershipAsset(99,"NewCo");
		P1.getOwners().put("JonesCo", 200.0);
//		Jones owns a 99% Partnership of JonesCo
		PartnershipAsset P2 = new PartnershipAsset(99,"JonesCo");
		P2.getOwners().put("Jones", 198.0);
//		Jones also owns a 99% Partnership in FamilyTrust
		PartnershipAsset P3 = new PartnershipAsset(99,"FamilyTrust");
		P3.getOwners().put("Jones", 198.0);
	
		

		Material Hotel = new Material(200,"Hotel",1);
		Hotel.setInsideBasis(120);
		Hotel.getOwners().put("JonesCo", 120.0);
		
		
		Cash cash3 = new Cash(200);
		cash3.setInsideBasis(200);
		cash3.getOwners().put("Brown", 200.0);
		
		//Add Assets
		Jones.getPortfolio().add(P2);
		JonesCo.getPortfolio().add(P1);
		Jones.getPortfolio().add(P3);
		NewCo.getPortfolio().add(Hotel);
		Brown.getPortfolio().add(cash3);
		
		this.transactionList = new ArrayList<Transaction>();
		this.nodesList = new ArrayList<Entity>();
		
		//add children
		Jones.getPartnershipIn().add(JonesCo);
		Jones.getPartnershipIn().add(FamilyTrust);
		JonesCo.getPartnershipIn().add(NewCo);
		
	
		//add parents
		JonesCo.getPartners().add(Jones);
		FamilyTrust.getPartners().add(Jones);
		NewCo.getPartners().add(JonesCo);
		
		//add parentData
		PartnerData pd1 = new PartnerData(99,"Jones");
		JonesCo.getPartnerData().add(pd1);
		
		PartnerData pd3 = new PartnerData(99,"Jones");
		FamilyTrust.getPartnerData().add(pd3);
		
		PartnerData pd4 = new PartnerData(99,"JonesCo");
		NewCo.getPartnerData().add(pd4);
		
		//set taxable taxpayer
		Jones.setCanBeTaxed(true);
//		Set beginning tax to be $80 
		Jones.setTotalTax(Double.MIN_VALUE);
//		Jones.setTotalTax(1.0);
//		Jones.setTotalTax(80);
		
		this.nodesList.add(Jones);
		this.nodesList.add(NewCo);
		this.nodesList.add(JonesCo);
		this.nodesList.add(FamilyTrust);
		this.nodesList.add(Brown);
		
	}
	
	/*
	 * Finds whoever owns the hotel and generates a transaction object that sells
	 * the hotel to Brown
	 */
	public Transaction getFinalTransaction() {
		Transaction finalTransaction = null;
		
		ArrayList<Entity> nodesList = getNodes();
		Entity seller = null;
		outerloop:
		for (Entity e : nodesList) {
			for (Assets a : e.getPortfolio()) {
				if (a.toString() == "Material") {
					if (((Material)a).getName().equals("Hotel")) {
						seller = e;
						break outerloop;
					}
				}
			}
		}
		
		if (seller != null) {
			Material m1 = new Material(200,"Hotel",1);
			Cash c1 = new Cash(200);
			
			Actions a21 = new Actions(seller.getName(),"Brown",m1);
			Actions a22 = new Actions("Brown",seller.getName(),c1);
			finalTransaction = new Transaction(a22,a21);
		}
		
		/*
		 * Test to see what happens if we require that the hotel be sold by NewCo in the end
		 */
//		Material m2 = new Material(200,"Hotel",1);
//		Cash c2 = new Cash(200);
//		Actions a1 = new Actions("NewCo","Brown",m2);
//		Actions a2 = new Actions("Brown","NewCo",c2);
//		finalTransaction = new Transaction(a1,a2);
		
		return finalTransaction;
	}
	
	
	public void setTransactions(ArrayList<Transaction> trans) {
		this.transactionList = (ArrayList<Transaction>)trans.clone();
	}
	public ArrayList<Transaction> getTransactions(){
		return (ArrayList<Transaction>) this.transactionList.clone();
	}
	public ArrayList<Entity> getNodes(){
		return (ArrayList<Entity>) this.nodesList;
	}
	
	public int getNumPartnerships() {
		int ret = 0;
		for (Entity e : getNodes()) {
			if (e.getType() == "Partnership")
				ret += 1;
		}
		return ret;
	}
	
	/*
	 * takes in a String of actions generated by the parser
	 * and converts them into Transaction objects.
	 * returns a list of Transactions
	 */
	public void createAction(ArrayList<String> transactions){
		//System.out.println("INSIDE INTERPRETER GRAPH: PERFORMING ACTIONS BETWEEN ENTITIES TO CALCULATE TAX");
		
		for(int i=0;i<transactions.size();i++){
			String t1 = transactions.get(i);
			//System.out.println("transactions: "+ t1);
			ArrayList<String> actions = createActionsFromTransactions(t1);
			Transaction transaction = createTransactionObject(actions);
			if (this.verbose)
				System.out.println("TRANSACTION OBJECT: "+ transaction.toString());
			transactionList.add(transaction);
		}
	}
    /*create a transaction object from a pair of actions
     */
	private Transaction createTransactionObject(ArrayList<String> actions){
		ArrayList<Actions> actionList = new ArrayList<Actions>();

		for(String actionString : actions){
			String s  = actionString.substring(7,actionString.length()-1);
			//System.out.println("INSIDE GRAPH CREATING TOKENS:" + s);

			String tokenRegex = "(([a-zA-Z]+\\(\\d+(,([a-zA-Z]+|\\d+))*\\))|[a-zA-Z]+)";
			Pattern tokenPattern = Pattern.compile(tokenRegex);
			Matcher m = tokenPattern.matcher(s);
			ArrayList<String> token = new ArrayList<String>();
			while(m.find()) {
				//System.out.println("TOKEN: " + m.group());
				token.add(m.group());
			}
		
			String entity1 = token.get(0);
			String entity2 = token.get(1);
			String Asset = token.get(2);
		
			Assets asset = createTransferAsset(Asset);
			Actions action = new Actions(entity1,entity2,asset);
			actionList.add(action);
		}
		Transaction t = new Transaction(actionList.get(0),actionList.get(1));
		//System.out.println("return:" + t.toString());
		
		return t;
	}

	
	public Assets createTransferAsset(String Asset){
		int index = Asset.indexOf("(");
		//System.out.println("Index:" + index);
		String subRight = Asset.substring(index+1, Asset.length()-1).toString();
		String subLeft = Asset.substring(0,index).toString();
		//System.out.println("sub seq: " + subRight);
		//System.out.println("sub seq: " + subLeft);
		String[] str = subRight.split(",");
		//System.out.println("sub seq comma: " + str.length);

		
		if(subLeft.equals("Material")){
			int value = Integer.parseInt(str[0]);
			String name = str[1];
			int quantity = Integer.parseInt(str[2]);
			return new Material(value,name,quantity);
			
		}
		else if(subLeft.equals("Annuity")){
			int value = Integer.parseInt(str[0]);
			int year = Integer.parseInt(str[1]);
			return new Annuity(value,year);
			
		}
		else if(subLeft.equals("Cash")){
			int value = Integer.parseInt(str[0]);
			return new Cash(value);
		}
		else if(subLeft.equals("PartnershipAsset")){
			String name = str[1];
			double share = Integer.parseInt(str[0]);
			return new PartnershipAsset(share,name);
		}
		else if(subLeft.equals("Share")){
			double share = Integer.parseInt(str[0]);
			return new Share(share);
		}
		else{
			return null;
		}
	}
	
	/*creates a String of two actions from a String Transaction*/
	public ArrayList<String> createActionsFromTransactions(String transaction){
		
		ArrayList<String> actions = new ArrayList<String>();
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
		actions.add(action1);
		actions.add(action2);
		return actions;
	}
	
	
	
	
}




