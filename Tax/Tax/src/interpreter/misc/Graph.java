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
public class Graph {
	public ArrayList<Transaction> transactionList;
	public static ArrayList<Entity> nodesList; 
	private boolean verbose = false;
	
	
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
		Jones.setTotalTax(Double.MIN_VALUE);
		
		this.nodesList.add(Jones);
		this.nodesList.add(NewCo);
		this.nodesList.add(JonesCo);
		this.nodesList.add(FamilyTrust);
		this.nodesList.add(Brown);
		
	/*
		
				//Entities
				TaxPayer A = new TaxPayer("A");
				TaxPayer B = new TaxPayer("B");
				TaxPayer C = new TaxPayer("C");
				TaxPayer D = new TaxPayer("D");
				TaxPayer E = new TaxPayer("E");


				Partnership P1 = new Partnership("P1");
				Partnership P2 = new Partnership("P2");
				Partnership P3 = new Partnership("P3");

		

				
				//Assets 
				PartnershipAsset PA1 = new PartnershipAsset(30,"P1");
				PA1.getOwners().put("A", 30.0);
				
				PartnershipAsset PA2 = new PartnershipAsset(20,"P1");
				PA2.getOwners().put("B", 20.0);

				PartnershipAsset PA3 = new PartnershipAsset(50,"P1");
				PA3.getOwners().put("D", 50.0);
				
				PartnershipAsset PA4 = new PartnershipAsset(50,"P2");
				PA4.getOwners().put("P1", 50.0);
				

				
				Material HouseB = new Material(300,"HouseB",1);
				HouseB.setInsideBasis(100);
				HouseB.getOwners().put("B", 200.0);
				
				Material HouseAB = new Material(400,"HouseAB",1);
				HouseAB.setInsideBasis(300);
				HouseAB.getOwners().put("A", 250.0);
				HouseAB.getOwners().put("B", 50.0);

				
				Material HouseP2 = new Material(300,"HouseP2",1);
				HouseP2.setInsideBasis(100);
				HouseP2.getOwners().put("P1", 200.0);
				
				
				Cash cashA = new Cash(300);
				cashA.setInsideBasis(300);
				cashA.getOwners().put("A", 300.0);
				
				Cash cashC = new Cash(600);
				cashC.setInsideBasis(600);
				cashC.getOwners().put("C", 600.0);
				
				
				
				Material HouseE = new Material(500,"HouseE",1);
				HouseE.setInsideBasis(100);
				HouseE.getOwners().put("E", 200.0);
				
				Material HouseA = new Material(300,"HouseA",1);
				HouseA.setInsideBasis(100);
				HouseA.getOwners().put("A", 200.0);
				
				Cash cashB = new Cash(300);
				cashB.setInsideBasis(300);
				cashB.getOwners().put("B", 300.0);
				
				Cash cashD = new Cash(500);
				cashD.setInsideBasis(500);
				cashD.getOwners().put("D", 500.0);
				
				
				
				
				//Add Assets
				A.getPortfolio().add(PA1);
				A.getPortfolio().add(HouseA);
				B.getPortfolio().add(PA2);
				C.getPortfolio().add(cashC);
				D.getPortfolio().add(PA3);
				P1.getPortfolio().add(HouseAB);
				P1.getPortfolio().add(HouseB);
				P1.getPortfolio().add(cashA);
				P1.getPortfolio().add(PA4);
				P2.getPortfolio().add(HouseP2);
				P1.getPortfolio().add(cashB);
				E.getPortfolio().add(HouseE);
				D.getPortfolio().add(cashD);



				
				
				this.transactionList = new ArrayList<Transaction>();
				this.nodesList = new ArrayList<Entity>();
				
				//add children
				A.getPartnershipIn().add(P1);
				B.getPartnershipIn().add(P1);
				P1.getPartnershipIn().add(P2);
				D.getPartnershipIn().add(P1);

				
				
			
				//add parents
				P1.getPartners().add(A);
				P1.getPartners().add(B);
				P1.getPartners().add(D);
				P2.getPartners().add(P1);


				
				
				//add parentData
				PartnerData pd1 = new PartnerData(30,"A");
				P1.getPartnerData().add(pd1);
				
				PartnerData pd2 = new PartnerData(20,"B");
				P1.getPartnerData().add(pd2);
				
				PartnerData pd3 = new PartnerData(50,"D");
				P1.getPartnerData().add(pd3);
				
				PartnerData pd4 = new PartnerData(50,"P1");
				P2.getPartnerData().add(pd4);
				
				
				this.nodesList.add(A);
				this.nodesList.add(B);
				this.nodesList.add(C);
				this.nodesList.add(D);
				this.nodesList.add(P1);
				this.nodesList.add(P2);
				this.nodesList.add(E);
				this.nodesList.add(P3);
				
		*/
		
		
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




