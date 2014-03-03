package interpreter.misc;

import interpreter.assets.Assets;
import interpreter.assets.Cash;
import interpreter.assets.Material;
import interpreter.assets.PartnershipAsset;
import interpreter.entities.Entity;
import interpreter.taxCode.TaxCode;

import evogpj.algorithm.Parameters;

import java.util.ArrayList;
import java.util.Iterator;
/*
 * Traverses the nodes graph on each action 
 * and prints out the state of the graph 
 * after each time step.
 * 
 * @author Osama Badar
 */
public class Transfer {
	
	private ArrayList<Entity> nodes; 
	private TaxCode taxCode;
	private boolean isTaxable = true;
	private boolean verbose = Parameters.Defaults.VERBOSE;
	
	
	public Transfer(ArrayList<Entity> nodes, TaxCode taxCode){
		this.nodes = (ArrayList<Entity>) nodes;
		this.taxCode = taxCode;
	}
	public boolean doTransfer(Transaction transaction){
		
		isTaxable = true;
//		check if legal
		if(isLegal(transaction)){
			if (this.verbose) {
				System.out.println("___________TRANSACTION IS LEGAL____________");
			}
			//System.exit(0);

			transferAction(transaction.getAction1(),transaction.getAction2());
			transferAction(transaction.getAction2(),transaction.getAction1());
			return true;
		}
		else{
			if (this.verbose) {
				System.out.println("___________TRANSACTION IS ILLEGAL____________");
			}
			return false;
		}
	}
	public void transferAction(Actions action1,Actions action2){
		Entity from = getFrom(action1);
		Entity to = getTo(action1);
		if(isTaxable){
			from.getAssetToBeTransferred().calculateTax(from);
		}
		action1.getTransferableAssets().transfer(from, to, action2.getTransferableAssets());
	}
	/*
	 * @param action object that stores information about the action
	 * that needs to be performed.
	 * @return Entity from which the action needs to be performed.
	 */
	private Entity getFrom(Actions action){
		Entity from = null;
		Iterator<Entity> itr = nodes.iterator();
		while(itr.hasNext()){
			 from =  itr.next();
			 if (from.getName().equals(action.getFrom())){
				 break;
			 }
		}
		return from;
	}
	/*
	 * @param action object that stores information about the action
	 * that needs to be performed.
	 * @return Entity to which the action needs to be performed.
	 */
	private Entity getTo(Actions action){
		Entity to = null;
		Iterator<Entity> itr = nodes.iterator();
		while(itr.hasNext()){
			 to =  itr.next();
			 if (to.getName().equals(action.getTo())){
				 break;
			 }
		}
		return to;
	}
	
	/*check to see if a transaction is legal
	 * don't transfer object to itself
	 * a transaction is only legal if A1&&A2 are legal actions
	 * if from entity does not contain the object, action is illegal
	 */
	private boolean isLegal(Transaction transaction){
		if (this.verbose) {
			System.out.println("Checking to see if transaction is legal");
			System.out.println("transaction:"+ transaction.toString());
		}
		
		return (isAsset1PlusAsset2Legal(transaction) && isLegalAction(transaction.getAction1(),transaction.getAction2()) && isLegalAction(transaction.getAction2(),transaction.getAction1()));		
	}

	/*
	 * checks to see if two assets in a transaction are legal.
	 * example: if A transfers cash to B and in return gets a Material,
	 * this is deemed legal but if A transfers a PshipAsset and gets back
	 * a PshipAsset, this is not legal.
	 */
	private boolean isAsset1PlusAsset2Legal(Transaction transaction){
		
		Actions action1 = transaction.getAction1();
		Actions action2 = transaction.getAction2();
		Assets asset1 = action1.getTransferableAssets();
		Assets asset2 = action2.getTransferableAssets();
		if(asset1.toString().equals("Cash") && asset2.toString().equals("Cash")){
			return false;
		}
		else if(asset1.toString().equals("Cash") && asset2.toString().equals("Annuity")){
			return false;
		}
		else if(asset1.toString().equals("Annuity") && asset2.toString().equals("Cash")){
			return false;
		}
		else if(asset1.toString().equals("Annuity") && asset2.toString().equals("Annuity")){
			return false;
		}
		else if(asset1.toString().equals("Annuity") && asset2.toString().equals("Share")){
			return false;
		}
		else if(asset1.toString().equals("PartnershipAsset") && asset2.toString().equals("PartnershipAsset")){
			return false;
		}
		else if(asset1.toString().equals("PartnershipAsset") && asset2.toString().equals("Material")){
			return false;
		}
		else if(asset1.toString().equals("PartnershipAsset") && asset2.toString().equals("Share")){
			return false;
		}
		else if(asset1.toString().equals("Material") && asset2.toString().equals("Material")){
			return false;
		}
		else if(asset1.toString().equals("Share") && asset2.toString().equals("Share")){
			return false;
		}
		else if(asset1.toString().equals("Share") && asset2.toString().equals("PartnershipAsset")){
			return false;
		}
		else if(asset1.toString().equals("Share") && asset2.toString().equals("Annuity")){
			return false;
		}
		else if(asset1.toString().equals("Material") && asset2.toString().equals("PartnershipAsset")){
			return false;
		}
		if (!taxCode.getAnnuityForMaterial()) {
			if(asset1.toString().equals("Material") && asset2.toString().equals("Annuity")) {
				return false;
			}
			else if(asset1.toString().equals("Annuity") && asset2.toString().equals("Material")) {
				return false;
			}
		}
		return true;
	}
	
//	checks if a certain transaction, which is a combination of two actions, is legal
	private boolean isLegalAction(Actions action,Actions otherAction){
//		FROM gives ASSET and TO gives OTHERASSET
		
		Entity from = getFrom(action);
		Entity to = getTo(action);
		Assets asset = action.getTransferableAssets();
		Assets otherAsset = otherAction.getTransferableAssets();
//		can't make an exchange with yourself
		if(from.getName().equals(to.getName())){
			if (this.verbose) {
				System.out.println("same entities");
			}
			return false;
		}
//		then check if FROM even has ASSET in its portfolio
		else if(!isAssetInFromEntity(from,to,asset,otherAsset)){
			if (this.verbose) {
				System.out.println("asset not in from entity");
			}
			return false;
		}
		return true;
	}
	
	
	private boolean isAssetInFromEntity(Entity from, Entity to, Assets asset,Assets otherAsset){
//		checks if fromAsset and toAsset are in FROMS's portfolio
		boolean fromFound = false;
//		the actual assets in the portfolio
		Assets fromAsset = null;
		ArrayList<Assets> fromPortfolio = from.getPortfolio();
		Iterator<Assets> fromItr = fromPortfolio.iterator();
		if (this.verbose) {
			System.out.println("FROM ENTITY: " + from.getName());
		}
		
//		if the asset that FROM is supposed to give is a PartnershipAsset
		if(asset.toString().equals("PartnershipAsset")){
			if (this.verbose) {
				System.out.println("PORTOLIO SIZE: "+fromPortfolio.size()+"\n");
				System.out.println("ASSET COMPARED :" + ((PartnershipAsset) asset).printPAsset());
				System.out.println("FROM ENTITY :" + from.getName());
			}
//			iterate through FROM's portfolio
			while(fromItr.hasNext()){
				fromAsset = (Assets) fromItr.next();
				
//				if a matching partnership asset is found
				if((fromAsset).toString().equals(asset.toString())){
					if (this.verbose) {
						System.out.println("PASSET VALUE:" + ((PartnershipAsset) fromAsset).getCurrentFMV());
						System.out.println("OTHER ASSER VALUE:" + otherAsset.getCurrentFMV());
					}
//					three conditions for transfer
//					1) current market value of asset must be less than or equal to FMV of asset its being transfered for
//					2) the name of the asset in FROM's portfolio must match with the name of the original asset
//					     NOTE: I'm pretty sure this is a redundancy after checking if equal
//					3) the share of the found asset and asset must be the same, another redundancy probably
					if(((PartnershipAsset) fromAsset).getCurrentFMV()<=otherAsset.getCurrentFMV() &&  ((PartnershipAsset) fromAsset).getName().equals(((PartnershipAsset) asset).getName()) && ((PartnershipAsset) fromAsset).getShare() >= ((PartnershipAsset) asset).getShare()){
//						if the case, then set everything up to be transfered and break the loop
						from.setAssetToBeTransferred(fromAsset);
						from.setAssetToBeTransferredClone(new PartnershipAsset((PartnershipAsset) fromAsset));
						if (this.verbose)
							System.out.println("PORTFOLIO CONTAINS :" + from.getAssetToBeTransferredClone().getCurrentFMV());	
						fromFound = true;
//						TELL OSAMA!!!
						break;
					}
				}
			}
//			the asset can't be the same entity it is being transfered to
			if(((PartnershipAsset) asset).getName().equals(to.getName())){
				if (this.verbose) {
					System.out.println("TRANSFERRING CHILD SHARE TO CHILD ENTITY PROHIBITED ");
				}
				fromFound = false;
			}
			
			//can't transfer passet to a node which has a passet in node to be transferred
//			cannot transfer a partnership asset to someone who is in a partnership with that asset
			for(Entity e: to.getPartners()){
				if(e.getName().equals(((PartnershipAsset) asset).getName())){
					if (this.verbose) {
						System.out.println("TRANSFERRING PASSET TO AN ENTITY WHICH HAS INDIRECTLY LINKED AN WILL CREATE AN INFINITE LOOP ");
					}
					fromFound = false;
				}
//				for each Partner of TO, check if they are already partners with the PartnshipAsset
				else if (e.getType() == "Partnership") {
					for (Entity ee : e.getPartners()) {
						if (ee.getName().equals(((PartnershipAsset) asset).getName())) {
							if (this.verbose) {
								System.out.println("TRANSFERRING PASSET TO AN ENTITY WHICH HAS INDIRECTLY LINKED AN WILL CREATE AN INFINITE LOOP ");
							}
							fromFound = false;
						}
					}
				}
			}
			
			/*
			 * Potentially need to check the child partners as well for a potential linkage. Won't cause a crash
			 * but might be logically incorrect
			 */
			if (taxCode.getChildSalePrevention() > 0) {
				for(Entity e: to.getPartnershipIn()){
					if(e.getName().equals(((PartnershipAsset) asset).getName())){
						if (this.verbose) {
							System.out.println("TRANSFERRING PASSET TO AN ENTITY WHICH HAS INDIRECTLY LINKED AN WILL CREATE AN INFINITE LOOP ");
						}
						fromFound = false;
					}
	//					for each Partner of TO, check if they are already partners with the PartnshipAsset
					else if (e.getType() == "Partnership" && taxCode.getChildSalePrevention() > 1) {
						for (Entity ee : e.getPartnershipIn()) {
							if (ee.getName().equals(((PartnershipAsset) asset).getName())) {
								if (this.verbose) {
									System.out.println("TRANSFERRING PASSET TO AN ENTITY WHICH HAS INDIRECTLY LINKED AN WILL CREATE AN INFINITE LOOP ");
								}
								fromFound = false;
							}
						}
					}
				}
			}
			
			/*
			 * 
			 */
		}
//		if the asset to be transfered is an annuity
		else if (asset.toString().equals("Annuity")){
			this.isTaxable = false;
			fromFound = true;
			//no need to have cash as tax is calculated is calculated differently.
			//no tax when paid in Annuity
			
//			just make sure that the current market value is greater than or equal to otherAsset
			if(asset.getCurrentFMV() < otherAsset.getCurrentFMV()){
				 fromFound = false;
			 }
			
//			just messing around
			if (taxCode.getChildSalePrevention() > 1) {
				if (from.getName() == "Brown")
					fromFound = false;
			}

		}
//		if the asset to be transfered is a material
		else if (asset.toString().equals("Material")){
//			iterate through FROM's portfolio
			while(fromItr.hasNext()){
				fromAsset = (Assets) fromItr.next();
				if (this.verbose) 
					System.out.println("Asset seen:" + fromAsset.toString());
//				if an equal asset is located
				if((fromAsset).toString().equals(asset.toString())){
					if (this.verbose)
						System.out.println("name:"+((Material) fromAsset).getName());
//					if the two assets also have the same name REDUNDANCY
					if(((Material) fromAsset).getName().equals(((Material) asset).getName())){
//						set everything to be transfered
//						fromAsset.setCurrentFMV(200*this.taxCode.getAnnuityThreshold());	// TEST
						from.setAssetToBeTransferred(fromAsset);
						from.setAssetToBeTransferredClone(new Material((Material) fromAsset));
						fromFound = true;
						break;
					}
				}
			}
		}
//		if the asset to be transfered is cash
		else if (asset.toString().equals("Cash")){
			
//			WHY DON'T YOU HAVE TO CHECK WHETHER THE FMV OF THE SHARE IS THE SAME/LESS THAN/EQUAL TO THE CASH VALUE
			//check to see the other asset is a share.
			if(otherAsset.toString().equals("Share")){
				ArrayList<Cash> fromCash = from.getCash();
//				if there is enough money in FROM's portfolio
				
				if ( from.getTotalCash() >= asset.getCurrentFMV() ) {
					Iterator<Cash> fromCashItr = fromCash.iterator();
//					check if there is one cash object that can satisfy the the transaction.
					while ( fromCashItr.hasNext()) {
						Cash fromCashAsset = fromCashItr.next();
						if (fromCashAsset.getCurrentFMV() >= asset.getCurrentFMV()) {
							fromAsset = fromCashAsset;
							from.setAssetToBeTransferred(fromAsset);
							from.setAssetToBeTransferredClone(new Cash((Cash) fromAsset));
							fromFound = true;
							break;
						}
					}
//					if a single cash object was found
					if (fromFound) {
//						extract the necessary value from the cash object that was sufficient to cover the asset cost
						double fValue = fromAsset.getCurrentFMV() - asset.getCurrentFMV();
						fromAsset.setCurrentFMV(fValue);
						fromAsset.setInsideBasis(fValue);
						for (String s : fromAsset.getOwners().keySet()) {
							fromAsset.getOwners().put(s, fValue);
						}
//						remove the cash object if there is nothing left
						if (fValue == 0) {
							fromPortfolio.remove(fromAsset);
						}
					}
//					if there is no single cash object but enough in total
					else {
						Cash newCash = new Cash(asset.getCurrentFMV());
						double totCash = 0;
						for (int i=0 ; i<fromCash.size() ; i++) {
							totCash += fromCash.get(i).getCurrentFMV();
//							do this until the whole asset value is covered
							if (totCash > asset.getCurrentFMV() ) {
								newCash.getOwners().put(from.getName(), asset.getCurrentFMV());
								double fVal = totCash - asset.getCurrentFMV();
								from.getPortfolio().get(i).setCurrentFMV(fVal);
								from.getPortfolio().get(i).setInsideBasis(fVal);
								for (String s : from.getPortfolio().get(i).getOwners().keySet()) {
									from.getPortfolio().get(i).getOwners().put(s, fVal);
								}
							}
							else {
								from.getPortfolio().remove(i);
							}
						}
						from.setAssetToBeTransferred(newCash);
						from.setAssetToBeTransferredClone(new Cash(newCash));
					}
				}
			}
			
//			if the other asset is NOT a share
			else{
//				make sure that the assets being exchanged have the same FMV
				if(asset.getCurrentFMV()!=otherAsset.getCurrentFMV()){
					fromFound = false;
				}
//				if the FROM entity is a partnership
				else if(from.getType().equals("Partnership")){
					//check to see if we have one cash object whose value is satisfied
					while(fromItr.hasNext()){
						fromAsset = fromItr.next();
						if(fromAsset.toString().equals("Cash")){
							double fvalue = fromAsset.getCurrentFMV() - asset.getCurrentFMV();
							if(fvalue >= 0){
								from.setAssetToBeTransferred(fromAsset);
								from.setAssetToBeTransferredClone(new Cash((Cash) fromAsset));					
								fromFound = true;
								break;
								}
							}
					}
					if(fromFound){
						if (this.verbose) 
							System.out.println("YES CASH HAS BEEN FOUND");
						double fvalue = fromAsset.getCurrentFMV() - asset.getCurrentFMV();
						fromAsset.setCurrentFMV(fvalue);
						if(fvalue == 0){
							fromPortfolio.remove(fromAsset);
						}
					}
					else{
						//now check if we have multiple cash objects that can satisfy the price
					    double tempFmv = 0;
					    fromItr = fromPortfolio.iterator();
						while(fromItr.hasNext()){
							fromAsset = fromItr.next();
							if(fromAsset.toString().equals("Cash")){
								//System.out.println("NOW I AM HERE 1:"+fromAsset.getCurrentFMV());
								
								tempFmv  += fromAsset.getCurrentFMV();
								if(tempFmv - asset.getCurrentFMV() >= 0){
									//System.out.println("NOW I AM HERE 2:"+(tempFmv - asset.getCurrentFMV()));
									fromFound = true;
									break;
								}
							}
						}
//						if the otherAsset is not a share and you need to aggregate cash
//						THIS MIGHT BE A BUG
						if(fromFound){
							Cash newCash = new Cash(asset.getCurrentFMV());
							tempFmv = 0;
							for(int i=0;i<from.getPortfolio().size();i++){
								if(from.getPortfolio().get(i).toString().equals("Cash")){
									tempFmv  += from.getPortfolio().get(i).getCurrentFMV();
									if(tempFmv - asset.getCurrentFMV() > 0){
										//System.out.println("NOW I AM HERE 3:"+(tempFmv - asset.getCurrentFMV()));
//										for every owner of the cash indexed at i
										for(String name : from.getPortfolio().get(i).getOwners().keySet()){
//											WHAT IS THIS???
											newCash.getOwners().put(name,from.getPortfolio().get(i).getOwners().get(name)-(tempFmv - asset.getCurrentFMV()));
										}
										double fvalue = tempFmv - asset.getCurrentFMV();
										from.getPortfolio().get(i).setCurrentFMV(fvalue);
										from.getPortfolio().get(i).setInsideBasis(fvalue);
										for(String s : from.getPortfolio().get(i).getOwners().keySet()){
											from.getPortfolio().get(i).getOwners().put(s, fvalue);
										}
									}
									else{
//										why is this here and not there when otherAsset is a share???
										for(String name : from.getPortfolio().get(i).getOwners().keySet()){
											newCash.getOwners().put(name,from.getPortfolio().get(i).getOwners().get(name));
										}
										from.getPortfolio().remove(i);			
									}
								}
							}
							from.setAssetToBeTransferred(newCash);
							from.setAssetToBeTransferredClone(new Cash(newCash));
						}
					}
			}
//			if the from entity is not a partnership AND otherAsset is not a share
//			NOTE why can't a taxpayer pull cash from multiple Cash objects?
			else{
				while(fromItr.hasNext()){
				fromAsset = fromItr.next();
				if(fromAsset.toString().equals("Cash")){
					//can't check passet like this so do the check in passet
					double fvalue = fromAsset.getCurrentFMV() - asset.getCurrentFMV();
					if(fvalue >= 0){
						from.setAssetToBeTransferred(fromAsset);
						from.setAssetToBeTransferredClone(new Cash((Cash) fromAsset));
						//from.getAssetToBeTransferredClone().getOwners().put(from.getName(), fvalue);
						fromFound = true;
						break;
						}
					}
				}
				if(fromFound){
					double fvalue = fromAsset.getCurrentFMV() - asset.getCurrentFMV();
					fromAsset.setCurrentFMV(fvalue);
					fromAsset.setInsideBasis(fvalue);
					if(fvalue == 0){
						fromPortfolio.remove(fromAsset);
					}
				}
			}
		}
		
		
		}
//		if the asset to be transfered is a share
		else{
			isTaxable = false;
			fromFound = true;
//			can't purchase a share of a taxpayer
			if(from.getType().equals("TaxPayer")){
				fromFound = false;
			}
//			if from is a Partnership
			else{
				//can't give share to someone who already has a share.
				for(Entity e :from.getPartners()){
					if(to.getName().equals(e.getName())){
						fromFound = false;
						break;
					}
				}
				
				/*
				 * Need to check Partnership cycles of length greater than 2
				 * Check to see if an edge leads to a cycle
				 */
				//can't give a share to someone we have a passet in--infinite loop otherwise.
				
//				for each entity that the Partnership has a share in 
				for(Entity e :from.getPartnershipIn()){
//					cannot transfer a share of itself to an asset it owns
					if(to.getName().equals(e.getName())){
						fromFound = false;
						break;
					}
				}
			}
				
		}

	return fromFound;
	}
	
	public void setTaxCode(TaxCode taxCode) {
		this.taxCode = taxCode;
	}
	public TaxCode getTaxCode() {
		return this.taxCode;
	}
	
	
}
