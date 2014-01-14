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

import phenotype.IntListPhenotype;

/**
 * Class representing Semantic Order problems. This problem has a size: typical
 * size is 16, which means the terminals are [-16, -15, ... -1, 1, 2, ... 16].
 * Fitness is 1 for every time when positive i occurs before -i, in an inorder
 * traversal. Or fitness can be scaled linearly or exponentially.
 * 
 */

public class SemanticOrder extends SemanticFitness {

	/**
	 * @param _N
	 *            problem size
	 * @param _scaling
	 *            how to scale the solution components ("uniform", "linear", or
	 *            "exponential")
	 */
	public SemanticOrder(int _N, String _scaling) {
		N = _N;
		scaling = _scaling;
	}

	/**
	 * @param ai
	 *            Input integers
	 * @return problem-specific expressed phenotype
	 */
	@Override
	IntListPhenotype getSemanticPhenotype(ArrayList<Integer> ai) {
		IntListPhenotype p = new IntListPhenotype();
		for (Integer i : ai) {
			// Add i to phenotype if i and -i do not yet exist in
			// phenotype.
			if (!p.contains(i) && !p.contains(-i)) {
				p.setDataValue(i);
			}
		}
		return p;
	}

	public static void main(String[] args) {
		int n = 4;
		SemanticOrder order = new SemanticOrder(n, "uniform");
		testSemanticFitness(order, n);
	}
}