package interpreter.entities;


import interpreter.assets.Assets;
import interpreter.assets.Cash;
import interpreter.misc.PartnerData;

import java.util.ArrayList;
import java.util.Iterator;

public class Company implements Entity{
	private final ArrayList<Assets> Portfolio = new ArrayList<Assets>();
	private String name;
	private double totalTax = 0;
	private Assets assetToBeTransferred;
	public Assets otherAssetClone;

	
	public Company(String name){
		this.name = name;
	}
	
	public String getName(){
		return name;
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
	
	@Override
	public ArrayList<Assets> getPortfolio(){
		return Portfolio;
	}

	public double getTotalTax() {
		return totalTax;
	}

	public void setTotalTax(double totalTax) {
		this.totalTax = totalTax;
	}
	public String getType(){
		return "Company";
	}

	@Override
	public double getStartTax() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ArrayList<Entity> getPartners() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<Entity> getPartnershipIn() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ArrayList<PartnerData> getPartnerData() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAssetToBeTransferred(Assets fromAsset) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Assets getAssetToBeTransferred() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setAssetToBeTransferredClone(Assets asset) {
		this.otherAssetClone = asset;
	}

	@Override
	public Assets getAssetToBeTransferredClone() {
		return otherAssetClone;
	}
}
