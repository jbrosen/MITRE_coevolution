package interpreter.transfers;

import java.util.Iterator;

import evogpj.algorithm.Parameters;

import interpreter.assets.Annuity;
import interpreter.assets.Assets;
import interpreter.assets.Cash;
import interpreter.assets.Material;
import interpreter.assets.PartnershipAsset;
import interpreter.assets.Share;
import interpreter.entities.Entity;
import interpreter.taxCode.TaxCode;

public class FromAsset {
	
	private TaxCode taxCode;
	private boolean verbose = Parameters.Defaults.VERBOSE;
	private double auditScore;
	
	public FromAsset(TaxCode tc) {
		this.taxCode = tc;
		this.auditScore = 0.0;
	}
	
	public Assets canGive(Entity from, Assets asset) {
		if (asset.toString() == "PartnershipAsset")
			return canGive(from, (PartnershipAsset)asset);
		else if (asset.toString() == "Annuity")
			return canGive(from, (Annuity)asset);
		else if (asset.toString() == "Material")
			return canGive(from, (Material)asset);
		else if (asset.toString() == "Cash")
			return canGive(from, (Cash)asset);
		else if (asset.toString() == "Share")
			return canGive(from, (Share)asset);
		return null;
	}
	
	public PartnershipAsset canGive(Entity from, PartnershipAsset asset) {
		
		PartnershipAsset retAsset = null;
		Assets fromAsset = null;
		Iterator<Assets> fromItr = from.getPortfolio().iterator();
		if (this.verbose) {
			System.out.println("PORTOLIO SIZE: "+from.getPortfolio().size()+"\n");
			System.out.println("ASSET COMPARED :" + ((PartnershipAsset) asset).printPAsset());
			System.out.println("FROM ENTITY :" + from.getName());
		}
		
		while(fromItr.hasNext()) {
			fromAsset = (Assets) fromItr.next();
			
//			if a matching partnership asset is found
			if((fromAsset).toString().equals(asset.toString())){
				if (this.verbose) {
					System.out.println("PASSET VALUE:" + ((PartnershipAsset) fromAsset).getCurrentFMV());
				}
//				REMEMBER TO THROW IN THE FMV COMPARISON IN THE ASSET-TO-ASSET CLASS
//				two conditions for transfer
//				1) the name of the asset in FROM's portfolio must match with the name of the original asset
//				     NOTE: I'm pretty sure this is a redundancy after checking if equal
//				2) the share of the found asset and asset must be the same, another redundancy probably
				if(((PartnershipAsset) fromAsset).getName().equals(((PartnershipAsset) asset).getName()) 
						&& ((PartnershipAsset) fromAsset).getShare() >= ((PartnershipAsset) asset).getShare()){
					
					/*
					 * Set up if-else clause. If the actual asset to be transferred is less than the share that exists
					 * in the portfolio, then
					 */
					
					
//					if the case, then set everything up to be transfered and break the loop
					from.setAssetToBeTransferred(fromAsset);
					from.setAssetToBeTransferredClone(new PartnershipAsset((PartnershipAsset) fromAsset,((PartnershipAsset) asset).getShare()));
					if (this.verbose)
						System.out.println("PORTFOLIO CONTAINS :" + from.getAssetToBeTransferredClone().getCurrentFMV());
					retAsset = (PartnershipAsset)fromAsset;
					break;
				}
			}
		}
		return retAsset;
	}
	
	public Annuity canGive(Entity from, Annuity asset) {
		
		return asset;
	}
	
	public Material canGive(Entity from, Material asset) {
		Material retAsset = null;
		Assets fromAsset = null;
		Iterator<Assets> fromItr = from.getPortfolio().iterator();
		
		while(fromItr.hasNext()){
			fromAsset = (Assets) fromItr.next();
			if (this.verbose) 
				System.out.println("Asset seen:" + fromAsset.toString());
//			if an equal asset is located
			if((fromAsset).toString().equals(asset.toString())){
				if (this.verbose)
					System.out.println("name:"+((Material) fromAsset).getName());
//				if the two assets also have the same name REDUNDANCY
				if(((Material) fromAsset).getName().equals(asset.getName())){
//					set everything to be transfered
					from.setAssetToBeTransferred(fromAsset);
					from.setAssetToBeTransferredClone(new Material((Material) fromAsset));
					retAsset = (Material)fromAsset;
					break;
				}
			}
		}
		
		return retAsset;
	}
	
	/*
	 * NOTE: In Osama's code there's a difference between how Cash is transferred depending on whether
	 * or not the other asset is a Share object. I'm going to hold off on making that distinction until I
	 * can talk to him.
	 */
	public Cash canGive(Entity from, Cash asset) {
		if (from.getTotalCash() < asset.getCurrentFMV())
			return null;
		
		Cash retCash = null;
		Cash fromCash = null;
		boolean fromFound = true;
		if (from.getType() == "Partnership") {
			Iterator<Cash> fromCashItr = from.getCash().iterator();
			while (fromCashItr.hasNext()) {
				fromCash = fromCashItr.next();
				if (fromCash.getCurrentFMV() >= asset.getCurrentFMV()) {
					from.setAssetToBeTransferred(fromCash);
					from.setAssetToBeTransferredClone(new Cash(fromCash));
					fromFound = true;
					break;
				}
			}
			if (fromFound) {
				double fVal = fromCash.getCurrentFMV() - asset.getCurrentFMV();
				fromCash.setCurrentFMV(fVal);
				if (fVal == 0) {
					from.getPortfolio().remove(fromCash);
				}
				retCash = fromCash;
			}
//			now aggregate the cash objects that will satisfy the asset value if you 
//			need more than one
			else {
				double tmpFmv = 0;
				Cash newCash = new Cash(asset.getCurrentFMV());
				for (int i = 0 ; i < from.getCash().size() ; ++i) {
					tmpFmv += from.getCash().get(i).getCurrentFMV();
//					if the object still does not completely cover the asset value,
//					take the whole thing.
					if (tmpFmv <= asset.getCurrentFMV()) {
						for (String name : from.getCash().get(i).getOwners().keySet()) {
							newCash.getOwners().put(name, from.getCash().get(i).getOwners().get(name));
						}
//						from.getPortfolio().remove(from.getCash().get(i));
					}
//					if only part of a Cash object needs to be taken.
					/*
					 * What happens to the owners of the Cash object if more than one of the objects have the
					 * same owner? That is, because we are just putting the values that each owners contributed to 
					 * each object rather than checking if their name already exists as a key, we may miscalculate
					 * how much each person contributed.
					 * EX: P1 has two cash objects that are being transferred for $250 in total, both contributed by A.
					 * The first is worth $200 and the second $100. In the first loop, A's basis in the newCash object would
					 * be set to $200. But the next loop, his basis would be reset to $50.
					 */
					else {
						double fVal = tmpFmv - asset.getCurrentFMV();
//						this may have been done wrong in the original code
//						for each owner of the last Cash object to be transferred
						for (String name : from.getCash().get(i).getOwners().keySet()) {
							newCash.getOwners().put(name, from.getCash().get(i).getCurrentFMV() - fVal);
						}
//						set the new inside basis and FMV of the residual Cash object
//						int cashIndexInPortfolio = from.getPortfolio().indexOf(from.getCash().get(i));
//						from.getPortfolio().get(cashIndexInPortfolio).setCurrentFMV(fVal);
//						from.getPortfolio().get(cashIndexInPortfolio).setInsideBasis(fVal);
//						
//						for (String name : from.getCash().get(i).getOwners().keySet()) {
//							from.getPortfolio().get(cashIndexInPortfolio).getOwners().put(name, fVal);
//						}
						
					}
				}
				retCash = newCash;
				from.setAssetToBeTransferred(newCash);
				from.setAssetToBeTransferredClone(new Cash(newCash));
			}
		}
		else if (from.getType() == "TaxPayer") {
			Iterator<Cash> fromCashItr = from.getCash().iterator();
			while (fromCashItr.hasNext()) {
				fromCash = fromCashItr.next();
				if (fromCash.getCurrentFMV() >= asset.getCurrentFMV()) {
					from.setAssetToBeTransferred(fromCash);
					from.setAssetToBeTransferredClone(new Cash(fromCash));
					fromFound = true;
					break;
				}
			}
			if (fromFound) {
//				double fVal = fromCash.getCurrentFMV() - asset.getCurrentFMV();
//				fromCash.setCurrentFMV(fVal);
//				if (fVal == 0) {
//					from.getPortfolio().remove(fromCash);
//				}
				retCash = fromCash;
			}
		}
		return retCash;
	}
	
	public Share canGive(Entity from, Share asset) {
		if (from.toString() == "TaxPayer")
			return null;
		
		return asset;
	}
	
	public void incAuditScore(double inc) {
		this.auditScore += inc;
	}
	
	public double getAuditScore() {
		return this.auditScore;
	}
	
	public void setAuditScore(double auditScore) {
		this.auditScore = auditScore;
	}
	
}
