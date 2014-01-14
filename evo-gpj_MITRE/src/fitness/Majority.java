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
/**
 * Majority.java
 *
 * @author jmmcd
 * March 3 2011
 */

package fitness;

import java.util.List;

public class Majority extends BooleanFitness {

	public Majority(int _N) {
		super(_N);
	}

	/**
	 * @param fitnessCase
	 *            int representing values of all inputs bitwise
	 * @return target value (correct value) at the fitness case
	 */
	@Override
	public boolean target(int fitnessCase) {
		List<Boolean> t = setInputs(fitnessCase);
		return majority(t);
	}

	// Given inputs x and size N: if majority of inputs are zeroes,
	// return 0, else return 1.
	public boolean majority(List<Boolean> x) {
		int sum = 0;
		for (int i = 0; i < N; i++) {
			if (x.get(i)) {
				sum += 1;
			}
		}
		if (sum > N / 2) {
			return true;
		} else {
			return false;
		}
	}

	public static void main(String[] args) {
		// booleanTest("Majority", Integer.parseInt(args[0]));
	}
}
