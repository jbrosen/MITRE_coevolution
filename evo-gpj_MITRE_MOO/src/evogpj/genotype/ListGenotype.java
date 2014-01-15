package evogpj.genotype;

import java.util.ArrayList;
import java.util.List;


public class ListGenotype extends Genotype{

	private ArrayList<Integer> genes;
	
	public ListGenotype(List<Integer> genes ){

		this.genes = (ArrayList<Integer>) genes;
	}
	
	@Override
	public Genotype copy() {
		return new ListGenotype(this.genes);
	}
	@Override
	public ArrayList<Integer> getGenotype(){
		return genes;
	}
	@Override
	public void setGenotype(ArrayList<Integer> genes){
		this.genes = genes;
	}
//	equals method added by jbrosen, 1/15/2014
	@Override
	public Boolean equals(Genotype other) {
		if (this.genes == other.getGenotype()) {
			return true;
		}
		return false;
	}
	
	@Override
	public String toString(){
		return genes.toString();
	}
}
