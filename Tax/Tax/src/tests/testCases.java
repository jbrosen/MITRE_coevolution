package tests;

import static org.junit.Assert.*;
import fitness.TaxCodeFitness;
import genotype.ListGenotype;
import gp.Individual;
import interpreter.PrintGraph;
import interpreter.assets.Annuity;
import interpreter.assets.Cash;
import interpreter.assets.Material;
import interpreter.assets.PartnershipAsset;
import interpreter.assets.Share;
import interpreter.entities.Entity;
import interpreter.misc.Actions;
import interpreter.misc.Graph;
import interpreter.misc.Transaction;
import interpreter.misc.Transfer;
import interpreter.misc.writeFile;
import interpreter.taxCode.TaxCode;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.junit.Test;

import calculator.Calculator;
//import java.lang.System;


public class testCases {
	Graph graph = new Graph();
	TaxCode taxCode = new TaxCode();
	
	
	ArrayList<Entity> nodesList = graph.getNodes();
	Transfer t = new Transfer(nodesList, taxCode);
	Calculator c = new Calculator(nodesList);
	PrintGraph g = new PrintGraph(nodesList);
	
	
	
	
	
	//@Test
	public void test1() {
		
		
		
		Cash c1 = new Cash(300);
		Material HouseB = new Material(300,"HouseB",1);
		Actions a1 = new Actions("P1","C",HouseB);
		Actions a2 = new Actions("C","P1",c1);

		Transaction t1 = new Transaction(a1,a2);
		if(t.doTransfer(t1)){
			g.printGraph(t1);
		}
		
	}
	
	//@Test
	public void test2() {
		
		
		Cash c1 = new Cash(400);
		Material HouseAB = new Material(400,"HouseAB",1);
		Actions a1 = new Actions("P1","C",HouseAB);
		Actions a2 = new Actions("C","P1",c1);

		Transaction t1 = new Transaction(a1,a2);
		if(t.doTransfer(t1)){
			g.printGraph(t1);
		}
		
	}
	
	//@Test
	public void test3() {
		Cash c1 = new Cash(600);
		PartnershipAsset pa = new PartnershipAsset(30,"P1");
		Actions a1 = new Actions("A","C",pa);
		Actions a2 = new Actions("C","A",c1);

		Transaction t1 = new Transaction(a1,a2);
		if(t.doTransfer(t1)){
			g.printGraph(t1);
		}
		
	}
	
	//@Test
	public void test4() {
		Cash c1 = new Cash(300);
		Material HouseP2 = new Material(300,"HouseP2",1);
		Actions a1 = new Actions("P2","C",HouseP2);
		Actions a2 = new Actions("C","P2",c1);

		Transaction t1 = new Transaction(a1,a2);
		if(t.doTransfer(t1)){
			g.printGraph(t1);
		}
		
	}
	
	//@Test
	public void test5() {
		Cash c1 = new Cash(300);
		Material HouseE = new Material(300,"HouseE",1);
		Actions a1 = new Actions("P1","E",c1);
		Actions a2 = new Actions("E","P1",HouseE);

		Transaction t1 = new Transaction(a1,a2);
		if(t.doTransfer(t1)){
			g.printGraph(t1);
		}
		
	}
	
	//check when house>cash.
	//@Test
	public void test6() {
		Cash c1 = new Cash(500);
		Material HouseE = new Material(500,"HouseE",1);
		Actions a1 = new Actions("P1","E",c1);
		Actions a2 = new Actions("E","P1",HouseE);

		Transaction t1 = new Transaction(a1,a2);
		if(t.doTransfer(t1)){
			g.printGraph(t1);
		}
		
	}
	
	//test shares.
	//taxpayer C gives Cash
	//Partnership P1 gives a share
	//@Test
	public void test7() {
		Cash c1 = new Cash(500);
		Share s = new Share(30);
		Actions a1 = new Actions("C","P1",c1);
		Actions a2 = new Actions("P1","C",s);

		Transaction t1 = new Transaction(a1,a2);
		if(t.doTransfer(t1)){
			g.printGraph(t1);
		}
		
	}
	
	//+++ doesn't work because cash values not added up
	//test shares.
	//partnership to another partnership
	//Partnership P2 gives a share
	//@Test
	public void test8() {
		Cash c1 = new Cash(500);
		Share s = new Share(30);
		Actions a1 = new Actions("P1","P3",c1);
		Actions a2 = new Actions("P3","P1",s);
		
		Transaction t1 = new Transaction(a1,a2);
		if(t.doTransfer(t1)){
			g.printGraph(t1);
		}
		
	}
	
	//test shares.
	//taxpayer C gives share
	//Partnership P1 gives cash
	//this should be illegal
	//@Test
	public void test9() {
		Cash c1 = new Cash(300);
		Share s = new Share(30);
		Actions a1 = new Actions("P1","C",c1);
		Actions a2 = new Actions("C","P1",s);

		Transaction t1 = new Transaction(a1,a2);
		if(t.doTransfer(t1)){
			g.printGraph(t1);
		}
		
	}
	
	//P1 and P2 exchange share and house.
	//@Test
	public void test10() {
		Material HouseB = new Material(300,"HouseB",1);
		Share s = new Share(30);
		Actions a1 = new Actions("P1","P2",HouseB);
		Actions a2 = new Actions("P2","P1",s);

		Transaction t1 = new Transaction(a1,a2);
		if(t.doTransfer(t1)){
			g.printGraph(t1);
		}
		
	}
	
	// taxpayer E and P1 exchange house and share
	//@Test
	public void test11() {
		Material HouseE = new Material(500,"HouseE",1);
		Share s = new Share(30);
		Actions a1 = new Actions("E","P1",HouseE);
		Actions a2 = new Actions("P1","E",s);

		Transaction t1 = new Transaction(a1,a2);
		if(t.doTransfer(t1)){
			g.printGraph(t1);
		}
		
	}
	
	//tax payer A and E exchange house and share
	//should be illegal
	//@Test
	public void test12() {
		Material HouseE = new Material(500,"HouseE",1);
		Share s = new Share(30);
		Actions a1 = new Actions("E","A",HouseE);
		Actions a2 = new Actions("A","E",s);

		Transaction t1 = new Transaction(a1,a2);
		if(t.doTransfer(t1)){
			g.printGraph(t1);
		}
		
	}
	
	// exchange PAsset and Share
	//illegal
	//@Test
	public void test13() {
		PartnershipAsset pa = new PartnershipAsset(30.0,"P1");
		Share s = new Share(30);
		Actions a1 = new Actions("A","P1",pa);
		Actions a2 = new Actions("P1","A",s);

		Transaction t1 = new Transaction(a1,a2);
		if(t.doTransfer(t1)){
			g.printGraph(t1);
		}
		
	}
	
	// A contributes a house.P1 gives a share.
	// A already has a PASSET.It shouldn't get a new PAsset.
	//this is illegal
	
	//@Test
	public void test14() {
		Material HouseA = new Material(300,"HouseA",1);
		Share s = new Share(30);
		Actions a1 = new Actions("A","P1",HouseA);
		Actions a2 = new Actions("P1","A",s);
		
		Transaction t1 = new Transaction(a1,a2);
		if(t.doTransfer(t1)){
			g.printGraph(t1);
		}
		
	}
	//checking annuity.
	//transfer annuity and house between two tax payers
//	@Test
	public void test15() {
		Material HouseA = new Material(300,"HouseA",1);
		Annuity a  = new Annuity(600,30);
		Actions a1 = new Actions("A","C",HouseA);
		Actions a2 = new Actions("C","A",a);
		
		
		Transaction t1 = new Transaction(a1,a2);
		if(t.doTransfer(t1)){
			g.printGraph(t1);
		}
		
	}
	
	//checking annuity.
	//transfer annuity and house between P1 and E
//	@Test
	public void test16() {
		Material HouseE = new Material(500,"HouseE",1);
		Annuity a  = new Annuity(500,30);
		Actions a1 = new Actions("E","P1",HouseE);
		Actions a2 = new Actions("P1","E",a);
		
		
		Transaction t1 = new Transaction(a1,a2);
		if(t.doTransfer(t1)){
			g.printGraph(t1);
		}
		
	}
	
	//checking annuity.
	//transfer annuity and Passet between A and E
//	@Test
	public void test17() {
		PartnershipAsset pa = new PartnershipAsset(30.0,"P1");
		Annuity a  = new Annuity(700,30);
		Actions a1 = new Actions("A","E",pa);
		Actions a2 = new Actions("E","A",a);
		
		
		Transaction t1 = new Transaction(a1,a2);
		if(t.doTransfer(t1)){
			g.printGraph(t1);
		}
		
	}
	
	//checking annuity.
	//transfer annuity and share between D and E
	//should be illegal
//	@Test
	public void test18() {
		Annuity a  = new Annuity(500,30);
		Share s = new Share(30);

		Actions a1 = new Actions("C","P1",a);
		Actions a2 = new Actions("P1","C",s);
		
		
		Transaction t1 = new Transaction(a1,a2);
		if(t.doTransfer(t1)){
			g.printGraph(t1);
		}
		
	}
//	@Test
	public void allTax() {
		TaxCode tc = new TaxCode();
		tc.setAnnuityThreshold(0);
		t.setTaxCode(tc);
		
//		NewCo sells m1 to Brown for c1
		Material m1 = new Material(200,"Hotel",1);
		Cash c1 = new Cash(200);
		
		Actions a21 = new Actions("NewCo","Brown",m1);
		Actions a22 = new Actions("Brown","NewCo",c1);
		Transaction t2 = new Transaction(a21,a22);
		if (t.doTransfer(t2)) {
			g.printGraph(t2);
		}
	}
	
	@Test
	public void fitnessTest() {
		writeFile wf = new writeFile("C:\\Users\\Jacob\\Documents\\MIT\\SCOTE\\code\\Tax\\Tax\\src\\interpreter\\output.txt");
		ArrayList<Integer> alist = new ArrayList<Integer>();
		ListGenotype lg = new ListGenotype(alist);
		Individual ind1 = new Individual(lg);
		
		TaxCodeFitness tcf = new TaxCodeFitness(graph,wf);
		tcf.eval(ind1);

	}
	
//	@Test
	public void ibob() {
		TaxCode tc = new TaxCode();
		tc.setAnnuityThreshold(0);
		t.setTaxCode(tc);
		
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
		
		if (t.doTransfer(t1)) {
			g.printGraph(t1);
		}
		if (t.doTransfer(t2)) {
			g.printGraph(t2);
		}
		
	}
	
	
	//test case with IBOB graph
//	@Test
	public void test19() {
		
		TaxCode tc = new TaxCode();
		tc.setAnnuityThreshold(0);
		t.setTaxCode(tc);
		
		Annuity a  = new Annuity(200,30);
		PartnershipAsset pa = new PartnershipAsset(30,"NewCo");

		Material hotel = new Material(200,"Hotel",1);
		Cash c = new Cash(200);
		
		
		Actions a1 = new Actions("FamilyTrust","JonesCo",a);
		Actions a2 = new Actions("JonesCo","FamilyTrust",pa);
		Actions a3 = new Actions("NewCo","Brown",hotel);
		Actions a4 = new Actions("Brown","NewCo",c);

		
		
		
		Transaction t1 = new Transaction(a1,a2);
		Transaction t2 = new Transaction(a3,a4);

		if(t.doTransfer(t1)){
			g.printGraph(t1);
		}
		if(t.doTransfer(t2)){
			g.printGraph(t2);
		}
		
//		String guiFile="C:\\Users\\Jacob\\Documents\\NichWorkspace\\NichWork\\ALFANetworkGUI.py";
//		String outputFile="C:\\Users\\Jacob\\Documents\\MIT\\SCOTE\\output1.txt";
//		
//		String[] command = new String[3];
//		command[0]="python";
//		command[1]=guiFile;
//		command[2]=outputFile;
//
//		
//		try{
//			Process process = Runtime.getRuntime().exec(command);
//			process.waitFor();
//			process.destroy();
//		} catch (IOException e) {
//			System.out.println("IOException");
//		} catch (InterruptedException e) {
//			System.out.println("IOException");
//		}
//		
//		System.out.println(command.toString());
		

		
		
//		IOException, InterruptedException
		
	}
	
	
	
	
	//NOT IBOB--long output for jacob
//	@Test
	public void test20() {
		
		Cash c1 = new Cash(300);
		Material HouseB = new Material(300,"HouseB",1);
		Actions a1 = new Actions("P1","C",HouseB);
		Actions a2 = new Actions("C","P1",c1);
		
		Cash c2 = new Cash(600);
		PartnershipAsset pa = new PartnershipAsset(30,"P1");
		Actions a3 = new Actions("A","C",pa);
		Actions a4 = new Actions("C","A",c1);
		
		Cash c3 = new Cash(300);
		Material HouseP2 = new Material(300,"HouseP2",1);
		Actions a5 = new Actions("P2","C",HouseP2);
		Actions a6 = new Actions("C","P2",c3);

		Cash c4 = new Cash(300);
		Material HouseE = new Material(300,"HouseE",1);
		Actions a7 = new Actions("P1","E",c4);
		Actions a8 = new Actions("E","P1",HouseE);
		
		Cash c5 = new Cash(500);
		Share s = new Share(30);
		Actions a9 = new Actions("C","P1",c5);
		Actions a10 = new Actions("P1","C",s);
		
		Cash c6 = new Cash(500);
		Share s1 = new Share(30);
		Actions a11 = new Actions("P1","P3",c6);
		Actions a12 = new Actions("P3","P1",s1);
		
		
		//Material HouseB = new Material(300,"HouseB",1);
		Share s4 = new Share(30);
		Actions a13 = new Actions("P1","P2",HouseB);
		Actions a14= new Actions("P2","P1",s4);
		
		Material HouseA = new Material(300,"HouseA",1);
		Share s5 = new Share(30);
		Actions a15 = new Actions("A","P1",HouseA);
		Actions a16 = new Actions("P1","A",s5);
		
		
		Transaction t1 = new Transaction(a1,a2);
		Transaction t2 = new Transaction(a3,a4);
		Transaction t3 = new Transaction(a5,a6);
		Transaction t4 = new Transaction(a7,a8);
		Transaction t5 = new Transaction(a9,a10);
		Transaction t6 = new Transaction(a11,a12);
		Transaction t7 = new Transaction(a13,a14);
		Transaction t8 = new Transaction(a15,a16);

		
		if(t.doTransfer(t1)){
			g.printGraph(t1);
		}
		if(t.doTransfer(t2)){
			g.printGraph(t2);
		}
		if(t.doTransfer(t3)){
			g.printGraph(t3);
		}
		if(t.doTransfer(t4)){
			g.printGraph(t4);
		}
		if(t.doTransfer(t5)){
			g.printGraph(t5);
		}
		if(t.doTransfer(t6)){
			g.printGraph(t6);
		}
		if(t.doTransfer(t7)){
			g.printGraph(t7);
		}
		if(t.doTransfer(t8)){
			g.printGraph(t8);
		}
		
	}
	

}
