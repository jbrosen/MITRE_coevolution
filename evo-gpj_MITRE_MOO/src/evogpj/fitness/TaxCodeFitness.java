package evogpj.fitness;

import interpreter.PrintGraph;
import interpreter.assets.Annuity;
import interpreter.assets.Cash;
import interpreter.assets.Material;
import interpreter.assets.PartnershipAsset;
import interpreter.entities.Entity;
import interpreter.entities.TaxPayer;
import interpreter.misc.Actions;
import interpreter.misc.Graph;
import interpreter.misc.Transaction;
import interpreter.misc.Transfer;
import interpreter.misc.writeFile;
import interpreter.taxCode.TaxCode;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import evogpj.phenotype.ListPhenotype;
import evogpj.Parser.Parser;
import calculator.Calculator;
import evogpj.genotype.ListGenotype;
import evogpj.gp.Individual;

public class TaxCodeFitness extends FitnessFunction {
	private double startTax;
	private double finalTax;
	private Graph graph;
	
	public TaxCodeFitness(Graph graph,writeFile wf) {
		this.startTax = 0;
		this.finalTax = 0;
	}
	
	
	@Override
	public void eval(Individual ind) {
		System.out.println("INSIDE TAX CODE FITNESS\n");
		ArrayList<Transaction> transactions = getiBob();
		double annuityThreshold=0;
		Parser p = new Parser();
		try {
			annuityThreshold = p.getAnnuityThreshold(ind.getGenotype().getGenotype());
			ind.setPhenotype(new ListPhenotype(p.getPhenotype()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		System.out.println("Annuity Threshold: "+annuityThreshold+"\n");
		
		TaxCode tc = new TaxCode();
		tc.setAnnuityThreshold(annuityThreshold);
		
		Graph graph = new Graph();
		ArrayList<Entity> nodesList = graph.getNodes();
//		graph.createAction(transactions);
		graph.setTransactions(transactions);
		ArrayList<Transaction> transactionList = graph.getTransactions();
		Transfer t = new Transfer(nodesList, tc);
		Calculator c = new Calculator(nodesList);
		PrintGraph g = new PrintGraph(nodesList);
		
		for(int i=0;i<transactionList.size();i++){

			if(t.doTransfer((Transaction) transactionList.get(i))){
				g.printGraph((Transaction) transactionList.get(i));
			}
			
			if (i==transactionList.size()-1){
				for(int j=0;j<nodesList.size();j++){
					if(nodesList.get(j).getType().equals("TaxPayer")){
						if(((TaxPayer) nodesList.get(j)).getCanBeTaxed()){
							this.finalTax = nodesList.get(j).getTotalTax();
						}
						break;
					}
				}
			}
		}
		
		ind.setFitness(finalTax);

		System.out.println("FITNESS: " + ind.getFitness());
		
	}

	
	public ArrayList<Transaction> getiBob() {
//		JonesCo gives p1 to FamilyTrust in exchange for a1
		PartnershipAsset p1 = new PartnershipAsset(99,"NewCo");
		Annuity a1 = new Annuity(200,30);

		Actions a11 = new Actions("JonesCo","FamilyTrust",p1);
		Actions a12 = new Actions("FamilyTrust", "JonesCo",a1);
		Transaction t1 = new Transaction(a11,a12);
		
//		NewCo sells m1 to Brown for c1
		Material m1 = new Material(200,"Hotel",1);
		Cash c1 = new Cash(200);
		
		Actions a21 = new Actions("NewCo","Brown",m1);
		Actions a22 = new Actions("Brown","NewCo",c1);
		Transaction t2 = new Transaction(a21,a22);
		ArrayList<Transaction> ret = new ArrayList<Transaction>();
		ret.add(t1);
		ret.add(t2);
		return ret;
		
	}
	
	
	
	
}









