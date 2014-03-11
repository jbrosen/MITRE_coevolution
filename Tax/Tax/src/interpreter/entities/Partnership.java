package interpreter.entities;


import interpreter.assets.Assets;
import interpreter.assets.Cash;
import interpreter.misc.PartnerData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import evogpj.algorithm.Parameters;

/*
 * Each node stores info about parent nodes and child nodes.
 * Parent nodes are partners who have partnership in this node.
 * child nodes are the ones where this node has shares in.they 
 * are stored in partnership assets in the Portfolio.
 */
public class Partnership implements Entity{
	private final ArrayList<Entity> partnershipIn = new ArrayList<Entity>();
	private final ArrayList<Assets> Portfolio = new ArrayList<Assets>();
	private final ArrayList<Entity> partners = new ArrayList<Entity>();
	private final ArrayList<PartnerData> partnerData = new ArrayList<PartnerData>();
    private Assets assetToBeTransferred;
	public Assets assetClone;
	private boolean verbose = Parameters.Defaults.VERBOSE;


	private String name;
	private double totalTax = 0;

	public Partnership(String name){
		this.name = name;
	}
	@Override
	public String getName() {
		return name;
	}

	@Override
	public ArrayList<Assets> getPortfolio() {
		return Portfolio;
	}

	@Override
	public double getTotalTax() {
		return totalTax;
	}

	@Override
	public void setTotalTax(double totalTax) {
		this.totalTax = totalTax;		
	}

	@Override
	public double getStartTax() {
		return 0;
	}
	
	/*parent nodes*/
	public ArrayList<Entity> getPartners() {
		return partners;
	}
	
	public boolean isOwnedBy(Entity e) {
		
		for (Entity parent : this.getPartners()) {
			if (parent.getName() == e.getName())
				return true;
		}
		
		return false;
	}
	
	
	/*child nodes*/
	public ArrayList<Entity> getPartnershipIn() {
		return partnershipIn;
	}
	
	public boolean ownsPartnershipIn(Entity e) {
		for (Entity child : this.partnershipIn) {
			if (child.getName() == e.getName()) {
				return true;
			}
		}
		return false;
	}
	
	public ArrayList<PartnerData> getPartnerData() {
		return this.partnerData;
	}
	@Override
	public Assets getAssetToBeTransferred() {
		return this.assetToBeTransferred;
	}
	@Override
	public void setAssetToBeTransferred(Assets asset) {
		this.assetToBeTransferred = asset;		
	}
	
	@Override
	public String getType() {
		return "Partnership";
	}
	@Override
	public void setAssetToBeTransferredClone(Assets asset) {
		this.assetClone = asset;
	}
	@Override
	public Assets getAssetToBeTransferredClone() {
		return assetClone;
	}
	public void pushTaxToPartners(){
		if (this.verbose)
			System.out.println("PUSH TAX TO PARTNERS");
		for(PartnerData pd : this.getPartnerData()){
			if (this.verbose)
				System.out.println("partners that tax needs to be pushed to:" + pd.getName());
			
			for(Entity e: this.getPartners()){
				if(e.getType().equals("Partnership") && pd.getName().equals(e.getName())){
					double tax = (pd.getShare()/100.0)*this.getTotalTax();
					tax+=e.getTotalTax();
					e.setTotalTax(tax);
					if (this.verbose)
						System.out.println("TAX VALUES trying to push in pship "+e.getName()+":"+tax);
					((Partnership) e).pushTaxToPartners();
				}
				else if(pd.getName().equals(e.getName()) && e.getType().equals("TaxPayer")){
					if (this.verbose)
						System.out.println("name of Tax Payer is:"+e.getName());
					
					double tax = (pd.getShare()/100.0)*this.getTotalTax();
					
					if(e.getTotalTax() == Double.MIN_VALUE){
						e.setTotalTax(tax);
					}
					else{
						tax+=e.getTotalTax();
						e.setTotalTax(tax);
					}
					if (this.verbose)
						System.out.println("TAX VALUES is not in partnership:"+tax);
				}
			}
		}
	}
	
	@Override
	public double getTotalCash() {
		Iterator<Assets> portItr = Portfolio.iterator();
		double totCash = 0;
		while (portItr.hasNext()) {
			Assets thisAsset = portItr.next();
			if (thisAsset.toString() == "Cash") {
				totCash += thisAsset.getCurrentFMV();
			}	
		}
		return totCash;
	}
	
	@Override
	public ArrayList<Cash> getCash() {
		Iterator<Assets> portItr = Portfolio.iterator();
		ArrayList<Cash> ret = new ArrayList<Cash>();
		while (portItr.hasNext()) {
			Assets thisAsset = portItr.next();
			if (thisAsset.toString() == "Cash") {
				ret.add((Cash)thisAsset);
			}
		}
		return ret;
	}
	
}
