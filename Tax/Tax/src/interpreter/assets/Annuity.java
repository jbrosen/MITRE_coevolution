package interpreter.assets;

import interpreter.entities.Entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Annuity extends Assets{

	private int years;
	private double value;
	public Annuity(int value , int years) {
		this.currentFMV = value;
		this.years = years;
		this.name = "Annuity, "+this.years+" year, $"+this.currentFMV;
	}

	public Annuity(Annuity obj){
		this.currentFMV = obj.getCurrentFMV();
		this.years = obj.getYears();
		this.taxValue = obj.getTaxValue();
		this.owners = (HashMap<String, Double>) obj.getOwners().clone();
		this.insideBasisMap = (HashMap<String, Double>) obj.getInsideBasisMap().clone();
	}
	@Override
	public void calculateTax(Entity from) {
		//return 0;		
	}

	@Override
	public String getName() {
		return this.name;
	}
	
	public void setYears(int years){
		this.years = years;
	}
	public int getYears(){
		return years;
	}


	/*
	 * Performs a Annuity transfer operation between two entities.
	 * @param from Entity from which asset is transfered
	 * @param to Entity to which asset is transfered
	 */
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
		
		toPortfolio.add(this);
	
		
	}
	
	


	@Override
	public String toString() {
		return "Annuity";
	}

}
