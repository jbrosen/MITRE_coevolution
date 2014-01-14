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

import static java.lang.Math.pow;
import genotype.Tree;
import genotype.TreeNode;
import gp.Individual;
import gp.MersenneTwisterFast;

import java.util.ArrayList;

import phenotype.IntListPhenotype;

/**
 * Abstract class representing Semantic problems, including Order and Majority.
 * Every such problem has a size: typical size is 16, which means the terminals
 * are [-16, -15, ... -1, 1, 2, ... 16]. SemanticFitness extends
 * FitnessFunction, so it implements an eval() method.
 * 
 */

public abstract class SemanticFitness extends FitnessFunction {

	int N; // problem size
	String scaling; // scaling can be uniform, linear, or exponential.

	/**
	 * Evaluate an individual. This method does the appropriate transformations,
	 * which are generic across both problems. It calls into subclasses to get
	 * specific parts.
	 * 
	 * @param ind
	 *            Individual to be evaluated.
	 */
	@Override
	public void eval(Individual ind) {
		if (ind.getFitness() != null) {
			return;
		}
		Tree genotype = (Tree) ind.getGenotype();

		// Get all nodes inorder
		ArrayList<TreeNode> inorder = genotype.getRoot()
				.depthFirstTraversalInOrder();

		// System.out.println("inorder: " + inorder);

		// Get just the integers from the inorder nodes: discard Join
		ArrayList<Integer> ints = new ArrayList<Integer>();
		for (TreeNode tn : inorder) {
			if (!tn.label.equals("j")) {
				ints.add(Integer.parseInt(tn.label));
			}
		}

		// System.out.println("inorder ints: " + ints);

		// Calculate and save phenotype
		IntListPhenotype phenotype = getSemanticPhenotype(ints);
		ind.setPhenotype(phenotype);

		// System.out.println("phenotype: " + phenotype);

		// Calculate and save fitness (maximising)
		int fitness = evalPhenotype(phenotype);
		// System.out.println("fitness: " + fitness);

		ind.setFitness((double) fitness);
	}

	/**
	 * @param ai
	 *            Input integers
	 * @return problem-specific expressed phenotype
	 */
	abstract IntListPhenotype getSemanticPhenotype(ArrayList<Integer> ai);

	/**
	 * @param p
	 *            phenotype
	 * @return fitness
	 */
	int evalPhenotype(IntListPhenotype p) {
		int fitness = 0;
		for (int i = 0; i < p.size(); i++) {
			int x = p.getDataValue(i);

			// Get a fitness contribution for every positive integer
			// in the phenotype.
			if (x > 0) {
				if (scaling.equals("uniform")) {
					fitness += 1;
				} else if (scaling.equals("linear")) {
					fitness += (N - x);
				} else if (scaling.equals("exponential")) {
					fitness += pow(2.0, N - x);
				} else {
					System.out.println("Unexpected value for scaling: "
							+ scaling);
					System.exit(1);
				}
			}
		}
		return fitness;
	}

	/**
	 * @return a list of Strings representing the functions, ie internal nodes,
	 *         for this problem.
	 */
	public static ArrayList<String> getFunctionSet() {
		ArrayList<String> functionSet = new ArrayList<String>();
		functionSet.add("j");
		return functionSet;
	}

	/**
	 * @param n
	 *            problem size.
	 * @return a list of Strings representing the terminals, ie external nodes,
	 *         for this problem. Size-dependent.
	 */
	public static ArrayList<String> getTerminalSet(int n) {
		ArrayList<String> terminalSet = new ArrayList<String>();
		for (int i = 1; i <= n; i++) {
			terminalSet.add(Integer.toString(i));
			terminalSet.add(Integer.toString(-i));
		}
		return terminalSet;
	}

	/**
	 * Generic method for testing the semantic fitness classes. For the given
	 * problem, it will eval various hard-coded trees.
	 * 
	 * @param problem
	 *            an instance of SemanticOrder, SemanticMajority...
	 * @param n
	 *            problem size
	 */
	public static void testSemanticFitness(SemanticFitness problem, int n) {
		ArrayList<String> terminalSet = getTerminalSet(n);
		ArrayList<String> functionSet = getFunctionSet();

		String[] ss = { "(j -1 1)", "(j 1 2)", "(j 1 (j (j 2 -2) 2))",
				"(j (j -1 1) (j 1 1))" };
		for (String s : ss) {
			Tree t = new Tree(new MersenneTwisterFast(), s, functionSet,
					terminalSet);
			Individual ind = new Individual(t);
			System.out.println(ind);
			problem.eval(ind);
			System.out.println(ind.getFitness());
		}
	}
}
