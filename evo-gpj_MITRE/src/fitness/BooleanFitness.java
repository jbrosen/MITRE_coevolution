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
package fitness;

import java.util.ArrayList;
import java.util.List;

import phenotype.BooleanPhenotype;
import genotype.Tree;
import gp.Individual;

/**
 * Abstract class representing Boolean problems. Every such problem has a size
 * (ie number of inputs) and a target phenotype (ie the value the target
 * function achieves on all the possible fitness cases, ordered in the natural
 * way. BooleanFitness extends FitnessFunction, so it implements an eval()
 * method.
 * 
 */

public abstract class BooleanFitness extends FitnessFunction {

	protected BooleanPhenotype target;

	int N;
	int nCases;

	/**
	 * @param _N
	 *            problem size
	 */
	public BooleanFitness(int _N) {
		N = _N;

		nCases = (int) Math.pow(2, N);
		System.out.println("N = " + N + " nCases = " + nCases);

		target = new BooleanPhenotype();

		// set targets to target values
		for (int i = 0; i < nCases; i++) {
			target.setDataValue(target(i));
		}
	}

	@Override
	public void eval(Individual ind) {
		if (ind.getFitness() != null) {
			return;
		}
		BooleanPhenotype phenotype = new BooleanPhenotype();
		Tree genotype = (Tree) ind.getGenotype();
		for (int i = 0; i < nCases; i++) {
			List<Boolean> t = setInputs(i);
			phenotype.setDataValue(genotype.evalBoolean(t));
		}
		ind.setPhenotype(phenotype);

		// Maximising fitness.
		ind.setFitness((double) (nCases - hamming(target, phenotype)));

	}

	/**
	 * @param fitnessCase
	 *            int representing values of all N inputs, bitwise
	 */
	public List<Boolean> setInputs(int fitnessCase) {
		// Each bit in fitnessCase will represent the value of an input.
		// for each bit-position in y, starting from the least-significant,
		// set x[j]
		ArrayList<Boolean> x = new ArrayList<Boolean>();

		int y = fitnessCase;
		for (int j = 0; j < N; j++) {
			if (y % 2 > 0) {
				x.add(true);
			} else {
				x.add(false);
			}
			y = (y >> 1);
		}
		return x;
	}

	/**
	 * @param fitnessCase
	 *            int representing values of all inputs bitwise
	 * @return target value (correct value) at the fitness case
	 * 
	 *         Subclasses just need to override this method.
	 */
	public boolean target(int fitnessCase) {
		setInputs(fitnessCase);
		// return a dummy value
		return false;
	}

	/**
	 * @param a
	 *            to be compared.
	 * @param b
	 *            to be compared.
	 * @return Hamming distance between a and b.
	 */
	public int hamming(BooleanPhenotype a, BooleanPhenotype b) {
		int sum = 0;
		for (int i = 0; i < a.size(); i++) {
			if (a.getDataValue(i) != b.getDataValue(i)) {
				sum += 1;
			}
		}
		return sum;
	}

	/**
	 * Static factory for BooleanProblem sub-class instances.
	 * 
	 * @param name
	 *            Which type of Boolean problem to construct.
	 * @param size
	 *            How many inputs the problem should have.
	 * @return a new instance of a type which subclasses BooleanFitness.
	 */
	public static BooleanFitness constructProblem(String name, int size) {
		if (name.equals("True")) {
			return new True(size);
		} else if (name.equals("Multiplexer")) {
			return new Multiplexer(size);
		} else if (name.equals("Majority")) {
			return new Majority(size);
		} else if (name.equals("EvenParity")) {
			return new EvenParity(size);
		}

		System.out.println("Unexpected Boolean problem name " + name);
		System.exit(1);
		return new True(size);
	}

	// public static void booleanTest(String name, int n) {
	// Boolean p = new OneMax(n);
	// if (name.equals("OneMax")) {
	// p = new OneMax(n);
	// } else if (name.equals("MajorityN")) {
	// p = new MajorityN(n);
	// } else if (name.equals("Multiplexer")) {
	// p = new Multiplexer(n);
	// } else if (name.equals("EvenNParity")) {
	// p = new EvenNParity(n);
	// } else {
	// System.exit(1);
	// }

	// for (int i = 0; i < (int) Math.pow(2, n); i++) {
	// p.setInputs(i);
	// for (int j = 0; j < n; j++) {
	// System.out.print(p.x[j]);
	// }
	// System.out.println(" : " + p.targets[i]);
	// }
	// }

}
