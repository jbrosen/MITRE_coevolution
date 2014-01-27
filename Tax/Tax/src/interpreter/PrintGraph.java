package interpreter;

import interpreter.assets.Annuity;
import interpreter.assets.Assets;
import interpreter.assets.Material;
import interpreter.assets.PartnershipAsset;
import interpreter.entities.Entity;
import interpreter.misc.Transaction;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import evogpj.algorithm.Parameters;

public class PrintGraph {

	ArrayList<Entity> nodes = new ArrayList<Entity>();
	public static int count=0;
	public boolean toFile = false;
	public boolean printToScreen = false;
	
	public PrintGraph(ArrayList<Entity> nodes){
		this.nodes = nodes;
		//this.count = 0;
	}
	
	public void printGraph(Transaction transaction){
		
		if (toFile) {
		
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
		
		}
		
		else if (this.printToScreen) {
			
			System.out.println("XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX");
			
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
		}
		
	}
}
