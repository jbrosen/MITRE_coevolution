package interpreter.transfers;

import interpreter.assets.Assets;
import interpreter.assets.Share;
import interpreter.entities.Entity;
import interpreter.entities.Partnership;
import interpreter.taxCode.TaxCode;

public class FromToAsset {
	private TaxCode taxCode;
	
	public FromToAsset(TaxCode tc) {
		this.taxCode = tc;
	}
	
	public boolean canBeTransferred(Entity from, Entity to, Assets asset) {
		if (asset.toString() == "Share") {
			if (from.toString() == "Partnership") {
				return canBeTransferred((Partnership)from, to, (Share)asset);
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
	
	
	
	
}
