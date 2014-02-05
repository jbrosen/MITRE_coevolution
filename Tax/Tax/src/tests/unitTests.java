package tests;

import org.junit.Test;

import evogpj.Parser.Parser;
import evogpj.algorithm.SymbRegMOO;
import evogpj.evaluation.TaxCodeFitness;
import evogpj.evaluation.TaxFitness;
import evogpj.genotype.ListGenotype;
import evogpj.gp.Individual;
import evogpj.gp.MersenneTwisterFast;
import evogpj.gp.Population;
import interpreter.PrintGraph;
import interpreter.entities.Entity;
import interpreter.misc.Graph;
import interpreter.misc.Transaction;
import interpreter.misc.Transfer;
import interpreter.taxCode.TaxCode;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.ArrayList;


/**
 * Functions/methods to test
 * 
 * Parser: getAnnuityThreshold
 * TaxFitness and TaxCodeFitness: eval(ind,pop), getFitnessOfIndividual
 * Graph: getFinalTransaction
 * SymbRegMOO/_CO: create_operators, step, run_population
 * 
 * 
 **/


public class unitTests {
	Graph graph = new Graph();
	TaxCode taxCode = new TaxCode();
	
	
	ArrayList<Entity> nodesList = graph.getNodes();
	Transfer t = new Transfer(nodesList, taxCode);
	PrintGraph g = new PrintGraph(nodesList);
	
//	@Test
	public void testSymbRegMOO() {
		
	}
	
	
	
//	@Test
	public void testGetFitnessOfIndividual_TF() {
		ArrayList<Integer> randList = new ArrayList<Integer>();
//		integers that generate first transaction in iBOB
		randList.add(1);
		randList.add(4);
		randList.add(3);
		randList.add(2);
		randList.add(0);
		randList.add(3);
		randList.add(0);
		
//		integers that generate an illegal transaction, should be a normal amount of tax
//		randList.add(1);
//		randList.add(0);
//		randList.add(0);
//		randList.add(0);
//		randList.add(0);
//		randList.add(0);
//		randList.add(0);
		
		Individual ind = new Individual(new ListGenotype(randList));
		TaxFitness tf = new TaxFitness(graph);
		double fit = tf.getFitnessOfIndividual(ind, taxCode);
		System.out.println(fit);
	}
	
//	@Test
	public void testEval_TF() {
//		make new population of tax codes
		Population pop = new Population();
		ArrayList<Integer> gen1 = new ArrayList<Integer>();
		gen1.add(0);
		Individual ind1 = new Individual(new ListGenotype(gen1));
		
		ArrayList<Integer> gen2 = new ArrayList<Integer>();
		gen2.add(1);
		Individual ind2 = new Individual(new ListGenotype(gen2));
		
		ArrayList<Integer> gen3 = new ArrayList<Integer>();
		gen3.add(0);
		Individual ind3 = new Individual(new ListGenotype(gen2));
		
		pop.add(ind1);
		pop.add(ind2);
		pop.add(ind3);
		
		ArrayList<Integer> randList = new ArrayList<Integer>();
//		integers that generate first transaction in iBOB
		randList.add(1);
		randList.add(4);
		randList.add(3);
		randList.add(2);
		randList.add(0);
		randList.add(3);
		randList.add(0);
//		integers that generate an illegal transaction
//		randList.add(1);
//		randList.add(0);
//		randList.add(0);
//		randList.add(0);
//		randList.add(0);
//		randList.add(0);
//		randList.add(0);
		Individual ind = new Individual(new ListGenotype(randList));
		
		TaxFitness tc = new TaxFitness(graph);
		tc.eval(ind,pop);
		System.out.println(ind.getFitness());
	}
	
//	@Test
	public void testGetFitnessOfIndividual_TCF() {
		ArrayList<String> transList = new ArrayList<String>();
		transList.add("Transaction(Jones,JonesCo,Annuity(200,30),PartnershipAsset(99,NewCo))");
//		create a tax code individual to test fitness against iBOB
		ArrayList<Integer> taxCodeGenotype = new ArrayList<Integer>();
		taxCodeGenotype.add(6);
		Individual ind = new Individual(new ListGenotype(taxCodeGenotype));
		
		TaxCodeFitness tcf = new TaxCodeFitness(graph);
		double fit = tcf.getFitnessOfIndividual(ind, transList);
		System.out.println("FITNESS: "+fit);
	}
	
//	@Test
	public void testEval_TCF() {
//		create a population of two transactions, one is iBOB and the other is illegal and test against a tax code
		ArrayList<Integer> trans1 = new ArrayList<Integer>();
		trans1.add(1);
		trans1.add(4);
		trans1.add(3);
		trans1.add(2);
		trans1.add(0);
		trans1.add(3);
		trans1.add(0);
		Individual ind1 = new Individual(new ListGenotype(trans1));
		
		ArrayList<Integer> trans2 = new ArrayList<Integer>();
		trans2.add(1);
		trans2.add(0);
		trans2.add(0);
		trans2.add(0);
		trans2.add(0);
		trans2.add(0);
		trans2.add(0);
		Individual ind2 = new Individual(new ListGenotype(trans2));
		
		Population pop = new Population();
		pop.add(ind1);
		pop.add(ind2);
		
//		create a tax code individual to test fitness against iBOB and illegal scheme
		ArrayList<Integer> taxCodeGenotype = new ArrayList<Integer>();
		taxCodeGenotype.add(3);
		Individual ind = new Individual(new ListGenotype(taxCodeGenotype));
		
		TaxCodeFitness tcf = new TaxCodeFitness(graph);
		
		tcf.eval(ind,pop);
		System.out.println("FITNESS: "+ind.getFitness());
		
	}
	
//	@Test
	public void testGetFinalTransaction() {
//		should print out a sale between whoever owns the hotel and Brown
		ArrayList<String> ret = new ArrayList<String>();
		ret.add("Transaction(NewCo,FamilyTrust,Material(200,Hotel,1),Annuity(200,30))");
		graph.createAction(ret);
		ArrayList<Transaction> trans = graph.getTransactions();
		if (t.doTransfer(trans.get(0)))
			g.printGraph(trans.get(0));
		
		
		Transaction finalTrans = graph.getFinalTransaction();
		System.out.println(finalTrans.toString());
	}
	
	/*
	 * Given a list of integers, sets the phenotype to be the choice of annuity threshold that is mod 7
	 * of the first element of randList
	 */
	@Test
	public void testGetClauses() {
		Parser p = new Parser();
//		ArrayList<Integer> randList = p.generateRandomList(10);
		ArrayList<Integer> randList = p.generateRandomList(100);
		ArrayList<String> clauses = new ArrayList<String>();
		try{
			clauses = p.getClauses(randList);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		for (int i=0 ; i<clauses.size(); ++i) {
			System.out.println(i+": "+clauses.get(i));
		}
		
		taxCode.createClauses(clauses);
		System.out.println(taxCode.getAnnuityThreshold());
		System.out.println(taxCode.getChildSalePrevention());
		System.out.println(taxCode.getAnnuityForMaterial());
		
//		try {
//			Class testClass = taxCode.getClass();
//			Class[] cArg1 = new Class[1];
//			cArg1[0] = double.class;
//			Method aMethod = testClass.getMethod("setAnnuityThreshold", cArg1);
//			System.out.println("method = "+aMethod.toString());
//		} catch (NoSuchMethodException e) {
//			e.printStackTrace();
//		}
		
		
	}
	
//	@Test
	public void testGetActions() {
		Parser p = new Parser();
//		ArrayList<Integer> randList = p.generateRandomList(10);
		ArrayList<Integer> randList = new ArrayList<Integer>();
//		integers that generate iBOB
		randList.add(1);
		randList.add(4);
		randList.add(3);
		randList.add(2);
		randList.add(0);
		randList.add(3);
		randList.add(0);
		
		Individual ind = new Individual(new ListGenotype(randList));
		
		try{
			ArrayList<String> st = p.getAction(randList);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		System.out.println(p.getPhenotype());
	}
	
	
	
	
}
