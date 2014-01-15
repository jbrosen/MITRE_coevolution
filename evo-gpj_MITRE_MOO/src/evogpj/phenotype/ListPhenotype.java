package evogpj.phenotype;

import java.util.ArrayList;

public class ListPhenotype extends Phenotype{

	private String phenotype;
	public ListPhenotype(String phenotype){
		this.phenotype = phenotype;
	}
	
	@Override
	public Phenotype copy() {
		return this.copy();
	}

	@Override
	public String getPhenotype(){
		return phenotype;
	}
}
