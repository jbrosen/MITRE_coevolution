package genotype;

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
	
	@Override
	public String toString(){
		return genes.toString();
	}
}
