package interpreter.assets;


import interpreter.entities.Entity;

import java.util.ArrayList;
import java.util.HashMap;

import evogpj.algorithm.Parameters;

/*
 * Abstract class that defines properties of Assets
 * 
 * @author Osama Badar
 */
public abstract class Assets {
	protected double taxValue;
	protected String PAssetOwner;
	protected double insideBasis;
	protected double initialFMV;
	protected double currentFMV;
	protected String name;
	protected HashMap<String,Double> insideBasisMap = new HashMap<String,Double>();
	protected HashMap<String,Double> owners = new HashMap<String,Double>();
	protected boolean verbose = Parameters.Defaults.VERBOSE;

	public abstract void calculateTax(Entity from);
	//public abstract double getAssetValue();
	//public abstract void setAssetValue(double value);

	public abstract String toString();
	public abstract void transfer(Entity from, Entity to,Assets asset);
	public abstract String getName();
	
	public Assets(){
	}

	public double getInitialFMV(){
		return this.initialFMV;
	}
	
	public double getCurrentFMV(){
		return this.currentFMV;
	}
	
	public void setInitialFMV(double initialFMV){
		this.initialFMV = initialFMV;
	}
	
	public void setCurrentFMV(double currentFMV){
		this.currentFMV = currentFMV;
	}
	
	public double getTaxValue(){
		return taxValue;
	}
	public void setTaxValue(int taxValue){
		this.taxValue =  taxValue;
	}
	public double getInsideBasis(){
		return this.insideBasis;
	}
	
	public void setInsideBasis(double insideBasis){
		this.insideBasis = insideBasis;
	}
	
	public HashMap<String,Double> getOwners(){
		return owners;
	}
	
	public HashMap<String,Double> getInsideBasisMap(){
		return insideBasisMap;
	}
	public String getOwnerName(){
		return PAssetOwner;
	}
	
	public void setOwnerName(String name){
		this.PAssetOwner = name;
	}
	//add values in the map
	public double getTotalInsideBasis(){
		double basis=0;
		for(String val:this.getInsideBasisMap().keySet()){
			basis+=this.getInsideBasisMap().get(val);
		}
		return basis;
	}
	//add all values in the map
	public double getTotalInitialFMV(){
		double fmv=0;
		for(String val:this.getOwners().keySet()){
			fmv+=this.getOwners().get(val);
		}
		return fmv;
	}
}
