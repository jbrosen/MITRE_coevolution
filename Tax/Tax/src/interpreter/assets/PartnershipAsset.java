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
	
	public PartnershipAsset(PartnershipAsset obj, double newShare){
		this.name  = obj.getName();
		this.share = newShare;
		this.owners = (HashMap<String, Double>) obj.getOwners().clone();
		this.insideBasisMap = (HashMap<String, Double>) obj.getInsideBasisMap().clone();
		
		this.currentFMV = this.getCurrentFMV();
		this.insideBasis = obj.getInsideBasis();
		this.taxValue = obj.taxValue;

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
//		find the node that represents yourself in the Graph
		for(Entity e: nodes){
			if(this.name.equals(e.getName())){
				child = e;
				break;
			}
		}
//		for every owner of the PartnershipAsset
		for(String ownerName:this.getOwners().keySet()){
//			for every asset in the Partnership's portfolio
			for(Assets asset: child.getPortfolio()){
//				recursively call the method if the asset is also a PartnershipAsset
				if(asset.toString().equals("PartnershipAsset")){
					outsideBasis += ((PartnershipAsset) asset).getOutsideBasis()*(this.getShare()/100);
				}
//				find the asset owned by the partnership and its corresponding owner
				else if(asset.getOwners().containsKey(ownerName)){
					//check if the name is in the inside Basis Map first.If so, then use that.
					if(asset.getInsideBasisMap().containsKey(ownerName)){
						outsideBasis+=asset.getInsideBasisMap().get(ownerName);
						//System.out.println("outside basis in map:" + outsideBasis);
					}
					else {
						//System.out.println("outside basis not in map:" + asset.getInsideBasis());
						outsideBasis += asset.getInsideBasis();
					}
				}
			}
		}
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
//		find the Partnership that this asset represents in the graph
		for(Entity e: nodes){
			if(this.getName().equals(e.getName())){
				//System.out.println("CHILD FOUND:"+e.getName());
				child = e;
				break;
			}
		}
//		System.out.println("START: "+this.getName());
//		for all entities that own a share in the PartnershipAsset
		for(String ownerName:this.getOwners().keySet()){
//			System.out.println("OWNER NAME:" + ownerName);
//			and for all assets that the Partnership owns
			for(Assets asset: child.getPortfolio()){
//				System.out.println("asset found:"+ asset.getName());
//				CHECK WHAT MY ASSETS
//				if the asset owned by the Partnership is also owned by an entity with a share in the partnership
				if(asset.getOwners().containsKey(ownerName) ){
//					Add the CFMV of the asset to this asset
					CFMV += asset.getCurrentFMV();
//					System.out.println("1VALUE OF CFMV:" + CFMV);
				}
//				otherwise, if the asset that this Partnership owns is also a PartnershipAsset 
				else if(asset.toString().equals("PartnershipAsset")){
//					Add the share percentage that this PartnershipAsset represents of the asset to the list
//					I think the problem arises when the asset tries to call itself.
//					Should I put a clause in that passes it up? Or is it illegal to begin with?
					CFMV += asset.getCurrentFMV() * (this.getShare()/100);
//					System.out.println("2VALUE OF CFMV:" + CFMV);
				}
			}
		}
//		System.out.println("CFMV: "+CFMV);
		return CFMV;
	}
	
	
	/*
	 * Function that gets a percentage of the FMV based on a partial share
	 */
	public double getCurrentFMV(double partialShare) {
		return (partialShare / this.share) * this.getCurrentFMV();
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
		
		/*
		 * Setup if-else block to see if the whole pship asset is bought or just part of it
		 */
//		get the share that is being transfered
		double newShare = ((PartnershipAsset)from.getAssetToBeTransferredClone()).getShare();
		double oldShare = ((PartnershipAsset)fromAsset).getShare();
		
		if (newShare == oldShare) {
			//change name of owner before transfer
			fromAsset.getOwners().clear();
			fromAsset.getInsideBasisMap().clear();
			fromAsset.getOwners().put(to.getName(), otherAsset.getCurrentFMV());
			fromAsset.getInsideBasisMap().put(to.getName(), otherAsset.getCurrentFMV());
			toPortfolio.add(fromAsset);
			fromPortfolio.remove(fromAsset);
			update(from,to,otherAsset);
		}
		else {
//			set the new share in FROM's portfolio
			((PartnershipAsset)fromAsset).setShare(oldShare - newShare);
//			set the new basis in the partnership asset to be the percentage of the original share
			double percent = newShare / ((PartnershipAsset)fromAsset).getShare();
			double originalBasis = fromAsset.getOwners().get(from.getName());
			fromAsset.getOwners().put(from.getName(), originalBasis * percent);
			
			toPortfolio.add(new PartnershipAsset((PartnershipAsset)fromAsset,newShare));
			update(from,to,otherAsset);
			
		}
	}
	
	/*
	 * Update 
	 */
	public void update(Entity from, Entity to,Assets otherAsset){
		if (this.verbose)
			System.out.println("UPDATE");
		//System.exit(0);
		printPAsset();
//		System.out.println(from.getName()+" gives "+to.getName()+" "+this.getName()+" for "+otherAsset.getName());
		PartnershipAsset fromAsset = (PartnershipAsset)from.getAssetToBeTransferredClone();
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
		//adjust old parent data in child
		ArrayList<PartnerData> pdata = child.getPartnerData();
		double oldShare = 0.0;
		for(int i=0;i<pdata.size();i++){
			if(pdata.get(i).getName().equals(from.getName())){
//				if the entire PartnershipAsset is purchased
				if (pdata.get(i).getShare() == fromAsset.getShare()) {
					oldShare = fromAsset.getShare();
					pdata.remove(i);
				}
//				otherwise, if only part is purchased, just adjust the data
				else {
					oldShare = pdata.get(i).getShare();
					pdata.get(i).setShare(oldShare - fromAsset.getShare());
				}
				break;
			}
		}
		
		//change inside basis and FMV and name of owners of assets owned by parent.
		for(Assets asset : child.getPortfolio()){
			if(asset.getOwners().containsKey(from.getName())){
				asset.getOwners().put(to.getName(),otherAsset.getCurrentFMV());
				asset.getInsideBasisMap().put(to.getName(), asset.getCurrentFMV());
//				if the whole share is taken
				if (oldShare == fromAsset.getShare()) {
					asset.getOwners().remove(from.getName());
					asset.getInsideBasisMap().remove(from.getName());
					
				}
//				otherwise, set the new inside basis as the percentage of the old and new shares
				else if (asset.getInsideBasisMap().containsKey(from.getName())) {
//					System.out.println(from.getName());
//					System.out.println(asset.getInsideBasisMap().keySet());
//					System.out.println(asset.getOwners().keySet());
					
					double oldInsideBasis = asset.getInsideBasisMap().get(from.getName());
					asset.getInsideBasisMap().put(from.getName(), oldInsideBasis * fromAsset.getShare() / oldShare);
				}
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
