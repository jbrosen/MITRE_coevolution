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

import gp.Individual;
import gp.MersenneTwisterFast;
import gp.Population;

public class TournamentSelection extends RandomOperator implements Select {

	private int tsize;

	public TournamentSelection(int tsize, MersenneTwisterFast rand) {
		super(rand);
		this.tsize = tsize;
	}

	@Override
	public Individual select(Population pop) {
		System.out.println("INSIDE TOURNAMENT SELECTION\n");

		int n = pop.size();
		// want newPop of size n, but have to account for elitism
		Individual best, challenger;
		best = pop.get(rand.nextInt(n));
		for (int j = 0; j < tsize - 1; j++) {
			//System.out.println("INSIDE for LOOP: " + j);

			challenger = pop.get(rand.nextInt(n));
			if (challenger.getFitness() > best.getFitness())
				best = challenger;
		}
		System.out.println("fitness of best individual selected: " + best.getFitness());

		return best;
	}

}
