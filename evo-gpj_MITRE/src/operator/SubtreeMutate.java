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
import genotype.TreeNode;
import gp.GPException;
import gp.Individual;
import gp.MersenneTwisterFast;

import java.util.ArrayList;

public class SubtreeMutate extends RandomOperator implements Mutate {

	private int maxDepth;

	public SubtreeMutate(MersenneTwisterFast rand, int maxD) {
		super(rand);
		maxDepth = maxD;
	}

	@Override
	public Individual mutate(Individual i) throws GPException {
		if (!(i.getGenotype() instanceof Tree)) {
			throw new GPException(
					"attempting UniformMutate of genotype not of type Tree");
		}
		Tree copy = (Tree) i.getGenotype().copy();
		ArrayList<TreeNode> treeNodes = copy.getRoot().depthFirstTraversal();
		int nnodes = treeNodes.size();
		int whichNode;
		whichNode = rand.nextInt(nnodes);
		TreeNode n = treeNodes.get(whichNode);
		// System.out.println("Node picked is: " + whichNode + "; " +
		// n.toString());
		int curDepth = n.getDepth();
		copy.grow(n, maxDepth - curDepth);
		// System.out.println("New subtree there is: " + n.toStringAsTree());

		return new Individual(copy);
	}

}
