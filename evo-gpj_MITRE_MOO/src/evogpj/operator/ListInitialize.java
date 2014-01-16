/**
 * 
 */
package evogpj.operator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import evogpj.Parser.Parser;

import evogpj.phenotype.ListPhenotype;
import evogpj.phenotype.Phenotype;

import evogpj.genotype.ListGenotype;
import evogpj.gp.Individual;
import evogpj.gp.MersenneTwisterFast;
import evogpj.gp.Population;

/**
 * @author Badar
 *
 */
public class ListInitialize extends RandomOperator implements Initialize {


	public ListInitialize(MersenneTwisterFast rand2) {
		super(rand2);
		// TODO Auto-generated constructor stub
	}


	@Override
	public Population initialize(int popSize, List<String> funcset,
			List<String> termset) {
		Population ret = new Population();
		return ret;
	}

//	added by jbrosen, 1/15/2014, doesn't do anything, just for parent class
	@Override
	public Population initialize(int popSize) {
		return null;
	}
	
	@Override
	public Population listInitialize(int popSize, ArrayList<ArrayList> set) {
		Population ret = new Population();
		for(int i=0;i<set.size();i++){
			
			Individual ind = new Individual(new ListGenotype(set.get(i)));
			ArrayList<String> actions = new ArrayList<String>();
			/*Parser p = new Parser();
			try {
				actions = p.getAction(ind.getGenotype().getGenotype());
			} catch (IOException e) {
				e.printStackTrace();
			}
			ind.setPhenotype(new ListPhenotype(actions));*/
			ret.add(ind);
		}
		return ret;
	}
	
}
