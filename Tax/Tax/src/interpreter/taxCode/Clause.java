package interpreter.taxCode;

import java.util.Iterator;

import evogpj.algorithm.Parameters;

import interpreter.assets.Annuity;
import interpreter.assets.Assets;
import interpreter.assets.Cash;
import interpreter.assets.Material;
import interpreter.assets.PartnershipAsset;
import interpreter.assets.Share;
import interpreter.entities.Entity;

/**
 * Example class. Usually each clause class would only contain functions in a compartamentalized
 * fashion, but I just want to fully transfer over the PartnershipAsset determinations
 * @author Jacob
 *
 */
public class Clause {
	
	private TaxCode taxCode;
	private boolean verbose = Parameters.Defaults.VERBOSE;
	
	public Clause(TaxCode tc) {
		this.taxCode = tc;
	}
	
	
	public PartnershipAsset canGive(Entity from, PartnershipAsset asset) {
		PartnershipAsset retAsset = null;
		Assets fromAsset = null;
		Iterator<Assets> fromItr = from.getPortfolio().iterator();
		
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
				if(((PartnershipAsset) fromAsset).getName().equals(((PartnershipAsset) asset).getName()) && ((PartnershipAsset) fromAsset).getShare() >= ((PartnershipAsset) asset).getShare()){
//					if the case, then set everything up to be transfered and break the loop
					from.setAssetToBeTransferred(fromAsset);
					from.setAssetToBeTransferredClone(new PartnershipAsset((PartnershipAsset) fromAsset));
					if (this.verbose)
						System.out.println("PORTFOLIO CONTAINS :" + from.getAssetToBeTransferredClone().getCurrentFMV());
					retAsset = (PartnershipAsset)fromAsset;
//					TELL OSAMA!!!
					break;
				}
			}
		}
		return retAsset;
	}
	
	
	
	
	public boolean canReceive(Entity to, PartnershipAsset asset) {
		if (asset.getName().equals(to.getName())) {
			if (this.verbose) {
				System.out.println("TRANSFERRING CHILD SHARE TO CHILD ENTITY PROHIBITED ");
			}
			return false;
		}
		
		//can't transfer passet to a node which has a passet in node to be transferred
//		cannot transfer a partnership asset to someone who is in a partnership with that asset
		for(Entity e: to.getPartners()){
			if(e.getName().equals(asset.getName())){
				if (this.verbose) {
					System.out.println("TRANSFERRING PASSET TO AN ENTITY WHICH HAS INDIRECTLY LINKED AN WILL CREATE AN INFINITE LOOP ");
				}
				return false;
			}
//			for each Partner of TO, check if they are already partners with the PartnshipAsset
			else if (e.getType() == "Partnership") {
				for (Entity ee : e.getPartners()) {
					if (ee.getName().equals(asset.getName())) {
						if (this.verbose) {
							System.out.println("TRANSFERRING PASSET TO AN ENTITY WHICH HAS INDIRECTLY LINKED AN WILL CREATE AN INFINITE LOOP ");
						}
						return false;
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
				if(e.getName().equals(asset.getName())){
					if (this.verbose) {
						System.out.println("TRANSFERRING PASSET TO AN ENTITY WHICH HAS INDIRECTLY LINKED AN WILL CREATE AN INFINITE LOOP ");
					}
					return false;
				}
//					for each Partner of TO, check if they are already partners with the PartnshipAsset
				else if (e.getType() == "Partnership" && taxCode.getChildSalePrevention() > 1) {
					for (Entity ee : e.getPartnershipIn()) {
						if (ee.getName().equals(asset.getName())) {
							if (this.verbose) {
								System.out.println("TRANSFERRING PASSET TO AN ENTITY WHICH HAS INDIRECTLY LINKED AN WILL CREATE AN INFINITE LOOP ");
							}
							return false;
						}
					}
				}
			}
		}
		return true;
	}
	
	
	
	public boolean canTransferAssets(PartnershipAsset asset1, Material asset2) {
		return false;	
	}
	public boolean canTransferAssets(Material asset1, PartnershipAsset asset2) {
		return false;	
	}
	
	public boolean canTransferAssets(PartnershipAsset asset1, PartnershipAsset asset2) {
		return false;	
	}
	
	public boolean canTransferAssets(PartnershipAsset asset1, Share asset2) {
		return false;	
	}
	public boolean canTransferAssets(Share asset1, PartnershipAsset asset2) {
		return false;	
	}
	
	public boolean canTransferAssets(PartnershipAsset asset1, Annuity asset2) {
		if (asset1.getCurrentFMV() > asset2.getCurrentFMV())
			return false;
		
		return true;	
	}
	public boolean canTransferAssets(Annuity asset1, PartnershipAsset asset2) {
		if (asset2.getCurrentFMV() > asset1.getCurrentFMV())
			return false;
		
		return true;	
	}
	
	public boolean canTransferAssets(PartnershipAsset asset1, Cash asset2) {
		if (asset1.getCurrentFMV() > asset2.getCurrentFMV())
			return false;
		
		return false;	
	}
	public boolean canTransferAssets(Cash asset1, PartnershipAsset asset2) {
		if (asset2.getCurrentFMV() > asset1.getCurrentFMV())
			return false;
		
		return false;	
	}
	
	
	
	
	
	
	
	

}
