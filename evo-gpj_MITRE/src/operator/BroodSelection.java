/**
 * Copyright (c) 2011-2012 Evolutionary Design and Optimization Group
 * 
 * Licensed under the MIT License.
 * 
 * See the "LICENSE" file for a copy of the license.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS
 * BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN
 * ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.  
 *
 */
package operator;

import fitness.FitnessFunction;
import gp.GPException;
import gp.Individual;
import gp.MersenneTwisterFast;
import gp.Population;

// BroodSelection is really a composite
// crossover/mutation/evaluation/selection. You pass in two
// individuals, cross them over multiple times (presumably at
// different crossover points), possibly mutate, and select the best
// among the children. But since the basic idea is to get two children
// from two parents, it's best to think of it as a crossover.
public class BroodSelection extends RandomOperator implements Crossover {

	private int nXovers;
	private double mutationProb;
	private FitnessFunction fitness;
	private Crossover crossover;
	private Mutate mutation;
	private Select selection;

	public BroodSelection(int nXovers, double mutationProb,
			FitnessFunction fitness, Crossover crossover, Mutate mutation,
			MersenneTwisterFast rand) {
		super(rand);
		this.nXovers = nXovers;
		this.mutationProb = mutationProb;
		this.fitness = fitness;
		this.crossover = crossover;
		this.mutation = mutation;
		this.selection = new TournamentSelection(2 * nXovers, this.rand);
	}

	@Override
	public Population crossOver(Individual ind1, Individual ind2)
			throws GPException {
		Population children = new Population();
		for (int i = 0; i < nXovers; i++) {
			children.addAll(crossover.crossOver(ind1, ind2));
		}
		for (int i = 0; i < children.size(); i++) {
			Individual ind = children.get(i);
			if (rand.nextDouble() < mutationProb) {
				children.set(i, mutation.mutate(ind));
			}
		}
		for (Individual ind : children) {
			fitness.eval(ind);
		}

		// Make a new population, add the best, then add the second best.
		// FIXME should check they're not identical..?
		Population retval = new Population();
		Individual best = selection.select(children);
		retval.add(best);
		children.remove(best);
		best = selection.select(children);
		retval.add(best);
		return retval;
	}
}
