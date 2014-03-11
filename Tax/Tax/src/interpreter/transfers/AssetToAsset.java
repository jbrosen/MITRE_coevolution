package interpreter.transfers;

import java.util.ArrayList;

import interpreter.assets.Annuity;
import interpreter.assets.Assets;
import interpreter.assets.Cash;
import interpreter.assets.Material;
import interpreter.assets.PartnershipAsset;
import interpreter.assets.Share;
import interpreter.entities.Entity;
import interpreter.misc.Graph;
import interpreter.taxCode.TaxCode;

public class AssetToAsset {
	
	private Graph graph;
	private TaxCode taxCode;
	private double auditScore;
	
	public AssetToAsset(Graph graph, TaxCode tc) {
		this.graph = graph;
		this.taxCode = tc;
		this.auditScore = 0;
	}

	
	private PartnershipAsset getPartnershipAsset(PartnershipAsset asset) {
		ArrayList<Entity> nodes = graph.getNodes();
//		((PartnershipAsset) fromAsset).getCurrentFMV()<=otherAsset.getCurrentFMV() 
//		&&  ((PartnershipAsset) fromAsset).getName().equals(((PartnershipAsset) asset).getName()) 
//		&& ((PartnershipAsset) fromAsset).getShare() >= ((PartnershipAsset) asset).getShare())
		
		for (Entity e : nodes) {
			for (Assets a : e.getPortfolio()) {
				if (a.toString() == "PartnershipAsset") {
					PartnershipAsset pship = (PartnershipAsset)a;
					if (asset.getName().equals(pship.getName()) && pship.getShare()>=asset.getShare()) {
						return pship;
					}
				}
			}
		}
		
		return null;
	}
	
	
	/**
	 * Determines which asset class the assets are and casts the instances to call
	 * the proper overloaded function
	 * @param asset1
	 * @param asset2
	 * @return
	 */
	public boolean canTransferAssets(Assets asset1, Assets asset2) {
		
		if (asset1.toString() == "PartnershipAsset" ) {
			if (asset2.toString() == "PartnershipAsset" ) {
				return canTransferAssets((PartnershipAsset)asset1,(PartnershipAsset)asset2);
			}
			else if (asset2.toString() == "Annuity") {
				return canTransferAssets((PartnershipAsset)asset1,(Annuity)asset2);
			}
			else if (asset2.toString() == "Material") {
				return canTransferAssets((PartnershipAsset)asset1,(Material)asset2);
			}
			else if (asset2.toString() == "Cash") {
				return canTransferAssets((PartnershipAsset)asset1,(Cash)asset2);
			}
			else if (asset2.toString() == "Share") {
				return canTransferAssets((PartnershipAsset)asset1,(Share)asset2);
			}
		}
		else if (asset1.toString() == "Annuity") {
			if (asset2.toString() == "PartnershipAsset" ) {
				return canTransferAssets((Annuity)asset1,(PartnershipAsset)asset2);
			}
			else if (asset2.toString() == "Annuity") {
				return canTransferAssets((Annuity)asset1,(Annuity)asset2);
			}
			else if (asset2.toString() == "Material") {
				return canTransferAssets((Annuity)asset1,(Material)asset2);
			}
			else if (asset2.toString() == "Cash") {
				return canTransferAssets((Annuity)asset1,(Cash)asset2);
			}
			else if (asset2.toString() == "Share") {
				return canTransferAssets((Annuity)asset1,(Share)asset2);
			}
		}
		else if (asset1.toString() == "Material") {
			if (asset2.toString() == "PartnershipAsset" ) {
				return canTransferAssets((Material)asset1,(PartnershipAsset)asset2);
			}
			else if (asset2.toString() == "Annuity") {
				return canTransferAssets((Material)asset1,(Annuity)asset2);
			}
			else if (asset2.toString() == "Material") {
				return canTransferAssets((Material)asset1,(Material)asset2);
			}
			else if (asset2.toString() == "Cash") {
				return canTransferAssets((Material)asset1,(Cash)asset2);
			}
			else if (asset2.toString() == "Share") {
				return canTransferAssets((Material)asset1,(Share)asset2);
			}
		}
		else if (asset1.toString() == "Cash") {
			if (asset2.toString() == "PartnershipAsset" ) {
				return canTransferAssets((Cash)asset1,(PartnershipAsset)asset2);
			}
			else if (asset2.toString() == "Annuity") {
				return canTransferAssets((Cash)asset1,(Annuity)asset2);
			}
			else if (asset2.toString() == "Material") {
				return canTransferAssets((Cash)asset1,(Material)asset2);
			}
			else if (asset2.toString() == "Cash") {
				return canTransferAssets((Cash)asset1,(Cash)asset2);
			}
			else if (asset2.toString() == "Share") {
				return canTransferAssets((Cash)asset1,(Share)asset2);
			}
		}
		else if (asset1.toString() == "Share") {
			if (asset2.toString() == "PartnershipAsset" ) {
				return canTransferAssets((Share)asset1,(PartnershipAsset)asset2);
			}
			else if (asset2.toString() == "Annuity") {
				return canTransferAssets((Share)asset1,(Annuity)asset2);
			}
			else if (asset2.toString() == "Material") {
				return canTransferAssets((Share)asset1,(Material)asset2);
			}
			else if (asset2.toString() == "Cash") {
				return canTransferAssets((Share)asset1,(Cash)asset2);
			}
			else if (asset2.toString() == "Share") {
				return canTransferAssets((Share)asset1,(Share)asset2);
			}
		}
		return false;
	}
	
	
	
	/*
	 * Same asset exchange
	 */
	public boolean canTransferAssets(PartnershipAsset asset1, PartnershipAsset asset2) {
		return false;	
	}
	
	public boolean canTransferAssets(Annuity asset1, Annuity asset2) {
		return false;	
	}
	
	public boolean canTransferAssets(Material asset1, Material asset2) {
		return false;	
	}
	
	public boolean canTransferAssets(Cash asset1, Cash asset2) {
		return false;	
	}
	
	public boolean canTransferAssets(Share asset1, Share asset2) {
		return false;	
	}
	
	
	/*
	 * PartnershipAsset <=> _________
	 */

	public boolean canTransferAssets(PartnershipAsset asset1, Material asset2) {
		return false;	
	}
	
	public boolean canTransferAssets(PartnershipAsset asset1, Share asset2) {
		return false;	
	}
	
	public boolean canTransferAssets(PartnershipAsset asset1, Annuity asset2) {
		if (getPartnershipAsset(asset1).getCurrentFMV() > asset2.getCurrentFMV())
			return false;
		
		return true;	
	}
	
	public boolean canTransferAssets(PartnershipAsset asset1, Cash asset2) {
		if (getPartnershipAsset(asset1).getCurrentFMV(asset1.getShare()) != asset2.getCurrentFMV()) {
			return false;
		}
		
		return false;
	}
	
	
	/*
	 * Material <=> _______
	 */
	
	public boolean canTransferAssets(Material asset1, PartnershipAsset asset2) {
		return false;	
	}
	
	public boolean canTransferAssets(Material asset1, Annuity asset2) {
		if (asset1.getCurrentFMV() > asset2.getCurrentFMV())
			return false;
		this.auditScore += taxCode.getMaterialForAnnuityAudit();
		return true;
	}
	
	public boolean canTransferAssets(Material asset1, Cash asset2) {
		if (asset1.getCurrentFMV() != asset2.getCurrentFMV())
			return false;
		return true;	
	}
	
	public boolean canTransferAssets(Material asset1, Share asset2) {
		return true;	
	}
	
	
	/*
	 * Share <=> ________
	 */
	
	public boolean canTransferAssets(Share asset1, PartnershipAsset asset2) {
		return false;	
	}
	
	public boolean canTransferAssets(Share asset1, Annuity asset2) {
		return false;	
	}
	
	public boolean canTransferAssets(Share asset1, Material asset2) {
		return true;	
	}
	
	public boolean canTransferAssets(Share asset1, Cash asset2) {
		return true;	
	}
	
	
	/*
	 * Annuity <=>
	 */
	
	public boolean canTransferAssets(Annuity asset1, PartnershipAsset asset2) {
		if (getPartnershipAsset(asset2).getCurrentFMV() > asset1.getCurrentFMV())
			return false;
		return true;	
	}
	
	public boolean canTransferAssets(Annuity asset1, Cash asset2) {
		return false;	
	}
	
	public boolean canTransferAssets(Annuity asset1, Share asset2) {
		return false;
	}
	
	public boolean canTransferAssets(Annuity asset1, Material asset2) {
		if (asset2.getCurrentFMV() > asset1.getCurrentFMV())
			return false;
		this.auditScore += taxCode.getMaterialForAnnuityAudit();
		return true;	
	}
	
	
	/*
	 * Cash <=> __________
	 */
	
	public boolean canTransferAssets(Cash asset1, PartnershipAsset asset2) {
		if (getPartnershipAsset(asset2).getCurrentFMV(asset2.getShare()) != asset1.getCurrentFMV())
			return false;
		
		return true;	
	}
	
	public boolean canTransferAssets(Cash asset1, Annuity asset2) {
		return false;	
	}
	
	public boolean canTransferAssets(Cash asset1, Material asset2) {
		if (asset1.getCurrentFMV() != asset2.getCurrentFMV())
			return false;
		return true;	
	}
	
	public boolean canTransferAssets(Cash asset1, Share asset2) {
		return true;	
	}
	
	
	
	public double getAuditScore() {
		return this.auditScore;
	}
	
	public void setAuditScore(double auditScore) {
		this.auditScore = auditScore;
	}
	

}
