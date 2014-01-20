package evogpj.operator;

import java.io.IOException;
import java.util.ArrayList;

import evogpj.Parser.Parser;

import evogpj.phenotype.ListPhenotype;

import evogpj.algorithm.Parameters;
import evogpj.genotype.ListGenotype;
import evogpj.gp.GPException;
import evogpj.gp.Individual;
import evogpj.gp.MersenneTwisterFast;
import evogpj.gp.Population;

public class ListSinglePointCrossover extends RandomOperator implements Crossover{
	private boolean verbose = Parameters.Defaults.VERBOSE;
	public ListSinglePointCrossover(MersenneTwisterFast rand2) {
		super(rand2);
	}

	@Override
	public Population crossOver(Individual ind1, Individual ind2)
			throws GPException {
		if (this.verbose) {
			System.out.println("INSIDE XOVER\n");
			System.out.println(ind1.toString());
		}
		ArrayList<Integer> l1 = ind1.getGenotype().getGenotype();
		ArrayList<Integer> l2 = ind2.getGenotype().getGenotype();
		if (this.verbose) {
			System.out.println("individual1 fitness: " + ind1.getFitness());
			System.out.println("individual2 fitness: " + ind2.getFitness());
		}
		//System.out.println("individual1 : " + ind1.getGenotype().getGenotype().toString());


		int size = l1.size();
		int randomIndex = rand.nextInt(size);
		
		ArrayList<Integer> temp = (ArrayList<Integer>) (l1).clone();
		
		for(int j=randomIndex;j<size;j++){
			l1.set(j, l2.get(j));		
		}
		for(int i=randomIndex;i<size;i++){
			l2.set(i, temp.get(i));		
		}
		
		Individual in1 = new Individual (new ListGenotype(l1));
		Individual in2 = new Individual(new ListGenotype(l2));
		
		ArrayList<String> actions1 = new ArrayList<String>();
		ArrayList<String> actions2 = new ArrayList<String>();

		/*Parser p = new Parser();
		try {
			actions1 = p.getAction(in1.getGenotype().getGenotype());
			actions2 = p.getAction(in2.getGenotype().getGenotype());

		} catch (IOException e) {
			e.printStackTrace();
		}
		
		in1.setPhenotype(new ListPhenotype(actions1));
		in2.setPhenotype(new ListPhenotype(actions2));*/
		new Individual (new ListGenotype(l2));
		Population children = new Population();
		children.add(in1);
		children.add(in2);

		return children;
	}

}
