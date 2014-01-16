package evogpj.selectors;

import evogpj.gp.Individual;
import evogpj.gp.Population;

/**
 * SelectPop Operation: Given a population, this will return some subset of that population
 * that an individual will compare its fitness to
 * Used for co-evolution
 * 
 * @author Jacob Rosen
 */
public interface SelectPop {
	
	public abstract Population select(Population pop);

}
