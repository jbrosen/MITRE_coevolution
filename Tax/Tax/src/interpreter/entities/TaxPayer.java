package interpreter.entities;


import interpreter.assets.Assets;
import interpreter.assets.Cash;
import interpreter.misc.PartnerData;

import java.util.ArrayList;
import java.util.Iterator;

public class TaxPayer implements Entity{
	private final ArrayList<Assets> Portfolio = new ArrayList<Assets>();
	private final ArrayList<Entity> partnershipIn = new ArrayList<Entity>();
	private final ArrayList<Entity> partners = new ArrayList<Entity>();
	private final ArrayList<PartnerData> parentData = new ArrayList<PartnerData>();

	private String name;
	private double totalTax = 0;
	private double startTax = 0;
	private Assets assetToBeTransferred;
	public Assets assetClone;

	private boolean canBeTaxed = false;
	
	
	public TaxPayer(String name){
		this.name = name;
	}
	public String getName(){
		return name;
	}
	
	public boolean getCanBeTaxed(){
		return this.canBeTaxed;
	}
	public void setCanBeTaxed(boolean canBeTaxed){
		this.canBeTaxed = canBeTaxed;
	}
	
	public boolean ownsPartnershipIn(Entity e) {
		for (Entity child : this.partnershipIn) {
			if (child.getName() == e.getName()) {
				return true;
			}
		}
		return false;
	}
	
	@Override
	public ArrayList<Assets> getPortfolio(){
		return Portfolio;
	}
	public double getTotalTax() {
		return this.totalTax;
	}
	public double getStartTax() {
		return startTax;
	}
	
	public void setTotalTax(double totalTax) {
		this.totalTax = totalTax;
	}

	public void calculuateStartTax(){
//		test, tax starts at 80
		this.startTax=80;
		this.setTotalTax(this.startTax);
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
	
	public String getType(){
		return "TaxPayer";
	}
	@Override
	public ArrayList<Entity> getPartners() {
		return partners;
	}
	@Override
	public ArrayList<Entity> getPartnershipIn() {
		return partnershipIn;
	}
	@Override
	public ArrayList<PartnerData> getPartnerData() {
		return parentData;
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
	public void setAssetToBeTransferredClone(Assets asset){		
		this.assetClone = asset;
	}
	@Override
	public Assets getAssetToBeTransferredClone() {
		return this.assetClone;
	}
}
