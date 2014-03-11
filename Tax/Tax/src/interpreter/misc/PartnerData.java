package interpreter.misc;

import java.util.ArrayList;


/*
 * stores info about a partner.
 * Need this info to adjust tax.
 */
public class PartnerData {
	
	private String name;
	private double share;
	private double taxValue;
	private ArrayList<String> contributedAssets = new ArrayList<String>();
	
	public PartnerData(double share,String name){
		this.name = name;
		this.share = share;
	}
	
	public double getTaxValue(){
		return taxValue;
	}
	public void setTaxValue(double taxValue){
		this.taxValue = taxValue;
	}
	public double getShare(){
		return this.share;
	}
	public void setShare(double share) {
		this.share = share;
	}
	
	public String getName(){
		return this.name;
	}
	
	public ArrayList<String> getContributedAssets(){
		return contributedAssets;
	}
	
	
	public boolean equals(Object o){
		if(!(o instanceof PartnerData)){
			return false;
		}
		else if(o == this){
			return true;
		}
		else{
			PartnerData compare = (PartnerData) o;
			if(compare.getShare() == this.getShare() && compare.getName() == this.getName()){
				return true;
			}
		}
		return false;
	}
	
	
}
