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
import gp.Population;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import algorithm.AlgorithmBase;

/**
 * This operator is an implementation of Silva's Dynamic Operator Equalization,
 * as described in her paper ( Silva and Dignum. Extending Operator
 * Equalisation: Fitness Based Self Adaptive Length Distribution for Bloat Free
 * GP. EuroGP 2009). The idea is we bin all individuals based on their size, and
 * use the binning to intelligently accept or reject individuals.
 * 
 * @author Owen Derby
 */
public class TreeDynamicEqualizer extends Operator implements Equalizer {

	// There is some debate over whether it makes sense to just add
	// all intermediate bins, but we believe this is what Silva did,
	// so we kept it. Set this const to false to turn off this behavior.
	private static final boolean KEEP_INTERMEDIATE_BINS = true;

	@SuppressWarnings("serial")
	protected class Bin extends ArrayList<Individual> {
		// average fitness of this bin
		public double fitness;
		// best fitness of this bin
		public double most_fit;
		// target capacity of this bin
		public int capacity;

		public Bin() {
			super();
			this.fitness = 0;
			this.most_fit = 0;
			this.capacity = 1;
		}
	}

	protected Map<Integer, Bin> bins;
	protected int bin_width;
	protected int max_bin = 1;

	public TreeDynamicEqualizer(int width, Population initPop)
			throws GPException {
		super();
		this.bin_width = width;
		this.bins = new HashMap<Integer, Bin>();
		int bin_index;
		for (Individual i : initPop) {
			if (!(i.getGenotype() instanceof Tree)) {
				throw new GPException(
						"attempting equalization with an individual not of type Tree");
			}
			bin_index = ((Tree) i.getGenotype()).getSize() / bin_width;
			if (!bins.containsKey(bin_index)) {
				bins.put(bin_index, new Bin());
				addIntermediateBins(bin_index);
			}
			bins.get(bin_index).add(i);
		}
	}

	/**
	 * If new_index is greater than the last max_bin, create all intermediate
	 * binst up until new max_bin.
	 * 
	 * @param new_index
	 */
	private void addIntermediateBins(int new_index) {
		if (KEEP_INTERMEDIATE_BINS && new_index > max_bin) {
			// if the new bin is outside of the
			// continuous range of bins, add all
			// intermediate bins
			for (int j = max_bin + 1; j < new_index; j++) {
				if (bins.containsKey(j))
					System.out.println("impossible bin?" + j + ": "
							+ this.toString());
				bins.put(j, new Bin());
			}
			max_bin = new_index;
		}
	}

	@Override
	public void update(Population init) throws GPException {
		int n = 0;
		double f = 0;
		for (Bin b : bins.values()) {
			n += b.size();
			b.fitness = 0;
			for (Individual i : b) {
				b.fitness += i.getFitness();
				if (i.getFitness() > b.most_fit)
					b.most_fit = i.getFitness();
			}
			if (b.size() > 0) {
				b.fitness = b.fitness / b.size();
				f += b.fitness;
				b.clear();
			}
		}
		// rem is used to remove dead bins (no individuals). However, should not
		// be used if we want to have bins up to max_bin.

		// List<Integer> rem = new ArrayList<Integer>();
		for (int i : bins.keySet()) {
			Bin b = bins.get(i);
			b.capacity = (int) Math.round(n * b.fitness / f);
			if (b.capacity < 1) {
				// rem.add(i);
				b.capacity = 1;
			}
		}
		// for (int i : rem) {
		// bins.remove(i);
		// }
		Bin b;
		for (Individual i : init) {
			if (!(i.getGenotype() instanceof Tree)) {
				throw new GPException(
						"attempting to accept an individual not of type Tree");
			}
			double fitness = i.getFitness();
			int bin_index = ((Tree) i.getGenotype()).getSize() / bin_width;
			if (bins.containsKey(bin_index)) {
				b = bins.get(bin_index);
				b.add(i);
				if (fitness > b.most_fit)
					b.most_fit = fitness;
			} else {
				b = new Bin();
				b.add(i);
				b.most_fit = fitness;
				bins.put(bin_index, b);
				addIntermediateBins(bin_index);
			}
		}
	}

	@Override
	public boolean accept(Individual i) throws GPException {
		if (!(i.getGenotype() instanceof Tree)) {
			throw new GPException(
					"attempting to accept an individual not of type Tree");
		}
		boolean ret = false;
		double fitness = i.getFitness();
		int bin_index = ((Tree) i.getGenotype()).getSize() / bin_width;
		Bin b;
		if (bins.containsKey(bin_index)) {
			b = bins.get(bin_index);
			if (b.size() < b.capacity || fitness > b.most_fit) {
				b.add(i);
				if (fitness > b.most_fit)
					b.most_fit = fitness;
				ret = true;
			}
		} else {
			boolean best_of_run = true;
			for (Bin bin : bins.values()) {
				if (fitness <= bin.most_fit) {
					best_of_run = false;
					break;
				}
			}
			if (best_of_run) {
				b = new Bin();
				b.add(i);
				b.most_fit = fitness;
				bins.put(bin_index, b);
				ret = true;
				addIntermediateBins(bin_index);
			}
		}
		if (AlgorithmBase.VERBOSE)
			System.out.println("accept? " + ret);
		return ret;
	}

	@Override
	public String toString() {
		String s = "bins={";
		for (int i : bins.keySet()) {
			Bin b = bins.get(i);
			s += String.format("%d:[%d,%d,%f,%f],", i * bin_width, b.size(),
					b.capacity, b.fitness, b.most_fit);
		}
		return s.substring(0, s.length()-1)+"}";
	}
}
