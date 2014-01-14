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
import gp.Individual;
import gp.MersenneTwisterFast;
import gp.Population;

import java.util.ArrayList;
import java.util.List;

/**
 * Create new population of Trees using Koza's ramped half and half
 * initialization.
 * 
 * @author Owen Derby
 */
public class TreeInitialize extends RandomOperator implements Initialize {
	private int maxDepth;

	/**
	 * Constructor, configure parameters.
	 * 
	 * @param rand
	 * @param maxD the maximum depth of the trees created (note, not same as max
	 *            size!)
	 */
	public TreeInitialize(MersenneTwisterFast rand, int maxD) {
		super(rand);
		this.maxDepth = maxD;
	}

	@Override
	public Population initialize(int popSize, List<String> funcset,
			List<String> termset) {
		Population ret = new Population();
		for (int i = 0; i < popSize / 2; i++) {
			ret.add(new Individual(Tree.fullTree(
					maxDepth * 2 * i / popSize + 1, rand, funcset, termset)));
			ret.add(new Individual(Tree.growTree(
					maxDepth * 2 * i / popSize + 1, rand, funcset, termset)));
		}
		return ret;
	}



	@Override
	public Population listInitialize(int popSize, ArrayList<ArrayList> set) {
		// TODO Auto-generated method stub
		return null;
	}

}
