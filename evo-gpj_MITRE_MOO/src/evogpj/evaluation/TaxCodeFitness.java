package evogpj.evaluation;

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
import interpreter.taxCode.TaxCode;

import java.io.IOException;
import java.util.ArrayList;

import evogpj.phenotype.ListPhenotype;
import evogpj.Parser.Parser;
import evogpj.algorithm.Parameters;
import evogpj.gp.Individual;
import evogpj.gp.Population;

public class TaxCodeFitness extends FitnessFunction {
	private double finalTax;
	private boolean verbose = Parameters.Defaults.VERBOSE;
	
	public TaxCodeFitness(Graph graph) {
		this.finalTax = 0;
	}
	
	/*
	 * METHODS FOR UNIDIRECTIONAL GA
	 */
	
//	evaluates and sets the fitness of each individual in pop
	@Override
	public void evalPop(Population pop) {
		for (Individual individual : pop) {
			this.eval(individual);
		}
	}
	
//	evaluates the fitness of a tax code individual against an iBOB scheme
	public void eval(Individual ind) {
		if (this.verbose)
			System.out.println("INSIDE TAX CODE FITNESS\n");
		ArrayList<Transaction> transactions = getiBob();
		ArrayList<String> clauses = new ArrayList<String>();
//		use a Parser instance to convert the genotype (list of integers) into a phenotype
//		(annuityThreshold)
		Parser p = new Parser();
		
		try {
			clauses = p.getClauses(ind.getGenotype().getGenotype());
			ind.setPhenotype(new ListPhenotype(p.getPhenotype()));
		} catch (IOException e) {
			e.printStackTrace();
		}

//		make a new TaxCode object from the found annuityThreshold
		TaxCode tc = new TaxCode(clauses);
		
		Graph graph = new Graph();
		ArrayList<Entity> nodesList = graph.getNodes();
		graph.setTransactions(transactions);
		ArrayList<Transaction> transactionList = graph.getTransactions();
		
		Transfer t = new Transfer(nodesList, tc);
		PrintGraph g = new PrintGraph(nodesList);

//		execute each transaction
		for(int i=0;i<transactionList.size();i++){
			if(t.doTransfer((Transaction) transactionList.get(i))){
				g.printGraph((Transaction) transactionList.get(i));
			}
//			when all of the transactions in the list of been completed
			if (i==transactionList.size()-1){
//				find who owns the hotel and have them sell it to Brown
//				see Graph.getFinalTransaction for details
				Transaction finalTransaction = graph.getFinalTransaction();
				if (finalTransaction != null) {
					if (t.doTransfer(finalTransaction)) {
						g.printGraph(finalTransaction);
					}
				}
				
//				get the final tax of the primary taxpayer
				for(int j=0;j<nodesList.size();j++){
					if(nodesList.get(j).getType().equals("TaxPayer")){
						if(((TaxPayer) nodesList.get(j)).getCanBeTaxed()){
							this.finalTax = nodesList.get(j).getTotalTax()+tc.getAnnuityThreshold();
						}
						break;
					}
				}
			}
		}
		
		ind.setFitness("TaxCodeFitness",finalTax);
		if (this.verbose)
			System.out.println("FITNESS: " + ind.getFitness());
	}

	/*
	 * CO-EVOLUTIONARY GA METHODS
	 */
	
//	evaluates and sets the fitness of individual ind as the sum of its fitnesses
//	when compared against all of population pop
	public void eval(Individual ind, Population pop ) {
		double totFitness = 0.0;
		ArrayList<Double> fitList = new ArrayList<Double>();
		
//		use the Parser to convert the Genotype of each member of pop into a string
//		representation of a set of transactions
		for (Individual i : pop) {
			Parser p = new Parser();
			try {
				ArrayList<String> transactions = p.getAction(i.getGenotype().getGenotype());
				double fit = getFitnessOfIndividual(ind, transactions);
				fitList.add(fit);
				totFitness += fit;
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
		ind.setFitness(totFitness);
	}
	
	
//	evaluate the fitness of an individual GIVEN A CERTAIN SET OF TRANSACTIONS and returns it
	public double getFitnessOfIndividual(Individual ind, ArrayList<String> transactions ) {
		
		ArrayList<String> clauses = new ArrayList<String>();
//		use a Parser instance to convert the genotype (list of integers) into a phenotype
		Parser p = new Parser();
		
		try {
			clauses = p.getClauses(ind.getGenotype().getGenotype());
			ind.setPhenotype(new ListPhenotype(p.getPhenotype()));
			
		} catch (IOException e) {
			e.printStackTrace();
		}
		
//		make a new TaxCode object from the annuityThreshold
		TaxCode tc = new TaxCode(clauses);
		
//		set up the Graph and Transfer class, and convert string representation of
//		transaction list into an ArrayList of Transaction objects
		Graph graph = new Graph();
		ArrayList<Entity> nodesList = graph.getNodes();
		graph.createAction(transactions);
		ArrayList<Transaction> transactionList = graph.getTransactions();
		
		
		Transfer t = new Transfer(nodesList, tc);
		PrintGraph g = new PrintGraph(nodesList);
		
//		execute each transaction
		for(int i=0;i<transactionList.size();i++){

			if(t.doTransfer((Transaction) transactionList.get(i))){
				g.printGraph((Transaction) transactionList.get(i));
			}
			
//			when all of the transactions in the list of been completed
			if (i==transactionList.size()-1){
//				find who owns the hotel and have them sell it to Brown
//				see Graph.getFinalTransaction for details
				Transaction finalTransaction = graph.getFinalTransaction();
				if (finalTransaction != null) {
					if (t.doTransfer(finalTransaction)) {
						g.printGraph(finalTransaction);
					}
				}
//				get the final tax of the primary taxpayer
				for(int j=0;j<nodesList.size();j++){
					if(nodesList.get(j).getType().equals("TaxPayer")){
						if(((TaxPayer) nodesList.get(j)).getCanBeTaxed()){
							this.finalTax = nodesList.get(j).getTotalTax()+tc.getAnnuityThreshold();
						}
						break;
					}
				}
			}
		}
//		ind.setFitness("TaxCodeFitness",finalTax);
		return finalTax;
	}
	
//	test using an annuity threshold as input rather than an Individual
	public double getFitnessOfIndividual(double annuityThreshold, ArrayList<String> transactions ) {

		if (Parameters.Defaults.VERBOSE)
			System.out.println("Annuity Threshold: "+annuityThreshold+"\n");
		
		TaxCode tc = new TaxCode();
		tc.setAnnuityThreshold(annuityThreshold);
		
		Graph graph = new Graph();
		ArrayList<Entity> nodesList = graph.getNodes();
		graph.createAction(transactions);
		ArrayList<Transaction> transactionList = graph.getTransactions();
		
		
		Transfer t = new Transfer(nodesList, tc);
		PrintGraph g = new PrintGraph(nodesList);
//
		for(int i=0;i<transactionList.size();i++){

			if(t.doTransfer((Transaction) transactionList.get(i))){
				g.printGraph((Transaction) transactionList.get(i));
			}
			
			if (i==transactionList.size()-1){
				Transaction finalTransaction = graph.getFinalTransaction();
				if (finalTransaction != null) {
					if (t.doTransfer(finalTransaction)) {
						g.printGraph(finalTransaction);
					}
				}
				
				for(int j=0;j<nodesList.size();j++){
					if(nodesList.get(j).getType().equals("TaxPayer")){
						if(((TaxPayer) nodesList.get(j)).getCanBeTaxed()){
							this.finalTax = nodesList.get(j).getTotalTax()+annuityThreshold;
						}
						break;
					}
				}
			}
		}
		
		return finalTax;
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
	
	public Boolean isMaximizingFunction() {
		return true;
	}
	
	
}









