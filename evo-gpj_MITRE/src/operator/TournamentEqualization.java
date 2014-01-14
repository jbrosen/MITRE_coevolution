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

import genotype.Tree;
import gp.GPException;
import gp.Individual;
import gp.MersenneTwisterFast;
import gp.Population;

/**
 * Test operator that implements multiple operations. This operator performs
 * standard tournament selection, but then when asked to accept or reject the
 * newly created individuals, compares them to the candidates used in the
 * tournament.
 * 
 * @author oderby
 * 
 */
public class TournamentEqualization extends RandomOperator implements
		Equalizer, Select {

	private int tsize; // size of tournament
	private Population pool;// candidates considered in last tournament
	private double avg_f; // average fitness of tournament candidates
	private boolean reset = true;

	public TournamentEqualization(int tsize, MersenneTwisterFast rand) {
		super(rand);
		this.tsize = tsize;
	}

	@Override
	public Individual select(Population pop) {
		// reset the pool if this is a new round of selection
		if (reset) {
			reset = false;
			pool = new Population();
			avg_f = 0.0;
		}
		// select tsize individuals to add to the pool
		int n = pop.size();
		avg_f *= pool.size();
		int j = pool.size();
		for (int k = 0; k < tsize; k++) {
			pool.add(pop.get(rand.nextInt(n)));
		}
		// find the best individual from the last tsize individuals in the pool
		Individual best, challenger;
		best = pool.get(j);
		avg_f += best.getFitness();
		for (; j < pool.size(); j++) {
			challenger = pool.get(j);
			avg_f += challenger.getFitness();
			if (challenger.getFitness() > best.getFitness())
				best = challenger;
		}
		avg_f /= pool.size();
		return best;
	}

	/**
	 * This is just a sample accept method, but many ways of deciding to accept
	 * the individual are possible
	 */
	@Override
	public boolean accept(Individual i) throws GPException {
		// reject an offspring if it is worse than all of the individuals in the
		// pool it was selected from
		reset = true;
		// if (i.getFitness() > avg_f)
		// return true;
		for (Individual j : pool) {
			// if (i.getFitness() > j.getFitness()) {
			if (((Tree) i.getGenotype()).getSize() > ((Tree) j.getGenotype())
					.getSize() && i.getFitness() < j.getFitness()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void update(Population init) throws GPException {
		// do nothing here. We only look at the pool of individuals used in the
		// tournament when accepting.
	}

}
