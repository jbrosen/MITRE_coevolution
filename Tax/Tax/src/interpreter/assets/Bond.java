package interpreter.assets;

import interpreter.entities.Entity;

public class Bond extends Assets{

	private int value;
	public Bond(int value) {
		this.value = value;	
		this.name = "bond";
	}

	@Override
	public void calculateTax(Entity from) {
		//return 0.25*this.getCurrentFMV();
	}
	
	@Override
	public String getName() {
		return this.name;
	}
	
	@Override
	public void transfer(Entity from, Entity to,Assets asset) {
		// TODO Auto-generated method stub
		
	}

	
	@Override
	public String toString() {
		return "Bond";
	}

/*	@Override
	public double getAssetValue() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setAssetValue(double value) {
		// TODO Auto-generated method stub
		
	}
	*/
}
