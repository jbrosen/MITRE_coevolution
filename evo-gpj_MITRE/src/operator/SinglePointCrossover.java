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
import gp.Population;

import java.util.ArrayList;

public class SinglePointCrossover extends RandomOperator implements Crossover {

	private int maxDepth;
	private int numTries;

	public SinglePointCrossover(MersenneTwisterFast rand, int maxD, int numT) {
		super(rand);
		maxDepth = maxD;
		numTries = numT;
	}

	@Override
	public Population crossOver(Individual ind1, Individual ind2)
			throws GPException {
		if (!(ind1.getGenotype() instanceof Tree && ind2.getGenotype() instanceof Tree)) {
			throw new GPException(
					"attempting SinglePointCrossover of two genotypes not of type Tree");
		}
		Tree c1, c2;
		int tries = 0;
		do {
			c2 = (Tree) ind2.getGenotype().copy();
			c1 = (Tree) ind1.getGenotype().copy();
			// pick a xover pt in this Tree by uniform sampling of
			// depthFirstTraversal, and find that node's index among its
			// siblings
			ArrayList<TreeNode> nodes = c1.getRoot().depthFirstTraversal();
			int nodeCount = nodes.size();
			int xoverPt1idx = rand.nextInt(nodeCount);
			TreeNode xoverPt1 = nodes.get(xoverPt1idx);
			int xoverPt1idxInChildren = xoverPt1.parent.children
					.indexOf(xoverPt1);

			// same for other Tree
			nodes = c2.getRoot().depthFirstTraversal();
			nodeCount = nodes.size();
			int xoverPt2idx = rand.nextInt(nodeCount);
			TreeNode xoverPt2 = nodes.get(xoverPt2idx);
			int xoverPt2idxInChildren = xoverPt2.parent.children
					.indexOf(xoverPt2);

			// other.xoverpt = this.xoverpt, and fix up parent link
			xoverPt2.parent.children.set(xoverPt2idxInChildren, xoverPt1);
			TreeNode tmpParent = xoverPt1.parent;
			xoverPt1.parent = xoverPt2.parent;

			// this.xoverpt = other.xoverpt, and fix up parent link
			tmpParent.children.set(xoverPt1idxInChildren, xoverPt2);
			xoverPt2.parent = tmpParent;

			// reset cached values
			xoverPt1.reset();
			xoverPt2.reset();
			tries++;
			// } while ((c1.getSize()> maxSize || c2.getSize() > maxSize) &&
			// tries < numTries);
			// if (tries >= numTries || c1.getSize()> maxSize || c2.getSize() >
			// maxSize) {
		} while ((c1.getDepth() > maxDepth || c2.getDepth() > maxDepth)
				&& tries < numTries);
		Population twoPop = new Population();
		if (tries >= numTries || c1.getDepth() > maxDepth
				|| c2.getDepth() > maxDepth) {
			// System.out.println("failed to xover properly"+ c1.getDepth() +
			// " " +c2.getDepth());
			twoPop.add(ind1.copy());
			twoPop.add(ind2.copy());
		} else {
			twoPop.add(new Individual(c1));
			twoPop.add(new Individual(c2));
		}
		return twoPop;
	}

}
