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
 * EvenParity.java
 *
 * @author jmmcd
 * March 3 2011
 */

package fitness;

import java.util.List;

public class EvenParity extends BooleanFitness {

	public EvenParity(int _N) {
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
		return evenParity(t);
	}

	// Given inputs x and size N, return x[0] ^ x[1] ^ ... ^ x[N -1]
	// which is the same as even-N parity.
	public boolean evenParity(List<Boolean> x) {
		boolean retval = x.get(0);
		for (int i = 1; i < N; i++) {
			retval ^= x.get(i);
		}
		return retval;
	}

	public static void main(String[] args) {
		// booleanTest("EvenParity", Integer.parseInt(args[0]));
	}
}
