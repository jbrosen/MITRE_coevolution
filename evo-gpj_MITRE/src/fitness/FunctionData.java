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

import genotype.Tree;
import gp.MersenneTwisterFast;

import java.util.ArrayList;
import java.util.List;

import math.Function;

public class FunctionData extends ScaledData {
	/**
	 * Given a function, subsample a series of training points and target
	 * values.
	 * 
	 * @param r
	 *            RNG to use
	 * @param function
	 *            The problem to evaluate against
	 * @param funcset
	 *            The functionset used in the individuals
	 * @param termset
	 *            the terminal set used in the individuals
	 */
	public FunctionData(MersenneTwisterFast r, String function,
			List<String> funcset, List<String> termset) {
		for (int i = -10; i <= 10; i++) {
			List<Double> t = new ArrayList<Double>();
			t.add(i / 10.0);
			this.fitnessCases.add(t);
		}
		Tree temp = new Tree(r, function, funcset, termset);
		final Function func = temp.generate();
		for (List<Double> t : fitnessCases) {
			Double val = func.eval(t);
			this.addTargetValue(val);
		}
		this.scaleTarget();
	}
}
