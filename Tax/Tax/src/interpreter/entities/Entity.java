package interpreter.entities;


import interpreter.assets.Assets;
import interpreter.assets.Cash;
import interpreter.misc.PartnerData;

import java.util.ArrayList;
import java.util.Iterator;
/*
 * Interface for Entities.
 * 
 * @author Osama Badar
 */
public interface Entity {

	
	
	
	public String getName();
	public ArrayList<Assets> getPortfolio();
	public ArrayList<Entity> getPartners();
	public ArrayList<Entity> getPartnershipIn();
	public ArrayList<PartnerData> getPartnerData();

	
	
	
	public double getTotalTax();
	public void setTotalTax(double tax);
	public String getType();
	public double getStartTax();
	public void setAssetToBeTransferred(Assets fromAsset);
	public Assets getAssetToBeTransferred();
	public void setAssetToBeTransferredClone(Assets asset);
	public Assets getAssetToBeTransferredClone();

	public double getTotalCash();
	public ArrayList<Cash> getCash();

	
	
}
