package interpreter.assets;

import interpreter.entities.Entity;
import interpreter.entities.Partnership;
import interpreter.misc.PartnerData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

public class Material extends Assets{

	private String name;
	private int quantity;
	private double value;
	
	public Material(double value,String name,int quantity) {
		this.currentFMV = value;
		this.name = name;
		this.quantity = quantity;
	}
	public Material(Material obj){
		this.initialFMV = obj.initialFMV;
		this.currentFMV = obj.currentFMV;
		this.insideBasis = obj.getInsideBasis();
		this.taxValue = obj.taxValue;
		this.owners = (HashMap<String, Double>) obj.getOwners().clone();
		this.insideBasisMap = (HashMap<String, Double>) obj.getInsideBasisMap().clone();
	}
	@Override
	public void calculateTax(Entity from) {
		
		double tax = 0;
		//ArrayList<Entity> nodes = Graph.nodesList;
//		if FROM is transferring this asset
		if(from.getType().equals("TaxPayer")){
//			difference between FMV and inside basis of its clone
			double diff = this.getCurrentFMV() - from.getAssetToBeTransferredClone().getInsideBasis();
//			set total tax FROM's tax plus the difference between FMV and inside basis
			if(from.getTotalTax() == Double.MIN_VALUE){
				from.setTotalTax(diff);
			}
			else{
				from.setTotalTax(from.getTotalTax() + diff);
			}
		}
//		if FROM is NOT a taxpayer
		else{
			//partnership-divide profit between all partners.
			double totalIFMV=0;
//			for each owner of the asset being transfered
			for(String name : from.getAssetToBeTransferredClone().getOwners().keySet()){
//				increment the total initial market value
				totalIFMV+=from.getAssetToBeTransferredClone().getOwners().get(name);
//				find the owner from the for loop above to get the PartnerData object of this
				for(PartnerData partnerData: from.getPartnerData()){
					if(partnerData.getName().equals(name)){
//						if they already have an inside basis
						if(from.getAssetToBeTransferredClone().getInsideBasisMap().containsKey(name)){
//							set the tax value of its share in the partnership to be its market value minus the inside basis
							double insideBasis = from.getAssetToBeTransferredClone().getInsideBasisMap().get(name);
							if (this.verbose)
								System.out.println("I AM HERE AND MY TOTAL VALUE IS:"+ insideBasis);
							
							tax = from.getAssetToBeTransferredClone().getOwners().get(name) - insideBasis;
							if (this.verbose)
								System.out.println("I AM HERE AND MY TOTAL VALUE IS:"+ tax);
							partnerData.setTaxValue(tax);
						}
						else{
							tax = Math.max(0.0,from.getAssetToBeTransferredClone().getOwners().get(name) - from.getAssetToBeTransferredClone().getInsideBasis());
							partnerData.setTaxValue(tax);
							}	
						}
					}
				}
//			for each partner in FROM, find the associated PartnerData object
			for(Entity e: from.getPartners()){
				for(PartnerData p: from.getPartnerData()){
					if(e.getName().equals(p.getName())){
//						tax is current market value minus the sum of the amount each partner paid for it
						double totalTax = this.getCurrentFMV() - totalIFMV;
						if (this.verbose) {
							System.out.println("TAX Before :" + totalTax);
	
							System.out.println("name :" + p.getName());
							System.out.println("share :" + p.getShare());
						}
//						tax for each partner is proportional to its share
						totalTax = (p.getShare()/100)*totalTax;
						if (this.verbose)
							System.out.println("TAX NOW :" + totalTax);
						
//						push the tax from the ParterData asset to the taxpayer and 
						if(e.getType().equals("TaxPayer")){
							
							if(e.getTotalTax() == Double.MIN_VALUE){
								e.setTotalTax(p.getTaxValue());
								p.setTaxValue(0);
							}
							else{
								totalTax += p.getTaxValue();
								totalTax += e.getTotalTax();
								e.setTotalTax(totalTax);
								p.setTaxValue(0);
							}
						}
						
						if(e.getType().equals("Partnership")){
							totalTax += p.getTaxValue();
							totalTax += e.getTotalTax();
							e.setTotalTax(totalTax);
							p.setTaxValue(0);
							//recursively push up the values to all partners.
							((Partnership) e).pushTaxToPartners();
						}
					}
				}
			}
		}
	}
	
	/*
	 * Performs a Material transfer operation between two entities.
	 * @param from Entity from which asset is transfered
	 * @param to Entity to which asset is transfered
	 */
	@Override
	public void transfer(Entity from, Entity to,Assets otherAsset) {
		//System.out.println("from: " + from.getName() + " -->" + " to: " + to.getName());

		boolean fromFound = false;
		boolean toFound = false;
		Assets fromAsset = from.getAssetToBeTransferred();
		Assets toAsset = to.getAssetToBeTransferred();
		ArrayList<Assets> fromPortfolio = from.getPortfolio();
		ArrayList<Assets> toPortfolio = to.getPortfolio();
		Iterator<Assets> fromItr = fromPortfolio.iterator();
		Iterator<Assets> toItr = toPortfolio.iterator();
		
		
		
		if(otherAsset.toString().equals("Share")){
			//contribute asset
			fromPortfolio.remove(fromAsset);
			this.getOwners().putAll(fromAsset.getOwners());
			this.setInsideBasis(fromAsset.getInsideBasis());
			toPortfolio.add(this);
		}
		else{
			fromPortfolio.remove(fromAsset);
			//System.out.println("THE VALUE OF CFMV TO TEST:" + from.getAssetToBeTransferredClone().getCurrentFMV());
			//this.setCurrentFMV(from.getAssetToBeTransferredClone().getCurrentFMV());
			if(otherAsset.toString().equals("Annuity")){
				this.getOwners().put(to.getName(), this.getCurrentFMV());
				this.setInsideBasis(this.getCurrentFMV());
				toPortfolio.add(this);
			}
			else{
				(this.getOwners()).putAll(to.getAssetToBeTransferredClone().getOwners());
				this.setInsideBasis(otherAsset.getCurrentFMV());
				toPortfolio.add(this);
			}
		}
	}
	
	public String getName(){
		return name;
	}
	public void setName(String name){
		this.name = name;
	}
	
	public int getQuantity(){
		return quantity;
	}
	public void setQuantity(int quantity){
		this.quantity = quantity;
	}



	public boolean equals(Object o){
		if(!(o instanceof Material)){
			return false;
		}
		else if(o == this){
			return true;
		}
		else{
			Material compare = (Material) o;
			if(compare.getName().equals(this.getName()) && compare.getInitialFMV() == this.getInitialFMV() && compare.getQuantity() == this.getQuantity()){
				return true;
			}
		}
		return false;
	}
	
	
	public int hashCode()
	{
		int hash = (int) (this.getInitialFMV() + 7*getName().hashCode());
		return hash;
	}
	
	
	
	@Override
	public String toString() {
		return "Material";
	}


}
