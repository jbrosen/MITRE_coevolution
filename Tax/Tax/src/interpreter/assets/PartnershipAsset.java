package interpreter.assets;

import interpreter.entities.Entity;
import interpreter.entities.Partnership;
import interpreter.misc.Graph;
import interpreter.misc.PartnerData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class PartnershipAsset extends Assets{

	private String name;
	private double share;
	private double outsideBasis;
	

	public PartnershipAsset(double share,String name){	
		this.name = name;
		this.share = share;
	}
	
	public PartnershipAsset(PartnershipAsset obj){
		this.name  = obj.getName();
		this.share = obj.getShare();
		this.currentFMV = obj.getCurrentFMV();
		this.insideBasis = obj.getInsideBasis();
		this.taxValue = obj.taxValue;
		this.owners = (HashMap<String, Double>) obj.getOwners().clone();
		this.insideBasisMap = (HashMap<String, Double>) obj.getInsideBasisMap().clone();
	}
	
	public String getName(){
		return name;
	}
	
	public double getShare(){
		return share;
	}
	
	
	public double getOutsideBasis(){
		//System.out.println("INSIDE RECURSIVE OUTSIDE BASIS");

		double outsideBasis=0;
		ArrayList<Entity> nodes = Graph.nodesList;
		Entity child = null;
		for(Entity e: nodes){
			if(this.name.equals(e.getName())){
				child = e;
				break;
			}
		}
		for(String ownerName:this.getOwners().keySet()){
	
			for(Assets asset: child.getPortfolio()){
				if(asset.toString().equals("PartnershipAsset")){
					outsideBasis += ((PartnershipAsset) asset).getOutsideBasis()*(this.getShare()/100);
				}
				else if(asset.getOwners().containsKey(ownerName)){
					//check if the name is in the inside Basis Map first.If so, then use that.
					if(asset.getInsideBasisMap().containsKey(ownerName)){
						outsideBasis+=asset.getInsideBasisMap().get(ownerName);
						//System.out.println("outside basis in map:" + outsideBasis);

					}
					else{
						//System.out.println("outside basis not in map:" + asset.getInsideBasis());

						outsideBasis += asset.getInsideBasis();
					}
				}
			}
	}
		//System.out.println("VALUE OF OUTSIDE BASIS:" + outsideBasis);

		return outsideBasis;
	}
	
	public void setShare(double share){
		this.share = share;
	}

	
	@Override
	public double getCurrentFMV(){
		//System.out.println("INSIDE RECURSIVE CFMV");
		double CFMV=0;
		ArrayList<Entity> nodes = Graph.nodesList;
		Entity child = null;
		for(Entity e: nodes){
			if(this.getName().equals(e.getName())){
				//System.out.println("CHILD FOUND:"+e.getName());
				child = e;
				break;
			}
		}
		for(String ownerName:this.getOwners().keySet()){
			//System.out.println("OWNER NAME:" + ownerName);

			for(Assets asset: child.getPortfolio()){
				//System.out.println("asset found:"+ asset.toString());
	
				if(asset.getOwners().containsKey(ownerName) ){
					CFMV += asset.getCurrentFMV();
					//System.out.println("VALUE OF CFMV:" + CFMV);
				}
				else if(asset.toString().equals("PartnershipAsset")){
					CFMV += asset.getCurrentFMV() * (this.getShare()/100);
				}
			}
		}

		return CFMV;
	}
	
	public void setOutsideBasis(double outsideBasis){
		this.outsideBasis = outsideBasis;
	}
	
	@Override
	public void calculateTax(Entity from) {
		if (this.verbose)
			System.out.println("CALCULATING TAX FOR PASSET");
		Double diff = this.getCurrentFMV() - this.getOutsideBasis();
		if (this.verbose) {
			System.out.println("THE CFMV IS:"+ this.getCurrentFMV());
			System.out.println("DIFF IS:"+ diff);
		}

		if(from.getType().equals("TaxPayer")){

			if(from.getTotalTax() == Double.MIN_VALUE){
				from.setTotalTax(diff);
			}
			else{
				from.setTotalTax(from.getTotalTax() + diff);
			}
		}
		else if(from.getType().equals("Partnership")){
			from.setTotalTax(from.getTotalTax() + diff);
			if (this.verbose)
				System.out.println("NOW PUSH TAX UPWARDS");

			((Partnership) from).pushTaxToPartners();
		}
	
	}

	@Override
	public void transfer(Entity from, Entity to,Assets otherAsset) {
		if (this.verbose)
			System.out.println("from: " + from.getName() + " -->" + " to: " + to.getName());
		

		boolean fromFound = false;
		boolean toFound = false;
		Assets fromAsset = from.getAssetToBeTransferred();
		Assets toAsset = to.getAssetToBeTransferred();
		ArrayList<Assets> fromPortfolio = from.getPortfolio();
		ArrayList<Assets> toPortfolio = to.getPortfolio();
		Iterator<Assets> fromItr = fromPortfolio.iterator();
		Iterator<Assets> toItr = toPortfolio.iterator();
		//check if the cash value > CFMV

	
		//change name of owner before transfer
			fromAsset.getOwners().clear();
			fromAsset.getOwners().put(to.getName(), otherAsset.getCurrentFMV());
			toPortfolio.add(fromAsset);
			fromPortfolio.remove(fromAsset);
			update(from,to,otherAsset);
		
	}
	
	/*
	 * Update 
	 */
	public void update(Entity from, Entity to,Assets otherAsset){
		if (this.verbose)
			System.out.println("UPDATE");
		//System.exit(0);
		printPAsset();
		
		//Add a pointer from toAsset to child
		Entity child = null;
		ArrayList<Entity> part = (from).getPartnershipIn();
		for(Entity p: part){
			if(p.getName().equals(this.getName())){
				child = p;
				if (this.verbose)
					System.out.println("CHILD : " + child.getName());
				break;
			}
		}
	
		ArrayList<Entity> partIn = (to).getPartnershipIn();
		if(child!=null){
			if(!partIn.contains(child)){
				partIn.add(child);
			}
		
		
		//Add parent pointer in child
			ArrayList<Entity> partner = child.getPartners();
			if(!partner.contains(to)){
				partner.add(to);
			}
		
		//add parent data in child 
			PartnerData pd = new PartnerData(this.getShare(),to.getName());
			child.getPartnerData().add(pd);
		}
		else{
				System.err.println("Child was Null\n");
			}
		//remove old parent data in child
		ArrayList<PartnerData> pdata = child.getPartnerData();
		for(int i=0;i<pdata.size();i++){
			if(pdata.get(i).getName().equals(from.getName())){
				pdata.remove(i);
				break;
			}
		}

		//change inside basis and IFMV and name of owners of assets owned by parent.
		for(Assets asset : child.getPortfolio()){
			if(asset.getOwners().containsKey(from.getName())){
				asset.getOwners().put(to.getName(),otherAsset.getCurrentFMV());
				asset.getOwners().remove(from.getName());
				asset.getInsideBasisMap().remove(from.getName());
				asset.getInsideBasisMap().put(to.getName(), asset.getCurrentFMV());
			}
		}
	}
	

	
	public boolean equals(Object o){
		if(!(o instanceof PartnershipAsset)){
			return false;
		}
		else if(o == this){
			return true;
		}
		else{
			PartnershipAsset compare = (PartnershipAsset) o;
			if(compare.getInsideBasis() == (this.getInsideBasis()) && compare.getInitialFMV() == this.getInitialFMV() && compare.getShare() == this.getShare() && compare.getName().equals(this.getName())){
				return true;
			}
		}
		return false;
	}
	
	public int hashCode()
	{
		int hash = (int) (this.getInsideBasis()*getInitialFMV()*getShare() + 7*getName().hashCode());
		return hash;
	}
	
	
	@Override
	public String toString() {
		return "PartnershipAsset";
	}
	
	public String printPAsset() {
		return "Name:" + this.getName() + " CFMV:" + this.getCurrentFMV() + " OutsideBasis:" + this.getOutsideBasis() + " Share:" + this.getShare();
	}


}
