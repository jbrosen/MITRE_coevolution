package interpreter.transfers;

import interpreter.assets.Assets;
import interpreter.assets.Share;
import interpreter.entities.Entity;
import interpreter.entities.Partnership;
import interpreter.taxCode.TaxCode;

public class FromToAsset {
	private TaxCode taxCode;
	private double auditScore;
	
	
	public FromToAsset(TaxCode tc) {
		this.taxCode = tc;
		this.auditScore = 0.0;
	}
	
	public boolean canBeTransferred(Entity from, Entity to, Assets asset) {
		if (asset.toString() == "Share") {
			if (from.toString() == "Partnership") {
				return canBeTransferred((Partnership)from, to, (Share)asset);
			}
		}
		/*
		 * If the asset isn't a Share, which isn't even relevant at this stage, check 
		 * if there is some linkage between the two entities
		 * We just need to check one direction because both directions are checked in the code
		 * kludgy for now because we know that there can only be a connection of two
		 */
		outerloop:
		for (Entity e : to.getPartnershipIn()) {
			if (e.getName().equals(from.getName())) {
				this.auditScore += taxCode.getSingleLinkAudit();
				break;
			}
			else if (e.getPartnershipIn().size() > 0) {
				for (Entity ee : e.getPartnershipIn()) {
					if (ee.getName().equals(from.getName())) {
						auditScore += taxCode.getDoubleLinkAudit();
						break outerloop;
					}
				}
			}
		}
		return true;
	}
	
	
	public boolean canBeTransferred(Partnership from, Entity to, Share asset) {
		for(Entity e :from.getPartners()){
			if(to.getName().equals(e.getName())){
				return false;
			}
		}
		for(Entity e :from.getPartnershipIn()){
//			cannot transfer a share of itself to an asset it owns
			if(to.getName().equals(e.getName())){
				return false;
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
