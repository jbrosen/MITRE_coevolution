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
 * Class representing Semantic Majority problems. This problem has a size:
 * typical size is 16, which means the terminals are [-16, -15, ... -1, 1, 2,
 * ... 16]. Fitness is 1 for every time when positive i occurs more often than
 * -i. Or fitness can be scaled linearly or exponentially.
 * 
 */

public class SemanticMajority extends SemanticFitness {

	/**
	 * @param _N
	 *            problem size
	 * @param _scaling
	 *            how to scale the solution components ("uniform", "linear", or
	 *            "exponential")
	 */
	public SemanticMajority(int _N, String _scaling) {
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
		for (int i = 1; i <= N; i++) {
			// How many occurences of positive and negative terminal i?
			int pos = 0;
			int neg = 0;
			for (Integer j : ai) {
				if (j == i) {
					pos += 1;
				} else if (j == -i) {
					neg += 1;
				}
			}
			if (pos > 0 && pos >= neg) {
				p.setDataValue(i);
			}
		}
		return p;
	}

	public static void main(String[] args) {
		int n = 4;
		SemanticMajority majority = new SemanticMajority(n, "uniform");
		testSemanticFitness(majority, n);
	}

}