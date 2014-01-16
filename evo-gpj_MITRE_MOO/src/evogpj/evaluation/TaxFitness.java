package evogpj.evaluation;

import interpreter.PrintGraph;
import interpreter.entities.Entity;
import interpreter.entities.TaxPayer;
import interpreter.misc.Actions;
import interpreter.misc.Graph;
import interpreter.misc.Transaction;
import interpreter.misc.Transfer;
import interpreter.misc.writeFile;
import interpreter.taxCode.TaxCode;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import evogpj.phenotype.ListPhenotype;

import calculator.Calculator;
import evogpj.Parser.Parser;

import evogpj.gp.Individual;
import evogpj.gp.Population;

public class TaxFitness extends FitnessFunction {

	private double startTax;
	private double finalTax;
	private Graph graph;
	private boolean verbose = false;
	
	public TaxFitness(Graph graph) {
		this.startTax = 0;
		this.finalTax = 0;
	}

	@Override
	public void evalPop(Population pop) {
		for (Individual individual : pop) {
			this.eval(individual);
		}
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
		Calculator c = new Calculator(nodesList);
		PrintGraph g = new PrintGraph(nodesList);
		
		/*for(int j=0;j<nodesList.size();j++){
			if(nodesList.get(j).getType().equals("TaxPayer")){
				this.startTax = nodesList.get(j).getStartTax();
				System.out.println("Start Tax: " + this.startTax);
				break;
			}
		}*/
		//this.finalTax = Double.MIN_VALUE;
		for(int i=0;i<transactionList.size();i++){

			if(t.doTransfer((Transaction) transactionList.get(i)) && this.verbose){
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
		
		ind.setFitness("TaxFitness",-this.finalTax);
		
		if (this.verbose)
			System.out.println("FITNESS: " + ind.getFitness());
	}
	public Boolean isMaximizingFunction() {
		return true;
	}
}