package interpreter;

import interpreter.assets.Annuity;
import interpreter.assets.Assets;
import interpreter.assets.Material;
import interpreter.assets.PartnershipAsset;
import interpreter.entities.Entity;
import interpreter.misc.Transaction;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Iterator;

public class PrintGraph {

	ArrayList<Entity> nodes = new ArrayList<Entity>();
	public static int count=0;
	public PrintGraph(ArrayList<Entity> nodes){
		this.nodes = nodes;
		//this.count = 0;
	}
	
	public void printGraph(Transaction transaction){
	    //String name = "output" + this.count +".dot";
		//writeFile wf = new writeFile("/Users/Badar/MITRE_ArtificialStepUpBasisTransactions/code/code/Tax/Tax/src/interpreter/dot/" + name);
	   
	/*	System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		System.out.println("---State of the Graph after transaction "+transaction.toString() + "---");

		for(int i=0;i<nodes.size();i++){
			System.out.println("	+Node:" + nodes.get(i).getName());
			System.out.println("		*TotalTax: "+ nodes.get(i).getTotalTax());
			Iterator<Assets> it = nodes.get(i).getPortfolio().iterator();
			while(it.hasNext()){
				Assets asset = ((Assets) it.next());
				System.out.println("		*"+asset.toString());
				if(asset.toString().equals("PartnershipAsset")){
					System.out.println("		"+((PartnershipAsset) asset).printPAsset());

				}
				if(asset.toString().equals("Material")){
				System.out.println("		Name: " + ((Material) asset).getName());
				}
				System.out.println("		CFMV: " + asset.getCurrentFMV());
				System.out.println("		insideBasis: " + asset.getInsideBasis());
				System.out.println("		Map of Owner-->InsideBasis:");
				for(String s : asset.getInsideBasisMap().keySet()){
					System.out.println("                        " + s + ":" + asset.getInsideBasisMap().get(s).toString());
				}
				System.out.println("		Map of Owner--> IFMV:");

				for(String s : asset.getOwners().keySet()){
					System.out.println("                        " + s + ":" + asset.getOwners().get(s).toString());
				}
				
				if(asset.toString().equals("Annuity")){
					System.out.println("		Years: " + ((Annuity) asset).getYears());
				}
			}

		}
		System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		

		*/
		
//		System.out.println("MESS UP RIGHT HERE"); 
//		File file = new File("/Users/Badar/MITRE_ArtificialStepUpBasisTransactions/code/code/Tax/Tax/src/interpreter/output1.txt");
		
		File file = new File("C:\\Users\\Jacob\\Documents\\MIT\\SCOTE\\output1.txt");
		
		// if file doesnt exists, then create it
		if (!file.exists()) {
			try {
				file.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		FileWriter fw;
		try {
			fw = new FileWriter(file.getAbsoluteFile(),true);
			BufferedWriter bw = new BufferedWriter(fw);

		
		


		bw.write("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		bw.newLine();
		bw.write("---State of the Graph after transaction "+transaction.toString() + "---");
		bw.newLine();


		for(int i=0;i<nodes.size();i++){
			bw.write("	+Node:" + nodes.get(i).getName());
			bw.newLine();

			bw.write("		*TotalTax: "+ nodes.get(i).getTotalTax());
			bw.newLine();

			Iterator<Assets> it = nodes.get(i).getPortfolio().iterator();
			while(it.hasNext()){
				Assets asset = ((Assets) it.next());
				bw.write("		*"+asset.toString());
				bw.newLine();

				if(asset.toString().equals("PartnershipAsset")){
					bw.write("		"+((PartnershipAsset) asset).printPAsset());
					bw.newLine();


				}
				if(asset.toString().equals("Material")){
					bw.write("		Name: " + ((Material) asset).getName());
					bw.newLine();

				}
				bw.write("		CFMV: " + asset.getCurrentFMV());
				bw.newLine();

				bw.write("		insideBasis: " + asset.getInsideBasis());
				bw.newLine();

				bw.write("		Map of Owner-->InsideBasis:");
				bw.newLine();

				for(String s : asset.getInsideBasisMap().keySet()){
					bw.write("                        " + s + ":" + asset.getInsideBasisMap().get(s).toString());
					bw.newLine();

				}
				bw.write("		Map of Owner--> IFMV:");
				bw.newLine();


				for(String s : asset.getOwners().keySet()){
					bw.write("                        " + s + ":" + asset.getOwners().get(s).toString());
					bw.newLine();

				}
				
				if(asset.toString().equals("Annuity")){
					bw.write("		Years: " + ((Annuity) asset).getYears());
					bw.newLine();

				}
			}

		}
		bw.write("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
		
		bw.newLine();
		bw.flush();
		
		
		bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		


		
		
		///DO NOT USE THIS CODE
		
		
		/*System.out.println("+Transaction");
		System.out.println("*Action1");
		System.out.println("from:"+transaction.getAction1().getFrom());
		System.out.println("to:"+transaction.getAction1().getTo());
		System.out.println("Asset:"+transaction.getAction1().getTransferableAssets());
		System.out.println("*Action2");
		System.out.println("from:"+transaction.getAction2().getFrom());
		System.out.println("to:"+transaction.getAction2().getTo());
		System.out.println("Asset:"+transaction.getAction2().getTransferableAssets());


		for(int i=0;i<nodes.size();i++){
			System.out.println("+Node");
			System.out.println("Name:"+nodes.get(i).getName());
			System.out.println("TotalTax:"+ nodes.get(i).getTotalTax());
			if(nodes.get(i).getType().equals("Partnership")){
				System.out.println("Type:Partnership");

			}
			else{
				System.out.println("Type:TaxPayer");

			}
			Iterator<Assets> it = nodes.get(i).getPortfolio().iterator();
			while(it.hasNext()){
				Assets asset = ((Assets) it.next());
				System.out.println("*"+asset.toString());
				if(asset.toString().equals("PartnershipAsset")){
					System.out.println("Name:"+((PartnershipAsset) asset).getName());
					System.out.println("CFMV:"+asset.getCurrentFMV());
					System.out.println("OBasis:"+((PartnershipAsset) asset).getOutsideBasis());
					System.out.println("Share:"+((PartnershipAsset) asset).getShare());


				}
				else{
					if(asset.toString().equals("Material")){
					System.out.println("Name:" + ((Material) asset).getName());
					}
					if(asset.toString().equals("Annuity")){
						System.out.println("Years:" + ((Annuity) asset).getYears());
					}
					System.out.println("CFMV:" + asset.getCurrentFMV());
					System.out.println("InsideBasis:" + asset.getInsideBasis());
				
				}
				
				System.out.println("*Map:InsideBasis");
				for(String s : asset.getInsideBasisMap().keySet()){
					System.out.println(s + ":" + asset.getInsideBasisMap().get(s).toString());
				}
				System.out.println("*Map:IFMV");

				for(String s : asset.getOwners().keySet()){
					System.out.println(s + ":" + asset.getOwners().get(s).toString());
				}
			}

		}
		System.out.println("XXX");
		
		*/
		
		
		
		
	/*	String s ="";
		s +="digraph G {" + "\n" + "size=\"8,8\";" + "center=true;nodesep=\"3.0\";node[style=filled,color=\".7 .3 1.0\"];edge [style=dotted];" +"\n";

		for(int i=0;i<nodes.size();i++){
			s+=nodes.get(i).getName() +"["+"label="+"\""+ nodes.get(i).getName() +"\\n";
			s+="*TotalTax: " + nodes.get(i).getTotalTax() +"\\n";
			Iterator<Assets> it = nodes.get(i).getPortfolio().iterator();
			String p ="";
			while(it.hasNext()){
				
				Assets asset = ((Assets) it.next());
				s+=asset.toString() + "\\n";
				if(asset.toString().equals("PartnershipAsset")){
				//	s+="		"+((PartnershipAsset) asset).printPAsset() +"\\n";
				/*	String toP = ((PartnershipAsset) asset).getName();
					String fromP = nodes.get(i).getName();
					p += fromP + "->" + toP + "[style=dotted];"+ "\n"; */
/*				}
				s+="FMvalue: " + asset.getCurrentFMV()+"\\n";
				s+="insideBasis: " + asset.getInsideBasis()+"\\n";
				
			
			}
			s+="\""+"];"+"\n";
			s+=p;
		}
		s+="edge [color=red,style=filled];" + "\n";
		String from = transaction.getAction1().getFrom();
		String to = transaction.getAction1().getTo();
		String asset1 = transaction.getAction1().getTransferableAssets().toString();
		String asset2 = transaction.getAction2().getTransferableAssets().toString();

		s+=from +"->"+to + "[label=\""+ asset1 + "\""+"];"+"\n";
		s+=to +"->"+from+ "[label=\""+ asset2 + "\""+"];"+"\n";
		s+="}";
		wf.writeToText(s);
		this.count+=1;
	*/	
	}
}
