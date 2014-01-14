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
 * Multiplexer.java
 *
 * @author jmmcd
 * March 3 2011
 */

package fitness;

import java.util.List;

public class Multiplexer extends BooleanFitness {

	public Multiplexer(int _N) {
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
		return multiplexer(t);
	}

	public boolean multiplexer(List<Boolean> x) {
		// Unfortunately we can't put this setup code in the
		// constructor where it should be because Java won't allow
		// code before the super() call. Spit!
		int m = 0;
		switch (N) {
		case 3:
			m = 1;
			break;
		case 6:
			m = 2;
			break;
		case 11:
			m = 3;
			break;
		default:
			System.out.println("Unexpected Multiplexer size: " + N);
			System.exit(30);
		}

		int whichInput = 0;
		for (int i = 0; i < m; i++) {
			if (x.get(i)) {
				whichInput += (int) Math.pow(2, i);
			}
		}
		return x.get(m + whichInput);
	}

	public static void main(String[] args) {
		// booleanTest("Multiplexer", Integer.parseInt(args[0]));
	}
}
