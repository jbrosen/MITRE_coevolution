package interpreter.assets;

import interpreter.entities.Entity;
import interpreter.misc.Graph;
import interpreter.misc.PartnerData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Share extends Assets{

	private double share;
	public Share(double share){
		this.share = share;
		this.name = "";
	}
	
	public Share(Share obj){
		this.initialFMV = obj.initialFMV;
		this.currentFMV = obj.currentFMV;
		this.insideBasis = obj.getInsideBasis();
		this.taxValue = obj.taxValue;
		this.owners = (HashMap<String, Double>) obj.getOwners().clone();
		this.insideBasisMap = (HashMap<String, Double>) obj.getInsideBasisMap().clone();
	}
	@Override
	public void calculateTax(Entity from) {
		
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	public double getShare(){
		return share;
	}
	public void setShare(double share){
		this.share = share;
	}
	
	public void adjustPartnerShares(Entity from) {
		double oldShare;
		for (PartnerData pd : from.getPartnerData()) {
			oldShare = pd.getShare();
			pd.setShare(oldShare * this.getShare() / 100.0);
		}
		
//		adjust the share of each partner's PartnershipAsset
		for (Entity e : from.getPartners()) {
			for (int i = 0 ; i < e.getPortfolio().size() ; ++i) {
				Assets a = e.getPortfolio().get(i);
				if (a.toString().equals("PartnershipAsset")) {
					PartnershipAsset pship = (PartnershipAsset)a;
					if (pship.getName().equals(from.getName())) {
						oldShare = pship.getShare();
						((PartnershipAsset)e.getPortfolio().get(i)).setShare(oldShare * this.getShare() / 100.0);
						break;
					}
				}
			}
		}
		
	}
	
	@Override
	public void transfer(Entity from, Entity to,Assets asset) {
		
		this.adjustPartnerShares(from);
		
		PartnerData pd = new PartnerData(this.getShare(),to.getName());
		from.getPartnerData().add(pd);
		if(!from.getPartners().contains(to)){
			if (this.verbose)
				System.out.println("CREATING PARENT DATA");
			from.getPartners().add(to);
		}
		
		
		PartnershipAsset pa = new PartnershipAsset(this.getShare(),from.getName());
		pa.getOwners().put(to.getName(), this.getShare());
		to.getPortfolio().add(pa);
		if(!to.getPartnershipIn().contains(from)){
			if (this.verbose)
				System.out.println("CREATING CHILD DATA");

			to.getPartnershipIn().add(from);
		}
	}

	@Override
	public String toString() {
		return "Share";
	}
}
