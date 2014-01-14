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

import gp.MersenneTwisterFast;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import math.Function;

/**
 * Tree genotype implementation. This class represents the genotype of an
 * individual as a tree. {@link #holder} is a pointer to the root of the tree,
 * where the root and all other nodes in the tree are instances of
 * {@link TreeNode}. This level of indirection allows the entire tree to change
 * without creating a new @ Tree} instance.
 * 
 * @author Owen Derby
 */
public class Tree extends Genotype {

	// holder is a node whose only child is the root
	private TreeNode holder;
	private MersenneTwisterFast rng;

	private List<String> terminals;
	private List<String> functions;

	public Tree(MersenneTwisterFast r) {
		this.rng = r;
		holder = new TreeNode(null, "holder");
		terminals = new ArrayList<String>();
		terminals.add("X1");
		functions = new ArrayList<String>();
		functions.add("plus");
		functions.add("times");
		functions.add("minus");
		functions.add("mydivide");
		functions.add("mylog");
		functions.add("exp");
		functions.add("sin");
		functions.add("cos");
	}

	public Tree(MersenneTwisterFast r, List<String> funcset,
			List<String> termset) {
		this(r);
		if (funcset == null || termset == null) {
			System.out.println("null func or term set!");
		} else {
			terminals = termset;
			functions = funcset;
		}
	}

	public static Tree growTree(int maxDepth, MersenneTwisterFast r,
			List<String> funcset, List<String> termset) {
		Tree ret = new Tree(r, funcset, termset);
		TreeNode child = new TreeNode(ret.holder, "null");
		ret.grow(child, maxDepth);
		ret.holder.addChild(child);
		return ret;
	}

	public static Tree fullTree(int maxDepth, MersenneTwisterFast rand,
			List<String> funcset, List<String> termset) {
		Tree ret = new Tree(rand, funcset, termset);
		TreeNode child = new TreeNode(ret.holder, "null");
		ret.fill(child, maxDepth);
		ret.holder.addChild(child);
		return ret;
	}

	// Constructor from a string.
	public Tree(MersenneTwisterFast r, String input, List<String> funcset,
			List<String> termset) {
		this(r, funcset, termset);
		// Make sure the string is tokenizable
		// FIXME allow other delimiters?
		input = input.replace("(", " ( ");
		input = input.replace("[", " [ ");
		input = input.replace(")", " ) ");
		input = input.replace("]", " ] ");

		StringTokenizer st = new StringTokenizer(input);
		parseString(holder, st);
	}

	@Override
	public Genotype copy() {
		// hehe, the old serialise-deserialise trick
		return new Tree(this.rng, toString(), functions, terminals);
	}

	public TreeNode getRoot() {
		return holder.children.get(0);
	}

	public void grow(TreeNode treeNode, int depth) {
		// 2 terminals, 8 nonterminals
		double PT = 2.0 / 10.0;
		// See node as a placeholder: we overwrite its label,
		// children, but we don't change its parent or id.
		treeNode.children.clear();
		if (depth <= 0 || rng.nextDouble() < PT) {
			// make it a terminal
			treeNode.label = chooseTerminal();
		} else {
			// make it a function
			treeNode.label = chooseFunction();
			for (int i = 0; i < arity(treeNode.label); i++) {
				TreeNode newNode = new TreeNode(treeNode, "");
				treeNode.addChild(newNode);
				grow(newNode, depth - 1);
			}
		}
		treeNode.resetAbove();
	}

	private void fill(TreeNode node, int depth) {
		node.children.clear();
		if (depth <= 0) {
			// make it a terminal
			node.label = chooseTerminal();
		} else {
			// make it a function
			node.label = chooseFunction();
			for (int i = 0; i < arity(node.label); i++) {
				TreeNode newNode = new TreeNode(node, "");
				node.addChild(newNode);
				fill(newNode, depth - 1);
			}
		}
		node.resetAbove();
	}

	public String chooseTerminal() {
		return terminals.get(rng.nextInt(terminals.size()));
	}

	public String chooseFunction() {
		return functions.get(rng.nextInt(functions.size()));
	}

	public static int arity(String label) {
		// Symbolic regression functions:
		if (label.equals("+") || label.equals("plus")) {
			return 2;
		} else if (label.equals("*") || label.equals("times")) {
			return 2;
		} else if (label.equals("-") || label.equals("minus")) {
			return 2;
		} else if (label.equals("/") || label.equals("mydivide")) {
			return 2;
		} else if (label.equals("log") || label.equals("mylog")) {
			return 1;
		} else if (label.equals("exp")) {
			return 1;
		} else if (label.equals("sin")) {
			return 1;
		} else if (label.equals("cos")) {
			return 1;
		} else if (label.equals("sqrt")) {
			return 1;
		} else if (label.equals("square")) {
			return 1;
		} else if (label.equals("cube")) {
			return 1;
		} else if (label.equals("quart")) {
			return 1;

			// Boolean problems functions:
		} else if (label.equals("if")) {
			return 3;
		} else if (label.equals("and")) {
			return 2;
		} else if (label.equals("or")) {
			return 2;
		} else if (label.equals("nand")) {
			return 2;
		} else if (label.equals("nor")) {
			return 2;
		} else if (label.equals("not")) {
			return 1;

			// Common terminals and default case
		} else {
			return 0;
		}
	}

	private static void parseString(TreeNode parent, StringTokenizer st) {

		while (st.hasMoreTokens()) {
			String currTok = st.nextToken().trim();

			if (currTok.equals("")) {
				// Tokenizer gave us an empty token, do nothing.

			} else if (currTok.equals("(") || currTok.equals("[")) {
				// The next token is the parent of a new subtree
				currTok = st.nextToken().trim();
				TreeNode newNode = new TreeNode(parent, currTok);
				parent.addChild(newNode);
				parseString(newNode, st);

			} else if (currTok.equals(")") || currTok.equals("]")) {
				// Finish this subtree
				return;

			} else {
				// An ordinary child node: add it to parent and continue.
				TreeNode newNode = new TreeNode(parent, currTok);
				parent.addChild(newNode);
			}
		}
		return;
	}

	@Override
	public String toString() {
		return getRoot().toStringAsTree();
	}

	public boolean evalBoolean(List<Boolean> t) {
		return getRoot().evalBoolean(t);
	}

	public int getSize() {
		return getRoot().getSubtreeSize();
	}

	public int getDepth() {
		return getRoot().getSubtreeDepth();
	}

	/**
	 * @see Function
	 * @return
	 */
	public Function generate() {
		return getRoot().generate();
	}

	@Override
	public ArrayList<Integer> getGenotype() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setGenotype(ArrayList<Integer> genes) {
		// TODO Auto-generated method stub
		
	}

}
