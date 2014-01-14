/**
 * 
 */
package operator;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import Parser.Parser;

import phenotype.ListPhenotype;
import phenotype.Phenotype;

import genotype.ListGenotype;
import genotype.Tree;
import gp.Individual;
import gp.MersenneTwisterFast;
import gp.Population;

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
