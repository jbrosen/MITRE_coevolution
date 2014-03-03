package evogpj.evaluation;

import interpreter.PrintGraph;
import interpreter.entities.Entity;
import interpreter.entities.TaxPayer;
import interpreter.misc.Graph;
import interpreter.misc.Transaction;
import interpreter.misc.Transfer;
import interpreter.misc.Transfer_NEO;
import interpreter.taxCode.TaxCode;

import java.io.IOException;
import java.util.ArrayList;

import evogpj.phenotype.ListPhenotype;

import evogpj.Parser.Parser;

import evogpj.algorithm.Parameters;
import evogpj.gp.Individual;
import evogpj.gp.Population;

public class TaxFitness extends FitnessFunction {

	private double finalTax;
	private boolean verbose = Parameters.Defaults.VERBOSE;
	
	public TaxFitness(Graph graph) {
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
	
//	evaluates and sets the fitness of individual ind
	public void eval(Individual ind) {
		if (this.verbose)
			System.out.println("INSIDE TAX FITNESS\n");
		ArrayList<String> transactions = new ArrayList<String>();
//		use a Parser instance to convert the genotype (list of integers) into a phenotype
//		(string representation of a transaction list)
		Parser p = new Parser();
		try {
			
			transactions = p.getAction(ind.getGenotype().getGenotype());
			ind.setPhenotype(new ListPhenotype(p.getPhenotype()));
		} catch (IOException e) {
			e.printStackTrace();
		}
//		set up the Graph and Transfer class, and convert string representation of
//		transaction list into an ArrayList of Transaction objects
		Graph graph = new Graph();
		ArrayList<Entity> nodesList = graph.getNodes();
		graph.createAction(transactions);
		ArrayList<Transaction> transactionList = graph.getTransactions();
		
		
		TaxCode tc = new TaxCode();
		tc.setMaterialForAnnuityAudit(0.4);
		tc.setSingleLinkAudit(0.4);
		tc.setDoubleLinkAudit(0.2);
		
		Transfer_NEO tn = new Transfer_NEO(graph,tc);

		double auditScore = 0.0;
		ArrayList<Transaction> legalTransactions = new ArrayList<Transaction>();
		
		PrintGraph g = new PrintGraph(nodesList);
		
//		execute each transaction
		for(int i=0;i<transactionList.size();i++){
			if(tn.doTransfer((Transaction) transactionList.get(i))){
				auditScore += ((Transaction)transactionList.get(i)).getAuditScore();
				legalTransactions.add((Transaction)transactionList.get(i));
				if (this.verbose)
					g.printGraph((Transaction) transactionList.get(i));
			}


			
//			when all of the transactions in the list of been completed
			if (i==transactionList.size()-1){
//				find who owns the hotel and have them sell it to Brown
//				see Graph.getFinalTransaction for details
				Transaction finalTransaction = graph.getFinalTransaction();
				if (finalTransaction != null) {
					if (tn.doTransfer(finalTransaction)) {
						auditScore += finalTransaction.getAuditScore();
						legalTransactions.add((Transaction)transactionList.get(i));
						g.printGraph(finalTransaction);
					}
					

				}
//				get the final tax of the primary taxpayer
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
		
//		ind.setFitness("TaxFitness",-this.finalTax);
		
		ind.setFitness("TaxFitness",-this.finalTax - ((80-this.finalTax)*auditScore));
		ind.setFitness("auditScore",auditScore);
		
		if (this.verbose)
			System.out.println("FITNESS: " + ind.getFitness());
	}
	
	
	public double getOverallAuditScore(ArrayList<Transaction> transactions, Graph graph) {
		double retScore = 0.0;
		
		
		
		return retScore;
	}
	
	
	/*
	 * CO-EVOLUTIONARY GA METHODS
	 */
	
//	evaulates and sets the fitness of ind against each member of the Population pop
	public void eval(Individual ind, Population pop) {
		double totFitness = 0.0;
		int numLegal = 0;
//		use the Parser to convert the Genotype of each member of pop into a Tax Code object
		for (Individual i : pop) {
			Parser p = new Parser();
			
			try{
				ArrayList<String> clauses = p.getClauses(i.getGenotype().getGenotype());
				TaxCode tc = new TaxCode(clauses);
				
				double fit = getFitnessOfIndividual(ind, tc);
//				make sure the interaction did not result in all impossible/infeasible transactions
				if (fit < Double.MAX_VALUE) {
					totFitness += fit;
					numLegal += 1;
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
//		the final fitness is the mean of all transactions with at least one legal transaction
		if (numLegal > 0) {
			ind.setFitness(totFitness/numLegal);
		}
		else {
			ind.setFitness(-100.0);
		}
	}
	
//	Given an individual and a tax code, returns the fitness of the individual when compared to that tax code
	public double getFitnessOfIndividual(Individual ind, TaxCode tc ) {
		ArrayList<String> transactions = new ArrayList<String>();
//		use a Parser instance to convert the genotype (list of integers) into a phenotype
		Parser p = new Parser();
		try {
			transactions = p.getAction(ind.getGenotype().getGenotype());
			ind.setPhenotype(new ListPhenotype(p.getPhenotype()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
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
//			HOW TO ADDRESS ILLEGAL TRANSACTIONS
			if(t.doTransfer((Transaction) transactionList.get(i)) && this.verbose){
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
							this.finalTax = nodesList.get(j).getTotalTax();
						}
						break;
					}
				}
			}
		}
		return -finalTax;
	}
	
//	test using ArrayList of transaction strings and anniuty threshold
	public double getFitnessOfIndividual(ArrayList<String> transactions, double annuityThreshold) {
		
//		set up the Graph and Transfer class, and convert string representation of
//		transaction list into an ArrayList of Transaction objects
		Graph graph = new Graph();
		ArrayList<Entity> nodesList = graph.getNodes();
		graph.createAction(transactions);
		ArrayList<Transaction> transactionList = graph.getTransactions();
		
		TaxCode tc = new TaxCode();
		tc.setAnnuityThreshold(annuityThreshold);
		
		Transfer t = new Transfer(nodesList, tc);
		PrintGraph g = new PrintGraph(nodesList);
		
//		execute each transaction
		for(int i=0;i<transactionList.size();i++){
//			HOW TO ADDRESS ILLEGAL TRANSACTIONS
			if(t.doTransfer((Transaction) transactionList.get(i)) && this.verbose){
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
							this.finalTax = nodesList.get(j).getTotalTax();
						}
						break;
					}
				}
			}
		}
		return -finalTax;
	}
	
	
	public Boolean isMaximizingFunction() {
		return true;
	}
}