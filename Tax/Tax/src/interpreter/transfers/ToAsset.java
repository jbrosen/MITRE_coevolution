package interpreter.transfers;

import interpreter.assets.Assets;
import interpreter.assets.PartnershipAsset;
import interpreter.entities.Entity;
import interpreter.taxCode.TaxCode;
import evogpj.algorithm.Parameters;

public class ToAsset {
	
	private TaxCode taxCode;
	private boolean verbose = Parameters.Defaults.VERBOSE;
	private double auditScore;
	
	public ToAsset(TaxCode tc) {
		this.taxCode = tc;
		this.auditScore = 0.0;
	}

	public boolean canReceive(Entity to, Assets asset) {
		if (asset.toString() == "PartnershipAsset")
			return canReceive(to,(PartnershipAsset)asset);
		
		return true;
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
		if (taxCode.getChildSalePrevention() > 5) {
			for(Entity e: to.getPartnershipIn()){
				if(e.getName().equals(asset.getName())){
					if (this.verbose) {
						System.out.println("TRANSFERRING PASSET TO AN ENTITY WHICH HAS INDIRECTLY LINKED AN WILL CREATE AN INFINITE LOOP ");
					}
					return false;
				}
//					for each Partner of TO, check if they are already partners with the PartnshipAsset
				else if (e.getType() == "Partnership" && taxCode.getChildSalePrevention() > 5) {
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
	public double getAuditScore() {
		return this.auditScore;
	}
	
	public void setAuditScore(double auditScore) {
		this.auditScore = auditScore;
	}
}
