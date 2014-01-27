package interpreter.assets;

import interpreter.entities.Entity;
import interpreter.misc.PartnerData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Cash extends Assets{

	public Cash(double value){
		this.initialFMV = value;
		this.currentFMV = value;
	}
	
	public Cash(Cash obj){
		this.initialFMV = obj.initialFMV;
		this.currentFMV = obj.currentFMV;
		this.insideBasis = obj.getInsideBasis();
		this.taxValue = obj.taxValue;
		this.owners = (HashMap<String, Double>) obj.getOwners().clone();
		this.insideBasisMap = (HashMap<String, Double>) obj.getInsideBasisMap().clone();
	}
	@Override
	public void calculateTax(Entity from) {
		//return 0.25 * this.getCurrentFMV();
	}
	
	@Override
	public void setCurrentFMV(double currentFMV){
		this.currentFMV = currentFMV;
		
	}
	
	
	/*
	 * Performs a cash transfer operation between two entities.
	 * @param from Entity from which asset is transfered
	 * @param to Entity to which asset is transfered.
	 */
	@Override
	public void transfer(Entity from, Entity to,Assets otherAsset) {	
		//System.out.println("from: " + from.getName() + " -->" + " to: " + to.getName());
		
		Assets fromAsset = from.getAssetToBeTransferred();
		Assets toAsset = to.getAssetToBeTransferred();
		ArrayList<Assets> fromPortfolio = from.getPortfolio();
		ArrayList<Assets> toPortfolio = to.getPortfolio();
		Iterator<Assets> fromItr = fromPortfolio.iterator();
		Iterator<Assets> toItr = toPortfolio.iterator();
		boolean fromFound = false;
		boolean toFound = false;

		if(otherAsset.toString().equals("Share")){
			if (this.verbose)
				System.out.println("SHARE");
			
			this.setInsideBasis(this.getCurrentFMV());
			for(String s : from.getAssetToBeTransferredClone().getOwners().keySet()){
				this.getOwners().put(s, this.getCurrentFMV());
			}
			toPortfolio.add(this);
		}
		else{
			if(to.getType().equals("Partnership")){
				//to Entity
				//calculate the share of each partner.
				double initialFMV = 0;
				for(String name : to.getAssetToBeTransferredClone().getOwners().keySet()){
					initialFMV+=to.getAssetToBeTransferredClone().getOwners().get(name);
				}

				double diff = this.getCurrentFMV() - initialFMV;
				
				//divide it based on shares of each partner.
				//divide among partners
				ArrayList<PartnerData> partners=to.getPartnerData();
				for(PartnerData pd : partners){
					//for(String entity: to.getAssetToBeTransferredClone().getOwners().keySet()){
//						if one of the partners of TO is an owner of the asset to be transfered
						if(to.getAssetToBeTransferredClone().getOwners().keySet().contains(pd.getName())){
//							  FMV is the inside basis they have in that asset plus their share in the diff
							  double share = pd.getShare();
							  double fmv = to.getAssetToBeTransferredClone().getOwners().get(pd.getName()) + (share/100.0)*diff;
							  Cash c = new Cash(fmv);
							  c.getOwners().put(pd.getName(), fmv);
							  c.setInsideBasis(fmv);
							  toPortfolio.add(c);
						  }
						  else{
//							  if not, then they just get their share in the diff
							  double share = pd.getShare();
							  double fmv = (share/100.0)*diff;
							  Cash c = new Cash(fmv);
							  c.getOwners().put(pd.getName(), fmv);
							  c.setInsideBasis(fmv);
							  toPortfolio.add(c);
						  }
					//}
					
				}	
			}
//			if TO is a TaxPayer
			else{
				
				//from Entity not partnership
				double fvalue = fromAsset.getCurrentFMV() - this.getCurrentFMV();
				fromAsset.setCurrentFMV(fvalue);
				if(fvalue == 0){
					fromPortfolio.remove(fromAsset);
				}
				
				// to Entity not partnership
				while(toItr.hasNext()){
					toAsset = (Assets) toItr.next();
					if(toAsset.toString().equals(this.toString())){
						toFound = true;
						break;
					}
				}
				if(toFound){
					toAsset.setInsideBasis(this.getCurrentFMV());
					toAsset.getOwners().put(to.getName(),this.getCurrentFMV());
				 
				}
				else{
					 this.setInsideBasis(this.getCurrentFMV());
					 this.getOwners().put(to.getName(),this.getCurrentFMV());
					 toPortfolio.add(this);
				}

			}
		}	
	}
	
	@Override
	public String toString() {
		return "Cash";
	}
	
}
