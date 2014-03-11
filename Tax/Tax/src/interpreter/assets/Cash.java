package interpreter.assets;

import interpreter.entities.Entity;
import interpreter.misc.PartnerData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class Cash extends Assets{

	public Cash(double value){
		this.initialFMV = value;
		this.currentFMV = value;
		this.name = "Cash: " + this.currentFMV;
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
	public String getName() {
		return this.name;
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
		
		/*
		 * "this" is the asset that was indicated in the original transaction
		 * and fromAsset is the one that was eventually selected from the
		 * entity's portfolio.
		 * This block does some housekeeping that was originally done in the Transfer class,
		 * but makes more sense to happen here. Specifically, it sets the new FMV of whatever Cash
		 * object or objects were used to create the transfered Cash object in FROM's portfolio
		 * . This can include setting the new inside basis map, or removing it all together.
		 */
		if (from.getType() == "TaxPayer") {
			//from Entity not partnership
			double fvalue = fromAsset.getCurrentFMV() - this.getCurrentFMV();
			fromAsset.setCurrentFMV(fvalue);
			if(fvalue == 0){
				fromPortfolio.remove(fromAsset);
			}
		}
		else if (from.getType() == "Partnership") {
			HashMap<String,Double> newCashOwners = fromAsset.getOwners();
//			look in every element of the Partnership's portfolio
			for (int i = 0 ; i < fromPortfolio.size() ; ++i) {
//				if the asset is Cash
				if (fromPortfolio.get(i).toString() == "Cash") {
					Cash thisCash = (Cash)fromPortfolio.get(i);
//					get the owner(s) of the cash object and see if it is one of the cash objects
//					contributed to the exchanged cash object
					for (String name : thisCash.getOwners().keySet()) {
						if (newCashOwners.containsKey(name)) {
//							get the amount that the owner in question constributed to the original cash object
							double fval = thisCash.getOwners().get(name) - newCashOwners.get(name);
							from.getPortfolio().get(i).setCurrentFMV(fval);
							from.getPortfolio().get(i).setInsideBasis(fval);
							from.getPortfolio().get(i).getOwners().put(name,fval);
							if (fval == 0) {
								from.getPortfolio().remove(i);
							}
							break;
						}
					}
				}
			}
		}
	}
	
	@Override
	public String toString() {
		return "Cash";
	}
	
}
