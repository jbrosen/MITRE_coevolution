package evogpj.evaluation;

import interpreter.PrintGraph;
import interpreter.entities.Entity;
import interpreter.entities.TaxPayer;
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

public class TaxFitness extends FitnessFunction {

	private double finalTax;
	private boolean verbose = Parameters.Defaults.VERBOSE;
	
	public TaxFitness(Graph graph) {
		this.finalTax = 0;
	}

	@Override
	public void evalPop(Population pop) {
		for (Individual individual : pop) {
			this.eval(individual);
		}
	}
	
	public void eval(Individual ind, Population pop) {
		double totFitness = 0.0;
		Parser p = new Parser();
		for (Individual i : pop) {
			try{
				double annThresh = p.getAnnuityThreshold(i.getGenotype().getGenotype());
				TaxCode tc = new TaxCode();
				tc.setAnnuityThreshold(annThresh);
				totFitness += getFitnessOfIndividual(ind, tc);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		ind.setFitness(totFitness);
	}
	
	/*
	 * Given an individual and a tax code, returns the fitness of the individual when compared to that tax code
	 */
	public double getFitnessOfIndividual(Individual ind, TaxCode tc ) {
		ArrayList<String> transactions = new ArrayList<String>();
		Parser p = new Parser();
		try {
			transactions = p.getAction(ind.getGenotype().getGenotype());
			ind.setPhenotype(new ListPhenotype(p.getPhenotype()));
//			System.out.println(p.getPhenotype());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Graph graph = new Graph();
		ArrayList<Entity> nodesList = graph.getNodes();
		graph.createAction(transactions);
		ArrayList<Transaction> transactionList = graph.getTransactions();
		
		Transfer t = new Transfer(nodesList, tc);
		PrintGraph g = new PrintGraph(nodesList);
		
		
		for(int i=0;i<transactionList.size();i++){
//			HOW TO ADDRESS ILLEGAL TRANSACTIONS
			if(t.doTransfer((Transaction) transactionList.get(i)) && this.verbose){
				g.printGraph((Transaction) transactionList.get(i));
			}
			
			if (i==transactionList.size()-1){
				boolean finalTrans = false;
				Transaction finalTransaction = graph.getFinalTransaction();
				if (finalTransaction != null) {
					if (t.doTransfer(finalTransaction)) {
						g.printGraph(finalTransaction);
						finalTrans = true;
					}
				}
				
				
				for(int j=0;j<nodesList.size();j++){
					if(nodesList.get(j).getType().equals("TaxPayer")){
						if(((TaxPayer) nodesList.get(j)).getCanBeTaxed()){
							this.finalTax = nodesList.get(j).getTotalTax();
						}
						break;
					}
				}
				if (!finalTrans)
					this.finalTax = Double.MAX_VALUE;
			}
		}
		return -finalTax;
	}
	
//	test using ArrayList of transaction strings and anniuty threshold
	public double getFitnessOfIndividual(ArrayList<String> transactions, double annuityThreshold) {

		
		Graph graph = new Graph();
		ArrayList<Entity> nodesList = graph.getNodes();
		graph.createAction(transactions);
		ArrayList<Transaction> transactionList = graph.getTransactions();
		TaxCode tc = new TaxCode();
		tc.setAnnuityThreshold(annuityThreshold);
		
		
		Transfer t = new Transfer(nodesList, tc);
		PrintGraph g = new PrintGraph(nodesList);
		
		
		for(int i=0;i<transactionList.size();i++){
//			HOW TO ADDRESS ILLEGAL TRANSACTIONS
			if(t.doTransfer((Transaction) transactionList.get(i)) && this.verbose){
				g.printGraph((Transaction) transactionList.get(i));
			}
			
			if (i==transactionList.size()-1){
				boolean finalTrans = false;
				Transaction finalTransaction = graph.getFinalTransaction();
				if (finalTransaction != null) {
					if (t.doTransfer(finalTransaction)) {
						g.printGraph(finalTransaction);
						finalTrans = true;
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
				if (!finalTrans)
					this.finalTax = Double.MAX_VALUE;
			}
		}
		return -finalTax;
	}
	
	public void eval(Individual ind) {
		if (this.verbose)
			System.out.println("INSIDE TAX FITNESS\n");
		ArrayList<String> transactions = new ArrayList<String>();
		Parser p = new Parser();
		try {
			
			transactions = p.getAction(ind.getGenotype().getGenotype());
			ind.setPhenotype(new ListPhenotype(p.getPhenotype()));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		Graph graph = new Graph();
		ArrayList<Entity> nodesList = graph.getNodes();
		graph.createAction(transactions);
		
		ArrayList<Transaction> transactionList = graph.getTransactions();
		
		Transfer t = new Transfer(nodesList, new TaxCode());
		
		PrintGraph g = new PrintGraph(nodesList);
		
		for(int i=0;i<transactionList.size();i++){
			if(t.doTransfer((Transaction) transactionList.get(i)) && this.verbose){
				g.printGraph((Transaction) transactionList.get(i));
			}

			if (i==transactionList.size()-1){
				/*
				 * Test the final transaction idea
				 */
				boolean finalTrans = false;
				Transaction finalTransaction = graph.getFinalTransaction();
				if (finalTransaction != null) {
					if (t.doTransfer(finalTransaction)) {
						g.printGraph(finalTransaction);
						finalTrans = true;
					}
				}
				
				for(int j=0;j<nodesList.size();j++){
					if(nodesList.get(j).getType().equals("TaxPayer")){
						if(((TaxPayer) nodesList.get(j)).getCanBeTaxed()){
							this.finalTax = nodesList.get(j).getTotalTax();
						}
						break;
					}
				}
				if (!finalTrans)
					this.finalTax = Double.MAX_VALUE;
			}
		}
		
		ind.setFitness("TaxFitness",-this.finalTax);
		
		if (this.verbose)
			System.out.println("FITNESS: " + ind.getFitness());
	}
	
	
	
	
	
	public Boolean isMaximizingFunction() {
		return true;
	}
}