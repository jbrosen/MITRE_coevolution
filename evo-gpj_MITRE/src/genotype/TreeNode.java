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
package genotype;

import java.util.ArrayList;
import java.util.List;

import math.Const;
import math.Cos;
import math.Cube;
import math.Divide;
import math.Exp;
import math.Log;
import math.Minus;
import math.Multiply;
import math.Function;
import math.Plus;
import math.Quart;
import math.Sin;
import math.Sqrt;
import math.Square;
import math.Var;

/**
 * A tree is really just a set of nodes, organized into a tree-shape by
 * parent-child relations. We create trees out of instances of @ TreeNode} . A
 * node tracks both its parent and child(ren), as well as its depth in the
 * current tree and the depth and size of the subtree rooted at it. These values
 * are cached for optimization reasons. Finally, ever node has a label,
 * corresponding to the function or terminal it represents in the individual
 * solution.
 * 
 * @author Owen Derby
 */
public class TreeNode {
	public TreeNode parent;
	public String label;
	public ArrayList<TreeNode> children;
	private int subtreeSize;
	private int subtreeDepth;
	private int depth;

	public TreeNode(TreeNode _parent, String _label) {
		parent = _parent;
		label = _label;
		children = new ArrayList<TreeNode>();
		subtreeSize = -1;
		subtreeDepth = -1;
		depth = -1;
	}

	public void addChild(TreeNode child) {
		children.add(child);
	}

	@Override
	public String toString() {
		return label;
	}

	public String toStringAsTree() {
		if (children.size() > 0) {
			String retval = "(" + this;
			for (TreeNode child : children) {
				retval += " " + child.toStringAsTree();
			}
			retval += ")";
			return retval;
		} else {
			return this.toString();
		}
	}

	public String toStringAsFunction() {
		if (children.size() > 0) {
			String retval;
			if (children.size() == 1)
				retval = this + "(" + children.get(0).toStringAsFunction()
						+ ")";
			else
				retval = "(" + children.get(0).toStringAsFunction() + " "
						+ this + " " + children.get(1).toStringAsFunction()
						+ ")";
			return retval;
		} else {
			return this.toString();
		}
	}

	/**
	 * Do a depth-first preorder traversal of the tree starting at a given node.
	 * 
	 * @return a list of Nodes in depth-first preorder.
	 */
	public ArrayList<TreeNode> depthFirstTraversal() {
		ArrayList<TreeNode> retval = new ArrayList<TreeNode>();
		retval.add(this);
		for (TreeNode child : children) {
			retval.addAll(child.depthFirstTraversal());
		}
		return retval;
	}

	/**
	 * Do a depth-first inorder traversal of the tree starting at a given node.
	 * 
	 * @return a list of Nodes in depth-first inorder.
	 */
	public ArrayList<TreeNode> depthFirstTraversalInOrder() {
		ArrayList<TreeNode> retval = new ArrayList<TreeNode>();
		int nChildren = children.size();
		int i = 0;
		for (; i < nChildren / 2; i++) {
			retval.addAll(children.get(i).depthFirstTraversalInOrder());
		}
		retval.add(this);
		for (; i < nChildren; i++) {
			retval.addAll(children.get(i).depthFirstTraversalInOrder());
		}
		return retval;
	}

	public int getDepth() {
		if (depth == -1) {
			// Travel back to root to calculate depth
			int _depth = 0;
			TreeNode n = this;
			while (!n.parent.label.equals("holder")) {
				n = n.parent;
				_depth++;
			}
			depth = _depth;
		}

		return depth;
	}

	public int getSubtreeSize() {
		if (subtreeSize == -1) {
			subtreeSize = 1;
			for (TreeNode child : children) {
				subtreeSize += child.getSubtreeSize();
			}
		}
		return subtreeSize;
	}

	public int getSubtreeDepth() {
		if (subtreeDepth == -1) {
			subtreeDepth = 0;
			for (TreeNode child : children) {
				if (child.getSubtreeDepth() > subtreeDepth)
					subtreeDepth = child.getSubtreeDepth();
			}
		}
		return subtreeDepth + 1;
	}

	public ArrayList<TreeNode> getAncestors() {
		TreeNode n = this;
		ArrayList<TreeNode> retval = new ArrayList<TreeNode>();
		retval.add(n);
		while (!n.parent.label.equals("holder")) {
			n = n.parent;
			retval.add(n);
		}
		return retval;
	}

	/**
	 * Prepare to be evaluated. Generate an {@link Function} representing the
	 * complete function described by the subtree rooted at this node.
	 * 
	 * @return Function representation of subtree
	 */
	public Function generate() {
		if (label.startsWith("X")) {
			String numPart = label.substring(1);
			int idx = Integer.parseInt(numPart) - 1; // zero-index
			return new Var(idx);
		} else if (label.equals("+") || label.equals("plus")) {
			return new Plus(children.get(0).generate(), children.get(1)
					.generate());
		} else if (label.equals("*") || label.equals("times")) {
			return new Multiply(children.get(0).generate(), children.get(1)
					.generate());
		} else if (label.equals("-") || label.equals("minus")) {
			return new Minus(children.get(0).generate(), children.get(1)
					.generate());
		} else if (label.equals("/") || label.equals("mydivide")) {
			return new Divide(children.get(0).generate(), children.get(1)
					.generate());
		} else if (label.equals("sin")) {
			return new Sin(children.get(0).generate());
		} else if (label.equals("cos")) {
			return new Cos(children.get(0).generate());
		} else if (label.equals("log") || label.equals("mylog")) {
			return new Log(children.get(0).generate());
		} else if (label.equals("exp")) {
			return new Exp(children.get(0).generate());
		} else if (label.equals("sqrt")) {
			return new Sqrt(children.get(0).generate());
		} else if (label.equals("square")) {
			return new Square(children.get(0).generate());
		} else if (label.equals("cube")) {
			return new Cube(children.get(0).generate());
		} else if (label.equals("quart")) {
			return new Quart(children.get(0).generate());
		} else if (label.equals("x")) {
			return new Var(0);
		} else if (label.equals("y")) {
			return new Var(1);
		} else {
			return new Const(Double.parseDouble(label));
		}
	}

	/**
	 * Evaluate a tree of type boolean.
	 * 
	 * @param t
	 * @return
	 */
	public boolean evalBoolean(List<Boolean> t) {
		if (label.equals("and")) {
			return (children.get(0).evalBoolean(t))
					&& (children.get(1).evalBoolean(t));
		} else if (label.equals("or")) {
			return children.get(0).evalBoolean(t)
					|| children.get(1).evalBoolean(t);
		} else if (label.equals("nand")) {
			return !(children.get(0).evalBoolean(t) && children.get(1)
					.evalBoolean(t));
		} else if (label.equals("nor")) {
			return !(children.get(0).evalBoolean(t) || children.get(1)
					.evalBoolean(t));
		} else if (label.equals("if")) {
			if (children.get(0).evalBoolean(t)) {
				return (children.get(1).evalBoolean(t));
			} else {
				return (children.get(2).evalBoolean(t));
			}
		} else if (label.equals("not")) {
			return !(children.get(0).evalBoolean(t));
		} else if (label.equals("x")) {
			return t.get(0);
		} else if (label.equals("y")) {
			return t.get(1);
		} else if (label.startsWith("X") || label.startsWith("x")) {
			String numPart = label.substring(1);
			int idx = Integer.parseInt(numPart) - 1; // zero-index
			return t.get(idx);
		}
		// FIXME how to signal this error?
		return false;
	}

	public void reset() {
		resetAbove();
		resetBelow();
	}

	/**
	 * Something changed about the size/depth of the subtree at this node, so
	 * reset the cached values of this node and its parents.
	 */
	public void resetAbove() {
		subtreeDepth = -1;
		subtreeSize = -1;
		if (!parent.label.equals("holder")) {
			parent.resetAbove();
		}
	}

	/**
	 * Something changed with this node and it's parents, so reset the cached
	 * values relating to this node and below.
	 */
	public void resetBelow() {
		depth = -1;
		for (TreeNode child : children) {
			child.resetBelow();
		}
	}

}