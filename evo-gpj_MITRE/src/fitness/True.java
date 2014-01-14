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
 * True.java
 *
 * @author jmmcd
 * March 3 2011
 */

package fitness;

public class True extends BooleanFitness {

	public True(int _N) {
		super(_N);
	}

	/**
	 * @param fitnessCase
	 *            int representing values of all inputs bitwise
	 * @return target value (correct value) at the fitness case
	 */
	@Override
	public boolean target(int fitnessCase) {
		setInputs(fitnessCase);
		return trueMax();
	}

	// Given inputs x and size N: return 1 always
	public boolean trueMax() {
		return true;
	}

	public static void main(String[] args) {
		// booleanTest("True", Integer.parseInt(args[0]));
	}
}
