package evogpj.operator;

import java.io.IOException;
import java.util.ArrayList;

import evogpj.Parser.Parser;

import evogpj.phenotype.ListPhenotype;

import evogpj.genotype.ListGenotype;
import evogpj.gp.GPException;
import evogpj.gp.Individual;
import evogpj.gp.MersenneTwisterFast;

public class ListMutate extends RandomOperator implements Mutate{

	private int POP_SIZE;
	public ListMutate(MersenneTwisterFast rand2,int POP_SIZE) {
		super(rand2);
		this.POP_SIZE = POP_SIZE;
	}

	@Override
	public Individual mutate(Individual i) throws GPException {
		int size = i.getGenotype().getGenotype().size();
		int randomValue = rand.nextInt(Integer.MAX_VALUE);
		ArrayList<Integer> genotype = i.getGenotype().getGenotype();
		int randomIndex = rand.nextInt(size);
		ArrayList<Integer> child = (ArrayList<Integer>) genotype.clone();
		child.add(randomIndex, randomValue);
		child.remove(randomIndex+1);
		Individual newI = new Individual(new ListGenotype(child));
		//set phenotype
		/*ArrayList<String> actions = new ArrayList<String>();
		Parser p = new Parser();
		try {
			actions = p.getAction(newI.getGenotype().getGenotype());
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		newI.setPhenotype(new ListPhenotype(actions));*/
		return newI;
	}

}
